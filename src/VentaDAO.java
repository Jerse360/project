
import javax.swing.*;
import java.sql.*;

public class VentaDAO {

    Conexion conexion = new Conexion();

    String estado;

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

        Connection con = conexion.getConnection();

        String sql = "UPDATE venta SET id_cliente = ?, estado = ? WHERE id_venta = ?";
        try {

             PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, venta.getId_cliente());
            pst.setString(2, venta.getEstado());
            pst.setInt(3, venta.getId_venta());

            pst.executeUpdate();

            String sql2 = "SELECT estado FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(sql2);
            pst.setInt(1, venta.getId_venta());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                estado =rs.getString("estado");
            }

            if (estado.equals("Enviado"))
            {

                String query = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";

                try  {
                 pst = con.prepareStatement(query);
                 pst.setInt(1, venta.getId_venta());

                 try (ResultSet validacion = pst.executeQuery()) {
                        while (validacion.next()) {
                            int idProducto = validacion.getInt("id_producto");
                            int cantidad = validacion.getInt("cantidad");

                            // Validar el stock
                            if (!validarStock(idProducto, cantidad)) {
                                return false; // No hay suficiente stock
                            }

                            // Restar el stock
                            restarStock(idProducto, cantidad);

                            // Verificar si el stock resultante es menor que el mínimo
                            verificarStockMinimo(idProducto);
                        }
                 }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }


    public boolean validarStock(int idProducto, int cantidad) {

        Connection con = conexion.getConnection();

        String query = "SELECT stock FROM productos WHERE id_producto = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, idProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock < cantidad) {
                        JOptionPane.showMessageDialog(null, "No hay suficiente stock para el producto con ID: " + idProducto, "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean restarStock(int idProducto, int cantidad)  {

        Connection con = conexion.getConnection();

        String query = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, cantidad);
            pst.setInt(2, idProducto);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean verificarStockMinimo(int idProducto) {

        Connection con = conexion.getConnection();

        String query = "SELECT stock, stock_minimo FROM productos WHERE id_producto = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, idProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    int stockMinimo = rs.getInt("stock_minimo");
                    if (stock < stockMinimo) {
                        JOptionPane.showMessageDialog(null, "¡Alerta! El stock del producto con ID: " + idProducto + " está por debajo del mínimo.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


}

