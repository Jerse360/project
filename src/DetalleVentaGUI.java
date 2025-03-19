import javax.swing.*;

public class DetalleVentaGUI {
    private JComboBox comboBoxProducto;
    private JTextField precioProducto;
    private JSpinner spinner1;
    private JButton agregarButton;
    private JTable table1;
    private JButton cancelarButton;
    private JButton generarVentaButton;
    private JPanel Main;
    private JLabel idOrden;


    public static void main(String[] args) {
        JFrame frame = new JFrame("Detalle de Ventas");
        frame.setContentPane(new DetalleVentaGUI().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
