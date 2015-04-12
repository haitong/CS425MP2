import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;



public class Node implements Runnable{

	private TreeSet<Integer> data = new TreeSet<Integer>();

	private List<fingerEntry> finger = new ArrayList<fingerEntry>();

	private int predecessor;
	private int index;
	private Chord chord;
	public static final int TOTAL_NUM = 8;
	public static final int TOTAL_NODE = 256;

	public class fingerEntry{
		int start;
		int end;
		int node;
		public fingerEntry(){
			start = 0;
			end = 0;
			node = 0;
		}

	}

	// check if key exists in any of the replicas
	public int find(int key){
		chord.incrementCount();
//		System.out.println("Curent index is " + index);
		// if it exists in current replica
		if(data.contains(key)){
		// The node needs to return the index of current replica, which is 1 message
			chord.incrementCount();
			return index;
		}
		// Check if the key should be stored in current node
		if(index == finger.get(0).node || withinRangeEe(index,finger.get(0).node,key)){
			// key does not exists
			return -1;
		}
		// Otherwise, need to query other nodes for the key
		// query 2-6 fingers
		boolean success = false;
		int result = -1;
		for(int i=0; i < TOTAL_NUM-1; i++){
			if(finger.get(i).node != finger.get(i+1).node && withinRangeEe(finger.get(i).node, finger.get(i+1).node, key)){
				success = true;
//				System.out.println(" Node " + index + " is asking (" + i + ") " + finger.get(i).node + "  " + finger.get(i+1).node);
				result = chord.getNode(finger.get(i).node).find(key);
				break;
			}
		}

		// If none of the 2-6 fingers success, query the 7th finger
		if(!success && finger.get(TOTAL_NUM-1).node != index){
			result = chord.getNode(finger.get(TOTAL_NUM-1).node).find(key);
		}
		chord.setComplete(true);
		return result;
	}

	public Node(Chord c, int id){
		chord = c;
		index = id;
		int jump = 1;
		for(int i=0; i < TOTAL_NUM; i++){
			fingerEntry f = new fingerEntry();
			f.start = (id + jump) % TOTAL_NODE;
			jump *= 2;
			f.end = (id + jump) % TOTAL_NODE;
			finger.add(f);
		}
		predecessor = 0;
	}


	public void printKey(){
//		System.out.print("Keys in node " + index + ": ");
		System.out.print(index + " ");
		for(Integer e : data){
			System.out.print(e + " ");
		}
		System.out.println("");
//		System.out.println(". Done!");
	}

	public synchronized void addData(Set<Integer> d){
		data.addAll(d);
	}

	public synchronized void addData(int key){
		data.add(key);
	}

	public synchronized SortedSet<Integer> getData(int start, int end){
		// The start should be the inserted node's ID
		// End should be the sucessor of the inserted node
		return data.subSet(start, end);
	}

	public int findSuccessor(int id){
		int successor = findPredecessor(id);
		return chord.getNode(successor).getSuccessor();	
	}

	public int getSuccessor(){
		return finger.get(0).node;	
	}
	
	public int getPredecessor(){
		return predecessor;
	}

	public void setPredecessor(int id){
		predecessor = id;
	}
	
	public boolean withinRange(int id1, int id2, int id){
		if(id1 < id2){
			if(id > id1 && id < id2) return true;
		}
		else{
			if (id > id1 || id < id2) return true;
		}
		return false;
	}

	public boolean withinRangeeE(int id1, int id2, int id){
		if(id1 < id2){
			if(id > id1 && id <= id2) return true;
		}
		else{
			if (id > id1 || id <= id2) return true;
		}
		return false;
	}
	
	public boolean withinRangeEe(int id1, int id2, int id){
		if(id1 < id2){
			if(id >= id1 && id < id2) return true;
		}
		else{
			if (id >= id1 || id < id2) return true;
		}
		return false;
	}

	public int findPredecessor(int id){
		if(predecessor == index)
		return index;
		
		int curr = index;

		while(!withinRangeeE(curr,chord.getNode(curr).getSuccessor(), id)){
			chord.incrementCount();
			curr = chord.getNode(curr).closestPrecedingFinger(id);
		}
		return curr;
	}

	public void showFinger(){
		for(int i=0; i < TOTAL_NUM; i++){
			System.out.println("finger " + i + " : start=>" + finger.get(i).start 
			+ ", end=>" + finger.get(i).end + ", node=>" + finger.get(i).node );
		}
		chord.setComplete(true);
	}

