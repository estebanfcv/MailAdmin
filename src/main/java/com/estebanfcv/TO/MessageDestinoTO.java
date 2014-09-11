package com.estebanfcv.TO;

import javax.mail.internet.MimeMessage;

/**
 *
 * @author estebanfcv
 */
public class MessageDestinoTO {
    
    private MimeMessage mensaje;
    private String correoDestino;
    private String correoOrigen;
    private boolean envioExitoso;
    private int permisoBorrar;

    public MimeMessage getMensaje() {
        return mensaje;
    }

    public void setMensaje(MimeMessage mensaje) {
        this.mensaje = mensaje;
    }

    public String getCorreoDestino() {
        return correoDestino;
    }

    public void setCorreoDestino(String correoDestino) {
        this.correoDestino = correoDestino;
    }

    public boolean isEnvioExitoso() {
        return envioExitoso;
    }

    public void setEnvioExitoso(boolean envioExitoso) {
        this.envioExitoso = envioExitoso;
    }

    public int getPermisoBorrar() {
        return permisoBorrar;
    }

    public void setPermisoBorrar(int permisoBorrar) {
        this.permisoBorrar = permisoBorrar;
    }

    public String getCorreoOrigen() {
        return correoOrigen;
    }

    public void setCorreoOrigen(String correoOrigen) {
        this.correoOrigen = correoOrigen;
    }
    
    
}
