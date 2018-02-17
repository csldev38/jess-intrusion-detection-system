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
public class IntrusionAlert {
    
    private final SimpleStringProperty details;
    private final SimpleStringProperty timestamp;
    private final SimpleIntegerProperty packetReference;
 
    public IntrusionAlert(String details, String timestamp, int ref) 
    {
        this.details = new SimpleStringProperty(details);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.packetReference = new SimpleIntegerProperty(ref);
    }
    
    public String getDetails()
    {
        return details.get();
    }
    public String getTimestamp()
    {
        return timestamp.get();
    }
    
    public int getPacketReference()
    {
        return packetReference.get();
    }
    
    
}
