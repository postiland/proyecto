package postigo.listadelacompra;

import java.util.ArrayList;

public class ResultadoUsuario {
    boolean code;
    int status;
    String message;
    Usuario usuario;

    public boolean getCode() {return code;}

    public void setCode(boolean code) { this.code = code;}

    public int getStatus() {return status;}

    public void setStatus(int status) {this.status = status;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
