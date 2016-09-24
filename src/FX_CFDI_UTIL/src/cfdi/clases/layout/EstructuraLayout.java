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

package cfdi.clases.layout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author José González Caballero (joalgoca)
 */
public class EstructuraLayout {
    private String nombreArchivo="";
    private String rutaArchivo="";
    private String comprobanteTipo="";
    //Segmento DC
                                                        //Tamaño              Obligatorio   Descripcion
    private String version="3.2";                       //3                   SI         Versión del CFDI que se va a generar, debe de ser “3.2”
    private String serie;                               //1-25                NO         Serie del CFDI, aprobado por el SAT para el emisor   _ En la última plática se acordo que estos datos son propios 
    private String folio;                               //1-20                NO         Folio del CFDI, aprobado por el SAT para el emisor   =anterior 
    private String fecha;                               //YYYY-MM-DDTHH:MM:SS SI         Fecha de generación del CFDI
    private String formaPago;                           //1-300               SI         Forma de pago del CFDI
    private String subtotal;                            //numerico 6 decimal  SI         Sub total del CFDI, antes de impuestos
    private String descuento="0";                       //numerico 6 decimal  NO         Importe total de los descuentos aplicables antes. DEFAULT: 0
    private String total;                               //numerico 6 decimal  SI         Importe total de los descuentos aplicables antes.
    private String metodoPago;                          //300                 SI         Se entiende como método de pago leyendas tales como: cheque, tarjeta de crédito o débito, depósito en cuenta.
    private String tipodeComprobante;                   //1-8                 SI         Expresa el efecto del comprobante fiscal para el contribuyente emisor. ”ingreso”  "egreso”  "traslado”
    private String tipoCambio="";                       //22 y 6              NO         Representar el tipo de cambio conforme a la moneda usada
    private String moneda="";                           //1-20                NO         Expresa la moneda utilizada para expresar los montos
    private String motivoDescuento="";                  //300                 NO         Expresa el motivo del descuento aplicable
    private String condicionesDePago="";                //300                 NO         Expresa las condiciones comerciales aplicables para el pago del comprobante fiscal digital a través de Internet.
    private String lugarExpedicion;                     //300                 SI         Expresa el lugar de expedición del comprobante.
    private String numCtaPago="";                       //4-25                NO         Incorpora al menos los cuatro últimos dígitos del número de cuenta con la que se realizó el pago.
    private String folioFiscalOrig="";                  //1-20                NO         Expresa el motivo del descuento aplicable
    private String serieFolioFiscalOrig="";             //1-25                NO         Expresa las condiciones comerciales aplicables para el pago del comprobante fiscal digital a través de Internet.
    private String fechaFolioFiscalOrig="";             //YYYY-MM-DDTHH:MM:SS NO         Expresa el lugar de expedición del comprobante.
    private String montoFolioFiscalOrig="";             //22 y 6              NO         Incorpora al menos los cuatro últimos dígitos del número de cuenta con la que se realizó el pago.
    
    //Detalle del elemento (EM) - Datos del Emisor
    private String RFC;                                 //1-13                SI         RFC del emisor del CFDI
    private String nombreEmisor="";                     //hasta 200           NO         Nombre completo del Emisor en caso de persona física, o nombre de la empresa en caso de persona moral
    //Detalle del elemento (EE) – Domicilio Fiscal
    private String calle_df="";                         //hasta 80           NO         
    private String noExterior_df="";                    //hasta 40           NO         
    private String noInterior_df="";                    //hasta 40           NO         
    private String colonia_df="";                       //hasta 80           NO 
    private String localidad_df="";                     //hasta 80           NO 
    private String referencia_df="";                    //hasta 80           NO 
    private String municipio_df="";                     //hasta 80           NO 
    private String estado_df="";                       //hasta 80           NO 
    private String pais_df;                            //hasta 100           SI 
    private String cp_df="";                            //hasta 5           NO 

