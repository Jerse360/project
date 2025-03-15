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

public class ProductosGUI {
    private JTextField txtNombre;
    private JTextField txtCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    private JButton agregarButton;
    private JButton verButton;
    private JButton actualizarButton; // CORREGIDO (antes era "actuaizarButton")
    private JButton eliminarButton;
    private JTable table1;
    private JTextField txtId;
    private JPanel Main;
    private JComboBox comboBox1;
    private JTextField textField1;
    private ProductosDAO productosDAO; // Se necesita una instancia del DAO

    public ProductosGUI() {
        productosDAO = new ProductosDAO(); // Instanciar DAO

        textField1.setEnabled(false);
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    Productos producto = new Productos(0, nombre, categoria, precio, stock, stockMinimo);

                    if (productosDAO.agregarProducto(producto)) {
                        JOptionPane.showMessageDialog(null, "Producto agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos");
                }
            }
        });



        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = txtNombre.getText();
                    String categoria = comboBox1.getSelectedItem().toString();
                    int precio = Integer.parseInt(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    int stockMinimo = Integer.parseInt(txtStockMinimo.getText());

                    if (productosDAO.actualizarProducto(id, nombre, categoria, precio, stock, stockMinimo)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    if (productosDAO.eliminarProducto(id)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el producto");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido");
                }
            }
        });



        obtenerDatos();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int selectFilas = table1.getSelectedRow();

                if (selectFilas >=0) {

                    textField1.setText((String.valueOf( table1.getValueAt(selectFilas,0))));
                    txtNombre.setText((String.valueOf( table1.getValueAt(selectFilas,1))));
                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas,2));
                    txtPrecio.setText((String.valueOf( table1.getValueAt(selectFilas,3))));
                    txtStock.setText((String.valueOf( table1.getValueAt(selectFilas,4))));
                    txtStockMinimo.setText((String.valueOf( table1.getValueAt(selectFilas,5))));



                }
            }
        });
    }

    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Categoría");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Stock Mínimo");

        table1.setModel(model);

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM productos")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("precio"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo")
                };
                model.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Productos");
        frame.setContentPane(new ProductosGUI().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

