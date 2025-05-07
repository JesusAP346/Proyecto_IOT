package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;




public class RegistroInformacionPersonalFragment extends Fragment {
    private static final int REQUEST_GALLERY = 100;
    private ImageView imageViewPerfil;


    public RegistroInformacionPersonalFragment() {
        super(R.layout.fragment_registro_informacion_personal);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_informacion_personal, container, false);

        imageViewPerfil = view.findViewById(R.id.imageView4);

        imageViewPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        });

        // Código del DatePicker...
        TextInputEditText etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        etFechaNacimiento.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(requireContext(),
                    (view1, y, m, d) -> {
                        String fecha = String.format("%02d/%02d/%04d", d, m + 1, y);
                        etFechaNacimiento.setText(fecha);
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            dp.show();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageViewPerfil.setImageURI(selectedImageUri);
        }
    }



}