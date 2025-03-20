import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class VentaGUI {
    private JLabel Venta;
    private JTable table1;
    private JComboBox comboBox1;
    private JPanel Main;
    private JComboBox comboBox2;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton pedirButton;

    Conexion conexion = new Conexion();
    VentaDAO ventaDAO = new VentaDAO();
    int id_cliente, id_venta;
    public VentaGUI() {
        obtenerTabla();
        obtenerComboBox();
        pedirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DetalleVentaGUI detalleVenta = new DetalleVentaGUI();
                detalleVenta.main();
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();

                obtenerIdCliente(cliente);
                Venta venta = new Venta(0,id_cliente,0,estado,"",cliente);

                if (ventaDAO.agregar(venta)){

                    JOptionPane.showMessageDialog(null, "Venta agregada exitosamente");
                    obtenerTabla();
                }

            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String cliente = comboBox1.getSelectedItem().toString();
                String estado = comboBox2.getSelectedItem().toString();
                obtenerIdCliente(cliente);

                Venta venta = new Venta(id_venta,id_cliente,0,estado,"",cliente);

                if (ventaDAO.actualizar(venta)) {

                    JOptionPane.showMessageDialog(null, "Venta actualizada exitosamente");
                    obtenerTabla();
                }

                obtenerTabla();

            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {

                    id_venta = Integer.parseInt(table1.getValueAt(selectedRow, 0).toString());
                    comboBox1.setSelectedItem(table1.getValueAt(selectedRow, 1).toString());
                    comboBox2.setSelectedItem(table1.getValueAt(selectedRow, 3).toString());


                }
            }
        });
    }



    public void obtenerComboBox() {

        Connection con = conexion.getConnection();

        try
        {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nombre FROM cliente");


            while (rs.next()){
                String nombre = rs.getString("nombre");
                comboBox1.addItem(nombre);
            }


        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public void obtenerIdCliente(String nombre) {
        Connection con = conexion.getConnection();

        try
        {

            String query = "SELECT id_cliente FROM cliente WHERE nombre = ?;";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                id_cliente = rs.getInt("id_cliente");
            }


        } catch (SQLException e){
            throw new RuntimeException(e);
        }


    }



    public  void obtenerTabla() {

        DefaultTableModel modelo = new DefaultTableModel();

        modelo.addColumn("Id_venta");
        modelo.addColumn("Id_cliente");
        modelo.addColumn("Total_Venta");
        modelo.addColumn("Estado");
        modelo.addColumn("Fecha y hora");


        table1.setModel(modelo);

        String[] dato = new String[5];
        Connection con = conexion.getConnection();

        try
        {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT venta.id_venta, cliente.nombre AS cliente, venta.total_venta, venta.estado, venta.fecha_hora FROM venta JOIN cliente ON venta.id_cliente = cliente.id_cliente;");

            while (rs.next()){
                dato[0] = rs.getString(1);
                dato[1] = rs.getString(2);
                dato[2] = rs.getString(3);
                dato[3] = rs.getString(4);
                dato[4] = rs.getString(5);






                modelo.addRow(dato);
            }
        }

        catch (SQLException e){
            e.printStackTrace();

        }


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventas");
        frame.setContentPane(new VentaGUI().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}

