package EmpleadoInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que implementa un servidor de chat con interfaz gráfica.
 * Permite recibir conexiones de clientes y enviar/recibir mensajes.
 */
public class Servidor {

    // Componentes de la interfaz gráfica
    private JTextField textField1;          // Campo para escribir mensajes

    /**
     * Botón para enviar mensajes al cliente conectado.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Obtiene el texto del campo de mensaje</li>
     *   <li>Verifica que haya un cliente conectado</li>
     *   <li>Envía el mensaje a través del socket</li>
     *   <li>Muestra el mensaje en el área de chat</li>
     *   <li>Limpia el campo de texto</li>
     * </ol>
     * <p>
     * También se activa al presionar Enter en el campo de texto.
     */
    private JButton enviarButton;          // Botón para enviar mensajes
    private JTextArea textArea1;           // Area para mostrar la conversacion
    private JPanel panel;                  // Panel principal
    /**
     * Botón para iniciar el servidor de chat.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Inicia un nuevo hilo para el servidor</li>
     *   <li>Crea un ServerSocket en el puerto 12345</li>
     *   <li>Espera conexiones entrantes de clientes</li>
     *   <li>Muestra el estado en el área de texto</li>
     * </ol>
     */
    private JButton iniciarServidorButton; // Botón para iniciar el servidor

    // Objetos para la comunicación con el cliente
    private PrintWriter out;               // Flujo de salida para enviar mensajes
    private Socket clientSocket;           // Socket del cliente conectado

    /**
     * Constructor de la clase Servidor.
     * Configura los listeners y la interfaz gráfica.
     */
    public Servidor() {
        // Configurar el área de texto como no editable (solo lectura)
        textArea1.setEditable(false);

        // Listener para el botón de iniciar servidor
        iniciarServidorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Iniciar el servidor en un hilo separado
                new Thread(() -> iniciar()).start();
            }
        });

        // Listener para el botón de enviar mensaje
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        // Listener para detectar la tecla Enter en el campo de texto
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
     * Método para iniciar el servidor y aceptar conexiones de clientes.
     * Se ejecuta en un hilo separado para no bloquear la interfaz gráfica.
     */
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            actualizarTextArea("Servidor iniciado. Esperando conexión...\n");

            // Bucle infinito para aceptar múltiples clientes (uno a la vez)
            while (true) {
                clientSocket = serverSocket.accept();
                actualizarTextArea("Cliente conectado\n");

                // Configurar flujos de entrada/salida
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Hilo para recibir mensajes del cliente
                new Thread(() -> recibirMensajes(in)).start();
            }
        } catch (IOException e) {
            actualizarTextArea("Error en el servidor: " + e.getMessage() + "\n");
        }
    }

    /**
     * Método para enviar mensajes al cliente conectado.
     */
    public void enviarMensaje() {
        String mensaje = textField1.getText();

        if (mensaje != null && !mensaje.isEmpty()) {
            if (out != null) {
                // Enviar mensaje al cliente
                out.println(mensaje);
                actualizarTextArea("Servidor dice: " + mensaje + "\n");
                textField1.setText("");  // Limpiar campo de texto
            } else {
                actualizarTextArea("Error: No hay cliente conectado.\n");
            }
        }
    }

    /**
     * Método para recibir mensajes del cliente.
     * @param in BufferedReader para leer mensajes del cliente
     */
    public void recibirMensajes(BufferedReader in) {
        try {
            String receivedMessage;
            // Bucle para recibir mensajes continuamente
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
            cerrarRecursos();  // Cerrar recursos cuando el cliente se desconecta
        }
    }

    /**
     * Método para cerrar los recursos de conexión.
     */
    private void cerrarRecursos() {
        try {
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            actualizarTextArea("Cliente desconectado. Esperando nueva conexión...\n");
        } catch (IOException e) {
            actualizarTextArea("Error al cerrar recursos: " + e.getMessage() + "\n");
        }
    }

    /**
     * Método para actualizar el área de texto de forma segura desde cualquier hilo.
     * @param mensaje Texto a mostrar en el área de texto
     */
    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    /**
     * Método principal para iniciar la aplicación del servidor.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Servidor de Chat");
        Servidor servidor = new Servidor();
        frame.setContentPane(servidor.panel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Comentado por alguna razón
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}