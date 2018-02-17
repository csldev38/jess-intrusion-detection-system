/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kavish-PC
 */
public class RulesManagerMenuController implements Initializable {

    public static int ruleCounter;
    public static int customCounter = 0;
    
    static FXMLController mainUI;
    public static AddRulesMenuController addRulesMenu = new AddRulesMenuController();
    public static UpdateRulesMenuController updateRulesMenu = new UpdateRulesMenuController();
    
    static Stage addRulesMenuStage = new Stage();
    static Stage updateRulesMenuStage = new Stage();
    
    static int cnt = 0,counter=0;
    static File singlefile ;
    static String first_part="",second_part="", str_protocol,src_port,dst_port,str_content,s_id,alert_msg = null;
    static String strLine;
    
    public static String directory = "C:\\Users\\Kavish\\Dropbox\\JAN2017 - FYP2\\FYP2\\_PROJECT WORKS\\Snort Rules\\rules";
    public static String rulesFile = "C:\\Users\\Kavish\\Dropbox\\JAN2017 - FYP2\\FYP2\\_PROJECT WORKS\\_Final_Files\\test1.clp";
    
    @FXML
    Button addRule;
    
    @FXML
    TextArea rulesFileTextArea;
    
    @FXML
    TextArea customCounterTextArea;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        addRulesMenuStage.setTitle("Rule Addition");
        updateRulesMenuStage.setTitle("Update/Reset Rules");
        
