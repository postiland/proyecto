package postigo.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogNombreLista extends AppCompatDialogFragment {

    private EditText nombre_lista;

    private DialogCrearListaListener listener;

    private String nom_list_edit = "";

    private String titulo_boton_ok = "Crear lista";

    public void setNomListEdit(String nom_list){
        this.nom_list_edit = nom_list;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_crear_lista, null);

        nombre_lista = view.findViewById(R.id.edt_nom_lista);

        nombre_lista.setTextColor(getResources().getColor(R.color.gris));

        if (!nom_list_edit.isEmpty()) {
            nombre_lista.setText(this.nom_list_edit);
            nombre_lista.setTextColor(getResources().getColor(R.color.negro));
            titulo_boton_ok = "Editar nombre";
        }

        builder.setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(titulo_boton_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nom_lista = nombre_lista.getText().toString();

                        if (!nom_lista.isEmpty()){
                            if (nom_list_edit.isEmpty()) {
                                listener.cogerTextoCrearLista(nom_lista, true);
                            }else {
                                listener.cogerTextoCrearLista(nom_lista, false);
                            }
                        }

                    }
                });

        nombre_lista.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String cojoTextoActual = String.valueOf(nombre_lista.getText());
                    if (cojoTextoActual.equals("Nombre lista")){
                        nombre_lista.setText("");
                        nombre_lista.setTextColor(getResources().getColor(R.color.negro));
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
            listener = (DialogCrearListaListener) context;
        }catch (Exception e){
            Toast.makeText(getContext(), "Error al crear dialog crear lista:\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface DialogCrearListaListener{
        void cogerTextoCrearLista(String nombre_lista, boolean isNew);
    }
}
