/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Kavish-PC
 */
public class FXMLController implements Initializable
{
    ThreadHandler THREAD;
    static Operations operations = new Operations();
    
    static InterfaceMenuController interfaceMenu = new InterfaceMenuController();
    static RulesManagerMenuController rulesManagerMenu = new RulesManagerMenuController();
    static StatisticsMenuController statisticsMenu;
    static LoggingEngine loggingEngine = new LoggingEngine();
    static LogManagerMenuController logManagerMenu;
    
    static int selected = 0;
    static boolean running = false;
    static boolean simpleMode = false;
    int packetNum = 1;
    int alertNum = 1;
    
    final ObservableList<Packet> packetData = FXCollections.observableArrayList();
    final ObservableList<IntrusionAlert> alertData = FXCollections.observableArrayList();
    
    static Stage interfaceMenuStage = new Stage();
    static Stage rulesManagerMenuStage = new Stage();
    static Stage statisticsMenuStage = new Stage();
    static Stage logManagerMenuStage = new Stage();
    
    static TranslateTransition openNav, closeNav;
    
    @FXML 
    private Button interfaceButton;
    
    @FXML 
    private Button exitButton;
    
    @FXML
    private Button stopButton;
    
    @FXML
    private Button memoryButton;
    
    @FXML
    private AnchorPane anchor;
    
    @FXML
    private Label statusLabel;
    @FXML
    private Label modeLabel;
    @FXML
    private TextField filterField;

    @FXML
    private TableView<Packet> packetTable;    
    
    @FXML
    private TableColumn<Packet, Integer> number;
    @FXML
    private TableColumn<Packet, String> protocol;
    @FXML
    private TableColumn<Packet, String> source_ip;
    @FXML
    private TableColumn<Packet, Integer> source_port;
    @FXML
    private TableColumn<Packet, String> destination_ip;
    @FXML
    private TableColumn<Packet, Integer> destination_port;
    
