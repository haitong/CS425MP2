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
			f.start = id + jump;
			jump *= 2;
			f.end = id + jump - 1;
			finger.add(f);
		}
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
	
	public int findPredecessor(int id){
		if(predecessor == index)
		return index;
		
		int curr = index;

		while(!withinRange(curr,chord.getNode(curr).getSuccessor(), id)){
			curr = chord.getNode(curr).closestPrecedingFinger(id);
		}
		return curr;
	}

	public int closestPrecedingFinger(int id){
		for(int i = TOTAL_NUM - 1; i >= 0; i-- ){
			if(finger.get(i).node > index && finger.get(i).node < id)
				return finger.get(i).node;
		}	
		return index;
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

	public void initFingerTable(){
		finger.get(0).node = chord.getNode(0).findSuccessor(finger.get(0).start);
		Node successor = chord.getNode(finger.get(0).node);
		predecessor = successor.getPredecessor();
		successor.setPredecessor(index);
		
		for(int i=0; i < TOTAL_NUM-2; i++){
			if(withinRangeEe(index,finger.get(i).node,finger.get(i+1).start)){
				finger.get(i+1).node = finger.get(i).node;
			}
			else{
				finger.get(i+1).node = chord.getNode(0).findSuccessor(finger.get(i+1).start);
			}
		}
	}

	public void updateOthers(){
	}

	public void join(){
		// we always use node 0 as the starting node
		initFingerTable();
		updateOthers();
	}

	@Override
	public void run(){
	}
}






















