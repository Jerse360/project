package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase que implementa un cliente de chat con interfaz gráfica.
 * Permite conectarse a un servidor de chat, enviar y recibir mensajes.
 */
public class Cliente {

    // Componentes de la interfaz gráfica
    private JTextField textField1;
    private JButton enviarButton;
    private JTextArea textArea1;
    private JPanel panel;
    private JButton conectarButton;

    // Variables para la comunicación con el servidor
    private PrintWriter out;
    private Socket socket;

    /**
     * Constructor de la clase Cliente.
     * Configura los listeners y acciones de los componentes.
     */
    public Cliente() {
        textArea1.setEditable(false);  // El área de texto no es editable directamente

        // Listener para el botón de conectar
        conectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Solicitar dirección IP del servidor
                String serverAddress = JOptionPane.showInputDialog("Ingrese la IP del servidor (localhost si es local):");
                if (serverAddress == null || serverAddress.isEmpty()) {
                    serverAddress = "localhost";  // Valor por defecto
                }
                String finalServerAddress = serverAddress;
                // Conectar al servidor en un hilo separado
                new Thread(() -> conectarAlServidor(finalServerAddress)).start();
            }
        });

        // Listener para el botón de enviar mensaje
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        // Listener para enviar mensaje al presionar Enter
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });
    }

    /**
     * Envía un mensaje al servidor.
     * Verifica la conexión antes de enviar y actualiza el área de texto.
     */
    public void enviarMensaje() {
        String mensaje = textField1.getText(); // Obtener el mensaje del JTextField

        if (mensaje != null && !mensaje.isEmpty()) {
            if (out != null) {
                out.println(mensaje); // Enviar el mensaje al servidor
                actualizarTextArea("Cliente dice: " + mensaje + "\n");
                textField1.setText(""); // Limpiar el campo de texto
            } else {
                actualizarTextArea("Error: No estás conectado al servidor.\n");
            }
        }
    }

    /**
     * Establece conexión con el servidor de chat.
     * @param serverAddress Dirección IP del servidor
     */
    public void conectarAlServidor(String serverAddress) {
        try {
            // Crear socket y conectar al servidor en el puerto 12345
            socket = new Socket(serverAddress, 12345);
            actualizarTextArea("Conectado al servidor.\n");

            // Configurar flujos de entrada/salida
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto-flush activado

            // Iniciar hilo para recibir mensajes
            new Thread(() -> recibirMensajes(in)).start();

        } catch (IOException e) {
            actualizarTextArea("Error al conectar al servidor: " + e.getMessage() + "\n");
        }
    }

    /**
     * Recibe mensajes del servidor de forma continua.
     * @param in BufferedReader para leer mensajes del servidor
     */
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

    /**
     * Actualiza el área de texto de forma segura desde el hilo de eventos.
     * @param mensaje Mensaje a mostrar en el área de texto
     */
    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    /**
     * Método principal que inicia la aplicación cliente.
     */
    public static void main(String[] args) {
        // Configurar y mostrar la ventana principal
        JFrame frame = new JFrame("Cliente de Chat");
        Cliente cliente = new Cliente();
        frame.setContentPane(cliente.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}