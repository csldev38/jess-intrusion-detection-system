/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JTable;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.JFormatter;
import org.jnetpcap.packet.format.XmlFormatter;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.network.Icmp;

/**
 *
 * @author Kavish
 */
public class Operations {
    public static List<PcapIf> list;
    public static int counter = 0;
    public static Pcap pcap;
    public static DetectionEngine engine;
    public static boolean simpleMode = false;
    static StatisticsMenuController statistics;
    static LoggingEngine loggingEngine;
    
    public static int tcp_counter = 0;
    public static int udp_counter = 0;
    public static int icmp_counter = 0;
    public static int others_counter = 0;
    
    public Operations()
    {
        engine  = new DetectionEngine(this);
        engine.initialize();
        engine.loadRules();
    }
    
    public LoggingEngine getLoggingEngine()
    {
        return loggingEngine;
    }
    
    public void setLoggingEngine(LoggingEngine l)
    {
        loggingEngine = l;
        engine.setLoggingEngine(loggingEngine);
    }
    
    public void setMode(boolean m)
    {
        simpleMode = m;
        engine.setMode(m);
    }
    
    public static DetectionEngine getDetectionEngine()
    {
        return engine;
    }
    
    public void setStatistics(StatisticsMenuController s)
    {
        statistics = s;
        
    }
    
    public static List<PcapIf> findInterfaces()
    {
        List<PcapIf> alldevs = new ArrayList<PcapIf>();
        StringBuilder errbuf = new StringBuilder();


        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
                System.out.println("Unable to list network interfaces! Error: " + errbuf.toString());

                return null;
            } 

        
        list = alldevs;
        return list;
    }
    
    public static List<String> findInterfaceName(List<PcapIf> interfaces)
    {
        List<String> list = new ArrayList<String>();
        
        int i = 0;
        for(PcapIf device : interfaces)
        {
            i++;
            if(device.getDescription() == null)
            {
                System.out.println("No description available");
            }
            else
            {
                list.add(device.getDescription());
            }
        }
        
        
        return list;
    }
    
    
    public void capturePackets(int index, FXMLController control)
    {
        System.out.println("Capture Started!");
        StringBuilder errbuf = new StringBuilder();
        
        list = findInterfaces(); // added for testing
        PcapIf device = list.get(index);
        String str = device.getDescription() + " : " + device.getName();
        
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 10 * 1000;           // 10 seconds in millis  
        
        pcap =  
            Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
        
        if (pcap == null) {  
            System.err.print("Unable to open device for capture! Error: " + errbuf.toString());  
            return;
        }  
        
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() 
        {  
            public void nextPacket(PcapPacket packet, String user) {  
            
                identifyPacket(packet, control);
                
            }  
        }; 
        pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "");
        pcap.close();
    }
    
    
    
    public void identifyPacket(PcapPacket packet, FXMLController control)
    {
        Ip4 ip = new Ip4();  
        Tcp tcp = new Tcp(); 
        Udp udp = new Udp();
        Icmp icmp = new Icmp();
        
        if(packet.hasHeader(ip))
        {
            ip = packet.getHeader(ip);
            String source_ip = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
            String destination_ip = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());

            if(packet.hasHeader(tcp))
            {
                tcp = packet.getHeader(tcp);
                String content = "";
                int source_port = tcp.source();
                int destination_port = tcp.destination();

                String source = Integer.toString(source_port);
                String destination = Integer.toString(destination_port);
                
                
                Payload payload = new Payload();
                if(tcp.getPayloadLength()>0)
                {
                    try
                    {
                        byte[] temp = tcp.getPayload();
                        content = new String(temp, "UTF-8");
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
                counter++;
                tcp_counter++;
                if(!simpleMode)
                {
                    statistics.updateTCP(tcp_counter);
                    control.addEntry(counter, "TCP", source_ip, source_port, destination_ip, destination_port);
                }
                engine.loadFact(counter, control, "tcp", source_ip, source, destination_ip , destination, content);
            }
            else if(packet.hasHeader(udp))
            {
                udp = packet.getHeader(udp);
                String content = "";
                int source_port = udp.source();
                int destination_port = udp.destination();

                String source = Integer.toString(source_port);
                String destination = Integer.toString(destination_port);
                
                
                Payload payload = new Payload();
                if(udp.getPayloadLength()>0)
                {
                    try
                    {
                        byte[] temp = udp.getPayload();
                        content = new String(temp, "UTF-8");
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
                udp_counter++;
                counter++;
                if(!simpleMode)
                {
                    statistics.updateUDP(udp_counter);
                    control.addEntry(counter, "UDP", source_ip, source_port, destination_ip, destination_port);
                }
                engine.loadFact(counter, control, "udp", source_ip, source, destination_ip , destination, content);
            }
            else if(packet.hasHeader(icmp))
            {
                String content = "";
                Payload payload = packet.getHeader( new Payload() );
                byte[] temp = payload.getPayload();
                try
                {
                    content = new String(temp, "UTF-8");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                icmp_counter++;
                counter++;
                if(!simpleMode)
                {
                    statistics.updateICMP(icmp_counter);
                    control.addEntry(counter, "ICMP", source_ip, 0, destination_ip, 0);
                }
                engine.loadFact(counter, control, "icmp", source_ip, "", destination_ip , "", content);
            }
            else
            {
                others_counter++;
                if(!simpleMode)
                {
                    statistics.updateOthers(others_counter);
                }
            }
        }
    }
    
   
    
    
}
