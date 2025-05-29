package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;


public class InfoPersonal extends AppCompatActivity {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_personal); // Cambia por el nombre real

        // Botón de regreso
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Cierra esta Activity y vuelve a la anterior
        });

        // Botones editar - ejemplo para nombre
        TextView btnEditarNombre = findViewById(R.id.btn_editar_nombre);
        btnEditarNombre.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Nombre", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar nombre
        });

        // Editar Documento
        TextView btnEditarDocumento = findViewById(R.id.btn_editar_documento);
        btnEditarDocumento.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Documento", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar documento
        });

        // Editar Teléfono
        TextView btnEditarTelefono = findViewById(R.id.btn_editar_telefono);
        btnEditarTelefono.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Teléfono", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar teléfono
        });

        // Editar Fecha de nacimiento
        TextView btnEditarFecha = findViewById(R.id.btn_editar_fecha);
        btnEditarFecha.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Fecha de nacimiento", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar fecha
        });

        // Editar Domicilio
        TextView btnEditarDomicilio = findViewById(R.id.btn_editar_domicilio);
        btnEditarDomicilio.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Domicilio", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar domicilio
        });

        // Editar Correo
        TextView btnEditarCorreo = findViewById(R.id.btn_editar_correo);
        btnEditarCorreo.setOnClickListener(v -> {
            Toast.makeText(this, "Editar Correo", Toast.LENGTH_SHORT).show();
            // Aquí abres diálogo o pantalla para editar correo
        });
    }
}