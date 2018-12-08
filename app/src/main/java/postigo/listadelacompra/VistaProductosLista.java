package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VistaProductosLista extends AppCompatActivity implements DialogProducto.DialogProductoListener{

    private String datosLista_id_lista;
    private String datosLista_nombre_lista;
    private String datosUsuario_id_usuario;
    private String datosUsuario_nombre_usuario;

    private int posicion_producto_editar;

    private Button btn_anadir_producto;

    private Button btn_limpiar_lista;

    ListView lista_productos;

    private ImageView icono_errores;

    private ImageView icono_ok;

    private TextView txv_mensajes;

    private ArrayList<Producto> productos_lista;
    ArrayAdapter<Producto> myAdapter;

    public static final String URL_COGER_PRODUCTOS = "http://antoniopostigo.es/Slim2-ok/api/id_lista/obtener/productos";

    public static final String URL_CREAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/anadir/producto/lista";

    public static final String URL_EDITAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/editar/producto";

    public static final String URL_EDITAR_PRODUCTO_COMPRADO = "http://antoniopostigo.es/Slim2-ok/api/editar/producto/comprado";

    public static final String URL_LIMPIAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/productos/lista";

    public static final String URL_ELIMINAR_PRODUCTO = "http://antoniopostigo.es/Slim2-ok/api/eliminar/producto/lista";

    public static final String URL_LIMPIAR_PRODUCTOS = "http://antoniopostigo.es/Slim2-ok/api/limpiar/productos/comprados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_productos_lista);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        datosLista_id_lista= getIntent().getStringExtra("ID_LISTA");
        datosLista_nombre_lista= getIntent().getStringExtra("NOMBRE_LISTA");
        datosUsuario_id_usuario= getIntent().getStringExtra("ID_USUARIO");
        datosUsuario_nombre_usuario= getIntent().getStringExtra("NOMBRE_USUARIO");

        getSupportActionBar().setTitle("Lista: "+datosLista_nombre_lista);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.verdeOscuro)));

        lista_productos=(ListView) findViewById(R.id.ltv_productos);

        btn_anadir_producto=(Button) findViewById(R.id.btn_anadir_producto);
        btn_anadir_producto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                crearDialogAnadirProducto();
            }
        });

        btn_limpiar_lista=(Button) findViewById(R.id.btn_limpiar_lista);
        btn_limpiar_lista.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mostrarAlertDialogLimpiarLista("Vas a eliminar los productos de la lista ¿estas segura/o?");
            }
        });

        icono_errores = (ImageView) findViewById(R.id.imv_icono_error_productos);
        icono_errores.setVisibility(View.INVISIBLE);
        icono_ok = (ImageView) findViewById(R.id.imv_icono_ok_productos);
        icono_ok.setVisibility(View.INVISIBLE);
        txv_mensajes = (TextView) findViewById(R.id.txv_mensajes_productos);

        cogerProductos(datosLista_id_lista);

        productos_lista=new ArrayList<Producto>();
        myAdapter = new ArrayAdapter<Producto>(this, R.layout.vista_item_producto, productos_lista){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
                View itemLista = inflater.inflate(R.layout.vista_item_producto, null);

                ViewHolder mContenedor = new ViewHolder();
                mContenedor.producto = (TextView) itemLista.findViewById(R.id.txv_nom_pro_lis);
                mContenedor.cantidad = (TextView) itemLista.findViewById(R.id.txv_cant_pro_list);
                mContenedor.precio = (TextView) itemLista.findViewById(R.id.txv_prec_pro_list);
                mContenedor.tituloCantidad = (TextView) itemLista.findViewById(R.id.txv_titulo_cantidad);
                mContenedor.tituloPrecio = (TextView) itemLista.findViewById(R.id.txv_titulo_precio);
                mContenedor.simboloEuro = (TextView) itemLista.findViewById(R.id.txv_simbolo_euro);
                mContenedor.icono_tick = (ImageView) itemLista.findViewById(R.id.icono_tick);

                mContenedor.producto.setText(productos_lista.get(position).getNombre_producto());
                mContenedor.cantidad.setText(String.valueOf(productos_lista.get(position).getCantidad()));
                mContenedor.precio.setText(String.valueOf(productos_lista.get(position).getPrecio()));

                mContenedor.producto.setTextColor(getResources().getColor(R.color.negro));
                mContenedor.cantidad.setTextColor(getResources().getColor(R.color.negro));
                mContenedor.precio.setTextColor(getResources().getColor(R.color.negro));
                mContenedor.tituloCantidad.setTextColor(getResources().getColor(R.color.negro));
                mContenedor.tituloPrecio.setTextColor(getResources().getColor(R.color.negro));
                mContenedor.simboloEuro.setTextColor(getResources().getColor(R.color.negro));

                if (productos_lista.get(position).getComprado() == 1){
                    itemLista.setBackgroundColor(getResources().getColor(R.color.grisClaro));
                    mContenedor.icono_tick.setVisibility(View.VISIBLE);
                    mContenedor.producto.setTextColor(getResources().getColor(R.color.verdeOscuro));
                    mContenedor.cantidad.setTextColor(getResources().getColor(R.color.verdeOscuro));
                    mContenedor.precio.setTextColor(getResources().getColor(R.color.verdeOscuro));
                    mContenedor.tituloCantidad.setTextColor(getResources().getColor(R.color.verdeOscuro));
                    mContenedor.tituloPrecio.setTextColor(getResources().getColor(R.color.verdeOscuro));
                    mContenedor.simboloEuro.setTextColor(getResources().getColor(R.color.verdeOscuro));
                }

                itemLista.setTag(mContenedor);

                return itemLista;
            }
        };
        lista_productos.setAdapter(myAdapter);

        lista_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Producto producto = (Producto) lista_productos.getAdapter().getItem(position);

                if (producto.getComprado() == 1) {
                    view.setBackgroundColor(Color.WHITE);
                    cambiarEstadoComprado(0, producto.getId_producto());
                }else{
                    view.setBackgroundColor(Color.LTGRAY);
                    cambiarEstadoComprado(1, producto.getId_producto());
                }
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
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), VistaListas.class);
        intent.putExtra("ID_USUARIO", datosUsuario_id_usuario);
        intent.putExtra("NOMBRE_USUARIO", datosUsuario_nombre_usuario);
        startActivity(intent);
    }

    private void cambiarEstadoComprado(final int comprado, final int id_producto) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_editar_producto = new RequestParams();
        envio_editar_producto.put("comprado", comprado);
        envio_editar_producto.put("id_producto", id_producto);
        envio_editar_producto.put("id_lista", datosLista_id_lista);

        client.post(URL_EDITAR_PRODUCTO_COMPRADO, envio_editar_producto, new JsonHttpResponseHandler(){
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

                    int contadorComprados = 0;

                    for (int i=0; i<productos_lista.size(); i++){
                        Producto recorriendo_productos = productos_lista.get(i);

                        if (recorriendo_productos.getId_producto() == id_producto){
                            productos_lista.get(i).setComprado(comprado);

                            myAdapter.notifyDataSetChanged();

                        }
                    }

                    for (int i=0; i<productos_lista.size(); i++){
                        if (productos_lista.get(i).getComprado() == 1){
                            contadorComprados++;
                        }
                    }

                    if (contadorComprados == productos_lista.size()){
                        mostrarAlertDialogLimpiarLista("¡Has completado la compra! ¿Quieres limpiar la lista?");
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




    private static class ViewHolder {
        TextView producto;
        TextView cantidad;
        TextView precio;
        TextView tituloCantidad;
        TextView tituloPrecio;
        TextView simboloEuro;
        ImageView icono_tick;
    }

    private void mostrarMensajeInfo(String mensaje, boolean esError){
        if (esError) {
            icono_ok.setVisibility(View.INVISIBLE);
            icono_errores.setVisibility(View.VISIBLE);
            txv_mensajes.setTextColor(getResources().getColor(R.color.rojo));
        }else {
            icono_errores.setVisibility(View.INVISIBLE);
            icono_ok.setVisibility(View.VISIBLE);
            txv_mensajes.setTextColor(getResources().getColor(R.color.verdeOscuro));
        }
        txv_mensajes.setText(mensaje);
    }

    private void mostrarAlertDialogLimpiarLista(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setTitle("COMPRA FINALIZADA")
                .setPositiveButton("ELIMINAR TODOS LOS PRODUCTOS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        limpiarLista();
                    }
                })
                .setNegativeButton("LIMPIAR COLOR PRODUCTOS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        limpiarColorLista();
                    }
                })
                .setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
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
            crearDialogEditarProducto(producto_pulsado.getId_producto(), producto_pulsado.getNombre_producto(), producto_pulsado.getPrecio(), producto_pulsado.getCantidad());
        }else if(item.getItemId()==R.id.eliminar_producto){
            eliminarProductoLista(producto_pulsado.getId_producto());
        }else{
            return false;
        }
        return true;
    }

    private void eliminarProductoLista(final int id_producto) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_eliminar_producto_lista = new RequestParams();
        envio_eliminar_producto_lista.put("id_lista", datosLista_id_lista);
        envio_eliminar_producto_lista.put("id_producto", id_producto);

        client.post(URL_ELIMINAR_PRODUCTO, envio_eliminar_producto_lista, new JsonHttpResponseHandler(){
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
                    Producto productoBorrar;

                    if (estado.length()>0) {
                        mostrarMensajeInfo("Se ha eliminado el producto ", false);
                        for(int i=0;i<productos_lista.size();i++) {
                            productoBorrar = productos_lista.get(i);
                            if (productoBorrar.getId_producto() == id_producto){
                                productos_lista.remove(i);

                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    }else {
                        mostrarMensajeInfo("Error al eliminar el producto", true);
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
                        producto.setComprado(Integer.parseInt(data_user.getString("comprado")));

                        productos_lista.add(producto);

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
                        mostrarMensajeInfo("Producto creado", false);
                        producto_anadido.setId_producto(id_producto_creado);
                        productos_lista.add(producto_anadido);
                        myAdapter.notifyDataSetChanged();
                    }else {
                        mostrarMensajeInfo("Error al crear el producto", true);
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
                        productos_lista.clear();

                        myAdapter.notifyDataSetChanged();
                        mostrarMensajeInfo("Se ha vaciado la lista ", false);
                    }else {
                        mostrarMensajeInfo("Error al vaciar la lista", true);
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

    private void limpiarColorLista() {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_limpiar_lista = new RequestParams();
        envio_limpiar_lista.put("id_lista", datosLista_id_lista);

        client.post(URL_LIMPIAR_PRODUCTOS, envio_limpiar_lista, new JsonHttpResponseHandler(){
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

                        for (int i=0; i<productos_lista.size(); i++){
                            productos_lista.get(i).setComprado(0);
                        }

                        myAdapter.notifyDataSetChanged();
                    }else {
                        mostrarMensajeInfo("Error al vaciar la lista", true);
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
        DialogProducto dialogProducto= new DialogProducto();
        dialogProducto.show(getSupportFragmentManager(), "Anadir producto");
    }

    @Override
    public void cogerContenidoCajasTextoDialogProducto(String id_producto, String nombre_producto, String canditad, String precio, boolean isNew) {
        try {
            int id_pro = 0;
            int cant_pro = 1;
            double prec_pro = 0;

            //DecimalFormat df2 = new DecimalFormat("####.##");

            if (!nombre_producto.isEmpty() && !nombre_producto.equals("Producto")){
                if (!canditad.isEmpty() && !canditad.equals("Cantidad")){
                    cant_pro = Integer.parseInt(canditad);
                }
                if (!precio.isEmpty() && !precio.equals("Precio")){
                    prec_pro = Double.parseDouble(precio);
                    //prec_pro = Double.parseDouble(df2.format(prec_pro));
                }
                if (isNew) {
                    crearProducto(nombre_producto, cant_pro, prec_pro);
                }else {
                    id_pro = Integer.parseInt(id_producto);
                    editarProducto(id_pro, nombre_producto, cant_pro, prec_pro);
                }
            }else {
                mostrarMensajeInfo("Debes introducir un nombre de producto", true);
            }
        }catch (Exception e){
            mostrarMensajeInfo("Error al añadir el producto "+e.getMessage(), true);
        }
    }

    private void crearDialogEditarProducto(int id_producto, final String nombre_producto, final double precio_producto, final int cantidad_producto) {
        DialogProducto dialogProducto= new DialogProducto();
        dialogProducto.setIdProdEdit(String.valueOf(id_producto));
        dialogProducto.setNomProdEdit(nombre_producto);
        dialogProducto.setCantProdEdit(String.valueOf(cantidad_producto));
        dialogProducto.setPrecProdEdit(String.valueOf(precio_producto));
        dialogProducto.show(getSupportFragmentManager(), "Editar producto");
        /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

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
        int maxLengthNom = 20;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLengthNom);
        edt_nom_pro.setFilters(fArray);

        final EditText edt_pro_cant = new EditText(this);
        edt_pro_cant.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_pro_cant.setText("1");
        int maxLengthCant = 4;
        InputFilter[] fArrayC = new InputFilter[1];
        fArrayC[0] = new InputFilter.LengthFilter(maxLengthCant);
        edt_pro_cant.setFilters(fArrayC);
        edt_pro_cant.setText(String.valueOf(cantidad_producto));
        TextView txv_pro_cant = new TextView(this);
        txv_pro_cant.setText("Cantidad:");

        final EditText edt_pro_prec = new EditText(this);
        edt_pro_prec.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edt_pro_prec.setText("0");
        edt_pro_prec.setText(String.valueOf(precio_producto));
        int maxLengthPre = 7;
        InputFilter[] fArrayP = new InputFilter[1];
        fArrayP[0] = new InputFilter.LengthFilter(maxLengthPre);
        edt_pro_prec.setFilters(fArrayP);
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
                    int cant_pro = cantidad_producto;
                    double prec_pro = precio_producto;
                    String nom_pro = nombre_producto;

                    if (!edt_nom_pro.getText().toString().isEmpty()){
                        nom_pro = edt_nom_pro.getText().toString();
                    }
                    if (!edt_pro_cant.getText().toString().isEmpty()){
                        cant_pro = Integer.parseInt(edt_pro_cant.getText().toString());
                    }
                    if (!edt_pro_prec.getText().toString().isEmpty()){
                        prec_pro = Double.parseDouble(edt_pro_prec.getText().toString());
                    }
                    editarProducto(id_producto, nom_pro, cant_pro, prec_pro);
                }catch (Exception e){
                    mostrarMensajeInfo("Error al editar el producto", true);
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void editarProducto(final int id_producto, final String nombre_producto, final int cant_pro, final double prec_pro) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_editar_producto = new RequestParams();
        envio_editar_producto.put("id_producto", id_producto);
        envio_editar_producto.put("nombre", nombre_producto);
        envio_editar_producto.put("cantidad", cant_pro);
        envio_editar_producto.put("precio", prec_pro);
        envio_editar_producto.put("id_lista", datosLista_id_lista);
        final Producto producto_editar = new Producto();
        producto_editar.setId_producto(id_producto);
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

                try {
                    estado = response.getString("status");


                    if (estado.length()>0) {

                        for (int i=0; i<productos_lista.size(); i++){
                            Producto recorriendo_productos = productos_lista.get(i);

                            if (recorriendo_productos.getId_producto() == id_producto){
                                productos_lista.get(i).setNombre_producto(nombre_producto);
                                productos_lista.get(i).setPrecio(prec_pro);
                                productos_lista.get(i).setCantidad(cant_pro);
                                productos_lista.get(i).setComprado(0);

                                myAdapter.notifyDataSetChanged();

                                mostrarMensajeInfo("Producto editado", false);
                            }
                        }
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
