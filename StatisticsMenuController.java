/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Kavish-PC
 */
public class StatisticsMenuController implements Initializable {

    static FXMLController mainUI;
    static ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    final ObservableList<String> alertData = FXCollections.observableArrayList();
    final ObservableList<PacketStats> statsData = FXCollections.observableArrayList();
    static XYChart.Series<String, Integer> series;
    
    static SimpleStringProperty tcpPercent = new SimpleStringProperty("0.00%");
    static SimpleStringProperty udpPercent = new SimpleStringProperty("0.00%");
    static SimpleStringProperty icmpPercent = new SimpleStringProperty("0.00%");
    static SimpleStringProperty othersPercent = new SimpleStringProperty("0.00%");
    
    
    static int ignoredCounter = 0;
    static int alertCounter = 0;
    
    @FXML
    PieChart pieChart;
    @FXML
    BarChart barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private Label tcpPercentLabel;
    @FXML
    private Label udpPercentLabel;
    @FXML
    private Label icmpPercentLabel;
    @FXML
    private Label othersPercentLabel;
    @FXML
    private Label tcp;
    @FXML
    private Label udp;
    @FXML
    private Label icmp;
    @FXML
    private Label others;
    @FXML
    private TableColumn<PacketStats, String> protocolColumn;
    @FXML
    private TableColumn<PacketStats, Integer> counterColumn;
    @FXML
    private TableView protocolTable;
    @FXML
    private Label ignoredLabel;
    @FXML
    private Label maliciousLabel;
            
    
    DecimalFormat df;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        counterColumn.setCellValueFactory(new PropertyValueFactory<>("counter"));
        
        pieChartData.add(new PieChart.Data("TCP", 0));
        pieChartData.add(new PieChart.Data("UDP", 0));
        pieChartData.add(new PieChart.Data("ICMP", 0));
        pieChartData.add(new PieChart.Data("Others", 0));
        
        pieChart.setData(pieChartData);
        pieChart.setAnimated(false);
        
        statsData.removeAll();
        statsData.add(0, new PacketStats("TCP", 0));
        statsData.add(1, new PacketStats("UDP", 0));
        statsData.add(2, new PacketStats("ICMP", 0));
        statsData.add(3, new PacketStats("Others", 0));
        protocolTable.setItems(statsData);
        protocolTable.setSelectionModel(null);
        protocolColumn.setSortable(false);
        counterColumn.setSortable(false);
        
        String[] months = {"Ignored", "Potentially Malicious"};
        alertData.addAll(months);
        xAxis.setCategories(alertData);
        
        series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Potentially Malicious", 0));
        series.getData().add(new XYChart.Data<>("Ignored", 0));
        barChart.getData().add(series);
        barChart.setAnimated(false);
        
        tcpPercentLabel.textProperty().bind(tcpPercent);
        udpPercentLabel.textProperty().bind(udpPercent);
        icmpPercentLabel.textProperty().bind(icmpPercent);
        othersPercentLabel.textProperty().bind(othersPercent);
        df = new DecimalFormat("00.00"); 
    }    
    
    public void setFXMLController(FXMLController fx)
    {
        mainUI = fx;
    }
    
    @FXML
    public void back()
    {
        mainUI.hideStatisticsMenu();
    }
    
    
    public void updateTCP(int value)
    {
        double newVal = (double)value/(alertCounter + ignoredCounter)*100;
        String new_str = df.format(newVal)+"%";
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tcpPercent.set(new_str);
                    }
                });
        
        pieChartData.get(0).setPieValue(value);
        statsData.remove(0);
        statsData.add(0, new PacketStats("TCP", value));
    }
    
    public void updateUDP(int value)
    {
        double newVal = (double)value/(alertCounter + ignoredCounter)*100;
        String new_str = df.format(newVal)+"%";
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        udpPercent.set(new_str);
                    }
                });
        pieChartData.get(1).setPieValue(value);
        statsData.remove(1);
        statsData.add(1, new PacketStats("UDP", value));
    }
    
    public void updateICMP(int value)
    {
        double newVal = (double)value/(alertCounter + ignoredCounter)*100;
        String new_str = df.format(newVal)+"%";
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        icmpPercent.set(new_str);
                    }
                });
        pieChartData.get(2).setPieValue(value);
        statsData.remove(2);
        statsData.add(2, new PacketStats("ICMP", value));
    }
    
    public void updateOthers(int value)
    {
        double newVal = (double)value/(alertCounter + ignoredCounter)*100;
        String new_str = df.format(newVal)+"%";
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        othersPercent.set(new_str);
                    }
                });
        pieChartData.get(3).setPieValue(value);
        statsData.remove(3);
        statsData.add(3, new PacketStats("Others", value));
    }
    
    
    public void updateTotal()
    {
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        series.getData().add(new XYChart.Data<>("Ignored", ++ignoredCounter));
                        ignoredLabel.setText(Integer.toString(ignoredCounter));
                    }
                });
        
    }
    
    public void updateAlert()
    {
        Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        series.getData().add(new XYChart.Data<>("Potentially Malicious", ++alertCounter));
                        maliciousLabel.setText(Integer.toString(alertCounter));
                    }
                });
        
    }
    
}
