package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class VistaListas extends AppCompatActivity implements View.OnClickListener{

    private String datosUsuario_id_usuario;

    ListView lista_listas;

    Button crear_lista;

    TextView txv_nom_lis;

    private int id_lista_borrar = 0;

    private int id_posicion_lista = 0;

    private ArrayList<Lista> listas_usuario;
    ArrayAdapter<Lista> myAdapter;

    public static final String URL_COGER_LISTAS = "http://antoniopostigo.es/Slim2-ok/api/id_usuario/obtener/id_listas";

    public static final String URL_CREAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/crear/lista";

    public static final String URL_BORRAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/lista";

    public static final String URL_EDITAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/editar/lista";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_listas);

        datosUsuario_id_usuario= getIntent().getStringExtra("ID_USUARIO");

        lista_listas=(ListView) findViewById(R.id.ltv_listas);

        crear_lista=(Button) findViewById(R.id.btn_crear_lista);
        crear_lista.setOnClickListener(this);

        txv_nom_lis = (TextView) findViewById(R.id.txv_nombre_lista);

        cogerListas();

        myAdapter = new ArrayAdapter<Lista>(this, android.R.layout.simple_list_item_1, listas_usuario);
        lista_listas.setAdapter(myAdapter);

        lista_listas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lista lista = (Lista) lista_listas.getAdapter().getItem(position);

                Intent intent = new Intent(getBaseContext(), VistaProductosLista.class);
                intent.putExtra("ID_LISTA", String.valueOf(lista.getId_lista()));
                startActivity(intent);
            }
        });

        lista_listas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Lista lista = (Lista) lista_listas.getAdapter().getItem(position);
                id_posicion_lista = position;

                if (String.valueOf(lista.getId_usuario()).equals(datosUsuario_id_usuario)){
                    id_lista_borrar = lista.getId_lista();
                    openContextMenu(lista_listas);
                }else {
                    Toast.makeText(getApplicationContext(), "Solo el propietario puede editar o eliminar una lista", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        registerForContextMenu(lista_listas);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_long_click, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        Lista lista_pulsada = listas_usuario.get(id_posicion_lista);

        if (id_lista_borrar == 0){
            Toast.makeText(getApplicationContext(),"ERROR obteniendo la id a borrar",Toast.LENGTH_LONG).show();
            return false;
        }

        if(item.getItemId()==R.id.editar_lista){
            crearDialogEditarLista(lista_pulsada.getNombre());
        }
        else if(item.getItemId()==R.id.eliminar_lista){
            borrarLista();
        }else{
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == crear_lista) {
            crearDialogCrearLista();
        }
    }

    private void borrarLista() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_id_lista = new RequestParams();
        envio_id_lista.put("id_lista", id_lista_borrar);

        client.post(URL_BORRAR_LISTA, envio_id_lista, new JsonHttpResponseHandler(){
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
                Lista listaBorrar = new Lista();

                try {
                    estado = response.getString("status");

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), "Resultado " + estado, Toast.LENGTH_SHORT).show();
                        for(int i=0;i<listas_usuario.size();i++) {
                            listaBorrar = listas_usuario.get(i);
                            if (listaBorrar.getId_lista() == id_lista_borrar){
                                listas_usuario.remove(i);

                                myAdapter.notifyDataSetChanged();
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

    private void cogerListas() {
        final ProgressDialog progreso = new ProgressDialog(this);
        listas_usuario=new ArrayList<Lista>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_id_usuario = new RequestParams();
        envio_id_usuario.put("id_usuario", datosUsuario_id_usuario);

        client.post(URL_COGER_LISTAS, envio_id_usuario, new JsonHttpResponseHandler(){
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

                    listas_usuario.clear();

                    for (int i=0; i<datos_usuario.length(); i++){
                        Lista lista=new Lista();

                        data_user = datos_usuario.getJSONObject(i);
                        lista.setId_lista(Integer.parseInt(data_user.getString("id_lista")));
                        lista.setNombre(data_user.getString("nombre"));
                        lista.setId_usuario(Integer.parseInt(data_user.getString("id_usuario")));

                        listas_usuario.add(lista);
                    }

                    myAdapter.notifyDataSetChanged();
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

    private void crearLista(final String nombre_lista) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_nueva_lista = new RequestParams();
        envio_nueva_lista.put("nombre", nombre_lista);
        envio_nueva_lista.put("id_usuario", datosUsuario_id_usuario);
        final Lista listaAnadir = new Lista();

        client.post(URL_CREAR_LISTA, envio_nueva_lista, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                String estado = "";
                int id_lista_creada;

                try {
                    estado = response.getString("status");
                    id_lista_creada = Integer.parseInt(response.getString("data"));

                    if (estado.length()>0) {
                        Toast.makeText(getApplicationContext(), "Resultado " + id_lista_creada, Toast.LENGTH_SHORT).show();
                        listaAnadir.setId_lista(id_lista_creada);
                        listaAnadir.setNombre(nombre_lista);
                        listaAnadir.setId_usuario(Integer.parseInt(datosUsuario_id_usuario));
                        listas_usuario.add(listaAnadir);

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

    private void editarLista(final String nombre_lista) {
        final ProgressDialog progreso = new ProgressDialog(getApplicationContext());
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_nueva_lista = new RequestParams();
        envio_nueva_lista.put("nombre", nombre_lista);
        envio_nueva_lista.put("id_lista", id_lista_borrar);

        client.post(URL_EDITAR_LISTA, envio_nueva_lista, new JsonHttpResponseHandler(){
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

                        for (int i=0; i<listas_usuario.size(); i++){
                            Lista recorriendo_lista = listas_usuario.get(i);

                            if (recorriendo_lista.getId_lista() == id_lista_borrar){
                                listas_usuario.get(i).setNombre(nombre_lista);

                                myAdapter.notifyDataSetChanged();
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

    private void crearDialogCrearLista() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("CREAR LISTA");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        TextView tv1 = new TextView(this);
        tv1.setText("Nombre lista:");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1, tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("CREAR LISTA");
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
        alertDialogBuilder.setPositiveButton("CREAR LISTA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    crearLista(et.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "ERROR AL DAR OK", Toast.LENGTH_SHORT).show();
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

    private void crearDialogEditarLista(String nombre_lista_pulsada) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("EDITAR LISTA");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        et.setText(nombre_lista_pulsada);
        TextView tv1 = new TextView(this);
        tv1.setText("Nombre lista:");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1, tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

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
                    editarLista(et.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "ERROR AL DAR OK", Toast.LENGTH_SHORT).show();
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
}
