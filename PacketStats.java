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
public class PacketStats {
    
    private final SimpleStringProperty protocol;
    private final SimpleIntegerProperty counter;

    public String getProtocol() {
        return protocol.get();
    }

    public int getCounter() {
        return counter.get();
    }
    
    public PacketStats(String p, int c)
    {
        protocol = new SimpleStringProperty(p);
        counter = new SimpleIntegerProperty(c);
    }
    
}
