package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Interfaz gráfica para la gestión de productos en el sistema.
 */
public class ProductosGUI {
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private TableRowSorter<DefaultTableModel> sorter;
    private NonEditableTableModel model;
    private JTextField txtStockMinimo;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JTable table1;
    private JPanel Main;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton volverButton;
    private JTextField buscar;
    private ProductosDAO productosDAO;

    /**
     * Constructor que inicializa la interfaz y configura los listeners.
     */
    public ProductosGUI() {
        productosDAO = new ProductosDAO();
        textField1.setEditable(false);
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    Productos producto = new Productos(0, nombre, categoria, precio, stock, stockMinimo);

                    if (productosDAO.agregarProducto(producto)) {
                        JOptionPane.showMessageDialog(null, "Producto agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos");
                }
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    if (productosDAO.actualizarProducto(id, nombre, categoria, precio, stock, stockMinimo)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    if (productosDAO.eliminarProducto(id)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });

        obtenerDatos();

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectFilas = table1.getSelectedRow();

                if (selectFilas >= 0) {
                    textField1.setText((String.valueOf(table1.getValueAt(selectFilas, 0))));
                    txtNombre.setText((String.valueOf(table1.getValueAt(selectFilas, 1))));
                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    txtPrecio.setText((String.valueOf(table1.getValueAt(selectFilas, 3))));
                    txtStock.setText((String.valueOf(table1.getValueAt(selectFilas, 4))));
                    txtStockMinimo.setText((String.valueOf(table1.getValueAt(selectFilas, 5))));
                }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();
                MenuGUI menu = new MenuGUI();
                menu.main(null);
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
    }

    /**
     * Obtiene los datos de productos desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        if (sorter != null) {
            table1.setRowSorter(null);
        }

        NonEditableTableModel model = new NonEditableTableModel();
        model.setRowCount(0);
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Categoría");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Stock Mínimo");

        table1.setModel(model);

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM productos")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo")
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
     * Modelo de tabla que impide la edición directa de celdas.
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
     */
    public void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
    }

    /**
     * Método principal para ejecutar la interfaz.
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Productos");
        frame.setContentPane(new ProductosGUI().Main);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}