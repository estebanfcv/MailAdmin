package com.estebanfcv.Util;

import com.estebanfcv.MailAdmin.MailAdmin;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author esteb_000
 */
public class Util {

    public static File obtenerRutaJar() {
        File f = null;
        try {
            f = new File(MailAdmin.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public static void cerrarLecturaEscritura( OutputStream out, InputStream is) {
        try {
            if (out != null) {
                out.close();
            }
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean validarEmail(String email) {
        Pattern pattern;
        pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
