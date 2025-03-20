import javax.swing.*;
import java.sql.*;

public class ClienteDAO {

private static Conexion conexion = new Conexion();

    public boolean agregar(ClienteSetGet clienteSetGet) {
        String query = "INSERT INTO cliente (nombre, cedula, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexion.getConnection();
        PreparedStatement pst = con.prepareStatement(query)){


            pst.setString(1, clienteSetGet.getNombre());
            pst.setString(2, clienteSetGet.getCedula());
            pst.setString(3, clienteSetGet.getTelefono());
            pst.setString(4, clienteSetGet.getDireccion());
            pst.setString(5, clienteSetGet.getEmail());


            return pst.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Eliminar
    public static boolean eliminar(int id) {

        String query = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = Conexion.getConnection();
        PreparedStatement stmt = con.prepareStatement(query))
        {

            stmt.setInt(1, id);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Eliminado con Exito");
            return true;
            }else {
                JOptionPane.showMessageDialog(null, "No Eliminado con Exito");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No Eliminado con Exito");
            return false;
        }

    }
    //actualizar
    public static boolean actualizar(int id_cliente, String cedula, String nombre, String direccion, String telefono, String email) {
        String query = "UPDATE cliente SET cedula = ?, nombre = ?, direccion = ?, telefono = ?, email = ? WHERE id_cliente = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

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
    }

}

