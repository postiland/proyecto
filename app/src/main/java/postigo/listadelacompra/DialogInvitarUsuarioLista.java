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

public class DialogInvitarUsuarioLista extends AppCompatDialogFragment {

    private EditText email_usuario;

    private DialogInvitarUsuarioLista.DialogInvitarUsuarioListaListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_invitar_usuario_lista, null);

        email_usuario = view.findViewById(R.id.edt_nom_lista);

        email_usuario.setTextColor(getResources().getColor(R.color.gris));

        builder.setView(view)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Invitar a usuario", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email_usu = email_usuario.getText().toString();

                if (!email_usu.isEmpty()){
                        listener.cogerEmailInvitarUsuarioLista(email_usu);
                }

            }
        });

        email_usuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String cojoTextoActual = String.valueOf(email_usuario.getText());
                if (hasFocus) {
                    if (cojoTextoActual.equals("E-mail usuario")){
                        email_usuario.setText("");
                        email_usuario.setTextColor(getResources().getColor(R.color.negro));
                    }
                } else {
                    if (cojoTextoActual.equals("")){
                        email_usuario.setText("E-mail usuario");
                        email_usuario.setTextColor(getResources().getColor(R.color.gris));
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
            listener = (DialogInvitarUsuarioLista.DialogInvitarUsuarioListaListener) context;
        }catch (Exception e){
            Toast.makeText(getContext(), "Error al crear dialog invitar usuario:\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface DialogInvitarUsuarioListaListener{
        void cogerEmailInvitarUsuarioLista(String email_usuario);
    }
}
