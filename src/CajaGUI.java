import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CajaGUI {

    private JPanel Main;
    private JTable table1;

    public CajaGUI() {
        obtenerDatos();


    }



    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.addColumn("Id_caja");
        model.addColumn("Concepto");
        model.addColumn("Total");



        table1.setModel(model);
        String[] dato = new String[3];
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();

             ResultSet rs = stmt.executeQuery("SELECT * FROM caja")) {

            while (rs.next()) {
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);


                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Caja");
        frame.setContentPane(new CajaGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 150);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
