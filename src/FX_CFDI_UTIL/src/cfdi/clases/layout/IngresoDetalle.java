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

/**
 *
 * @author José González Caballero (joalgoca)
 */
public class IngresoDetalle {    
    private String cantidad;                             
    private String unidad;                               
    private String noIdentificacion="";                     
    private String descripcion;                          
    private String valorUnitario;                            
    private String importe;  
    public IngresoDetalle() {
    }

    /**
     * @return the cantidad
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the unidad
     */
    public String getUnidad() {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    /**
     * @return the noIdentificacion
     */
    public String getNoIdentificacion() {
        return noIdentificacion;
    }

    /**
     * @param noIdentificacion the noIdentificacion to set
     */
    public void setNoIdentificacion(String noIdentificacion) {
        this.noIdentificacion = noIdentificacion;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the valorUnitario
     */
    public String getValorUnitario() {
        return valorUnitario;
    }

    /**
     * @param valorUnitario the valorUnitario to set
     */
    public void setValorUnitario(String valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    /**
     * @return the importe
     */
    public String getImporte() {
        return importe;
    }

    /**
     * @param importe the importe to set
     */
    public void setImporte(String importe) {
        this.importe = importe;
    }
    
}
