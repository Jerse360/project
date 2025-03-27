package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

/**
 * Interfaz gráfica para la gestión de movimientos financieros (ingresos y egresos).
 * Permite agregar, actualizar y eliminar movimientos, así como visualizar el estado de caja.
 */
public class MovimientoGUI {

    // Componentes de la interfaz gráfica
    private JPanel Main;                    // Panel principal
    private JTable table1;                  // Tabla para mostrar movimientos
    private JButton agregarButton;          // Botón para agregar movimiento
    private JButton actualizarButton;       // Botón para actualizar movimiento
    private JButton eliminarButton;         // Botón para eliminar movimiento
    private JComboBox comboBox1;            // Combo box para categorías de egresos
    private JTextField textField1;          // Campo para monto
    private JTextField textField2;          // Campo para ID (no editable)
    private JButton verCajaButton;          // Botón para ver estado de caja
    private JButton volverButton;           // Botón para volver al menú
    private JComboBox comboBoxCategoria;    // Combo box para categorías de ingresos
    private JComboBox comboBoxTipo;         // Combo box para tipo (Ingreso/Egreso)

    // Objetos para acceso a datos
    MovimientosDAO movimientosDAO = new MovimientosDAO();
    CajaGUI caja = new CajaGUI();

    /**
     * Constructor que inicializa la interfaz y configura los listeners.
     */
    public MovimientoGUI() {
        // Configuración inicial
        obtenerDatos();
        textField2.setEditable(false);
        comboBox1.setEnabled(false);
        comboBoxCategoria.setEnabled(false);

        // Listener para cambio en el tipo de movimiento (Ingreso/Egreso)
        comboBoxTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoSeleccionado = comboBoxTipo.getSelectedItem().toString();
                // Habilita/deshabilita combos según el tipo seleccionado
                if (tipoSeleccionado.equals("---")) {
                    comboBox1.setEnabled(false);
                    comboBoxCategoria.setEnabled(false);

                }
                else {
                    comboBoxCategoria.setEnabled("Ingreso".equals(tipoSeleccionado));
                    comboBox1.setEnabled("Egreso".equals(tipoSeleccionado));
                }
            }
        });

        // Listener para botón Agregar
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreTipo = String.valueOf(comboBoxTipo.getSelectedItem());
                if(nombreTipo.equals("---")){
                    JOptionPane.showMessageDialog(null,"Por favor seleccione un tipo de gasto");
                }
                else {

                String tipo = comboBoxTipo.getSelectedItem().toString();
                String categoria = tipo.equals("Ingreso") ?
                        comboBoxCategoria.getSelectedItem().toString() :
                        comboBox1.getSelectedItem().toString();
                int monto = Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(0, 0, monto, categoria, "", tipo);

                // Procesar según el tipo de movimiento
                boolean resultado = tipo.equals("Ingreso") ?
                        movimientosDAO.agregarIngreso(movimiento) :
                        movimientosDAO.agregarEgreso(movimiento);

                if (resultado) {
                    obtenerDatos(); // Actualizar tabla
                } else {
                    JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                }
                textField1.setText(""); // Limpiar campo
                }
            }
        });

        // Listener para botón Actualizar (solo egresos)
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());
                String categoria = comboBox1.getSelectedItem().toString();
                int monto = Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(id, 0, monto, categoria, "", "Egreso");
                movimientosDAO.actualizar(movimiento);
                obtenerDatos();
                caja.obtenerDatos(); // Actualizar estado de caja
            }
        });

        // Listener para botón Eliminar
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());
                movimientosDAO.eliminar(id);
                obtenerDatos();
                caja.obtenerDatos(); // Actualizar estado de caja
            }
        });

        // Listener para botón Ver Caja
        verCajaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(verCajaButton);
                menuFrame.dispose();
                caja.main(null); // Abrir interfaz de caja
            }
        });

        // Listener para botón Volver
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();
                new MenuGUI().main(null); // Volver al menú principal
            }
        });

        // Listener para selección en tabla
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectFilas = table1.getSelectedRow();
                if (selectFilas >= 0) {
                    // Obtener el valor de la tabla (puede ser negativo)
                    Object value = table1.getValueAt(selectFilas, 4);

                    // Convertir a número y aplicar valor absoluto
                    try {
                        int valor = Integer.parseInt(String.valueOf(value));
                        textField1.setText(String.valueOf(Math.abs(valor))); // Mostrar siempre positivo
                    } catch (NumberFormatException ex) {
                        textField1.setText(String.valueOf(value)); // Si no es número, mostrar tal cual
                    }

                    comboBoxTipo.setSelectedItem(table1.getValueAt(selectFilas, 3));
                    comboBoxCategoria.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    textField2.setText(String.valueOf(table1.getValueAt(selectFilas, 0)));

                    // Doble click para ver recibo
                    if (e.getClickCount() == 2) {
                        int id_venta = Integer.parseInt(String.valueOf(table1.getValueAt(selectFilas, 1)));
                        ReciboGUI reciboGUI = new ReciboGUI(id_venta);

                        JFrame frame = new JFrame("Recibo");
                        frame.setContentPane(reciboGUI.Main);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.pack();
                        frame.setSize(700, 700);
                        frame.setResizable(true);
                        frame.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Obtiene los movimientos financieros desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 1 && column != 0; // Columnas ID y ID_venta no editables
            }
        };

        model.setRowCount(0); // Limpiar tabla
        // Configurar columnas
        model.addColumn("ID_movimiento");
        model.addColumn("ID_venta");
        model.addColumn("Categoria");
        model.addColumn("Descripcion");
        model.addColumn("Monto");
        model.addColumn("Fecha");

        table1.setModel(model);

        try (Connection con = Conexion.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movimiento_financiero")) {

            // Llenar tabla con datos
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal para iniciar la interfaz.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Movimientos financieros");
        frame.setContentPane(new MovimientoGUI().Main);
        frame.pack();
        frame.setSize(1800, 600);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
