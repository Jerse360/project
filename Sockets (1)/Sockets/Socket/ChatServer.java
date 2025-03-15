package Socket;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    public static void main(String[] args)
    {

        try(ServerSocket serverSocket = new ServerSocket(12345))
        {
            JOptionPane.showMessageDialog(null, "servidor iniciado. Esperando conexion...");

            Socket clientSocket = serverSocket.accept();
            JOptionPane.showMessageDialog(null, "Cliente conectado");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

            String receivedMesssage, sendMessage;

            do{
                receivedMesssage = in.readLine();

                if (receivedMesssage == null || receivedMesssage.equalsIgnoreCase("salir"))
                {
                    JOptionPane.showMessageDialog(null, "Cliente ha abandonado el chat");
                    break;
                }

                JOptionPane.showMessageDialog(null, "Cliente dice: " + receivedMesssage);

                sendMessage = JOptionPane.showInputDialog(null, "Escribe tu mensaje");

                if (sendMessage == null || sendMessage.equalsIgnoreCase("salir"))
                {
                    out.println("salir");
                    break;
                }
                out.println(sendMessage);
                out.flush();
            }while (true);

            clientSocket.close();
            serverSocket.close();
        } catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Error en el servidor" + e.getMessage());
        }
    }
}

