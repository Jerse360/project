package socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Chatserver {
    public static void main(String[] args) {


        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            JOptionPane.showMessageDialog(null, "Servidor iniciado. Esperando conexión...");

            Socket clientSocket = serverSocket.accept();
            JOptionPane.showMessageDialog(null, "Cliente conectado.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String receivedMessage, sendMessage;

            do {
                receivedMessage = in.readLine();
                if (receivedMessage == null || receivedMessage.equalsIgnoreCase("salir")) {
                    JOptionPane.showMessageDialog(null, "Cliente ha salido del chat.");
                    break;
                }

                JOptionPane.showMessageDialog(null, "Cliente dice: " + receivedMessage);

                sendMessage = JOptionPane.showInputDialog("Escribe tu mensaje:");
                if (sendMessage == null || sendMessage.equalsIgnoreCase("salir")) {
                    out.println("salir");
                    break;
                }

                out.println(sendMessage);
            } while (true);

            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error en el servidor" + e.getMessage());
        }
    }
}