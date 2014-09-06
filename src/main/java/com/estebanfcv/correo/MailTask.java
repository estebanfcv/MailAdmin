package com.estebanfcv.correo;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MailTask implements Runnable {

    private InternetAddress from;
    private InternetAddress[] to;
    private String subject;
    private String text;
    private String mailServer;
    private String password = "";
    private String user = "";
    private String tls = "0";
    private int port;
    private ByteArrayDataSource attachment;
    private boolean envioExitoso;

    /**
     *
     * @param idEmpresa
     * @param to
     * @param cc
     * @param subject
     * @param text
     * @param datosCorreo
     * @param attachment
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    public MailTask(String idEmpresa,
            String to,
            String cc,
            String subject,
            String text,
            String[] datosCorreo,
            ByteArrayDataSource attachment) throws AddressException, UnsupportedEncodingException {
        this.user = datosCorreo[0];
        this.password = datosCorreo[1];
        inicializaTask(to, cc, subject, text, datosCorreo, attachment);
    }

    private void inicializaTask(String to,
            String cc,
            String subject,
            String text,
            String[] datosCorreo,
            ByteArrayDataSource attachment) throws IllegalArgumentException, NullPointerException, AddressException, UnsupportedEncodingException {
        if (to == null) {
            throw new NullPointerException("to es nulo");
        }
        if (to.length() == 0) {
            throw new IllegalArgumentException("to esta vacio");
        }
        if (subject == null) {
            throw new NullPointerException("subject es nulo");
        }
        if (subject.length() == 0) {
            throw new IllegalArgumentException("subject esta vacio");
        }
        if (text == null) {
            throw new NullPointerException("text es nulo");
        }
        if (text.length() == 0) {
            throw new IllegalArgumentException("text esta vacio");
        }
        if (datosCorreo[0] == null) {
            throw new NullPointerException("from es nulo");
        }
        if (datosCorreo[0].length() == 0) {
            throw new IllegalArgumentException("from esta vacio");
        }
        if (datosCorreo[2] == null) {
            throw new NullPointerException("mail server es nulo");
        }
        if (datosCorreo[2].length() == 0) {
            throw new IllegalArgumentException("mail server esta vacio");
        }
        if (!datosCorreo[1].isEmpty()) {
            this.password = datosCorreo[1];
        }
        if (!datosCorreo[4].isEmpty()) {
            this.tls = datosCorreo[4];
        }
        this.from = new InternetAddress(datosCorreo[0]);
        if (cc != null) {
            this.to = new InternetAddress[]{new InternetAddress(to), new InternetAddress(cc)};
        } else {
            System.out.println("La lista de emails es:::::: " + to);
            StringTokenizer token = new StringTokenizer(to, ",");
            int conta = 0;
            this.to = new InternetAddress[token.countTokens()];
            System.out.println("tamanio " + this.to.length);
            while (token.hasMoreTokens()) {
                this.to[conta++] = new InternetAddress(token.nextToken().trim());
            }
        }

        this.subject = subject;
        this.text = text;
        this.mailServer = datosCorreo[2];
        this.port = Integer.parseInt(datosCorreo[3]);
        this.attachment = attachment;
    }

    private void mandarCorreo() {
        envioExitoso = false;
        try {
            if (attachment == null) {
                enviaCorreo();
            } else {
                enviaCorreoConAttachment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        envioExitoso = false;
        if (attachment == null) {
            enviaCorreo();
        } else {
            enviaCorreoConAttachment();
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void enviaCorreoConAttachment() {
        try {
            // create a message
            MimeMessage message = new MimeMessage(getSession());

            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject, "UTF-8");

            // create and fill the first message part
            MimeBodyPart cuerpo = new MimeBodyPart();
            cuerpo.setContent(text, "text/html");

            // create the second message part
            MimeBodyPart archivo = new MimeBodyPart();
            // attach the file to the message
            archivo.setDataHandler(new DataHandler(attachment));
            archivo.setFileName(attachment.getName());

            // create the Multipart and add its parts to it
            Multipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(cuerpo);
            multiPart.addBodyPart(archivo);
            // add the Multipart to the message
            message.setContent(multiPart);
            // set the Date: header
            message.setSentDate(new Date());
            message.saveChanges();
            // send the message
            Transport transport = getSession().getTransport("smtp");
            transport.send(message);
        } catch (MessagingException ex) {
            System.out.println("Exception 2");
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void enviaCorreo() {
        try {
            MimeMessage message = new MimeMessage(getSession());
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject, "UTF-8");
            message.setContent(text, "text/html");
            message.saveChanges();
            Transport transport = getSession().getTransport("smtp");
            transport.send(message);
            envioExitoso = true;
        } catch (Exception e) {
            envioExitoso = false;
            e.printStackTrace();
        }
    }

    private Session getSession() {
        Authenticator authenticator = new Authenticator();
        Properties properties = new Properties();
        properties.put("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", mailServer);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.EnableSSL.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", "smtpserver");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        if (null == tls || "0".equals(tls)) {
            properties.put("mail.smtp.starttls.enable", "false");
        } else {
            properties.put("mail.smtp.starttls.enable", "true");
        }
        Session session = Session.getInstance(properties, authenticator);
        return session;
    }

    private class Authenticator extends javax.mail.Authenticator {

        private PasswordAuthentication authentication;

        public Authenticator() {
            authentication = new PasswordAuthentication(user, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    public boolean isEnvioExitoso() {
        return envioExitoso;
    }
}