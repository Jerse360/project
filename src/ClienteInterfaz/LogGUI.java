package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que proporciona la interfaz gráfica para el inicio de sesión y creación de cuentas.
 * Permite validar credenciales de usuarios contra la base de datos y redirigir a las interfaces correspondientes.
 */
public class LogGUI {

    // Componentes de la interfaz gráfica
    private JPanel main;                    // Panel principal
    private JButton iniciarSesionButton;    // Botón para iniciar sesión
    private JButton crearCuentaButton;      // Botón para crear nueva cuenta

    // Objeto para manejar la conexión a la base de datos
    private Conexion conexion = new Conexion();

    /**
     * Constructor de la clase LogGUI.
     * Configura los listeners para los botones de la interfaz.
     */
    public LogGUI() {
        // Configuración del listener para el botón de inicio de sesión
        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Solicitar número de cédula al usuario
                String input = JOptionPane.showInputDialog(null, "Ingrese su número de cédula");

                // Validar formato de la cédula
                if (validarCedula(input)) {
                    // Verificar existencia en base de datos
                    if (validarCedulaEnBaseDeDatos(input)) {
                        // Si es válida, abrir interfaz de cliente
                        JFrame log = (JFrame) SwingUtilities.getWindowAncestor(iniciarSesionButton);
                        log.dispose();

                        Cliente cliente = new Cliente();
                        cliente.main(null);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "La cédula " + input + " no se encuentra en la base de datos.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Configuración del listener para el botón de crear cuenta
        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(crearCuentaButton);
                menuFrame.dispose();

                // Abrir interfaz de creación de cuenta
                CrearCuentaGUI crearCuentaGUI = new CrearCuentaGUI();
                crearCuentaGUI.main(null);
            }
        });
    }

    /**
     * Valida si una cédula existe en la base de datos.
     *
     * @param cedula Número de cédula a validar
     * @return true si la cédula existe, false en caso contrario
     */
    public boolean validarCedulaEnBaseDeDatos(String cedula) {
        Connection con = conexion.getConnection();
        String query = "SELECT cedula FROM cliente WHERE cedula = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, cedula);

            try (ResultSet rs = pst.executeQuery()) {

                return rs.next();  // Retorna true si hay al menos un resultado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida el formato de un número de cédula.
     *
     * @param input Cadena con el número de cédula a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public boolean validarCedula(String input) {
        try {
            // Validar entrada vacía o nula
            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Entrada inválida. No se ingresó ningún valor.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Convertir a número y validar rango
            int cedula = Integer.parseInt(input);
            if (cedula <= 0 || cedula < 111111111) {
                JOptionPane.showMessageDialog(null,
                        "Número de cédula inválido. Debe ser un número positivo y tener máximo 9 dígitos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Entrada inválida. Debe ingresar un número entero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Método principal para iniciar la interfaz de login.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configuración de la ventana principal
        JFrame frame = new JFrame("Inicio de sesión");
        frame.setContentPane(new LogGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
