
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Detalle_ventaDAO {
    private static Conexion conexion = new Conexion();


    public boolean agregarDetalleVenta(Detalle_venta detalle_venta) {
        String query = "INSERT INTO detalle_venta (id_venta, id_producto, precio_total, tipo, cantidad) VALUES (?, ?, (SELECT precio FROM productos WHERE id_producto = ?) * ?, ?,?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, detalle_venta.getId_venta());
            pst.setInt(2, detalle_venta.getId_producto());
            pst.setInt(3, detalle_venta.getId_producto());
            pst.setInt(4,detalle_venta.getCantidad());

            pst.setString(5, detalle_venta.getTipo());
            pst.setInt(6, detalle_venta.getCantidad());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean actualizarTotal(Detalle_venta detalle_venta){

        Connection con = conexion.getConnection();

        String query = " UPDATE venta SET total_venta = (SELECT SUM(precio_total) FROM detalle_venta WHERE id_venta = ?) WHERE id_venta = ?;";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, detalle_venta.getId_venta());
            pst.setInt(2, detalle_venta.getId_venta());
            pst.executeUpdate();

        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

            return true;

    }
    public List<Detalle_venta> obtenerDetalles() {
        List<Detalle_venta> lista = new ArrayList<>();
        String query = "SELECT * FROM detalle_venta";

        try (Connection con = conexion.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                lista.add(new Detalle_venta(
                        rs.getInt("id_detalleVenta"),
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getInt("precio_total"),
                        rs.getInt("cantidad"),
                        rs.getString("tipo")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarDetalle(Detalle_venta detalle) {
        String query = "UPDATE detalle_venta SET id_venta = ?, id_producto = ?, precio_total = ?, tipo = ?, cantidad = ? WHERE id_detalleVenta = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, detalle.getId_venta());
            pst.setInt(2, detalle.getId_producto());
            pst.setInt(3, detalle.getPrecio_total());
            pst.setString(4, detalle.getTipo());
            pst.setInt(5, detalle.getCantidad());
            pst.setInt(6, detalle.getId_detalleVenta());
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al actualizar detalle de venta:");
            return false;
        }
    }

    public boolean eliminarDetalle(int idDetalleVenta) {
        String query = "DELETE FROM Detalle_venta WHERE id_detalleVenta = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, idDetalleVenta);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al eliminar detalle de venta:");
            return false;
        }
    }
}