    @FXML
    private TableView alertTable;
    @FXML
    private TableColumn<IntrusionAlert, String> alertTime;
    @FXML
    private TableColumn<IntrusionAlert, String> alertDetails;
    @FXML
    private TableColumn<IntrusionAlert, Integer> packetReference;
    @FXML
    private AnchorPane navList;
    @FXML
    private Button menu;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("Main UI Initialized!");
        //init packets table
        number.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));
        protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        source_ip.setCellValueFactory(new PropertyValueFactory<>("source_ip"));
        source_port.setCellValueFactory(new PropertyValueFactory<>("source_port"));
        destination_ip.setCellValueFactory(new PropertyValueFactory<>("destination_ip"));
        destination_port.setCellValueFactory(new PropertyValueFactory<>("destination_port"));
        packetTable.setItems(packetData);
        
        
        //init alerts table
        alertDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        alertTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        packetReference.setCellValueFactory(new PropertyValueFactory<>("packetReference"));
        alertTable.setItems(alertData);
        
        alertTable.setRowFactory( tv -> {
            TableRow<IntrusionAlert> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) 
                {
                    IntrusionAlert rowData = row.getItem();
                    int ref = rowData.getPacketReference();
                    int focus = 0;
                    for(int i = 0; i<packetData.size(); i++)
                    {
                        if(packetData.get(i).getReferenceNumber() == ref)
                        {
                            focus = i;
                        }
                    }
                    
                    packetTable.requestFocus();
                    packetTable.getSelectionModel().select(focus);
                    packetTable.getFocusModel().focus(focus);
                    packetTable.scrollTo(focus);
                }
            });
            return row;
        });
        
        Parent interfaceMenuRoot, rulesManagerMenuRoot, statisticsMenuRoot, logManagerMenuRoot;
        try 
        {
            interfaceMenuStage.setTitle("Interface Selection");
            rulesManagerMenuStage.setTitle("Intrusion Detection Rules Manager");
            statisticsMenuStage.setTitle("IDS Statistics");
            logManagerMenuStage.setTitle("IDS Log Manager");
            interfaceMenuRoot = FXMLLoader.load(getClass().getResource("InterfaceMenu.fxml"));
            rulesManagerMenuRoot = FXMLLoader.load(getClass().getResource("RulesManagerMenu.fxml"));
            
            FXMLLoader statisticsLoader = new FXMLLoader();
            statisticsLoader.setLocation(FXMLController.class.getResource("StatisticsMenu.fxml"));
            statisticsMenuRoot = statisticsLoader.load();
            statisticsMenu = statisticsLoader.getController();
            
            FXMLLoader logManagerLoader = new FXMLLoader();
            logManagerLoader.setLocation(FXMLController.class.getResource("LogManagerMenu.fxml"));
            logManagerMenuRoot = logManagerLoader.load();
            logManagerMenu = logManagerLoader.getController();
            
            Scene interfaceMenuScene = new Scene(interfaceMenuRoot);
            Scene rulesManagerMenuScene = new Scene(rulesManagerMenuRoot);
            Scene statisticsMenuScene = new Scene(statisticsMenuRoot);
            Scene logManagerMenuScene = new Scene(logManagerMenuRoot);
            interfaceMenuStage.setScene(interfaceMenuScene);
            rulesManagerMenuStage.setScene(rulesManagerMenuScene);
            statisticsMenuStage.setScene(statisticsMenuScene);
            logManagerMenuStage.setScene(logManagerMenuScene);
            interfaceMenuStage.initModality(Modality.APPLICATION_MODAL);
            rulesManagerMenuStage.initModality(Modality.APPLICATION_MODAL);
            statisticsMenuStage.initModality(Modality.APPLICATION_MODAL);
            logManagerMenuStage.initModality(Modality.APPLICATION_MODAL);
            
            interfaceMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            rulesManagerMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            statisticsMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            logManagerMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            
            //interfaceMenuStage.setResizable(false);
            //rulesManagerMenuStage.setResizable(false);
            //statisticsMenuStage.setResizable(false);
            //logManagerMenuStage.setResizable(false);
            
            logManagerMenuStage.sizeToScene();
            statisticsMenuStage.sizeToScene();
            rulesManagerMenuStage.sizeToScene();
            interfaceMenuStage.sizeToScene();
            
            operations.setStatistics(statisticsMenu);
            operations.setLoggingEngine(loggingEngine);
            statusLabel.setTextFill(Color.web("#f2f2f2"));
            
            logManagerMenu.setLogEngine(loggingEngine);
            logManagerMenu.updateStatus();
            
            
        }
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
        
        
    }
    
    public StatisticsMenuController getStatisticsMenu()
    {
        return statisticsMenu;
    }
    
    public LoggingEngine getLoggingEngine()
    {
        return loggingEngine;
    }
    
    public void exit()
    {
        System.exit(0);
    }
    
    public void addEntry(int num, String protocol, String source_ip, int source_port, String destination_ip, int destination_port)
    {
        packetData.add(new Packet(num, protocol, source_ip, source_port, destination_ip, destination_port));
    }
    
    public void addAlert(String alert, String time, int ref)
    {
        alertData.add(new IntrusionAlert(alert, time, ref));
    }
    
    public void capture()
    {
        operations.getDetectionEngine().updateRulesFile(rulesManagerMenu.getRulesFileLocation()); //update location of rules file
        THREAD = new ThreadHandler()
        {
                public Object construct()
                {
                    startCapture();
                    return 0;
                }
                public void finished()
                {
                    this.interrupt();
                }
        };
        THREAD.start();
    }
    
    public void receiveSelected(int num)
    {
        selected = num; 
        capture();
    }
    
    public void startCapture()
    {
        changeStatus();
        operations.capturePackets(selected, this);
    }
    
    @FXML 
    public void showInterfaceMenu()
    {
        if(!running)
        {
            showSideMenu();
            interfaceMenu.setFXMLController(this);
            interfaceMenuStage.showAndWait();
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Capture Process Manager");
            alert.setHeaderText("The capturing process could not be started!");
            alert.setContentText("The current capturing process needs to be stopped before starting a new one.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
    }
    
    @FXML 
    public void showRulesManagerMenu()
    {
        showSideMenu();
        rulesManagerMenu.setFXMLController(this);
        rulesManagerMenuStage.showAndWait();
    }
    
    @FXML 
    public void showLogManagerMenu()
    {
        showSideMenu();
        logManagerMenu.setFXMLController(this);
        logManagerMenuStage.showAndWait();
    }
    
    @FXML 
    public void showStatisticsMenu()
    {
        showSideMenu();
        statisticsMenu.setFXMLController(this);
        statisticsMenuStage.showAndWait();
    }
    
    @FXML
    public void hideStatisticsMenu()
    {
        statisticsMenuStage.hide();
    }
    
    public void hideInterfaceMenu()
    {
        interfaceMenuStage.hide();
    }
    
    public void hideRulesManagerMenu()
    {
        rulesManagerMenuStage.hide();
    }
    
    public void hideLogManagerMenu()
    {
        logManagerMenuStage.hide();
    }
    
    @FXML
    public void stopCapture()
    {
        if(running)
        {
            operations.pcap.breakloop();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Packet Capturing has been stopped.");
            changeStatus();
            
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Packet Capturing has not started.");
            alert.setContentText("Select a network interface to initiate the packet capturing process.");
            
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            
            alert.showAndWait();
        }
    }
    
    public void changeStatus()
    {
        if(running)
        {
            Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        running = false;
                        statusLabel.setText("STOPPED");
                        statusLabel.setTextFill(Color.web("#f2f2f2"));
                    }
                });
        }
        else
        {
            Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        running = true;
                        statusLabel.setText("RUNNING");
                        statusLabel.setTextFill(Color.web("#1aff1a"));
                    }
                });
        }
    }
    
    public boolean getStatus()
    {
        return running;
    }
    
    @FXML
    public void checkMemory()
    {
        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory(); 
        System.out.println("Current: " +heapSize);
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        System.out.println("Max:    " + heapMaxSize);
    }
    
    public void setMode(boolean m)
    {
        simpleMode = m;
        operations.setMode(m);
        
        if(m)
        {
            Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        modeLabel.setText("HIGH EFFICIENCY");
                    }
                });
        }
        else
        {
            Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        modeLabel.setText("NORMAL");
                    }
                });
        }
    }
    
    @FXML
    private void showSideMenu() 
    {
        TranslateTransition moveUI = new TranslateTransition(new Duration(350), anchor);
        moveUI.setToX(120);
        TranslateTransition revertUI = new TranslateTransition(new Duration(350), anchor);
        revertUI.setToX(0);
        
        if(anchor.getTranslateX()==0)
        {
            //openNav.play();
            moveUI.play();
        }
        else
        {
            //closeNav.play();
            revertUI.play();
        }
    }
    
    
}
