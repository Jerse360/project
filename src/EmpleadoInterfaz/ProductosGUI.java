package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase que representa la interfaz gráfica para la gestión de productos.
 * Permite agregar, actualizar, eliminar y visualizar productos en una base de datos.
 */
public class ProductosGUI {

    // Componentes de la interfaz gráfica
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    /**
     * Botón para agregar nuevos productos al sistema.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Recoge los datos de los campos del formulario</li>
     *   <li>Crea un nuevo objeto Producto</li>
     *   <li>Intenta guardarlo en la base de datos</li>
     *   <li>Muestra mensaje de confirmación o error</li>
     *   <li>Limpia los campos y actualiza la tabla</li>
     * </ol>
     */
    private JButton agregarButton;

    /**
     * Botón para modificar productos existentes.
     * <p>
     * Requiere que se seleccione un producto de la tabla primero.
     * Al hacer clic:
     * <ol>
     *   <li>Obtiene el ID del producto seleccionado</li>
     *   <li>Recoge los nuevos datos de los campos</li>
     *   <li>Actualiza el registro en la base de datos</li>
     *   <li>Muestra mensaje de confirmación o error</li>
     *   <li>Limpia los campos y actualiza la tabla</li>
     * </ol>
     */
    private JButton actualizarButton;

    /**
     * Botón para eliminar productos del sistema.
     * <p>
     * Requiere que se seleccione un producto de la tabla primero.
     * Al hacer clic:
     * <ol>
     *   <li>Obtiene el ID del producto seleccionado</li>
     *   <li>Elimina el registro de la base de datos</li>
     *   <li>Muestra mensaje de confirmación o error</li>
     *   <li>Limpia los campos y actualiza la tabla</li>
     * </ol>
     */
    private JButton eliminarButton;
    private JTable table1;
    private JPanel Main;
    private JComboBox comboBox1;
    private JTextField textField1;  // Campo para el ID (no editable)

    /**
     * Botón para volver al menú principal.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual de gestión de productos</li>
     *   <li>Abre la interfaz del menú principal</li>
     * </ol>
     * @see MenuGUI
     */
    private JButton volverButton;

    // Objeto para acceder a la capa de datos de productos
    private ProductosDAO productosDAO;

    /**
     * Constructor de la clase ProductosGUI.
     * Inicializa los componentes y configura los listeners para los eventos.
     */
    public ProductosGUI() {
        productosDAO = new ProductosDAO();
        textField1.setEditable(false);  // El campo ID no es editable directamente

        // Listener para el botón Agregar
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener los valores de los campos de texto
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    // Crear un nuevo objeto Producto
                    Productos producto = new Productos(0, nombre, categoria, precio, stock, stockMinimo);

                    // Intentar agregar el producto a la base de datos
                    if (productosDAO.agregarProducto(producto)) {
                        JOptionPane.showMessageDialog(null, "Producto agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();  // Actualizar la tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos");
                }
            }
        });

        // Listener para el botón Actualizar
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtener los valores de los campos de texto
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    // Intentar actualizar el producto en la base de datos
                    if (productosDAO.actualizarProducto(id, nombre, categoria, precio, stock, stockMinimo)) {
                        limpiarCampos();
                        obtenerDatos();  // Actualizar la tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });

        // Listener para el botón Eliminar
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    // Intentar eliminar el producto de la base de datos
                    if (productosDAO.eliminarProducto(id)) {
                        limpiarCampos();
                        obtenerDatos();  // Actualizar la tabla
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });

        // Cargar los datos iniciales en la tabla
        obtenerDatos();

        // Listener para la tabla (selección de filas)
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int selectFilas = table1.getSelectedRow();

                if (selectFilas >= 0) {
                    // Rellenar los campos con los datos de la fila seleccionada
                    textField1.setText((String.valueOf(table1.getValueAt(selectFilas, 0))));
                    txtNombre.setText((String.valueOf(table1.getValueAt(selectFilas, 1))));
                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    txtPrecio.setText((String.valueOf(table1.getValueAt(selectFilas, 3))));
                    txtStock.setText((String.valueOf(table1.getValueAt(selectFilas, 4))));
                    txtStockMinimo.setText((String.valueOf(table1.getValueAt(selectFilas, 5))));
                }
            }
        });

        // Listener para el botón Volver
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la ventana actual
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                // Abrir el menú principal
                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });
    }

    /**
     * Obtiene los datos de los productos desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        // Configurar el modelo de la tabla
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);  // Limpiar la tabla
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Categoría");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Stock Mínimo");

        table1.setModel(model);

        // Obtener conexión a la base de datos
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        // Consultar los productos en la base de datos
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM productos")) {

            // Llenar la tabla con los resultados
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpia los campos de texto del formulario.
     */
    public void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
    }

    /**
     * Método principal para ejecutar la interfaz de gestión de productos.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configurar y mostrar la ventana principal
        JFrame frame = new JFrame("Gestión de Productos");
        frame.setContentPane(new ProductosGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Comentado por alguna razón
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
