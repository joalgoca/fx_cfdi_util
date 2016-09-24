/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfdi.clases.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jgonzalezc
 */
public class Utilidades {
    
    public Utilidades(){
        
    }
    public  String completarIzquierda(String str, int size,String caracter){
        String temp="";
        for (int i=(size-str.length());i>0;--i )
            temp=temp+caracter;
        return temp+str;
    }
    public String completarDerecha(String str, int size,String caracter){
        String temp="";
        for (int i=(size-str.length());i>0;--i )
            temp=temp+caracter;
        return str+temp;
    }
    
    public String replaceExcelNumerico(String str){        
        return str.replaceAll("\\.", "").replaceAll("\\$", "").replace(",", ".");
    }
    
    public void copyFile_Java7(String origen, String destino,String nombreArchivo) throws IOException {
        Path FROM = Paths.get(origen+nombreArchivo);
        File folder = new File(destino);
        if(!folder.exists())
            folder.mkdirs();
        Path TO = Paths.get(destino+nombreArchivo);
        //sobreescribir el fichero de destino, si existe, y copiar
        // los atributos, incluyendo los permisos rwx
        CopyOption[] options = new CopyOption[]{
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.COPY_ATTRIBUTES
        }; 
        Files.copy(FROM, TO, options);
    }
    
    public  boolean deleteWithChildren(String path) {  
        File file = new File(path);  
        if (!file.exists()) {  
            return true;  
        }  
        if (!file.isDirectory()) {  
            return file.delete();  
        }  
        return this.deleteChildren(file) && file.delete();  
    }  
  
    private  boolean deleteChildren(File dir) {  
        File[] children = dir.listFiles();  
        boolean childrenDeleted = true;  
        for (int i = 0; children != null && i < children.length; i++) {  
            File child = children[i];  
            if (child.isDirectory()) {  
                childrenDeleted = this.deleteChildren(child) && childrenDeleted;  
            }  
            if (child.exists()) {  
                childrenDeleted = child.delete() && childrenDeleted;  
            }  
        }  
        return childrenDeleted;  
    }  
    
    public File[] dirListByAscendingDate(File folder) {
        if (!folder.isDirectory()) {
          return null;
        }
        File files[] = folder.listFiles();
        Arrays.sort( files, new Comparator()
        {
          public int compare(final Object o1, final Object o2) {
            return new Long(((File)o1).lastModified()).compareTo
                 (new Long(((File) o2).lastModified()));
          }
        }); 
        return files;
      } 
    
        public  Node findNode(Node root,String elementName,boolean deep,boolean elementsOnly) {
          //Check to see if root has any children if not return null
          if (!(root.hasChildNodes()))
            return null;

          //Root has children, so continue searching for them
          Node matchingNode = null;
          String nodeName = null;
          Node child = null;

          NodeList childNodes = root.getChildNodes();
          int noChildren = childNodes.getLength();
          for (int i = 0; i < noChildren; i++) {
            if (matchingNode == null) {
              child = childNodes.item(i);
              nodeName = child.getNodeName();
              if ((nodeName != null) & (nodeName.equals(elementName)))
                return child;
              if (deep)
                matchingNode =
                  findNode(child, elementName, deep, elementsOnly);
            } else
              break;
          }

          if (!elementsOnly) {
            NamedNodeMap childAttrs = root.getAttributes();
            noChildren = childAttrs.getLength();
            for (int i = 0; i < noChildren; i++) {
              if (matchingNode == null) {
                child = childAttrs.item(i);
                nodeName = child.getNodeName();
                if ((nodeName != null) & (nodeName.equals(elementName)))
                  return child;
              } else
                break;
            }
          }
          return matchingNode;
        }
    public boolean descargarArchivoUrl(String urlString,String rutaynombre){
        try {
            // Url con la foto
            URL url = new URL(urlString);
            
            // establecemos conexion
            URLConnection urlCon = url.openConnection();

            // Sacamos por pantalla el tipo de fichero
            //System.out.println(urlCon.getContentType());

            // Se obtiene el inputStream de la foto web y se abre el fichero
            // local.
            InputStream is = urlCon.getInputStream();
            FileOutputStream fos = new FileOutputStream(rutaynombre);

            // Lectura de la foto de la web y escritura en fichero local
            byte[] array = new byte[1000]; // buffer temporal de lectura.
            int leido = is.read(array);
            while (leido > 0) {
                fos.write(array, 0, leido);
                leido = is.read(array);
            }

            // cierre de conexion y fichero.
            is.close();
            fos.close();
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilidades.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String getSerialNumber(String drive) {
        String result = "";
          try {
            File file = File.createTempFile("realhowto",".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                        +"Set colDrives = objFSO.Drives\n"
                        +"Set objDrive = colDrives.item(\"" + drive + "\")\n"
                        +"Wscript.Echo objDrive.SerialNumber";  // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input =
              new BufferedReader
                (new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
               result += line;
            }
            input.close();
          }
          catch(Exception e){
              e.printStackTrace();
          }
          return result.trim();
    }
    
    public String encrypt(String cadena,String cadenaEncriptacion) { 
        StandardPBEStringEncryptor s = new StandardPBEStringEncryptor(); 
        s.setPassword(cadenaEncriptacion); 
        return s.encrypt(cadena); 
    } 
    
    public String decrypt(String cadena,String cadenaEncriptacion) { 
        StandardPBEStringEncryptor s = new StandardPBEStringEncryptor(); 
        s.setPassword(cadenaEncriptacion); 
        String devuelve = ""; 
        try { 
            devuelve = s.decrypt(cadena); 
        } catch (Exception e) { 
            System.out.print(e.getMessage());
        } 
        return devuelve; 
    }
}
