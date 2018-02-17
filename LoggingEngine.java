/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Kavish-PC
 */
public class LoggingEngine {
    
    public static String logFileLocation = "C:\\Users\\Kavish\\Dropbox\\"
            + "JAN2017 - FYP2\\FYP2\\_PROJECT WORKS\\_Final_Files\\logs";
    public static String logFile = "";
    public static boolean loggingMode = true;
    
    public LoggingEngine()
    {
        createLog();
    }
    
    public boolean getMode()
    {
        return loggingMode;
    }
    
    public void changeMode()
    {
        loggingMode = !loggingMode;
    }
    
    public void createLog()
    {
        try
        {
            System.out.println(logFileLocation+"\\LOG"+getDate()+".txt");
            logFile = logFileLocation+"\\LOG"+getDate()+".txt";
            File file = new File(logFile);
            file.createNewFile();
            System.out.println("File created.");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public void log(String entry)
    {
        if(loggingMode)
        {
            try
            {
                FileWriter fw = new FileWriter(logFile,true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("___________________________________________________________________\r\n");
                bw.write(entry+"\r\n");
                bw.write("___________________________________________________________________\r\n");
                bw.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public String getLocation()
    {
        return logFileLocation;
    }
    
    public String getFileLocation()
    {
        return logFile;
    }
    
}
