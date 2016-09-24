/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfdi.clases.qr;

public class QRCodeException extends RuntimeException {
    public QRCodeException(String mensaje, Throwable excepcion) {
        super(mensaje, excepcion);
    }
}
