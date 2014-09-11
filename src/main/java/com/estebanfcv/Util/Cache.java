package com.estebanfcv.Util;

import com.estebanfcv.TO.CorreoTO;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author estebanfcv
 */
public class Cache {

    private static Properties propConfig;
    private static AESCrypt aes;
    private static String archivoConfiguracion;
    private static List<CorreoTO> listaCorreos;
    private static Map<String, String> correosEnviados;

    public static void inicializarPropiedades(Calendar fecha) {
        try {
            aes = new AESCrypt(false, "123");
            propConfig = new Properties();
            archivoConfiguracion = aes.desencriptar(new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_CONF));
            propConfig.load(new ByteArrayInputStream(archivoConfiguracion.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }

    public static void inicializarListaCorreos(Calendar fecha) {
        try {
            aes = new AESCrypt(false, "123");
            listaCorreos = new ArrayList<>();
            archivoConfiguracion = aes.desencriptar(new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ARCHIVO_CORREO));
            for (StringTokenizer renglon = new StringTokenizer(archivoConfiguracion, "\n"); renglon.hasMoreTokens();) {
                for (StringTokenizer columna = new StringTokenizer(renglon.nextToken(), ":"); columna.hasMoreTokens();) {
                    CorreoTO c = new CorreoTO();
                    c.setCorreoOrigen(columna.nextToken());
                    c.setCorreoDestino(columna.nextToken());
                    c.setBorrar(Integer.parseInt(columna.nextToken()));
                    listaCorreos.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inicializarMapaCorreosEnviados(Calendar fecha) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            correosEnviados = new LinkedHashMap<>();
            File f = new File(Util.obtenerRutaJar(), Constantes.NOMBRE_ENVIADOS);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                correosEnviados.put(linea, linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            Util.cerrarLecturaEscritura(br, fr);
        }
    }

    public static Properties getPropConfig() {
        return propConfig;
    }

    public static List<CorreoTO> getListaCorreos() {
        return listaCorreos;
    }

    public static Map<String, String> getCorreosEnviados() {
        return correosEnviados;
    }
}
