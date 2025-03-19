package farmacia;

public class Venta {
    int id_venta, id_cliente, total_venta;
    String estado, fecha_hora;

    public Venta(int id_venta, int id_cliente, int total_venta, String estado, String fecha_hora) {
        this.id_venta = id_venta;
        this.id_cliente = id_cliente;
        this.total_venta = total_venta;
        this.estado = estado;
        this.fecha_hora = fecha_hora;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getTotal_venta() {
        return total_venta;
    }

    public void setTotal_venta(int total_venta) {
        this.total_venta = total_venta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
}
