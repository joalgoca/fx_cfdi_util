/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfdi.clases.util;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author jgonzalezc
 */
public class MyLogger {
    private static FileHandler fileTxt;  
    private static SimpleFormatter formatterTxt;  
  
    static public void setUp(String pathfile) {  
        // Create Logger  
        Logger logger = Logger.getLogger("");  
        logger.setLevel(Level.INFO);  
        try {  
            fileTxt = new FileHandler(pathfile,true);  
        } catch (Exception ex) {  
            throw new RuntimeException("Error al inicializar el logger. "+ex.getLocalizedMessage());  
        }   
  
        // Create txt Formatter  
        formatterTxt = new SimpleFormatter();  
        fileTxt.setFormatter(formatterTxt);  
        logger.addHandler(fileTxt);  
  
    }  
    
}
