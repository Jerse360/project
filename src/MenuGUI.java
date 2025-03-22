import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGUI {

    private JButton chatButton;
    private JButton clienteButton;
    private JButton productosButton;
    private JButton ventasButton;
    private JButton movimientosFinancierosButton;
    private JPanel main;
    private JButton reportesButton;

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

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(clienteButton);
                menuFrame.dispose();

                ClienteGUI cli = new ClienteGUI();

                cli.main(null);
            }
        });

        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(productosButton);
                menuFrame.dispose();

                ProductosGUI prod = new ProductosGUI();
                prod.main(null);

            }
        });

        ventasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(ventasButton);
                menuFrame.dispose();

                VentaGUI venta = new VentaGUI();
                venta.main(null);

            }
        });

        movimientosFinancierosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(movimientosFinancierosButton);
                menuFrame.dispose();

                MovimientoGUI mov = new MovimientoGUI();
                mov.main(null);
            }
        });

        reportesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(reportesButton);
                menuFrame.dispose();

                ReportesGUI rep = new ReportesGUI();
                rep.main(null);

            }
        });

    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu");
        frame.setContentPane(new MenuGUI().main);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
