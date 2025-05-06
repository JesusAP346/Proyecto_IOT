package com.example.proyecto_iot.taxista.perfil;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_iot.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditarCampoDialogFragment extends DialogFragment {

    public interface OnCampoEditadoListener {
        void onCampoEditado(String nuevoTexto);
    }

    private String titulo;
    private String contenidoActual;
    private OnCampoEditadoListener listener;

    public EditarCampoDialogFragment(String titulo, String contenidoActual, OnCampoEditadoListener listener) {
        this.titulo = titulo;
        this.contenidoActual = contenidoActual;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_editar_campo, null);
        EditText editText = view.findViewById(R.id.editTextCampo);
        editText.setText(contenidoActual);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(titulo)
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoTexto = editText.getText().toString().trim();
                    if (!nuevoTexto.isEmpty()) {
                        listener.onCampoEditado(nuevoTexto);
                    }
                })
                .create();
    }
}
