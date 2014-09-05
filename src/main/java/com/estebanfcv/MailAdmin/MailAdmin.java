package com.estebanfcv.MailAdmin;

import com.estebanfcv.Administrador.Principal;
import com.estebanfcv.Util.Constantes;
import javax.swing.JOptionPane;

public class MailAdmin {

    public static void main(String[] args) {
        int contador = 0;
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
                    Principal p = new Principal();
                    Thread hiloMail = new Thread(p, "HiloMail");
                    hiloMail.start();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "MailConfig", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
