package com.company;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
public class ScanHost implements Runnable
{
    private String[] ip;
    private int[] ports;
    JSONObject obj = new JSONObject();
    volatile static JSONArray ipsInfo = new JSONArray();
    volatile static public int position = 0;
    ScanHost(String[] ip, int[] ports)
    {
        this.ip=ip;
        this.ports=ports;
    }
    @Override
    public void run()
    {
        for(String i: ip)
        {
            JSONArray OpenPorts = new JSONArray();
            JSONArray ClosedPorts = new JSONArray();
            if(i!=null)
            {
            for (int j : ports) {
            try {
                    Socket sk = new Socket();
                    sk.connect(new InetSocketAddress(i, j), 2);
                    OpenPorts.add(sk.getPort());
                    sk.close();
            } catch (Exception ex)
            {
                    //System.out.println(ex.toString());
                ClosedPorts.add(j);
            }
            }
                WriteToJsonFile(ClosedPorts, OpenPorts, i);
                position++;
              OpenPorts.clear();
            ClosedPorts.clear();
            }

        }
}
private synchronized void  WriteToJsonFile(JSONArray ClosedPorts, JSONArray OpenPorts, String ip)
{
    JSONObject elem = new JSONObject();
    elem.put("ip", ip);
    JSONArray OpenPorts2 = new JSONArray();
    JSONArray ClosedPorts2 = new JSONArray();
    OpenPorts2.addAll(OpenPorts);
    ClosedPorts2.addAll(ClosedPorts);
    elem.put("OpenPorts", OpenPorts2);
    elem.put("ClosedPorts", ClosedPorts2);
    ipsInfo.add(position,elem);
    obj.put("ipsInfo", ipsInfo);
    try {
        try (FileWriter file = new FileWriter("test.json")) {
            file.write(obj.toJSONString());
            file.flush();
        }

    } catch (IOException e) {
        System.out.println(e.toString());
    }
}
    }


