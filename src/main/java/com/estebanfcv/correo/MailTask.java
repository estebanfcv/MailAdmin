package com.estebanfcv.correo;

import com.estebanfcv.Util.Util;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
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
    private boolean leer;

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

    public MailTask(boolean leer) {
        this.leer = leer;
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

    @Override
    public void run() {
        if (!leer) {
            envioExitoso = false;
            if (attachment == null) {
                enviaCorreo();
            } else {
                enviaCorreoConAttachment();
            }
        } else {
            leerCorreo();
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

    private Session getSessionLectura() {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(properties);
        return session;
    }
    
    private void leerCorreo() {
        try {
            Store store = getSessionLectura().getStore("imaps");
            store.connect("imap.gmail.com", "estebanfcv@gmail.com", "estebanfcv090.");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message msg[] = inbox.getMessages();
//            javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
//            for (javax.mail.Folder folder : folders) {
//                if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
//                    System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
//                }
//            }
            for (Message message : msg) {
                System.out.println("TO:"+parseAddresses(message.getRecipients(RecipientType.TO)));
                System.out.println("FROM:" + message.getFrom()[0]);
                System.out.println("SENT DATE:" + message.getSentDate());
                System.out.println("SUBJECT:" + message.getSubject());
                analizaParteDeMensaje(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }

    private static void analizaParteDeMensaje(Part parte) {
        try {
            // Si es multipart, se analiza cada una de sus partes recursivamente.
            if (parte.isMimeType("multipart/*")) {
                Multipart multi;
                multi = (Multipart) parte.getContent();
                for (int j = 0; j < multi.getCount(); j++) {
                    analizaParteDeMensaje(multi.getBodyPart(j));
                }
            } else {
                // Si es texto, se escribe el texto.
                if (parte.isMimeType("text/*")) {
                    System.out.println("Texto " + parte.getContentType());
                    System.out.println(parte.getContent());
                    System.out.println("---------------------------------");
                } else {
                    // Si es imagen, se guarda en fichero y se visualiza en JFrame
                    if (parte.isMimeType("image/*")) {
                        System.out.println(
                                "Imagen " + parte.getContentType());
                        System.out.println("Fichero=" + parte.getFileName());
                        System.out.println("---------------------------------");
                    } else {
                        // Si no es ninguna de las anteriores, se escribe en pantalla
                        // el tipo.
                        System.out.println("Recibido " + parte.getContentType());
                        System.out.println("---------------------------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        private String parseAddresses(Address[] address) {
        String listAddress = "";
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
        return listAddress;
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
