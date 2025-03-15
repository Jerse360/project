package Socket;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args)
    {

        String serverAddress = JOptionPane.showInputDialog("Ingrese la IP del servidor (localhost si es local):");
        if (serverAddress == null || serverAddress.isEmpty()) serverAddress = "localhost";

        try (Socket socket = new Socket(serverAddress, 12345)) {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            String sendMessage, receiveMessage;

            do {
                sendMessage = JOptionPane.showInputDialog("Escribe tu mensaje:");
                if (sendMessage == null || sendMessage.equalsIgnoreCase("salir")) {
                    out.println("salir");
                    break;

                }

                out.println(sendMessage);
                out.flush();

                receiveMessage = in.readLine();

                if (receiveMessage == null || receiveMessage.equalsIgnoreCase("salir")) {

                    JOptionPane.showMessageDialog(null,"El servidor ha cerrado la conexion");
                    break;

                }

                JOptionPane.showMessageDialog(null,"servidor dice: "+receiveMessage);
            }while (true);


        }catch (IOException e){

            JOptionPane.showMessageDialog(null,"error en el cliente: "+ e.getMessage());
        }


    }


}
