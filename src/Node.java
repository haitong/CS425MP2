import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;



public class Node extends chord implements Runnable{

	private Map<Integer, Integer> data = new HashMap<Integer,Integer>();

	private List<fingerEntry> finger = new ArrayList<fingerEntry>();

	private int predecessor;
	private int index;

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

	public void updateFinger(){
	}
	
	public synchronized void addData(int key, int value){
		data.put(key,value);
	}

	public synchronized void getData(int key){
		return data.get(key);
	}

	public int findSuccessor(int id){
		
	}

	public int getPredecessor(){
		return predecessor;
	}

	public int findPredecessor(int id){
		if(predecessor == index)
		return index;
		
		int curr = index;

		while(!withinRange(curr,chord.getNode(curr).getSuccessor())){
			curr = chord.getNode(curr).closestPrecedingFinger(id);
		}
		return curr;
	}

	public int closestPrecedingFinger(int id){
		for(int i = TOTAL_NUM - 1; i >= 0; i-- ){
			if(finger[i].node > index && finger[i].node < id)
				return finger[i].node;
		}	
		return index;
	}
}






















