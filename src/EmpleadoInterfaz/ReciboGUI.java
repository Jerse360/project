package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

/**
 * Clase que representa la interfaz gráfica para mostrar el recibo de una venta.
 * Muestra los detalles de los productos vendidos en una orden específica.
 */
public class ReciboGUI {
    // Componentes de la interfaz gráfica
    public JPanel Main;
    private JTable table1;      // Tabla para mostrar los detalles de la venta
    private JLabel IdOrden;     // Etiqueta para mostrar el ID de la orden
    private JLabel orden;       // Etiqueta adicional (no utilizada directamente en el código mostrado)

    // Objeto para manejar la conexión a la base de datos
    Conexion conexion = new Conexion();

    // Variable para almacenar el ID de la venta que se está mostrando
    int id_venta;

    /**
     * Constructor de la clase ReciboGUI.
     *
     * @param id_venta El ID de la venta cuyos detalles se mostrarán en el recibo
     */
    public ReciboGUI(int id_venta) {
        this.id_venta = id_venta;
        // Mostrar el ID de la orden en la interfaz
        IdOrden.setText(String.valueOf(id_venta));
        // Cargar los datos de la venta
        obtenerDatos();
    }

    /**
     * Método para obtener y mostrar los datos de la venta desde la base de datos.
     * Consulta los detalles de la venta y los muestra en la tabla.
     */
    public void obtenerDatos() {
        // Crear el modelo de tabla con las columnas necesarias
        DefaultTableModel modelo = new DefaultTableModel();

        // Configurar las columnas del recibo
        modelo.addColumn("Id_detalleVenta");
        modelo.addColumn("Id_Venta");
        modelo.addColumn("id_producto");
        modelo.addColumn("Precio Total (IVA)");
        modelo.addColumn("Tipo");
        modelo.addColumn("Cantidad");

        // Asignar el modelo a la tabla
        table1.setModel(modelo);

        // Array para almacenar temporalmente los datos de cada fila
        String[] dato = new String[6];

        // Obtener la conexión a la base de datos
        Connection con = conexion.getConnection();

        try {
            // Consulta SQL para obtener los detalles de la venta
            // Se une con la tabla productos para obtener el nombre del producto
            String query = "SELECT id_detalleVenta, id_venta, productos.nombre AS producto, " +
                    "precio_total, tipo, cantidad FROM detalle_venta " +
                    "JOIN productos ON detalle_venta.id_producto = productos.id_producto " +
                    "WHERE id_venta = ?";

            // Preparar la sentencia SQL con parámetros
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_venta);  // Establecer el parámetro con el ID de venta

            // Ejecutar la consulta y obtener resultados
            ResultSet rs = pst.executeQuery();

            // Recorrer los resultados y añadirlos a la tabla
            while (rs.next()) {
                dato[0] = rs.getString(1);  // id_detalleVenta
                dato[1] = rs.getString(2);  // id_venta
                dato[2] = rs.getString(3);  // nombre del producto
                dato[3] = rs.getString(4);  // precio_total
                dato[4] = rs.getString(5);  // tipo
                dato[5] = rs.getString(6); // cantidad

                // Añadir la fila al modelo de la tabla
                modelo.addRow(dato);
            }
        } catch (SQLException e) {
            // Manejo de errores en la consulta SQL
            e.printStackTrace();
        }
    }
}
