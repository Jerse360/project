package farmacia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    public boolean agregar(Venta venta) {
        String sql = "INSERT INTO venta (id_cliente, total_venta, estado, fecha_hora) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, venta.getId_cliente());
            pst.setDouble(2, venta.getTotal_venta());
            pst.setString(3, venta.getEstado());
            pst.setString(4, venta.getFecha_hora());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al agregar venta: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> obtenerVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM venta";
        try (Connection con = Conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ventas.add(new Venta(
                        rs.getInt("id_venta"),
                        rs.getInt("id_cliente"),
                        rs.getInt("total_venta"),
                        rs.getString("estado"),
                        rs.getString("fecha_hora")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    public boolean actualizar(Venta venta) {
        String sql = "UPDATE venta SET id_cliente = ?, total_venta = ?, estado = ?, fecha_hora = ? WHERE id_venta = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, venta.getId_cliente());
            pst.setDouble(2, venta.getTotal_venta());
            pst.setString(3, venta.getEstado());
            pst.setString(4, venta.getFecha_hora());
            pst.setInt(5, venta.getId_venta());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idVenta) {
        String sql = "DELETE FROM venta WHERE id_venta = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idVenta);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }
}

