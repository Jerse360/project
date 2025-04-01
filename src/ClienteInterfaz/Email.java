package ClienteInterfaz;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

/**
 * Clase utilitaria para el envío de correos electrónicos desde la aplicación.
 * Utiliza el protocolo SMTP de Gmail para el envío de mensajes.
 */
public class Email {

    /**
     * Envía un correo electrónico de bienvenida al cliente registrado.
     * <p>
     * El método configura una conexión segura con el servidor SMTP de Gmail
     * y envía un mensaje HTML de confirmación de registro.
     * </p>
     *
     * @param destinoemail La dirección de correo electrónico del destinatario.
     *                    No debe ser nulo y debe ser una dirección válida.
     * @throws RuntimeException Si ocurre un error durante el envío del correo.
     *                         La excepción original puede obtenerse mediante getCause().
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
            MimeBodyPart textPart = new MimeBodyPart();
            String htmlMessage = "<p>Tu registro en PharmaSoft ha sido exitoso, gracias por unirte!</p>"
                    + "<p>Saludos,<br>PharmaSoft</p>";
            textPart.setContent(htmlMessage,"text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Correo enviado");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo.");
        }
    }
}