        Parent addRulesRoot, updateRulesRoot;
        try 
        {
            addRulesRoot = FXMLLoader.load(getClass().getResource("AddRulesMenu.fxml"));
            updateRulesRoot = FXMLLoader.load(getClass().getResource("UpdateRulesMenu.fxml"));
            Scene addRulesScene = new Scene(addRulesRoot);
            Scene updateRulesScene = new Scene(updateRulesRoot);
            addRulesMenuStage.setScene(addRulesScene);
            updateRulesMenuStage.setScene(updateRulesScene);
            addRulesMenuStage.initModality(Modality.APPLICATION_MODAL);
            updateRulesMenuStage.initModality(Modality.APPLICATION_MODAL);
            addRulesMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            updateRulesMenuStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            rulesFileTextArea.setWrapText(true);
            rulesFileTextArea.setText(rulesFile);
            
            updateCustomRulesCounterTextArea();
        }
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
    }   
    
    @FXML
    public void display()
    {
        addRulesMenuStage.showAndWait();
    }
    
    public void hideAddRulesMenu()
    {
        addRulesMenuStage.hide();
    }
    
    public void hideUpdateRulesMenu()
    {
        updateRulesMenuStage.hide();
        updateCustomRulesCounterTextArea();
    }
    
    @FXML
    public static Integer[] jessConverter(UpdateRulesMenuController fx) throws IOException
    {
        boolean proceed = true;
        
        File dir = new File(directory);
        FileWriter infstream = new FileWriter(rulesFile, true);
        BufferedWriter out = new BufferedWriter(infstream);
        if(dir.isDirectory())
        {
            File [] rulesfiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".rules"); } });
            int nfiles = rulesfiles.length;
            if(nfiles == 0)
            {
                fx.updateStatus("<Rules file(s) were not found in this directory.>\n<Please revise your selection to continue.>\n");
                proceed = false;
            }
            else
            {
                PrintWriter writer = new PrintWriter(rulesFile);
                writer.close();
                writeJESSInitialData();
            }
            int i =0;
            while(i<nfiles)
            {
                singlefile = new File(""+rulesfiles[i]);
                FileInputStream fstream = new FileInputStream(singlefile);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                strLine = "";
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) 
                {
                    if(!strLine.startsWith("#") && strLine.startsWith("alert"))
                    {
                        try
                        {
                            out.newLine();
                            String rule_data =ConvertIntoJESSRule(separateRules(strLine));
                            out.write(rule_data);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        } 
                    } 
                }
                fx.updateStatus("Name of file => "+singlefile.getName()+"\n");
                //Close the input stream
                in.close();
                i++; 
            } 
        }
        out.write("\r");
        out.close();
        if(proceed)
            fx.updateStatus("***\tAll Rules Converted!\t***\n");
        
        return null; 
    }
    
    private static ArrayList<String> separateRules(String strLine) 
    {
        ArrayList<String> data_for_jess = new ArrayList<String>();
        ArrayList<String> LHS_arra = new ArrayList<String>();
        ArrayList<String> RHS_arra = new ArrayList<String>();
        ArrayList<String> sid_arra = new ArrayList<String>();
        /*
        * The single rule devide into two parts.
        */
        StringTokenizer tokens = new StringTokenizer(strLine,"()");
        int npart = 1,i=0;
        while(tokens.hasMoreTokens())
        {
            if(npart==1)
            {
                first_part = tokens.nextToken();
                try
                {
                    String[] LHSparts = first_part.split(" ");
                    for (int j = 0; j<LHSparts.length; j++)
                    {
                        LHS_arra.add(LHSparts[j]); 
                        //System.out.println(LHSparts[j]);
                    }
                   // System.out.println("Array =>>>"+LHS_arra.get(1));
                   
                    if(LHS_arra.get(1).equals("ip"))
                    {
                        data_for_jess.add("");
                    }
                    else
                    {
                        data_for_jess.add(LHS_arra.get(1)); //protocol
                    }
                    
                    
                    if(LHS_arra.get(2).equals("$EXTERNAL_NET") || LHS_arra.get(2).equals("$HTTP_SERVERS") || 
                            LHS_arra.get(2).equals("$HOME_NET") || LHS_arra.get(2).equals("$TELNET_SERVERS") || LHS_arra.get(2).equals("any"))
                    {
                        data_for_jess.add(""); //source ip blank
                    }
                    else
                    {
                        data_for_jess.add(LHS_arra.get(2)); //source ip
                    }
                    
                    if(LHS_arra.get(3).equals("any") || LHS_arra.get(3).equals("$HTTP_PORTS") || LHS_arra.get(3).equals("$SMTP_PORTS") || LHS_arra.get(3).equals("$SHELLCODE_PORTS"))
                    {
                        data_for_jess.add(""); //source port
                    }
                    else
                    {
                        data_for_jess.add(LHS_arra.get(3)); //source port
                    }
                    
                    
                    if(LHS_arra.get(2).equals("$EXTERNAL_NET") || LHS_arra.get(2).equals("$HTTP_SERVERS") || 
                            LHS_arra.get(2).equals("$HOME_NET") || LHS_arra.get(2).equals("$TELNET_SERVERS") || LHS_arra.get(2).equals("any"))
                    {
                        data_for_jess.add(""); //source ip blank
                    }
                    else
                    {
                        data_for_jess.add(LHS_arra.get(5)); //source ip
                    }
                    
                    if(LHS_arra.get(6).equals("any") || LHS_arra.get(6).equals("$HTTP_PORTS") || LHS_arra.get(6).equals("$SMTP_PORTS") || LHS_arra.get(6).equals("$SHELLCODE_PORTS"))
                    {
                        data_for_jess.add(""); //destination port
                    }
                    else
                    {
                        data_for_jess.add(LHS_arra.get(6)); //destination port
                    }
                    
                    LHSparts = null;
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                    System.out.println("npart==1"); 
                }
                npart=2;
            }
            else
            {
                second_part = tokens.nextToken();
                try
                {
                    String[] RHSparts = second_part.split("; ");
                    boolean msg_found = false;
                    boolean content_found = false;
                    for (int j = 0; j<RHSparts.length; j++)
                    {
                        //System.out.println(RHSparts[j]); 
                        //System.out.println("Part " + j + ": " + RHSparts[j]);
                        String temp = RHSparts[j];
                        if(temp.contains("msg:"))
                        {
                            temp = temp.replace("msg:", "");
                            temp = temp.replace("\"", "");
                            //System.out.println(temp);
                            data_for_jess.add(temp); //msg
                            msg_found = true;
                        }
                        if(temp.contains("content:"))
                        {
                            //System.out.println(temp);
                            temp = temp.replace("content:", "");
                            temp = temp.replace("\"", "");
                            //System.out.println(temp);
                            data_for_jess.add(temp); //content
                            content_found = true;
                        }
                        if(msg_found && content_found)
                            break;
                        if(j == RHSparts.length - 1)
                        {
                            data_for_jess.add(""); //content
                            break;
                        }
                    }
                    
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();

                    System.out.println("npart==2"); 
                }
                npart=1; 
            } 
        }
        //printArray(data_for_jess);
        return data_for_jess; 
    }
    
    private static String ConvertIntoJESSRule(ArrayList<String> ruleComponents) 
    {
        String j_rule = "";
        char quote ='"';
        if(checkIfEmpty(ruleComponents) == false)
        {
            counter++;
            try
            {   
                j_rule="(defrule Rule"+counter+ " " + quote+singlefile.getName().toString()
                        +quote+" (packet (protocol "+ruleComponents.get(0)+")"
                        + " (source_ip "+ruleComponents.get(1)+") "
                        + "(source_port "+ruleComponents.get(2)+") " 
                + "(destination_ip "+ruleComponents.get(3)+") "
                + "(destination_port "+ruleComponents.get(4)+") "
                + "(content ?c&:(?c contains "+quote+ruleComponents.get(6)+quote
                + "))) => (store output " + quote + ruleComponents.get(5) + quote +"))"; 
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return j_rule; 
    } 
    
    public static String convertToCustomRule(ArrayList<String> ruleComponents) 
    {
        customCounter = getCustomRulesCounter();
        String j_rule = "";
        char quote ='"';
        try
        {   
            j_rule="(defrule CustomRule"+customCounter+ " " + quote + "customRules"
                    +quote+" (packet (protocol "+ruleComponents.get(0)+")"
                    + " (source_ip "+ruleComponents.get(1)+") "
                    + "(source_port "+ruleComponents.get(2)+") " 
            + "(destination_ip "+ruleComponents.get(3)+") "
            + "(destination_port "+ruleComponents.get(4)+") "
            + "(content ?c&:(?c contains "+quote+ruleComponents.get(6)+quote
            + "))) => (store output " + quote + ruleComponents.get(5) + quote +"))"; 
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return j_rule; 
    } 
    
    public static void printArray(ArrayList<String> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            System.out.println("Item #" + i + " :" + list.get(i));
        }
    }
    
    public static boolean checkIfEmpty(ArrayList<String> list)
    {
        boolean empty = true;
        
        for(int i = 1; i < 5; i++)
        {
            if(list.get(i).equals("") == false)
            {
                empty = false;
            }
        }
        if(list.get(6).equals("") == false)
        {
            empty = false;
        }
        return empty;
    }
    
    @FXML
    public void showAddRulesMenu()
    {
        addRulesMenu.setController(this);
        addRulesMenuStage.showAndWait();
    }
    
    @FXML
    public void showUpdateRulesMenu()
    {
        updateRulesMenu.setController(this);
        updateRulesMenuStage.showAndWait();
    }
    
    public void setFXMLController(FXMLController fx)
    {
        mainUI = fx;
    }
    
    @FXML
    public void back()
    {
        mainUI.hideRulesManagerMenu();
        updateCustomRulesCounterTextArea();
    }
    
    @FXML
    public void viewRules()
    {
         if (Desktop.isDesktopSupported())
        {
            try
            {
                File rules = new File(rulesFile);
                Desktop.getDesktop().open(rules);
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }
        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Rules Manager Dialog");
            alert.setHeaderText("Operation failed!");
            alert.setContentText("There was an error opening the file. Please make sure "
                    + "your PC has a default text viewer.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();
        }
    }
    
    public static void writeJESSInitialData()
    {
        String data = "#IDS custom_rules_counter 0\n" +
                        "(deftemplate packet\n" +
                        "\"Generic Network Packet\"\n" +
                        "(slot protocol)\n" +
                        "(slot source_ip) \n" +
                        "(slot source_port)\n" +
                        "(slot destination_ip)\n" +
                        "(slot destination_port)\n" +
                        "(slot content)\n" +
                        ")";
        
        try
        {
            File file =new File(rulesFile);

            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data+"\r\n");
            bw.close();

        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    public void writeCustomRule(String rule)
    {
        try
        {
            File file =new File(rulesFile);
            if(!file.exists()){
               file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(rule+"\r\n");
            bw.close();
            
            //RandomAccessFile
            updateCustomRulesCounter();
            updateCustomRulesCounterTextArea();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Rules Manager Dialog");
            alert.setHeaderText("Rule Addition Successful");
            alert.setContentText("Please note that the application needs to be restarted for the new rules to be in effect.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("logo.png").toString()));
            alert.showAndWait();

        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
            
    }
    
    public void changeDirectory(String d)
    {
        directory = d;
    }
    
    public static int getCustomRulesCounter()
    {
        int n = 0;
        try
        {
            BufferedReader brTest = new BufferedReader(new FileReader(rulesFile));
            String text = brTest.readLine();
            String[] new_text = text.split(" ");
            n = Integer.parseInt(new_text[2]);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return n;
    }
    
    @FXML
    public void updateCustomRulesCounter()
    {
        String new_counter = "#IDS custom_rules_counter " + ++customCounter;
        try
        {
            RandomAccessFile file = new RandomAccessFile(rulesFile, "rw");
            file.seek(0);
            file.write(new_counter.getBytes());
            file.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public String getRulesFileLocation()
    {
        return rulesFile;
    }
    
    public void updateCustomRulesCounterTextArea()
    {
        customCounter = getCustomRulesCounter();
        customCounterTextArea.setText(Integer.toString(customCounter));
    }
    
    
}
