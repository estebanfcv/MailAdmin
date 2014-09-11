package com.estebanfcv.Administrador;

import com.estebanfcv.MailAdmin.Archivos;
import com.estebanfcv.TO.MessageDestinoTO;
import com.estebanfcv.Util.AESCrypt;
import com.estebanfcv.Util.Cache;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import com.estebanfcv.conexion.Conexiones;
import com.estebanfcv.correo.MailTask;
import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author estebanfcv
 */
public class HiloCorreo implements Runnable {

    private Archivos archivo;
    private AESCrypt aes;
    private Archivos arc;
    private Calendar fecha;
    List<MessageDestinoTO> listaMensajes;
    private boolean corriendo;

    public HiloCorreo() {
        try {
            fecha = Calendar.getInstance();
            aes = new AESCrypt(false, "123");
            Cache.inicializarPropiedades(fecha);
            Cache.inicializarListaCorreos(fecha);
            Cache.inicializarMapaCorreosEnviados(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }

    @Override
    public void run() {
        try {
            corriendo = true;
            while (corriendo) {
                fecha = Calendar.getInstance();
                arc = new Archivos(false);
                arc.generarLog(fecha);
                Util.agregarLog(Util.armarCadenaLog("[INFO] Iniciando proceso..."), fecha);
                if (!Conexiones.verificarConexionInternet(fecha) || !Conexiones.verificarConexionServidor()) {
                    System.out.println("No hay internet");
                    Util.agregarLog(Util.armarCadenaLog("[ERROR] No hay conexión internet o al servidor de correos"), fecha);
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Se volverá a intentar en 10 segundos"), fecha);
                    Thread.sleep(10000);
                    continue;
                } else {
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Conectado"), fecha);
                }
                archivo = new Archivos();
                if (!archivo.revisarArchivoMailAdmin(fecha)) {
                    archivo.crearArchivoMailAdmin(fecha);
                }
                compararCorreoPrincipal();
                leerCorreos();
                if (!listaMensajes.isEmpty()) {
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Se van a enviar los correos obtenidos"), fecha);
                    reenviarCorreos();
                } else {
                    Util.agregarLog(Util.armarCadenaLog("[INFO] No hay correos pendientes de envio"), fecha);
                    System.out.println("La lista esta vacia");
                }
                Util.agregarLog(Util.armarCadenaLog("[INFO] El proceso estará inactivo "
                        + Cache.getPropConfig().getProperty("TiempoEsperaHilo") + " minutos"), fecha);
                for (int i = 0; i < new Integer(Cache.getPropConfig().getProperty("TiempoEsperaHilo")); i++) {
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Durmiendo..."), fecha);
                    Thread.sleep(60000);
                }
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
            if (admin.isEmpty() || !Cache.getPropConfig().getProperty("EmailPrincipal").equals(admin)) {
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

    private void leerCorreos() {
        try {

            listaMensajes = new MailTask(fecha).leerCorreo();
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }

    private boolean reenviarCorreos() {
        boolean envioExitoso = false;
        try {
            for (MessageDestinoTO md : listaMensajes) {
                MailTask mt = new MailTask(md, fecha);
                Thread t = new Thread(mt, "Enviar correo ");
                t.start();
            }
        } catch (Exception e) {
            Util.agregarLog(Util.armarCadenaLog("[ERROR] Hubo un error al enviar los correos"), fecha);
            e.printStackTrace();
            Util.agregarDebug(e);
        }
        return envioExitoso;
    }

    public void detener() {
        corriendo = false;
    }

    public void arrancar() {
        Thread hiloMail = new Thread(this, "HiloMail");
        hiloMail.start();
    }

    public Calendar getFecha() {
        return fecha;
    }
}