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
            task = new MailTask(to, cc, asunto, texto, false);
            Thread t = new Thread(task);
            t.start();
            while (t.isAlive()) {
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
