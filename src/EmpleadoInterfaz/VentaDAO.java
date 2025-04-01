package EmpleadoInterfaz;

import javax.swing.*;
import java.sql.*;

/**
 * Clase DAO (Data Access Object) para gestionar las operaciones CRUD de ventas
 * en la base de datos. Maneja la lógica de transiciones de estado, control de stock
 * y registro financiero asociado a las ventas.
 * <p>
 * Esta clase implementa:
 * - Creación de nuevas ventas con estado inicial "Preparacion"
 * - Actualización de ventas con validación de transiciones de estado
 * - Gestión de inventario (validación y descuento de stock)
 * - Registro automático de movimientos financieros
 * - Validación de operaciones según el estado de la venta
 * </p>
 */
public class VentaDAO {

    private Conexion conexion = new Conexion();

    /**
     * Agrega una nueva venta a la base de datos con estado "Preparacion".
     * <p>
     * Crea un registro inicial de venta con estado "Preparacion" y total 0.
     * El total se actualizará posteriormente al agregar productos.
     * </p>
     *
     * @param venta Objeto Venta con los datos del cliente asociado
     * @return true si la operación fue exitosa, false en caso de error
     */
    public boolean agregar(Venta venta) {
        // Query para insertar nueva venta con estado inicial "Preparacion"
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

    /**
     * Actualiza los datos de una venta existente, validando transiciones de estado.
     * <p>
     * Realiza operaciones adicionales según el estado:
     * - Para estado "Enviado": valida y descuenta stock
     * - Para estado "Entregado": registra movimiento financiero
     * Valida que las transiciones entre estados sean válidas.
     * </p>
     *
     * @param venta Objeto Venta con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso de error o validación fallida
     */
    public boolean actualizar(Venta venta) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = conexion.getConnection();
            CajaGUI caja = new CajaGUI();

            // 1. Obtener estado actual de la venta antes de modificarla
            String estadoAntiguoQuery = "SELECT estado FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(estadoAntiguoQuery);
            pst.setInt(1, venta.getId_venta());
            rs = pst.executeQuery();

            String estadoAntiguo = "";
            if (rs.next()) {
                estadoAntiguo = rs.getString("estado");
            }

            // 2. Actualizar datos básicos de la venta (cliente y estado)
            String updateQuery = "UPDATE venta SET id_cliente = ?, estado = ? WHERE id_venta = ?";
            pst = con.prepareStatement(updateQuery);
            pst.setInt(1, venta.getId_cliente());
            pst.setString(2, venta.getEstado());
            pst.setInt(3, venta.getId_venta());
            pst.executeUpdate();

            // 3. Obtener estado actualizado para validar
            String estadoQuery = "SELECT estado FROM venta WHERE id_venta = ?";
            pst = con.prepareStatement(estadoQuery);
            pst.setInt(1, venta.getId_venta());
            rs = pst.executeQuery();

            String estado = "";
            if (rs.next()) {
                estado = rs.getString("estado");
            }

            // 4. Validar transición de estado
            if (validarTransicionEstado(estadoAntiguo, venta.getEstado()) == false) {
                JOptionPane.showMessageDialog(null, "No se puede cambiar de " + estadoAntiguo + " a " + venta.getEstado(), "Error", JOptionPane.ERROR_MESSAGE);

                // Revertir el cambio de estado si la transición no es válida
                String revertirQuery = "UPDATE venta SET estado = ? WHERE id_venta = ?";
                pst = con.prepareStatement(revertirQuery);
                pst.setString(1, estadoAntiguo);
                pst.setInt(2, venta.getId_venta());
                pst.executeUpdate();

                return false;
            }

            // 5. Procesamiento especial para estado "Enviado"
            if (estado.equals("Enviado")) {
                // Obtener todos los productos asociados a la venta
                String detalleQuery = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";
                pst = con.prepareStatement(detalleQuery);
                pst.setInt(1, venta.getId_venta());
                rs = pst.executeQuery();

                while (rs.next()) {
                    int idProducto = rs.getInt("id_producto");
                    int cantidad = rs.getInt("cantidad");

                    // Validar disponibilidad de stock
                    if (!validarStock(idProducto, cantidad)) {
                        // Revertir estado si no hay stock suficiente
                        String revertirQuery = "UPDATE venta SET estado = ? WHERE id_venta = ?";
                        pst = con.prepareStatement(revertirQuery);
                        pst.setString(1, estadoAntiguo);
                        pst.setInt(2, venta.getId_venta());
                        pst.executeUpdate();

                        return false;
                    }

                    // Descontar stock
                    restarStock(idProducto, cantidad);

                    // Verificar si el stock queda por debajo del mínimo
                    verificarStockMinimo(idProducto);
                }
            }

            // 6. Procesamiento especial para estado "Entregado"
            if(estado.equals("Entregado")) {
                // Registrar movimiento financiero
                String movimiento = "INSERT INTO movimiento_financiero( id_venta, categoria, descripcion, monto, fecha) VALUES (?,'Venta','Ingreso',?,CURRENT_TIME)";
                pst = con.prepareStatement(movimiento);
                pst.setInt(1, venta.getId_venta());
                pst.setInt(2,venta.getTotal_venta());
                pst.executeUpdate();

                // Actualizar saldo en caja
                String query2 = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";
                pst = con.prepareStatement(query2);
                pst.executeUpdate();

                // Actualizar interfaz de caja
                caja.obtenerDatos();
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
            return false;
        } finally {
            // Cierre seguro de recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Válida sí hay suficiente stock para un producto específico.
     * <p>
     * Verifica que la cantidad solicitada esté disponible en inventario.
     * Muestra mensaje de error si no hay stock suficiente.
     * </p>
     *
     * @param idProducto Identificador del producto a validar
     * @param cantidad Cantidad requerida
     * @return true si hay stock suficiente, false en caso contrario
     */
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
            // Cierre seguro de recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reduce el stock de un producto en la cantidad especificada.
     * <p>
     * Actualiza el inventario descontando la cantidad vendida.
     * </p>
     *
     * @param idProducto Identificador del producto
     * @param cantidad Cantidad a descontar
     * @return true si la operación fue exitosa, false en caso de error
     */
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
            // Cierre seguro de recursos
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verifica si el stock de un producto está por debajo del mínimo establecido.
     * <p>
     * Muestra una alerta gráfica si el stock actual es menor al mínimo configurado.
     * </p>
     *
     * @param idProducto Identificador del producto a verificar
     * @return true si la operación fue exitosa, false en caso de error
     */
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
            // Cierre seguro de recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Válida si una transición entre estados de venta es permitida.
     * <p>
     * Las transiciones válidas son:
     * - De "Preparacion" a "Enviado"
     * - De "Enviado" a "Entregado"
     * - Mantener el mismo estado
     * </p>
     *
     * @param estadoAntiguo Estado actual de la venta
     * @param estadoNuevo Estado deseado
     * @return true si la transición es válida, false en caso contrario
     */
    public boolean validarTransicionEstado(String estadoAntiguo, String estadoNuevo) {
        // Validar las transiciones permitidas:
        // 1. De Preparacion a Enviado
        // 2. De Enviado a Entregado
        // 3. No cambiar de estado (mismo estado)
        if (estadoAntiguo.equals("Preparacion") && estadoNuevo.equals("Enviado")) {
            return true;
        } else if (estadoAntiguo.equals("Enviado") && estadoNuevo.equals("Entregado")) {
            return true;
        } else if (estadoAntiguo.equals(estadoNuevo)) {
            return true;
        } else {
            return false;
        }
    }
}


