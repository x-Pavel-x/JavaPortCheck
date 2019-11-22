package com.company;

import org.apache.commons.cli.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException{
        CommandLine comLine;
        Option opt_h = Option.builder("h")
                .required(true)
                .desc("host")
                .longOpt("opt1")
                .build();
        opt_h.setArgs(10);
        opt_h.setOptionalArg(true);

        Option opt_p = Option.builder("p")
                .required(true)
                .desc("port")
                .longOpt("opt2")
                .build();
        opt_p.setArgs(10);
        opt_p.setOptionalArg(true);

        Option opt_t = Option.builder("t")
                .required(false)
                .desc("threads")
                .longOpt("opt3")
                .build();
        opt_t.setArgs(1);
        opt_t.setOptionalArg(true);

        Options opts = new Options();
        CommandLineParser parser = new DefaultParser();
        opts.addOption(opt_h);
        opts.addOption(opt_p);
        opts.addOption(opt_t);
        String[] ips = {""};
        String[] ports = {""};
        int threadAmount=1;
        try
        {
           comLine = parser.parse(opts, args);
            if(comLine.hasOption("h"))
            {
                 ips = comLine.getOptionValues("h");
            }
            if(comLine.hasOption("p"))
            {
                ports = comLine.getOptionValues("p");
            }
            if(comLine.hasOption("t"))
            {
                threadAmount=Integer.parseInt(comLine.getOptionValue("t"));
            }
        }

        catch(ParseException ex)
        {
            System.out.println(ex.toString());
        }
        String[] trueips = new String[ips.length*255];
        int count = 0;
        for(int i = 0 ; i<ips.length;i++)
        {
            int min,max;
            String tmp;
            if(ips[i].contains("-"))
            {
                min = Integer.parseInt(ips[i].split("\\.")[3].split("-")[0]);
                max = Integer.parseInt(ips[i].split("\\.")[3].split("-")[1]);
                for(int j = 0; j<(max-min)+1;j++)
                {
                    tmp = Integer.toString(min+j);

                    trueips[i+count+j]=ips[i].substring(0, ips[i].lastIndexOf('.')+1)+tmp;
                }
                count+=(max-min);
            }
            else
            {
                trueips[i+count]=ips[i];
            }
        }
        ArrayList<String> tmps = new ArrayList();
        int y=0;
        while (trueips[y]!=null){
            tmps.add(trueips[y]);
            y++;
        }
        trueips = tmps.toArray(new String[tmps.size()]);
        count = 0;
        int[] truePorts = new int[65535];
        for(int i = 0 ; i<ports.length;i++)
        {
            int min,max;
            if(ports[i].contains("-"))
            {
                min = Integer.parseInt(ports[i].split("-")[0]);
                max = Integer.parseInt(ports[i].split("-")[1]);
                for(int j = 0; j<(max-min)+1;j++)
                {
                    truePorts[i+count+j]=min+j;
                }
                count+=(max-min);
            }
            else
            {
                truePorts[i+count]=Integer.parseInt(ports[i]);
            }
        }
        int y2=0;
        int[] tmp2;
        while (truePorts[y2]!=0){
            y2++;
        }
        tmp2 = new int[y2];
        y2=0;
        while (truePorts[y2]!=0){
            tmp2[y2] = truePorts[y2];
            y2++;
        }
truePorts = tmp2;
        ArrayList<Thread> treads = new ArrayList();
        int amountHosts = trueips.length/threadAmount;
        int modAmountHosts = trueips.length%threadAmount;
        String[] trueipsByThreads= new String[amountHosts+1];
        int i2=0;
for(int q = 1; q <threadAmount+1;q++)
{
    if(modAmountHosts!=0)
    {
        trueipsByThreads[amountHosts] = trueips[trueips.length - q];
        modAmountHosts--;
    }
    for(int i = 0 ; i<amountHosts;i++)
    {
        trueipsByThreads[i] = trueips[i+i2];
    }
    i2+=amountHosts;
               treads.add(q - 1, new Thread(new ScanHost(trueipsByThreads, truePorts)));
               treads.get(q - 1).start();
               TimeUnit.MILLISECONDS.sleep(300);
            if(amountHosts==0 && modAmountHosts==0)
            {
                break;
            }
}
    }
}
