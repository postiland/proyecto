package postigo.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogProducto extends AppCompatDialogFragment {

    private TextView titulo_dialog;
    private EditText nombre_producto;
    private EditText cantidad_producto;
    private EditText precio_producto;


    private DialogProductoListener listener;

    private String id_prod_edit = "";
    private String nom_prod_edit = "";
    private String cant_prod_edit = "";
    private String prec_prod_edit = "";

    private String titulo_boton_ok = "Añadir producto";

    public void setIdProdEdit(String id_prod){
        this.id_prod_edit = id_prod;
    }
    public void setNomProdEdit(String nom_prod){
        this.nom_prod_edit = nom_prod;
    }
    public void setCantProdEdit(String cant_prod){
        this.cant_prod_edit = cant_prod;
    }
    public void setPrecProdEdit(String prec_prod){
        this.prec_prod_edit = prec_prod;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_producto, null);

        nombre_producto = view.findViewById(R.id.edt_nom_producto);

        titulo_dialog = view.findViewById(R.id.titulo_dialog_producto);

        cantidad_producto = view.findViewById(R.id.edt_cant_producto);
        int maxLengthCant = 4;
        InputFilter[] fArrayC = new InputFilter[1];
        fArrayC[0] = new InputFilter.LengthFilter(maxLengthCant);
        cantidad_producto.setFilters(fArrayC);
        cantidad_producto.setInputType(InputType.TYPE_CLASS_NUMBER);

        precio_producto = view.findViewById(R.id.edt_prec_producto);
        int maxLengthPre = 7;
        InputFilter[] fArrayP = new InputFilter[1];
        fArrayP[0] = new InputFilter.LengthFilter(maxLengthPre);
        precio_producto.setFilters(fArrayP);
        precio_producto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (!nom_prod_edit.isEmpty()) {
            titulo_dialog.setText("Editar");
            nombre_producto.setText(this.nom_prod_edit);
            nombre_producto.setTextColor(getResources().getColor(R.color.negro));
            if (!cant_prod_edit.isEmpty()) {
                cantidad_producto.setText(this.cant_prod_edit);
                cantidad_producto.setTextColor(getResources().getColor(R.color.negro));
            }
            if (!prec_prod_edit.isEmpty()) {
                precio_producto.setText(this.prec_prod_edit);
                precio_producto.setTextColor(getResources().getColor(R.color.negro));
            }
            titulo_boton_ok = "Editar producto";
        }

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
                    if (nom_prod_edit.isEmpty()) {
                        listener.cogerContenidoCajasTextoDialogProducto("0", nom_pro, cant_pro, prec_pro, true);
                    }else {
                        listener.cogerContenidoCajasTextoDialogProducto(id_prod_edit, nom_pro, cant_pro, prec_pro,false);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogProductoListener) context;
        }catch (Exception e){
            Toast.makeText(getContext(), "Error al crear dialog crear lista:\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface DialogProductoListener {
        void cogerContenidoCajasTextoDialogProducto(String id_producto, String nombre_producto, String canditad, String precio, boolean isNew);
    }
}
