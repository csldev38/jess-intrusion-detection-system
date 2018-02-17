/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;



public class UpdateRulesMenuController implements Initializable {

    static RulesManagerMenuController rulesManager;
    static String selectedDirectory;
    static boolean selected = false;
    
    @FXML
    private TextArea directoryTextArea;
    
    @FXML
    private TextArea statusTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
        directoryTextArea.setWrapText(true);
    }    
    
    @FXML
    public void selectDir() 
    {
        DirectoryChooser directoryChooser = new DirectoryChooser(); 

                  directoryChooser.setTitle("Directory Selector");

                  //Show open file dialog

                  File file = directoryChooser.showDialog(null);
                  if(file!=null)
                  {
                      selectedDirectory = file.getPath();
                      directoryTextArea.setText(selectedDirectory);
                      rulesManager.changeDirectory(selectedDirectory);
                      selected = true;
                      updateStatus("<Directory Selection was successful>\n<Press Process Rules to continue>\n");
                 }
    }
    
    public void setController(RulesManagerMenuController fx)
    {
        rulesManager = fx;
    }
    
    @FXML
    public void back()
    {
        rulesManager.hideUpdateRulesMenu();
        directoryTextArea.clear();
        statusTextArea.clear();
    }
    
    @FXML
    public void updateRules()
    {
        if(selected)
            {
            try
            {
                rulesManager.jessConverter(this);
                selected = false;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("The directory of the rules needs to be selected first.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
    }
    
    public void updateStatus(String str)
    {
        statusTextArea.appendText(str);
    }
}
