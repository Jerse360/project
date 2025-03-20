package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    private JTextField textField1;
    private JButton enviarButton;
    private JTextArea textArea1;
    private JPanel panel;
    private JButton conectarButton;
    private PrintWriter out;
    private Socket socket;

    public Cliente() {
        textArea1.setEditable(false);

        conectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = JOptionPane.showInputDialog("Ingrese la IP del servidor (localhost si es local):");
                if (serverAddress == null || serverAddress.isEmpty()) serverAddress = "localhost";
                String finalServerAddress = serverAddress;
                new Thread(() -> conectarAlServidor(finalServerAddress)).start();
            }
        });

        // Botón para enviar mensajes
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String mensaje = textField1.getText(); // Obtener el mensaje del JTextField
                if (mensaje != null && !mensaje.isEmpty()) {
                    if (out != null) {
                        out.println(mensaje); // Enviar el mensaje al servidor
                        actualizarTextArea("Cliente dice: " + mensaje + "\n");
                        textField1.setText(""); // Limpiar el JTextField
                    } else {
                        actualizarTextArea("Error: No estás conectado al servidor.\n");
                    }
                }
            }
        });
    }

    public void conectarAlServidor(String serverAddress) {
        try {
            socket = new Socket(serverAddress, 12345);
            actualizarTextArea("Conectado al servidor.\n");


            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto-flush para enviar mensajes inmediatamente


            new Thread(() -> recibirMensajes(in)).start();

        } catch (IOException e) {
            actualizarTextArea("Error al conectar al servidor: " + e.getMessage() + "\n");
        }
    }

    public void recibirMensajes(BufferedReader in) {
        try {
            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                if (receivedMessage.equalsIgnoreCase("salir")) {
                    actualizarTextArea("Servidor ha cerrado la conexión.\n");
                    break;
                }
                actualizarTextArea("Servidor dice: " + receivedMessage + "\n");
            }
        } catch (IOException e) {
            actualizarTextArea("Error al recibir mensajes: " + e.getMessage() + "\n");
        }

    }

    
    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente de Chat");
        Cliente cliente = new Cliente();
        frame.setContentPane(cliente.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
