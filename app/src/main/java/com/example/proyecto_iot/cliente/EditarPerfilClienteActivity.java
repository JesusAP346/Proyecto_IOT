package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EditarPerfilClienteActivity extends AppCompatActivity {

    private LinearLayout itemNombre, itemDNI, itemLocalidad, itemNumero;
    private ImageView itemFoto, btnBack;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_cliente);

        // Vincular views
        itemNombre = findViewById(R.id.itemNombre);
        itemDNI = findViewById(R.id.itemDni);
        itemLocalidad = findViewById(R.id.itemCiudad);
        itemNumero = findViewById(R.id.itemTelefono);
        itemFoto = findViewById(R.id.ivEditarFotoPerfil);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnBack = findViewById(R.id.btnBack);

        // Listener para editar nombre completo
        itemNombre.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilClienteActivity.this, RegisterActivityNombresCliente.class);
            startActivity(intent);
        });

        // Listener para editar número de teléfono
        itemNumero.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilClienteActivity.this, RegisterActivityNumeroCliente.class);
            startActivity(intent);
        });

        // Listener para editar localidad / dirección
        itemLocalidad.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilClienteActivity.this, RegisterActivityLocalidadCliente.class);
            startActivity(intent);
        });
//
        // Listener para editar DNI
        itemDNI.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilClienteActivity.this, RegisterActivityDniCliente.class);
            startActivity(intent);
        });
//
//        // Listener para editar foto de perfil
        itemFoto.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilClienteActivity.this, RegisterFotoPerfilClienteActivity.class);
            startActivity(intent);
        });

        // Botón Cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Botón Back (flechita)
        btnBack.setOnClickListener(v -> finish());
    }
}
