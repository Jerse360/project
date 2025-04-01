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
 *
 * <p>Proporciona funcionalidades para:
 * <ul>
 *   <li>Agregar nuevos productos al inventario</li>
 *   <li>Actualizar información de productos existentes</li>
 *   <li>Eliminar productos del sistema</li>
 *   <li>Visualizar y buscar productos</li>
 *   <li>Gestionar niveles de stock</li>
 * </ul>
 */
public class ProductosGUI {
    // Componentes de la interfaz
    private JTextField txtNombre;            // Campo para nombre del producto
    private JTextField txtPrecio;           // Campo para precio del producto
    private JTextField txtStock;            // Campo para cantidad en stock
    private TableRowSorter<DefaultTableModel> sorter; // Ordenador para la tabla
    private NonEditableTableModel model;    // Modelo de tabla no editable
    private JTextField txtStockMinimo;      // Campo para stock mínimo requerido
    private JButton agregarButton;          // Botón para agregar productos
    private JButton actualizarButton;       // Botón para actualizar productos
    private JButton eliminarButton;         // Botón para eliminar productos
    private JTable table1;                  // Tabla para mostrar productos
    private JPanel Main;                    // Panel principal
    private JComboBox comboBox1;            // Combo box para categorías
    private JTextField textField1;          // Campo para ID del producto
    private JButton volverButton;           // Botón para volver al menú
    private JTextField buscar;              // Campo para búsqueda de productos
    private ProductosDAO productosDAO;      // Objeto para acceso a datos de productos

    /**
     * Constructor que inicializa la interfaz y configura los listeners.
     *
     * <p>Realiza las siguientes configuraciones:
     * <ol>
     *   <li>Inicializa el DAO para productos</li>
     *   <li>Configura la tabla con modelo no editable</li>
     *   <li>Establece el ordenador de filas</li>
     *   <li>Carga los datos iniciales</li>
     *   <li>Configura todos los listeners necesarios</li>
     * </ol>
     */
    public ProductosGUI() {
        productosDAO = new ProductosDAO();
        textField1.setEditable(false);
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);

        /**
         * Listener para el botón Agregar.
         * <p>
         * Valida los datos y agrega un nuevo producto al sistema.
         */
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

        /**
         * Listener para el botón Actualizar.
         * <p>
         * Actualiza un producto existente con los nuevos valores.
         */
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

        /**
         * Listener para el botón Eliminar.
         * <p>
         * Elimina el producto seleccionado del sistema.
         */
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

        /**
         * Listener para eventos de ratón en la tabla.
         * <p>
         * Al seleccionar una fila, carga los datos del producto en los campos correspondientes.
         */
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

        /**
         * Listener para el botón Volver.
         * <p>
         * Regresa al menú principal.
         */
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();
                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });

        /**
         * Listener para el campo de búsqueda.
         * <p>
         * Filtra los productos según el texto ingresado.
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
     * Obtiene los datos de productos desde la base de datos y los muestra en la tabla.
     *
     * <p>Las columnas mostradas son:
     * <ol>
     *   <li>ID</li>
     *   <li>Nombre</li>
     *   <li>Categoría</li>
     *   <li>Precio</li>
     *   <li>Stock</li>
     *   <li>Stock Mínimo</li>
     * </ol>
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
     * Modelo de tabla personalizado que impide la edición directa de celdas.
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
     * @param args argumentos de línea de comandos (no utilizados)
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