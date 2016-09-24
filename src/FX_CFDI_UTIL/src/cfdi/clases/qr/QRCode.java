/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfdi.clases.qr;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;

public class QRCode {
    private final String texto;
    private TipoImagen tipoImagen = TipoImagen.PNG;
    private int ancho = 125;
    private int alto = 125;

    private QRCode(String p_texto) {
        this.texto = p_texto;
    }
    public static QRCode generar(String texto) {
        return new QRCode(texto);
    }
    public QRCode imagen(TipoImagen p_tipo) {
        this.tipoImagen = p_tipo;
        return this;
    }
    public QRCode dimensiones(int p_ancho, int p_alto) {
        this.ancho = p_ancho;
        this.alto = p_alto;
        return this;
    }
    public ByteArrayOutputStream stream() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(createMatrix(), tipoImagen.toString(), stream);
        } catch (Exception e) {
            throw new QRCodeException("Fallo al generar la imagen del QRCode", e);
        }
        return stream;
    }
    
    private BitMatrix createMatrix() throws WriterException {
        return new QRCodeWriter().encode(texto, com.google.zxing.BarcodeFormat.QR_CODE, ancho, alto);
    }
    
}
