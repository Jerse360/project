package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase que representa la interfaz gráfica para generar diferentes tipos de reportes de ventas.
 * Permite visualizar reportes diarios, semanales, mensuales, por cliente y por producto.
 */
public class ReportesGUI {
    // Componentes de la interfaz gráfica
    private JPanel main;                // Panel principal que contiene todos los componentes
    private JButton diariosButton;      // Botón para generar reportes diarios
    private JButton semanalesButton;    // Botón para generar reportes semanales
    private JButton mensualButton;      // Botón para generar reportes mensuales
    private JTable table1;              // Tabla para mostrar los resultados de los reportes
    private JButton clienteButton;      // Botón para generar reportes por cliente
    private JButton productosButton;    // Botón para generar reportes por producto
    private JButton volverButton;       // Botón para volver al menú principal

    // Objeto para manejar la conexión a la base de datos
    Conexion conexion = new Conexion();

    /**
     * Constructor de la clase ReportesGUI.
     * Configura los listeners para los botones y prepara la interfaz.
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
     * Método para obtener y mostrar los reportes de ventas diarias.
     * Consulta las ventas completadas en el día actual.
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
     * Agrupa las ventas por semana mostrando el total semanal.
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

        cargarDatosEnTabla(query, new String[]{"Fecha", "Semana", "Inicio Semana", "Fin Semana", "Venta Semana"});
    }

    /**
     * Método para obtener y mostrar los reportes de ventas mensuales.
     * Agrupa las ventas por mes mostrando el total mensual.
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
     * Muestra las ventas asociadas a cada cliente.
     */
    private void obtenerdatosClientes() {
        // Consulta SQL para obtener ventas por cliente
        String query = "SELECT cliente.nombre, venta.id_venta, venta.total_venta " +
                "FROM cliente JOIN venta ON cliente.id_cliente = venta.id_cliente " +
                "ORDER BY venta.total_venta DESC;";
        cargarDatosEnTabla(query, new String[]{"cliente", "id_venta", "Total"});
    }

    /**
     * Método para obtener y mostrar reportes de ventas por producto.
     * Muestra los productos vendidos y sus cantidades.
     */
    private void obtenerdatosProductos() {
        // Consulta SQL para obtener ventas por producto
        String query = "SELECT productos.nombre, cliente.nombre AS cliente, detalle_venta.cantidad " +
                "FROM detalle_venta " +
                "JOIN productos ON detalle_venta.id_producto = productos.id_producto " +
                "JOIN venta ON detalle_venta.id_venta = venta.id_venta " +
                "JOIN cliente ON venta.id_cliente = cliente.id_cliente";
        cargarDatosEnTabla(query, new String[]{"productos", "cliente", "cantidad"});
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
     *@param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Configurar y mostrar la ventana principal
                JFrame frame = new JFrame("Reportes de Ventas");
                frame.setContentPane(new ReportesGUI().main);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
