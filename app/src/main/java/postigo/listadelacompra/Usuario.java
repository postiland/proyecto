package postigo.listadelacompra;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String apellidos;
    private String email;
    private int telefono;
    private String contrasena;
    private int cuenta_activa;

    public Usuario(String nombre, String apellidos, String email, int telefono, String contrasena) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    public Usuario() {}

    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public int getTelefono() {
        return telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setCuenta_activa(int cuenta_activa) {
        this.cuenta_activa = cuenta_activa;
    }

    public int getCuenta_activa() {
        return cuenta_activa;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id_usuario=" + id_usuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono=" + telefono +
                ", contrasena='" + contrasena + '\'' +
                '}';
    }
}