    //Detalle del elemento (EE) – Expedido En
    private String calle="";                         //hasta 80           NO         
    private String noExterior="";                    //hasta 40           NO         
    private String noInterior="";                    //hasta 40           NO         
    private String colonia="";                       //hasta 80           NO 
    private String localidad="";                     //hasta 80           NO 
    private String referencia="";                    //hasta 80           NO 
    private String municipio="";                     //hasta 80           NO 
    private String estado="";                       //hasta 80           NO 
    private String pais;                            //hasta 100           SI 
    private String cp="";                            //hasta 5           NO 
     //Detalle del elemento (RC) - Datos del Receptor
     private String rfcReceptor;                           //1-13                SI         RFC del receptor del CFDI
     private String nombreReceptor="";                     //hasta 200           NO         Nombre completo del receptor en caso de persona física, o nombre de la empresa en caso de persona moral
    //Detalle del elemento (DE) - Dirección del Receptor
    private String calleReceptor="";                         //hasta 80           NO         
    private String noExteriorReceptor="";                    //hasta 40           NO         
    private String noInteriorReceptor="";                    //hasta 40           NO         
    private String coloniaReceptor="";                       //hasta 80           NO 
    private String localidadReceptor="";                     //hasta 80           NO 
    private String referenciaReceptor="";                    //hasta 80           NO 
    private String municipioReceptor="";                     //hasta 80           NO 
    private String estadoReceptor="";                       //hasta 80           NO 
    private String paisReceptor;                            //hasta 100           SI 
    private String cpReceptor="";                            //hasta 5           NO 
    //Detalle del elemento (CN) - Información del concepto (Pueden existir varios conceptos en un solo archivo)
    private String cantidad;                             //18 y 2          SI 
    private String unidad;                               //1-50          SI 
    private String noIdentificacion="";                     //1 a 1000           NO 
    private String descripcion;                          //1 a 1000          SI 
    private String valorUnitario;                            //22 y 6           SI 
    private String importe;                            //22 y 6           SI 
    //Detalle del elemento (IT) - Total de impuestos en el comprobante, tanto retenido como Trasladado.
    private String retenidos="0";                          //1 a 1000          SI           Monto total de los impuestos retenidos
    private String trasladados="0";                            //22 y 6           SI        Monto total de los impuestos trasladados
    //Detalle del elemento (TI) - Impuestos Trasladados.
    private String impuestoTrasladado;                            //1-150           SI        Monto total de los impuestos trasladados
    private String importeTrasladado;                            //22 y 6           SI        Monto total de los impuestos trasladados
    private String tasaTrasladado;                            //3           SI        Monto total de los impuestos trasladados
    //Detalle del elemento (RI) - Impuestos retenidos.
    private String impuestoRetenido;                            //1-150           SI        Monto total de los impuestos trasladados
    private String importeRetenido;                            //22 y 6           SI        Monto total de los impuestos trasladados
    private String tasaRetenido;                                //3           SI        Monto total de los impuestos trasladados
    
    //Detalle del elemento (Ml) - Mail
    private String para="";                             //Cadena         NO 
    private String cc="";                               //Cadena          NO 
    private String co="";                     //Cadena          NO 
    
    //Detalle del elemento (OP) - Observaciones
    private String op_observaciones=""; 
    
    //////////////////////////////////////////////////////////////////////////
    //////////COMPLEMENTO NOMINA
    /////////////////////////////////////////////////////////////////////////
    
    //Detalle del elemento CNE
    private String versionN="1.1";            //*
    private String registroPatronal;    //*
    private String numEmpleado;
    private String curp;                //*
    private String tipoRegimen;         //*   catalogo sat
    private String nss;                 
    private String fechaPago;           //*    yyyy-mm-dd
    private String fechaInicialPago;    //*
    private String fechaFinalPago;      //*
    private String numDiasPagados;      //*
    private String departamento;
    private String clabe;               
    private String banco;
    private String fechaInicioRelLaboral;
    private String antiguedad;
    private String puesto;
    private String tipoContrato;
    private String tipoJornada;
    private String periodicidadPago;    //*
    private String salarioBaseCotApor;
    private String riesgoPuesto;         // catalogo sat
    private String salarioDiarioIntegrado;
    
    //(CNP) información de percepciones
    private String totalGravadoP;        //*
    private String totalExentoP;         //*
    
