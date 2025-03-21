
import javax.swing.*;
import java.sql.*;

public class VentaDAO {

    Conexion conexion = new Conexion();

    String estado, estadoAntiguo;

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
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = conexion.getConnection();

            // Obtener el estado antiguo de la venta
            String estadoAntiguoQuery = "SELECT estado FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(estadoAntiguoQuery);
            pst.setInt(1, venta.getId_venta());
            rs = pst.executeQuery();

            String estadoAntiguo = "";
            if (rs.next()) {
                estadoAntiguo = rs.getString("estado");
            }

            // Actualizar la venta
            String updateQuery = "UPDATE venta SET id_cliente = ?, estado = ? WHERE id_venta = ?";
            pst = con.prepareStatement(updateQuery);
            pst.setInt(1, venta.getId_cliente());
            pst.setString(2, venta.getEstado());
            pst.setInt(3, venta.getId_venta());
            pst.executeUpdate();

            // Obtener el nuevo estado de la venta
            String estadoQuery = "SELECT estado FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(estadoQuery);
            pst.setInt(1, venta.getId_venta());
            rs = pst.executeQuery();

            String estado = "";
            if (rs.next()) {
                estado = rs.getString("estado");
            }

            // Si el estado es "Enviado", validar y restar stock
            if (estado.equals("Enviado")) {
                String detalleQuery = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";
                pst = con.prepareStatement(detalleQuery);
                pst.setInt(1, venta.getId_venta());
                rs = pst.executeQuery();

                while (rs.next()) {
                    int idProducto = rs.getInt("id_producto");
                    int cantidad = rs.getInt("cantidad");

                    // Validar el stock
                    if (!validarStock(idProducto, cantidad)) {
                        // Revertir el estado de la venta si no hay suficiente stock
                        String revertirQuery = "UPDATE venta SET estado = ? WHERE id_venta = ?";
                        pst = con.prepareStatement(revertirQuery);
                        pst.setString(1, estadoAntiguo);
                        pst.setInt(2, venta.getId_venta());
                        pst.executeUpdate();

                        return false; // No hay suficiente stock
                    }

                    // Restar el stock
                    restarStock(idProducto, cantidad);

                    // Verificar si el stock resultante es menor que el mínimo
                    verificarStockMinimo(idProducto);
                }
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validarStock(int idProducto, int cantidad) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = conexion.getConnection();
            String query = "SELECT stock FROM productos WHERE id_producto = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, idProducto);
            rs = pst.executeQuery();

            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (stock < cantidad) {
                    JOptionPane.showMessageDialog(null, "No hay suficiente stock para el producto con ID: " + idProducto, "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean restarStock(int idProducto, int cantidad) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = conexion.getConnection();
            String query = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, cantidad);
            pst.setInt(2, idProducto);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean verificarStockMinimo(int idProducto) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = conexion.getConnection();
            String query = "SELECT stock, stock_minimo FROM productos WHERE id_producto = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, idProducto);
            rs = pst.executeQuery();

            if (rs.next()) {
                int stock = rs.getInt("stock");
                int stockMinimo = rs.getInt("stock_minimo");
                if (stock < stockMinimo) {
                    JOptionPane.showMessageDialog(null, "¡Alerta! El stock del producto con ID: " + idProducto + " está por debajo del mínimo.", "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


