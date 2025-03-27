import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Interfaz gráfica para la gestión de clientes.
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los clientes.
 */
public class ClienteGUI {

    // Componentes de la interfaz gráfica
    private JTable table1;                // Tabla para mostrar los clientes
    private JPanel Main;                  // Panel principal
    private JTextField textField1;        // Campo para ID (no editable)
    private JTextField textField2;        // Campo para Nombre
    private JTextField textField3;        // Campo para Cédula
    private JTextField textField4;        // Campo para Teléfono
    private JTextField textField5;        // Campo para Dirección
    private JTextField textField6;        // Campo para Email
    private JButton agregarButton;        // Botón para agregar cliente
    private JButton actualizarButton;     // Botón para actualizar cliente
    private JButton eliminarButton;       // Botón para eliminar cliente
    private JButton volverButton;         // Botón para volver al menú
    private ClienteDAO clienteDAO;        // Objeto para acceso a datos de clientes

    /**
     * Constructor de la clase ClienteGUI.
     * Configura los listeners y carga los datos iniciales.
     */
    public ClienteGUI() {
        clienteDAO = new ClienteDAO();
        textField1.setEnabled(false);  // El campo ID no es editable

        // Configuración del listener para el botón Agregar
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener datos de los campos de texto
                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();

                    // Crear objeto cliente y agregarlo a la base de datos
                    ClienteSetGet clienteSetGet = new ClienteSetGet(0, nombre, cedula, telefono, direccion, email);
                    ClienteDAO clienteDAO = new ClienteDAO();

                    if (clienteDAO.agregar(clienteSetGet)) {
                        JOptionPane.showMessageDialog(null, "Cliente agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();  // Actualizar tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos del Cliente");
                }
            }
        });

        // Configuración del listener para el botón Actualizar
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener datos de los campos de texto
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();

                    if (clienteDAO.actualizar(id, cedula, nombre, direccion, telefono, email)) {
                        limpiarCampos();
                        obtenerDatos();  // Actualizar tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido para actualizar");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Configuración del listener para el botón Eliminar
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    if (clienteDAO.eliminar(id)) {
                        limpiarCampos();
                        obtenerDatos();  // Actualizar tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un Id Válido");
                }
            }
        });

        // Configuración del listener para el botón Volver
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar ventana actual
                JFrame clienteFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                clienteFrame.dispose();

                // Abrir menú principal
                MenuGUI menuGUI = new MenuGUI();
                menuGUI.main(null);
            }
        });

        // Cargar datos iniciales y configurar selección en tabla
        obtenerDatos();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // Llenar campos con datos del cliente seleccionado
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    textField1.setText(String.valueOf(table1.getValueAt(fila, 0).toString()));
                    textField2.setText(String.valueOf(table1.getValueAt(fila, 1).toString()));
                    textField3.setText(String.valueOf(table1.getValueAt(fila, 2).toString()));
                    textField4.setText(String.valueOf(table1.getValueAt(fila, 3).toString()));
                    textField5.setText(String.valueOf(table1.getValueAt(fila, 4).toString()));
                    textField6.setText(String.valueOf(table1.getValueAt(fila, 5).toString()));
                }
            }
        });
    }

    /**
     * Obtiene los datos de clientes desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);  // Limpiar tabla
        // Configurar columnas
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Cedula");
        model.addColumn("Telefono");
        model.addColumn("Direccion");
        model.addColumn("Email");

        table1.setModel(model);  // Asignar modelo a la tabla

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cliente")) {

            // Llenar tabla con datos de la consulta
            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email")
                };
                model.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpia los campos de texto del formulario.
     */
    public void limpiarCampos() {
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        textField6.setText("");
    }

    /**
     * Método principal para iniciar la interfaz de gestión de clientes.
     */
    public static void main(String[] args) {
        // Configuración de la ventana principal
        JFrame frame = new JFrame("Gestión de Clientes");
        frame.setContentPane(new ClienteGUI().Main);
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comentado para evitar cierre completo
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}