	public int closestPrecedingFinger(int id){
		for(int i = TOTAL_NUM - 1; i >= 0; i-- ){
			if(withinRange(index,id,finger.get(i).node))
				return finger.get(i).node;
		}	
		return index;
	}

	public void initFingerTable(){
		finger.get(0).node = chord.getNode(0).findSuccessor(finger.get(0).start);
		chord.incrementCount();
		Node successor = chord.getNode(finger.get(0).node);
		predecessor = successor.getPredecessor();
		successor.setPredecessor(index);
		System.out.println("Initializing finger table for node " + index);
		
		for(int i=0; i < TOTAL_NUM-1; i++){
			System.out.println("Index = " + index + ", ith " + i + " node = " + finger.get(i).node
			+ ", start = " + finger.get(i+1).start);
			if(withinRangeEe(index,finger.get(i).node,finger.get(i+1).start)){
				finger.get(i+1).node = finger.get(i).node % TOTAL_NODE;
			}
			else{
				int ID = chord.getNode(0).findSuccessor(finger.get(i+1).start);
//				System.out.println("successor = " + ID + " for node " + finger.get(i+1).start + ", i = " + i);
				if(withinRangeEe(finger.get(i+1).start, index, ID)){
					finger.get(i+1).node = ID;
				}
				else{
					finger.get(i+1).node = index;
				}
			}
		}
		showFinger();
	}

	public void updateFingerTable(int nodeID, int fingerID){
		chord.incrementCount();
		if(withinRange(index,finger.get(fingerID).node,nodeID)){
			finger.get(fingerID).node = nodeID;
			chord.getNode(predecessor).updateFingerTable(nodeID,fingerID);
		}
	}
	
	public void updateFingerTableLeave(int nodeID, int fingerID){
		chord.incrementCount();
		if(finger.get(fingerID).node == nodeID){
			finger.get(fingerID).node = chord.getNode(nodeID).getSuccessor();
			chord.getNode(predecessor).updateFingerTableLeave(nodeID,fingerID);
		}
	}
	
	public void updateOthersLeave(){
		int step = 1;
		int nodeID = 0;
		int changeID = 0;
		for(int i=0; i < TOTAL_NUM; i++){
			changeID = index + 1 - step;
			if(changeID < 0) changeID += TOTAL_NODE;
			nodeID = findPredecessor(changeID);
			chord.getNode(nodeID).updateFingerTableLeave(index,i);
			step *=2;
		}
		chord.getNode(finger.get(0).node).setPredecessor(predecessor);
	}

	public void updateOthers(){
		int step = 1;
		int nodeID = 0;
		int changeID = 0;
		for(int i=0; i < TOTAL_NUM; i++){
			changeID = index + 1 - step;
			if(changeID < 0) changeID += TOTAL_NODE;
			nodeID = findPredecessor(changeID);
			chord.getNode(nodeID).updateFingerTable(index,i);
			step *=2;
		}
	}

	public synchronized void removeData(int start, int end){
		if(end > start){
			SortedSet<Integer> t1 = new TreeSet<Integer>(getData(end,256));
			SortedSet<Integer> t2 = new TreeSet<Integer>(getData(0,start));
			data.clear();
			data.addAll(t1);
			data.addAll(t2);
		}
		else{
			SortedSet<Integer> t1 = new TreeSet<Integer>(getData(end,start));
			data.clear();
			data.addAll(t1);
		}
	}

	public synchronized void moveData(){
		Node pre = chord.getNode(predecessor);
		if(index > finger.get(0).node){
			SortedSet<Integer> moved1 = pre.getData(index, 256);
			SortedSet<Integer> moved2 = pre.getData(0, finger.get(0).node);
			addData(moved1);
			addData(moved2);
		}
		else{
			SortedSet<Integer> moved = pre.getData(index, finger.get(0).node);
			addData(moved);
		}
		chord.incrementCount();
		pre.removeData(index, finger.get(0).node);
	}

	public void join(){
		// we always use node 0 as the starting node
		initFingerTable();
		updateOthers();
		moveData();
		System.out.println("Join complete");
		chord.setComplete(true);
	}


	public void leave(){
		updateOthersLeave();
		chord.getNode(predecessor).addData(data);
		// data message
		chord.incrementCount();
		chord.setComplete(true);
	}	



	@Override
	public void run(){
		if(index != 0){
			System.out.println("Node " + index + " is running");
			join();
		}
		while(true);
	}
}












