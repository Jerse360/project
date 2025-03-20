package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearCuentaGUI {
    private JPanel main;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JButton crearCuentaButton;

    Conexion conexion = new Conexion();
    LogGUI log = new LogGUI();

    public CrearCuentaGUI() {

        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = textField1.getText();
                    String cedula = textField2.getText();
                    String telefono = textField3.getText();
                    String direccion = textField4.getText();
                    String email = textField5.getText();

                    if (agregar(nombre,cedula,telefono,direccion,email)) {
                        JOptionPane.showMessageDialog(null, "Cuenta agregada correctamente");

                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese datos válidos del Cliente");
                }
            }
        });



    }

    public boolean agregar(String nombre, String cedula, String telefono, String direccion, String email) {
        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            return false;
        }

        if (log.validarCedula(cedula)) {
            if (log.validarCedulaEnBaseDeDatos(cedula) == false) {
        String query = "INSERT INTO cliente (nombre, cedula, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement pst = con.prepareStatement(query)){


            pst.setString(1, nombre);
            pst.setString(2, cedula);
            pst.setString(3, telefono);
            pst.setString(4, direccion);
            pst.setString(5, email);


            return pst.executeUpdate()>0;
        } catch (SQLException e) {

                e.printStackTrace();
                return false;
            }
        }
            else{
                JOptionPane.showMessageDialog(null, "Cedula ya existe");
            }
        }
        return false;
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Crear Cuenta");
        frame.setContentPane(new CrearCuentaGUI().main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);

    }
}
