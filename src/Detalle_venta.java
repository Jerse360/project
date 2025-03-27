/**
 * Clase que representa el detalle de una venta en el sistema.
 * Contiene información sobre los productos vendidos, cantidades y precios.
 * Sigue el patrón JavaBean para encapsulación de datos.
 */
public class Detalle_venta {

    // Atributos de la clase
    private int id_detalleVenta;    // Identificador único del detalle de venta
    private int id_venta;          // ID de la venta asociada
    private int id_producto;       // ID del producto vendido
    private int precio_total;      // Precio total del producto (precio unitario * cantidad)
    private int cantidad;          // Cantidad de productos vendidos
    private String tipo;           // Tipo de producto o categoría

    /**
     * Constructor completo para crear una instancia de Detalle_venta.
     *
     * @param id_detalleVenta Identificador único del detalle de venta
     * @param id_venta ID de la venta asociada
     * @param id_producto ID del producto vendido
     * @param precio_total Precio total del producto (precio unitario * cantidad)
     * @param cantidad Cantidad de productos vendidos
     * @param tipo Tipo de producto o categoría
     */
    public Detalle_venta(int id_detalleVenta, int id_venta, int id_producto,
                         int precio_total, int cantidad, String tipo) {
        this.id_detalleVenta = id_detalleVenta;
        this.id_venta = id_venta;
        this.id_producto = id_producto;
        this.precio_total = precio_total;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    // Métodos getter y setter

    /**
     * Obtiene el ID del detalle de venta.
     * @return Identificador único del detalle de venta
     */
    public int getId_detalleVenta() {
        return id_detalleVenta;
    }

    /**
     * Establece el ID del detalle de venta.
     * @param id_detalleVenta Nuevo identificador único del detalle de venta
     */
    public void setId_detalleVenta(int id_detalleVenta) {
        this.id_detalleVenta = id_detalleVenta;
    }

    /**
     * Obtiene el ID de la venta asociada.
     * @return ID de la venta asociada
     */
    public int getId_venta() {
        return id_venta;
    }

    /**
     * Establece el ID de la venta asociada.
     * @param id_venta Nuevo ID de la venta asociada
     */
    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    /**
     * Obtiene el ID del producto vendido.
     * @return ID del producto vendido
     */
    public int getId_producto() {
        return id_producto;
    }

    /**
     * Establece el ID del producto vendido.
     * @param id_producto Nuevo ID del producto vendido
     */
    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    /**
     * Obtiene el precio total del producto.
     * @return Precio total (precio unitario * cantidad)
     */
    public int getPrecio_total() {
        return precio_total;
    }

    /**
     * Establece el precio total del producto.
     * @param precio_total Nuevo precio total (precio unitario * cantidad)
     */
    public void setPrecio_total(int precio_total) {
        this.precio_total = precio_total;
    }

    /**
     * Obtiene la cantidad de productos vendidos.
     * @return Cantidad de productos vendidos
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de productos vendidos.
     * @param cantidad Nueva cantidad de productos vendidos
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el tipo de producto.
     * @return Tipo o categoría del producto
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de producto.
     * @param tipo Nuevo tipo o categoría del producto
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}