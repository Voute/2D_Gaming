/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author ytokmakov
 */
public class GameLog {
    
    private static FileHandler fileHandler;
    private static Logger logger;
    private static SimpleFormatter formatter;
    
    public static void write(Exception ex)
    {
        if (logger != null) logger.log(Level.SEVERE, null, ex);
    }
    public static void write(String message)
    {
        if (logger != null) logger.info(message);
    }
    public static void initiate(String dirPath) throws IOException
    {
        dirPath += "GameLog.log";
        logger = Logger.getLogger("2D_GamingLog");
        fileHandler = new FileHandler(dirPath);
        formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
    }
}
