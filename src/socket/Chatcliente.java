package socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Chatcliente {
    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "true");

        String serverAddress = JOptionPane.showInputDialog("Ingresa la IP del servidor (localhost si es local):");
        if (serverAddress == null || serverAddress.isEmpty()) serverAddress = "localhost";

        try (Socket socket = new Socket(serverAddress, 12345)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String sendMessage, receivedMessage;

            do {
                sendMessage = JOptionPane.showInputDialog("Escribe tu mensaje:");
                if (sendMessage == null || sendMessage.equalsIgnoreCase("salir")) {
                    out.println("salir");
                    break;
                }

                out.println(sendMessage);

                receivedMessage = in.readLine();
                if (receivedMessage == null || receivedMessage.equalsIgnoreCase("salir")) {
                    JOptionPane.showMessageDialog(null, "El servidor ha cerrado la conexión.");
                    break;
                }

                JOptionPane.showMessageDialog(null, "Servidor dice: " + receivedMessage);
            } while (true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error en el cliente: " + e.getMessage());
        }
    }
}
