import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class CajaGUI {

    private JPanel Main;
    private JTable table1;
    private JButton volverButton;

    Conexion conexion = new Conexion();
    public CajaGUI() {
        actualizarCaja();
        obtenerDatos();

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                MovimientoGUI movimientoGUI = new MovimientoGUI();
                movimientoGUI.main(null);
            }
        });

    }



    public boolean actualizarCaja() {
        Connection con = conexion.getConnection();

        try {

        String query = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";

        PreparedStatement pst = con.prepareStatement(query);

        pst.executeUpdate();

        }

        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
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
