
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private JTextField textField1;
    private JButton enviarButton;
    private JTextArea textArea1;
    private JPanel panel;
    private JButton iniciarServidorButton;
    private PrintWriter out;
    private Socket clientSocket;

    public Servidor() {
        textArea1.setEditable(false);

        iniciarServidorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> iniciar()).start();
            }
        });

        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = textField1.getText();
                if (mensaje != null && !mensaje.isEmpty()) {
                    if (out != null) {

                        out.println(mensaje);
                        actualizarTextArea("Servidor dice: " + mensaje + "\n");
                        textField1.setText("");
                    } else {

                        actualizarTextArea("Error: No hay cliente conectado.\n");

                    }
                }
            }
        });
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            actualizarTextArea("Servidor iniciado. Esperando conexión...\n");

            while (true){
            clientSocket = serverSocket.accept();
            actualizarTextArea("Cliente conectado\n");


            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            new Thread(() -> recibirMensajes(in)).start();

            }
        } catch (IOException e) {
            actualizarTextArea("Error en el servidor: " + e.getMessage() + "\n");
        }
    }

    public void recibirMensajes(BufferedReader in) {
        try {
            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                if (receivedMessage.equalsIgnoreCase("salir")) {
                    actualizarTextArea("Cliente ha abandonado el chat\n");
                    break;
                }
                actualizarTextArea("Cliente dice: " + receivedMessage + "\n");
            }
        } catch (IOException e) {
            actualizarTextArea("Error al recibir mensajes: " + e.getMessage() + "\n");
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        try {
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            actualizarTextArea("Cliente desconectado. Esperando nueva conexión...\n");
        } catch (IOException e) {
            actualizarTextArea("Error al cerrar recursos: " + e.getMessage() + "\n");
        }
    }


    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Servidor de Chat");
        Servidor servidor = new Servidor();
        frame.setContentPane(servidor.panel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}