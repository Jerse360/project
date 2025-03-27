package ClienteInterfaz;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Clase que gestiona el envío de correos electrónicos.
 */
public class Email {

    /**
     * Envía un correo electrónico de bienvenida al cliente.
     *
     * @param destinoemail La dirección de correo electrónico del cliente.
     */
    public static void enviarCorreo(String destinoemail) {
        final String enviarEmail = "Jefferroacarrillo.JR@gmail.com";
        final String appContrasena = "dhmn erva ofop jqaj";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(enviarEmail, appContrasena);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(enviarEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinoemail));
            message.setSubject("Bienvenido!");
            message.setText("Registro exitoso.");

            Transport.send(message);
            System.out.println("Correo enviado");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo.");
        }
    }
}