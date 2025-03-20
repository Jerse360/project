import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ServiceConfigurationError;

public class MenuGUI {
    private JButton chatButton;
    private JButton clienteButton;
    private JButton productosButton;
    private JButton ventasButton;
    private JButton movimientosFinancierosButton;
    private JPanel main;

    public MenuGUI() {

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Servidor serv = new Servidor();
                serv.main(null);
            }
        });

        clienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClienteGUI cli = new ClienteGUI();

                cli.main(null);
            }
        });

        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductosGUI prod = new ProductosGUI();
                prod.main(null);

            }
        });

        ventasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VentaGUI venta = new VentaGUI();
                venta.main(null);

            }
        });

        movimientosFinancierosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovimientoGUI mov = new MovimientoGUI();
                mov.main(null);
            }
        });

    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu");
        frame.setContentPane(new MenuGUI().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
