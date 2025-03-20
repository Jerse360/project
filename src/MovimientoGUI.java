import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JTextField textField2;
    private JButton verCajaButton;

    MovimientosDAO movimientosDAO = new MovimientosDAO();

    CajaGUI caja = new CajaGUI();

    public MovimientoGUI() {

        obtenerDatos();
        textField2.setEditable(false);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoria = comboBox1.getSelectedItem().toString();
                int monto =Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(0, 0, monto, categoria, "","Egreso");

                if (movimientosDAO.agregar(movimiento)) {
                    obtenerDatos();

                }
                else {
                    JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                }
                textField1.setText("");
                obtenerDatos();
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            int id = Integer.parseInt(textField2.getText());
            String categoria = comboBox1.getSelectedItem().toString();
            int monto =Integer.parseInt(textField1.getText());

            Movimiento movimiento = new Movimiento(id, 0, monto, categoria, "","Egreso");

            movimientosDAO.actualizar(movimiento);
            obtenerDatos();

            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());

                movimientosDAO.eliminar(id);
                obtenerDatos();


            }
        });

        verCajaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                caja.main(null);

            }
        });


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);


                int selectFilas = table1.getSelectedRow();

                if (selectFilas >=0) {

                    textField1.setText((String.valueOf( table1.getValueAt(selectFilas,4))));

                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas,2));

                    textField2.setText((String.valueOf( table1.getValueAt(selectFilas,0))));



                }
            }
        });
    }




    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.addColumn("ID_movimiento");
        model.addColumn("ID_venta");
        model.addColumn("Categoria");
        model.addColumn("Descripcion");
        model.addColumn("Monto");
        model.addColumn("Fecha");


        table1.setModel(model);
        String[] dato = new String[6];
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


                model.addRow(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Movimientos financieros");
        frame.setContentPane(new MovimientoGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1400, 600);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
