/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kavish-PC
 */
public class InterfaceMenuController implements Initializable{
    
    @FXML
    private ComboBox interfaceBox;
    
    @FXML
    private ToggleButton toggle;
    @FXML 
    private Pane pane;
    @FXML 
    private Button backButton;
    static FXMLController mainUI;

    private static Stage window = new Stage();
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        ObservableList<String> interfaces = FXCollections.observableArrayList();
        List<String> list = Operations.findInterfaceName(Operations.findInterfaces());
        for(int i = 0; i < list.size(); i++)
        {
            interfaces.add(list.get(i));
        }
        interfaceBox.setItems(interfaces);
        
    }
    
    @FXML
    public void back()
    {
        mainUI.hideInterfaceMenu();
    }
    
    @FXML
    public void getSelected()
    {
        int num = interfaceBox.getSelectionModel().getSelectedIndex();
        System.out.println("Selected: "+num);
        if(num != -1)
        {
            back();
            mainUI.receiveSelected(num);
        }
    }
    
    public void setFXMLController(FXMLController fx)
    {
        mainUI = fx;
    }
    
    @FXML
    public void processSimpleMode()
    {
        System.out.println(mainUI.getStatus());
        if(mainUI.getStatus())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("High Efficiency Mode");
            alert.setHeaderText("Unable to change mode while capturing process is ongoing!");
            alert.setContentText("Please stop the capturing process before changing the mode of the application.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
        else
        {
            if(toggle.isSelected())
            {
                mainUI.setMode(true);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("High Efficiency Mode");
                alert.setHeaderText("High Efficiency Mode Activated!");
                alert.setContentText("This mode prevents the processing and display of packets not detected as potentially malicious.\nPlease note that enabling this mode will have the following effects: "
                        + "\n1. The table with the details of captured packets will not be updated."
                        + "\n2. Only packets identified to be potentially malicious will be displayed."
                        + "\n3. The statistical table and charts related to the packets captured will not be updated."
                        + "\n\nFor increased performance, the logging feature may be disabled in the Log Manager.");
                toggle.textProperty().set("Press To Deactivate");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
                alert.showAndWait();
            }
            else
            {
                mainUI.setMode(false);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("High Efficiency Mode");
                alert.setHeaderText("High Efficiency Mode Deactivated!");
                toggle.textProperty().set("Press To Activate");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
                alert.showAndWait();
            }
        }
        
    }
}
