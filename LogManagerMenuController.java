/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kavish-PC
 */
public class LogManagerMenuController implements Initializable {

    static FXMLController mainUI;
    static LoggingEngine logEngine;
    
    @FXML
    private TextArea logFileLocation;
    @FXML
    private Label logStatus;
    @FXML
    private Button changeModeButton;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void updateStatus()
    {
        if(logEngine.getMode())
        {
            logStatus.setText("ON");
        }
        else
            logStatus.setText("OFF");
    }
            
    public void setFXMLController(FXMLController fx)
    {
        mainUI = fx;
    }
    
    @FXML
    public void back()
    {
        mainUI.hideLogManagerMenu();
    }
    
    public void setLogEngine(LoggingEngine l)
    {
        logEngine = l;
        setTextArea();
    }
    
    public void setTextArea()
    {
        logFileLocation.setText(logEngine.getLocation());
    }
    
    @FXML
    public void openFolder() 
    {
        try
        {
            Runtime.getRuntime().exec("Explorer.exe \""+ logEngine.getLocation() +"\"");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void viewLog()
    {
         if (Desktop.isDesktopSupported())
        {
            try
            {
                File rules = new File(logEngine.getFileLocation());
                Desktop.getDesktop().open(rules);
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Awt Desktop is not supported!");
        }
    }
    
    @FXML
    public void changeMode()
    {
        logEngine.changeMode();
        if(logEngine.getMode())
        {
            changeModeButton.setText("TURN OFF");
            updateStatus();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Log Manager Alert");
            alert.setHeaderText("Logging has been enabled!");
            alert.setContentText("Intrusion attempts detected will be logged to the current log file.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
        else
        {
            changeModeButton.setText("TURN ON");
            updateStatus();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Log Manager Alert");
            alert.setHeaderText("Logging has been disabled!");
            alert.setContentText("Intrusion attempts will not be logged from this point onwards until Logging is turned on again.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
    }
}
