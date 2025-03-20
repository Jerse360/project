
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    public boolean agregar(Venta venta) {
        String sql = "INSERT INTO venta (id_cliente, total_venta, estado, fecha_hora) VALUES (?, 0, 'Preparacion', CURRENT_TIME)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, venta.getId_cliente());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al agregar venta: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Venta venta) {
        String sql = "UPDATE venta SET id_cliente = ?, estado = ? WHERE id_venta = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, venta.getId_cliente());
            pst.setString(2, venta.getEstado());
            pst.setInt(3, venta.getId_venta());

            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }
}

