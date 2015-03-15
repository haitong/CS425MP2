import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;



public class Node implements Runnable{

	private Map<Integer, Integer> data = new HashMap<Integer,Integer>();

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


	public void updateFinger(){
	}
	
	public synchronized void addData(int key, int value){
		data.put(key,value);
	}

	public synchronized int getData(int key){
		return data.get(key);
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
			curr = chord.getNode(curr).closestPrecedingFinger(id);
		}
		return curr;
	}

	public void showFinger(){
		for(int i=0; i < TOTAL_NUM; i++){
			System.out.println("finger " + i + " : start=>" + finger.get(i).start 
			+ ", end=>" + finger.get(i).end + ", node=>" + finger.get(i).node );
		}
	}

	public int closestPrecedingFinger(int id){
		for(int i = TOTAL_NUM - 1; i >= 0; i-- ){
//			if(finger.get(i).node > index && finger.get(i).node < id)
			if(withinRange(index,id,finger.get(i).node))
				return finger.get(i).node;
		}	
		return index;
	}

	public void initFingerTable(){
		finger.get(0).node = chord.getNode(0).findSuccessor(finger.get(0).start);
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
				System.out.println("successor = " + ID + " for node " + finger.get(i+1).start + ", i = " + i);
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
		if(withinRange(index,finger.get(fingerID).node,nodeID)){
			finger.get(fingerID).node = nodeID;
			chord.getNode(predecessor).updateFingerTable(nodeID,fingerID);
		}
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

	public void join(){
		// we always use node 0 as the starting node
		initFingerTable();
		updateOthers();
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












