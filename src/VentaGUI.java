import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

/**
 * Clase que representa la interfaz gráfica para la gestión de ventas.
 * Permite agregar, actualizar y visualizar ventas, así como acceder a detalles de venta.
 */
public class VentaGUI {
    // Componentes de la interfaz gráfica
    private JLabel Venta;                // Etiqueta del título
    private JTable table1;               // Tabla para mostrar las ventas
    private JComboBox comboBox1;         // ComboBox para seleccionar clientes
    private JPanel Main;                 // Panel principal
    private JComboBox comboBox2;         // ComboBox para seleccionar estado de venta
    private JButton agregarButton;       // Botón para agregar ventas
    private JButton actualizarButton;    // Botón para actualizar ventas
    private JButton pedirButton;         // Botón para acceder a detalles de venta
    private JButton volverButton;        // Botón para volver al menú principal
    private JScrollPane scroll;          // Panel de desplazamiento para la tabla

    // Objetos para conexión y acceso a datos
    Conexion conexion = new Conexion();
    VentaDAO ventaDAO = new VentaDAO();

    // Variables para almacenar datos de la venta
    int id_cliente, id_venta, total_venta;

    /**
     * Constructor de la clase VentaGUI.
     * Configura los listeners y componentes iniciales.
     */
    public VentaGUI() {
        // Inicializar componentes
        obtenerTabla();
        obtenerComboBox();
        table1.setRowSelectionAllowed(true);  // Permitir selección de filas

        // Listener para el botón de pedido/detalle
        pedirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar ventana actual y abrir interfaz de detalle de venta
                JFrame ventaFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                ventaFrame.dispose();

                DetalleVentaGUI detalleVenta = new DetalleVentaGUI();
                detalleVenta.main();
            }
        });

        // Listener para el botón de agregar venta
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener datos de los combobox
                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();

                obtenerIdCliente(cliente);
                Venta venta = new Venta(0, id_cliente, 0, estado, "", cliente);

                // Intentar agregar la venta
                if (ventaDAO.agregar(venta)) {
                    JOptionPane.showMessageDialog(null, "Venta agregada exitosamente");
                    obtenerTabla();  // Actualizar tabla
                }
            }
        });

        // Listener para el botón de actualizar venta
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener datos de los combobox
                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();

                // Obtener IDs y total
                obtenerIdCliente(cliente);
                obtenerTotal(id_venta);

                // Crear objeto venta
                Venta venta = new Venta(id_venta, id_cliente, total_venta, estado, "", cliente);

                // Intentar actualizar la venta
                if (ventaDAO.actualizar(venta)) {
                    JOptionPane.showMessageDialog(null, "Venta actualizada exitosamente");
                    obtenerTabla();  // Actualizar tabla
                }

                obtenerTabla();
            }
        });

        // Listener para el botón de volver al menú
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar ventana actual y abrir menú principal
                JFrame ventaFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                ventaFrame.dispose();

                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });

        // Listener para la tabla (selección y doble clic)
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Obtener la fila seleccionada
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) { // Verificar selección válida
                    // Obtener valores de la fila seleccionada
                    id_venta = Integer.parseInt(table1.getValueAt(selectedRow, 0).toString());
                    comboBox1.setSelectedItem(table1.getValueAt(selectedRow, 1).toString());
                    comboBox2.setSelectedItem(table1.getValueAt(selectedRow, 3).toString());

                    // Verificar doble clic para mostrar recibo
                    if (e.getClickCount() == 2) {
                        // Mostrar recibo de la venta seleccionada
                        ReciboGUI reciboGUI = new ReciboGUI(id_venta);

                        JFrame frame = new JFrame("Recibo");
                        frame.setContentPane(reciboGUI.Main);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.pack();
                        frame.setSize(700, 700);
                        frame.setResizable(true);
                        frame.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Llena el ComboBox1 con los nombres de los clientes desde la base de datos.
     */
    public void obtenerComboBox() {
        Connection con = conexion.getConnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nombre FROM cliente");

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                comboBox1.addItem(nombre);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el ID de un cliente basado en su nombre.
     * @param nombre Nombre del cliente a buscar
     */
    public void obtenerIdCliente(String nombre) {
        Connection con = conexion.getConnection();

        try {
            String query = "SELECT id_cliente FROM cliente WHERE nombre = ?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                id_cliente = rs.getInt("id_cliente");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el total de una venta específica.
     * @param id_venta ID de la venta a consultar
     */
    public void obtenerTotal(int id_venta) {
        Connection con = conexion.getConnection();

        try {
            String query = "SELECT total_venta FROM venta WHERE id_venta = ?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id_venta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total_venta = rs.getInt("total_venta");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene y muestra en la tabla todas las ventas registradas.
     */
    public void obtenerTabla() {
        // Modelo de tabla con columna ID no editable
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Columna 0 (ID) no editable
            }
        };

        // Configurar columnas de la tabla
        modelo.addColumn("Id_venta");
        modelo.addColumn("Id_cliente");
        modelo.addColumn("Total_Venta");
        modelo.addColumn("Estado");
        modelo.addColumn("Fecha y hora");

        table1.setModel(modelo);

        String[] dato = new String[5];
        Connection con = conexion.getConnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT venta.id_venta, cliente.nombre AS cliente, venta.total_venta, venta.estado, venta.fecha_hora FROM venta JOIN cliente ON venta.id_cliente = cliente.id_cliente;");

            // Llenar tabla con datos de la consulta
            while (rs.next()) {
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);

                modelo.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal para ejecutar la interfaz de ventas.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventas");
        frame.setContentPane(new VentaGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comentado por alguna razón
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}

