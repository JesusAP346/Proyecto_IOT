package com.example.proyecto_iot.cliente.busqueda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import androidx.fragment.app.DialogFragment;

import com.example.proyecto_iot.R;

public class FechaDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fecha, null);

        // Aquí puedes manejar tus controles del calendario
        CalendarView calendarView = view.findViewById(R.id.calendarView);

        builder.setView(view)
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    // Aquí puedes enviar la fecha seleccionada al fragmento o actividad
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}

