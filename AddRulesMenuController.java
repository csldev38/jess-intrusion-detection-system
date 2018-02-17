/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Kavish-PC
 */
public class AddRulesMenuController implements Initializable{
    
    static RulesManagerMenuController rulesManagerUI;
    
    @FXML
    ChoiceBox protocol;
    @FXML
    TextField source_ip;
    @FXML
    TextField source_port;
    @FXML
    TextField destination_ip;
    @FXML
    TextField destination_port;
    @FXML
    TextArea content;
    @FXML
    TextArea message;
    
    @FXML
    Label contentLabel;
    
    static String errorMessage = "";
    static int customRulesCounter;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        protocol.getItems().removeAll(protocol.getItems());
        protocol.getItems().addAll("ICMP", "TCP", "UDP");
        protocol.getSelectionModel().selectedIndexProperty().addListener(new
            ChangeListener<Number>(){
                public void changed(ObservableValue ov, Number value, Number new_value)
                {
                    if(new_value.intValue() == 0)
                    {
                        content.clear();
                        content.setEditable(false);
                        content.disableProperty();
                        contentLabel.setText("Content \n(disabled for ICMP packets)");
                    }
                    else if(new_value.intValue() == 1 || new_value.intValue() == 2)
                    {
                        content.setEditable(true);
                        contentLabel.setText("Content");
                    }
                }
            });
        
        content.setWrapText(true);
        message.setWrapText(true);
        
    }
    
    @FXML
    public void back()
    {
        rulesManagerUI.hideAddRulesMenu();
    }
    
    public void setController(RulesManagerMenuController fx)
    {
        rulesManagerUI = fx;
    }
   
    public void getRuleOptions()
    {
        errorMessage = "";
        boolean badEntry = false;
        if(protocol.getSelectionModel().isEmpty())
        {
            badEntry = true;
            errorMessage += "A Protocol was not selected.\n\n";
        }
        if(!isInteger(source_port.getText()) && !source_port.getText().equals(""))
        {
            badEntry = true;
            errorMessage += "Incorrect Source Port entry detected.\n\n";
        }
        if(!isInteger(destination_port.getText()) && !destination_port.getText().equals(""))
        {
            badEntry = true;
            errorMessage += "Incorrect Destination Port entry detected.\n\n";
        }
        if(!validateIP(source_ip.getText()) && !source_ip.getText().equals(""))
        {
            badEntry = true;
            errorMessage += "Incorrect Source IP entry detected.\n\n";
        }
        if(!validateIP(destination_ip.getText()) && !destination_ip.getText().equals(""))
        {
            badEntry = true;
            errorMessage += "Incorrect Destination IP entry detected.\n\n";
        }
        if(message.getText().trim().equals(""))
        {
            badEntry = true;
            errorMessage += "Incorrect Message entry detected.\nPlease note that the message field is compulsory for custom rules.\n\n";
        }
             
        if(badEntry)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Rule Addition Failed");
            alert.setContentText(errorMessage);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
        else
        {
            
            String protocol_str = protocol.getSelectionModel().getSelectedItem().toString().toLowerCase();
            String source_ip_str = source_ip.getText();
            String source_port_str = source_port.getText();
            String destination_ip_str = destination_ip.getText();
            String destination_port_str = destination_port.getText();
            String content_str = content.getText();
            String message_str = message.getText();
            
            ArrayList<String> list = new ArrayList<>();
            list.add(protocol_str);
            list.add(source_ip_str);
            list.add(source_port_str);
            list.add(destination_ip_str);
            list.add(destination_port_str);
            list.add(message_str);
            list.add(content_str);
            
            String entry = rulesManagerUI.convertToCustomRule(list);
            rulesManagerUI.writeCustomRule(entry);
            
            
            source_ip.clear();
            source_port.clear();
            destination_ip.clear();
            destination_port.clear();
            content.clear();
            message.clear();
        }
    }
    
    public static boolean isInteger(String str)  
    {  
        try  
        {  
            int d = Integer.parseInt(str);  
        }  
        catch(NumberFormatException e)  
        {  
            return false;  
        }  
        return true;  
    }
    
    public static boolean validateIP(String ip)
    {
        Pattern pattern;
        Matcher matcher;

        String IPADDRESS_PATTERN =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
	return matcher.matches();
    }
    
    
    
    
}
