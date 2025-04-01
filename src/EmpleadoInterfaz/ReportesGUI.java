package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase que representa la interfaz gráfica para generar diferentes tipos de reportes de ventas.
 * Permite visualizar reportes diarios, semanales, mensuales, por cliente, por producto y de stock mínimo.
 *
 * <p>La interfaz proporciona:
 * <ul>
 *   <li>Generación de reportes con diferentes criterios de agrupación</li>
 *   <li>Visualización tabular de resultados</li>
 *   <li>Integración con la base de datos para obtener datos actualizados</li>
 *   <li>Navegación de regreso al menú principal</li>
 * </ul>
 */
public class ReportesGUI {
    // Componentes de la interfaz gráfica
    private JPanel main;                // Panel principal que contiene todos los componentes

    /**
     * Botón para generar reportes de ventas diarias.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Consulta las ventas completadas en el día actual</li>
     *   <li>Muestra el total de ventas por fecha</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton diariosButton;      // Botón para generar reportes diarios

    /**
     * Botón para generar reportes de ventas semanales.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Agrupa las ventas por semana</li>
     *   <li>Muestra el rango de fechas de cada semana</li>
     *   <li>Muestra el total de ventas por semana</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton semanalesButton;    // Botón para generar reportes semanales

    /**
     * Botón para generar reportes de ventas mensuales.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Agrupa las ventas por mes</li>
     *   <li>Muestra el total de ventas por mes</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton mensualButton;      // Botón para generar reportes mensuales

    /**
     * Tabla para mostrar los resultados de los reportes.
     * <p>
     * Muestra los datos según el tipo de reporte seleccionado.
     */
    private JTable table1;              // Tabla para mostrar los resultados de los reportes

    /**
     * Botón para generar reportes de ventas por cliente.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Muestra las ventas asociadas a cada cliente</li>
     *   <li>Ordena los resultados por monto total descendente</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton clienteButton;      // Botón para generar reportes por cliente

    /**
     * Botón para generar reportes de ventas por producto.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Muestra los productos vendidos</li>
     *   <li>Incluye información del cliente comprador</li>
     *   <li>Muestra las cantidades vendidas</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton productosButton;    // Botón para generar reportes por producto

    /**
     * Botón para volver al menú principal.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Cierra la ventana actual de reportes</li>
     *   <li>Abre la interfaz del menú principal</li>
     * </ol>
     * @see MenuGUI
     */
    private JButton volverButton;       // Botón para volver al menú principal

    /**
     * Botón para generar reportes de productos con stock mínimo.
     * <p>
     * Al hacer clic:
     * <ol>
     *   <li>Muestra productos con stock por debajo del mínimo requerido</li>
     *   <li>Incluye información de stock actual y mínimo</li>
     *   <li>Actualiza la tabla con los resultados</li>
     * </ol>
     */
    private JButton stockMinimoButton;  // Botón para reporte de stock mínimo

    // Objeto para manejar la conexión a la base de datos
    private Conexion conexion = new Conexion();

