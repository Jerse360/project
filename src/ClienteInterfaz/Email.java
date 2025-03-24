package ClienteInterfaz;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class Email {

    public static void enviarCorreo(String destinoemail, String nombreCliente) {
        final String enviarEmail= "Jefferroacarrillo.JR@gmail.com";  //Pones tu correo si quieres
        final String appContraseña= "dhmn erva ofop jqaj";  //Aca dentro va la contraseña de aplicación de Gmail

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(enviarEmail, appContraseña );
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
