
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Detalle_ventaDAO {

    private static Conexion conexion = new Conexion();


    public boolean agregarDetalleVenta(Detalle_venta detalle_venta) {
        String query = "INSERT INTO detalle_venta (id_venta, id_producto, precio_total, tipo, cantidad) VALUES (?, ?, ((SELECT precio FROM productos WHERE id_producto = ?) +((SELECT precio FROM productos WHERE id_producto = ?)*0.19)) * ?, ?,?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, detalle_venta.getId_venta());
            pst.setInt(2, detalle_venta.getId_producto());
            pst.setInt(3, detalle_venta.getId_producto());
            pst.setInt(4, detalle_venta.getId_producto());

            pst.setInt(5,detalle_venta.getCantidad());

            pst.setString(6, detalle_venta.getTipo());
            pst.setInt(7, detalle_venta.getCantidad());

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


    public boolean eliminar(int id) {

        Connection con = conexion.getConnection();

        String query = "DELETE FROM detalle_venta WHERE id_detalleVenta = ?";

        try {



            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

        return true;

    }



    public boolean restar(int id_detalle, int id_venta){

        Connection con = conexion.getConnection();
        String query = " UPDATE venta SET total_venta = total_venta-(SELECT precio_total FROM detalle_venta WHERE id_detalleVenta = ?) WHERE id_venta = ?;";
        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_detalle);
            pst.setInt(2, id_venta);


            pst.executeUpdate();




        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

        return false;
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

