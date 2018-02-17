/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import jess.*;

/**
 *
 * @author Kavish-PC
 */
public class DetectionEngine {
    
    public Rete engine;
    public Operations start;
    public static String rules = RulesManagerMenuController.rulesFile;
    public static LoggingEngine loggingEngine;
    public static boolean simpleMode = false;
    
    public DetectionEngine(Operations s)
    {
        engine = initialize();
        start = s;
    }
    
    public void setLoggingEngine(LoggingEngine l)
    {
        loggingEngine = l;
    }
    
    public void setMode(boolean m)
    {
        simpleMode = m;
    }
    
    public Rete initialize()
    {
        try
        {
            Rete engine = new Rete();
            return engine;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public void loadRules()
    {
        try
        {
            engine.batch(rules);
        }
        catch(Exception e)
        {
            e.printStackTrace();   
        }
    }
    
    public void updateRulesFile(String location)
    {
        rules = location;
    }
    
    public String loadFact(int counter, FXMLController control, String protocol, String s_ip, String s_port, String d_ip, String d_port, String content)
    {
        String str = "";
        try
        {
            String command = convertToFact(protocol, s_ip, s_port, d_ip, d_port, content);
            String output = "empty";
            engine.store("output", output);
            engine.reset();
            engine.executeCommand(command);
            
            engine.run();
            Value v = engine.fetch("output");
            String value = v.stringValue(engine.getGlobalContext());
            if(value.equals("empty") == false)
            {
                control.addAlert(value, getTime(), counter);
                control.getStatisticsMenu().updateAlert();
                if(simpleMode)
                    control.addEntry(counter, protocol.toUpperCase(), s_ip, Integer.parseInt(s_port), d_ip, Integer.parseInt(d_port));
                loggingEngine.log(getTime() + " Alert Generated\r\n"
                        + "Packet Reference: " + counter + "\r\n" 
                        + "Attack Description: " + value + "\r\n" 
                        + "Packet Details: Source IP: "+s_ip+" Source Port: "
                        +s_port+"\r\nDestination IP: " + d_ip + ""
                        + " Destination Port: " + d_port + "\r\n\r\n\t#Start of Content#\r\n\r\n" 
                        + content + "\t\n#End of Content#\r\n");
            }
            else
            {
                if(!simpleMode)
                    control.getStatisticsMenu().updateTotal();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return str;
    }
    
    public String getTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public String convertToFact(String protocol, String s_ip, String s_port, String d_ip, String d_port, String content)
    {
        String command = "";
        String quote = "\"";
        content = content.replace("\"", "");
        content = content.replace("\\", "");
        if(protocol.equals("icmp"))
        {
            command = "(assert(packet (protocol icmp)" + "(source_ip " + s_ip + ")(destination_ip " + d_ip + ")"
                    + "(content " + quote + content + quote + ")))";
        }
        else
        {
            command = "(assert(packet (protocol " + protocol + ")(source_ip " + s_ip + ")(source_port "+ s_port + ")"
                    + "(destination_ip " + d_ip + ")(destination_port " + d_port + ")(content " + quote + content + quote + ")))";
        }
        return command;
    }
    
}

