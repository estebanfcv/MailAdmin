package com.estebanfcv.conexion;

import com.estebanfcv.Util.Util;
import java.net.Socket;
import java.util.Calendar;

/**
 *
 * @author estebanfcv
 */
public class Conexiones {

    public static boolean verificarConexionInternet(Calendar fecha) {
        Util.agregarLog(Util.armarCadenaLog("Verificando conexión a Internet"), fecha);
        boolean conexion=false;
        try {
            conexion = new Socket("www.google.com", 80).isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarLog(Util.armarCadenaLog(e.getMessage()), fecha);
            Util.agregarDebug(e);
        }
        return conexion;
    }
    public static boolean verificarConexionInternet() {
        boolean conexion=false;
        try {
            conexion = new Socket("www.google.com", 80).isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
    
    public static boolean verificarPuertoCorreo(){
        boolean conexion=false;
        try {
            conexion = new Socket("smtp.gmail.com", 587).isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
        return conexion;
    }

}
