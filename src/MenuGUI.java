import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica principal del sistema que muestra el menú de opciones.
 * Desde aquí se puede acceder a todas las funcionalidades del sistema.
 */
public class MenuGUI {

    // Componentes de la interfaz gráfica
    private JButton chatButton;                     // Botón para acceder al chat
    private JButton clienteButton;                  // Botón para gestión de clientes
    private JButton productosButton;                // Botón para gestión de productos
    private JButton ventasButton;                   // Botón para gestión de ventas
    private JButton movimientosFinancierosButton;   // Botón para movimientos financieros
    private JPanel main;                            // Panel principal
    private JButton reportesButton;                 // Botón para reportes

    /**
     * Constructor que configura los listeners para los botones del menú.
     * Cada botón abre una interfaz diferente del sistema.
     */
    public MenuGUI() {

        // Listener para el botón de Chat
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Servidor serv = new Servidor();
                serv.main(null);  // Inicia el servidor de chat
            }
        });

        // Listener para el botón de Clientes
        clienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(clienteButton);
                menuFrame.dispose();

                // Abre la interfaz de gestión de clientes
                ClienteGUI cli = new ClienteGUI();
                cli.main(null);
            }
        });

        // Listener para el botón de Productos
        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(productosButton);
                menuFrame.dispose();

                // Abre la interfaz de gestión de productos
                ProductosGUI prod = new ProductosGUI();
                prod.main(null);
            }
        });

        // Listener para el botón de Ventas
        ventasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(ventasButton);
                menuFrame.dispose();

                // Abre la interfaz de gestión de ventas
                VentaGUI venta = new VentaGUI();
                venta.main(null);
            }
        });

        // Listener para el botón de Movimientos Financieros
        movimientosFinancierosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(movimientosFinancierosButton);
                menuFrame.dispose();

                // Abre la interfaz de movimientos financieros
                MovimientoGUI mov = new MovimientoGUI();
                mov.main(null);
            }
        });

        // Listener para el botón de Reportes
        reportesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(reportesButton);
                menuFrame.dispose();

                // Abre la interfaz de reportes
                ReportesGUI rep = new ReportesGUI();
                rep.main(null);
            }
        });
    }

    /**
     * Método principal que inicia la interfaz del menú.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configuración de la ventana principal
        JFrame frame = new JFrame("Menu");
        frame.setContentPane(new MenuGUI().main);
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comentado para evitar cierre completo

        frame.pack();
        frame.setSize(700, 700);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
