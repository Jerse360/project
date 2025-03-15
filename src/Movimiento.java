public class Movimiento {
    int idMovimiento, idventa,  monto;
    String categoria, fecha,ingreso, egreso;

    public Movimiento(int idMovimiento, int idventa, int monto, String categoria, String fecha, String ingreso, String egreso) {
        this.idMovimiento = idMovimiento;
        this.idventa = idventa;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
        this.ingreso = ingreso;
        this.egreso = egreso;
    }


    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIngreso() {
        return ingreso;
    }

    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }

    public String getEgreso() {
        return egreso;
    }

    public void setEgreso(String egreso) {
        this.egreso = egreso;
    }
}