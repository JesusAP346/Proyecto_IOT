package com.example.proyecto_iot.utils; // O la carpeta que uses para utilidades

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends AsyncTask<Void, Void, Boolean> {

    // !!! ADVERTENCIA CRÍTICA: NUNCA USAR ESTO EN PRODUCCIÓN. SOLO PARA DEMO DE CURSO. !!!
    // Es esencial que estas credenciales NO sean las de tu cuenta personal principal.
    // Usa una cuenta de correo DE PRUEBA/DESECHABLE para esta demo.
    private final String SENDER_EMAIL = "tu_correo_de_demo@gmail.com"; // <-- REEMPLAZA ESTO
    private final String SENDER_PASSWORD = "tu_contraseña_de_aplicacion_o_cuenta"; // <-- REEMPLAZA ESTO
    // !!! FIN ADVERTENCIA CRÍTICA !!!

    private String recipientEmail;
    private String subject;
    private String body;
    private EmailSendListener listener;

    public GMailSender(String recipientEmail, String subject, String body, EmailSendListener listener) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body); // Para texto plano. Si quieres HTML, usa setContent(body, "text/html")

            Transport.send(message);
            Log.d("GMailSender", "Correo enviado con éxito a: " + recipientEmail);
            return true;
        } catch (Exception e) {
            Log.e("GMailSender", "Error al enviar correo: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (listener != null) {
            if (success) {
                listener.onEmailSendSuccess();
            } else {
                listener.onEmailSendFailure();
            }
        }
    }

    public interface EmailSendListener {
        void onEmailSendSuccess();
        void onEmailSendFailure();
    }
}