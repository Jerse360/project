package EmpleadoInterfaz;

/**
 * Clase que representa una venta en el sistema.
 * Contiene información sobre la transacción comercial, incluyendo cliente asociado,
 * monto total y estado de la venta. Sigue el patrón JavaBean para encapsulación de datos.
 */
public class Venta {

    // Atributos de la clase
    private int id_venta;          // Identificador único de la venta
    private int id_cliente;        // Identificador del cliente asociado
    private int total_venta;       // Monto total de la venta (en unidades monetarias)
    private String estado;         // Estado actual de la venta (Ej: "Completada", "Cancelada", "Pendiente")
    private String fecha;          // Fecha en que se realizó la venta (formato YYYY-MM-DD)
    private String nombreCliente;  // Nombre del cliente asociado (para visualización)

    /**
     * Constructor completo para crear una instancia de Venta.
     *
     * @param id_venta Identificador único de la venta
     * @param id_cliente Identificador del cliente asociado
     * @param total_venta Monto total de la venta
     * @param estado Estado actual de la venta
     * @param fecha Fecha de la venta (YYYY-MM-DD)
     * @param nombreCliente Nombre completo del cliente
     */
    public Venta(int id_venta, int id_cliente, int total_venta,
                 String estado, String fecha, String nombreCliente) {
        this.id_venta = id_venta;
        this.id_cliente = id_cliente;
        this.total_venta = total_venta;
        this.estado = estado;
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
    }

    // Métodos getter y setter

    /**
     * Obtiene el ID de la venta.
     * @return Identificador único de la venta
     */
    public int getId_venta() {
        return id_venta;
    }

    /**
     * Establece el ID de la venta.
     * @param id_venta Nuevo identificador único
     */
    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    /**
     * Obtiene el ID del cliente asociado.
     * @return Identificador del cliente
     */
    public int getId_cliente() {
        return id_cliente;
    }

    /**
     * Establece el ID del cliente asociado.
     * @param id_cliente Nuevo identificador de cliente
     */
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    /**
     * Obtiene el monto total de la venta.
     * @return Total en unidades monetarias
     */
    public int getTotal_venta() {
        return total_venta;
    }

    /**
     * Establece el monto total de la venta.
     * @param total_venta Nuevo monto total
     */
    public void setTotal_venta(int total_venta) {
        this.total_venta = total_venta;
    }

    /**
     * Obtiene el estado actual de la venta.
     * @return Estado (Ej: "Completada", "Cancelada")
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la venta.
     * @param estado Nuevo estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la fecha de la venta.
     * @return Fecha en formato YYYY-MM-DD
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la venta.
     * @param fecha Nueva fecha en formato YYYY-MM-DD
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el nombre del cliente asociado.
     * @return Nombre completo del cliente
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Establece el nombre del cliente asociado.
     * @param nombreCliente Nuevo nombre de cliente
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}