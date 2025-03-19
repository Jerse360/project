import javax.swing.*;

public class VentaGUI {
    private JLabel Venta;
    private JTable table1;
    private JComboBox comboBox1;
    private JPanel Main;
    private JComboBox comboBox2;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton pedirButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventas");
        frame.setContentPane(new VentaGUI().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
