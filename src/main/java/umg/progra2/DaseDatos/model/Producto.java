package umg.progra2.DaseDatos.model;

public class Producto {
    private int idProducto;
    private String nombre;
    private String pais;
    private int peso;
    private double precio;
    private int existencia;

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }


    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }



    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    // Constructor
    public Producto(int idProducto, String nombre, String pais, double precio, int existencia) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.pais = pais;
        this.precio = precio;
        this.existencia = existencia;
    }


    public Producto() {
    }

    // Getters y setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
