package com.estebanfcv.Administrador;

import com.estebanfcv.MailAdmin.Archivos;
import com.estebanfcv.Util.AESCrypt;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import com.estebanfcv.conexion.Conexiones;
import com.estebanfcv.correo.CuerpoCorreos;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author estebanfcv
 */
public class HiloCorreo implements Runnable {

    private Archivos archivo;
    private AESCrypt aes;
    private String archivoConfiguracion;
    private Properties propConfig;
    private Archivos arc;
    private Calendar fecha;

    public HiloCorreo() {
        try {
            propConfig = new Properties();
            arc = new Archivos();
            aes = new AESCrypt(false, "123");
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                fecha = Calendar.getInstance();
                arc.generarLog(fecha);
                if (!Conexiones.verificarConexionInternet()) {
                    Thread.sleep(Long.parseLong(propConfig.getProperty("TiempoEsperaHilo")) * 60 * 1000);
                    System.out.println("No hay internet");
                    Util.agregarLog("No hay internet", fecha);
                    continue;
                }
                archivoConfiguracion = aes.desencriptar(new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_CONF));
                propConfig.load(new ByteArrayInputStream(archivoConfiguracion.getBytes()));
                archivo = new Archivos();
                if (!archivo.revisarArchivoMailAdmin()) {
                    archivo.crearArchivoMailAdmin();
                }
                compararCorreoPrincipal();
                Thread.sleep(Long.parseLong(propConfig.getProperty("TiempoEsperaHilo")) * 60 * 1000);
            }
        } catch (IOException | GeneralSecurityException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compararCorreoPrincipal() {
        try {
            File archivoMail = new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_MAIL_ADMIN);
            String admin = aes.desencriptar(archivoMail);
            if (admin.isEmpty() || !propConfig.getProperty("EmailPrincipal").equals(admin)) {
                if (CuerpoCorreos.enviarCorreoPorCambioEmail(admin.isEmpty() ? "Vacio" : admin,
                        propConfig.getProperty("EmailPrincipal"))) {
                    aes.encriptar(2, propConfig.getProperty("EmailPrincipal"), archivoMail);
                    System.out.println("el correo se mando con éxito");
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
