package com.example.proyecto_iot.taxista.perfil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.databinding.ActivityInformacionPersonalBinding;
import com.example.proyecto_iot.databinding.ItemDatoEditable2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InformacionPersonalActivity extends AppCompatActivity {

    private ActivityInformacionPersonalBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformacionPersonalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());

        configurarCampoFechaNacimiento();

        binding.btnguardar.setOnClickListener(v -> guardarCambios());

        cargarDatosDesdeFirestore();


    }

    private void cargarDatosDesdeFirestore() {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    setCampo(binding.layoutNombre, "Nombre legal", documentSnapshot.getString("nombres") + " " + documentSnapshot.getString("apellidos"));
                    setCampo(binding.campoDNI, "Documento de Identidad", documentSnapshot.getString("numDocumento"));
                    setCampo(binding.campoTelefono, "Número telefónico", documentSnapshot.getString("numCelular"));
                    setCampo(binding.campoCorreo, "Correo electrónico", documentSnapshot.getString("email"));
                    setCampo(binding.campoDomicilio, "Domicilio", documentSnapshot.getString("direccion"));
                    binding.etFechaNacimiento.setText(documentSnapshot.getString("fechaNacimiento"));
                    binding.campoCorreo.etContenido.setEnabled(false);
                    binding.campoDNI.etContenido.setEnabled(false);
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void guardarCambios() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        String[] nombreCompleto = binding.layoutNombre.etContenido.getText().toString().trim().split(" ", 2);
        String nombres = nombreCompleto.length > 0 ? nombreCompleto[0] : "";
        String apellidos = nombreCompleto.length > 1 ? nombreCompleto[1] : "";

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombres", nombres);
        updates.put("apellidos", apellidos);
        updates.put("numDocumento", binding.campoDNI.etContenido.getText().toString().trim());
        updates.put("numCelular", binding.campoTelefono.etContenido.getText().toString().trim());
        updates.put("email", binding.campoCorreo.etContenido.getText().toString().trim());
        updates.put("fechaNacimiento", binding.etFechaNacimiento.getText().toString().trim());
        updates.put("direccion", binding.campoDomicilio.etContenido.getText().toString().trim());

        db.collection("usuarios").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }

    private void setCampo(ItemDatoEditable2Binding campoBinding, String tituloText, String contenidoText) {
        campoBinding.layoutCampo.setHint(tituloText);
        campoBinding.etContenido.setText(contenidoText != null ? contenidoText : "");
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
    }
}
