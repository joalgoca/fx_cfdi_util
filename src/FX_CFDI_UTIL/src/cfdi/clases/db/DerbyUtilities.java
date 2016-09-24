/*
 * Copyright (C) 2016 jgonzalezc
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cfdi.clases.db;

import cfdi.clases.layout.EstructuraLayout;
import cfdi.clases.util.MyLogger;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author José González Caballero (joalgoca)
 */
public class DerbyUtilities {
    private final static Logger logger = Logger.getLogger(DerbyUtilities.class.getName());
    Properties propiedades = new Properties();       
    Properties comandosDerby = new Properties();
    public DerbyUtilities() throws IOException {
        propiedades.load(getClass().getResourceAsStream("/cfdi/configuration.properties"));
        comandosDerby.load(getClass().getResourceAsStream("dbComandos.properties"));
        MyLogger.setUp("logs/logDerby.txt");
    }     
    
    /**
     * Inicializa las tablas de la base de datos
     * 
     * @param operacion accion a realizar (CREATE,DROP,TRUNCATE)
     */
    public void  inicializacionBaseDatos(String operacion){
        BoneCP connectionPool = null;
        Connection connection = null;
        try {
            Class.forName(propiedades.getProperty("DB_DRIVER"));
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(propiedades.getProperty("DB_SERVER")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(propiedades.getProperty("DB_USER")); 
            config.setPassword(propiedades.getProperty("DB_PASSWORD"));
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
            connection = connectionPool.getConnection(); // fetch a connection
            if (connection != null){  
                if(operacion.equals("CREATE")){
                    connection.createStatement().execute(comandosDerby.getProperty("CREATE_CFDI"));
                    connection.createStatement().execute(comandosDerby.getProperty("CREATE_CFDI_DETALLE"));
                    connection.createStatement().execute(comandosDerby.getProperty("CREATE_CONFIGURACION"));
                    inicializarConfiguracion();
                }else if(operacion.equals("DROP")){
                    connection.createStatement().execute(comandosDerby.getProperty("DROP_CFDI_DETALLE"));
                    connection.createStatement().execute(comandosDerby.getProperty("DROP_CFDI"));
                    connection.createStatement().execute(comandosDerby.getProperty("DROP_CONFIGURACION"));
                }else if(operacion.equals("TRUNCATE")){
                    connection.createStatement().execute(comandosDerby.getProperty("TRUNCATE_CFDI_DETALLE"));
                    connection.createStatement().execute(comandosDerby.getProperty("TRUNCATE_CFDI"));
                    /*connection.createStatement().execute(comandosDerby.getProperty("DROP_CONFIGURACION"));
                    connection.createStatement().execute(comandosDerby.getProperty("CREATE_CONFIGURACION"));
                    inicializarConfiguracion();*/
                }
                connectionPool.shutdown();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        } 
    }

    /**
     * Inserta datos del cfdi en la tablas de la base de datos de derby
     * 
     * @param layout the value of layout
     * @return the boolean
     */

    public boolean  insertDatos(EstructuraLayout layout){
        boolean respuesta=false;
        BoneCP connectionPool = null;
        Connection connection = null;
        try {
            Class.forName(propiedades.getProperty("DB_DRIVER"));
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(propiedades.getProperty("DB_SERVER")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(propiedades.getProperty("DB_USER")); 
            config.setPassword(propiedades.getProperty("DB_PASSWORD"));
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
            connection = connectionPool.getConnection(); // fetch a connection
            if (connection != null){ 
                ResultSet rs=connection.createStatement().executeQuery("select UUID from cfdi where UUID='"+layout.getUuid()+"'");
                if(rs.next()){
                    String update="update  cfdi set RFC_EMISOR='"+layout.getRFC()+"', RAZON_EMISOR='"+layout.getNombreEmisor()+"', RFC_RECEPTOR='"+layout.getRfcReceptor()+"', RAZON_RECEPTOR='"+layout.getNombreReceptor()+"', SUBTOTAL="+layout.getSubtotal().replaceAll(",", "")+",OBSERVACIONES='"+(layout.getDescripcion()!=null?layout.getDescripcion():"")+"', DESCUENTO="+((layout.getDescuento()!=null && !layout.getDescuento().equals(""))?layout.getDescuento().replaceAll(",", ""):0.0)+", IMPUESTOS_TRASLADADOS="+(layout.getImporteTrasladado()!=null?layout.getImporteTrasladado().replaceAll(",", ""):0.0)+", IMPUESTOS_RETENIDOS="+(layout.getImporteRetenido()!=null?layout.getImporteRetenido().replaceAll(",", ""):0.0)+", TOTAL="+layout.getTotal().replaceAll(",", "")+", RUTA='"+layout.getRutaArchivo()+layout.getNombreArchivo()+"' where UUID='"+layout.getUuid()+"'";                        
                    connection.createStatement().execute(update);
                    connection.createStatement().execute("delete from cfdi_detalle where UUID='"+layout.getUuid()+"'");
                }else{                        
                    String insert="insert into cfdi(UUID,ESTATUS,OBSERVACIONES, TIPO, FECHA_TIMBRADO, RFC_EMISOR, RAZON_EMISOR, RFC_RECEPTOR, RAZON_RECEPTOR, DOMICILIO_RECEPTOR, SUBTOTAL, DESCUENTO, IMPUESTOS_TRASLADADOS, IMPUESTOS_RETENIDOS, TOTAL, RUTA) VALUES ('"+layout.getUuid()+"','','"+(layout.getDescripcion()!=null?layout.getDescripcion():"")+"', '"+layout.getComprobanteTipo()+"','"+layout.getFechaTimbrado().substring(0,10)+"', '"+layout.getRFC()+"', '"+layout.getNombreEmisor()+"', '"+layout.getRfcReceptor()+"', '"+layout.getNombreReceptor()+"', '"+layout.getCalleReceptor()+" "+layout.getColoniaReceptor()+"', "+(layout.getSubtotal()!=null?layout.getSubtotal().replaceAll(",", ""):0.0)+", "+((layout.getDescuento()!=null && !layout.getDescuento().equals(""))?layout.getDescuento().replaceAll(",", ""):0.0)+", "+(layout.getImporteTrasladado()!=null?layout.getImporteTrasladado().replaceAll(",", ""):0.0)+", "+(layout.getImporteRetenido()!=null?layout.getImporteRetenido().replaceAll(",", ""):0.00)+", "+(layout.getTotal()!=null?layout.getTotal().replaceAll(",", ""):0.0)+", '"+layout.getRutaArchivo()+layout.getNombreArchivo()+"')";
                    connection.createStatement().execute(insert);
                }        
                if(layout.getComprobanteTipo().equals("NOMINA")){
                    for(int i=0;i<layout.getNominaDetalle().size();i++){                            
                        String insertDetalle="insert into cfdi_detalle(UUID,NO_MOVIMIENTO,N_TIPO_CONCEPTO,N_TIPO,N_CLAVE,N_CONCEPTO,N_IMPORTE_GRAVADO,N_IMPORTE_EXCENTO) values ('"+layout.getUuid()+"',"+i+",'"+(layout.getNominaDetalle().get(i).getTipoConcepto().equals("1")?"Persepción":"Deducción")+"','"+layout.getNominaDetalle().get(i).getTipo()+"','"+layout.getNominaDetalle().get(i).getClave()+"','"+layout.getNominaDetalle().get(i).getConcepto()+"',"+(layout.getNominaDetalle().get(i).getImporteGravado()!=null?layout.getNominaDetalle().get(i).getImporteGravado().replaceAll(",", ""):0.0)+","+(layout.getNominaDetalle().get(i).getImporteExento()!=null?layout.getNominaDetalle().get(i).getImporteExento().replaceAll(",", ""):0.0)+")";
                        connection.createStatement().execute(insertDetalle);
                    }
                }else{
                    for(int i=0;i<layout.getIngresoDetalle().size();i++){ 
                        String cantidad=layout.getIngresoDetalle().get(i).getCantidad();
                        String insertDetalle="insert into cfdi_detalle(UUID,NO_MOVIMIENTO,I_CANTIDAD,I_UNIDAD,I_DESCRIPCION,I_VALOR_UNITARIO,I_IMPORTE) values ('"+layout.getUuid()+"',"+i+","+cantidad+",'"+layout.getIngresoDetalle().get(i).getUnidad()+"','"+layout.getIngresoDetalle().get(i).getDescripcion()+"',"+layout.getIngresoDetalle().get(i).getValorUnitario()+","+layout.getIngresoDetalle().get(i).getImporte()+")";
                        connection.createStatement().execute(insertDetalle);
                    }                        
                }
                respuesta=true;
                connectionPool.shutdown();
            }
        } catch (SQLException e) {
            System.out.println("Error: insertDatos 1 "+layout.getUuid());
            logger.log(Level.SEVERE, null, e);
        }catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("Error: insertDatos 3");
            logger.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error: insertDatos 4");
                    logger.log(Level.SEVERE, null, e);
                }
            }
        }
        return respuesta;
    }
    
