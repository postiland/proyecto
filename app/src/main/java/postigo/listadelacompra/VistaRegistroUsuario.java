package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class VistaRegistroUsuario extends AppCompatActivity implements View.OnClickListener{

    private EditText nombre = null;
    private EditText apellidos = null;
    private EditText email = null;
    private EditText telefono = null;
    private EditText contrasena = null;

    private Button btn_registrate = null;

    private ArrayList<String> emailsObtenidos;

    private Usuario crearUsuario;

    private ImageView icono_errores;

    private TextView txv_errores;

    public static final String URL_OBTENER_EMAILS = "http://antoniopostigo.es/Slim2-ok/api/emails/usuarios";
    public static final String URL_INSERTAR_USUARIO = "http://antoniopostigo.es/Slim2-ok/api/crear/usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro_usuario);

        nombre = (EditText) findViewById(R.id.txv_nombre);
        apellidos = (EditText) findViewById(R.id.txv_apellidos);
        email = (EditText) findViewById(R.id.txv_email);
        telefono = (EditText) findViewById(R.id.txv_telefono);
        contrasena = (EditText) findViewById(R.id.txv_contrasena);

        btn_registrate=(Button) findViewById(R.id.btn_registrate);
        btn_registrate.setOnClickListener(this);

        icono_errores = (ImageView) findViewById(R.id.imv_icono_errores_registro);
        icono_errores.setVisibility(View.INVISIBLE);
        txv_errores = (TextView) findViewById(R.id.txv_errores_registro);

        cogerEmails();
    }

    @Override
    public void onClick(View v) {
        if (v==btn_registrate){
            icono_errores.setVisibility(View.INVISIBLE);
            txv_errores.setText("");
            txv_errores.setTextColor(Color.RED);
            if (comprobarCampos()){
                insertarUsuario();
                Intent intent = new Intent(getBaseContext(), PaginaPrincipal.class);
                intent.putExtra("EMAIL_NUEVO_USUARIO", crearUsuario.getEmail());
                startActivity(intent);
            }
        }
    }

    private boolean comprobarCampos() {
        String txv_nombre = nombre.getText().toString();
        String txv_apellidos = apellidos.getText().toString();
        String txv_email = email.getText().toString();
        String txv_telefono = telefono.getText().toString();
        String txv_contrasena = contrasena.getText().toString();
        int numeroTelefono;

        Pattern modeloEmail = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Pattern modeloContrasena = Pattern
                .compile("^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}$");

        if (txv_email.isEmpty()){
            mostrarError("Debes introducir un email");
            return false;
        }else{
            Matcher mather = modeloEmail.matcher(txv_email);
            if (!mather.find()){
                mostrarError("Debes introducir un email válido");
                return false;
            }else {
                for (int i=0; i<emailsObtenidos.size(); i++){
                    if (txv_email.equals(emailsObtenidos.get(i))){
                        mostrarError("El email elegido ya existe");
                        return false;
                    }
                }
            }
        }

        if (txv_nombre.isEmpty()){
            mostrarError("Debes introducir un nombre");
            return false;
        }

        if (txv_apellidos.isEmpty()){
            mostrarError("Debes introducir los apellidos");
            return false;
        }

        if (txv_telefono.isEmpty()){
            mostrarError("Debes introducir un telefono");
            return false;
        }else if (txv_telefono.length() < 9){
            mostrarError("Debes introducir un telefono válido");
            return false;
        }else {
            try {
                numeroTelefono = Integer.parseInt(txv_telefono);
            }catch (Exception e){
                mostrarError("Debes introducir un telefono válido");
                return false;
            }
        }

        if (txv_contrasena.isEmpty()){
            mostrarError("Debes introducir una contraseña");
            return false;
        }else{
            Matcher mather = modeloContrasena.matcher(txv_contrasena);
            if (!mather.find()){
                mostrarError("Debes introducir una contraseña válida");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("La contraseña debe tener:\n\n- Entre 8 y 16 caracteres.\n- Al menos 1 número.\n- Mayúsculas y minúsculas.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        }

        crearUsuario = new Usuario(txv_nombre, txv_apellidos, txv_email, numeroTelefono, txv_contrasena);

        return true;
    }

    private void mostrarError(String error){
        icono_errores.setVisibility(View.VISIBLE);
        txv_errores.setText(error);
        txv_errores.setTextColor(Color.RED);
    }

    private void cogerEmails() {
        emailsObtenidos = new ArrayList<String>();
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL_OBTENER_EMAILS, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setCancelable(false);
                progreso.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                JSONArray emails_usuarios;
                JSONObject data_user;

                try {
                    emails_usuarios= response.getJSONArray("data");

                    for (int i=0; i<emails_usuarios.length(); i++){
                        data_user = emails_usuarios.getJSONObject(i);
                        emailsObtenidos.add(data_user.getString("email"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertarUsuario() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams datosCrearUsuario = new RequestParams();
        datosCrearUsuario.put("nombre", crearUsuario.getNombre());
        datosCrearUsuario.put("apellidos", crearUsuario.getApellidos());
        datosCrearUsuario.put("email", crearUsuario.getEmail());
        datosCrearUsuario.put("telefono", crearUsuario.getTelefono());
        datosCrearUsuario.put("contrasena", crearUsuario.getContrasena());

        client.post(URL_INSERTAR_USUARIO, datosCrearUsuario, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setCancelable(false);
                progreso.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                String estado = "";

                try {
                    estado = response.getString("status");

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), estado, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
