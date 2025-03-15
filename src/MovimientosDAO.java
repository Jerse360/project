import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimientosDAO {

    private Conexion conexion = new Conexion();


    public boolean agregar(Movimiento movimiento) {

        Connection con = conexion.getConnection();
        String query = "INSERT INTO movimiento_financiero (id_Movimiento,id_venta, Ingreso, Egreso, categoría, monto, fecha)VALUES (?, ?, ?,?,?,?,CURRENT_DATE);";

        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, pedido.getId_orden());
            pst.setInt(2, pedido.getId_producto());
            pst.setInt(3, pedido.getCantidad());
            pst.setInt(4, pedido.getId_producto());
            pst.setInt(5, pedido.getCantidad());
            pst.executeUpdate();




        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

        return true;
    }

}
