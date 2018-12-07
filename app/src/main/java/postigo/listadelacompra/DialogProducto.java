package postigo.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogProducto extends AppCompatDialogFragment {

    private EditText nombre_producto;
    private EditText cantidad_producto;
    private EditText precio_producto;


    private DialogProductoListener listener;

    private String nom_list_edit = "";

    private String titulo_boton_ok = "Crear lista";

    public void setNomListEdit(String nom_list){
        this.nom_list_edit = nom_list;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_producto, null);

        nombre_producto = view.findViewById(R.id.edt_nom_producto);
        cantidad_producto = view.findViewById(R.id.edt_cant_producto);
        precio_producto = view.findViewById(R.id.edt_prec_producto);

        /*if (!nom_list_edit.isEmpty()) {
            nombre_lista.setText(this.nom_list_edit);
            nombre_lista.setTextColor(getResources().getColor(R.color.negro));
            titulo_boton_ok = "Editar nombre";
        }*/

        builder.setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(titulo_boton_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nom_pro = nombre_producto.getText().toString();
                String cant_pro = cantidad_producto.getText().toString();
                String prec_pro = precio_producto.getText().toString();

                if (!nom_pro.isEmpty()){
                    if (nom_list_edit.isEmpty()) {
                        listener.cogerContenidoCajasTextoDialogProducto(nom_pro, cant_pro, prec_pro, true);
                    }else {
                        listener.cogerContenidoCajasTextoDialogProducto(nom_pro, cant_pro, prec_pro,false);
                    }
                }

            }
        });

        nombre_producto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(nombre_producto.getText());
                    if (cojoTextoActual.equals("Producto")){
                        nombre_producto.setText("");
                        nombre_producto.setTextColor(getResources().getColor(R.color.negro));
                    }
                }
            }
        });

        cantidad_producto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(cantidad_producto.getText());
                    if (cojoTextoActual.equals("Cantidad")){
                        cantidad_producto.setText("");
                        cantidad_producto.setInputType(InputType.TYPE_CLASS_NUMBER);
                        cantidad_producto.setTextColor(getResources().getColor(R.color.negro));
                    }
                }
            }
        });

        precio_producto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(precio_producto.getText());
                    if (cojoTextoActual.equals("Precio")){
                        precio_producto.setText("");
                        precio_producto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        precio_producto.setTextColor(getResources().getColor(R.color.negro));
                    }
                }
            }
        });

        return  builder.create();
    }

    public interface DialogProductoListener {
        void cogerContenidoCajasTextoDialogProducto(String nombre_producto, String canditad, String precio, boolean isNew);
    }
}
