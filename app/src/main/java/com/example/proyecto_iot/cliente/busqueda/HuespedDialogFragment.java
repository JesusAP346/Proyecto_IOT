package com.example.proyecto_iot.cliente.busqueda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.proyecto_iot.R;

public class HuespedDialogFragment extends DialogFragment {

    private int adultos = 2;
    private int ninos = 0;
    private int habitaciones = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_huespedes, null);

        // Puedes agregar aquí los botones para sumar/restar adultos, niños y habitaciones
        Button btnMasAdultos = view.findViewById(R.id.btnMasAdultos);
        Button btnMenosAdultos = view.findViewById(R.id.btnMenosAdultos);
        TextView txtAdultos = view.findViewById(R.id.txtAdultos);

        btnMasAdultos.setOnClickListener(v -> {
            adultos++;
            txtAdultos.setText(String.valueOf(adultos));
        });

        btnMenosAdultos.setOnClickListener(v -> {
            if (adultos > 1) adultos--;
            txtAdultos.setText(String.valueOf(adultos));
        });

        builder.setView(view)
                .setPositiveButton("Confirmar", (dialog, id) -> {
                    // Puedes enviar los valores seleccionados al fragmento
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}

