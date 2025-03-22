import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ReciboGUI {
    public JPanel Main;
    private JTable table1;
    private JLabel Id_orden;
    private JLabel id_orden;

    Conexion conexion = new Conexion();

    int id_venta;

    public ReciboGUI(int id_venta) {
        this.id_venta = id_venta;
        id_orden.setText(String.valueOf(id_venta));
        obtenerDatos();


    }



    public void obtenerDatos() {

        DefaultTableModel modelo = new DefaultTableModel();

        modelo.addColumn("Id_detalleVenta");
        modelo.addColumn("Id_Venta");
        modelo.addColumn("id_producto");
        modelo.addColumn("Precio Total");
        modelo.addColumn("Tipo");
        modelo.addColumn("Cantidad");




        table1.setModel(modelo);

        String[] dato = new String[6];
        Connection con = conexion.getConnection();

        try
        {
            String query = "SELECT id_detalleVenta, id_venta, productos.nombre AS producto, precio_total, tipo,cantidad FROM detalle_venta JOIN productos ON detalle_venta.id_producto = productos.id_producto WHERE id_venta = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id_venta);

            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);
                dato[5] = rs.getString(6);



                modelo.addRow(dato);
            }
        }

        catch (SQLException e){
            e.printStackTrace();

        }

    }

}
