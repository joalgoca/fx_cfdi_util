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

package cfdi.clases;

import cfdi.clases.db.DerbyUtilities;
import cfdi.clases.layout.EstructuraLayout;
import cfdi.clases.layout.IngresoDetalle;
import cfdi.clases.layout.NominaDetalle;
import cfdi.clases.qr.QRCode;
import cfdi.clases.qr.TipoImagen;
import cfdi.clases.util.MyLogger;
import cfdi.clases.util.NumberToLetterConvert;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author José González Caballero (joalgoca)
 */
public class UtilidadesArchivoCfdi {
    private final static Logger logger = Logger.getLogger(UtilidadesArchivoCfdi.class.getName());
    private final DerbyUtilities derby=new DerbyUtilities();
    Properties propiedades = new Properties();    
    
    public UtilidadesArchivoCfdi() throws IOException {
        propiedades.load(getClass().getResourceAsStream("/cfdi/configuration.properties"));
        MyLogger.setUp("logs/logExportarXmltoPdf.txt");
    }
    
    /**
     * Exportar archivo de xml a pdf utilizando jasperreports
     * mandando a llamar los archivos .jasper localizados en la carpeta recursos
     * 
     * @param rutaArchivo ruta donde se va a colocar el archivo
     * @param nombreArchivo nombre del archivo a exportar
     * @param showLog guardar información del inicio y finilizacion del proceso de exportación
     * @return EstructuraLayout
     */
    public EstructuraLayout exportarArchivo(String rutaArchivo,String nombreArchivo,boolean showLog){
        EstructuraLayout layout=null; 
        long inicio = System.currentTimeMillis();
        if(showLog)
            logger.log(Level.INFO, "Inicia exportar {0}", nombreArchivo);
        layout=parseLayout(rutaArchivo,nombreArchivo,showLog);
        String reporteName="";
        String reporteDetalleName="";        
        if(layout.getComprobanteTipo().equals("NOMINA")){
            reporteName="cfdiNominaJXmltoPdf.jasper";
            reporteDetalleName="cfdiNominaJXmltoPdf_detalle.jasper"; 
        }else{
            reporteName="cfdiIngresosJXmltoPdf.jasper";
            reporteDetalleName="cfdiIngresosJXmltoPdf_detalle.jasper";             
        }
        if(layout!=null){     
            try {     
                Collection layoutCollection=new ArrayList();
                // Aqui se agrega el objeto EstructuraLayout al reporte como parametro
                layoutCollection.add(layout);
                JRDataSource datasource = new JRBeanCollectionDataSource(layoutCollection, true);
                Map parameters = new HashMap();
                File f = new File("logos/"+layout.getRFC()+".png");
                if(f.exists())
                    parameters.put("P_LOGO_URL",new FileInputStream(f));
                else
                    parameters.put("P_LOGO_URL",getClass().getResourceAsStream("/recursos/img/logo.png"));
                InputStream qr=new ByteArrayInputStream((QRCode.generar("?re="+layout.getRFC()+"&rr="+layout.getRfcReceptor()+"&nr=&tt="+layout.getTotal()+"&id="+layout.getUuid()).imagen(TipoImagen.PNG).dimensiones(250,250).stream().toByteArray()));
                parameters.put("P_QR",qr);
                parameters.put("P_RECURSOS", getClass().getResourceAsStream("/recursos/"+reporteDetalleName));
                parameters.put(JRParameter.REPORT_LOCALE, new Locale("es","MX"));
                try {
                    InputStream reportStream =getClass().getResourceAsStream("/recursos/"+reporteName);
                    //JasperReport jasperReport=JasperCompileManager.compileReport(reportStream);
                    JasperPrint jasperPrint =JasperFillManager.fillReport(reportStream,parameters,datasource );
                    JasperExportManager.exportReportToPdfFile(jasperPrint, rutaArchivo+nombreArchivo.replace(".xml", ".pdf").replace(".XML", ".PDF"));
                }catch (JRException e) {
                    logger.log(Level.INFO, "Error JR {0}.\n",e.getMessage());       
                    layout=null;
                }catch (Exception e) {        
                    logger.log(Level.INFO, "Error {0}.\n",e.getMessage());       
                    layout=null;
                }
            }catch (FileNotFoundException ex) { 
                Logger.getLogger(UtilidadesArchivoCfdi.class.getName()).log(Level.SEVERE, null, ex);        
                layout=null;
            }
        }
        long fin = System.currentTimeMillis() - inicio;
        if(showLog)
            logger.log(Level.INFO, "Fin exportar {0}, TP: {1} milisegundos.\nFin proceso",new Object[]{nombreArchivo,fin});        
        return layout;
    }
     /**
     * Importar información a la BD desde un XML
     * 
     * @param rutaArchivo ruta donde se va a colocar el archivo
     * @param nombreArchivo nombre del archivo a exportar
     * @param showLog guardar información del inicio y finilizacion del proceso de exportación
     * @return EstructuraLayout
     */
    public boolean guardarArchivoDb(String rutaArchivo,String nombreArchivo,boolean showLog){
        EstructuraLayout layout=null; 
        long inicio = System.currentTimeMillis();
        boolean respuesta=false;
        if(showLog)
            logger.log(Level.INFO, "Inicia exportar {0}", nombreArchivo);
        layout=parseLayout(rutaArchivo,nombreArchivo,showLog);
        if(layout!=null){            
            respuesta=derby.insertDatos(layout);
        }
        long fin = System.currentTimeMillis() - inicio;
        if(showLog)
            logger.log(Level.INFO, "Fin exportar {0}, TP: {1} milisegundos.\nFin proceso",new Object[]{nombreArchivo,fin});        
        return respuesta;
    }
      /**
     * Proceso de parseo del XML al objeto estructura layout
     * El objeto layout es el que se pasa como parametro al reporte
     * 
     * @param rutaArchivo ruta donde se va a colocar el archivo
     * @param nombreArchivo nombre del archivo a exportar
     * @param showLog guardar información del inicio y finilizacion del proceso de exportación
     * @return EstructuraLayout
     */
    public EstructuraLayout parseLayout(String rutaArchivo,String nombreArchivo,boolean showLog){
        EstructuraLayout layout=null; 
        if(showLog)
            logger.log(Level.INFO, "Inicia parse {0}", nombreArchivo);
        try {
            File archivo;
            archivo = new File(rutaArchivo+nombreArchivo);
            SAXBuilder constructorSAX = new SAXBuilder();
            try {        
                layout=new EstructuraLayout();
                layout.setRutaArchivo(rutaArchivo);
                layout.setNombreArchivo(nombreArchivo);
                Document documento = (Document)constructorSAX.build(archivo);
                layout.setVersion(documento.getRootElement().getAttribute("version")!=null?documento.getRootElement().getAttribute("version").getValue():(documento.getRootElement().getAttribute("Version")!=null?documento.getRootElement().getAttribute("Version").getValue():""));
                layout.setSerie(documento.getRootElement().getAttribute("serie")!=null?documento.getRootElement().getAttribute("serie").getValue():(documento.getRootElement().getAttribute("Serie")!=null?documento.getRootElement().getAttribute("Serie").getValue():""));
                layout.setFolio(documento.getRootElement().getAttribute("folio")!=null?documento.getRootElement().getAttribute("folio").getValue():(documento.getRootElement().getAttribute("Folio")!=null?documento.getRootElement().getAttribute("Folio").getValue():""));
                layout.setFecha(documento.getRootElement().getAttribute("fecha")!=null?documento.getRootElement().getAttribute("fecha").getValue():(documento.getRootElement().getAttribute("Fecha")!=null?documento.getRootElement().getAttribute("Fecha").getValue():""));
                layout.setSello(documento.getRootElement().getAttribute("sello")!=null?documento.getRootElement().getAttribute("sello").getValue():(documento.getRootElement().getAttribute("Sello")!=null?documento.getRootElement().getAttribute("Sello").getValue():""));
                layout.setFormaPago(documento.getRootElement().getAttribute("formaDePago")!=null?documento.getRootElement().getAttribute("formaDePago").getValue():(documento.getRootElement().getAttribute("FormaDePago")!=null?documento.getRootElement().getAttribute("FormaDePago").getValue():""));
                layout.setNoCertificado(documento.getRootElement().getAttribute("noCertificado")!=null?documento.getRootElement().getAttribute("noCertificado").getValue():(documento.getRootElement().getAttribute("NoCertificado")!=null?documento.getRootElement().getAttribute("NoCertificado").getValue():""));
                layout.setCertificado(documento.getRootElement().getAttribute("certificado")!=null?documento.getRootElement().getAttribute("certificado").getValue():(documento.getRootElement().getAttribute("Certificado")!=null?documento.getRootElement().getAttribute("Certificado").getValue():""));
                layout.setSubtotal(documento.getRootElement().getAttribute("subTotal")!=null?documento.getRootElement().getAttribute("subTotal").getValue():(documento.getRootElement().getAttribute("SubTotal")!=null?documento.getRootElement().getAttribute("SubTotal").getValue():""));                
                layout.setImporteLetras(NumberToLetterConvert.convertNumberToLetter(documento.getRootElement().getAttribute("total")!=null?documento.getRootElement().getAttribute("total").getValue():(documento.getRootElement().getAttribute("Total")!=null?documento.getRootElement().getAttribute("Total").getValue():"")));
                layout.setTotal(documento.getRootElement().getAttribute("total")!=null?documento.getRootElement().getAttribute("total").getValue():(documento.getRootElement().getAttribute("Total")!=null?documento.getRootElement().getAttribute("Total").getValue():""));
                layout.setDescuento(documento.getRootElement().getAttribute("descuento")!=null?documento.getRootElement().getAttribute("descuento").getValue():(documento.getRootElement().getAttribute("Descuento")!=null?documento.getRootElement().getAttribute("Descuento").getValue():""));
                layout.setMotivoDescuento(documento.getRootElement().getAttribute("motivoDescuento")!=null?documento.getRootElement().getAttribute("motivoDescuento").getValue():(documento.getRootElement().getAttribute("MotivoDescuento")!=null?documento.getRootElement().getAttribute("MotivoDescuento").getValue():""));
                layout.setTipoCambio(documento.getRootElement().getAttribute("TipoCambio")!=null?documento.getRootElement().getAttribute("TipoCambio").getValue():(documento.getRootElement().getAttribute("tipoCambio")!=null?documento.getRootElement().getAttribute("tipoCambio").getValue():""));
                layout.setMoneda(documento.getRootElement().getAttribute("Moneda")!=null?documento.getRootElement().getAttribute("Moneda").getValue():(documento.getRootElement().getAttribute("moneda")!=null?documento.getRootElement().getAttribute("moneda").getValue():""));
                layout.setMetodoPago(documento.getRootElement().getAttribute("metodoDePago")!=null?documento.getRootElement().getAttribute("metodoDePago").getValue():(documento.getRootElement().getAttribute("MetodoDePago")!=null?documento.getRootElement().getAttribute("MetodoDePago").getValue():""));
                layout.setTipodeComprobante(documento.getRootElement().getAttribute("tipoDeComprobante")!=null?documento.getRootElement().getAttribute("tipoDeComprobante").getValue():(documento.getRootElement().getAttribute("TipoDeComprobante")!=null?documento.getRootElement().getAttribute("TipoDeComprobante").getValue():""));
                layout.setLugarExpedicion(documento.getRootElement().getAttribute("LugarExpedicion")!=null?documento.getRootElement().getAttribute("LugarExpedicion").getValue():(documento.getRootElement().getAttribute("lugarExpedicion")!=null?documento.getRootElement().getAttribute("lugarExpedicion").getValue():""));
                layout.setNumCtaPago(documento.getRootElement().getAttribute("NumCtaPago")!=null?documento.getRootElement().getAttribute("NumCtaPago").getValue():(documento.getRootElement().getAttribute("numCtaPago")!=null?documento.getRootElement().getAttribute("numCtaPago").getValue():""));
                layout.setCondicionesDePago(documento.getRootElement().getAttribute("condicionesDePago")!=null?documento.getRootElement().getAttribute("condicionesDePago").getValue():(documento.getRootElement().getAttribute("CondicionesDePago")!=null?documento.getRootElement().getAttribute("CondicionesDePago").getValue():""));
                Element emisor=documento.getRootElement().getChild("Emisor",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                layout.setRFC(emisor.getAttribute("rfc")!=null?emisor.getAttribute("rfc").getValue():(emisor.getAttribute("Rfc")!=null?emisor.getAttribute("Rfc").getValue():""));
                Element domicilioEmisor=emisor.getChild("DomicilioFiscal",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                if(domicilioEmisor!=null){
                    layout.setCp_df(domicilioEmisor.getAttribute("codigoPostal")!=null?domicilioEmisor.getAttribute("codigoPostal").getValue():(domicilioEmisor.getAttribute("CodigoPostal")!=null?domicilioEmisor.getAttribute("CodigoPostal").getValue():""));
                    layout.setPais_df(domicilioEmisor.getAttribute("pais")!=null?domicilioEmisor.getAttribute("pais").getValue():(domicilioEmisor.getAttribute("Pais")!=null?domicilioEmisor.getAttribute("Pais").getValue():""));
                    layout.setEstado_df(domicilioEmisor.getAttribute("estado")!=null?domicilioEmisor.getAttribute("estado").getValue():(domicilioEmisor.getAttribute("Estado")!=null?domicilioEmisor.getAttribute("Estado").getValue():""));
                    layout.setMunicipio_df(domicilioEmisor.getAttribute("municipio")!=null?domicilioEmisor.getAttribute("municipio").getValue():(domicilioEmisor.getAttribute("Municipio")!=null?domicilioEmisor.getAttribute("Municipio").getValue():""));
                    layout.setColonia_df(domicilioEmisor.getAttribute("colonia")!=null?domicilioEmisor.getAttribute("colonia").getValue():(domicilioEmisor.getAttribute("Colonia")!=null?domicilioEmisor.getAttribute("Colonia").getValue():""));
                    layout.setNoInterior_df(domicilioEmisor.getAttribute("noInterior")!=null?domicilioEmisor.getAttribute("noInterior").getValue():(domicilioEmisor.getAttribute("NoInterior")!=null?domicilioEmisor.getAttribute("NoInterior").getValue():""));                    
                    layout.setNoExterior_df(domicilioEmisor.getAttribute("noExterior")!=null?domicilioEmisor.getAttribute("noExterior").getValue():(domicilioEmisor.getAttribute("NoExterior")!=null?domicilioEmisor.getAttribute("NoExterior").getValue():""));
                    layout.setCalle_df(domicilioEmisor.getAttribute("calle")!=null?domicilioEmisor.getAttribute("calle").getValue():(domicilioEmisor.getAttribute("Calle")!=null?domicilioEmisor.getAttribute("Calle").getValue():""));
                    layout.setColonia_df(domicilioEmisor.getAttribute("localidad")!=null?domicilioEmisor.getAttribute("localidad").getValue():(domicilioEmisor.getAttribute("Localidad")!=null?domicilioEmisor.getAttribute("Localidad").getValue():""));
                }
                Element expedidoEn=emisor.getChild("ExpedidoEn",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                if(expedidoEn!=null){
                    layout.setCp(expedidoEn.getAttribute("codigoPostal")!=null?expedidoEn.getAttribute("codigoPostal").getValue():(expedidoEn.getAttribute("CodigoPostal")!=null?expedidoEn.getAttribute("CodigoPostal").getValue():""));
                    layout.setPais(expedidoEn.getAttribute("pais")!=null?expedidoEn.getAttribute("pais").getValue():(expedidoEn.getAttribute("Pais")!=null?expedidoEn.getAttribute("Pais").getValue():""));
                    layout.setEstado(expedidoEn.getAttribute("estado")!=null?expedidoEn.getAttribute("estado").getValue():(expedidoEn.getAttribute("Estado")!=null?expedidoEn.getAttribute("Estado").getValue():""));
                    layout.setMunicipio(expedidoEn.getAttribute("municipio")!=null?expedidoEn.getAttribute("municipio").getValue():(expedidoEn.getAttribute("Municipio")!=null?expedidoEn.getAttribute("Municipio").getValue():""));
                    layout.setColonia(expedidoEn.getAttribute("colonia")!=null?expedidoEn.getAttribute("colonia").getValue():(expedidoEn.getAttribute("Colonia")!=null?expedidoEn.getAttribute("Colonia").getValue():""));
                    layout.setNoInterior(expedidoEn.getAttribute("noInterior")!=null?expedidoEn.getAttribute("noInterior").getValue():(expedidoEn.getAttribute("NoInterior")!=null?expedidoEn.getAttribute("NoInterior").getValue():""));                    
                    layout.setNoExterior(expedidoEn.getAttribute("noExterior")!=null?expedidoEn.getAttribute("noExterior").getValue():(expedidoEn.getAttribute("NoExterior")!=null?expedidoEn.getAttribute("NoExterior").getValue():""));
                    layout.setCalle(expedidoEn.getAttribute("calle")!=null?expedidoEn.getAttribute("calle").getValue():(expedidoEn.getAttribute("Calle")!=null?expedidoEn.getAttribute("Calle").getValue():""));
                }
                Element regimenFiscal=emisor.getChild("RegimenFiscal",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                if(regimenFiscal!=null){
                    layout.setRegimenFiscal(regimenFiscal.getAttribute("Regimen")!=null?regimenFiscal.getAttribute("Regimen").getValue():(regimenFiscal.getAttribute("regimen")!=null?regimenFiscal.getAttribute("regimen").getValue():""));
                }
                layout.setNombreEmisor(emisor.getAttribute("nombre")!=null?emisor.getAttribute("nombre").getValue():(emisor.getAttribute("Nombre")!=null?emisor.getAttribute("Nombre").getValue():""));
                Element receptor=documento.getRootElement().getChild("Receptor",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));                
                layout.setNombreReceptor(receptor.getAttribute("nombre")!=null?receptor.getAttribute("nombre").getValue():(receptor.getAttribute("Nombre")!=null?receptor.getAttribute("Nombre").getValue():""));
                layout.setRfcReceptor(receptor.getAttribute("rfc")!=null?receptor.getAttribute("rfc").getValue():(receptor.getAttribute("Rfc")!=null?receptor.getAttribute("Rfc").getValue():""));
                Element domicilioReceptor=receptor.getChild("Domicilio",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                if(domicilioReceptor!=null){
                    layout.setCpReceptor(domicilioReceptor.getAttribute("codigoPostal")!=null?domicilioReceptor.getAttribute("codigoPostal").getValue():(domicilioReceptor.getAttribute("CodigoPostal")!=null?domicilioReceptor.getAttribute("CodigoPostal").getValue():""));
                    layout.setPaisReceptor(domicilioReceptor.getAttribute("pais")!=null?domicilioReceptor.getAttribute("pais").getValue():(domicilioReceptor.getAttribute("Pais")!=null?domicilioReceptor.getAttribute("Pais").getValue():""));
                    layout.setEstadoReceptor(domicilioReceptor.getAttribute("estado")!=null?domicilioReceptor.getAttribute("estado").getValue():(domicilioReceptor.getAttribute("Estado")!=null?domicilioReceptor.getAttribute("Estado").getValue():""));
                    layout.setMunicipioReceptor(domicilioReceptor.getAttribute("municipio")!=null?domicilioReceptor.getAttribute("municipio").getValue():(domicilioReceptor.getAttribute("Municipio")!=null?domicilioReceptor.getAttribute("Municipio").getValue():""));
                    layout.setColoniaReceptor(domicilioReceptor.getAttribute("colonia")!=null?domicilioReceptor.getAttribute("colonia").getValue():(domicilioReceptor.getAttribute("Colonia")!=null?domicilioReceptor.getAttribute("Colonia").getValue():""));
                    layout.setNoInteriorReceptor(domicilioReceptor.getAttribute("noInterior")!=null?domicilioReceptor.getAttribute("noInterior").getValue():(domicilioReceptor.getAttribute("NoInterior")!=null?domicilioReceptor.getAttribute("NoInterior").getValue():""));                    
                    layout.setNoExteriorReceptor(domicilioReceptor.getAttribute("noExterior")!=null?domicilioReceptor.getAttribute("noExterior").getValue():(domicilioReceptor.getAttribute("NoExterior")!=null?domicilioReceptor.getAttribute("NoExterior").getValue():""));
                    layout.setCalleReceptor(domicilioReceptor.getAttribute("calle")!=null?domicilioReceptor.getAttribute("calle").getValue():(domicilioReceptor.getAttribute("Calle")!=null?domicilioReceptor.getAttribute("Calle").getValue():""));
                }
                Element impuestos=documento.getRootElement().getChild("Impuestos",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                if(impuestos!=null){                    
                    layout.setRetenidos(impuestos.getAttribute("totalImpuestosRetenidos")!=null?impuestos.getAttribute("totalImpuestosRetenidos").getValue():(impuestos.getAttribute("TotalImpuestosRetenidos")!=null?impuestos.getAttribute("TotalImpuestosRetenidos").getValue():""));
                    layout.setTrasladados(impuestos.getAttribute("totalImpuestosTrasladados")!=null?impuestos.getAttribute("totalImpuestosTrasladados").getValue():(impuestos.getAttribute("TotalImpuestosTrasladados")!=null?impuestos.getAttribute("TotalImpuestosTrasladados").getValue():""));
                    Element retenciones=impuestos.getChild("Retenciones",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                    if(retenciones!=null){
                        Element retencion=retenciones.getChild("Retencion",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                        layout.setImpuestoRetenido(retencion.getAttribute("impuesto")!=null?retencion.getAttribute("impuesto").getValue():(retencion.getAttribute("Impuesto")!=null?retencion.getAttribute("Impuesto").getValue():""));
                        layout.setImporteRetenido(retencion.getAttribute("importe")!=null?retencion.getAttribute("importe").getValue():(retencion.getAttribute("Importe")!=null?retencion.getAttribute("Importe").getValue():""));
                    }
                    Element trasladados=impuestos.getChild("Trasladados",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                    if(trasladados!=null){
                        Element trasladado=trasladados.getChild("Trasladado",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                        layout.setImpuestoTrasladado(trasladado.getAttribute("impuesto")!=null?trasladado.getAttribute("impuesto").getValue():(trasladado.getAttribute("Impuesto")!=null?trasladado.getAttribute("Impuesto").getValue():""));
                        layout.setImporteTrasladado(trasladado.getAttribute("importe")!=null?trasladado.getAttribute("importe").getValue():(trasladado.getAttribute("Importe")!=null?trasladado.getAttribute("Importe").getValue():""));
                        layout.setTasaTrasladado(trasladado.getAttribute("tasa")!=null?trasladado.getAttribute("tasa").getValue():(trasladado.getAttribute("Tasa")!=null?trasladado.getAttribute("Tasa").getValue():""));                        
                    }
                }else{
                    layout.setRetenidos("");
                    layout.setTrasladados("");                    
                    layout.setImpuestoRetenido("");
                    layout.setImporteRetenido("");
                    layout.setImpuestoTrasladado("");
                    layout.setImporteTrasladado("");
                    layout.setTasaTrasladado("");
                }
                Element complemento=documento.getRootElement().getChild("Complemento",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));                
                Element nomina=complemento.getChild("Nomina",Namespace.getNamespace("nomina","http://www.sat.gob.mx/nomina"));
                
                Element conceptos=documento.getRootElement().getChild("Conceptos",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                
                if(nomina!=null){
                    Element concepto=conceptos.getChild("Concepto",Namespace.getNamespace("cfdi","http://www.sat.gob.mx/cfd/3"));
                    if(concepto!=null){
                        layout.setImporte(concepto.getAttribute("importe")!=null?concepto.getAttribute("importe").getValue():(concepto.getAttribute("Importe")!=null?concepto.getAttribute("Importe").getValue():""));
                        layout.setValorUnitario(concepto.getAttribute("valorUnitario")!=null?concepto.getAttribute("valorUnitario").getValue():(concepto.getAttribute("ValorUnitario")!=null?concepto.getAttribute("ValorUnitario").getValue():""));
                        layout.setDescripcion(concepto.getAttribute("descripcion")!=null?concepto.getAttribute("descripcion").getValue():(concepto.getAttribute("Descripcion")!=null?concepto.getAttribute("Descripcion").getValue():""));
                        layout.setUnidad(concepto.getAttribute("unidad")!=null?concepto.getAttribute("unidad").getValue():(concepto.getAttribute("Unidad")!=null?concepto.getAttribute("Unidad").getValue():""));
                        layout.setCantidad(concepto.getAttribute("cantidad")!=null?concepto.getAttribute("cantidad").getValue():(concepto.getAttribute("Cantidad")!=null?concepto.getAttribute("Cantidad").getValue():""));
                    }
                    layout.setComprobanteTipo("NOMINA");
                    layout.setPuesto(nomina.getAttribute("Puesto")!=null?nomina.getAttribute("Puesto").getValue():(nomina.getAttribute("puesto")!=null?nomina.getAttribute("puesto").getValue():""));
                    layout.setFechaInicioRelLaboral(nomina.getAttribute("FechaInicioRelLaboral")!=null?nomina.getAttribute("FechaInicioRelLaboral").getValue():(nomina.getAttribute("fechaInicioRelLaboral")!=null?nomina.getAttribute("fechaInicioRelLaboral").getValue():""));
                    layout.setClabe(nomina.getAttribute("CLABE")!=null?nomina.getAttribute("CLABE").getValue():(nomina.getAttribute("clabe")!=null?nomina.getAttribute("clabe").getValue():""));
                    layout.setBanco(nomina.getAttribute("Banco")!=null?nomina.getAttribute("Banco").getValue():(nomina.getAttribute("banco")!=null?nomina.getAttribute("banco").getValue():""));
                    layout.setTipoContrato(nomina.getAttribute("TipoContrato")!=null?nomina.getAttribute("TipoContrato").getValue():(nomina.getAttribute("tipoContrato")!=null?nomina.getAttribute("tipoContrato").getValue():""));
                    layout.setRiesgoPuesto(nomina.getAttribute("RiesgoPuesto")!=null?nomina.getAttribute("RiesgoPuesto").getValue():(nomina.getAttribute("riesgoPuesto")!=null?nomina.getAttribute("riesgoPuesto").getValue():""));
                    layout.setSalarioDiarioIntegrado(nomina.getAttribute("SalarioDiarioIntegrado")!=null?nomina.getAttribute("SalarioDiarioIntegrado").getValue():(nomina.getAttribute("salarioDiarioIntegrado")!=null?nomina.getAttribute("salarioDiarioIntegrado").getValue():""));
                    layout.setSalarioBaseCotApor(nomina.getAttribute("SalarioBaseCotApor")!=null?nomina.getAttribute("SalarioBaseCotApor").getValue():(nomina.getAttribute("salarioBaseCotApor")!=null?nomina.getAttribute("salarioBaseCotApor").getValue():""));
                    layout.setTipoJornada(nomina.getAttribute("TipoJornada")!=null?nomina.getAttribute("TipoJornada").getValue():(nomina.getAttribute("tipoJornada")!=null?nomina.getAttribute("tipoJornada").getValue():""));
                    layout.setPeriodicidadPago(nomina.getAttribute("PeriodicidadPago")!=null?nomina.getAttribute("PeriodicidadPago").getValue():(nomina.getAttribute("periodicidadPago")!=null?nomina.getAttribute("periodicidadPago").getValue():""));
                    layout.setCurp(nomina.getAttribute("CURP")!=null?nomina.getAttribute("CURP").getValue():(nomina.getAttribute("curp")!=null?nomina.getAttribute("curp").getValue():""));
                    layout.setTipoRegimen(nomina.getAttribute("TipoRegimen")!=null?nomina.getAttribute("TipoRegimen").getValue():(nomina.getAttribute("tipoRegimen")!=null?nomina.getAttribute("tipoRegimen").getValue():""));
                    layout.setNumEmpleado(nomina.getAttribute("NumEmpleado")!=null?nomina.getAttribute("NumEmpleado").getValue():(nomina.getAttribute("numEmpleado")!=null?nomina.getAttribute("numEmpleado").getValue():""));
                    layout.setVersionN(nomina.getAttribute("Version")!=null?nomina.getAttribute("Version").getValue():(nomina.getAttribute("version")!=null?nomina.getAttribute("version").getValue():""));
                    layout.setRegistroPatronal(nomina.getAttribute("RegistroPatronal")!=null?nomina.getAttribute("RegistroPatronal").getValue():(nomina.getAttribute("registroPatronal")!=null?nomina.getAttribute("registroPatronal").getValue():""));
                    layout.setNss(nomina.getAttribute("NumSeguridadSocial")!=null?nomina.getAttribute("NumSeguridadSocial").getValue():(nomina.getAttribute("numSeguridadSocial")!=null?nomina.getAttribute("numSeguridadSocial").getValue():""));
                    layout.setNumDiasPagados(nomina.getAttribute("NumDiasPagados")!=null?nomina.getAttribute("NumDiasPagados").getValue():(nomina.getAttribute("numDiasPagados")!=null?nomina.getAttribute("numDiasPagados").getValue():""));
                    layout.setDepartamento(nomina.getAttribute("Departamento")!=null?nomina.getAttribute("Departamento").getValue():(nomina.getAttribute("departamento")!=null?nomina.getAttribute("departamento").getValue():""));
                    layout.setFechaFinalPago(nomina.getAttribute("FechaFinalPago")!=null?nomina.getAttribute("FechaFinalPago").getValue():(nomina.getAttribute("fechaFinalPago")!=null?nomina.getAttribute("fechaFinalPago").getValue():""));
                    layout.setFechaPago(nomina.getAttribute("FechaPago")!=null?nomina.getAttribute("FechaPago").getValue():(nomina.getAttribute("fechaPago")!=null?nomina.getAttribute("fechaPago").getValue():""));
                    layout.setFechaInicialPago(nomina.getAttribute("FechaInicialPago")!=null?nomina.getAttribute("FechaInicialPago").getValue():(nomina.getAttribute("fechaInicialPago")!=null?nomina.getAttribute("fechaInicialPago").getValue():""));
                    Element persepciones=nomina.getChild("Percepciones",Namespace.getNamespace("nomina","http://www.sat.gob.mx/nomina"));

                    if(persepciones!=null){
                        layout.setTotalExentoP(persepciones.getAttribute("TotalExento")!=null?persepciones.getAttribute("TotalExento").getValue():(persepciones.getAttribute("totalExento")!=null?persepciones.getAttribute("totalExento").getValue():""));
                        layout.setTotalGravadoP(persepciones.getAttribute("TotalGravado")!=null?persepciones.getAttribute("TotalGravado").getValue():(persepciones.getAttribute("totalGravado")!=null?persepciones.getAttribute("totalGravado").getValue():""));
                        for(Element persepcion:persepciones.getChildren()){
                            NominaDetalle nominaDetalle=new NominaDetalle();
                            nominaDetalle.setTipo(persepcion.getAttribute("TipoPercepcion")!=null?persepcion.getAttribute("TipoPercepcion").getValue():(persepcion.getAttribute("tipoPercepcion")!=null?persepcion.getAttribute("tipoPercepcion").getValue():""));
                            nominaDetalle.setConcepto(persepcion.getAttribute("Concepto")!=null?persepcion.getAttribute("Concepto").getValue():(persepcion.getAttribute("concepto")!=null?persepcion.getAttribute("concepto").getValue():""));
                            nominaDetalle.setClave(persepcion.getAttribute("Clave")!=null?persepcion.getAttribute("Clave").getValue():(persepcion.getAttribute("clave")!=null?persepcion.getAttribute("clave").getValue():""));
                            nominaDetalle.setImporteGravado(persepcion.getAttribute("ImporteGravado")!=null?persepcion.getAttribute("ImporteGravado").getValue():(persepcion.getAttribute("importeGravado")!=null?persepcion.getAttribute("importeGravado").getValue():""));
                            nominaDetalle.setImporteExento(persepcion.getAttribute("ImporteExento")!=null?persepcion.getAttribute("ImporteExento").getValue():(persepcion.getAttribute("importeExento")!=null?persepcion.getAttribute("importeExento").getValue():""));
                            nominaDetalle.setTipoConcepto("1");                          
                            layout.addNominaDetalle(nominaDetalle);
                        }
                    }
                    Element deducciones=nomina.getChild("Deducciones",Namespace.getNamespace("nomina","http://www.sat.gob.mx/nomina"));
                    if(deducciones!=null){
                        layout.setTotalExentoD(deducciones.getAttribute("TotalExento")!=null?deducciones.getAttribute("TotalExento").getValue():(deducciones.getAttribute("totalExento")!=null?deducciones.getAttribute("totalExento").getValue():""));
                        layout.setTotalGravadoD(deducciones.getAttribute("TotalGravado")!=null?deducciones.getAttribute("TotalGravado").getValue():(deducciones.getAttribute("totalGravado")!=null?deducciones.getAttribute("totalGravado").getValue():""));
                        for(Element deduccion:deducciones.getChildren()){
                           NominaDetalle nominaDetalle=new NominaDetalle();
                            nominaDetalle.setTipo(deduccion.getAttribute("TipoDeduccion")!=null?deduccion.getAttribute("TipoDeduccion").getValue():(deduccion.getAttribute("tipoDeduccion")!=null?deduccion.getAttribute("tipoDeduccion").getValue():""));
                            nominaDetalle.setConcepto(deduccion.getAttribute("Concepto")!=null?deduccion.getAttribute("Concepto").getValue():(deduccion.getAttribute("concepto")!=null?deduccion.getAttribute("concepto").getValue():""));
                            nominaDetalle.setClave(deduccion.getAttribute("Clave")!=null?deduccion.getAttribute("Clave").getValue():(deduccion.getAttribute("clave")!=null?deduccion.getAttribute("clave").getValue():""));
                            nominaDetalle.setImporteGravado(deduccion.getAttribute("ImporteGravado")!=null?deduccion.getAttribute("ImporteGravado").getValue():(deduccion.getAttribute("importeGravado")!=null?deduccion.getAttribute("importeGravado").getValue():""));
                            nominaDetalle.setImporteExento(deduccion.getAttribute("ImporteExento")!=null?deduccion.getAttribute("ImporteExento").getValue():(deduccion.getAttribute("importeExento")!=null?deduccion.getAttribute("importeExento").getValue():""));
                            nominaDetalle.setTipoConcepto("2");                          
                            layout.addNominaDetalle(nominaDetalle);
                        }
                    }
                }else{                    
                    layout.setComprobanteTipo("FACTURA");
                    for(Element concepto:conceptos.getChildren()){
                            IngresoDetalle ingresoDetalle=new IngresoDetalle();
                            
                            ingresoDetalle.setImporte(concepto.getAttribute("importe")!=null?concepto.getAttribute("importe").getValue():(concepto.getAttribute("importe")!=null?concepto.getAttribute("importe").getValue():""));
                            ingresoDetalle.setValorUnitario(concepto.getAttribute("valorUnitario")!=null?concepto.getAttribute("valorUnitario").getValue():(concepto.getAttribute("valorUnitario")!=null?concepto.getAttribute("valorUnitario").getValue():""));
                            ingresoDetalle.setDescripcion(concepto.getAttribute("descripcion")!=null?concepto.getAttribute("descripcion").getValue():(concepto.getAttribute("descripcion")!=null?concepto.getAttribute("descripcion").getValue():""));
                            ingresoDetalle.setUnidad(concepto.getAttribute("unidad")!=null?concepto.getAttribute("unidad").getValue():(concepto.getAttribute("unidad")!=null?concepto.getAttribute("unidad").getValue():""));
                            ingresoDetalle.setCantidad(concepto.getAttribute("cantidad")!=null?concepto.getAttribute("cantidad").getValue():(concepto.getAttribute("cantidad")!=null?concepto.getAttribute("cantidad").getValue():""));
                            layout.addIngresoDetalle(ingresoDetalle);
                        }
                }  
                Element timbreFiscal=complemento.getChild("TimbreFiscalDigital",Namespace.getNamespace("tfd","http://www.sat.gob.mx/TimbreFiscalDigital"));
                if(timbreFiscal!=null){
                    layout.setUuid(timbreFiscal.getAttribute("UUID")!=null?timbreFiscal.getAttribute("UUID").getValue():(timbreFiscal.getAttribute("Uuid")!=null?timbreFiscal.getAttribute("Uuid").getValue():""));
                    layout.setFechaTimbrado(timbreFiscal.getAttribute("FechaTimbrado")!=null?timbreFiscal.getAttribute("FechaTimbrado").getValue():(timbreFiscal.getAttribute("fechaTimbrado")!=null?timbreFiscal.getAttribute("fechaTimbrado").getValue():""));
                    layout.setSelloCfd(timbreFiscal.getAttribute("selloCFD")!=null?timbreFiscal.getAttribute("selloCFD").getValue():(timbreFiscal.getAttribute("SelloCFD")!=null?timbreFiscal.getAttribute("SelloCFD").getValue():""));
                    layout.setNoCertificadoSat(timbreFiscal.getAttribute("noCertificadoSAT")!=null?timbreFiscal.getAttribute("noCertificadoSAT").getValue():(timbreFiscal.getAttribute("NoCertificadoSAT")!=null?timbreFiscal.getAttribute("NoCertificadoSAT").getValue():""));
                    layout.setSelloSat(timbreFiscal.getAttribute("selloSAT")!=null?timbreFiscal.getAttribute("selloSAT").getValue():(timbreFiscal.getAttribute("SelloSAT")!=null?timbreFiscal.getAttribute("SelloSAT").getValue():""));
                }   
                
                layout.setCadenaOriginal("||"+layout.getVersion()+"|"+layout.getUuid()+"|"+layout.getFechaTimbrado()+"|"+layout.getSelloCfd()+"|"+layout.getNoCertificadoSat()+"||");
            } catch (JDOMException e) {
                logger.log(Level.SEVERE, "{0}: {1}", new Object[]{nombreArchivo, e.getMessage()});
                layout=null;
            }
        } catch (IOException e) {
                logger.log(Level.SEVERE,  "{0}: {1}", new Object[]{nombreArchivo, e.getMessage()});
                layout=null;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{nombreArchivo, ex.getMessage()});
            layout=null;
        }
        if(showLog)
            logger.log(Level.INFO, "Fin parse ");        
        return layout;
    }    
}
