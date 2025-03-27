import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase Data Access Object (DAO) para gestionar operaciones CRUD de movimientos financieros.
 * Proporciona métodos para agregar, actualizar y eliminar ingresos y egresos.
 */
public class MovimientosDAO {

    private Conexion conexion = new Conexion();
    private String validacion;  // Variable para almacenar validación temporal

    /**
     * Agrega un nuevo ingreso financiero a la base de datos.
     * Actualiza automáticamente el total en caja.
     *
     * @param movimiento Objeto Movimiento con los datos del ingreso
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean agregarIngreso(Movimiento movimiento) {
        String query = "INSERT INTO movimiento_financiero (id_venta, categoria, descripcion, monto, fecha) " +
                "VALUES (0, ?, 'Ingreso', ?, CURRENT_TIME)";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            // Establecer parámetros
            pst.setString(1, movimiento.getCategoria());
            pst.setInt(2, movimiento.getMonto());

            int filas = pst.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Ingreso registrado con éxito");

                // Actualizar total en caja
                actualizarTotalCaja(con);
                new CajaGUI().obtenerDatos();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar el ingreso");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Agrega un nuevo egreso financiero a la base de datos.
     * Actualiza automáticamente el total en caja.
     *
     * @param movimiento Objeto Movimiento con los datos del egreso
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean agregarEgreso(Movimiento movimiento) {
        String query = "INSERT INTO movimiento_financiero (id_venta, categoria, descripcion, monto, fecha) " +
                "VALUES (0, ?, 'Egreso', -?, CURRENT_TIME)";

        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, movimiento.getCategoria());
            pst.setInt(2, movimiento.getMonto());

            int filas = pst.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Egreso registrado con éxito");

                // Actualizar total en caja
                actualizarTotalCaja(con);
                new CajaGUI().obtenerDatos();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar el egreso");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un movimiento financiero existente (solo egresos).
     *
     * @param movimiento Objeto Movimiento con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Movimiento movimiento) {
        try (Connection con = conexion.getConnection()) {
            // Validar tipo de movimiento
            if (!validarTipoMovimiento(con, movimiento.getIdMovimiento(), "Egreso")) {
                JOptionPane.showMessageDialog(null, "Solo se pueden modificar egresos");
                return false;
            }

            String query = "UPDATE movimiento_financiero SET categoria = ?, monto = -? " +
                    "WHERE id_Movimiento = ?";

            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, movimiento.getCategoria());
                pst.setInt(2, movimiento.getMonto());
                pst.setInt(3, movimiento.getIdMovimiento());

                int filas = pst.executeUpdate();

                if (filas > 0) {
                    JOptionPane.showMessageDialog(null, "Egreso actualizado con éxito");
                    actualizarTotalCaja(con);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el movimiento");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un movimiento financiero (solo egresos).
     *
     * @param id ID del movimiento a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int id) {
        try (Connection con = conexion.getConnection()) {
            // Validar tipo de movimiento
            if (!validarTipoMovimiento(con, id, "Egreso")) {
                JOptionPane.showMessageDialog(null, "Solo se pueden eliminar egresos");
                return false;
            }

            String query = "DELETE FROM movimiento_financiero WHERE id_Movimiento = ?";

            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, id);

                int filas = pst.executeUpdate();

                if (filas > 0) {
                    JOptionPane.showMessageDialog(null, "Egreso eliminado con éxito");
                    actualizarTotalCaja(con);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el movimiento");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método privado para actualizar el total en caja.
     *
     * @param con Conexión a la base de datos
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    private void actualizarTotalCaja(Connection con) throws SQLException {
        String query = "UPDATE caja SET Valor = (SELECT SUM(monto) FROM movimiento_financiero)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.executeUpdate();
        }
    }

    /**
     * Valida el tipo de movimiento (Ingreso/Egreso).
     *
     * @param con Conexión a la base de datos
     * @param id ID del movimiento a validar
     * @param tipo Esperado ("Ingreso" o "Egreso")
     * @return true si coincide, false en caso contrario
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    private boolean validarTipoMovimiento(Connection con, int id, String tipo) throws SQLException {
        String query = "SELECT descripcion FROM movimiento_financiero WHERE id_Movimiento = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            return rs.next() && tipo.equals(rs.getString("descripcion"));
        }
    }
}


