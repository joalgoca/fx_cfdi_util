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
package cfdi;

import cfdi.clases.UtilidadesArchivoCfdi;
import cfdi.clases.db.DerbyUtilities;
import cfdi.clases.layout.EstructuraLayout;
import cfdi.clases.util.Utilidades;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author José González Caballero (joalgoca)
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TabPane tabPanePrincipal;
    @FXML
    private CheckBox chkRecursivo;
    @FXML
    private TextField txtDirectorioXmltoPdf;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtKey;
    @FXML
    private TextField txtFiltro;
    @FXML
    private TextField txtRfcEmisor;
    @FXML
    private TextField txtRfcReceptor;
    @FXML
    private Button btnLimpiarFiltro;
    @FXML
    private Button btnGenerarPdf;
    @FXML
    private Button btnGuardarCfdi;
    @FXML
    private Button btnSeleccionarDirectorio;
    @FXML
    private Button btnDescargar;
    @FXML
    private Button btnSincronizar;
    @FXML
    private ProgressBar pgbarConversorPdf;
    @FXML
    private WebView webViewSat;
    @FXML
    private TextField txtProgress;
    @FXML
    private BarChart chartResultadosExportar;
    private ObjectProperty<ObservableList<Series<String, Integer>>> seriesProperty = new SimpleObjectProperty<>();

    private ObservableList<ObservableList> data;
    private Properties propiedades =new Properties();
    private HashMap configuracion;
    @FXML
    private TableView tableView;
    
    @FXML
    private Button btnActualizarConfiguracion;
    @FXML
    private Button btnTruncateDb;
    @FXML
    private Button btnFiltrar;
    @FXML
    private ComboBox cmbColumna;
    @FXML
    private ComboBox cmbAnio;
    @FXML
    private ComboBox cmbMes;
    @FXML
    private TextField txtRows;
    @FXML
    private TextField txtTotalPag;
    @FXML
    private TextField txtNoPagina;
    private String filtroExport;
    
    private UtilidadesArchivoCfdi utilidadesCfid;
    private DerbyUtilities utilidadesDerby;
    

    @FXML
    private void handleBtnTruncateDbAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Esta operación no es reversible");
        alert.setContentText("¿Desea continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            utilidadesDerby.inicializacionBaseDatos("TRUNCATE");
            buildData(true,1,"","","",false);
        } 
    }
    @FXML
    private void handleBtnFiltrarAction(ActionEvent event) {
        String filtro="";
        if( !txtFiltro.getText().equals(""))
            filtro=cmbColumna.getValue().toString()+" ='"+txtFiltro.getText()+"' ";        
        if( !txtRfcEmisor.getText().equals(""))
            filtro=(filtro.equals("")?"":filtro+" and ")+" RFC_EMISOR LIKE '"+txtRfcEmisor.getText()+"%' ";
        if( !txtRfcReceptor.getText().equals(""))
            filtro=(filtro.equals("")?"":filtro+" and ")+" RFC_RECEPTOR LIKE '"+txtRfcReceptor.getText()+"%' ";
        if( !cmbAnio.getValue().equals(""))
            filtro=(filtro.equals("")?"":filtro+" and ")+" year(FECHA_TIMBRADO)="+cmbAnio.getValue()+" ";
        if( !cmbMes.getValue().equals(""))
            filtro=(filtro.equals("")?"":filtro+" and ")+" month(FECHA_TIMBRADO)="+cmbMes.getValue()+" ";
        if( !filtro.equals("")){ 
            filtro=" where "+filtro;
            buildData(true,1,filtro, cmbColumna.getValue().toString(), "desc",false);
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Debe escribir el valor a filtrar!");
            alert.showAndWait();
        }
                   
    }
    @FXML
    private void handleBtnLimpiarFiltroAction(ActionEvent event) {
        buildData(true,1,"", "", "",false);
    }
    @FXML
    private void handleBtnExportarGeneralAction(ActionEvent event) {
        DirectoryChooser directoryChooser=new DirectoryChooser();
        File selectedDirectory =directoryChooser.showDialog(btnSeleccionarDirectorio.getScene().getWindow());
        if(selectedDirectory != null){
                String query="select * from cfdi "+filtroExport;
                if(!utilidadesDerby.exportarExcel(query, "ExportarGeneral", selectedDirectory.getPath()+"//")){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("No existe ningun registro a exportar");
                    alert.showAndWait();            
                }else{
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("El archivo a sido generado exitosamente");
                    alert.showAndWait();  
                }
        }
    }
    @FXML
    private void handleBtnExportarDetalleAction(ActionEvent event) {
        DirectoryChooser directoryChooser=new DirectoryChooser();
        File selectedDirectory =directoryChooser.showDialog(btnSeleccionarDirectorio.getScene().getWindow());
        if(selectedDirectory != null){
            String query="select c.* from cfdi_detalle c where c.uuid in (select uuid from cfdi  "+filtroExport+")";
            if(!utilidadesDerby.exportarExcel(query, "ExportarDetalle", selectedDirectory.getPath()+"//")){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("No existe ningun registro a exportar");
                alert.showAndWait();            
            }else{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("El archivo a sido generado exitosamente");
                alert.showAndWait();  
            }
        }
    }
    @FXML
    private void handleBtnDescargarAction(ActionEvent event) {
        Document doc=webViewSat.getEngine().getDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
	try {
		NodeList nodes = (NodeList) xpath.evaluate("//*[@name='BtnDescarga']", doc,XPathConstants.NODESET);             
                if(nodes.getLength()>0){
                    DirectoryChooser directoryChooser=new DirectoryChooser();
                    File selectedDirectory =directoryChooser.showDialog(btnSeleccionarDirectorio.getScene().getWindow());
                    if(selectedDirectory != null){                        
                        tabPanePrincipal.getSelectionModel().select(1);
                        Task task = new Task<Void>() {
                            @Override public Void call() {
                                Utilidades util=new Utilidades();
                                int[] contadores={0,0};
                                int total=nodes.getLength();   
                                String rutaSat="https://portalcfdi.facturaelectronica.sat.gob.mx/";
                                txtProgress.setText("Progreso:");
                                for (int i = 0; i < total; i++) {
                                        String enlace= nodes.item(i).getAttributes().getNamedItem("onClick").getNodeValue();
                                        if(nodes.item(i).getParentNode().getFirstChild().getAttributes().getNamedItem("value")!=null){
                                            String uuid=nodes.item(i).getParentNode().getFirstChild().getAttributes().getNamedItem("value").getNodeValue();
                                            boolean respuesta=util.descargarArchivoUrl(rutaSat+enlace.substring(19,enlace.length()-18), selectedDirectory.getAbsolutePath()+"//"+uuid+".xml");
                                            if(respuesta)
                                                contadores[0]++;
                                            else
                                                contadores[1]++;                                        
                                            pgbarConversorPdf.setProgress(i+1/(double)total);
                                        }else if(nodes.item(i).getParentNode().getFirstChild().getFirstChild().getAttributes().getNamedItem("value")!=null){
                                            String uuid=nodes.item(i).getParentNode().getFirstChild().getFirstChild().getAttributes().getNamedItem("value").getNodeValue();
                                            boolean respuesta=util.descargarArchivoUrl(rutaSat+enlace.substring(19,enlace.length()-18), selectedDirectory.getAbsolutePath()+"//"+uuid+".xml");
                                            if(respuesta)
                                                contadores[0]++;
                                            else
                                                contadores[1]++;                                        
                                            pgbarConversorPdf.setProgress(i+1/(double)total);
                                        }
                                }

                                XYChart.Series serie = new XYChart.Series();
                                    serie.setName("");   
                                    serie.getData().add(new XYChart.Data("Exito", contadores[0])); 
                                    serie.getData().add(new XYChart.Data("Errores", contadores[1]));
                                    List<Series<String, Integer>> lista = new ArrayList<>();  
                                    lista.add(serie);
                                    Platform.runLater(() -> seriesProperty.set(FXCollections.observableArrayList(lista))); 
                                return null;
                            }                
                        };
                        //pgbarConversorPdf.progressProperty().bind(task.progressProperty());
                        new Thread(task).start();
                    }
                }   else{                               
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("No existe ningun archivo a descargar");
                    alert.showAndWait();
                }            
                
	} catch (XPathExpressionException e) {
		e.printStackTrace();
	}
    }
    @FXML
    private void handleActualizarBtnConfiguracionAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Esta operación no es reversible");
        alert.setContentText("¿Desea continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
           configuracion=new HashMap();
           configuracion.put("CONTRASENA",txtContrasena.getText());
           configuracion.put("CORREO",txtCorreo.getText());
           configuracion.put("KEY",txtKey.getText());
           configuracion.put("COLOR","#999999");
           utilidadesDerby.actualizarConfiguracion(configuracion,"update");
        }
    }
    @FXML
    private void handleBtnSeleccionarDirectorioAction(ActionEvent event) {
        DirectoryChooser directoryChooser=new DirectoryChooser();
        File selectedDirectory =directoryChooser.showDialog(btnSeleccionarDirectorio.getScene().getWindow());
        if(selectedDirectory == null){
            txtDirectorioXmltoPdf.setText("");
        }else{
            txtDirectorioXmltoPdf.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    @FXML
    private void handleTxtPaginaAction(ActionEvent event) {
        String order="FECHA_TIMBRADO";
        if(!filtroExport.equals(""))
            order=cmbColumna.getValue().toString();
        if(txtNoPagina.getText().matches("\\d")){
            int noPagina=Integer.parseInt(txtNoPagina.getText());
            if(noPagina>Integer.parseInt(txtTotalPag.getText())){
                noPagina=Integer.parseInt(txtTotalPag.getText());
                txtNoPagina.setText(noPagina+"");
                buildData(true,noPagina,filtroExport, order, "desc",true);
            }else
                buildData(true,noPagina,filtroExport, order, "desc",true);
        }else
            txtNoPagina.setText("1");
    }
    
    @FXML
    private void handlebtnGenerarPdfAction(ActionEvent event) {        
        if( !txtDirectorioXmltoPdf.getText().equals("")){  
                   
            Task task = new Task<Void>() {
                @Override public Void call() {
                    btnGuardarCfdi.setDisable(true);
                    btnGenerarPdf.setDisable(true);
                    btnSeleccionarDirectorio.setDisable(true);
                    try{
                        File directorio = new File(txtDirectorioXmltoPdf.getText());
                        //{otros,directorio,xml,error,correcto}
                        int[] contadores={0,0,0,0,0};
                        
                        contadores=procesarDirectorioToPdf(directorio,contadores,chkRecursivo.isSelected());
                        
                        XYChart.Series serie = new XYChart.Series();
                        serie.setName("");   
                        serie.getData().add(new XYChart.Data("Otros", contadores[0])); 
                        serie.getData().add(new XYChart.Data("Directorio", contadores[1])); 
                        serie.getData().add(new XYChart.Data("Xml", contadores[2]));   
                        serie.getData().add(new XYChart.Data("Errores", contadores[3]));
                        serie.getData().add(new XYChart.Data("Exito", contadores[4])); 
                        List<Series<String, Integer>> lista = new ArrayList<>();  
                        lista.add(serie);
                        Platform.runLater(() -> seriesProperty.set(FXCollections.observableArrayList(lista))); 
                    }catch(Exception e){                
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                    txtDirectorioXmltoPdf.setText("");
                    btnGuardarCfdi.setDisable(false);
                    btnGenerarPdf.setDisable(false);                    
                    btnSeleccionarDirectorio.setDisable(false);
                    return null;
                }                
            };
            //pgbarConversorPdf.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar el directorio a procesar!");
            alert.showAndWait();
        }
    }
    @FXML
    private void handlebtnGuardarCfdiAction(ActionEvent event) {
        
        if( !txtDirectorioXmltoPdf.getText().equals("")){  
                   
            Task task = new Task<Void>() {
                @Override public Void call() {
                    btnGuardarCfdi.setDisable(true);
                    btnGenerarPdf.setDisable(true);
                    btnSeleccionarDirectorio.setDisable(true);
                    try{
                        File directorio = new File(txtDirectorioXmltoPdf.getText());
                        //{otros,directorio,xml,error,correcto}
                        int[] contadores={0,0,0,0,0};
                        
                        contadores=procesarDirectorioGuardarDb(directorio,contadores,chkRecursivo.isSelected());
                        
                        XYChart.Series serie = new XYChart.Series();
                        serie.setName("");   
                        serie.getData().add(new XYChart.Data("Otros", contadores[0])); 
                        serie.getData().add(new XYChart.Data("Directorio", contadores[1])); 
                        serie.getData().add(new XYChart.Data("Xml", contadores[2]));   
                        serie.getData().add(new XYChart.Data("Errores", contadores[3]));
                        serie.getData().add(new XYChart.Data("Exito", contadores[4])); 
                        List<Series<String, Integer>> lista = new ArrayList<>();  
                        lista.add(serie);
                        buildData(true,1,"","","",false);
                        Platform.runLater(() -> seriesProperty.set(FXCollections.observableArrayList(lista))); 
                    }catch(Exception e){                
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                    txtDirectorioXmltoPdf.setText("");
                    btnGuardarCfdi.setDisable(false);
                    btnGenerarPdf.setDisable(false);                    
                    btnSeleccionarDirectorio.setDisable(false);
                    return null;
                }                
            };
            //pgbarConversorPdf.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar el directorio a procesar!");
            alert.showAndWait();
        }
    }
    /**
     * Metodo para explorar una ruta y convertir los archivos xml a pdf
     * 
     * @param directorio ruta inicial a procesar
     * @param contadores arreglo con los contadores de las gráficas
     * @param isRecursivo revisar los directorios hijos en forma recursiva
     * @return 
     */
    private int[] procesarDirectorioToPdf(File directorio,int[] contadores,boolean isRecursivo){  
        try{      
            Utilidades utilidades=new Utilidades();
            File[] archivosDirectorio=utilidades.dirListByAscendingDate(directorio);
            int totalArchivosDirectoriol=archivosDirectorio.length;                
            int count=0;
            pgbarConversorPdf.setProgress(0);
            //{otros,directorio,xml,error,correcto}
            for (File archivo : archivosDirectorio) {
                if(!archivo.isDirectory()){
                    if(archivo.getName().toUpperCase().contains(".XML")){
                        contadores[2]++;
                        EstructuraLayout estructuraLayout=utilidadesCfid.exportarArchivo(archivo.getParent()+"\\",archivo.getName(),true);
                        if(estructuraLayout!=null)
                            contadores[4]++;
                        else
                            contadores[3]++;

                    }else
                        contadores[0]++;
                }else{
                    contadores[1]++; 
                    if(isRecursivo){
                        contadores=procesarDirectorioToPdf(archivo,contadores,isRecursivo);
                    }
                }
                count++;
                double progress=count/(double)totalArchivosDirectoriol;
                txtProgress.setText(archivo.getParent().substring(archivo.getParent().lastIndexOf("\\") + 1, archivo.getParent().length())+": "+Math.floor(progress*100)+"%");
                pgbarConversorPdf.setProgress(progress);

                //updateProgress(count, totalArchivosDirectoriol);
            }
        }catch(Exception e){                
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return contadores;
    }
    
    /**
     * Metodo para guardar información de los archivos XML en BD
     * 
     * @param directorio ruta inicial a procesar
     * @param contadores arreglo con los contadores de las gráficas
     * @param isRecursivo revisar los directorios hijos en forma recursiva
     * @return 
     */
    private int[] procesarDirectorioGuardarDb(File directorio,int[] contadores,boolean isRecursivo){  
        try{      
            Utilidades utilidades=new Utilidades();
            File[] archivosDirectorio=utilidades.dirListByAscendingDate(directorio);
            int totalArchivosDirectoriol=archivosDirectorio.length;                
            int count=0;
            pgbarConversorPdf.setProgress(0);
            //{otros,directorio,xml,error,correcto}
            for (File archivo : archivosDirectorio) {
                if(!archivo.isDirectory()){
                    if(archivo.getName().toUpperCase().contains(".XML")){
                        contadores[2]++;
                        boolean respuesta=utilidadesCfid.guardarArchivoDb(archivo.getParent()+"\\",archivo.getName(),false);
                        if(respuesta)
                            contadores[4]++;
                        else
                            contadores[3]++;

                    }else
                        contadores[0]++;
                }else{
                    contadores[1]++; 
                    if(isRecursivo){
                        contadores=procesarDirectorioGuardarDb(archivo,contadores,isRecursivo);
                    }
                }
                count++;
                double progress=count/(double)totalArchivosDirectoriol;
                txtProgress.setText(archivo.getParent().substring(archivo.getParent().lastIndexOf("\\") + 1, archivo.getParent().length())+": "+Math.floor(progress*100)+"%");
                pgbarConversorPdf.setProgress(progress);

                //updateProgress(count, totalArchivosDirectoriol);
            }
        }catch(Exception e){                
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return contadores;
    }
    //*Metodo de inicio
    //*
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilidades util=new Utilidades();
        try {
            //Archivo properties
            utilidadesDerby=new DerbyUtilities();
            utilidadesCfid=new UtilidadesArchivoCfdi();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String password="";
        try { 
            configuracion=utilidadesDerby.getConfiguracion();
            propiedades.load(getClass().getResourceAsStream("/cfdi/configuration.properties"));
            //*****Si quisieran casar la aplicación con un disco en particular en windows podrían usar 
            //*****el siguiente codigo
            //String numeroSerie=util.getSerialNumber(System.getProperty("user.dir").substring(0,2));
            //String numSerie=util.decrypt(configuracion.get("KEY").toString(), "GETKEYSERIAL");
            password=configuracion.get("CONTRASENA").toString();
            txtContrasena.setText(password);  
            txtCorreo.setText(configuracion.get("CORREO").toString());    
            txtKey.setText(configuracion.get("KEY").toString());
            //if(numSerie.equals(numeroSerie)){
            if(true){
                txtKey.setDisable(true);
                btnSincronizar.setDisable(true);
            }
            /*if(!numSerie.equals(numeroSerie) ){
                tabPanePrincipal.getTabs().get(0).setDisable(true);
                tabPanePrincipal.getTabs().get(1).setDisable(true);
                tabPanePrincipal.getTabs().get(2).setDisable(true);
                tabPanePrincipal.getSelectionModel().select(3);
            }*/
        } catch (IOException ex) {            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("La carga de archivo de configuración ha fallado!");
            alert.showAndWait();
        }
        // TODO
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Ingresar credenciales");
        dialog.setHeaderText("Escribir contraseña");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && result.get().equals(password) ){
            txtDirectorioXmltoPdf.setDisable(true);
            txtProgress.setDisable(true);
            chartResultadosExportar.dataProperty().bind(seriesProperty);
            ObservableList<String> anios = FXCollections.observableArrayList();
            Calendar calendario=Calendar.getInstance();
            int anio=calendario.get(Calendar.YEAR);
            anios.add("");
            for(int i=0;i<5;i++)
                anios.add((anio-i)+"");                
            cmbAnio.setItems(anios);
            cmbAnio.getSelectionModel().select("");        
            ObservableList<String> meses = FXCollections.observableArrayList();        
            meses.add("");  
            for(int i=1;i<13;i++)
                meses.add(i+"");                
            cmbMes.setItems(meses);
            cmbMes.getSelectionModel().select("");
            buildData(false,1,"","","",false);
            WebEngine engineSat = webViewSat.getEngine();
            engineSat.load("https://portalcfdi.facturaelectronica.sat.gob.mx/");
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            MenuItem item = new MenuItem("Copy");
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    ObservableList rowList = (ObservableList) tableView.getSelectionModel().getSelectedItems();

                    StringBuilder clipboardString = new StringBuilder();

                    for (Iterator it = rowList.iterator(); it.hasNext();) {
                        ObservableList<Object> row = (ObservableList<Object>) it.next();

                        for (Object cell : row) {
                            if (cell == null) {
                                cell = "";
                            }
                            clipboardString.append(cell);
                            clipboardString.append('\t');
                        }
                        clipboardString.append('\n');

                    }
                    final ClipboardContent content = new ClipboardContent();

                    content.putString(clipboardString.toString());
                    Clipboard.getSystemClipboard().setContent(content);
                }
            });
            ContextMenu menu = new ContextMenu();
            menu.getItems().add(item);
            tableView.setContextMenu(menu);
        }else{
            tabPanePrincipal.getTabs().get(0).setDisable(true);
            tabPanePrincipal.getTabs().get(1).setDisable(true);
            tabPanePrincipal.getTabs().get(2).setDisable(true);
            tabPanePrincipal.getTabs().get(3).setDisable(true);
            
        }
    }
    
    
    /**
     * Metodo para explorar una ruta y convertir los archivos xml a pdf
     * 
     * @param reload ruta inicial a procesar
     * @param pagina numero de pagina 
     * @param filtro filtro de la consulta SQL
     * @param sort ordenado por el campo
     * @param order asc|desc
     * @param isPagination activar paginación
     */
    public void buildData(boolean reload,int pagina,String filtro,String sort,String order,boolean isPagination){
        
        btnFiltrar.setDisable(true);
        btnLimpiarFiltro.setDisable(true);
        txtFiltro.setDisable(true);
        cmbColumna.setDisable(true);
        //cmbPagina.setDisable(true);
        if(filtro.equals("")){            
            txtFiltro.setText("");
            cmbAnio.getSelectionModel().select("");      
            txtRfcEmisor.setText("");      
            txtRfcReceptor.setText("");            
            cmbMes.getSelectionModel().select("");
            filtroExport="";
        }else
           filtroExport=filtro; 
        BoneCP connectionPool = null;
        Connection connection = null;
        data = FXCollections.observableArrayList();
        tableView.getItems().clear();
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
                //SQL FOR SELECTING ALL OF CUSTOMER
                int rows=500;                
                int inicio = ((pagina - 1) * rows);
                int count=0;
                if(sort.equals(""))
                    sort="FECHA_TIMBRADO";
                if(order.equals(""))
                    order="desc";
                ResultSet rs=null;
                try{
                    rs = connection.createStatement().executeQuery("SELECT count(*) from cfdi "+filtro);
                    if(rs.next())
                        count=rs.getInt(1);
                }catch(SQLException e){
                    count=0;                    
                    utilidadesDerby.inicializacionBaseDatos("CREATE");
                }
                //Controles del GUI
                int fin=(inicio+rows);
                if(count<fin)
                    fin=count;                
                txtRows.setText((inicio+1)+" - "+fin+" de "+count);                
                if(!isPagination){
                    ObservableList<String> pages = FXCollections.observableArrayList();
                    int paginas=Math.floorDiv(count, rows);
                    if(count>(rows*paginas))
                        paginas++;
                    txtTotalPag.setText(paginas+"");
                    txtNoPagina.setText("1");
                }
                //Cargar grid dinamicamente y agregarle funcion de copiado                
                String query = "SELECT * from cfdi  " + filtro + " order by " + sort + " " +order;
                String queryPaginado=query + " OFFSET " + inicio + "ROWS FETCH NEXT " + rows+" ROWS ONLY";
                rs = connection.createStatement().executeQuery(queryPaginado);
                /**********************************
                 * TABLE COLUMN ADDED DYNAMICALLY *
                 **********************************/
                if(!reload){
                    ObservableList<String> columns = FXCollections.observableArrayList();
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        final int j = i;               
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                   
                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {     
                                return new SimpleStringProperty(param.getValue().get(j).toString());     
                            }                   
                        });
                        tableView.getColumns().addAll(col);
                        if(!rs.getMetaData().getColumnName(i+1).equals("RFC_EMISOR") && !rs.getMetaData().getColumnName(i+1).equals("RFC_RECEPTOR")
                            && rs.getMetaData().getColumnTypeName(i+1).equals("VARCHAR"))
                        columns.add(rs.getMetaData().getColumnName(i+1));
                    }
                    cmbColumna.setItems(columns);
                    cmbColumna.getSelectionModel().select("UUID");
                }
                /********************************
                 * Data added to ObservableList *
                 ********************************/
                while(rs.next()){
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                        //Iterate Column
                        String valor=rs.getString(i);
                        row.add((valor!=null)?valor:"");
                    }
                    data.add(row);
                }
                //FINALLY ADDED TO TableView
                tableView.setItems(data);
                connectionPool.shutdown(); // shutdown connection pool.
            }
        } catch (SQLException e) {
            System.out.println("E1: "+e.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("E2: "+ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("E3: "+e.getMessage());
                }
            }
        }
        btnFiltrar.setDisable(false);
        btnLimpiarFiltro.setDisable(false);
        txtFiltro.setDisable(false);
        cmbColumna.setDisable(false);
        //cmbPagina.setDisable(false);
    }
}
