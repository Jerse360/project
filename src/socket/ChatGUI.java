package socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatGUI extends JFrame {
    private JButton enviarButton;
    private JTextField textField1;
    private JTextArea textArea1;
    private PrintWriter out;
    private BufferedReader in;

    public ChatGUI() {
        setTitle("Cliente Chat");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        add(new JScrollPane(textArea1), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        textField1 = new JTextField();
        enviarButton = new JButton("Enviar");

        panelInferior.add(textField1, BorderLayout.CENTER);
        panelInferior.add(enviarButton, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        enviarButton.addActionListener(e -> enviarMensaje());
        textField1.addActionListener(e -> enviarMensaje());

        conectarServidor(); // Iniciar conexión con el servidor

        setVisible(true);
    }

    private void conectarServidor() {
        try {
            String serverAddress = JOptionPane.showInputDialog("Ingresa la IP del servidor (localhost):");
            if (serverAddress == null || serverAddress.isEmpty()) {
                serverAddress = "localhost";
            }

            Socket socket = new Socket(serverAddress, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            textArea1.append("Conectado al servidor.\n");

            new Thread(() -> {
                try {
                    String receivedMessage;
                    while ((receivedMessage = in.readLine()) != null) {
                        textArea1.append("Servidor: " + receivedMessage + "\n");
                    }
                } catch (Exception e) {
                    textArea1.append("Conexión cerrada.\n");
                }
            }).start();

        } catch (Exception e) {
            textArea1.append("Error al conectar con el servidor.\n");
        }
    }

    private void enviarMensaje() {
        String mensaje = textField1.getText().trim();
        if (!mensaje.isEmpty() && out != null) {
            out.println(mensaje); // Enviar mensaje al servidor
            textArea1.append("Tú: " + mensaje + "\n");
            textField1.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}
