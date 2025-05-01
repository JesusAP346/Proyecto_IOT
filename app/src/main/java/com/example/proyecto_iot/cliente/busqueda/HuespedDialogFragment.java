package com.example.proyecto_iot.cliente.busqueda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_iot.R;

public class HuespedDialogFragment extends DialogFragment {

    public interface OnHuespedesSeleccionadosListener {
        void onHuespedesSeleccionados(int adultos, int ninos, int habitaciones);
    }

    private OnHuespedesSeleccionadosListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnHuespedesSeleccionadosListener) {
            listener = (OnHuespedesSeleccionadosListener) getParentFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona huéspedes")
                .setMessage("Simulando selección")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    int adultos = 2;
                    int ninos = 1;
                    int habitaciones = 1;
                    if (listener != null) {
                        listener.onHuespedesSeleccionados(adultos, ninos, habitaciones);
                    }
                })
                .setNegativeButton("Cancelar", null);
        return builder.create();
    }
}


