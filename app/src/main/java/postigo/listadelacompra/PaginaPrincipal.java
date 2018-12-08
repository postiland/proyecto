package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class PaginaPrincipal extends AppCompatActivity implements View.OnClickListener{

    private Usuario datosUsuario;

    private ImageView icono_error_email;
    private TextView txv_error_email;

    private ImageView icono_error_contrasena;
    private TextView txv_error_contrasena;

    private ImageView icono_info_principal;
    private TextView txv_info_principal;

    public static final String URL_COMPROBAR_USUARIO = "http://antoniopostigo.es/Slim2-ok/api/usuario/email";

    public static final String URL_CAMBIAR_CONTRASENA = "http://antoniopostigo.es/Slim2-ok/api/cambiar/contrasena/usuario";

    private String nuevo_usuario = "";

    private Button btn_login;
    private Button btn_registro;
    private Button btn_borrar_cuenta;
    private Button btn_cambiar_contrasena;

    private EditText email = null;
    private EditText contrasena = null;

    private Pattern modeloEmail = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        btn_login=(Button) findViewById(R.id.btn_acceder);
        btn_login.setOnClickListener(this);
        btn_registro = (Button) findViewById(R.id.btn_registro_usuario);
        btn_registro.setOnClickListener(this);
        btn_borrar_cuenta = (Button) findViewById(R.id.btn_borrar_cuenta_principal);
        btn_borrar_cuenta.setOnClickListener(this);
        btn_cambiar_contrasena = (Button) findViewById(R.id.btn_cambiar_contrasena);
        btn_cambiar_contrasena.setOnClickListener(this);

        email = (EditText) findViewById(R.id.txv_email_usuario);
//        email.setText("E-mail");
//        email.setTextColor(getResources().getColor(R.color.gris));
        contrasena = (EditText) findViewById(R.id.txv_contrasena_usuario);
        contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
//        contrasena.setText("Contraseña");
//        contrasena.setTextColor(getResources().getColor(R.color.gris));

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(email.getText());
                    if (cojoTextoActual.equals("E-mail")){
                        email.setText("");
                        email.setTextColor(getResources().getColor(R.color.negro));
                    }
                }
            }
        });

        contrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(contrasena.getText());
                    if (cojoTextoActual.equals("Contraseña")){
                        contrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        contrasena.setTextColor(getResources().getColor(R.color.negro));
                        contrasena.setText("");
                    }
                }
            }
        });

        icono_error_email = (ImageView) findViewById(R.id.imv_icono_error_email);
        icono_error_email.setVisibility(View.INVISIBLE);
        txv_error_email = (TextView) findViewById(R.id.txv_error_email);

        icono_error_contrasena = (ImageView) findViewById(R.id.imv_icono_error_contrasena);
        icono_error_contrasena.setVisibility(View.INVISIBLE);
        txv_error_contrasena = (TextView) findViewById(R.id.txv_error_contrasena);

        icono_info_principal = (ImageView) findViewById(R.id.imv_icono_info_principal);
        icono_info_principal.setVisibility(View.INVISIBLE);
        txv_info_principal = (TextView) findViewById(R.id.txv_informacion_principal);

        nuevo_usuario = getIntent().getStringExtra("EMAIL_NUEVO_USUARIO");
        if (nuevo_usuario != null){
            if (nuevo_usuario.length() > 0) {
                email.setText(nuevo_usuario);
                email.setTextColor(getResources().getColor(R.color.negro));
                contrasena.setText("");
                contrasena.setInputType(InputType.TYPE_CLASS_TEXT);
                contrasena.setText("Contraseña");
                contrasena.setTextColor(getResources().getColor(R.color.gris));
            }
        }

        if (!isNetDisponible()){
            txv_info_principal.setText("Por favor, revisa tu conexión. Para continuar debes tener conexión a internet.");
            txv_info_principal.setTextColor(getResources().getColor(R.color.rojo));
            btn_login.setEnabled(false);
            btn_login.setBackgroundColor(getResources().getColor(R.color.gris));
            btn_login.setTextColor(Color.WHITE);
            btn_borrar_cuenta.setEnabled(false);
            btn_cambiar_contrasena.setEnabled(false);
            btn_registro.setEnabled(false);
        }

        btn_login.requestFocus();
    }

    @Override
    public void onClick(View v) {
        if (v==btn_login || v == btn_borrar_cuenta){
            icono_error_email.setVisibility(View.INVISIBLE);
            txv_error_email.setText("");
            icono_error_contrasena.setVisibility(View.INVISIBLE);
            txv_error_contrasena.setText("");
            comprobarLogin(v);
        }

        if (v==btn_registro){
            Intent intent = new Intent(getBaseContext(), VistaRegistroUsuario.class);
            startActivity(intent);
        }

        if (v==btn_cambiar_contrasena){
            String txv_email = email.getText().toString();
            Matcher mather = modeloEmail.matcher(txv_email);

            if (txv_email.isEmpty()){
                icono_error_email.setVisibility(View.VISIBLE);
                txv_error_email.setText("Debes introducir un email");
                email.requestFocus();
                txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
            }else if (!mather.find()) {
                icono_error_email.setVisibility(View.VISIBLE);
                txv_error_email.setText("Debes introducir un email válido");
                email.requestFocus();
                txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
            }else {
                mandarEmailCambiarContrasena(txv_email);
            }
        }
    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    private void mandarEmailCambiarContrasena(String txv_email) {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_email = new RequestParams();
        envio_email.put("email", txv_email);

        client.post(URL_CAMBIAR_CONTRASENA, envio_email, new JsonHttpResponseHandler(){
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

                String estado="";

                try {
                    estado = response.getString("status");

                    if (estado.length()>0) {
                        icono_info_principal.setVisibility(View.VISIBLE);
                        txv_info_principal.setText("Te hemos enviado un email revisa tu correo");
                    }else {
                        txv_info_principal.setText("Ha ocurrido un error");
                        txv_info_principal.setTextColor(getResources().getColor(R.color.rojo));
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

    private void comprobarLogin(View v) {
        String txv_email = email.getText().toString();
        String txv_contrasena = contrasena.getText().toString();

        Matcher mather = modeloEmail.matcher(txv_email);

        if (txv_email.isEmpty()){
            icono_error_email.setVisibility(View.VISIBLE);
            txv_error_email.setText("Debes introducir un email");
            email.requestFocus();
            txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
        }else if (!mather.find()) {
            icono_error_email.setVisibility(View.VISIBLE);
            txv_error_email.setText("Debes introducir un email válido");
            email.requestFocus();
            txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
        }else if (txv_contrasena.isEmpty()) {
            icono_error_contrasena.setVisibility(View.VISIBLE);
            txv_error_contrasena.setText("Debes introducir una contraseña");
            contrasena.requestFocus();
            txv_error_contrasena.setTextColor(getResources().getColor(R.color.rojo));
        }else {
            cogerUsuario(txv_email, getMD5(txv_contrasena), v);
        }
    }

    private String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void comprobacionesInicioSesion(String txv_contrasena, View v){
        if (!datosUsuario.getContrasena().isEmpty()){
            if (datosUsuario.getContrasena().equals(txv_contrasena)){
                if (v == btn_login) {
                    if (datosUsuario.getCuenta_activa() == 0){
                        icono_error_email.setVisibility(View.VISIBLE);
                        txv_error_email.setText("Debes validar el email antes de iniciar sesión");
                        txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
                    }else {
                        Intent intent = new Intent(getBaseContext(), VistaListas.class);
                        intent.putExtra("ID_USUARIO", String.valueOf(datosUsuario.getId_usuario()));
                        intent.putExtra("NOMBRE_USUARIO", datosUsuario.getNombre());
                        startActivity(intent);
                    }
                }else {
                    Intent intent = new Intent(getBaseContext(), VistaBorrarCuenta.class);
                    intent.putExtra("ID_USUARIO", String.valueOf(datosUsuario.getId_usuario()));
                    intent.putExtra("NOMBRE_USUARIO", datosUsuario.getNombre());
                    startActivity(intent);
                }
            }else {
                icono_error_contrasena.setVisibility(View.VISIBLE);
                txv_error_contrasena.setText("Contraseña incorrecta");
                contrasena.requestFocus();
                txv_error_contrasena.setTextColor(getResources().getColor(R.color.rojo));
            }
        }
    }

    private void cogerUsuario(String txv_email, final String txv_contrasena, final View v) {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_email = new RequestParams();
        envio_email.put("email", txv_email);

        client.post(URL_COMPROBAR_USUARIO, envio_email, new JsonHttpResponseHandler(){
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
                JSONArray datos_usuario;
                JSONObject data_user;

                try {
                    datosUsuario = new Usuario();
                    datos_usuario= response.getJSONArray("data");

                    if (datos_usuario.length()>0) {
                        data_user = datos_usuario.getJSONObject(0);

                        datosUsuario.setNombre(data_user.getString("nombre"));
                        datosUsuario.setContrasena(data_user.getString("contrasena"));
                        datosUsuario.setId_usuario(Integer.parseInt(data_user.getString("id_usuario")));
                        datosUsuario.setCuenta_activa(Integer.parseInt(data_user.getString("cuenta_activa")));

                        comprobacionesInicioSesion(txv_contrasena, v);

                    }else {
                        icono_error_email.setVisibility(View.VISIBLE);
                        txv_error_email.setText("El email introducido no existe");
                        email.requestFocus();
                        txv_error_email.setTextColor(getResources().getColor(R.color.rojo));
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
