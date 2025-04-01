package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.TableRowSorter;

/**
 * Interfaz gráfica para la gestión de clientes en el sistema.
 * <p>
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre clientes.
 * Incluye funcionalidades para:
 * - Agregar nuevos clientes con validación de datos
 * - Actualizar información de clientes existentes
 * - Eliminar clientes del sistema
 * - Buscar y filtrar clientes en la tabla
 * - Navegar entre la interfaz de clientes y el menú principal
 * </p>
 */
public class ClienteGUI {
    private JTable table1;
    private JPanel Main;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private TableRowSorter<DefaultTableModel> sorter;
    private NonEditableTableModel model;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton volverButton;
    private JTextField buscar;
    private ClienteDAO clienteDAO;

    /**
     * Constructor que inicializa los componentes y configura los listeners.
     * <p>
     * Configura:
     * - La tabla de clientes con ordenamiento y búsqueda
     * - Los listeners para los botones de acción
     * - La carga inicial de datos desde la base de datos
     * </p>
     */
    public ClienteGUI() {
        clienteDAO = new ClienteDAO();
        textField1.setEnabled(false);
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);

        /**
         * Listener para el botón Agregar.
         * <p>
         * Recoge los datos de los campos de texto, valida y agrega un nuevo cliente.
         * Muestra mensajes de éxito o error según el resultado de la operación.
         * </p>
         */
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();

                    ClienteSetGet clienteSetGet = new ClienteSetGet(0, nombre, cedula, telefono, direccion, email);
                    ClienteDAO clienteDAO = new ClienteDAO();

                    if (clienteDAO.agregar(clienteSetGet)) {
                        JOptionPane.showMessageDialog(null, "Cliente agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Email ya existe");
                    }
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos del Cliente");
                }
            }
        });

        /**
         * Listener para el botón Actualizar.
         * <p>
         * Actualiza la información del cliente seleccionado con los datos de los campos de texto.
         * Valida que el ID sea correcto y muestra mensajes de error si la actualización falla.
         * </p>
         */
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();

                    if (clienteDAO.actualizar(id, cedula, nombre, direccion, telefono, email)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar Cliente Email o Cedula ya existe","ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido para actualizar");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        /**
         * Listener para el botón Eliminar.
         * <p>
         * Elimina el cliente seleccionado después de confirmar el ID.
         * Muestra mensajes de éxito o error según el resultado de la operación.
         * </p>
         */
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    if (clienteDAO.eliminar(id)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un Id Válido");
                }
            }
        });

        /**
         * Listener para el botón Volver.
         * <p>
         * Cierra la ventana actual y regresa al menú principal.
         * </p>
         */
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame clienteFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                clienteFrame.dispose();
                MenuGUI menuGUI = new MenuGUI();
                menuGUI.main(null);
            }
        });

        obtenerDatos();

        /**
         * Listener para la selección de filas en la tabla.
         * <p>
         * Al hacer clic en una fila, carga los datos del cliente seleccionado
         * en los campos de texto correspondientes.
         * </p>
         */
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
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

        /**
         * Listener para el campo de búsqueda.
         * <p>
         * Filtra los resultados en la tabla según el texto ingresado,
         * buscando en todas las columnas.
         * </p>
         */
        buscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String buscarText = buscar.getText().trim().toLowerCase();

                if (sorter != null) {
                    RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                if (entry.getStringValue(i).toLowerCase().contains(buscarText)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    /**
     * Obtiene los datos de clientes desde la base de datos y los muestra en la tabla.
     * <p>
     * Consulta todos los clientes registrados y los carga en un modelo de tabla
     * con las columnas: ID, Nombre, Cédula, Teléfono, Dirección y Email.
     * Configura el ordenamiento y filtrado de la tabla.
     * </p>
     */
    public void obtenerDatos() {
        if (sorter != null) {
            table1.setRowSorter(null);
        }

        NonEditableTableModel model = new NonEditableTableModel();
        model.setRowCount(0);
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Cedula");
        model.addColumn("Telefono");
        model.addColumn("Direccion");
        model.addColumn("Email");

        table1.setModel(model);

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cliente")) {

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
            sorter = new TableRowSorter<>(model);
            table1.setRowSorter(sorter);

            if (!buscar.getText().trim().isEmpty()) {
                buscar.setText(buscar.getText());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modelo de tabla personalizado que impide la edición directa de celdas.
     * <p>
     * Extiende DefaultTableModel para deshabilitar la edición de celdas
     * manteniendo todas las demás funcionalidades.
     * </p>
     */
    public class NonEditableTableModel extends DefaultTableModel {
        /**
         * Determina si una celda es editable.
         * @param row el índice de la fila
         * @param column el índice de la columna
         * @return siempre false para bloquear la edición
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * Limpia todos los campos de entrada de datos.
     * <p>
     * Vacía los campos de texto excepto el ID, preparando la interfaz
     * para una nueva operación.
     * </p>
     */
    public void limpiarCampos() {
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        textField6.setText("");
    }

    /**
     * Método principal para ejecutar la interfaz de gestión de clientes.
     * <p>
     * Crea y muestra la ventana principal con las siguientes características:
     * - Título "Gestión de Clientes"
     * - Maximizada pero con bordes visibles
     * - No redimensionable
     * </p>
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Clientes");
        frame.setContentPane(new ClienteGUI().Main);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}