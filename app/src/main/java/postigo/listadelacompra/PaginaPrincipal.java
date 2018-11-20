package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PaginaPrincipal extends AppCompatActivity implements View.OnClickListener{

    private Usuario datosUsuario;

    public static final String URL_COMPROBAR_USUARIO = "http://antoniopostigo.es/Slim2-ok/api/usuario/email";

    private String nuevo_usuario = "";

    Button btn_login;
    Button btn_registro;

    EditText email = null;
    EditText contrasena = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        btn_login=(Button) findViewById(R.id.btn_acceder);
        btn_login.setOnClickListener(this);
        btn_registro = (Button) findViewById(R.id.btn_registro_usuario);
        btn_registro.setOnClickListener(this);

        email = (EditText) findViewById(R.id.txv_email_usuario);
        contrasena = (EditText) findViewById(R.id.txv_contrasena_usuario);

        nuevo_usuario = getIntent().getStringExtra("EMAIL_NUEVO_USUARIO");
        if (nuevo_usuario != null){
            if (nuevo_usuario.length() > 0) {
                email.setText(nuevo_usuario);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v==btn_login){
            comprobarLogin();
        }

        if (v==btn_registro){
            Intent intent = new Intent(getBaseContext(), VistaRegistroUsuario.class);
            startActivity(intent);
        }
    }

    private void comprobarLogin() {
        String txv_email = email.getText().toString();
        String txv_contrasena = contrasena.getText().toString();

        if (txv_email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Debes introducir un email", Toast.LENGTH_SHORT).show();
        }else if (txv_contrasena.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Debes introducir una contraseña", Toast.LENGTH_SHORT).show();
        }else {
            cogerUsuario(txv_email, txv_contrasena);
        }
    }

    private void comprobarContrasena(String txv_contrasena){
        if (!datosUsuario.getContrasena().isEmpty()){
            if (datosUsuario.getContrasena().equals(txv_contrasena)){
                Intent intent = new Intent(getBaseContext(), VistaListas.class);
                intent.putExtra("ID_USUARIO", String.valueOf(datosUsuario.getId_usuario()));
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), "La contraseña es incorrecta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cogerUsuario(String txv_email, final String txv_contrasena) {
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

                        datosUsuario.setContrasena(data_user.getString("contrasena"));
                        datosUsuario.setId_usuario(Integer.parseInt(data_user.getString("id_usuario")));

                        comprobarContrasena(txv_contrasena);
                    }else {
                        Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
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
