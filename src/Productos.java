/**
 * Clase que representa un producto en el sistema de inventario.
 * Contiene información sobre precios, existencias y categorización de productos.
 * Sigue el patrón JavaBean para encapsulación de datos.
 */
public class Productos {

    // Atributos de la clase
    private int id_producto;     // Identificador único del producto
    private int stock;          // Cantidad actual en inventario
    private int stock_minimo;   // Nivel mínimo de stock para alertas
    private int precio;         // Precio unitario del producto
    private String nombre;      // Nombre descriptivo del producto
    private String categoria;   // Categoría/clasificación del producto

    /**
     * Constructor completo para crear una instancia de Producto.
     *
     * @param id_producto Identificador único del producto
     * @param nombre Nombre descriptivo del producto
     * @param categoria Categoría/clasificación del producto
     * @param precio Precio unitario en unidades monetarias
     * @param stock Cantidad actual en inventario
     * @param stock_minimo Nivel mínimo para alertas de reposición
     */
    public Productos(int id_producto, String nombre, String categoria,
                     int precio, int stock, int stock_minimo) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.stock_minimo = stock_minimo;
    }

    // Métodos getter y setter

    /**
     * Obtiene el ID del producto.
     * @return Identificador único del producto
     */
    public int getId_producto() {
        return id_producto;
    }

    /**
     * Establece el ID del producto.
     * @param id_producto Nuevo identificador único
     */
    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    /**
     * Obtiene el nombre del producto.
     * @return Nombre descriptivo del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     * @param nombre Nuevo nombre descriptivo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la categoría del producto.
     * @return Categoría/clasificación del producto
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del producto.
     * @param categoria Nueva categoría/clasificación
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el stock actual del producto.
     * @return Cantidad disponible en inventario
     */
    public int getStock() {
        return stock;
    }

    /**
     * Establece el stock actual del producto.
     * @param stock Nueva cantidad disponible
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Obtiene el stock mínimo del producto.
     * @return Nivel mínimo para alertas de reposición
     */
    public int getStock_minimo() {
        return stock_minimo;
    }

    /**
     * Establece el stock mínimo del producto.
     * @param stock_minimo Nuevo nivel mínimo para alertas
     */
    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    /**
     * Obtiene el precio unitario del producto.
     * @return Precio en unidades monetarias
     */
    public int getPrecio() {
        return precio;
    }

    /**
     * Establece el precio unitario del producto.
     * @param precio Nuevo precio en unidades monetarias
     */
    public void setPrecio(int precio) {
        this.precio = precio;
    }
}