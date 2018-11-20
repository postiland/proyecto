package postigo.listadelacompra;

public class Producto {
    private int id_producto;
    private String nombre_producto;
    private double precio;
    private int cantidad;

    public Producto() {
    }

    public int getId_producto() {
        return id_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Nombre producto: " + nombre_producto + "\nPrecio: " + precio + "\nCantidad: " + cantidad;
    }
}
