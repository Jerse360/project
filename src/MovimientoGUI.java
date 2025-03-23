import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

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
    private JButton volverButton;
    private JComboBox comboBoxCategoria;
    private JComboBox comboBoxTipo;

    MovimientosDAO movimientosDAO = new MovimientosDAO();

    CajaGUI caja = new CajaGUI();

    public MovimientoGUI() {

        obtenerDatos();
        textField2.setEditable(false);

        comboBoxTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoSeleccionado = comboBoxTipo.getSelectedItem().toString();
                if ("Ingreso".equals(tipoSeleccionado)) {
                    comboBoxCategoria.setEnabled(true);
                    comboBox1.setEnabled(false);
                } else {
                    comboBoxCategoria.setEnabled(false);
                    comboBox1.setEnabled(true);
                }
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = comboBoxTipo.getSelectedItem().toString();
                String categoria = tipo.equals("Ingreso") ? comboBoxCategoria.getSelectedItem().toString() : comboBox1.getSelectedItem().toString();
                int monto =Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(0, 0, monto, categoria, "",tipo);

                if (tipo.equals("Ingreso")) {
                    if (movimientosDAO.agregarIngreso(movimiento)) {

                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                    }
                }
                else {
                    if (movimientosDAO.agregarEgreso(movimiento)) {

                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                    }

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
            caja.obtenerDatos();

            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());

                movimientosDAO.eliminar(id);
                obtenerDatos();
                caja.obtenerDatos();

            }
        });

        verCajaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(verCajaButton);
                menuFrame.dispose();

                caja.main(null  );


            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectFilas = table1.getSelectedRow();

                if (selectFilas >=0) {

                    textField1.setText((String.valueOf( table1.getValueAt(selectFilas,4))));

                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas,2));

                    textField2.setText((String.valueOf( table1.getValueAt(selectFilas,0))));

                    int id_venta = Integer.parseInt(String.valueOf(table1.getValueAt(selectFilas,1)));

                    if (e.getClickCount() == 2) {
                        ReciboGUI reciboGUI = new ReciboGUI(id_venta);

                        // Mostrar la ventana de ReciboGUI
                        JFrame frame = new JFrame("Recibo");
                        frame.setContentPane(reciboGUI.Main);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cerrar solo esta ventana
                        frame.pack();
                        frame.setSize(700, 700);
                        frame.setResizable(true);
                        frame.setVisible(true);
                    }
                }
            }
        });
    }

    public void obtenerDatos() {

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que la columna "Id_venta" (columna 0) no sea editable
                return column != 1 && column != 0; // Solo las columnas diferentes a 0 y 1 serán editables
            }
        };

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
        frame.setSize(1800, 600);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
