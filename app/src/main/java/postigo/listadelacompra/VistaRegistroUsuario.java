package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private ImageView icono_error_email;
    private ImageView icono_error_nombre;
    private ImageView icono_error_apellidos;
    private ImageView icono_error_telefono;
    private ImageView icono_error_contrasena;

    private TextView txv_errores;

    private static final String URL_OBTENER_EMAILS = "http://antoniopostigo.es/Slim2-ok/api/emails/usuarios";
    private static final String URL_INSERTAR_USUARIO = "http://antoniopostigo.es/Slim2-ok/api/crear/usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro_usuario);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        nombre = (EditText) findViewById(R.id.txv_nombre);
        nombre.setText("Nombre");
        nombre.setTextColor(getResources().getColor(R.color.gris));
        apellidos = (EditText) findViewById(R.id.txv_apellidos);
        apellidos.setText("Apellidos");
        apellidos.setTextColor(getResources().getColor(R.color.gris));
        email = (EditText) findViewById(R.id.txv_email);
        email.setText("E-mail");
        email.setTextColor(getResources().getColor(R.color.gris));
        telefono = (EditText) findViewById(R.id.txv_telefono);
        telefono.setText("Teléfono");
        telefono.setTextColor(getResources().getColor(R.color.gris));
        contrasena = (EditText) findViewById(R.id.txv_contrasena);
        contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
        contrasena.setText("Contraseña");
        contrasena.setTextColor(getResources().getColor(R.color.gris));

        btn_registrate=(Button) findViewById(R.id.btn_registrate);
        btn_registrate.setOnClickListener(this);

        cambiarTextoObtenerFoco();

        icono_error_email = (ImageView) findViewById(R.id.imv_icono_error_email_registro);
        icono_error_email.setVisibility(View.INVISIBLE);
        icono_error_nombre = (ImageView) findViewById(R.id.imv_icono_error_nombre_registro);
        icono_error_nombre.setVisibility(View.INVISIBLE);
        icono_error_apellidos = (ImageView) findViewById(R.id.imv_icono_error_apellidos_registro);
        icono_error_apellidos.setVisibility(View.INVISIBLE);
        icono_error_telefono = (ImageView) findViewById(R.id.imv_icono_error_telefono_registro);
        icono_error_telefono.setVisibility(View.INVISIBLE);
        icono_error_contrasena = (ImageView) findViewById(R.id.imv_icono_error_contrasena_registro);
        icono_error_contrasena.setVisibility(View.INVISIBLE);
        txv_errores = (TextView) findViewById(R.id.txv_errores_registro);

        cogerEmails();
    }

    private void cambiarTextoObtenerFoco() {
        nombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(nombre.getText());
                if (hasFocus) {
                    if (cojoTextoActual.equals("Nombre")){
                        nombre.setText("");
                        nombre.setTextColor(getResources().getColor(R.color.negro));
                    }
                }else {
                    if (cojoTextoActual.equals("")){
                        nombre.setText("Nombre");
                        nombre.setTextColor(getResources().getColor(R.color.gris));
                    }
                }
            }
        });
        apellidos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(apellidos.getText());
                if (hasFocus) {

                    if (cojoTextoActual.equals("Apellidos")){
                        apellidos.setText("");
                        apellidos.setTextColor(getResources().getColor(R.color.negro));
                    }
                }else {
                    if (cojoTextoActual.equals("")){
                        apellidos.setText("Apellidos");
                        apellidos.setTextColor(getResources().getColor(R.color.gris));
                    }
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(email.getText());
                if (hasFocus) {
                    if (cojoTextoActual.equals("E-mail")){
                        email.setText("");
                        email.setTextColor(getResources().getColor(R.color.negro));
                    }
                }else {
                    if (cojoTextoActual.equals("")){
                        email.setText("E-mail");
                        email.setTextColor(getResources().getColor(R.color.gris));
                    }
                }
            }
        });
        telefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(telefono.getText());
                if (hasFocus) {
                    if (cojoTextoActual.equals("Teléfono")){
                        telefono.setText("");
                        telefono.setTextColor(getResources().getColor(R.color.negro));
                    }
                }else {
                    if (cojoTextoActual.equals("")){
                        telefono.setText("Teléfono");
                        telefono.setTextColor(getResources().getColor(R.color.gris));
                    }
                }
            }
        });
        contrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(contrasena.getText());
                if (hasFocus) {
                    if (cojoTextoActual.equals("Contraseña")){
                        contrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        contrasena.setText("");
                        contrasena.setTextColor(getResources().getColor(R.color.negro));
                    }
                }else {
                    if (cojoTextoActual.equals("")){
                        contrasena.setText("Contraseña");
                        contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
                        contrasena.setTextColor(getResources().getColor(R.color.gris));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==btn_registrate){
            quitarErrores();
            txv_errores.setText("");
            txv_errores.setTextColor(getResources().getColor(R.color.rojo));
            if (comprobarCampos()){
                insertarUsuario();
                Intent intent = new Intent(getBaseContext(), VistaActivarEmail.class);
                intent.putExtra("EMAIL_NUEVO_USUARIO", crearUsuario.getEmail());
                intent.putExtra("NOMBRE_NUEVO_USUARIO", crearUsuario.getNombre());
                startActivity(intent);
            }
        }
    }

    private void quitarErrores(){
        icono_error_email.setVisibility(View.INVISIBLE);
        icono_error_nombre.setVisibility(View.INVISIBLE);
        icono_error_apellidos.setVisibility(View.INVISIBLE);
        icono_error_telefono.setVisibility(View.INVISIBLE);
        icono_error_contrasena.setVisibility(View.INVISIBLE);
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
            icono_error_email.setVisibility(View.VISIBLE);
            email.requestFocus();
            return false;
        }else{
            Matcher mather = modeloEmail.matcher(txv_email);
            if (!mather.find()){
                mostrarError("Debes introducir un email válido");
                icono_error_email.setVisibility(View.VISIBLE);
                email.requestFocus();
                return false;
            }else {
                for (int i=0; i<emailsObtenidos.size(); i++){
                    if (txv_email.equals(emailsObtenidos.get(i))){
                        mostrarError("El email elegido ya existe");
                        icono_error_email.setVisibility(View.VISIBLE);
                        email.requestFocus();
                        return false;
                    }
                }
            }
        }

        if (txv_nombre.isEmpty()){
            mostrarError("Debes introducir un nombre");
            icono_error_nombre.setVisibility(View.VISIBLE);
            nombre.requestFocus();
            return false;
        }

        if (txv_apellidos.isEmpty()){
            mostrarError("Debes introducir los apellidos");
            icono_error_nombre.setVisibility(View.VISIBLE);
            apellidos.requestFocus();
            return false;
        }

        if (txv_telefono.isEmpty()){
            mostrarError("Debes introducir un telefono");
            icono_error_telefono.setVisibility(View.VISIBLE);
            telefono.requestFocus();
            return false;
        }else if (txv_telefono.length() < 9){
            mostrarError("Debes introducir un telefono válido");
            icono_error_telefono.setVisibility(View.VISIBLE);
            telefono.requestFocus();
            return false;
        }else {
            try {
                numeroTelefono = Integer.parseInt(txv_telefono);
            }catch (Exception e){
                mostrarError("Debes introducir un telefono válido");
                icono_error_telefono.setVisibility(View.VISIBLE);
                telefono.requestFocus();
                return false;
            }
        }

        if (txv_contrasena.isEmpty()){
            mostrarError("Debes introducir una contraseña");
            icono_error_contrasena.setVisibility(View.VISIBLE);
            contrasena.requestFocus();
            return false;
        }else{
            Matcher mather = modeloContrasena.matcher(txv_contrasena);
            if (!mather.find()){
                mostrarError("Debes introducir una contraseña válida");
                icono_error_contrasena.setVisibility(View.VISIBLE);
                contrasena.requestFocus();
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

        String pass = getMD5(txv_contrasena);

        crearUsuario = new Usuario(txv_nombre, txv_apellidos, txv_email, numeroTelefono, pass);
        return true;
    }

    private String getMD5(String contrasena) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(contrasena.getBytes());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.err.println("Error "+e.getMessage());
        }
        return "";
    }

    private void mostrarError(String error){
        txv_errores.setText(error);
        txv_errores.setTextColor(getResources().getColor(R.color.rojo));
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
