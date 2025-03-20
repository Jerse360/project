import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductosDAO {
    private static Conexion conexion = new Conexion();

    public List<Productos> obtenerProductos() {
        List<Productos> lista = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Productos(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public boolean agregarProducto(Productos productos) {
        String query = "INSERT INTO productos (nombre, categoria, precio, stock, stock_minimo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, productos.getNombre());
            pst.setString(2, productos.getCategoria());
            pst.setFloat(3, productos.getPrecio());
            pst.setInt(4, productos.getStock());
            pst.setInt(5, productos.getStock_minimo());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean eliminarProducto(int id) {
        String query = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Registro eliminado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean actualizarProducto(int id_producto, String nombre, String categoria, int precio, int stock, int stock_minimo) {
        String query = "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, stock = ?, stock_minimo = ? WHERE id_producto = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setFloat(3, precio);
            stmt.setInt(4, stock);
            stmt.setInt(5, stock_minimo);
            stmt.setInt(6, id_producto);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Producto actualizado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Productos> buscarProducto(int id) {
        List<Productos> productos = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE id_producto = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Productos producto = new Productos(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getInt("precio"),
                            rs.getInt("stock"),
                            rs.getInt("stock_minimo")
                    );
                    productos.add(producto);
                }
            }

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Producto no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    public boolean restarStock(int id_producto, int cantidadVendida) {
        String query = "UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, cantidadVendida);
            stmt.setInt(2, id_producto);
            stmt.setInt(3, cantidadVendida);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean aumentarStock(int id_producto,int cantidad){
        String query = "UPDATE productos SET stock = stock +? WHERE id_producto =?";

        try(Connection con = conexion.getConnection();
        PreparedStatement stmt = con.prepareStatement(query)){
            stmt.setInt(1,cantidad);
            stmt.setInt(2,id_producto);

            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public int obtenerStock(int id_producto) {
        String query = "SELECT stock FROM productos WHERE id_producto = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id_producto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
