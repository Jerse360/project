package farmacia;

import javax.swing.*;
import java.sql.*;

public class ClienteDAO {

    public void agregar(ClienteSetGet clienteSetGet) {
        Connection con = Conexion.getConnection();

        String query = "INSERT INTO cliente (id_cliente, cedula, nombre, direccion, telefono, email) VALUES (0,?,?,?,?,?)";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(0, clienteSetGet.getId_cliente());
            pst.setString(1, clienteSetGet.getCedula());
            pst.setString(2, clienteSetGet.getNombre());
            pst.setString(3, clienteSetGet.getDireccion());
            pst.setString(4, clienteSetGet.getTelefono());
            pst.setString(5, clienteSetGet .getEmail());

            int resultado = pst.executeUpdate();
            if (resultado > 0)
                JOptionPane.showMessageDialog(null, "Agregado con Exito");
            else
                JOptionPane.showMessageDialog(null, "No Agregado con Exito");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No Agregado con Exito");
        }
    }
    //Eliminar
    public void eliminar(int id) {
        Connection con = Conexion.getConnection();

        String query = "DELETE FROM cliente WHERE id_cliente = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);

            int resultado = pst.executeUpdate();

            if (resultado > 0)
                JOptionPane.showMessageDialog(null, "Eliminado con Exito");
            else
                JOptionPane.showMessageDialog(null, "No Eliminado con Exito");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No Eliminado con Exito");
        }
    }
    //actualizar
    public void actualizar(ClienteSetGet clienteSetGet) {
        Connection con = Conexion.getConnection();
        String query = "UPDATE clientes SET id_cliente = ?, cedula = ?, nombre = ?, direccion = ?,telefono = ?, email = ? WHERE id_clientes = ?";

        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(0, clienteSetGet.getId_cliente());
            pst.setString(1, clienteSetGet.getCedula());
            pst.setString(2, clienteSetGet.getNombre());
            pst.setString(3, clienteSetGet.getDireccion());
            pst.setString(4, clienteSetGet.getTelefono());
            pst.setString(5, clienteSetGet .getEmail());
            int resultado = pst.executeUpdate();
            if (resultado > 0)
                JOptionPane.showMessageDialog(null, "Actualizado con Exito");
            else
                JOptionPane.showMessageDialog(null, "No Actualizado con Exito");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No Actualizado con Exito");
        }
    }
    }

