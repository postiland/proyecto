package postigo.listadelacompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VistaActivarEmail extends AppCompatActivity implements View.OnClickListener{

    private TextView txv_aviso_activar_cuenta_saludo;
    private TextView txv_aviso_activar_cuenta_nombre;

    Button btn_volver;

    private String nombre_usuario;
    private String email_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_activar_email);

        getSupportActionBar().hide();

        txv_aviso_activar_cuenta_saludo = (TextView) findViewById(R.id.txv_aviso_activar_cuenta_saludo);
        txv_aviso_activar_cuenta_nombre = (TextView) findViewById(R.id.txv_aviso_activar_cuenta_nombre);

        btn_volver = (Button) findViewById(R.id.btn_vista_principal);
        btn_volver.setOnClickListener(this);

        email_usuario = getIntent().getStringExtra("EMAIL_NUEVO_USUARIO");
        nombre_usuario = getIntent().getStringExtra("NOMBRE_NUEVO_USUARIO");

        txv_aviso_activar_cuenta_saludo.setText("¡Hola,");
        txv_aviso_activar_cuenta_saludo.setTextColor(getResources().getColor(R.color.negro));

        txv_aviso_activar_cuenta_nombre.setText(nombre_usuario+"!");
        txv_aviso_activar_cuenta_nombre.setTextColor(getResources().getColor(R.color.verdeClaro));

    }

    @Override
    public void onClick(View v) {
        if (v == btn_volver) {
            Intent intent = new Intent(getBaseContext(), PaginaPrincipal.class);
            intent.putExtra("EMAIL_NUEVO_USUARIO", email_usuario);
            startActivity(intent);
        }
    }
}
