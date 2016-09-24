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

/**
 *
 * @author José González Caballero (joalgoca)
 */
public class NominaDetalle {
    private String tipoConcepto;
    private String tipo;
    private String clave;
    private String concepto;
    private String importeGravado;
    private String importeExento;
    private final DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
    public NominaDetalle() {
    }
    public NominaDetalle(String tipoConcepto,String tipo,String clave,String concepto,String importeGravado,String importeExento) {
        this.tipoConcepto=tipoConcepto;
        this.clave=clave;
        this.concepto=concepto;
        this.importeGravado=importeGravado;
        this.importeExento=importeExento;
    }

    /**
     * @return the tipoConcepto
     */
    public String getTipoConcepto() {
        return tipoConcepto;
    }

    /**
     * @param tipoConcepto the tipoConcepto to set
     */
    public void setTipoConcepto(String tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the importeGravado
     */
    public String getImporteGravado() {
        try{
            Double number=Double.parseDouble(importeGravado);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeGravado;
        }  
    }

    /**
     * @param importeGravado the importeGravado to set
     */
    public void setImporteGravado(String importeGravado) {
        this.importeGravado = importeGravado;
    }

    /**
     * @return the importeExento
     */
    public String getImporteExento() {
        try{
            Double number=Double.parseDouble(importeExento);
            return decimalFormat.format(number);
        }catch(Exception e){
            return importeExento;
        }  
    }

    /**
     * @param importeExento the importeExento to set
     */
    public void setImporteExento(String importeExento) {
        this.importeExento = importeExento;
    }
}
