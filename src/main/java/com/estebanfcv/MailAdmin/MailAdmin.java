package com.estebanfcv.MailAdmin;

import com.estebanfcv.Administrador.HiloCorreo;
import com.estebanfcv.Util.Constantes;
import com.estebanfcv.conexion.Conexiones;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class MailAdmin {

    public static void main(String[] args) throws UnknownHostException, IOException {
        int contador = 0;
        if(!Conexiones.verificarConexionInternet()){
            JOptionPane.showMessageDialog(null, "No hay conexión a internet", "MailConfig", JOptionPane.ERROR_MESSAGE);
            return;
        }
        while (contador <= 2) {
            String pass = JOptionPane.showInputDialog(null, "Escriba la contraseña", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
            if (pass == null || pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Escriba la contraseña", "MailAdmin", JOptionPane.WARNING_MESSAGE);
                contador++;
                if (contador > 2) {
                    JOptionPane.showMessageDialog(null, "Bye", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                }
                continue;
            }
            if (pass.equals(Constantes.PASSWORD)) {
                contador = 3;
                if (!new Archivos().validarArchivos()) {
                    JOptionPane.showMessageDialog(null, "Ejecute el programa MailConfig para crear los archivos necesarios",
                            "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Bienvenido", "MailAdmin", JOptionPane.INFORMATION_MESSAGE);
                    Thread hiloMail = new Thread(new HiloCorreo(), "HiloMail");
                    hiloMail.start();
                    System.out.println("Hola");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "MailConfig", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}