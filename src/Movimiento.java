/**
 * Clase que representa un movimiento financiero en el sistema.
 * Contiene información sobre transacciones, ventas y otros movimientos monetarios.
 * Sigue el patrón JavaBean para encapsulación de datos.
 */
public class Movimiento {

    // Atributos de la clase
    private int idMovimiento;   // Identificador único del movimiento
    private int idventa;        // ID de la venta asociada (si aplica)
    private int monto;          // Valor monetario del movimiento
    private String categoria;   // Categoría/clasificación del movimiento
    private String fecha;       // Fecha en que se realizó el movimiento
    private String descripcion; // Detalles adicionales del movimiento

    /**
     * Constructor completo para crear una instancia de Movimiento.
     *
     * @param idMovimiento Identificador único del movimiento
     * @param idventa ID de la venta asociada (0 si no aplica)
     * @param monto Valor monetario del movimiento (en unidades mínimas)
     * @param categoria Categoría/clasificación del movimiento
     * @param fecha Fecha del movimiento en formato YYYY-MM-DD
     * @param descripcion Detalles adicionales del movimiento
     */
    public Movimiento(int idMovimiento, int idventa, int monto,
                      String categoria, String fecha, String descripcion) {
        this.idMovimiento = idMovimiento;
        this.idventa = idventa;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // Métodos getter y setter

    /**
     * Obtiene el ID del movimiento.
     * @return Identificador único del movimiento
     */
    public int getIdMovimiento() {
        return idMovimiento;
    }

    /**
     * Establece el ID del movimiento.
     * @param idMovimiento Nuevo identificador único
     */
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    /**
     * Obtiene el ID de venta asociado.
     * @return ID de venta (0 si no aplica)
     */
    public int getIdventa() {
        return idventa;
    }

    /**
     * Establece el ID de venta asociado.
     * @param idventa Nuevo ID de venta asociada
     */
    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    /**
     * Obtiene el monto del movimiento.
     * @return Valor monetario en unidades mínimas
     */
    public int getMonto() {
        return monto;
    }

    /**
     * Establece el monto del movimiento.
     * @param monto Nuevo valor monetario en unidades mínimas
     */
    public void setMonto(int monto) {
        this.monto = monto;
    }

    /**
     * Obtiene la categoría del movimiento.
     * @return Categoría/clasificación
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del movimiento.
     * @param categoria Nueva categoría/clasificación
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene la fecha del movimiento.
     * @return Fecha en formato YYYY-MM-DD
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del movimiento.
     * @param fecha Nueva fecha en formato YYYY-MM-DD
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la descripción del movimiento.
     * @return Detalles adicionales
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del movimiento.
     * @param descripcion Nuevos detalles adicionales
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}