package com.mycompany.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Console;
import java.io.FileReader;
import java.util.ArrayList;

public enum Command
{
    QUIT(new Action()
    {
        @Override
        public void exec(Console c, String[] params)
        {
            c.printf("Bye%n");
            System.exit(0);
        }
    }),
    DETAILS(new Action()
    {
        @Override
        public void exec(Console c, String[] params) throws Exception
        {
            int detailsLevel = 1;
            try
            {
                detailsLevel = Integer.parseInt(params[0]);
            }
            catch (NumberFormatException e)
            {
                // ignore
            }

            for (int i = 1; i <= detailsLevel; i++)
            {
                c.printf("Detail number %1$X%n", i);
            }
        }
    }),
    SORT(new Action()
    {
    	@Override
        public void exec(Console c, String[] params) throws Exception
        {
            ArrayList<Integer> list = null;
            try
            {
            	list = new ArrayList<Integer>();
            	for (String s: params)
            	{
            		Integer i = Integer.parseInt(s);
                    list.add(i);
            	}
                RunnableUnit unit = new RunnableUnit(list);
                unit.start();
            }
            catch(Exception e){
            	
            }
        }
    }),
    RESULTS(new Action()
    {
        @Override
        public void exec(Console c, String[] params)
        {
            try
            {
                int result_id = Integer.parseInt(params[0]);
                BufferedReader reader = new BufferedReader(new FileReader("sortset.txt"));
                String line = null;
                for(int i = 0; i < result_id; i++){
                    line = reader.readLine();
                }
                c.printf(line.substring(0,line.length()-7)+"\n");
            }catch(Exception e){}
        }
    });

    private interface Action
    {
        public void exec(Console c, String[] params) throws Exception;
    }

    public interface Listener
    {
        public void exception(Exception e);
    }

    private Action action;

    private Command(Action a)
    {
        this.action = a;
    }

    public void exec(final Console c, final String[] params, final Listener l)
    {
        try
        {
            action.exec(c, params);
        }
        catch (Exception e)
        {
            l.exception(e);
        }
    }
}