    //(NPD) información detallada de percepciones
    private String tipoPercepcion;      //*    catalogo del sat
    private String claveP;               //*
    private String conceptoP;            //*
    private String importeGravadoP;      //*
    private String importeExentoP;       //*
    
    //(CND) información de deducciones
    private String totalGravadoD;
    private String totalExentoD;
    
    //(NDD) información detallada de percepciones
    private String tipoDeduccion;      //*    catalogo del sat
    private String claveD;               //*
    private String conceptoD;            //*
    private String importeGravadoD;      //*
    private String importeExentoD;       //*

    
    //(CNI) información de las incapacidades
    private String diasIncapacidad;      //*    
    private String tipoIncapacidad;      //*  catalogo del sat
    private String descuentoI;            //*
    
    //(CNH) información de las las horas extra
    private String dias;                    //*    catalogo del sat
    private String tipoHoras;               //*
    private String horasExtra;            //*
    private String importePagado;            //*
    
    //EXTRAS guardar nomina en base de datos
    private String numeroCheque;
    private String numeroPlaza;
    private String sector;
    
    //FT|idSucursal|noTicket|propina|idTransaccion
    private String idSucursal;
    private String noTicket;
    private String propina;
    private String idTransaccion;
    
    //DATOS SELLO
    private String sello;
    private String certificado;
    private String noCertificado;
    private String selloCfd;
    private String fechaTimbrado;
    private String noCertificadoSat;
    private String cadenaOriginal;
    private String selloSat;
    private String uuid;
    private String regimenFiscal;    
    
    private String importeLetras;
    
    private List<NominaDetalle> nominaDetalleList;
    private List<IngresoDetalle> ingresoDetalleList;
    
    private final DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
    
