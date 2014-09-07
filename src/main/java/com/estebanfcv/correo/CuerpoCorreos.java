package com.estebanfcv.correo;

import com.estebanfcv.Util.Constantes;

/**
 *
 * @author estebanfcv
 */
public class CuerpoCorreos {

    private static boolean enviarCorreo(String asunto, String texto, String to, String cc) {
        boolean envioExitoso = false;
        try {
            MailTask task;
            String[] datosCorreo = new String[5];
            datosCorreo[0] = "estebanfcv.sware@gmail.com";
            datosCorreo[1] = "SwareAdminSupremo";
            datosCorreo[2] = "smtp.gmail.com";
            datosCorreo[3] = "587";
            datosCorreo[4] = "1";

            task = new MailTask("0", //idEmpresa
                    to, //String to
                    cc, //String cc
                    asunto, //String subject
                    texto, //String text
                    datosCorreo, //String[] datosCorreo
                    null);  //attachment
            
            Thread t = new Thread(task);
            t.start();
            while(t.isAlive()){
                Thread.sleep(1000);
            }
            envioExitoso = task.isEnvioExitoso();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envioExitoso;
    }

    public static boolean enviarCorreoPorCambioEmail(String correoViejo, String correoNuevo) {
        String asunto = "Posible fraude en: " + Constantes.NOMBRE_CLIENTE;
        String contenido = Constantes.NOMBRE_CLIENTE + " cambio su cuenta de correo principal: " + correoViejo + " por la siguiente:"
                + "<br>"
                + correoNuevo;
        String html = "<html><body>" + contenido + "</body></html>";
        return enviarCorreo(asunto, html, "estebanfcv@gmail.com", null);
    }

}
