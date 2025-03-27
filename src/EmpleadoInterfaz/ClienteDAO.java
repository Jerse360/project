package EmpleadoInterfaz;

import ClienteInterfaz.LogGUI;
import javax.swing.*;
import java.sql.*;

/**
 * Clase Data Access Object (DAO) para gestionar operaciones CRUD de clientes en la base de datos.
 * Proporciona métodos para agregar, eliminar y actualizar registros de clientes.
 */
public class ClienteDAO {

    private static Conexion conexion = new Conexion();

    /**
     * Agrega un nuevo cliente a la base de datos.
     *
     * @param clienteSetGet Objeto que contiene los datos del cliente a agregar
     * @return true si el cliente fue agregado exitosamente, false en caso contrario
     */
    public boolean agregar(ClienteSetGet clienteSetGet) {
        LogGUI log = new LogGUI();

        // Validar formato de cédula
        if (log.validarCedula(clienteSetGet.getCedula())) {
            // Verificar que la cédula no exista previamente
            if (!log.validarCedulaEnBaseDeDatos(clienteSetGet.getCedula())) {
                String query = "INSERT INTO cliente (nombre, cedula, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)";

                try (Connection con = conexion.getConnection();
                     PreparedStatement pst = con.prepareStatement(query)) {

                    // Establecer parámetros para la consulta SQL
                    pst.setString(1, clienteSetGet.getNombre());
                    pst.setString(2, clienteSetGet.getCedula());
                    pst.setString(3, clienteSetGet.getTelefono());
                    pst.setString(4, clienteSetGet.getDireccion());
                    pst.setString(5, clienteSetGet.getEmail());

                    // Ejecutar inserción y retornar resultado
                    return pst.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "La cédula ya existe");
            }
        }
        return false;
    }

    /**
     * Elimina un cliente de la base de datos según su ID.
     *
     * @param id ID del cliente a eliminar
     * @return true si el cliente fue eliminado exitosamente, false en caso contrario
     */
    public static boolean eliminar(int id) {
        String query = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Eliminado con Éxito");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el cliente");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar");
            return false;
        }
    }

    /**
     * Actualiza los datos de un cliente existente en la base de datos.
     *
     * @param id_cliente ID del cliente a actualizar
     * @param cedula Nueva cédula del cliente
     * @param nombre Nuevo nombre del cliente
     * @param direccion Nueva dirección del cliente
     * @param telefono Nuevo teléfono del cliente
     * @param email Nuevo email del cliente
     * @return true si el cliente fue actualizado exitosamente, false en caso contrario
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    public boolean actualizar(int id_cliente, String cedula, String nombre, String direccion, String telefono, String email) throws SQLException {
        LogGUI log = new LogGUI();

        // Validar formato de cédula
        if (log.validarCedula(cedula)) {
            // Verificar que la nueva cédula no exista en otro registro
            if (!validarCedulaExistente(cedula, id_cliente)) {
                String query = "UPDATE cliente SET cedula = ?, nombre = ?, direccion = ?, telefono = ?, email = ? WHERE id_cliente = ?";

                try (Connection con = Conexion.getConnection();
                     PreparedStatement stmt = con.prepareStatement(query)) {

                    // Establecer parámetros para la consulta SQL
                    stmt.setString(1, cedula);
                    stmt.setString(2, nombre);
                    stmt.setString(3, direccion);
                    stmt.setString(4, telefono);
                    stmt.setString(5, email);
                    stmt.setInt(6, id_cliente);

                    int filas = stmt.executeUpdate();
                    if (filas > 0) {
                        JOptionPane.showMessageDialog(null, "Actualizado con Éxito");
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el cliente para actualizar");
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al actualizar");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "La cédula ya existe");
            }
        }
        return false;
    }
    /**
     * Valida que una cédula no exista en la base de datos, con opción para excluir un registro específico al actualizar.
     *
     * @param cedula Número de cédula a validar
     * @param idExcluir ID del registro a excluir de la validación (usar null para validación en creación)
     * @return true si la cédula ya existe (y no es el registro excluido), false si está disponible
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    public boolean validarCedulaExistente(String cedula, Integer idExcluir) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = conexion.getConnection();
            String query;

            if (idExcluir != null) {
                // Consulta para actualización (excluye un registro específico)
                query = "SELECT COUNT(*) FROM cliente WHERE cedula = ? AND id_cliente != ?";
            } else {
                // Consulta para creación (valida contra todos los registros)
                query = "SELECT COUNT(*) FROM cliente WHERE cedula = ?";
            }

            pst = con.prepareStatement(query);
            pst.setString(1, cedula);

            if (idExcluir != null) {
                pst.setInt(2, idExcluir);
            }

            rs = pst.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Retorna true si encuentra registros (cedula existe)
            }

            return false;
        } finally {
            // Cierre seguro de recursos
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignorar */ }
            if (pst != null) try { pst.close(); } catch (SQLException e) { /* ignorar */ }
            if (con != null) try { con.close(); } catch (SQLException e) { /* ignorar */ }
        }
    }
}

