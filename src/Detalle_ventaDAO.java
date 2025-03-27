import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Data Access Object (DAO) para gestionar operaciones CRUD de detalles de venta.
 * Proporciona métodos para interactuar con la base de datos relacionada a detalles de venta.
 */
public class Detalle_ventaDAO {

    private static Conexion conexion = new Conexion();

    /**
     * Agrega un nuevo detalle de venta a la base de datos.
     * Calcula automáticamente el precio total incluyendo el 19% de IVA.
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
}

