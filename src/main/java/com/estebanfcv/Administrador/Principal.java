package com.estebanfcv.Administrador;

import com.estebanfcv.MailAdmin.Archivos;
import com.estebanfcv.Util.AESCrypt;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author estebanfcv
 */
public class Principal implements Runnable {

    private Archivos archivo;
    private AESCrypt aes;

    public Principal() {
        try {
            aes = new AESCrypt(false, "123");
        } catch (GeneralSecurityException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            archivo = new Archivos();
            if (!archivo.revisarArchivoMailAdmin()) {
                archivo.crearArchivoMailAdmin();
            }
            compararCorreoPrincipal();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void compararCorreoPrincipal() {
        Properties p;
        String config;
        String admin;
        File archivoMail;
        try {
            config = aes.desencriptar(new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_CONF));
            archivoMail = new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_MAIL_ADMIN);
            admin = aes.desencriptar(archivoMail);
            p = new Properties();
            p.load(new ByteArrayInputStream(config.getBytes()));
            if (admin.isEmpty() || !p.getProperty("EmailPrincipal").equals(admin)) {
                aes.encriptar(2, p.getProperty("EmailPrincipal"), archivoMail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
