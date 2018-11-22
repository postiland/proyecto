package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private int posicion_producto_editar;

    private Button btn_anadir_producto;

    private Button btn_limpiar_lista;

    ListView lista_productos;

    private ArrayList<Producto> productos_lista;
    ArrayAdapter<Producto> myAdapter;

    public static final String URL_COGER_PRODUCTOS = "http://antoniopostigo.es/Slim2-ok/api/id_lista/obtener/productos";

    public static final String URL_CREAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/anadir/producto/lista";

    public static final String URL_EDITAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/editar/producto";

    public static final String URL_LIMPIAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/productos/lista";

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
                Producto producto = (Producto) lista_productos.getAdapter().getItem(position);
            }
        });

        lista_productos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Producto producto = (Producto) lista_productos.getAdapter().getItem(position);
                posicion_producto_editar = position;

                openContextMenu(lista_productos);

                return true;
            }
        });

        registerForContextMenu(lista_productos);
    }

    @Override
    public void onClick(View v) {
        if (v==btn_anadir_producto){
            crearDialogAnadirProducto();
        }

        if (v==btn_limpiar_lista){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Vas a eliminar los productos de la lista ¿estas segur@?")
                    .setTitle("COMPRA FINALIZADA")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            limpiarLista();
                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_productos_long_click, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        Producto producto_pulsado = productos_lista.get(posicion_producto_editar);

        if(item.getItemId()==R.id.editar_producto){
            Toast.makeText(getApplicationContext(),"-->"+posicion_producto_editar,Toast.LENGTH_LONG).show();
            crearDialogEditarProducto(producto_pulsado.getId_producto(), producto_pulsado.getNombre_producto(), producto_pulsado.getPrecio(), producto_pulsado.getCantidad());
        }else{
            return false;
        }
        return true;
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

    private void crearProducto(String nombre_producto, int cantidad_producto, final double precio_producto) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_nueva_lista = new RequestParams();
        envio_nueva_lista.put("nombre", nombre_producto);
        envio_nueva_lista.put("cantidad", cantidad_producto);
        envio_nueva_lista.put("precio", precio_producto);
        envio_nueva_lista.put("id_lista", datosLista_id_lista);
        final Producto producto_anadido = new Producto();
        producto_anadido.setNombre_producto(nombre_producto);
        producto_anadido.setPrecio(precio_producto);
        producto_anadido.setCantidad(cantidad_producto);

        client.post(URL_CREAR_PRODUCTO, envio_nueva_lista, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                String estado = "";
                int id_producto_creado;

                try {
                    estado = response.getString("status");
                    id_producto_creado = Integer.parseInt(response.getString("data"));

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), "Resultado " + estado, Toast.LENGTH_SHORT).show();
                        producto_anadido.setId_producto(id_producto_creado);

                        productos_lista.add(producto_anadido);

                        myAdapter.notifyDataSetChanged();
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

    private void limpiarLista() {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_limpiar_lista = new RequestParams();
        envio_limpiar_lista.put("id_lista", datosLista_id_lista);

        client.post(URL_LIMPIAR_LISTA, envio_limpiar_lista, new JsonHttpResponseHandler(){
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
                        productos_lista.clear();

                        myAdapter.notifyDataSetChanged();
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
        txv_pro_prec.setText("Precio:");

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
        alertDialogBuilder.setTitle("AÑADIR PRODUCTO");
        alertDialogBuilder.setCustomTitle(tv);

        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("AÑADIR PRODUCTO", new DialogInterface.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(), "ERROR al añadir el producto", Toast.LENGTH_SHORT).show();
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

    private void crearDialogEditarProducto(final int id_producto, final String nombre_producto, double precio_producto, int cantidad_producto) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("EDITAR PRODUCTO");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText edt_nom_pro = new EditText(this);
        edt_nom_pro.setText(nombre_producto);
        TextView txv_nom_pro = new TextView(this);
        txv_nom_pro.setText("Nombre producto:");

        final EditText edt_pro_cant = new EditText(this);
        edt_pro_cant.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_pro_cant.setText("1");
        edt_pro_cant.setText(String.valueOf(cantidad_producto));
        TextView txv_pro_cant = new TextView(this);
        txv_pro_cant.setText("Cantidad:");

        final EditText edt_pro_prec = new EditText(this);
        edt_pro_prec.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edt_pro_prec.setText("0");
        edt_pro_prec.setText(String.valueOf(precio_producto));
        TextView txv_pro_prec = new TextView(this);
        txv_pro_prec.setText("Precio:");

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
        alertDialogBuilder.setTitle("AÑADIR PRODUCTO");
        alertDialogBuilder.setCustomTitle(tv);

        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("EDITAR PRODUCTO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    /*int cant_pro = 1;
                    double prec_pro = 0;

                    if (!edt_nom_pro.getText().toString().isEmpty()){
                        if (!edt_pro_cant.getText().toString().isEmpty()){
                            cant_pro = Integer.parseInt(edt_pro_cant.getText().toString());
                        }
                        if (!edt_pro_prec.getText().toString().isEmpty()){
                            prec_pro = Double.parseDouble(edt_pro_prec.getText().toString());
                        }
                        editarProducto(id_producto, nombre_producto, cant_pro, prec_pro);
                    }*/
                    Toast.makeText(getApplicationContext(), "Todavia no esta", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "ERROR al editar el producto", Toast.LENGTH_SHORT).show();
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

    private void editarProducto(int id_producto, String nombre_producto, int cant_pro, double prec_pro) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_editar_producto = new RequestParams();
        envio_editar_producto.put("id_producto", id_producto);
        envio_editar_producto.put("nombre", nombre_producto);
        envio_editar_producto.put("cantidad", cant_pro);
        envio_editar_producto.put("precio", prec_pro);
        final Producto producto_editar = new Producto();
        producto_editar.setNombre_producto(nombre_producto);
        producto_editar.setPrecio(prec_pro);
        producto_editar.setCantidad(cant_pro);

        client.post(URL_EDITAR_PRODUCTO, envio_editar_producto, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                String estado = "";
                int id_producto_creado;

                try {
                    estado = response.getString("status");
                    id_producto_creado = Integer.parseInt(response.getString("data"));

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), "Resultado " + estado, Toast.LENGTH_SHORT).show();
                        /*producto_editar.setId_producto(id_producto_creado);

                        productos_lista.add(producto_anadido);

                        myAdapter.notifyDataSetChanged();*/
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
