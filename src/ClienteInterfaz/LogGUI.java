package ClienteInterfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que representa la interfaz gráfica de inicio de sesión y registro de usuarios.
 * <p>
 * Proporciona funcionalidad para autenticar usuarios existentes y redirigir a nuevos usuarios
 * al formulario de registro. Incluye validaciones de cédula y email contra la base de datos.
 * </p>
 */
public class LogGUI {
    private JPanel main;
    private JButton iniciarSesionButton;
    private JButton crearCuentaButton;
    private Conexion conexion = new Conexion();

    /**
     * Constructor de la clase LogGUI.
     * <p>
     * Configura la interfaz gráfica con fondo personalizado y botones de acción.
     * Establece los listeners para las operaciones de inicio de sesión y creación de cuenta.
     * </p>
     */
    public LogGUI() {
        // Panel con imagen de fondo escalable
        main = new JPanel() {
            private Image fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        main.setLayout(new BorderLayout());

        // Crear el panel de botones tipo menú en la parte superior
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centrado con espacio entre botones
        topMenu.setOpaque(false); // Hacerlo transparente para que se vea el fondo

        // Configuración de los botones
        iniciarSesionButton = new JButton("Iniciar Sesión");
        crearCuentaButton = new JButton("Crear Cuenta");

        Color buttonColor = Color.decode("#80F3ED");
        JButton[] botones = {iniciarSesionButton,crearCuentaButton};
        for (JButton boton : botones) {
            boton.setBackground(buttonColor);
            boton.setForeground(Color.BLACK); // Texto blanco
            boton.setFocusPainted(false); // Quitar borde de selección al hacer clic
            boton.setBorderPainted(false); // Quitar borde del botón
        }

        topMenu.add(iniciarSesionButton);
        topMenu.add(crearCuentaButton);

        main.add(topMenu, BorderLayout.NORTH);

        // Listeners
        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(null, "Ingrese su número de cédula");
                if (validarCedula(input) && validarCedulaEnBaseDeDatos(input)) {
                    Cliente cliente = new Cliente();
                    cliente.main(null);
                } else {
                    JOptionPane.showMessageDialog(null, "La cédula no está en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(crearCuentaButton);
                menuFrame.dispose();
                CrearCuentaGUI crearCuentaGUI = new CrearCuentaGUI();
                crearCuentaGUI.main(null);
            }
        });
    }

    /**
     * Valida si una cédula existe en la base de datos.
     *
     * @param cedula Número de cédula a validar
     * @return true si la cédula existe en la base de datos, false en caso contrario
     */
    public boolean validarCedulaEnBaseDeDatos(String cedula) {
        Connection con = conexion.getConnection();
        String query = "SELECT cedula FROM cliente WHERE cedula = ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, cedula);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida si un email existe en la base de datos.
     *
     * @param email Dirección de correo electrónico a validar
     * @return true si el email existe en la base de datos, false en caso contrario
     */
    public boolean validarEmailEnBaseDeDatos(String email) {
        Connection con = conexion.getConnection();
        String query = "SELECT email FROM cliente WHERE email = ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Válida el formato de una cédula ingresada.
     * <p>
     * Verifica que la entrada sea un número válido y cumpla con el formato básico.
     * </p>
     *
     * @param input Cadena de texto con la cédula a validar
     * @return true si la cédula tiene un formato válido, false en caso contrario
     */
    public boolean validarCedula(String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Entrada inválida.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            int cedula = Integer.parseInt(input);
            return cedula > 0 && cedula >= 111111111;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Método principal para iniciar la aplicación.
     * <p>
     * Crea y muestra la ventana principal de inicio de sesión.
     * </p>
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Inicio de sesión");
        frame.setContentPane(new LogGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}