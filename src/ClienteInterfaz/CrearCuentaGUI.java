package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase que proporciona una interfaz gráfica para la creación de cuentas de cliente.
 * Permite registrar nuevos clientes en la base de datos con validación de datos.
 * <p>
 * La interfaz incluye campos para: nombre, cédula, teléfono, dirección y email.
 * Realiza validaciones de formato y existencia previa de cédula/email.
 * </p>
 */
public class CrearCuentaGUI {

    // Componentes de la interfaz gráfica
    private JPanel main;
    private JTextField textField1;  // Campo para nombre
    private JTextField textField2;  // Campo para cédula
    private JTextField textField3;  // Campo para teléfono
    private JTextField textField4;  // Campo para dirección
    private JTextField textField5;  // Campo para email
    private JButton crearCuentaButton;  // Botón para crear cuenta

    // Objetos para manejo de base de datos y validaciones
    Conexion conexion = new Conexion();
    LogGUI log = new LogGUI();

    /**
     * Constructor de la clase CrearCuentaGUI.
     * <p>
     * Configura el ActionListener para el botón de creación de cuenta,
     * que valida y procesa los datos ingresados por el usuario.
     * </p>
     */
    public CrearCuentaGUI() {
        // Listener para el botón de crear cuenta
        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener datos de los campos de texto
                    String nombre = textField1.getText();
                    String cedula = textField2.getText();
                    String telefono = textField3.getText();
                    String direccion = textField4.getText();
                    String email = textField5.getText();

                    // Intentar agregar el cliente a la base de datos
                    if (agregar(nombre, cedula, telefono, direccion, email)) {
                        JOptionPane.showMessageDialog(null, "Cuenta agregada correctamente");

                        // Cerrar la ventana actual
                        JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(crearCuentaButton);
                        volverFrame.dispose();

                        // Abrir la ventana de login
                        LogGUI log = new LogGUI();
                        log.main(null);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos del Cliente");
                }
            }
        });
    }

    /**
     * Método para agregar un nuevo cliente a la base de datos.
     * <p>
     * Realiza las siguientes validaciones:
     * 1. Campos obligatorios no vacíos
     * 2. Formato válido de cédula
     * 3. Cédula no existente previamente
     * 4. Email no existente previamente
     * </p>
     * <p>
     * Si todas las validaciones son exitosas, registra al cliente y envía email de confirmación.
     * </p>
     *
     * @param nombre Nombre completo del cliente (no puede estar vacío)
     * @param cedula Número de cédula del cliente (debe tener formato válido)
     * @param telefono Número de teléfono del cliente (no puede estar vacío)
     * @param direccion Dirección física del cliente (no puede estar vacía)
     * @param email Dirección de correo electrónico del cliente (debe ser único)
     * @return true si el cliente fue agregado exitosamente, false en caso contrario
     */
    public boolean agregar(String nombre, String cedula, String telefono, String direccion, String email) {
        // Validar campos obligatorios
        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            return false;
        }

        // Validar formato de cédula
        if (log.validarCedula(cedula)) {
            // Verificar si la cédula ya existe en la base de datos
            if (log.validarCedulaEnBaseDeDatos(cedula) == false) {
                if (log.validarEmailEnBaseDeDatos(email) == false) {
                    String query = "INSERT INTO cliente (nombre, cedula, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)";

                    try (Connection con = conexion.getConnection();
                         PreparedStatement pst = con.prepareStatement(query)) {

                        // Establecer parámetros para la consulta SQL
                        pst.setString(1, nombre);
                        pst.setString(2, cedula);
                        pst.setString(3, telefono);
                        pst.setString(4, direccion);
                        pst.setString(5, email);

                        // Enviar correo de confirmación
                        Email.enviarCorreo(email);

                        // Ejecutar inserción y retornar resultado
                        return pst.executeUpdate() > 0;

                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }

                }else
                    JOptionPane.showMessageDialog(null, "email ya existe");


            } else {
                JOptionPane.showMessageDialog(null, "Cédula ya existe");
            }
        }
        return false;
    }

    /**
     * Método principal para iniciar la interfaz de creación de cuenta.
     * <p>
     * Crea y muestra la ventana de registro de nuevos clientes.
     * La ventana no cierra la aplicación completamente al ser cerrada.
     * </p>
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configurar y mostrar la ventana principal
        JFrame frame = new JFrame("Crear Cuenta");
        frame.setContentPane(new CrearCuentaGUI().main);
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comentado para evitar cierre completo
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}