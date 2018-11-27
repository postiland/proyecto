package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class VistaListas extends AppCompatActivity implements View.OnClickListener{

    private String datosUsuario_id_usuario;

    ListView lista_listas;

    Button crear_lista;

    TextView txv_nom_lis;

    private int id_lista_borrar = 0;

    private int id_posicion_lista = 0;

    private ImageView icono_errores;

    private ImageView icono_ok;

    private TextView txv_mensajes;

    private ArrayList<Lista> listas_usuario;
    ArrayAdapter<Lista> myAdapter;

    private Pattern modeloEmail = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static final String URL_COGER_LISTAS = "http://antoniopostigo.es/Slim2-ok/api/id_usuario/obtener/id_listas";

    public static final String URL_CREAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/crear/lista";

    public static final String URL_BORRAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/lista";

    public static final String URL_INVITAR_USUARIO_LISTA = "http://antoniopostigo.es/Slim2-ok/api/anadir/usuario/lista";

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

        icono_errores = (ImageView) findViewById(R.id.imv_icono_errores_listas);
        icono_errores.setVisibility(View.INVISIBLE);
        icono_ok = (ImageView) findViewById(R.id.imv_icono_ok_listas);
        icono_ok.setVisibility(View.INVISIBLE);
        txv_mensajes = (TextView) findViewById(R.id.txv_mensajes_listas);

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
                    mostrarMensajeInfo("Solo el propietario puede editar o eliminar una lista", true);
                }

                return true;
            }
        });

        registerForContextMenu(lista_listas);
    }

    private void mostrarMensajeInfo(String mensaje, boolean esError){
        if (esError) {
            icono_errores.setVisibility(View.VISIBLE);
            txv_mensajes.setTextColor(Color.RED);
        }else {
            icono_ok.setVisibility(View.VISIBLE);
            txv_mensajes.setTextColor(Color.GREEN);
        }
        txv_mensajes.setText(mensaje);
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
            mostrarMensajeInfo("ERROR! Imposible borrar lista", true);
            return false;
        }

        if(item.getItemId()==R.id.editar_lista){
            crearDialogEditarLista(lista_pulsada.getNombre());
        }
        else if(item.getItemId()==R.id.eliminar_lista){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Estás seguro de que deseas eliminar esta lista?")
                    .setTitle("ELIMINAR LISTA")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            borrarLista();
                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else if(item.getItemId()==R.id.invitar_usuario_lista){
            crearDialogInvitarUsuarioLista();
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

    private void invitarUsuarioLista(String email_usuario_invitar) {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_id_lista = new RequestParams();
        envio_id_lista.put("id_lista", id_lista_borrar);
        envio_id_lista.put("email_usuario", email_usuario_invitar);

        client.post(URL_INVITAR_USUARIO_LISTA, envio_id_lista, new JsonHttpResponseHandler(){
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
                String resultadoOK;
                Lista listaBorrar = new Lista();

                try {
                    estado = response.getString("status");
                    resultadoOK = response.getString("data");

                    if (resultadoOK.length() == 1) {
                        mostrarMensajeInfo("Usuario añadido a la lista", false);
                    }else {
                        mostrarMensajeInfo("Lo sentimos, el usuario no existe", true);
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
                        mostrarMensajeInfo("Lista eliminada", false);
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
                        mostrarMensajeInfo("Lista creada", false);
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
                        mostrarMensajeInfo("Lista editada", false);

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
        int maxLengthNom = 20;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLengthNom);
        et.setFilters(fArray);

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
                    String nombre_lista = et.getText().toString();
                    if (!nombre_lista.isEmpty()) {
                        crearLista(et.getText().toString());
                    }else {
                        mostrarMensajeInfo("Debes introducir un nombre", true);
                    }
                }catch (Exception e){
                    mostrarMensajeInfo("ERROR! Imposible crear lista", true);
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
        int maxLengthNom = 20;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLengthNom);
        et.setFilters(fArray);

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
                    String nombre_lista = et.getText().toString();
                    if (!nombre_lista.isEmpty()) {
                        editarLista(nombre_lista);
                    }else {
                        mostrarMensajeInfo("Debes introducir un nombre", true);
                    }
                }catch (Exception e){
                    mostrarMensajeInfo("ERROR! Imposible editar lista", true);
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

    private void crearDialogInvitarUsuarioLista() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("INVITAR USUARIO A LA LISTA");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        TextView tv1 = new TextView(this);
        tv1.setText("Email de usuario:");
        int maxLengthNom = 60;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLengthNom);
        et.setFilters(fArray);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1, tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("INVITAR USUARIO A LA LISTA");
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
        alertDialogBuilder.setPositiveButton("INVITAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String email_usuario = et.getText().toString();

                    Matcher mather = modeloEmail.matcher(email_usuario);

                    if (email_usuario.isEmpty()){
                        mostrarMensajeInfo("Debes introducir un email!", true);
                    }else {
                        if (!mather.find()){
                            mostrarMensajeInfo("Debes introducir un email válido", true);
                        } else {
                            invitarUsuarioLista(email_usuario);
                        }
                    }
                }catch (Exception e){
                    mostrarMensajeInfo("ERROR! Imposible invitar al usuario", true);
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
