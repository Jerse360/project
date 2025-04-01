package EmpleadoInterfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.TableRowSorter;

/**
 * Interfaz gráfica para la gestión de movimientos financieros (ingresos y egresos).
 *
 * <p>Proporciona funcionalidades para:
 * <ul>
 *   <li>Agregar nuevos movimientos (ingresos/egresos)</li>
 *   <li>Actualizar movimientos existentes</li>
 *   <li>Eliminar movimientos</li>
 *   <li>Visualizar el estado de caja</li>
 *   <li>Filtrar y buscar movimientos</li>
 *   <li>Generar recibos</li>
 * </ul>
 */
public class MovimientoGUI {
    // Componentes de la interfaz
    private JPanel Main;                         // Panel principal
    private TableRowSorter<DefaultTableModel> sorter; // Ordenador para la tabla
    private NonEditableTableModel model;         // Modelo de tabla no editable
    private JTable table1;                       // Tabla para mostrar movimientos
    private JButton agregarButton;               // Botón para agregar movimientos
    private JButton actualizarButton;            // Botón para actualizar movimientos
    private JButton eliminarButton;              // Botón para eliminar movimientos
    private JComboBox comboBox1;                 // Combo box para categorías de egreso
    private JTextField textField1;               // Campo para monto del movimiento
    private JTextField textField2;               // Campo para ID del movimiento
    private JButton verCajaButton;               // Botón para ver estado de caja
    private JButton volverButton;                // Botón para volver al menú principal
    private JComboBox comboBoxCategoria;         // Combo box para categorías de ingreso
    private JComboBox comboBoxTipo;              // Combo box para tipo de movimiento
    private JTextField buscar;                   // Campo para búsqueda/filtrado

    // Objetos de acceso a datos
    private MovimientosDAO movimientosDAO = new MovimientosDAO();
    private CajaGUI caja = new CajaGUI();

    /**
     * Constructor que inicializa la interfaz y configura los listeners.
     *
     * <p>Realiza las siguientes configuraciones:
     * <ol>
     *   <li>Prepara la tabla con modelo no editable</li>
     *   <li>Configura el ordenador de filas</li>
     *   <li>Carga los datos iniciales</li>
     *   <li>Establece el comportamiento de los componentes</li>
     *   <li>Configura todos los listeners necesarios</li>
     * </ol>
     */
    public MovimientoGUI() {
        // Configuración inicial de la tabla
        table1.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);
        obtenerDatos();

        // Configuración de componentes
        textField2.setEditable(false);
        comboBox1.setEnabled(false);
        comboBoxCategoria.setEnabled(false);

        /**
         * Listener para cambios en el tipo de movimiento.
         * <p>
         * Habilita/deshabilita los combobox de categoría según el tipo seleccionado.
         */
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

        /**
         * Listener para el botón Agregar.
         * <p>
         * Válida los datos y agrega un nuevo movimiento financiero.
         */
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

        /**
         * Listener para el botón Actualizar.
         * <p>
         * Actualiza un movimiento existente con los nuevos valores.
         */
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

        /**
         * Listener para el botón Eliminar.
         * <p>
         * Elimina el movimiento seleccionado.
         */
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(textField2.getText());
                movimientosDAO.eliminar(id);
                obtenerDatos();
                caja.obtenerDatos();
            }
        });

        /**
         * Listener para el botón Ver Caja.
         * <p>
         * Abre la interfaz de visualización de estado de caja.
         */
        verCajaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(verCajaButton);
                menuFrame.dispose();
                caja.main(null);
            }
        });

        /**
         * Listener para el botón Volver.
         * <p>
         * Regresa al menú principal.
         */
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame volverFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                volverFrame.dispose();
                MenuGUI menu = new MenuGUI();
                menu.main(null);
            }
        });

        /**
         * Listener para eventos de ratón en la tabla.
         * <p>
         * Maneja:
         * <ul>
         *   <li>Selección de filas para mostrar detalles</li>
         *   <li>Doble clic para generar recibo</li>
         * </ul>
         */
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

        /**
         * Listener para el campo de búsqueda.
         * <p>
         * Filtra los movimientos según el texto ingresado.
         */
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
     *
     * <p>Las columnas mostradas son:
     * <ol>
     *   <li>ID_movimiento</li>
     *   <li>ID_venta</li>
     *   <li>Categoria</li>
     *   <li>Descripcion</li>
     *   <li>Monto</li>
     *   <li>Fecha</li>
     * </ol>
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
     * @param args argumentos de línea de comandos (no utilizados)
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