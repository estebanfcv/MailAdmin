package com.estebanfcv.Util;

import com.estebanfcv.MailAdmin.MailAdmin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            Util.agregarDebug(e);
        }
        return f;
    }

    public static File obtenerRutaCarpetaLogs() {
        File logs = new File(Util.obtenerRutaJar().getAbsolutePath());
        for (File f : logs.listFiles()) {
            if (f.isDirectory() && f.getName().equals("Logs")) {
                logs = f;
                break;
            }
        }
        logs = new File(logs, new SimpleDateFormat("MMMMM-yyyy").format(Calendar.getInstance().getTime()));
        if (!logs.exists()) {
            logs.mkdir();
        }
        return logs;
    }

    public static void cerrarLecturaEscritura(OutputStream out, InputStream is) {
        try {
            if (out != null) {
                out.close();
            }
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        }
    }

    public static void cerrarLecturaEscritura(PrintWriter pw, FileWriter fw) {
        try {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                fw.close();
            }
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        }
    }

    public static void cerrarLecturaEscritura(BufferedReader br, FileReader fr) {
        try {
            if (br != null) {
                br.close();
            }

            if (fr != null) {
                fr.close();
            }

        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        }
    }

    public static void agregarDebug(Exception e) {
        Writer error = new StringWriter();
        e.printStackTrace(new PrintWriter(error));
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================");
        sb.append(new SimpleDateFormat("EEEEE dd/MMMMM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
        sb.append("=========================\n").append(error.toString());
        sb.append("============================================================================================\n");
        agregarDebug(sb.toString());

    }

    private static void agregarDebug(String debug) {
        File archivoDebug;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            archivoDebug = new File(obtenerRutaJar(), Constantes.NOMBRE_DEBUG);
            fw = new FileWriter(archivoDebug, true);
            pw = new PrintWriter(fw);
            pw.append(debug);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrarLecturaEscritura(pw, fw);
        }
    }

    public static void agregarLog(String log, Calendar fecha) {
        File archivoLog;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            archivoLog = new File(obtenerRutaCarpetaLogs(), String.valueOf(fecha.get(Calendar.DATE)) + ".txt");
            if (archivoLog.exists()) {
                fw = new FileWriter(archivoLog, true);
                pw = new PrintWriter(fw);
                pw.append(log).append("\n");
            }
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        } finally {
            cerrarLecturaEscritura(pw, fw);
        }
    }

    public static void agregarCorreoEnviado(String idCorreo) {
        File archivoEnviados;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            archivoEnviados = new File(obtenerRutaJar(), Constantes.NOMBRE_ENVIADOS);
            if (archivoEnviados.exists()) {
                fw = new FileWriter(archivoEnviados, true);
                pw = new PrintWriter(fw);
                pw.append(idCorreo).append("\n");
            }
        } catch (Exception e) {
            Util.agregarDebug(e);
            e.printStackTrace();
        } finally {
            cerrarLecturaEscritura(pw, fw);
        }
    }

    public static String armarCadenaLog(String log) {
        if (log.isEmpty()) {
            return log;
        }
        return new SimpleDateFormat("dd/MMMMM/yyyy HH:mm:ss").format(new Date()) + " " + log;
    }
}
