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
public class Record {
    
    private SimpleStringProperty type;
    private SimpleIntegerProperty value;

    public SimpleStringProperty getType() {
        return type;
    }

    public void setType(SimpleStringProperty type) {
        this.type = type;
    }

    public SimpleIntegerProperty getValue() {
        return value;
    }

    public void setValue(SimpleIntegerProperty value) {
        this.value = value;
    }

    Record(String type, int value)
    {
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleIntegerProperty(value);
    }

    
    
}
