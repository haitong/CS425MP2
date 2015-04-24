
import java.util.*;
import java.io.*;

public class Chord{
    
    TreeMap<Integer, Node> nodeList = new TreeMap<Integer, Node>();
    Map<Integer, Thread> threadList = new HashMap<Integer, Thread>();
    BufferedReader input = null;
    BufferedWriter res_output = null;
    BufferedWriter redirect_output = null;
    Stat stat = new Stat();
    boolean completeSignal = false;
    Object completeLock = new Object();
    Object countLock = new Object();

    public class Stat{
        int join_cmd=0;
        int join_message=0;
        int find_cmd=0;
        int find_message=0;
    }

    int messageCount = 0;


    public void setInput(String s){
        try{
            input = new BufferedReader(new FileReader(s));
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void setOutput(String s){
        try{
            res_output = new BufferedWriter(new FileWriter(s, true));
        }catch(IOException e){
            System.out.println(e);
        }
    }

    private void redirectOutput(String s){
        try{
            redirect_output = new BufferedWriter(new FileWriter(s, true));
        } catch(IOException e){
            System.out.println(e);
        }
    }

    public Chord(String input, String output){
        setInput(input);
        setOutput(output);
        this.start();
    }

    public Chord(String output){
        redirectOutput(output);
        this.start();
    }

    public Chord(){
        this.start();
    }

    public void start(){
    
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
        if(input==null)
            input = new BufferedReader(new InputStreamReader(System.in));

        try{
        
            String s;
            while((s = input.readLine())!=null){

                setComplete(false);
                Command cmd = parseCmd(s);
                //reset meesage count before execute command
                synchronized(countLock){
                    messageCount = 0;
                }
                switch(cmd.type){
                    case JOIN:{
                        //reject "join" if node already exists
                        if(nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" already exists. Reject Command.");
                            setComplete(true);
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
                            setComplete(true);
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.showFinger();
                        break;
                    } case SHOW: {
                        //show keys exist in specified node
                        if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            setComplete(true);
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.printKey(redirect_output);
                        break;
                    } case SHOWALL : {
                        //print all node and their keys
                        for(int i : nodeList.keySet()){
                            getNode(i).printKey(redirect_output);
                        }
                        break;
                    } case LEAVE: {
                         if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            setComplete(true);
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.leave();
                        //remove the node from map and terminate threads
                        nodeList.remove(cmd.p);
                        //TODO: stop thread
                        //threadList.get(cmd.p).kill();
                        break;
                    } case FIND: {
                         if(!nodeList.containsKey(cmd.p)){
                            System.out.println("Node "+cmd.p+" doesn't exist. Reject Command.");
                            setComplete(true);
                            break;
                        }
                        Node node = getNode(cmd.p);
                        node.find(cmd.k);
                        break;
                    } case EXIT: {
                        if(input!=null){
                            input.close();
                        }
                        if(res_output!=null){
                            s = new String(stat.join_cmd+" "+stat.join_message);
                            res_output.write(s, 0, s.length());
                            res_output.newLine();
                            s = new String(stat.find_cmd+" "+stat.find_message);
                            res_output.write(s, 0, s.length());
                            res_output.newLine();
                            res_output.flush();
                            res_output.close();
                        }
                        if(redirect_output!=null){
                            redirect_output.close();
                        }
                        System.exit(0);
                    }
                    default:
                        System.out.println("Please input valid command.");
                        setComplete(true);
                        break;
                }

                if(cmd.type==CmdType.JOIN || cmd.type==CmdType.FIND){
                    while(readComplete() == false) ;
/*
                    //After execution, print out the count
                    if(cmd.type==CmdType.JOIN){
                        System.out.print("Join ");
                    }else{
                        System.out.print("Find " + cmd.p + " " + cmd.k + " ");
                    }
                    System.out.println("Message Count = " + readCount());
*/
				}
                //update stat
                if(cmd.type==CmdType.JOIN){
                    stat.join_cmd ++;
                    stat.join_message += readCount();
                } else if(cmd.type==CmdType.FIND){
                    stat.find_cmd ++;
                    stat.find_message += readCount();
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
        JOIN, FIND, LEAVE, SHOW, SHOWALL, INVALID, SHOWFINGER, EXIT
    }
    
    public static void main(String[] args){
 
        System.out.println("Starting Chord...");
 
        if(args[0]!=null && args[1]!=null){
            Chord chord = new Chord(args[1]);
        }else{
            Chord chord = new Chord();
        }
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
        } else if(str[0].equalsIgnoreCase("show-all")){
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
        } else if(str[0].equalsIgnoreCase("exit")){
            cmd.type = CmdType.EXIT;
        } else{
            cmd.type = CmdType.INVALID;
            System.out.println("Can't recognize command!");
        }
        return cmd;
    }

    public void incrementCount(){
        synchronized(countLock){
            messageCount ++;
        }
    }

    public int readCount(){
        synchronized(countLock){
            return messageCount;
        }
    }

    public void setComplete(boolean b){
        synchronized(completeLock){
            completeSignal = b;
        }
    }

    public boolean readComplete(){
        synchronized(completeLock){
            return completeSignal;
        }
    }

}
