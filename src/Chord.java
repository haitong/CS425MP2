
import java.util.*;
import java.io.*;

public class Chord{
    
    TreeMap<Integer, Node> nodeList = new TreeMap<Integer, Node>();
    Map<Integer, Thread> threadList = new HashMap<Integer, Thread>();

    public Chord(){
    
        //create a node with id=0
        //TODO: initialize finger table and key-value for node 0
        Node n0 = new Node(this, 0);
        //insert n0 to nodeList
        nodeList.put(0, n0);

        //start thread for node 0
        System.out.println("Starting new thread for Node 0 ... ");
        Thread t0 = new Thread(n0);
        threadList.put(0, t0);

        t0.start();
        //initialize keys in node 0
        for(int i=0; i<=255; i++){
            n0.addData(i);
        }

        //keep reading from terminal, parse command, and execute
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try{
        
            String s;
            while((s = input.readLine())!=null){

                Command cmd = parseCmd(s);
                switch(cmd.type){
                    case JOIN:{
                        //reject "join" if node already exists
                        if(nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" already exists. Reject Command.");
                            break;
                        }
                        //create a new thread for the node p
                        Node node = new Node(this, cmd.p);
                        nodeList.put(cmd.p, node);
                        Thread thread = new Thread(node);
                        threadList.put(cmd.p, thread);
                        thread.start();
                        break;
                    } case SHOWFINGER:{
                        //show the finger table of specified node
                        //check the node exists or not
                        if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.showFinger();
                        break;
                    } case SHOW: {
                        //show keys exist in specified node
                        if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.printKey();
                        break;
                    } case SHOWALL : {
                        //print all node and their keys
                        for(int i : nodeList.keySet()){
                            getNode(i).printKey();
                        }
                        break;
                    } case LEAVE: {
                         if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.leave();
                        //remove the node from map and terminate threads
                        nodeList.remove(cmd.p);
                        //TODO: stop thread
                        //threadList.get(cmd.p).kill();
                        break;
                    }
                    default:
                        System.out.println("Please input valid command.");
                        break;
                }
            }

        } catch(IOException e){
            System.out.println("IO error when reading from terminal.");
            e.printStackTrace(System.err);
        }
   
    }
    //command class used to parse terminal input
    private class Command{
        CmdType type;
        int p;
        int k;
    }

    //command line type
    private enum CmdType{
        JOIN, FIND, LEAVE, SHOW, SHOWALL, INVALID, SHOWFINGER
    }

    public static void main(String[] args){
   
        System.out.println("Starting Chord...");
        Chord chord = new Chord();
    }

    public Node getNode(int id){
        return nodeList.get(id);
    }

    private Command parseCmd(String s){
        Command cmd = new Command();

        String[] str = s.split(" ");
        if(str[0].equalsIgnoreCase("join")){
            cmd.type = CmdType.JOIN;
            cmd.p = Integer.parseInt(str[1]);
        } else if(str[0].equalsIgnoreCase("show") && str[1].equalsIgnoreCase("all")){
            cmd.type = CmdType.SHOWALL;
        } else if(str[0].equalsIgnoreCase("show") && !str[1].equalsIgnoreCase("all")){
            cmd.type = CmdType.SHOW;
            cmd.p = Integer.parseInt(str[1]);
        } else if(str[0].equalsIgnoreCase("showfinger")){
            cmd.type = CmdType.SHOWFINGER;
            cmd.p = Integer.parseInt(str[1]);
        } else if(str[0].equalsIgnoreCase("leave")){
            cmd.type = CmdType.LEAVE;
            cmd.p = Integer.parseInt(str[1]);
        } else if(str[0].equalsIgnoreCase("find")){
            cmd.type = CmdType.FIND;
            cmd.p = Integer.parseInt(str[1]);
            cmd.k = Integer.parseInt(str[2]);
        }else{
            cmd.type = CmdType.INVALID;
            System.out.println("Can't recognize command!");
        }
        return cmd;
    }
}
