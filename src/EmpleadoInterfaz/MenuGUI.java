package EmpleadoInterfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica principal del sistema que muestra el menú de opciones.
 * Desde aquí se puede acceder a todas las funcionalidades del sistema.
 *
 * <p>La interfaz incluye:
 * <ul>
 *   <li>Botones para acceder a todos los módulos del sistema</li>
 *   <li>Fondo con imagen personalizada</li>
 *   <li>Diseño responsivo que se adapta al tamaño de la ventana</li>
 * </ul>
 */
public class MenuGUI {

    // Componentes de la interfaz gráfica
    /**
     * Botón para acceder al sistema de chat interno.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Inicia el servidor de chat</li>
     *   <li>Abre la interfaz de comunicación</li>
     * </ol>
     * @see Servidor
     */
    private JButton chatButton;                     // Botón para acceder al chat

    /**
     * Botón para acceder al módulo de gestión de clientes.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual del menú</li>
     *   <li>Abre la interfaz de gestión de clientes</li>
     * </ol>
     * @see ClienteGUI
     */
    private JButton clienteButton;                  // Botón para gestión de clientes

    /**
     * Botón para acceder al módulo de gestión de productos.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual del menú</li>
     *   <li>Abre la interfaz de gestión de productos</li>
     * </ol>
     * @see ProductosGUI
     */
    private JButton productosButton;                // Botón para gestión de productos

    /**
     * Botón para acceder al módulo de gestión de ventas.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual del menú</li>
     *   <li>Abre la interfaz de gestión de ventas</li>
     * </ol>
     * @see VentaGUI
     */
    private JButton ventasButton;                   // Botón para gestión de ventas

    /**
     * Botón para acceder al módulo de movimientos financieros.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual del menú</li>
     *   <li>Abre la interfaz de movimientos financieros</li>
     * </ol>
     * @see MovimientoGUI
     */
    private JButton movimientosFinancierosButton;   // Botón para movimientos financieros

    /**
     * Panel principal que contiene todos los componentes de la interfaz.
     * <p>
     * Incluye un fondo personalizado y el menú de botones.
     */
    private JPanel main;                            // Panel principal

    /**
     * Botón para acceder al módulo de reportes y estadísticas.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual del menú</li>
     *   <li>Abre la interfaz de reportes</li>
     * </ol>
     * @see ReportesGUI
     */
    private JButton reportesButton;                 // Botón para reportes

    /**
     * Constructor que configura la interfaz gráfica del menú principal.
     * <p>
     * Realiza las siguientes acciones:
     * <ol>
     *   <li>Configura el panel principal con imagen de fondo</li>
     *   <li>Crea y estiliza los botones del menú</li>
     *   <li>Configura los listeners para cada botón</li>
     *   <li>Organiza los componentes en el layout</li>
     * </ol>
     */
    public MenuGUI() {
        // Panel con imagen de fondo escalable
        main = new JPanel() {
            private Image fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.jpg")).getImage();

            /**
             * Método para dibujar el fondo de la interfaz.
             * @param g Objeto Graphics usado para dibujar
             */
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        main.setLayout(new BorderLayout()); // Usar BorderLayout para agregar el menú arriba

        // Crear el panel de botones tipo menú en la parte superior
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centrado con espacio entre botones
        topMenu.setOpaque(false); // Hacerlo transparente para que se vea el fondo

        // Inicializar botones
        chatButton = new JButton("Chat");
        clienteButton = new JButton("Clientes");
        productosButton = new JButton("Productos");
        ventasButton = new JButton("Ventas");
        movimientosFinancierosButton = new JButton("Movimientos");
        reportesButton = new JButton("Reportes");

        // Configurar estilo de los botones
        Color buttonColor = Color.decode("#80F3ED");
        JButton[] botones = {chatButton, clienteButton, productosButton, ventasButton, movimientosFinancierosButton, reportesButton};
        for (JButton boton : botones) {
            boton.setBackground(buttonColor);
            boton.setForeground(Color.BLACK); // Texto blanco
            boton.setFocusPainted(false); // Quitar borde de selección al hacer clic
            boton.setBorderPainted(false); // Quitar borde del botón
        }

        // Agregar botones al menú superior
        topMenu.add(chatButton);
        topMenu.add(clienteButton);
        topMenu.add(productosButton);
        topMenu.add(ventasButton);
        topMenu.add(movimientosFinancierosButton);
        topMenu.add(reportesButton);

        // Agregar el panel de botones al panel principal en la parte superior
        main.add(topMenu, BorderLayout.NORTH);

        /**
         * Listener para el botón de Chat.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Inicia una nueva instancia del servidor de chat</li>
         *   <li>Muestra la interfaz del chat</li>
         * </ol>
         */
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Servidor serv = new Servidor();
                serv.main(null);  // Inicia el servidor de chat
            }
        });

        /**
         * Listener para el botón de Clientes.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Cierra la ventana actual del menú</li>
         *   <li>Abre la interfaz de gestión de clientes</li>
         * </ol>
         */
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

        /**
         * Listener para el botón de Productos.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Cierra la ventana actual del menú</li>
         *   <li>Abre la interfaz de gestión de productos</li>
         * </ol>
         */
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

        /**
         * Listener para el botón de Ventas.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Cierra la ventana actual del menú</li>
         *   <li>Abre la interfaz de gestión de ventas</li>
         * </ol>
         */
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

        /**
         * Listener para el botón de Movimientos Financieros.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Cierra la ventana actual del menú</li>
         *   <li>Abre la interfaz de movimientos financieros</li>
         * </ol>
         */
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

        /**
         * Listener para el botón de Reportes.
         * <p>
         * Al activarse:
         * <ol>
         *   <li>Cierra la ventana actual del menú</li>
         *   <li>Abre la interfaz de reportes</li>
         * </ol>
         */
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
        JFrame frame = new JFrame("Menu");
        frame.setContentPane(new MenuGUI().main);

        // Maximizar la ventana (pero con bordes visibles)
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
