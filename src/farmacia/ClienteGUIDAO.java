package farmacia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ClienteGUIDAO implements ActionListener {

    private JTable table1;
    private JPanel main;
    private JTextField textField1; // ID Cliente
    private JTextField textField2; // Cédula
    private JTextField textField3; // Nombre
    private JTextField textField4; // Dirección
    private JTextField textField5; // Teléfono
    private JTextField textField6; // Email
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton verButton;
    private JButton eliminarButton;
    private Conexion conexion = new Conexion();
    private ClientesDAO clienteDAO = new ClientesDAO();

    public ClienteGUIDAO() {
        main = new JPanel(); // Se inicializa el panel para evitar errores
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS)); // Layout para organizar los componentes

        textField1 = new JTextField(1);
        textField2 = new JTextField(1);
        textField3 = new JTextField(1);
        textField4 = new JTextField(1);
        textField5 = new JTextField(1);
        textField6 = new JTextField(1);

        agregarButton = new JButton("Agregar");
        actualizarButton = new JButton("Actualizar");
        verButton = new JButton("Ver Clientes");
        eliminarButton = new JButton("Eliminar");

        main.add(new JLabel("ID Cliente:"));
        main.add(textField1);
        main.add(new JLabel("Cédula:"));
        main.add(textField2);
        main.add(new JLabel("Nombre:"));
        main.add(textField3);
        main.add(new JLabel("Dirección:"));
        main.add(textField4);
        main.add(new JLabel("Teléfono:"));
        main.add(textField5);
        main.add(new JLabel("Email:"));
        main.add(textField6);

        main.add(agregarButton);
        main.add(actualizarButton);
        main.add(verButton);
        main.add(eliminarButton);

        for (JButton jButton : Arrays.asList(agregarButton, actualizarButton, verButton, eliminarButton)) {
            jButton.addActionListener(this);
        }
    }

    class ClienteGUI {
        int id_cliente;
        String cedula, nombre, direccion, telefono, email;

        public ClienteGUI(int id_cliente, String cedula, String nombre, String direccion, String telefono, String email) {
            this.id_cliente = id_cliente;
            this.cedula = cedula;
            this.nombre = nombre;
            this.direccion = direccion;
            this.telefono = telefono;
            this.email = email;
        }

        public String getCedula() { return cedula; }
        public String getNombre() { return nombre; }
        public String getDireccion() { return direccion; }
        public String getTelefono() { return telefono; }
        public String getEmail() { return email; }
    }

    class ClientesDAO {
        public void agregar(ClienteGUI clienteGUI) {
            Connection con = conexion.getConnection();
            String query = "INSERT INTO cliente (cedula, nombre, direccion, telefono, email, id_cliente) VALUES (?,?,?,?,?,0)"; // Sin id_cliente

            try {
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, clienteGUI.getCedula());
                pst.setString(2, clienteGUI.getNombre());
                pst.setString(3, clienteGUI.getDireccion());
                pst.setString(4, clienteGUI.getTelefono());
                pst.setString(5, clienteGUI.getEmail());

                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Cliente agregado con éxito");
                pst.close();
                con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al agregar cliente: " + e.getMessage());
            }
        }

        public void verTodos() {
            Connection con = conexion.getConnection();
            StringBuilder clientesInfo = new StringBuilder();
            String query = "SELECT * FROM cliente";

            try {
                PreparedStatement pst = con.prepareStatement(query);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    clientesInfo.append("ID: ").append(rs.getInt("id_cliente"))
                            .append("\nCédula: ").append(rs.getString("cedula"))
                            .append("\nNombre: ").append(rs.getString("nombre"))
                            .append("\nDirección: ").append(rs.getString("direccion"))
                            .append("\nTeléfono: ").append(rs.getString("telefono"))
                            .append("\nEmail: ").append(rs.getString("email"))
                            .append("\n-------------------------\n");
                }
                JOptionPane.showMessageDialog(null, clientesInfo.toString(), "Lista de Clientes", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                pst.close();
                con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al obtener la lista de clientes: " + e.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == agregarButton) {
            ClienteGUI cliente = new ClienteGUI(0, textField2.getText(), textField3.getText(), textField4.getText(), textField5.getText(), textField6.getText());
            clienteDAO.agregar(cliente);
        }

        if (e.getSource() == verButton) {
            clienteDAO.verTodos();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestión de Clientes");
            ClienteGUIDAO clienteGUI = new ClienteGUIDAO();
            frame.setContentPane(clienteGUI.main);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
