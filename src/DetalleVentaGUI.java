import javax.swing.*;

public class DetalleVentaGUI extends JFrame{
    private JComboBox comboBoxProducto;
    private JTextField precioProducto;
    private JSpinner spinner1;
    private JButton agregarButton;
    private JTable table1;
    private JButton cancelarButton;
    private JButton generarVentaButton;
    private JPanel Main;
    private JLabel idOrden;


    public DetalleVentaGUI(){
        setTitle("Detalle de Ventas");
        setContentPane(Main);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setResizable(false);

    }
}
