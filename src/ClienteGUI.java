
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteGUI {

    private JTable table1;
    private JPanel Main;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton volverButton;
    private ClienteDAO clienteDAO;

    public ClienteGUI() {
        clienteDAO = new ClienteDAO();
        textField1.setEnabled(false);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();

                    ClienteSetGet clienteSetGet = new ClienteSetGet(0, nombre, cedula, telefono, direccion, email);
                    ClienteDAO clienteDAO = new ClienteDAO();

                    if (clienteDAO.agregar(clienteSetGet)) {
                        JOptionPane.showMessageDialog(null, "Cliente agregado correctamente");
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos del Cliente");
                }
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField1.getText());
                    String nombre = textField2.getText();
                    String cedula = textField3.getText();
                    String telefono = textField4.getText();
                    String direccion = textField5.getText();
                    String email = textField6.getText();



                    if (clienteDAO.actualizar(id,cedula,nombre,direccion,telefono,email)) {
                        limpiarCampos();
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar Cliente");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese un ID válido para actualizar");
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              try{
                  int id = Integer.parseInt(textField1.getText());
                  if(clienteDAO.eliminar(id)){
                      limpiarCampos();
                      obtenerDatos();
                  }else{
                      JOptionPane.showMessageDialog(null,"Error al eliminar el Cliente");
                  }
              } catch (NumberFormatException ex){
                  JOptionPane.showMessageDialog(null,"Ingrese un Id Valido");
              }
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame clienteFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                clienteFrame.dispose();

                MenuGUI menuGUI = new MenuGUI();

                menuGUI.main(null);

            }
        });

        obtenerDatos();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    textField1.setText(String.valueOf(table1.getValueAt(fila, 0).toString()));
                    textField2.setText(String.valueOf(table1.getValueAt(fila, 1).toString()));
                    textField3.setText(String.valueOf(table1.getValueAt(fila, 2).toString()));
                    textField4.setText(String.valueOf(table1.getValueAt(fila, 3).toString()));
                    textField5.setText(String.valueOf(table1.getValueAt(fila, 4).toString()));
                    textField6.setText(String.valueOf(table1.getValueAt(fila, 5).toString()));
                }
            }
        });

    }
    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Cedula");
        model.addColumn("Telefono");
        model.addColumn("Direccion");
        model.addColumn("ClienteInterfaz.Email");

        table1.setModel(model);

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cliente")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("cedula"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email")
                };
                model.addRow(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void limpiarCampos() {
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        textField6.setText("");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Clientes");
        frame.setContentPane(new ClienteGUI().Main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
