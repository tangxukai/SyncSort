package com.mycompany.app;

/**
 * Hello world!
 *
 */
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

import org.apache.log4j.BasicConfigurator;


public class App
{
    private static final String NO_CONSOLE = "Error: Console unavailable";
    private static final String GREETINGS = "Welcome to the System. Please login.%n";
    private static final String DENIED_ATTEMPT = "Wrong user name or password [%1$d]%n";
    private static final String ACCESS_DENIED = "Access denied%n";
    private static final String ACCESS_GRANTED = "Access granted%n";
    private static final String UNKNOWN_COMMAND = "Unknown command [%1$s]%n";
    private static final String COMMAND_ERROR = "Command error [%1$s]: [%2$s]%n";
    private static final String FILE_ERROR = "File could not be found or create";

    private static final String TIME_FORMAT = "%1$tH:%1$tM:%1$tS";
    private static final String PROMPT = TIME_FORMAT + " $ ";
    private static final String USER_PROMPT = TIME_FORMAT + " User: ";
    private static final String PASS_PROMPT = TIME_FORMAT + " Password [%2$s]: ";

    private static final String USER = "john";
    private static final String PASS = "secret";
    public static final Semaphore writeLock_sortset = new Semaphore(1);
    static Logger log = Logger.getLogger(
            App.class);
    private static final String temp_path = "temp.txt";
    //public static boolean DONE = false;

    public static void main(String[] args)
    {
        Console console = System.console();
        if (console != null)
        {
            BasicConfigurator.configure();
                execCommandLoop(console);
        }
    }

    private static void execCommandLoop(final Console console)
    {
        int counter = 0;
        boolean start_status = true;
        while (true)
        {
           if(start_status)
           {
               try
                    {
                        //copy from sortset to temp
                        BufferedReader exam_term = new BufferedReader(new FileReader(new File("sortset.txt")));
                        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(temp_path)),false);
                        String sort_line = null;
                        while((sort_line = exam_term.readLine())!= null){
                            writer.println(sort_line);
                        }
                        writer.close();
                        //
                        exam_term = new BufferedReader(new FileReader(new File(temp_path)));
                        String first_part = "";
                        String line1 = null;
                        boolean incomplete = false;
                        //copy from temp to sortset when not suc just re execute
                        while((line1 = exam_term.readLine())!=null){

                            counter++;
                            if(!line1.endsWith("SUCCEED")){
                                incomplete = true;
                                console.printf("Redoing the incomplete task......\n");
                                final Command cmd1 = Enum.valueOf(Command.class, "SORT");
                                //we need to recover the line using temp_path file
                                BufferedReader temp = new BufferedReader(new FileReader(new File("query.txt")));
                                String line = null;
                                for(int i = 0; i < counter; i++){
                                    line = temp.readLine();
                                }
                                String[] param = line.split(" ");
                                ArrayList<Integer> list = new ArrayList<Integer>();
                                for (String s: param)
                                {
                                    Integer i = Integer.parseInt(s);
                                    list.add(i);
                                }
                                try {


                                    PrintWriter writer1 = null;
                                    StringBuilder sb= null;
                                    try {
                                        Integer[] int_arr = list.toArray(new Integer[0]);
                                        // to write and remove the previous content
                                        writer1 = new PrintWriter(new BufferedWriter(new FileWriter("sortset.txt")),true);
                                        writer1.print(first_part);
                                        first_part="";
                                        Arrays.sort(int_arr);
                                        sb = new StringBuilder();
                                        for (int i = 0; i < int_arr.length; i++) {
                                            sb.append(int_arr[i] + " ");
                                        }

                                    } catch (Exception e) {

                                    }
                                    sb.append("SUCCEED\n");
                                    writer1.print(sb.toString());
                                    writer1.close();

                                }
                                catch(Exception e){

                                }
                            }
                            if(line1.endsWith("SUCCEED"))first_part = first_part + line1 + "\n";
                        }
                        if(incomplete)writer = new PrintWriter(new BufferedWriter(new FileWriter("sortset.txt", true)));
                        else writer = new PrintWriter(new BufferedWriter(new FileWriter("sortset.txt", false)));

                        writer.print(first_part);

                        writer.close();
                        PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter(temp_path)));
                        writer2.write(first_part);
                        writer2.close();
                    }

                    catch(Exception e)
                    {

                    }
           }// end of if start status
            start_status= false;
            String commandLine = console.readLine(PROMPT, new Date());
            Scanner scanner = new Scanner(commandLine);
            BufferedReader reader = null;
            int request_id = 0;
            boolean sort_command = false;
            if (scanner.hasNext())
            {
                if(scanner.next().equals("sort")) {
                    sort_command = true;
                    try {
                        reader = new BufferedReader(new FileReader("count.txt"));
                        try {
                            String line = reader.readLine();
                            request_id = Integer.parseInt(line);
                            reader.close();
                        } catch (IOException e) {
                            //
                        }
                        request_id++;
                        PrintWriter writer = new PrintWriter("count.txt", "UTF-8");
                        writer.println(request_id);
                        writer.close();
                    } catch (IOException e) {

                        try {

                            PrintWriter writer = new PrintWriter("count.txt", "UTF-8");
                            request_id++;
                            writer.println((request_id) + "");
                            writer.close();
                        } catch (IOException e2) {
                            console.printf(FILE_ERROR);
                        }
                    }
                }
                scanner = new Scanner(commandLine);
                final String commandName = scanner.next().toUpperCase();

                try
                {
                    final Command cmd = Enum.valueOf(Command.class, commandName);
                    ArrayList<String> str_list = new ArrayList<String>();
                    while(scanner.hasNext()){
                        str_list.add(scanner.next());
                    }
                    String[] param = str_list.toArray(new String[0]);
                    if(sort_command)
                    {
                        /***
                         * use query.txt to record the sort query we have made to the console.
                         * as well, we use this file to redo the task that has not been done completely
                         * ***/
                        try
                        {
                            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("query.txt", true)));

                            for (int i = 0; i < param.length; i++) {
                                writer.append(param[i] + " ");
                            }
                            writer.append("\n");
                            writer.close();

                        }catch(Exception e){

                        }
                    }
                    if(sort_command)console.printf(request_id+"\n");
                    cmd.exec(console, param, new Command.Listener()
                    {
                        @Override
                        public void exception(Exception e)
                        {
                            console.printf(COMMAND_ERROR, cmd, e.getMessage());
                        }
                    });
                    if(sort_command)log.info(request_id+": SORT SUCCEED!");
                }
                catch (IllegalArgumentException e)
                {
                    console.printf(UNKNOWN_COMMAND, commandName);
                }
            }

            scanner.close();
        }
    }
}

