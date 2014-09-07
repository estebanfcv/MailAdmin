package com.estebanfcv.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author estebanfcv
 */
public class Cache {

    private static Properties propConfig;
    private static AESCrypt aes;
    private static String archivoConfiguracion;

    public static void inicializarPropiedades(Calendar fecha) {
        try {
            aes = new AESCrypt(false, "123");
            propConfig = new Properties();
            Util.agregarLog(Util.armarCadenaLog("Leyendo el archivo de configuracion..."), fecha);
            archivoConfiguracion = aes.desencriptar(new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_CONF));
            propConfig.load(new ByteArrayInputStream(archivoConfiguracion.getBytes()));
            Util.agregarLog(Util.armarCadenaLog("Archivo de configuracion cargado"), fecha);
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarLog(Util.armarCadenaLog(e.getMessage()), fecha);
            Util.agregarDebug(e);
        }
    }

    public static Properties getPropConfig() {
        return propConfig;
    }
}
