
import java.util.*;
import java.io.*;

public class Chord{
    
    Map<Integer, Node> nodeList = new HashMap<Integer, Node>();

    public Chord(){
    
        //create a node with id=0
        //TODO: initialize finger table and key-value for node 0
        Node n0 = new Node(this, 0);
        //insert n0 to nodeList
        nodeList.put(0, n0);

        //start thread for node 0
        System.out.println("Starting new thread for Node 0 ... ");
        new Thread(n0).start();

        //keep reading from terminal, parse command, and execute
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try{
        
            String s;
            while((s = input.readLine())!=null){

                Command cmd = parseCmd(s);
                switch(cmd.type){
                    case JOIN:{
                        //create a new thread for the node p
                        Node node = new Node(this, cmd.p);
                        nodeList.put(cmd.p, node);
                        new Thread(node).start();
                        break;
                    } case SHOWFINGER:{
                        Node node = getNode(cmd.p);
                        node.showFinger();
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
        }else{
            cmd.type = CmdType.INVALID;
            System.out.println("Can't recognize command!");
        }
        return cmd;
    }
}
