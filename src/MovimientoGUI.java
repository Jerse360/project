import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MovimientoGUI {
    private JPanel Main;
    private JTable table1;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JComboBox comboBox1;
    private JTextField textField1;

    MovimientosDAO movimientosDAO = new MovimientosDAO();

    public MovimientoGUI() {
        obtenerDatos();
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoria = comboBox1.getSelectedItem().toString();
                int monto =Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(0,0,monto,categoria,"","","SI");

                if (movimientosDAO.agregar(movimiento)) {
                    JOptionPane.showMessageDialog(null, "Movimiento agregado correctamente");
                    obtenerDatos();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                }
                textField1.setText("");
                obtenerDatos();
            }
        });
    }




    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.addColumn("ID_movimiento");
        model.addColumn("ID_venta");
        model.addColumn("Ingreso");
        model.addColumn("Egreso");
        model.addColumn("Categoria");
        model.addColumn("Monto");
        model.addColumn("Fecha");


        table1.setModel(model);
        String[] dato = new String[7];
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movimiento_financiero")) {

            while (rs.next()) {
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);
                dato[5] = rs.getString(6);
                dato[6] = rs.getString(7);


                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Movimientos financieros");
        frame.setContentPane(new MovimientoGUI().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
