/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Kavish-PC
 */
public class Packet {
    
    private final SimpleIntegerProperty referenceNumber;
    private final SimpleStringProperty protocol;
    private final SimpleStringProperty source_ip;
    private final SimpleIntegerProperty source_port;
    private final SimpleStringProperty destination_ip;
    private final SimpleIntegerProperty destination_port;
 
    public Packet(int referenceNumber, String protocol, String source_ip, int source_port, String destination_ip, int destination_port) 
    {
        this.referenceNumber = new SimpleIntegerProperty(referenceNumber);
        this.protocol = new SimpleStringProperty(protocol);
        this.source_ip = new SimpleStringProperty(source_ip);
        this.source_port = new SimpleIntegerProperty(source_port);
        this.destination_ip = new SimpleStringProperty(destination_ip);
        this.destination_port = new SimpleIntegerProperty(destination_port);
    }
    
    public int getReferenceNumber()
    {
        return referenceNumber.get();
    }
    public String getProtocol()
    {
        return protocol.get();
    }
    public String getSource_ip()
    {
        return source_ip.get();
    }
    public String getDestination_ip()
    {
        return destination_ip.get();
    }
    public int getSource_port()
    {
        return source_port.get();
    }
    public int getDestination_port()
    {
        return destination_port.get();
    }
    public String getStringRef()
    {
        return Integer.toString(referenceNumber.get());
    }
    
    
}
