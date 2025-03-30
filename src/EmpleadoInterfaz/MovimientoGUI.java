package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.TableRowSorter;

/**
 * Interfaz gráfica para la gestión de movimientos financieros (ingresos y egresos).
 */
public class MovimientoGUI {
    private JPanel Main;
    private TableRowSorter<DefaultTableModel> sorter;
    private NonEditableTableModel model;
    private JTable table1;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton verCajaButton;
    private JButton volverButton;
    private JComboBox comboBoxCategoria;
    private JComboBox comboBoxTipo;
    private JTextField buscar;
    private MovimientosDAO movimientosDAO = new MovimientosDAO();
    private CajaGUI caja = new CajaGUI();

    /**
     * Constructor que inicializa la interfaz y configura los listeners.
     */
    public MovimientoGUI() {
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);
        obtenerDatos();

        textField2.setEditable(false);
        comboBox1.setEnabled(false);
        comboBoxCategoria.setEnabled(false);

        comboBoxTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipoSeleccionado = comboBoxTipo.getSelectedItem().toString();
                if (tipoSeleccionado.equals("---")) {
                    comboBox1.setEnabled(false);
                    comboBoxCategoria.setEnabled(false);
                } else {
                    comboBoxCategoria.setEnabled("Ingreso".equals(tipoSeleccionado));
                    comboBox1.setEnabled("Egreso".equals(tipoSeleccionado));
                }
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreTipo = String.valueOf(comboBoxTipo.getSelectedItem());
                if(nombreTipo.equals("---")) {
                    JOptionPane.showMessageDialog(null,"Por favor seleccione un tipo de gasto");
                } else {
                    String tipo = comboBoxTipo.getSelectedItem().toString();
                    String categoria = tipo.equals("Ingreso") ?
                            comboBoxCategoria.getSelectedItem().toString() :
                            comboBox1.getSelectedItem().toString();
                    int monto = Integer.parseInt(textField1.getText());

                    Movimiento movimiento = new Movimiento(0, 0, monto, categoria, "", tipo);

                    boolean resultado = tipo.equals("Ingreso") ?
                            movimientosDAO.agregarIngreso(movimiento) :
                            movimientosDAO.agregarEgreso(movimiento);

                    if (resultado) {
                        obtenerDatos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al agregar movimiento");
                    }
                    textField1.setText("");
                }
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());
                String categoria = comboBox1.getSelectedItem().toString();
                int monto = Integer.parseInt(textField1.getText());

                Movimiento movimiento = new Movimiento(id, 0, monto, categoria, "", "Egreso");
                movimientosDAO.actualizar(movimiento);
                obtenerDatos();
                caja.obtenerDatos();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());
                movimientosDAO.eliminar(id);
                obtenerDatos();
                caja.obtenerDatos();
            }
        });

        verCajaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(verCajaButton);
                menuFrame.dispose();
                caja.main(null);
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

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectFilas = table1.getSelectedRow();
                if (selectFilas >= 0) {
                    Object value = table1.getValueAt(selectFilas, 4);
                    try {
                        int valor = Integer.parseInt(String.valueOf(value));
                        textField1.setText(String.valueOf(Math.abs(valor)));
                    } catch (NumberFormatException ex) {
                        textField1.setText(String.valueOf(value));
                    }

                    comboBoxTipo.setSelectedItem(table1.getValueAt(selectFilas, 3));
                    comboBoxCategoria.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    comboBox1.setSelectedItem(table1.getValueAt(selectFilas, 2));
                    textField2.setText(String.valueOf(table1.getValueAt(selectFilas, 0)));

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

        buscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String buscarText = buscar.getText().trim().toLowerCase();
                if (sorter != null) {
                    RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                if (entry.getStringValue(i).toLowerCase().contains(buscarText)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    /**
     * Obtiene los movimientos financieros desde la base de datos y los muestra en la tabla.
     */
    public void obtenerDatos() {
        if (sorter != null) {
            table1.setRowSorter(null);
        }

        NonEditableTableModel model = new NonEditableTableModel();
        model.setRowCount(0);
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

            sorter = new TableRowSorter<>(model);
            table1.setRowSorter(sorter);

            if (!buscar.getText().trim().isEmpty()) {
                buscar.setText(buscar.getText());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modelo de tabla personalizado que impide la edición de celdas.
     */
    public class NonEditableTableModel extends DefaultTableModel {
        /**
         * Determina si una celda es editable.
         * @param row el índice de la fila
         * @param column el índice de la columna
         * @return siempre false para bloquear la edición
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * Método principal para iniciar la aplicación.
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Movimientos financieros");
        frame.setContentPane(new MovimientoGUI().Main);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}