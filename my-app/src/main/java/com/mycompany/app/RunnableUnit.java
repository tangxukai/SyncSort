package com.mycompany.app;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * Created by tangxukai on 8/4/14.
 */
public class RunnableUnit implements Runnable {

    private Thread t;
    private ArrayList<Integer> list;

    RunnableUnit( ArrayList<Integer> l){
        list = l;
    }
    public void run(){

        try {


            PrintWriter writer = null;
            try {
                Integer[] int_arr = this.list.toArray(new Integer[0]);

                writer = new PrintWriter(new BufferedWriter(new FileWriter("sortset.txt", true)));

                Arrays.sort(int_arr);
                for (int i = 0; i < int_arr.length; i++) {
                    writer.append(int_arr[i] + " ");
                }

            } catch (Exception e) {

            }
            writer.append("SUCCEED\n");
            writer.flush();
            writer.close();

        }
        catch(Exception e){

        }
    }

    public void start ()
    {
        if (t == null)
        {
            t = new Thread (this);
            t.start ();
        }
    }

}
