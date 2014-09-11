package com.estebanfcv.MailAdmin;

import com.estebanfcv.Administrador.HiloCorreo;
import com.estebanfcv.conexion.Conexiones;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class MailAdmin {

    public static void main(String[] args) throws UnknownHostException, IOException {
        if (!Conexiones.verificarConexionInternet()) {
            JOptionPane.showMessageDialog(null, "No hay conexión a internet", "MailConfig", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!new Archivos().validarArchivos()) {
            JOptionPane.showMessageDialog(null, "Ejecute el programa MailConfig para crear los archivos necesarios",
                    "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Bienvenido", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
            Thread hiloMail = new Thread(new HiloCorreo(), "HiloMail");
            hiloMail.start();
        }
    }
}
