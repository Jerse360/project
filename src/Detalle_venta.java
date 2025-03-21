public class Detalle_venta {

    int id_detalleVenta, id_venta, id_producto,precio_total, cantidad;
    String tipo;

    public Detalle_venta(int id_detalleVenta, int id_venta, int id_producto, int precio_total, int cantidad, String tipo) {
        this.id_detalleVenta = id_detalleVenta;
        this.id_venta = id_venta;
        this.id_producto = id_producto;
        this.precio_total = precio_total;
        this.cantidad = cantidad;
        this.tipo = tipo;

    }

    public int getId_detalleVenta() {
        return id_detalleVenta;
    }

    public void setId_detalleVenta(int id_detalleVenta) {
        this.id_detalleVenta = id_detalleVenta;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getPrecio_total() {
        return precio_total;
    }

    public void setPrecio_total(int precio_total) {
        this.precio_total = precio_total;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