    /**
     * Constructor de la clase ReportesGUI.
     * Configura los listeners para los botones y prepara la interfaz.
     *
     * <p>Inicializa:
     * <ol>
     *   <li>Listeners para todos los botones de reportes</li>
     *   <li>Configuración de acciones para cada tipo de reporte</li>
     *   <li>Listener para el botón de volver al menú</li>
     * </ol>
     */
    public ReportesGUI() {
        // Configurar acción para el botón de reportes diarios
        diariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosDiarios();
            }
        });

        // Configurar acción para el botón de reportes semanales
        semanalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosSemanales();
            }
        });

        // Configurar acción para el botón de reportes mensuales
        mensualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosMensuales();
            }
        });

        // Configurar acción para el botón de reportes por cliente
        clienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosClientes();
            }
        });

        // Configurar acción para el botón de reportes por producto
        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosProductos();
            }
        });

        // Configurar acción para el botón de reportes de stock mínimo
        stockMinimoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stockminimo();
            }
        });

        // Configurar acción para el botón de volver al menú principal
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la ventana actual
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                // Abrir el menú principal
                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });
    }

    /**
     * Genera un reporte de productos con stock por debajo del mínimo requerido.
     * <p>
     * Muestra:
     * <ul>
     *   <li>ID del producto</li>
     *   <li>Nombre del producto</li>
     *   <li>Stock actual</li>
     *   <li>Stock mínimo requerido</li>
     * </ul>
     */
    public void stockminimo(){
        String query = "SELECT id_producto, nombre, stock, stock_minimo FROM productos WHERE stock < stock_minimo;";
        cargarDatosEnTabla(query, new String[]{"id_producto", "nombre", "stock", "stock_minimo"});
    }

    /**
     * Método para obtener y mostrar los reportes de ventas diarias.
     * <p>
     * Consulta las ventas completadas en el día actual y muestra:
     * <ul>
     *   <li>Fecha de la venta</li>
     *   <li>Total de ventas del día</li>
     * </ul>
     */
    private void obtenerdatosDiarios() {
        // Consulta SQL para obtener ventas del día actual
        String query = "SELECT DATE(venta.fecha_hora) AS Fecha, SUM(venta.total_venta) AS total_diario " +
                "FROM venta WHERE estado = 'Entregado' AND DATE(venta.fecha_hora) = CURRENT_DATE " +
                "GROUP BY venta.fecha_hora ORDER BY venta.fecha_hora DESC;";
        cargarDatosEnTabla(query, new String[]{"Fecha", "Total"});
    }

    /**
     * Método para obtener y mostrar los reportes de ventas semanales.
     * <p>
     * Agrupa las ventas por semana mostrando:
     * <ul>
     *   <li>Año y número de semana</li>
     *   <li>Fecha de inicio de semana</li>
     *   <li>Fecha de fin de semana</li>
     *   <li>Total de ventas semanales</li>
     * </ul>
     */
    public void obtenerdatosSemanales() {
        // Consulta SQL para obtener ventas agrupadas por semana
        String query = "SELECT YEAR(venta.fecha_hora) AS año, WEEK(venta.fecha_hora, 1) AS semana, " +
                "MIN(DATE(venta.fecha_hora)) AS inicio_semana, " +
                "DATE_ADD(MIN(DATE(venta.fecha_hora)), INTERVAL 6 DAY) AS fin_semana, " +
                "SUM(venta.total_venta) AS venta_semanal " +
                "FROM venta WHERE venta.estado = 'Entregado' " +
                "GROUP BY YEAR(venta.fecha_hora), WEEK(venta.fecha_hora, 1) " +
                "ORDER BY año DESC, semana DESC";

        cargarDatosEnTabla(query, new String[]{"Año", "Semana", "Inicio Semana", "Fin Semana", "Venta Semana"});
    }

    /**
     * Método para obtener y mostrar los reportes de ventas mensuales.
     * <p>
     * Agrupa las ventas por mes mostrando:
     * <ul>
     *   <li>Mes y año (formato YYYY-MM)</li>
     *   <li>Total de ventas mensuales</li>
     * </ul>
     */
    private void obtenerdatosMensuales() {
        // Consulta SQL para obtener ventas agrupadas por mes
        String query = "SELECT DATE_FORMAT(venta.fecha_hora, '%Y-%m') AS mes, " +
                "SUM(venta.total_venta) AS total_mensual " +
                "FROM venta WHERE estado = 'Entregado' " +
                "GROUP BY DATE_FORMAT(venta.fecha_hora, '%Y-%m') ORDER BY mes DESC;";
        cargarDatosEnTabla(query, new String[]{"Mes", "Total Mensual"});
    }

    /**
     * Método para obtener y mostrar reportes de ventas por cliente.
     * <p>
     * Muestra:
     * <ul>
     *   <li>Nombre del cliente</li>
     *   <li>ID de la venta</li>
     *   <li>Total de la venta</li>
     * </ul>
     * Los resultados están ordenados por monto total descendente.
     */
    private void obtenerdatosClientes() {
        // Consulta SQL para obtener ventas por cliente
        String query = "SELECT cliente.nombre, venta.id_venta, venta.total_venta " +
                "FROM cliente JOIN venta ON cliente.id_cliente = venta.id_cliente " +
                "ORDER BY venta.total_venta DESC;";
        cargarDatosEnTabla(query, new String[]{"Cliente", "ID Venta", "Total"});
    }

    /**
     * Método para obtener y mostrar reportes de ventas por producto.
     * <p>
     * Muestra:
     * <ul>
     *   <li>Nombre del producto</li>
     *   <li>Nombre del cliente comprador</li>
     *   <li>Cantidad vendida</li>
     * </ul>
     */
    private void obtenerdatosProductos() {
        // Consulta SQL para obtener ventas por producto
        String query = "SELECT productos.nombre, cliente.nombre AS cliente, detalle_venta.cantidad " +
                "FROM detalle_venta " +
                "JOIN productos ON detalle_venta.id_producto = productos.id_producto " +
                "JOIN venta ON detalle_venta.id_venta = venta.id_venta " +
                "JOIN cliente ON venta.id_cliente = cliente.id_cliente";
        cargarDatosEnTabla(query, new String[]{"Producto", "Cliente", "Cantidad"});
    }

    /**
     * Método auxiliar para cargar datos en la tabla a partir de una consulta SQL.
     *
     * @param query Consulta SQL a ejecutar
     * @param columnNames Nombres de las columnas para la tabla
     */
    private void cargarDatosEnTabla(String query, String[] columnNames) {
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Crear modelo de tabla con las columnas especificadas
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columnNames);

            // Llenar el modelo con los datos del ResultSet
            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            // Asignar el modelo a la tabla
            table1.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método principal para ejecutar la interfaz de reportes.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Reportes de Ventas");
                frame.setContentPane(new ReportesGUI().main);

                // Solo maximiza (conserva bordes)
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
