package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

/**
 * Interfaz gráfica para la gestión de detalles de venta.
 * Permite agregar productos a una venta con diferentes tipos de empaque (Unidad, Blister, Caja)
 * y realizar operaciones CRUD sobre los detalles de venta.
 */
public class DetalleVentaGUI extends JFrame {

    // Componentes de la interfaz gráfica
    private JComboBox comboBoxProducto;  // Combo box para seleccionar productos
    private JTextField precioProducto;   // Campo para mostrar el precio del producto
    private JSpinner spinner1;           // Selector de cantidad
    /**
     * Botón para agregar productos a la venta actual.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Verifica que se haya seleccionado un producto válido</li>
     *   <li>Obtiene el tipo de empaque seleccionado (Unidad, Blister, Caja)</li>
     *   <li>Calcula la cantidad según el factor de empaque (1, 10, 100)</li>
     *   <li>Agrega el detalle de venta a la base de datos</li>
     *   <li>Actualiza el total de la venta</li>
     *   <li>Muestra mensaje de confirmación</li>
     *   <li>Actualiza la tabla de detalles</li>
     * </ol>
     */
    private JButton agregarButton;       // Botón para agregar producto a la venta
    private JTable table1;               // Tabla para mostrar los detalles de venta

    /**
     * Botón para eliminar un detalle de venta seleccionado.
     * <p>
     * Requiere que se seleccione un detalle en la tabla primero.
     * Al hacer clic:
     * <ol>
     *   <li>Resta el monto del detalle del total de la venta</li>
     *   <li>Elimina el registro de la base de datos</li>
     *   <li>Muestra mensaje de confirmación</li>
     *   <li>Actualiza la tabla de detalles</li>
     * </ol>
     */
    private JButton eliminarButton;      // Botón para eliminar detalle de venta
    private JPanel Main;                 // Panel principal de la interfaz
    private JLabel idOrden;              // Etiqueta para mostrar el ID de la venta
    private JComboBox comboBox1;         // Combo box para seleccionar tipo de empaque

    /**
     * Botón para volver a la interfaz de gestión de ventas.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual de detalles de venta</li>
     *   <li>Abre la interfaz principal de ventas</li>
     * </ol>
     * @see VentaGUI
     */
    private JButton volverButton;        // Botón para volver al menú anterior

    // Objetos para conexión y acceso a datos
    Conexion conexion = new Conexion();
    Detalle_ventaDAO detalle_ventaDAO = new Detalle_ventaDAO();

    // Variables de estado
    int precio;         // Precio unitario del producto seleccionado
    int id_venta;       // ID de la venta actual
    int id_producto;    // ID del producto seleccionado
    int id_detalle;     // ID del detalle de venta seleccionado

