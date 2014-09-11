package com.estebanfcv.correo;

import com.estebanfcv.TO.CorreoTO;
import com.estebanfcv.TO.MessageDestinoTO;
import com.estebanfcv.Util.Cache;
import com.estebanfcv.Util.Util;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
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

    private List<CorreoTO> listaCorreos;
    private InternetAddress from;
    private InternetAddress[] to;
    private String subject;
    private String text;
    private String mailServer;
    private String password = "";
    private String tls = "0";
    private int port;
    private ByteArrayDataSource attachment;
    private boolean envioExitoso;
    private boolean leer;
    private MessageDestinoTO md;
    private Store store;
    private Folder inbox;
    private Calendar fecha;

    /**
     *
     * @param to
     * @param cc
     * @param subject
     * @param text
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    public MailTask(String to, String cc, String subject, String text,
            boolean leer) throws AddressException, UnsupportedEncodingException {
        try {
            this.password = Cache.getPropConfig().getProperty("Password");
            this.leer = leer;
            inicializaTask(to, cc, subject, text, null);
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        }

    }

    public MailTask(Calendar fecha) {
        try {
            this.fecha = fecha;
            Util.agregarLog(Util.armarCadenaLog("[INFO] Estableciendo comunicación con el servidor de correos"), fecha);
            store = getSessionLectura().getStore("imaps");
            store.connect("imap.gmail.com", Cache.getPropConfig().getProperty("EmailPrincipal"),
                    Cache.getPropConfig().getProperty("Password"));
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
        } catch (Exception e) {
            Util.agregarLog(Util.armarCadenaLog("[ERRO] No se pudo establecer comunicación con el servidor de correos"), fecha);
            e.printStackTrace();
            Util.agregarDebug(e);
        }
    }

    public MailTask(MessageDestinoTO md, Calendar fecha) {
        try {
            this.fecha = fecha;
            leer = true;
            this.password = Cache.getPropConfig().getProperty("Password");
            this.tls = Cache.getPropConfig().getProperty("TLS");
            this.from = new InternetAddress(Cache.getPropConfig().getProperty("EmailPrincipal"));
            this.mailServer = Cache.getPropConfig().getProperty("Servidor");
            this.port = Integer.parseInt(Cache.getPropConfig().getProperty("Puerto"));
            store = getSessionLectura().getStore("imaps");
            store.connect("imap.gmail.com", Cache.getPropConfig().getProperty("EmailPrincipal"),
                    Cache.getPropConfig().getProperty("Password"));
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            this.md = md;
        } catch (Exception ex) {
            ex.printStackTrace();
            Util.agregarDebug(ex);
        }

    }

    private void inicializaTask(String to, String cc, String subject, String text,
            ByteArrayDataSource attachment) throws IllegalArgumentException, NullPointerException, AddressException, UnsupportedEncodingException {
        this.tls = Cache.getPropConfig().getProperty("TLS");
        this.from = new InternetAddress(Cache.getPropConfig().getProperty("EmailPrincipal"));
        if (cc != null) {
            this.to = new InternetAddress[]{new InternetAddress(to), new InternetAddress(cc)};
        } else {
            StringTokenizer token = new StringTokenizer(to, ",");
            int conta = 0;
            this.to = new InternetAddress[token.countTokens()];
            while (token.hasMoreTokens()) {
                this.to[conta++] = new InternetAddress(token.nextToken().trim());
            }
        }
        this.subject = subject;
        this.text = text;
        this.mailServer = Cache.getPropConfig().getProperty("Servidor");
        this.port = Integer.parseInt(Cache.getPropConfig().getProperty("Puerto"));
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
            reenviarCorreo();
        }
        try {
            if (inbox != null) {
                inbox.close(false);
            }
            if (store != null) {
                store.close();
            }
        } catch (Exception e) {
            Util.agregarDebug(e);
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

    private void reenviarCorreo() {
        MimeMessage message = new MimeMessage(getSession());
        try {
//            System.out.println("el ID es::::::: " + message.getMessageID());
            Util.agregarLog(Util.armarCadenaLog("[INFO] Datos del correo"), fecha);
            this.from = new InternetAddress(Cache.getPropConfig().getProperty("EmailPrincipal"), "Original "
                    + parseAddresses(md.getMensaje().getFrom()));
            Util.agregarLog(Util.armarCadenaLog("[INFO] De: " + parseAddresses(md.getMensaje().getFrom())), fecha);
            Util.agregarLog(Util.armarCadenaLog("[INFO] Asunto: " + md.getMensaje().getSubject()), fecha);
            Util.agregarLog(Util.armarCadenaLog("[INFO] Fecha de envio: " + md.getMensaje().getSentDate()), fecha);
            Util.agregarLog(Util.armarCadenaLog("[INFO] Se envió a: " + md.getCorreoOrigen()), fecha);
            Util.agregarLog(Util.armarCadenaLog("[INFO] Se enviará a: " + md.getCorreoDestino()), fecha);
            Util.agregarLog(Util.armarCadenaLog(""), fecha);

            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, md.getCorreoDestino());
            message.setSubject(md.getMensaje().getSubject());
            message.setContent((Multipart) md.getMensaje().getContent());
            message.saveChanges();
            Transport transport = getSession().getTransport("smtp");
            transport.send(message);
            md.setEnvioExitoso(true);
        } catch (Exception e) {
            md.setEnvioExitoso(false);
            e.printStackTrace();
            Util.agregarDebug(e);
            try {
                Util.agregarLog(Util.armarCadenaLog("[ERROR] No se pudo mandar el correo: "), fecha);
                Util.agregarLog(Util.armarCadenaLog("[ERROR] De: " + parseAddresses(md.getMensaje().getFrom())), fecha);
                Util.agregarLog(Util.armarCadenaLog("[ERROR] Asunto: " + md.getMensaje().getSubject()), fecha);
                Util.agregarLog(Util.armarCadenaLog("[ERROR] Fecha de envio: " + md.getMensaje().getSentDate()), fecha);
                Util.agregarLog(Util.armarCadenaLog(""), fecha);
            } catch (Exception ex) {
                ex.printStackTrace();
                Util.agregarDebug(e);
            }

        } finally {
            try {
                if (md.isEnvioExitoso()) {
                    Util.agregarLog(Util.armarCadenaLog("[INFO] El correo se envió: "), fecha);
                    Util.agregarLog(Util.armarCadenaLog("[INFO] De: " + parseAddresses(md.getMensaje().getFrom())), fecha);
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Asunto: " + md.getMensaje().getSubject()), fecha);
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Fecha de envio: " + md.getMensaje().getSentDate()), fecha);
                    Util.agregarLog(Util.armarCadenaLog(""), fecha);
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Se agrega el correo al archivo enviados.txt"), fecha);
                    Util.agregarCorreoEnviado(md.getMensaje().getMessageID());
                    Util.agregarLog(Util.armarCadenaLog("[INFO] Se actualiza el archivo enviados.txt"), fecha);
                    Cache.inicializarMapaCorreosEnviados(Calendar.getInstance());
                }
                if (inbox != null) {
                    inbox.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Util.agregarDebug(e);
            }
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
        properties.put("mail.smtp.starttls.enable", null == tls || "0".equals(tls) ? "false" : "true");
        Session session = Session.getInstance(properties, authenticator);
        return session;
    }

    private Session getSessionLectura() {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(properties);
        return session;
    }

    public List<MessageDestinoTO> leerCorreo() {

        List<MessageDestinoTO> listaMensajes = new ArrayList<>();
        try {
            Util.agregarLog(Util.armarCadenaLog("[INFO] Leyendo el archivo correos.ecv"), fecha);
            listaCorreos = Cache.getListaCorreos();
            Util.agregarLog(Util.armarCadenaLog("[INFO] Obteniendo los correos del email principal"), fecha);
            Message arregloCorreos[] = inbox.getMessages();
//            javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
//            for (javax.mail.Folder folder : folders) {
//                if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
//                    System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
//                }
//            }
            Util.agregarLog(Util.armarCadenaLog("[INFO] Se obtendrán los correos que vayan dirigidos a "
                    + "las cuentas origen del archivo correos.ecv y que no se hayan enviado a la cuenta destino"), fecha);
            for (Message message : arregloCorreos) {
                MimeMessage correo = new MimeMessage((MimeMessage) message);
                System.out.println("el id es::::::: " + correo.getMessageID());
                for (CorreoTO c : listaCorreos) {
                    if (Cache.getCorreosEnviados().get(correo.getMessageID()) == null) {
//                    if (parseAddresses(message.getRecipients(RecipientType.TO)).contains(c.getCorreoOrigen())) {
                        MessageDestinoTO mensaje = new MessageDestinoTO();
                        mensaje.setPermisoBorrar(c.getBorrar());
                        mensaje.setMensaje(correo);
                        mensaje.setCorreoDestino(c.getCorreoDestino());
                        mensaje.setCorreoOrigen(c.getCorreoOrigen());
                        Util.agregarLog(Util.armarCadenaLog("[INFO] Se obtuvo el correo " + mensaje.getMensaje().getSubject()),
                                fecha);
                        listaMensajes.add(mensaje);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarLog(Util.armarCadenaLog("[ERROR] No se pudieron leer los correos"), fecha);
            Util.agregarDebug(e);
        }
        return listaMensajes;
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
            for (Address addres : address) {
                listAddress += addres.toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
        return listAddress;
    }

    private class Authenticator extends javax.mail.Authenticator {

        private final PasswordAuthentication authentication;

        public Authenticator() {
            authentication = new PasswordAuthentication(from.getAddress(), password);
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
