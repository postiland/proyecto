package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VistaProductosLista extends AppCompatActivity implements View.OnClickListener{

    private String datosLista_id_lista;

    private Button btn_anadir_producto;

    private Button btn_limpiar_lista;

    ListView lista_productos;

    private ArrayList<Producto> productos_lista;
    ArrayAdapter<Producto> myAdapter;

    public static final String URL_COGER_PRODUCTOS = "http://antoniopostigo.es/Slim2-ok/api/id_lista/obtener/productos";

    public static final String URL_CREAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/anadir/producto/lista";

    public static final String URL_ELIMINAR_RELACION_LISTA_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/anadir/producto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_productos_lista);

        datosLista_id_lista= getIntent().getStringExtra("ID_LISTA");

        lista_productos=(ListView) findViewById(R.id.ltv_productos);

        btn_anadir_producto=(Button) findViewById(R.id.btn_anadir_producto);
        btn_anadir_producto.setOnClickListener(this);

        btn_limpiar_lista=(Button) findViewById(R.id.btn_limpiar_lista);
        btn_limpiar_lista.setOnClickListener(this);

        cogerProductos(datosLista_id_lista);

        productos_lista=new ArrayList<Producto>();
        myAdapter = new ArrayAdapter<Producto>(this, android.R.layout.simple_list_item_1, productos_lista);
        lista_productos.setAdapter(myAdapter);

        lista_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Usuario usuario = (Usuario) lista_productos.getAdapter().getItem(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==btn_anadir_producto){
            crearDialogAnadirProducto();
        }

        if (v==btn_limpiar_lista){
            Toast.makeText(getApplicationContext(), "Boton limpiar lista", Toast.LENGTH_SHORT).show();
        }
    }

    private void cogerProductos(String id_producto) {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_id_lista = new RequestParams();
        envio_id_lista.put("id_lista", id_producto);

        client.post(URL_COGER_PRODUCTOS, envio_id_lista, new JsonHttpResponseHandler(){
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
                    datos_usuario= response.getJSONArray("data");

                    productos_lista.clear();

                    for (int i=0; i<datos_usuario.length(); i++){
                        Producto producto=new Producto();

                        data_user = datos_usuario.getJSONObject(i);
                        producto.setId_producto(Integer.parseInt(data_user.getString("id_producto")));
                        producto.setNombre_producto(data_user.getString("nombre"));
                        producto.setPrecio(Double.parseDouble(data_user.getString("precio")));
                        producto.setCantidad(Integer.parseInt(data_user.getString("cantidad")));

                        productos_lista.add(producto);
                    }

                    myAdapter.notifyDataSetChanged();
                    //datosUsuario.setTelefono(Integer.parseInt(data_user.getString("telefono")));

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

    private void crearDialogAnadirProducto() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("AÑADIR PRODUCTO");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText edt_nom_pro = new EditText(this);
        TextView txv_nom_pro = new TextView(this);
        txv_nom_pro.setText("Nombre producto:");

        final EditText edt_pro_cant = new EditText(this);
        edt_pro_cant.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_pro_cant.setText("1");
        TextView txv_pro_cant = new TextView(this);
        txv_pro_cant.setText("Cantidad:");

        final EditText edt_pro_prec = new EditText(this);
        edt_pro_prec.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edt_pro_prec.setText("0");
        TextView txv_pro_prec = new TextView(this);
        txv_pro_cant.setText("Precio:");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        LinearLayout.LayoutParams tv2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv2Params.topMargin = 5;
        tv2Params.bottomMargin = 5;
        LinearLayout.LayoutParams tv3Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv3Params.topMargin = 5;
        tv3Params.bottomMargin = 5;

        layout.addView(txv_nom_pro, tv1Params);
        layout.addView(edt_nom_pro, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(txv_pro_cant, tv2Params);
        layout.addView(edt_pro_cant, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(txv_pro_prec, tv3Params);
        layout.addView(edt_pro_prec, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("EDITAR LISTA");
        // alertDialogBuilder.setMessage("Input Student ID");
        alertDialogBuilder.setCustomTitle(tv);

        // alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("EDITAR LISTA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Toast.makeText(getApplicationContext(), "->" + edt_nom_pro.getText().toString(), Toast.LENGTH_SHORT).show();
                    int cant_pro = 1;
                    double prec_pro = 0;

                    if (!edt_nom_pro.getText().toString().isEmpty()){
                        if (!edt_pro_cant.getText().toString().isEmpty()){
                            cant_pro = Integer.parseInt(edt_pro_cant.getText().toString());
                        }
                        if (!edt_pro_prec.getText().toString().isEmpty()){
                            prec_pro = Double.parseDouble(edt_pro_prec.getText().toString());
                        }
                        crearProducto(edt_nom_pro.getText().toString(), cant_pro, prec_pro);
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "ERROR AL DAR OK"+"->", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearProducto(String nombre_producto, int cantidad_producto, double precio_producto) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_nueva_lista = new RequestParams();
        envio_nueva_lista.put("nombre", nombre_producto);
        envio_nueva_lista.put("cantidad", cantidad_producto);
        envio_nueva_lista.put("precio", precio_producto);
        envio_nueva_lista.put("id_lista", datosLista_id_lista);

        client.post(URL_CREAR_PRODUCTO, envio_nueva_lista, new JsonHttpResponseHandler(){
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

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), "Resultado " + estado, Toast.LENGTH_SHORT).show();
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
