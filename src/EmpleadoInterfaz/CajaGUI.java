package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
/**
 * Interfaz gráfica para la gestión y visualización del estado de la caja.
 * Muestra el saldo actual y permite volver a la interfaz de movimientos.
 */
public class CajaGUI {

    // Componentes de la interfaz gráfica
    private JPanel Main;          // Panel principal que contiene todos los componentes

    /**
     * Tabla para visualizar los datos del estado de caja.
     * Muestra las columnas: Id_caja, Concepto y Total.
     */
    private JTable table1;

    /**
     * Botón para regresar a la interfaz de movimientos.
     * @see MovimientoGUI
     */
    private JButton volverButton;

    private Conexion conexion = new Conexion(); // Objeto para conexión a BD

    /**
     * Constructor de la clase CajaGUI.
     * Inicializa la interfaz, actualiza el saldo y carga los datos.
     * Configura el ActionListener para el botón volverButton.
     */
    public CajaGUI() {
        actualizarCaja();  // Actualiza el saldo en caja
        obtenerDatos();    // Carga los datos en la tabla

        /**
         * ActionListener para el botón volverButton.
         * @action Cierra la ventana actual y abre la interfaz de movimientos
         * @see MovimientoGUI
         */
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();

                // Abre la interfaz de movimientos
                MovimientoGUI movimientoGUI = new MovimientoGUI();
                movimientoGUI.main(null);
            }
        });
    }

    /**
     * Actualiza el valor total en caja sumando todos los movimientos financieros.
     * @return true si la actualización fue exitosa, false en caso de error
     */
    public boolean actualizarCaja() {
        Connection con = conexion.getConnection();

        try {
            // Query para actualizar el valor de caja con la suma de movimientos
            String query = "UPDATE `caja` SET Valor = (SELECT SUM(movimiento_financiero.monto) FROM movimiento_financiero)";

            PreparedStatement pst = con.prepareStatement(query);
            pst.executeUpdate();

            return true; // Operación exitosa

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtiene los datos de caja desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        // Configuración del modelo de tabla
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0); // Limpiar tabla
        model.addColumn("Id_caja");
        model.addColumn("Concepto");
        model.addColumn("Total");

        table1.setModel(model); // Asignar modelo a la tabla

        String[] dato = new String[3]; // Array para almacenar filas de datos

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos");
            return;
        }

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM caja")) {

            // Llenar la tabla con los datos de la consulta
            while (rs.next()) {
                dato[0] = rs.getString(1); // Id_caja
                dato[1] = rs.getString(2); // Concepto
                dato[2] = rs.getString(3); // Total

                model.addRow(dato); // Agregar fila al modelo
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal para iniciar la interfaz de caja.
     *@param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configuración de la ventana principal
        JFrame frame = new JFrame("Caja");
        frame.setContentPane(new CajaGUI().Main);
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Comentado para evitar cierre completo
        frame.pack();
        frame.setSize(400, 150);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
