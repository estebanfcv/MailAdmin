package com.estebanfcv.Administrador;

import com.estebanfcv.MailAdmin.Archivos;
import com.estebanfcv.Util.AESCrypt;
import com.estebanfcv.Util.Cache;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import com.estebanfcv.conexion.Conexiones;
import com.estebanfcv.correo.CuerpoCorreos;
import com.estebanfcv.correo.MailTask;
import java.io.File;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author estebanfcv
 */
public class HiloCorreo implements Runnable {

    private Archivos archivo;
    private AESCrypt aes;
    private Properties propConfig;
    private Archivos arc;
    private Calendar fecha;

    public HiloCorreo() {
        try {

            fecha = Calendar.getInstance();
            arc = new Archivos();
            arc.generarLog(fecha);
            aes = new AESCrypt(false, "123");
            Cache.inicializarPropiedades(fecha);
            propConfig = Cache.getPropConfig();
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
            Util.agregarLog(Util.armarCadenaLog(e.getMessage()), fecha);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                MailTask task;
                task = new MailTask(true);
                Thread t = new Thread(task);
                t.start();
                Util.agregarLog(Util.armarCadenaLog("Iniciando proceso..."), fecha);
                if (!Conexiones.verificarConexionInternet(fecha)) {
                    System.out.println("No hay internet");
                    Util.agregarLog(Util.armarCadenaLog("No hay internet"), fecha);
                    Thread.sleep(10000);
                    continue;
                } else {
                    Util.agregarLog(Util.armarCadenaLog("Conectado"), fecha);
                }
                archivo = new Archivos();
                if (!archivo.revisarArchivoMailAdmin()) {
                    archivo.crearArchivoMailAdmin();
                }
                compararCorreoPrincipal();
                Thread.sleep(Long.parseLong(propConfig.getProperty("TiempoEsperaHilo")) * 60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
            Util.agregarLog(Util.armarCadenaLog(e.getMessage()), fecha);
        }
    }

    private void compararCorreoPrincipal() {
        try {
            File archivoMail = new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_MAIL_ADMIN);
            String admin = aes.desencriptar(archivoMail);
            if (admin.isEmpty() || !propConfig.getProperty("EmailPrincipal").equals(admin)) {
//                if (CuerpoCorreos.enviarCorreoPorCambioEmail(admin.isEmpty() ? "Vacio" : admin,
//                        propConfig.getProperty("EmailPrincipal"))) {
//                    aes.encriptar(2, propConfig.getProperty("EmailPrincipal"), archivoMail);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }
}
