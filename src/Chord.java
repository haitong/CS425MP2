
import java.util.*;
import java.io.*;

public class Chord{
    
    Map<Integer, Node> nodeList = new HashMap<Integer, Node>();

    static public void main(){
        //create a node with id=0
        //TODO: initialize finger table and key-value for node 0
        Node n0 = new Node(this, 0);
        //insert n0 to nodeList
        nodeList.put(0, n0);

        //start thread for node 0
        System.out.println("Starting new thread for Node 0 ... ");
        new Thread(n0).start();

        //keep reading from terminal, parse command
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

    private class Command{
        CmdType type;
        int p;
        int k;
    }

    public enum CmdType{
        JOIN,
        FIND,
        LEAVE,
        SHOW,
        SHOWALL,
        INVALID
    }

    private Command parseCmd(String s){
        Command cmd = new Command();

        String[] str = s.split(" ");
        if(str[0].equalsIgnoreCase("join")){
            cmd.type = CmdType.JOIN;
            cmd.p = Integer.parseInt(str[1]);
        } else{
            cmd.type = CmdType.INVALID;
            System.out.println("Can't recognize command!");
        }

    }
}
