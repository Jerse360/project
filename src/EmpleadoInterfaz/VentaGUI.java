package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.TableRowSorter;

/**
 * Interfaz gráfica para la gestión de ventas en el sistema.
 * Permite realizar operaciones CRUD sobre ventas y visualizar registros.
 */
public class VentaGUI {
    private JLabel Venta;
    private JTable table1;
    private JComboBox comboBox1;
    private JPanel Main;
    private JComboBox comboBox2;
    private TableRowSorter<DefaultTableModel> sorter;
    private NonEditableTableModel modelo;
    private JButton agregarButton;
    private JButton pedirButton;
    private JButton volverButton;
    private JScrollPane scroll;
    private JTextField buscar;
    private Conexion conexion = new Conexion();
    private VentaDAO ventaDAO = new VentaDAO();
    private int id_cliente, id_venta, total_venta;
    private boolean cambioManual = true;
    /**
     * Constructor principal que inicializa los componentes y configura los listeners.
     */
    public VentaGUI() {
        obtenerComboBox();
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(modelo);
        table1.setRowSorter(sorter);
        obtenerTabla();

        pedirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame ventaFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                ventaFrame.dispose();
                DetalleVentaGUI detalleVenta = new DetalleVentaGUI();
                detalleVenta.main();
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();
                obtenerIdCliente(cliente);
                Venta venta = new Venta(0, id_cliente, 0, estado, "", cliente);
                if (ventaDAO.agregar(venta)) {
                    JOptionPane.showMessageDialog(null, "Venta agregada exitosamente");
                    obtenerTabla();
                }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame ventaFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                ventaFrame.dispose();
                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cambioManual = false;
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    id_venta = Integer.parseInt(table1.getValueAt(selectedRow, 0).toString());
                    comboBox1.setSelectedItem(table1.getValueAt(selectedRow, 1).toString());
                    comboBox2.setSelectedItem(table1.getValueAt(selectedRow, 3).toString());
                    cambioManual = true;

                    if (e.getClickCount() == 2) {
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
        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!cambioManual) return;

                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();
                obtenerIdCliente(cliente);
                obtenerTotal(id_venta);
                Venta venta = new Venta(id_venta, id_cliente, total_venta, estado, "", cliente);
                if (ventaDAO.actualizar(venta)) {
                    JOptionPane.showMessageDialog(null, "Venta actualizada exitosamente");
                    obtenerTabla();
                }
                obtenerTabla();
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!cambioManual) return;

                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();
                obtenerIdCliente(cliente);
                obtenerTotal(id_venta);
                Venta venta = new Venta(id_venta, id_cliente, total_venta, estado, "", cliente);
                if (ventaDAO.actualizar(venta)) {
                    JOptionPane.showMessageDialog(null, "Venta actualizada exitosamente");
                    obtenerTabla();
                }
                obtenerTabla();
            }
        });
    }

    /**
     * Llena el ComboBox con los nombres de clientes disponibles en la base de datos.
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
     * Carga y muestra los datos de ventas en la tabla principal.
     */
    public void obtenerTabla() {

        if (sorter != null) {
            table1.setRowSorter(null);
        }

        NonEditableTableModel modelo = new NonEditableTableModel();

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
            while (rs.next()) {
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);
                modelo.addRow(dato);
            }

            sorter = new TableRowSorter<>(modelo);
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
     */
    public class NonEditableTableModel extends DefaultTableModel {
        /**
         * Determina si una celda es editable.
         * @param row Índice de fila
         * @param column Índice de columna
         * @return Siempre false para bloquear edición
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * Método principal para ejecutar la interfaz de ventas.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventas");
        frame.setContentPane(new VentaGUI().Main);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}