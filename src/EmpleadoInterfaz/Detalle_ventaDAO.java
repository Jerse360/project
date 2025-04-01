package EmpleadoInterfaz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Data Access Object (DAO) para gestionar operaciones CRUD de detalles de venta.
 * <p>
 * Proporciona métodos para interactuar con la tabla detalle_venta en la base de datos,
 * incluyendo operaciones para agregar, actualizar y eliminar detalles de venta, así como
 * consultas para obtener información específica.
 * </p>
 */
public class Detalle_ventaDAO {

    private static Conexion conexion = new Conexion();

    /**
     * Agrega un nuevo detalle de venta a la base de datos.
     * <p>
     * Calcula automáticamente el precio total incluyendo el 19% de IVA basado en el precio
     * del producto y la cantidad vendida.
     * </p>
     *
     * @param detalle_venta Objeto Detalle_venta con los datos a insertar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean agregarDetalleVenta(Detalle_venta detalle_venta) {
        String query = "INSERT INTO detalle_venta (id_venta, id_producto, precio_total, tipo, cantidad) " +
                "VALUES (?, ?, ((SELECT precio FROM productos WHERE id_producto = ?) + " +
                "((SELECT precio FROM productos WHERE id_producto = ?)*0.19)) * ?, ?, ?)";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            // Establecer parámetros para la consulta SQL
            pst.setInt(1, detalle_venta.getId_venta());
            pst.setInt(2, detalle_venta.getId_producto());
            pst.setInt(3, detalle_venta.getId_producto());
            pst.setInt(4, detalle_venta.getId_producto());
            pst.setInt(5, detalle_venta.getCantidad());
            pst.setString(6, detalle_venta.getTipo());
            pst.setInt(7, detalle_venta.getCantidad());

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza el total de una venta sumando todos los detalles asociados.
     * <p>
     * Recalcula el total de la venta basado en la suma de todos los precios totales
     * de los detalles de venta asociados a esa venta.
     * </p>
     *
     * @param detalle_venta Objeto Detalle_venta que contiene el ID de venta a actualizar
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarTotal(Detalle_venta detalle_venta) {
        String query = "UPDATE venta SET total_venta = " +
                "(SELECT SUM(precio_total) FROM detalle_venta WHERE id_venta = ?) " +
                "WHERE id_venta = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, detalle_venta.getId_venta());
            pst.setInt(2, detalle_venta.getId_venta());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un detalle de venta específico de la base de datos.
     *
     * @param id ID del detalle de venta a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int id) {
        String query = "DELETE FROM detalle_venta WHERE id_detalleVenta = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Resta el monto de un detalle de venta específico del total de la venta.
     * <p>
     * Actualiza el total de la venta restando el precio total del detalle eliminado.
     * </p>
     *
     * @param id_detalle ID del detalle de venta a restar
     * @param id_venta ID de la venta a actualizar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean restar(int id_detalle, int id_venta) {
        String query = "UPDATE venta SET total_venta = total_venta - " +
                "(SELECT precio_total FROM detalle_venta WHERE id_detalleVenta = ?) " +
                "WHERE id_venta = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id_detalle);
            pst.setInt(2, id_venta);
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene los productos asociados a un pedido específico.
     * <p>
     * Devuelve una lista de cadenas formateadas con la información de cada producto
     * en el pedido, incluyendo cliente, producto, cantidad, tipo y precio total.
     * La última línea contiene el total general de la venta.
     * </p>
     *
     * @param idVenta ID de la venta a consultar
     * @return Lista de cadenas con los detalles del pedido
     */
    public List<String> obtenerProductosPorPedido(int idVenta) {
        List<String> detalles = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        String query = "SELECT cliente.nombre AS cliente, productos.nombre AS producto, " +
                "detalle_venta.cantidad, detalle_venta.tipo, detalle_venta.precio_total " +
                "FROM detalle_venta " +
                "JOIN productos ON detalle_venta.id_producto = productos.id_producto " +
                "JOIN venta ON detalle_venta.id_venta = venta.id_venta " +
                "JOIN cliente ON venta.id_cliente = cliente.id_cliente " +
                "WHERE detalle_venta.id_venta = ?";

        try {
            con = conexion.getConnection();
            pst = con.prepareStatement(query);
            pst.setInt(1, idVenta);
            rs = pst.executeQuery();

            while (rs.next()) {
                String fila = rs.getString("cliente") + "|" +
                        rs.getString("producto") + "|" +
                        rs.getInt("cantidad") + "|" +
                        rs.getString("tipo") + "|" +
                        rs.getInt("precio_total");
                detalles.add(fila);
            }

            // Agregar el total
            String totalQuery = "SELECT total_venta FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(totalQuery);
            pst.setInt(1, idVenta);
            ResultSet totalRs = pst.executeQuery();
            if (totalRs.next()) {
                detalles.add("|||Total:|" + totalRs.getInt("total_venta"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos (rs, pst, con)
        }
        return detalles;
    }
}