import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovimientosDAO {

    private Conexion conexion = new Conexion();

    String validacion;

    public boolean agregar(Movimiento movimiento) {

        Connection con = conexion.getConnection();

        CajaGUI caja = new CajaGUI();

        try
        {
            String query = "INSERT INTO movimiento_financiero (id_venta, categoria, descripcion, monto, fecha)VALUES (0, ?, 'Egreso',-?,CURRENT_TIME);";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, movimiento.getCategoria());
            pst.setInt(2, movimiento.getMonto());

            int filas = pst.executeUpdate();

            if (filas > 0){
                JOptionPane.showMessageDialog(null, "Creado con Exito");

                String query2 = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";
                pst = con.prepareStatement(query2);
                pst.executeUpdate();

                caja.obtenerDatos();

            }

            else
                JOptionPane.showMessageDialog(null, "No se Encontró el movimiento en la base de datos");




        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

        return true;
    }

    public boolean actualizar(Movimiento movimiento) {

        Connection con = conexion.getConnection();

        String descripcion = "SELECT descripcion FROM movimiento_financiero WHERE id_Movimiento = ?;";

        try {

            PreparedStatement pst = con.prepareStatement(descripcion);

            pst.setInt(1, movimiento.getIdMovimiento());
            ResultSet variable = pst.executeQuery();

            if (variable.next()) {
                validacion = variable.getString("descripcion");
            }

            if (validacion.equals("Egreso")) {

                String query = "UPDATE movimiento_financiero SET categoria = ?, monto = -? WHERE id_Movimiento = ?";

                pst = con.prepareStatement(query);
                pst.setString(1, movimiento.getCategoria());
                pst.setInt(2, movimiento.getMonto());
                pst.setInt(3, movimiento.getIdMovimiento());

                int filas = pst.executeUpdate();

                if (filas > 0){
                    JOptionPane.showMessageDialog(null, "Actualización Exitosa");

                    String query2 = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";
                    pst = con.prepareStatement(query2);
                    pst.executeUpdate();

                }

                else
                    JOptionPane.showMessageDialog(null, "No se Encontró el movimiento en la base de datos");
            }

            else
                JOptionPane.showMessageDialog(null,"No se puede modificar o eliminar un movimiento de tipo ingreso");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;
    }

    public boolean eliminar(int id) {

        Connection con = conexion.getConnection();

        String descripcion = "SELECT descripcion FROM movimiento_financiero WHERE id_Movimiento = ?;";

        try {

            PreparedStatement pst = con.prepareStatement(descripcion);

            pst.setInt(1, id);
            ResultSet variable = pst.executeQuery();

            if (variable.next()) {
                validacion = variable.getString("descripcion");
            }

            if (validacion.equals("Egreso")) {

                String query = "DELETE FROM movimiento_financiero WHERE id_Movimiento = ?";

                pst = con.prepareStatement(query);
                pst.setInt(1, id);

                int filas = pst.executeUpdate();

                if (filas > 0)
                {
                    JOptionPane.showMessageDialog(null, "Movimiento Eliminado");

                    String query2 = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";
                    pst = con.prepareStatement(query2);
                    pst.executeUpdate();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Not Found");
                }

            }
            else
                JOptionPane.showMessageDialog(null,"No se puede modificar o eliminar un movimiento de tipo ingreso");
        } catch (SQLException e) {

            e.printStackTrace();

        }
        return false;
    }
}