    /**
     * Constructor de la clase DetalleVentaGUI.
     * Inicializa la interfaz y configura los listeners de los componentes.
     */
    public DetalleVentaGUI() {
        // Configuración inicial de componentes
        precioProducto.setText(null);
        precioProducto.setEditable(false);

        // Carga inicial de datos
        obtenerDatos();
        obtenerComboBox();
        obtenerIdVenta();
        idOrden.setText(String.valueOf(id_venta));

        // Configuración del listener para el botón Agregar
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = String.valueOf(comboBoxProducto.getSelectedItem());

                if(nombreProducto.equals("---")){
                    JOptionPane.showMessageDialog(null,"Por favor seleccione un producto");
                }
                else {
                    String tipo = comboBox1.getSelectedItem().toString();

                    // Procesamiento para tipo Unidad
                    if (tipo.equals("Unidad")){
                        procesarAgregarProducto(nombreProducto, tipo, 1);
                    }

                    // Procesamiento para tipo Blister (10 unidades)
                    if (tipo.equals("Blister")){
                        procesarAgregarProducto(nombreProducto, tipo, 10);
                    }

                    // Procesamiento para tipo Caja (100 unidades)
                    if (tipo.equals("Caja")){
                        procesarAgregarProducto(nombreProducto, tipo, 100);
                    }
                }
            }
        });

        // Listener para cambios en la selección de producto
        comboBoxProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String producto = (String) comboBoxProducto.getSelectedItem();

                if (producto != null) {
                    if (producto.equals("---")) {
                        precioProducto.setText(String.valueOf(0));
                    }
                    else {
                        // Obtener y mostrar el precio del producto seleccionado
                        precio = obtenerPrecio(producto);
                        precioProducto.setText(String.valueOf(precio));
                    }
                }
            }
        });

        // Listener para el botón Eliminar
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Restar el monto del detalle eliminado del total
                detalle_ventaDAO.restar(id_detalle, id_venta);

                if (detalle_ventaDAO.eliminar(id_detalle)) {
                    JOptionPane.showMessageDialog(null, "Pedido eliminado con éxito");
                }
                obtenerDatos(); // Actualizar la tabla
            }
        });

        // Listener para el botón Volver
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la ventana actual y abrir la interfaz de ventas
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                VentaGUI venta = new VentaGUI();
                venta.main(null);
            }
        });

        // Listener para selección de fila en la tabla
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    // Obtener el ID del detalle seleccionado
                    id_detalle = Integer.parseInt(table1.getValueAt(selectedRow,0).toString());
                }
            }
        });
    }

    /**
     * Obtiene el ID de un producto a partir de su nombre.
     * @param nombre Nombre del producto a buscar
     */
    public void obtenerIdProducto(String nombre) {
        Connection con = conexion.getConnection();

        try {
            String query = "SELECT id_producto FROM productos WHERE nombre = ?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                id_producto = rs.getInt(1);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Carga los nombres de productos disponibles en el ComboBox.
     */
    public void obtenerComboBox() {
        Connection con = conexion.getConnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nombre FROM productos");

            while (rs.next()){
                String nombre = rs.getString("nombre");
                comboBoxProducto.addItem(nombre);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el precio de un producto por su nombre.
     * @param nombre Nombre del producto
     * @return Precio del producto
     */
    public int obtenerPrecio(String nombre) {
        Connection con = conexion.getConnection();

        try {
            String query = "SELECT precio FROM productos WHERE nombre = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, nombre);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    precio = rs.getInt("precio");
                }
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Error al obtener el precio: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return precio;
    }

    /**
     * Obtiene el ID de la última venta creada.
     */
    public void obtenerIdVenta() {
        Connection con = conexion.getConnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id_venta) FROM venta");

            if (rs.next()) {
                id_venta = rs.getInt(1);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Carga los detalles de venta en la tabla.
     */
    public void obtenerDatos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id_detalleVenta");
        modelo.addColumn("Id_Venta");
        modelo.addColumn("id_producto");
        modelo.addColumn("Precio Total (IVA)");
        modelo.addColumn("Tipo");
        modelo.addColumn("Cantidad");

        table1.setModel(modelo);

        String[] dato = new String[6];
        Connection con = conexion.getConnection();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id_detalleVenta, id_venta, productos.nombre AS producto, " +
                            "precio_total, tipo, cantidad FROM detalle_venta JOIN productos " +
                            "ON detalle_venta.id_producto = productos.id_producto " +
                            "WHERE id_venta = (SELECT MAX(id_venta) FROM venta);");

            while (rs.next()){
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);
                dato[5] = rs.getString(6);

                modelo.addRow(dato);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Método principal para iniciar la interfaz.
     */
    public static void main() {
        JFrame frame = new JFrame("Pedido");
        frame.setContentPane(new DetalleVentaGUI().Main);
        frame.pack();
        // Maximizar la ventana (pero con bordes visibles)
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    /**
     * Procesa la adición de un producto a la venta.
     * @param nombreProducto Nombre del producto a agregar
     * @param tipo Tipo de empaque (Unidad, Blister, Caja)
     * @param factor Multiplicador para la cantidad según el tipo de empaque
     */
    private void procesarAgregarProducto(String nombreProducto, String tipo, int factor) {
        int cant = (int) spinner1.getValue();
        obtenerIdProducto(nombreProducto);

        Detalle_venta detalle_venta = new Detalle_venta(
                0, id_venta, id_producto, 0, factor * cant, tipo);

        if (detalle_ventaDAO.agregarDetalleVenta(detalle_venta)) {
            JOptionPane.showMessageDialog(null, "Detalle venta agregado");
            detalle_ventaDAO.actualizarTotal(detalle_venta);
            obtenerDatos();
        }
    }
}
