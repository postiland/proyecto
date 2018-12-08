package postigo.listadelacompra;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class VistaListas extends AppCompatActivity implements DialogNombreLista.DialogCrearListaListener, DialogInvitarUsuarioLista.DialogInvitarUsuarioListaListener{

    private String datosUsuario_id_usuario;
    private String datosUsuario_nombre_usuario;

    ListView lista_listas;

    Button crear_lista;

    TextView txv_nom_lis;

    private int id_lista_borrar = 0;

    private String nombre_lista_manipular = "";

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

    public static final String URL_COGER_USUARIOS_AVISAR = "http://antoniopostigo.es/Slim2-ok/api/obtener/usuarios/lista";

    public static final String URL_ENVIAR_EMAIL_AVISO = "http://antoniopostigo.es/Slim2-ok/api/alerta/usuarios/compra";

    public static final String URL_CREAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/crear/lista";

    public static final String URL_BORRAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/eliminar/lista";

    public static final String URL_INVITAR_USUARIO_LISTA = "http://antoniopostigo.es/Slim2-ok/api/anadir/usuario/lista";

    public static final String URL_EDITAR_LISTA = "http://antoniopostigo.es/Slim2-ok/api/editar/lista";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_listas);

        getSupportActionBar().setTitle("Listas activas");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.verdeOscuro)));

        datosUsuario_id_usuario= getIntent().getStringExtra("ID_USUARIO");
        datosUsuario_nombre_usuario= getIntent().getStringExtra("NOMBRE_USUARIO");

        lista_listas=(ListView) findViewById(R.id.ltv_listas);

        crear_lista=(Button) findViewById(R.id.btn_crear_lista);
        crear_lista.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (v == crear_lista) {
                    crearDialogCrearLista();
                }
            }
        });

        icono_errores = (ImageView) findViewById(R.id.imv_icono_errores_listas);
        icono_errores.setVisibility(View.INVISIBLE);
        icono_ok = (ImageView) findViewById(R.id.imv_icono_ok_listas);
        icono_ok.setVisibility(View.INVISIBLE);
        txv_mensajes = (TextView) findViewById(R.id.txv_mensajes_listas);

        cogerListas();

        myAdapter = new ArrayAdapter<Lista>(this, R.layout.vista_item_producto, listas_usuario){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
                View itemLista = inflater.inflate(R.layout.vista_item_lista, null);

                if (listas_usuario.size() > 0) {
                    ViewHolder mContenedor = new ViewHolder();
                    mContenedor.nombre = (TextView) itemLista.findViewById(R.id.txv_nom_lis);
                    mContenedor.numArticulos = (TextView) itemLista.findViewById(R.id.txv_total_articulos);
                    mContenedor.numUsuarios = (TextView) itemLista.findViewById(R.id.txv_numero_usuarios);
                    mContenedor.precioTotal = (TextView) itemLista.findViewById(R.id.txv_total_precio);

                    mContenedor.nombre.setText(listas_usuario.get(position).getNombre());
                    mContenedor.numArticulos.setText(String.valueOf(listas_usuario.get(position).getNumero_articulos()));
                    mContenedor.numUsuarios.setText(String.valueOf(listas_usuario.get(position).getNumero_usuarios()));
                    mContenedor.precioTotal.setText(String.valueOf(listas_usuario.get(position).getPrecio_total()));

                    itemLista.setTag(mContenedor);
                }

                return itemLista;
            }
        };
        lista_listas.setAdapter(myAdapter);

        lista_listas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lista lista = (Lista) lista_listas.getAdapter().getItem(position);

                Intent intent = new Intent(getBaseContext(), VistaProductosLista.class);
                intent.putExtra("ID_LISTA", String.valueOf(lista.getId_lista()));
                intent.putExtra("NOMBRE_LISTA", String.valueOf(lista.getNombre()));
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
                    nombre_lista_manipular = lista.getNombre();
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
            txv_mensajes.setTextColor(getResources().getColor(R.color.rojo));
        }else {
            icono_ok.setVisibility(View.VISIBLE);
            txv_mensajes.setTextColor(getResources().getColor(R.color.verdeOscuro));
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
        else if(item.getItemId()==R.id.avisar_usuarios){
            cogerUsuariosAvisar();

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
                        lista.setId_usuario(Integer.parseInt(data_user.getString("id_usuario")));
                        lista.setNombre(data_user.getString("nombre"));
                        lista.setNumero_usuarios(Integer.parseInt(data_user.getString("numUsuarios")));
                        lista.setNumero_articulos(Integer.parseInt(data_user.getString("numArticulos")));
                        lista.setPrecio_total(Double.parseDouble(data_user.getString("precioTotal")));

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

    private void cogerUsuariosAvisar() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_email_aviso = new RequestParams();
        envio_email_aviso.put("id_lista", id_lista_borrar);

        client.post(URL_COGER_USUARIOS_AVISAR, envio_email_aviso, new JsonHttpResponseHandler(){
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

                    Usuario[] usuarios_avisar = new Usuario[datos_usuario.length()];

                    for (int i=0; i<datos_usuario.length(); i++){
                        Usuario usuario_avisar = new Usuario();

                        data_user = datos_usuario.getJSONObject(i);
                        usuario_avisar.setId_usuario(Integer.parseInt(data_user.getString("id_usuario")));
                        usuario_avisar.setNombre(data_user.getString("nombre"));
                        usuario_avisar.setEmail(data_user.getString("email"));

                        usuarios_avisar[i] = usuario_avisar;

                        enviarEmailAvisoCompra(usuario_avisar);
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

    private void enviarEmailAvisoCompra(Usuario usuario_avisar) {
        //Toast.makeText(getApplicationContext(), "Holi: "+usuario_avisar.toString()+"\n--"+nombre_lista_manipular, Toast.LENGTH_SHORT).show();

        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams envio_email_aviso = new RequestParams();
        envio_email_aviso.put("nombre_usuario_invitado", usuario_avisar.getNombre());
        envio_email_aviso.put("email_usuario_invitado", usuario_avisar.getEmail());
        envio_email_aviso.put("nombre_usuario_compra", datosUsuario_nombre_usuario);
        envio_email_aviso.put("nombre_lista", nombre_lista_manipular);

        client.post(URL_ENVIAR_EMAIL_AVISO, envio_email_aviso, new JsonHttpResponseHandler(){
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
                        mostrarMensajeInfo("Emails enviados a los usuarios", false);
                    }else {
                        mostrarMensajeInfo("Fallo al mandar emails", true);
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


                    if (estado.length()>0) {
                        id_lista_creada = Integer.parseInt(response.getString("data"));

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
        DialogNombreLista dialogCrearLista= new DialogNombreLista();
        dialogCrearLista.show(getSupportFragmentManager(), "Crear lista");
    }


    @Override
    public void cogerTextoCrearLista(String nombre_lista, boolean isNew) {
        try {
            if (nombre_lista.isEmpty() || nombre_lista.equals("Nombre lista")){
                mostrarMensajeInfo("Debes introducir un nombre de lista", true);
            }else {
                if (isNew) {
                    crearLista(nombre_lista);
                }else {
                    editarLista(nombre_lista);
                }
            }

        }catch (Exception e){
            mostrarMensajeInfo("ERROR! Imposible crear lista", true);
        }
    }

    private void crearDialogEditarLista(String nombre_lista_pulsada) {
        DialogNombreLista dialogCrearLista= new DialogNombreLista();
        dialogCrearLista.setNomListEdit(nombre_lista_pulsada);
        dialogCrearLista.show(getSupportFragmentManager(), "Editar lista");
    }

    private void crearDialogInvitarUsuarioLista() {
        DialogInvitarUsuarioLista dialogInvitarUsuarioLista= new DialogInvitarUsuarioLista();
        dialogInvitarUsuarioLista.show(getSupportFragmentManager(), "Invitar a usuario");
    }

    @Override
    public void cogerEmailInvitarUsuarioLista(String email_usuario) {
        try {
            Matcher mather = modeloEmail.matcher(email_usuario);

            if (email_usuario.isEmpty() || email_usuario.equals("E-mail usuario")){
                mostrarMensajeInfo("Debes introducir un email", true);
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


    private static class ViewHolder {
        TextView nombre;
        TextView numArticulos;
        TextView numUsuarios;
        TextView precioTotal;
    }
}
