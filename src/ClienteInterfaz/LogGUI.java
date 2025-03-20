package ClienteInterfaz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogGUI {
    private JPanel main;
    private JButton iniciarSesionButton;
    private JButton crearCuentaButton;

    private Conexion conexion = new Conexion();

    public LogGUI() {

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = JOptionPane.showInputDialog(null,"Ingrese su numero de cedula");

                if (validarCedula(input) == true){

                    if (validarCedulaEnBaseDeDatos(input)) {
                        Cliente cliente = new Cliente();
                        cliente.main(null);

                    }
                    else {
                        JOptionPane.showMessageDialog(null, "La cédula " + input + " no se encuentra en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }



            }
        });

        crearCuentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CrearCuentaGUI crearCuentaGUI = new CrearCuentaGUI();
                crearCuentaGUI.main(null);

            }
        });

    }



    public boolean validarCedulaEnBaseDeDatos(String cedula) {
        Connection con = conexion.getConnection();
        String query = "SELECT cedula FROM cliente WHERE cedula = ?";
        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, cedula);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;

        }

    }



public boolean validarCedula(String input) {

    try {
            if (input == null || input.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Entrada inválida. No se ingresó ningún valor.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;

            }

            int cedula = Integer.parseInt(input);

            if (cedula <= 0 || cedula < 111111111) {
                JOptionPane.showMessageDialog(null, "Número de cédula inválido. Debe ser un número positivo y tener máximo 9 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }
            catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(null, "Entrada inválida. Debe ingresar un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    return true;
};



    public static void main(String[] args) {
        JFrame frame = new JFrame("Inicio de sesion");
        frame.setContentPane(new LogGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);

    }

}
