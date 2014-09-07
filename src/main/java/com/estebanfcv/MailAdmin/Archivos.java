package com.estebanfcv.MailAdmin;

import com.estebanfcv.Util.AESCrypt;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import java.io.File;
import javax.swing.JOptionPane;
import static com.estebanfcv.Util.Util.obtenerRutaJar;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author estebanfcv
 */
public class Archivos {

    private File jarDir;
    private AESCrypt aes;

    public Archivos() {
        jarDir = obtenerRutaJar();
        try {
            aes = new AESCrypt(false, "123");
        } catch (GeneralSecurityException | UnsupportedEncodingException ex) {
            Util.agregarDebug(ex);
            ex.printStackTrace();
        }
    }

    public boolean validarArchivos() {
        try {
            if (!encontrarArchivoDebug()) {
                JOptionPane.showMessageDialog(null, "El archivo debug no existe", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (!encontrarArchivoProperties()) {
                JOptionPane.showMessageDialog(null, "El archivo de configuración no existe", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (!encontrarArchivoCorreos()) {
                JOptionPane.showMessageDialog(null, "El archivo de correos no existe", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            return encontrarCarpetaLogs();
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        }
        return false;
    }

    private boolean encontrarCarpetaLogs() {
        File carpeta = new File(jarDir, Constantes.NOMBRE_CARPETA_LOGS);
        if (!carpeta.exists()) {
            JOptionPane.showMessageDialog(null, "La carpeta logs no existe", "MailConfig", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean encontrarArchivoProperties() {
        try {
            if (jarDir != null && jarDir.isDirectory()) {
                return new File(jarDir, Constantes.NOMBRE_ARCHIVO_CONF).exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
            return false;
        }
        return false;
    }

    private boolean encontrarArchivoCorreos() {
        try {
            if (jarDir != null && jarDir.isDirectory()) {
                return new File(jarDir, Constantes.NOMBRE_ARCHIVO_CORREO).exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
            return false;
        }
        return false;
    }

    private boolean encontrarArchivoDebug() {
        try {
            if (jarDir != null && jarDir.isDirectory()) {
                return new File(jarDir, Constantes.NOMBRE_DEBUG).exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean revisarArchivoMailAdmin() {
        try {
            if (jarDir != null && jarDir.isDirectory()) {
                return new File(jarDir, Constantes.NOMBRE_ARCHIVO_MAIL_ADMIN).exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
            return false;
        }
        return false;
    }

    public void crearArchivoMailAdmin() {
        try {
            aes.encriptar(2, "", new File(jarDir, Constantes.NOMBRE_ARCHIVO_MAIL_ADMIN));
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        }
    }

    public void generarLog(Calendar fecha) {
        FileWriter fw = null;
        PrintWriter pw = null;
        File archivoLog;
        try {
            archivoLog = new File(Util.obtenerRutaCarpetaLogs(), String.valueOf(fecha.get(Calendar.DATE)) + ".txt");
            if (!archivoLog.exists()) {
                fw = new FileWriter(archivoLog);
                pw = new PrintWriter(fw);
                pw.append("Log del dia: " + new SimpleDateFormat("dd-MMMMM-yyyy").format(fecha.getTime()));
            }
        } catch (IOException e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        } finally {
            Util.cerrarLecturaEscritura(pw, fw);
        }
    }
}
