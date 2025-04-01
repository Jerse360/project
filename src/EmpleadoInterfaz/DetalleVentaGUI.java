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
 *
 * <p>La interfaz incluye funcionalidades para:
 * <ul>
 *   <li>Seleccionar productos disponibles</li>
 *   <li>Especificar cantidad y tipo de empaque</li>
 *   <li>Agregar/eliminar detalles de venta</li>
 *   <li>Generar facturas en PDF</li>
 *   <li>Volver al menú principal de ventas</li>
 * </ul>
 *
 */
public class DetalleVentaGUI extends JFrame {

    // Componentes de la interfaz gráfica
    private JComboBox comboBoxProducto;  // Combo box para seleccionar productos disponibles
    private JTextField precioProducto;   // Campo de texto para mostrar el precio unitario del producto seleccionado
    private JSpinner spinner1;           // Selector de cantidad de productos
    private JButton agregarButton;       // Botón para agregar producto a la venta actual
    private JTable table1;               // Tabla para mostrar los detalles de la venta actual
    private JButton eliminarButton;      // Botón para eliminar un detalle de venta seleccionado
    private JPanel Main;                 // Panel principal que contiene todos los componentes
    private JLabel idOrden;              // Etiqueta que muestra el ID de la venta actual
    private JComboBox comboBox1;         // Combo box para seleccionar tipo de empaque (Unidad, Blister, Caja)
    private JButton volverButton;        // Botón para volver al menú de gestión de ventas
    private JButton generarFacturaPDFButton; // Botón para generar factura en PDF y enviarla por correo

    // Objetos para conexión y acceso a datos
    private Conexion conexion = new Conexion();
    private Detalle_ventaDAO detalle_ventaDAO = new Detalle_ventaDAO();

    // Variables de estado
    private int precio;         // Precio unitario del producto seleccionado
    private int id_venta;       // ID de la venta actual
    private int id_producto;    // ID del producto seleccionado
    private int id_detalle;     // ID del detalle de venta seleccionado
    private String fecha;       // Fecha de la venta
    private String nombreCliente; // Nombre del cliente asociado a la venta
    private String email;       // Email del cliente para envío de factura

    /**
     * Constructor de la clase DetalleVentaGUI.
     * Inicializa la interfaz gráfica, configura los componentes
     * y establece los listeners para los eventos.
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

        /**
         * Listener para el botón de generar factura PDF.
         * <p>
         * Al hacer clic:
         * <ol>
         *   <li>Obtiene los datos necesarios del cliente y la venta</li>
         *   <li>Genera un archivo PDF con la factura</li>
         *   <li>Envía la factura por correo electrónico al cliente</li>
         * </ol>
         */
        generarFacturaPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenerarPDF generarPDF = new GenerarPDF();
                ClienteDAO clienteDAO = new ClienteDAO();
                obtenerNombreCliente(id_venta);
                obtenerEmailCliente(id_venta);
                obtenerFecha(id_venta);
                java.util.List<String> productos = detalle_ventaDAO.obtenerProductosPorPedido(id_venta);
                String directorio = System.getProperty("user.home") + "\\FacturasPinillos\\Venta_"+id_venta+".pdf";

                generarPDF.generarFacturaPDF(id_venta,productos,fecha);
                clienteDAO.enviarFacturaPorCorreo(directorio,nombreCliente,email);
            }
        });
    }

    /**
     * Obtiene el ID de un producto a partir de su nombre.
     * @param nombre Nombre del producto a buscar
     */
    private void obtenerIdProducto(String nombre) {
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
     * <p>
     * Los productos se obtienen directamente de la base de datos
     * y se agregan al componente comboBoxProducto.
     */
    private void obtenerComboBox() {
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
     * @param nombre Nombre del producto cuyo precio se desea obtener
     * @return Precio unitario del producto
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos
     */
    private int obtenerPrecio(String nombre) {
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
     * <p>
     * Este método se usa para asociar los nuevos detalles de venta
     * a la venta actual.
     */
    private void obtenerIdVenta() {
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
     * Obtiene la fecha de una venta específica.
     * @param id_venta ID de la venta cuya fecha se desea obtener
     * @return Fecha de la venta en formato String
     */
    private String obtenerFecha(int id_venta) {
        Connection con = conexion.getConnection();

        try {
            String query = "SELECT fecha_hora FROM venta WHERE id_venta = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_venta);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    fecha = rs.getString("fecha_hora");
                }
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Error al obtener la fecha: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return fecha;
    }

    /**
     * Obtiene el nombre del cliente asociado a una venta.
     * @param id_venta ID de la venta para la que se busca el cliente
     */
    private void obtenerNombreCliente(int id_venta) {
        Connection con = conexion.getConnection();
        try {
            String query = "SELECT cliente.nombre FROM cliente JOIN venta ON cliente.id_cliente =venta.id_cliente WHERE venta.id_venta = ?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id_venta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreCliente = rs.getString("nombre");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el email del cliente asociado a una venta.
     * @param id_venta ID de la venta para la que se busca el email
     */
    private void obtenerEmailCliente(int id_venta) {
        Connection con = conexion.getConnection();
        try {
            String query = "SELECT cliente.email FROM cliente JOIN venta ON cliente.id_cliente =venta.id_cliente WHERE venta.id_venta = ?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, id_venta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Carga los detalles de venta en la tabla.
     * <p>
     * Obtiene los detalles de la venta actual desde la base de datos
     * y los muestra en la tabla con el siguiente formato:
     * <ul>
     *   <li>ID Detalle Venta</li>
     *   <li>ID Venta</li>
     *   <li>Producto</li>
     *   <li>Precio Total (con IVA)</li>
     *   <li>Tipo de empaque</li>
     *   <li>Cantidad</li>
     * </ul>
     */
    private void obtenerDatos() {
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
     * <p>
     * Crea y muestra la ventana de gestión de detalles de venta,
     * maximizada y con bordes visibles.
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
     * Procesa la adición de un producto a la venta actual.
     * <p>
     * Realiza las siguientes acciones:
     * <ol>
     *   <li>Valida el stock disponible</li>
     *   <li>Crea un nuevo detalle de venta</li>
     *   <li>Agrega el detalle a la base de datos</li>
     *   <li>Actualiza el total de la venta</li>
     *   <li>Refresca la tabla de detalles</li>
     * </ol>
     *
     * @param nombreProducto Nombre del producto a agregar
     * @param tipo Tipo de empaque (Unidad, Blister, Caja)
     * @param factor Multiplicador para la cantidad según el tipo de empaque
     */
    private void procesarAgregarProducto(String nombreProducto, String tipo, int factor) {
        int cant = (int) spinner1.getValue();
        obtenerIdProducto(nombreProducto);
        VentaDAO ventaDAO = new VentaDAO();
        if (ventaDAO.validarStock(id_producto,cant*factor)){

            Detalle_venta detalle_venta = new Detalle_venta(
                    0, id_venta, id_producto, 0, factor * cant, tipo);

            if (detalle_ventaDAO.agregarDetalleVenta(detalle_venta)) {
                JOptionPane.showMessageDialog(null, "Detalle venta agregado");
                detalle_ventaDAO.actualizarTotal(detalle_venta);
                obtenerDatos();
            }
        }
    }
}
