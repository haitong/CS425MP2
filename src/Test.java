
import java.util.*;
import java.io.*;

public class Test{


    private int P; //number of processor to join in the chord
                   //4, 8, 10, 20, 30
    private int F; //number of find operations need to execute
                   // >= 64
    String res_file_name;//output result to the file

    public Test(int P, int F, String res_name, int id) throws IOException{

        this.P = P;
        this.F = F;
        this.res_file_name = res_name+"."+id+".output";

        String input_file_name = res_name+"."+id+".input";

        BufferedWriter output_file = new BufferedWriter(new FileWriter(this.res_file_name));
        String s = new String("Stat "+id+":");
        output_file.write(s, 0, s.length());
        output_file.newLine();
        output_file.flush();
        output_file.close();

 
        BufferedWriter input_file = new BufferedWriter(new FileWriter(input_file_name));

        //join P nodes to chord
        Random rand_generator = new Random(System.currentTimeMillis());
        Vector<Integer> node_set = new Vector<Integer>();
        node_set.add(0);
        int i=0;
        while(i<P){
            int rand_num = rand_generator.nextInt(256);
            if(node_set.contains(rand_num))
                continue;
            //guarantee don't insert duplicated node
            node_set.add(rand_num);
            String cmd = new String("join ");
            cmd += rand_num;
            input_file.write(cmd, 0, cmd.length());
            input_file.newLine();
            i++;
        }

        i=0;
        while(i<F){
            int rand_key = rand_generator.nextInt(256);
            int rand_node = node_set.get(rand_generator.nextInt(node_set.size()));
            String cmd = new String("find ");
            cmd += rand_node + " "+ rand_key;
            input_file.write(cmd, 0, cmd.length());
            input_file.newLine();
            i++;
        }

        String cmd = new String("exit");
        input_file.write(cmd, 0, cmd.length());
        input_file.newLine();

        input_file.flush();
        input_file.close();

       new Chord(input_file_name, this.res_file_name);

    }

    public static void main(String[] args){

        try{
//            for(int i=0; i<Integer.parseInt(args[3]); i++){
                Test test = new Test(Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
//            }
        } catch (IOException e){
            System.out.println(e);
        }

//        System.out.println("Done!");
//        System.exit(0);
    }

}