    public EstructuraLayout() {
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getSubtotal() {
        try{
            Double number=Double.parseDouble(subtotal);
            return decimalFormat.format(number);
        }catch(Exception e){
            return subtotal;
        }        
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getDescuento() {
        
        try{
            Double number=Double.parseDouble(descuento);
            return decimalFormat.format(number);
        }catch(Exception e){
            return descuento;
        }     
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getTotal() {
        try{
            Double number=Double.parseDouble(total);
            return decimalFormat.format(number);
        }catch(Exception e){
            return total;
        }     
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipodeComprobante() {
        return tipodeComprobante;
    }

    public void setTipodeComprobante(String tipodeComprobante) {
        this.tipodeComprobante = tipodeComprobante;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getMotivoDescuento() {
        return motivoDescuento;
    }

    public void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
    }

    public String getCondicionesDePago() {
        return condicionesDePago;
    }

    public void setCondicionesDePago(String condicionesDePago) {
        this.condicionesDePago = condicionesDePago;
    }

    public String getLugarExpedicion() {
        return lugarExpedicion;
    }

    public void setLugarExpedicion(String lugarExpedicion) {
        this.lugarExpedicion = lugarExpedicion;
    }

    public String getNumCtaPago() {
        return numCtaPago;
    }

    public void setNumCtaPago(String numCtaPago) {
        this.numCtaPago = numCtaPago;
    }

    public String getFolioFiscalOrig() {
        return folioFiscalOrig;
    }

    public void setFolioFiscalOrig(String folioFiscalOrig) {
        this.folioFiscalOrig = folioFiscalOrig;
    }

    public String getSerieFolioFiscalOrig() {
        return serieFolioFiscalOrig;
    }

    public void setSerieFolioFiscalOrig(String serieFolioFiscalOrig) {
        this.serieFolioFiscalOrig = serieFolioFiscalOrig;
    }

    public String getFechaFolioFiscalOrig() {
        return fechaFolioFiscalOrig;
    }

    public void setFechaFolioFiscalOrig(String fechaFolioFiscalOrig) {
        this.fechaFolioFiscalOrig = fechaFolioFiscalOrig;
    }

    public String getMontoFolioFiscalOrig() {
        return montoFolioFiscalOrig;
    }

    public void setMontoFolioFiscalOrig(String montoFolioFiscalOrig) {
        this.montoFolioFiscalOrig = montoFolioFiscalOrig;
    }

    public String getRFC() {
        return RFC;
    }

    public void setRFC(String RFC) {
        this.RFC = RFC;
    }

    public String getNombreEmisor() {
        return nombreEmisor;
    }

    public void setNombreEmisor(String nombreEmisor) {
        this.nombreEmisor = nombreEmisor;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNoExterior() {
        return noExterior;
    }

    public void setNoExterior(String noExterior) {
        this.noExterior = noExterior;
    }

    public String getNoInterior() {
        return noInterior;
    }

    public void setNoInterior(String noInterior) {
        this.noInterior = noInterior;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getRfcReceptor() {
        return rfcReceptor;
    }

    public void setRfcReceptor(String rfcReceptor) {
        this.rfcReceptor = rfcReceptor;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public String getCalleReceptor() {
        return calleReceptor;
    }

    public void setCalleReceptor(String calleReceptor) {
        this.calleReceptor = calleReceptor;
    }

    public String getNoExteriorReceptor() {
        return noExteriorReceptor;
    }

    public void setNoExteriorReceptor(String noExteriorReceptor) {
        this.noExteriorReceptor = noExteriorReceptor;
    }

    public String getNoInteriorReceptor() {
        return noInteriorReceptor;
    }

    public void setNoInteriorReceptor(String noInteriorReceptor) {
        this.noInteriorReceptor = noInteriorReceptor;
    }

    public String getColoniaReceptor() {
        return coloniaReceptor;
    }

    public void setColoniaReceptor(String coloniaReceptor) {
        this.coloniaReceptor = coloniaReceptor;
    }

    public String getLocalidadReceptor() {
        return localidadReceptor;
    }

    public void setLocalidadReceptor(String localidadReceptor) {
        this.localidadReceptor = localidadReceptor;
    }

    public String getReferenciaReceptor() {
        return referenciaReceptor;
    }

    public void setReferenciaReceptor(String referenciaReceptor) {
        this.referenciaReceptor = referenciaReceptor;
    }

    public String getMunicipioReceptor() {
        return municipioReceptor;
    }

    public void setMunicipioReceptor(String municipioReceptor) {
        this.municipioReceptor = municipioReceptor;
    }

    public String getEstadoReceptor() {
        return estadoReceptor;
    }

    public void setEstadoReceptor(String estadoReceptor) {
        this.estadoReceptor = estadoReceptor;
    }

    public String getPaisReceptor() {
        return paisReceptor;
    }

    public void setPaisReceptor(String paisReceptor) {
        this.paisReceptor = paisReceptor;
    }

    public String getCpReceptor() {
        return cpReceptor;
    }

    public void setCpReceptor(String cpReceptor) {
        this.cpReceptor = cpReceptor;
    }

    public String getCantidad() {
        try{
            Double number=Double.parseDouble(cantidad);
            return decimalFormat.format(number);
        }catch(Exception e){
            return cantidad;
        } 
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        try{
            Double number=Double.parseDouble(unidad);
            return decimalFormat.format(number);
        }catch(Exception e){
            return unidad;
        } 
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getNoIdentificacion() {
        return noIdentificacion;
    }

    public void setNoIdentificacion(String noIdentificacion) {
        this.noIdentificacion = noIdentificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getValorUnitario() {
        try{
            Double number=Double.parseDouble(valorUnitario);
            return decimalFormat.format(number);
        }catch(Exception e){
            return valorUnitario;
        } 
    }

    public void setValorUnitario(String valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public String getImporte() {
        try{
            Double number=Double.parseDouble(importe);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importe;
        } 
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getRetenidos() {        
        try{
            Double number=Double.parseDouble(retenidos);
            return decimalFormat.format(number);
        }catch(Exception e){
            return retenidos;
        }     
    }

    public void setRetenidos(String retenidos) {
        this.retenidos = retenidos;
    }

    public String getTrasladados() {    
        try{
            Double number=Double.parseDouble(trasladados);
            return decimalFormat.format(number);
        }catch(Exception e){
            return trasladados;
        }    
    }

    public void setTrasladados(String trasladados) {
        this.trasladados = trasladados;
    }

    public String getImpuestoTrasladado() {
        return impuestoTrasladado;
    }

    public void setImpuestoTrasladado(String impuestoTrasladado) {
        this.impuestoTrasladado = impuestoTrasladado;
    }

    public String getImporteTrasladado() {
        try{
            Double number=Double.parseDouble(importeTrasladado);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeTrasladado;
        } 
    }

    public void setImporteTrasladado(String importeTrasladado) {
        this.importeTrasladado = importeTrasladado;
    }

    public String getTasaTrasladado() {
        return tasaTrasladado;
    }

    public void setTasaTrasladado(String tasaTrasladado) {
        this.tasaTrasladado = tasaTrasladado;
    }

    public String getImpuestoRetenido() {
        return impuestoRetenido;
    }

    public void setImpuestoRetenido(String impuestoRetenido) {
        this.impuestoRetenido = impuestoRetenido;
    }

    public String getImporteRetenido() {
        try{
            Double number=Double.parseDouble(importeRetenido);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeRetenido;
        } 
    }

    public void setImporteRetenido(String importeRetenido) {
        this.importeRetenido = importeRetenido;
    }

    public String getTasaRetenido() {
        return tasaRetenido;
    }

    public void setTasaRetenido(String tasaRetenido) {
        this.tasaRetenido = tasaRetenido;
    }

    public String getVersionN() {
        return versionN;
    }

    public void setVersionN(String versionN) {
        this.versionN = versionN;
    }

    public String getRegistroPatronal() {
        return registroPatronal;
    }

    public void setRegistroPatronal(String registroPatronal) {
        this.registroPatronal = registroPatronal;
    }

    public String getNumEmpleado() {
        return numEmpleado;
    }

    public void setNumEmpleado(String numEmpleado) {
        this.numEmpleado = numEmpleado;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getTipoRegimen() {
        return tipoRegimen;
    }

    public void setTipoRegimen(String tipoRegimen) {
        this.tipoRegimen = tipoRegimen;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getFechaInicialPago() {
        return fechaInicialPago;
    }

    public void setFechaInicialPago(String fechaInicialPago) {
        this.fechaInicialPago = fechaInicialPago;
    }

    public String getFechaFinalPago() {
        return fechaFinalPago;
    }

    public void setFechaFinalPago(String fechaFinalPago) {
        this.fechaFinalPago = fechaFinalPago;
    }

    public String getNumDiasPagados() {
        return numDiasPagados;
    }

    public void setNumDiasPagados(String numDiasPagados) {
        this.numDiasPagados = numDiasPagados;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getFechaInicioRelLaboral() {
        return fechaInicioRelLaboral;
    }

    public void setFechaInicioRelLaboral(String fechaInicioRelLaboral) {
        this.fechaInicioRelLaboral = fechaInicioRelLaboral;
    }

    public String getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoJornada() {
        return tipoJornada;
    }

    public void setTipoJornada(String tipoJornada) {
        this.tipoJornada = tipoJornada;
    }

    public String getPeriodicidadPago() {
        return periodicidadPago;
    }

    public void setPeriodicidadPago(String periodicidadPago) {
        this.periodicidadPago = periodicidadPago;
    }

    public String getSalarioBaseCotApor() {
        return salarioBaseCotApor;
    }

    public void setSalarioBaseCotApor(String salarioBaseCotApor) {
        this.salarioBaseCotApor = salarioBaseCotApor;
    }

    public String getRiesgoPuesto() {
        return riesgoPuesto;
    }

    public void setRiesgoPuesto(String riesgoPuesto) {
        this.riesgoPuesto = riesgoPuesto;
    }

    public String getSalarioDiarioIntegrado() {
        try{
            Double number=Double.parseDouble(salarioDiarioIntegrado);
            return decimalFormat.format(number);
        }catch(Exception e){
            return salarioDiarioIntegrado;
        }   
    }

    public void setSalarioDiarioIntegrado(String salarioDiarioIntegrado) {
        this.salarioDiarioIntegrado = salarioDiarioIntegrado;
    }

    public String getTotalGravadoP() {
        try{
            Double number=Double.parseDouble(totalGravadoP);
            return decimalFormat.format(number);
        }catch(Exception e){
            return totalGravadoP;
        }   
    }

    public void setTotalGravadoP(String totalGravadoP) {
        this.totalGravadoP = totalGravadoP;
    }

    public String getTotalExentoP() {
        try{
            Double number=Double.parseDouble(totalExentoP);
            return decimalFormat.format(number);
        }catch(Exception e){
            return totalExentoP;
        }   
    }

    public void setTotalExentoP(String totalExentoP) {
        this.totalExentoP = totalExentoP;
    }

    public String getTipoPercepcion() {
        return tipoPercepcion;
    }

    public void setTipoPercepcion(String tipoPercepcion) {
        this.tipoPercepcion = tipoPercepcion;
    }

    public String getClaveP() {
        return claveP;
    }

    public void setClaveP(String claveP) {
        this.claveP = claveP;
    }

    public String getConceptoP() {
        return conceptoP;
    }

    public void setConceptoP(String conceptoP) {
        this.conceptoP = conceptoP;
    }

    public String getImporteGravadoP() {
        try{
            Double number=Double.parseDouble(importeGravadoP);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeGravadoP;
        } 
    }

    public void setImporteGravadoP(String importeGravadoP) {
        this.importeGravadoP = importeGravadoP;
    }

    public String getImporteExentoP() {
        try{
            Double number=Double.parseDouble(importeExentoP);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeExentoP;
        } 
    }

    public void setImporteExentoP(String importeExentoP) {
        this.importeExentoP = importeExentoP;
    }

    public String getTotalGravadoD() {
        try{
            Double number=Double.parseDouble(totalGravadoD);
            return decimalFormat.format(number);
        }catch(Exception e){
            return totalGravadoD;
        } 
    }

    public void setTotalGravadoD(String totalGravadoD) {
        this.totalGravadoD = totalGravadoD;
    }

    public String getTotalExentoD() {
        try{
            Double number=Double.parseDouble(totalExentoD);
            return decimalFormat.format(number);
        }catch(Exception e){
            return totalExentoD;
        } 
    }

    public void setTotalExentoD(String totalExentoD) {
        this.totalExentoD = totalExentoD;
    }

    public String getTipoDeduccion() {
        return tipoDeduccion;
    }

    public void setTipoDeduccion(String tipoDeduccion) {
        this.tipoDeduccion = tipoDeduccion;
    }

    public String getClaveD() {
        return claveD;
    }

    public void setClaveD(String claveD) {
        this.claveD = claveD;
    }

    public String getConceptoD() {
        return conceptoD;
    }

    public void setConceptoD(String conceptoD) {
        this.conceptoD = conceptoD;
    }

    public String getImporteGravadoD() {
        try{
            Double number=Double.parseDouble(importeGravadoD);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeGravadoD;
        } 
    }

    public void setImporteGravadoD(String importeGravadoD) {
        this.importeGravadoD = importeGravadoD;
    }

    public String getImporteExentoD() {
        try{
            Double number=Double.parseDouble(importeExentoD);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeExentoD;
        } 
    }

    public void setImporteExentoD(String importeExentoD) {
        this.importeExentoD = importeExentoD;
    }

    public String getDiasIncapacidad() {
        return diasIncapacidad;
    }

    public void setDiasIncapacidad(String diasIncapacidad) {
        this.diasIncapacidad = diasIncapacidad;
    }

    public String getTipoIncapacidad() {
        return tipoIncapacidad;
    }

    public void setTipoIncapacidad(String tipoIncapacidad) {
        this.tipoIncapacidad = tipoIncapacidad;
    }

    public String getDescuentoI() {
        return descuentoI;
    }

    public void setDescuentoI(String descuentoI) {
        this.descuentoI = descuentoI;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getTipoHoras() {
        return tipoHoras;
    }

    public void setTipoHoras(String tipoHoras) {
        this.tipoHoras = tipoHoras;
    }

    public String getHorasExtra() {
        return horasExtra;
    }

    public void setHorasExtra(String horasExtra) {
        this.horasExtra = horasExtra;
    }

    public String getImportePagado() {
        try{
            Double number=Double.parseDouble(importePagado);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importePagado;
        } 
    }

    public void setImportePagado(String importePagado) {
        this.importePagado = importePagado;
    }
    /**
     * @return the numeroCheque
     */
    public String getNumeroCheque() {
        return numeroCheque;
    }

    /**
     * @param numeroCheque the numeroCheque to set
     */
    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    /**
     * @return the numeroPlaza
     */
    public String getNumeroPlaza() {
        return numeroPlaza;
    }

    /**
     * @param numeroPlaza the numeroPlaza to set
     */
    public void setNumeroPlaza(String numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    /**
     * @return the sector
     */
    public String getSector() {
        return sector;
    }

    /**
     * @param sector the sector to set
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * @return the idSucursal
     */
    public String getIdSucursal() {
        return idSucursal;
    }

    /**
     * @param idSucursal the idSucursal to set
     */
    public void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    /**
     * @return the noTicket
     */
    public String getNoTicket() {
        return noTicket;
    }

    /**
     * @param noTicket the noTicket to set
     */
    public void setNoTicket(String noTicket) {
        this.noTicket = noTicket;
    }

    /**
     * @return the propina
     */
    public String getPropina() {
        return propina;
    }

    /**
     * @param propina the propina to set
     */
    public void setPropina(String propina) {
        this.propina = propina;
    }

    /**
     * @return the idTransaccion
     */
    public String getIdTransaccion() {
        return idTransaccion;
    }

    /**
     * @param idTransaccion the idTransaccion to set
     */
    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    /**
     * @return the op_observaciones
     */
    public String getOp_observaciones() {
        return op_observaciones;
    }

    /**
     * @param op_observaciones the op_observaciones to set
     */
    public void setOp_observaciones(String op_observaciones) {
        this.op_observaciones = op_observaciones;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    
   
    /**
     * @return the calle_df
     */
    public String getCalle_df() {
        return calle_df;
    }

    /**
     * @param calle_df the calle_df to set
     */
    public void setCalle_df(String calle_df) {
        this.calle_df = calle_df;
    }

    /**
     * @return the noExterior_df
     */
    public String getNoExterior_df() {
        return noExterior_df;
    }

    /**
     * @param noExterior_df the noExterior_df to set
     */
    public void setNoExterior_df(String noExterior_df) {
        this.noExterior_df = noExterior_df;
    }

    /**
     * @return the noInterior_df
     */
    public String getNoInterior_df() {
        return noInterior_df;
    }

    /**
     * @param noInterior_df the noInterior_df to set
     */
    public void setNoInterior_df(String noInterior_df) {
        this.noInterior_df = noInterior_df;
    }

    /**
     * @return the colonia_df
     */
    public String getColonia_df() {
        return colonia_df;
    }

    /**
     * @param colonia_df the colonia_df to set
     */
    public void setColonia_df(String colonia_df) {
        this.colonia_df = colonia_df;
    }

    /**
     * @return the localidad_df
     */
    public String getLocalidad_df() {
        return localidad_df;
    }

    /**
     * @param localidad_df the localidad_df to set
     */
    public void setLocalidad_df(String localidad_df) {
        this.localidad_df = localidad_df;
    }

    /**
     * @return the referencia_df
     */
    public String getReferencia_df() {
        return referencia_df;
    }

    /**
     * @param referencia_df the referencia_df to set
     */
    public void setReferencia_df(String referencia_df) {
        this.referencia_df = referencia_df;
    }

    /**
     * @return the municipio_df
     */
    public String getMunicipio_df() {
        return municipio_df;
    }

    /**
     * @param municipio_df the municipio_df to set
     */
    public void setMunicipio_df(String municipio_df) {
        this.municipio_df = municipio_df;
    }

    /**
     * @return the estado_df
     */
    public String getEstado_df() {
        return estado_df;
    }

    /**
     * @param estado_df the estado_df to set
     */
    public void setEstado_df(String estado_df) {
        this.estado_df = estado_df;
    }

    /**
     * @return the pais_df
     */
    public String getPais_df() {
        return pais_df;
    }

    /**
     * @param pais_df the pais_df to set
     */
    public void setPais_df(String pais_df) {
        this.pais_df = pais_df;
    }

    /**
     * @return the cp_df
     */
    public String getCp_df() {
        return cp_df;
    }

    /**
     * @param cp_df the cp_df to set
     */
    public void setCp_df(String cp_df) {
        this.cp_df = cp_df;
    }

    /**
     * @return the sello
     */
    public String getSello() {
        return sello;
    }

    /**
     * @param sello the sello to set
     */
    public void setSello(String sello) {
        this.sello = sello;
    }

    /**
     * @return the certificado
     */
    public String getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    /**
     * @return the noCertificado
     */
    public String getNoCertificado() {
        return noCertificado;
    }

    /**
     * @param noCertificado the noCertificado to set
     */
    public void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    /**
     * @return the selloCfd
     */
    public String getSelloCfd() {
        return selloCfd;
    }

    /**
     * @param selloCfd the selloCfd to set
     */
    public void setSelloCfd(String selloCfd) {
        this.selloCfd = selloCfd;
    }

    /**
     * @return the fechaTimbrado
     */
    public String getFechaTimbrado() {
        return fechaTimbrado;
    }

    /**
     * @param fechaTimbrado the fechaTimbrado to set
     */
    public void setFechaTimbrado(String fechaTimbrado) {
        this.fechaTimbrado = fechaTimbrado;
    }

    /**
     * @return the noCertificadoSat
     */
    public String getNoCertificadoSat() {
        return noCertificadoSat;
    }

    /**
     * @param noCertificadoSat the noCertificadoSat to set
     */
    public void setNoCertificadoSat(String noCertificadoSat) {
        this.noCertificadoSat = noCertificadoSat;
    }

    /**
     * @return the cadenaOriginal
     */
    public String getCadenaOriginal() {
        return cadenaOriginal;
    }

    /**
     * @param cadenaOriginal the cadenaOriginal to set
     */
    public void setCadenaOriginal(String cadenaOriginal) {
        this.cadenaOriginal = cadenaOriginal;
    }

    /**
     * @return the selloSat
     */
    public String getSelloSat() {
        return selloSat;
    }

    /**
     * @param selloSat the selloSat to set
     */
    public void setSelloSat(String selloSat) {
        this.selloSat = selloSat;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the regimenFiscal
     */
    public String getRegimenFiscal() {
        return regimenFiscal;
    }

    /**
     * @param regimenFiscal the regimenFiscal to set
     */
    public void setRegimenFiscal(String regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    /**
     * @return the importeLetras
     */
    public String getImporteLetras() {
        return importeLetras;
    }

    /**
     * @param importeLetras the detalleNomina to set
     */
    public void setImporteLetras(String importeLetras) {
        this.importeLetras = importeLetras;
    }
    
    public void addNominaDetalle(NominaDetalle nominaDetalle) {
        if(nominaDetalleList==null)
            nominaDetalleList=new ArrayList();
        nominaDetalleList.add(nominaDetalle);
    }

    /**
     * @return the nominaDetalleList
     */
    public List<NominaDetalle> getNominaDetalle() {
        return nominaDetalleList;
    }

    /**
     * @param nominaDetalle the nominaDetalleList to set
     */
    public void setNominaDetalle(List<NominaDetalle> nominaDetalle) {
        this.nominaDetalleList = nominaDetalle;
    }

    public void addIngresoDetalle(IngresoDetalle ingresoDetalle) {
        if(ingresoDetalleList==null)
            ingresoDetalleList=new ArrayList();
        ingresoDetalleList.add(ingresoDetalle);
    }
    /**
     * @return the ingresoDetalleList
     */
    public List<IngresoDetalle> getIngresoDetalle() {
        return ingresoDetalleList;
    }

    /**
     * @param ingresoDetalle the ingresoDetalleList to set
     */
    public void setIngresoDetalle(List<IngresoDetalle> ingresoDetalle) {
        this.ingresoDetalleList = ingresoDetalle;
    }

    /**
     * @return the rutaArchivo
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * @param rutaArchivo the rutaArchivo to set
     */
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * @return the comprobanteTipo
     */
    public String getComprobanteTipo() {
        return comprobanteTipo;
    }

    /**
     * @param comprobanteTipo the comprobanteTipo to set
     */
    public void setComprobanteTipo(String comprobanteTipo) {
        this.comprobanteTipo = comprobanteTipo;
    }
    
    
}
