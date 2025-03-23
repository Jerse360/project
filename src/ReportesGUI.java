import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ReportesGUI {
    private JPanel main;
    private JButton diariosButton;
    private JButton semanalesButton;
    private JButton mensualButton;
    private JTable table1;
    private JButton clienteButton;
    private JButton productosButton;
    private JButton volverButton;

    Conexion conexion = new Conexion();

    public ReportesGUI() {
        diariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosDiarios();
            }
        });

        semanalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosSemanales();
            }
        });

        mensualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosMensuales();
            }
        });

        clienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosClientes();
            }
        });

        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerdatosProductos();
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });
    }

    private void obtenerdatosDiarios() {
        String query = "SELECT DATE(venta.fecha_hora) AS Fecha, SUM(venta.total_venta) AS total_diario FROM venta WHERE estado = 'Entregado' AND DATE(venta.fecha_hora) = CURRENT_DATE GROUP BY venta.fecha_hora ORDER BY venta.fecha_hora DESC;";
        cargarDatosEnTabla(query, new String[]{"Fecha", "Total"});
    }

    public void obtenerdatosSemanales() {


        String query = "SELECT YEAR(venta.fecha_hora) AS año, WEEK(venta.fecha_hora, 1) AS semana, MIN(DATE(venta.fecha_hora)) AS inicio_semana, DATE_ADD(MIN(DATE(venta.fecha_hora)), INTERVAL 6 DAY) AS fin_semana, SUM(venta.total_venta) AS venta_semanal FROM venta WHERE venta.estado = 'Entregado' GROUP BY YEAR(venta.fecha_hora), WEEK(venta.fecha_hora, 1) ORDER BY año DESC, semana DESC";

        cargarDatosEnTabla(query, new String[]{"Fecha", "Semana", "Inicio Semana", "Fin Semana", "Venta Semana"});
    }

    private void obtenerdatosMensuales() {
        String query = "SELECT DATE_FORMAT(venta.fecha_hora, '%Y-%m') AS mes, SUM(venta.total_venta) AS total_mensual FROM venta WHERE estado = 'Entregado' GROUP BY DATE_FORMAT(venta.fecha_hora, '%Y-%m') ORDER BY mes DESC;";
        cargarDatosEnTabla(query, new String[]{"Mes", "Total Mensual"});
    }

    private void obtenerdatosClientes() {
        String query = "SELECT cliente.nombre, venta.id_venta, venta.total_venta FROM cliente JOIN venta ON cliente.id_cliente = venta.id_cliente ORDER BY venta.total_venta DESC;";
        cargarDatosEnTabla(query, new String[]{"cliente", "id_venta", "Total"});
    }

    private void obtenerdatosProductos() {
        String query = "SELECT productos.nombre, cliente.nombre AS cliente, detalle_venta.cantidad FROM detalle_venta " +
                "JOIN productos ON detalle_venta.id_producto = productos.id_producto " +
                "JOIN venta ON detalle_venta.id_venta = venta.id_venta " +
                "JOIN cliente ON venta.id_cliente = cliente.id_cliente";
        cargarDatosEnTabla(query, new String[]{"productos", "cliente", "cantidad"});
    }

    private void cargarDatosEnTabla(String query, String[] columnNames) {
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            table1.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Reportes de Ventas");
                frame.setContentPane(new ReportesGUI().main);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
