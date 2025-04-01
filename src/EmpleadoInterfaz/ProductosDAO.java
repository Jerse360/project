package EmpleadoInterfaz;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Data Access Object (DAO) para gestionar operaciones CRUD de productos.
 * <p>
 * Proporciona métodos para interactuar con la tabla de productos en la base de datos,
 * incluyendo operaciones para consultar, agregar, actualizar y eliminar productos.
 * Maneja validaciones y mensajes de error apropiados para cada operación.
 * </p>
 */
public class ProductosDAO {

    private static Conexion conexion = new Conexion();

    /**
     * Obtiene todos los productos registrados en la base de datos.
     * <p>
     * Consulta la tabla de productos y devuelve una lista con todos los registros encontrados.
     * Cada producto se representa como un objeto de la clase Productos.
     * </p>
     *
     * @return Lista de objetos Productos con todos los registros encontrados
     */
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
            JOptionPane.showMessageDialog(null, "Error al obtener productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Agrega un nuevo producto a la base de datos.
     * <p>
     * Inserta un nuevo registro en la tabla de productos con los datos proporcionados.
     * </p>
     *
     * @param producto Objeto Productos con los datos a insertar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean agregarProducto(Productos producto) {
        String query = "INSERT INTO productos (nombre, categoria, precio, stock, stock_minimo) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, producto.getNombre());
            pst.setString(2, producto.getCategoria());
            pst.setInt(3, producto.getPrecio());
            pst.setInt(4, producto.getStock());
            pst.setInt(5, producto.getStock_minimo());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Producto agregado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un producto de la base de datos.
     * <p>
     * Elimina el registro del producto con el ID especificado, verificando primero
     * que no esté asociado a ninguna venta (integridad referencial).
     * </p>
     *
     * @param id ID del producto a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminarProducto(int id) {
        String query = "DELETE FROM productos WHERE id_producto = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Producto eliminado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto con ID: " + id);
                return false;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar: El producto está asociado a ventas",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un producto existente.
     * <p>
     * Modifica todos los campos del producto con el ID especificado.
     * </p>
     *
     * @param id_producto ID del producto a actualizar
     * @param nombre Nuevo nombre del producto
     * @param categoria Nueva categoría del producto
     * @param precio Nuevo precio del producto
     * @param stock Nuevo stock disponible
     * @param stock_minimo Nuevo stock mínimo requerido
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public static boolean actualizarProducto(int id_producto, String nombre, String categoria,
                                             int precio, int stock, int stock_minimo) {
        String query = "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, stock = ?, stock_minimo = ? " +
                "WHERE id_producto = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setInt(3, precio);
            stmt.setInt(4, stock);
            stmt.setInt(5, stock_minimo);
            stmt.setInt(6, id_producto);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Producto actualizado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto con ID: " + id_producto);
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
}