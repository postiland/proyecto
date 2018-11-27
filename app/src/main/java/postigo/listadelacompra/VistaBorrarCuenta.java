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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class VistaBorrarCuenta extends AppCompatActivity implements View.OnClickListener{


    private TextView txv_aviso_borrar_cuenta;
    private TextView txv_cuenta_borra;
    private TextView txv_aviso_imposible_borrar;

    Button btn_borrar_cuenta;

    private String datosUsuario_id_usuario;

    public static final String URL_BORRAR_CUENTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/cuenta";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_borrar_cuenta);

        txv_aviso_borrar_cuenta = (TextView) findViewById(R.id.txv_aviso_borrar_cuenta);
        txv_cuenta_borra = (TextView) findViewById(R.id.txv_cuenta_borra);
        txv_aviso_imposible_borrar = (TextView) findViewById(R.id.txv_aviso_imposible_borrar);
        txv_aviso_borrar_cuenta.setText("Hola, "+getIntent().getStringExtra("NOMBRE_USUARIO"));

        btn_borrar_cuenta = (Button) findViewById(R.id.btn_borrar_cuenta);
        btn_borrar_cuenta.setOnClickListener(this);

        datosUsuario_id_usuario = getIntent().getStringExtra("ID_USUARIO");
    }

    @Override
    public void onClick(View v) {
        if (v == btn_borrar_cuenta){
            mostarConfirmacionBorrado();
        }
    }

    private void mostarConfirmacionBorrado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estas seguro/a?")
                .setTitle("BORRAR CUENTA")
                .setPositiveButton("BORRAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        borrar_cuenta();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void borrar_cuenta() {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_borrar_usuario = new RequestParams();
        envio_borrar_usuario.put("id_usuario", datosUsuario_id_usuario);

        client.post(URL_BORRAR_CUENTA, envio_borrar_usuario, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                String estado = "";

                try {
                    estado = response.getString("status");

                    if (estado.length()>0 && estado.equals("OK")) {
                        txv_aviso_borrar_cuenta.setText("Cuenta borrada");
                        txv_cuenta_borra.setText("¡Pero siempre puedes volver\na registrarte!");
                        txv_aviso_imposible_borrar.setText("");
                        btn_borrar_cuenta.setEnabled(false);
                    }else {
                        txv_aviso_borrar_cuenta.setText("Hemos tenido un problema");
                        txv_cuenta_borra.setText("Imposible eliminar cuenta...");
                        txv_aviso_imposible_borrar.setText("Por favor, ponte en contacto con:\nincidencias@antoniopostigo.com");
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
