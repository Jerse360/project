import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class DetalleVentaGUI extends JFrame{
    private JComboBox comboBoxProducto;
    private JTextField precioProducto;
    private JSpinner spinner1;
    private JButton agregarButton;
    private JTable table1;
    private JButton eliminarButton;
    private JPanel Main;
    private JLabel idOrden;
    private JComboBox comboBox1;
    private JButton volverButton;

    Conexion conexion = new Conexion();

    Detalle_ventaDAO detalle_ventaDAO = new Detalle_ventaDAO();

    int precio, id_venta, id_producto, id_detalle;

    public DetalleVentaGUI(){

        precioProducto.setText(null);
        precioProducto.setEditable(false);
        obtenerDatos();
        obtenerComboBox();
        obtenerIdVenta();
        idOrden.setText(String.valueOf(id_venta));

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = String.valueOf(comboBoxProducto.getSelectedItem());

                if(nombreProducto.equals("---")){
                    JOptionPane.showMessageDialog(null,"Porfavor seleccione un producto");
                }
                else {
                    String tipo = comboBox1.getSelectedItem().toString();

                    if (tipo.equals("Unidad")){

                        String producto = String.valueOf(comboBoxProducto.getSelectedItem());
                        int cant = (int) spinner1.getValue();

                        obtenerIdProducto(producto);

                        Detalle_venta detalle_venta = new Detalle_venta(0,id_venta,id_producto,0,cant,tipo);

                        if (detalle_ventaDAO.agregarDetalleVenta(detalle_venta)){
                            JOptionPane.showMessageDialog(null,"Detalle venta agregado");

                            detalle_ventaDAO.actualizarTotal(detalle_venta);

                            obtenerDatos();

                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(null,"No funciono");
                    }
                }

            }
        });



    comboBox1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            String seleccion = (String) comboBox1.getSelectedItem();

            // Bloquear o desbloquear el spinner según la selección
            if (seleccion.equals("Unidad")) {
                spinner1.setEnabled(true); // Habilitar el spinner
            } else {
                spinner1.setEnabled(false); // Bloquear el spinner
            }

        }
    });

    comboBoxProducto.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            String producto = (String) comboBoxProducto.getSelectedItem();

            if (producto != null) {
                if (producto.equals("---")) {
                    precioProducto.setText(String.valueOf(0));

                }
                else {// Obtener el precio del producto desde la base de datos
                        precio = obtenerPrecio(producto);

                        // Mostrar el precio en el JTextField
                        precioProducto.setText(String.valueOf(precio));
                    }
            }

        }
    });

    eliminarButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            detalle_ventaDAO.restar(id_detalle, id_venta);

            if (detalle_ventaDAO.eliminar(id_detalle))
            {
                JOptionPane.showMessageDialog(null, "Pedido eliminado con exito");
            }
            obtenerDatos();



        }
    });

    volverButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
            volverFrame.dispose();

            VentaGUI venta = new VentaGUI();
            venta.main(null);
        }
    });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                id_detalle = Integer.parseInt(table1.getValueAt(selectedRow,0).toString());

                }
            }
        });
    }


    public void obtenerIdProducto(String nombre) {
        Connection con = conexion.getConnection();

        try
        {

            String query = "SELECT id_producto FROM productos WHERE nombre = ?;";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                id_producto = rs.getInt(1);
            }


        } catch (SQLException e){
            throw new RuntimeException(e);
        }


    }



    public void obtenerComboBox() {

        Connection con = conexion.getConnection();

        try
        {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nombre FROM productos");


            while (rs.next()){
                String nombre = rs.getString("nombre");
                comboBoxProducto.addItem(nombre);
            }


        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }


    public int obtenerPrecio(String nombre) {

        Connection con = conexion.getConnection();

        try
        {
            String query = "SELECT precio FROM productos WHERE nombre = ?";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, nombre);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    precio = rs.getInt("precio");
                }
            }


        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Error al obtener el precio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);        }

        return precio;
    }

    public void obtenerIdVenta(){

        Connection con = conexion.getConnection();

        try
        {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id_venta) FROM venta");


            while (rs.next()){
                id_venta = rs.getInt(1);
            }


        } catch (SQLException e){
            throw new RuntimeException(e);
        }

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
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_detalleVenta, id_venta, productos.nombre AS producto, precio_total, tipo,cantidad FROM detalle_venta JOIN productos ON detalle_venta.id_producto = productos.id_producto WHERE id_venta = (SELECT MAX(id_venta) FROM venta);");

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

    public static void main() {
        JFrame frame = new JFrame("Pedido");
        frame.setContentPane(new DetalleVentaGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 600);
        frame.setResizable(true);
        frame.setVisible(true);

    }
}
