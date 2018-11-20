package postigo.listadelacompra;

public class Lista {
    private int id_lista;
    private String nombre;
    private String codigo_grupo;
    private int id_usuario;

    public Lista() {}

    public int getId_lista() {
        return id_lista;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo_grupo() {
        return codigo_grupo;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_lista(int id_lista) {
        this.id_lista = id_lista;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo_grupo(String codigo_grupo) {
        this.codigo_grupo = codigo_grupo;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    @Override
    public String toString() {
        return "Nombre lista: "+this.nombre;
    }
}