    /**
     * Exporta los registros de de CFDI datos generales o su detalle, con el filtro que se
     * haya utilizado en la interface gráfica
     * 
     * @param query es el query filtradn para la tabla de CFDI y CFDI_DETALLE
     * @param nombre nombre del archivo 
     * @param path directorio donde se va a crear el archivo de excel
     * @return the boolean
     */
     public boolean  exportarExcel(String query,String nombre,String path){
        Connection connection = null;
        Statement st=null;
        ResultSet rs = null;
        boolean respuesta=false;
        BoneCP connectionPool = null;
        try {
            Class.forName(propiedades.getProperty("DB_DRIVER"));
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(propiedades.getProperty("DB_SERVER")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(propiedades.getProperty("DB_USER")); 
            config.setPassword(propiedades.getProperty("DB_PASSWORD"));
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
            FileOutputStream fileOut = new FileOutputStream(path+nombre+".xlsx");                
            connection = connectionPool.getConnection(); // fetch a connection

            if (connection != null){
                st = connection.createStatement();
                rs=st.executeQuery(query);
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                SXSSFWorkbook workbook= new SXSSFWorkbook(10000); 
                Sheet  sheet = workbook.createSheet(nombre); 
                int rownum = 0;  
                Row row = sheet.createRow(rownum++);      
                CellStyle stylec = workbook.createCellStyle();                                  
                stylec.setBorderBottom(CellStyle.BORDER_THIN);
                stylec.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                Font fontc = workbook.createFont();
                fontc.setBoldweight(Font.BOLDWEIGHT_BOLD);
                stylec.setFont(fontc);
                for (int i = 1; i <= count; i++){
                    row.createCell(i).setCellValue(metaData.getColumnName(i)); 
                    row.getCell(i).setCellStyle(stylec); 
                }
                while(rs.next()){
                    Row rowh = sheet.createRow(rownum++);
                    for (int i = 1; i <= count; i++){
                        if(metaData.getColumnTypeName(i).equalsIgnoreCase("INT") || metaData.getColumnTypeName(i).equalsIgnoreCase("INT UNSIGNED"))
                            rowh.createCell(i).setCellValue(rs.getInt(i));
                        else if(metaData.getColumnTypeName(i).equalsIgnoreCase("DOUBLE"))
                            rowh.createCell(i).setCellValue(rs.getDouble(i));
                        else
                            rowh.createCell(i).setCellValue(rs.getString(i));
                    }
                }         
                /*if(rownum<5000){
                    for (int i = 1; i <= count; i++)
                        sheet.autoSizeColumn(i); 
                }*/
                try {
                    workbook.write(fileOut);
                    fileOut.flush();
                    fileOut.close();

                } catch (FileNotFoundException e) {
                    System.out.println("Error: export 1");
                } catch (IOException e) {
                    System.out.println("Error: export 2");
                }                      
                respuesta=true;
                connectionPool.shutdown();
            } 
        } catch (SQLException e) {
            System.out.println("Error: insertDatos 3");
            logger.log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("Error: insertDatos 5");
            logger.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error: insertDatos 4");
                    logger.log(Level.SEVERE, null, e);
                }
            }
        }
        return respuesta;
     }
     
    /**
     * Crea o actualiza los parametros de configuración en la BD
     * 
     * @param configuracion hash table con los parametros de configuración
     * @param modo accion a realizar
     */
     public void  actualizarConfiguracion(HashMap configuracion,String modo){
        BoneCP connectionPool = null;
        Connection connection = null;
        try {
            Class.forName(propiedades.getProperty("DB_DRIVER"));
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(propiedades.getProperty("DB_SERVER")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(propiedades.getProperty("DB_USER")); 
            config.setPassword(propiedades.getProperty("DB_PASSWORD"));
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
            connection = connectionPool.getConnection(); // fetch a connection
            if (connection != null){  
                Iterator<String> keySetIterator = configuracion.keySet().iterator(); 
                while(keySetIterator.hasNext()){ 
                    String key = keySetIterator.next();
                    if(modo.equals("update"))
                        connection.createStatement().execute("update configuracion set valor='"+configuracion.get(key)+"' where variable='"+key+"'");
                    else
                        connection.createStatement().execute("insert into configuracion (variable,valor) values ('"+key+"','"+configuracion.get(key)+"')");
                }
                connectionPool.shutdown();
            }
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        } 
    }
     
    /**
     * Crea o actualiza los parametros de configuración en la BD
     * 
     * @return devuelve la configuraciónde la base de datos
     */
     public HashMap  getConfiguracion(){
         HashMap configuracion=new HashMap();
        BoneCP connectionPool = null;
        Connection connection = null;
        try {
            Class.forName(propiedades.getProperty("DB_DRIVER"));
            // setup the connection pool
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(propiedades.getProperty("DB_SERVER")); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
            config.setUsername(propiedades.getProperty("DB_USER")); 
            config.setPassword(propiedades.getProperty("DB_PASSWORD"));
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(10);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config); // setup the connection pool
            connection = connectionPool.getConnection(); // fetch a connection
            if (connection != null){                  
                ResultSet rs=connection.createStatement().executeQuery("select VARIABLE,VALOR from configuracion");
                while(rs.next())
                    configuracion.put(rs.getString("VARIABLE"), rs.getString("VALOR"));
                connectionPool.shutdown();
            }
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        } 
        return configuracion;
    }
    /**
     * Configuración por default
     * 
     */
     public void inicializarConfiguracion(){
        HashMap configuracion=new HashMap();
        configuracion.put("CONTRASENA","DEFAULT");
        configuracion.put("CORREO","default@gmail.com");
        configuracion.put("KEY","SINCRONIZAR JAVA");
        configuracion.put("COLOR","#999999");
        actualizarConfiguracion(configuracion,"insert");
     }
    
}
