package com.example.proyecto_iot.taxista.perfil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.databinding.ActivityInformacionPersonalBinding;
import com.example.proyecto_iot.databinding.ItemDatoEditable2Binding;

import java.util.Calendar;
import java.util.Locale;

public class InformacionPersonalActivity extends AppCompatActivity {

    private ActivityInformacionPersonalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformacionPersonalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnguardar.setOnClickListener(v -> finish());

        cargarDatos(); // mostrar texto por defecto
        configurarCampoFechaNacimiento(); //

    }

    private void cargarDatos() {
        setCampo(binding.layoutNombre, "Nombre legal", "Roberto Tafur");
        setCampo(binding.campoDNI, "Documento de Identidad", "71986247");
        setCampo(binding.campoTelefono, "Número telefónico", "945 854 123");
        setCampo(binding.campoCorreo, "Correo electrónico", "a20210535@pucp.edu.pe");
        //setCampo(binding.campoNacimiento, "Fecha de nacimiento", "31/05/04");
        setCampo(binding.campoDomicilio, "Domicilio", "Pueblo Libre 232");
    }

    private void setCampo(ItemDatoEditable2Binding campoBinding, String tituloText, String contenidoText) {
        campoBinding.layoutCampo.setHint(tituloText);
        campoBinding.etContenido.setText(contenidoText);


    }
    private void configurarCampoFechaNacimiento() {
        binding.etFechaNacimiento.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        binding.etFechaNacimiento.setText(fecha);
                    },
                    anio, mes, dia
            );
            datePicker.show();
        });

        // Valor inicial
        binding.etFechaNacimiento.setText("31/05/04");
    }

}
