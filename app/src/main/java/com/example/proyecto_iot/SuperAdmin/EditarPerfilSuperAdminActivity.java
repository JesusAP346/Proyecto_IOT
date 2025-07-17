package com.example.proyecto_iot.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EditarPerfilSuperAdminActivity extends AppCompatActivity {

    private LinearLayout itemNombre, itemDNI, itemLocalidad, itemNumero, itemContrasena;
    private ImageView itemFoto, btnBack;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_super_admin); // Usa tu layout XML principal

        // Vincular views
        itemNombre = findViewById(R.id.itemNombre);
        itemDNI = findViewById(R.id.itemDni);
        itemLocalidad = findViewById(R.id.itemCiudad);
        itemNumero = findViewById(R.id.itemTelefono);
        itemFoto = findViewById(R.id.ivEditarFotoPerfil); // IMPORTANTE: debes asegurarte que esta id corresponda a la opción de editar foto
        btnCancelar = findViewById(R.id.btnCancelar);
        btnBack = findViewById(R.id.btnBack);

        // Listener para editar nombre completo
        itemNombre.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilSuperAdminActivity.this, RegisterActivityNombresSA.class);
            startActivity(intent);
        });

        // Listener para editar número
        itemNumero.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilSuperAdminActivity.this, RegisterActivityNumeroSA.class);
            startActivity(intent);
        });

        // Listener para editar localidad
        itemLocalidad.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilSuperAdminActivity.this, RegisterActivityLocalidadSA.class);
            startActivity(intent);
        });

        // Listener para editar DNI
        itemDNI.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilSuperAdminActivity.this, RegisterActivityDNISA.class);
            startActivity(intent);
        });

        // Listener para editar foto de perfil
        itemFoto.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilSuperAdminActivity.this, RegisterFotoPerfilSAActivity.class);
            startActivity(intent);
        });

        // Listener para cancelar
        btnCancelar.setOnClickListener(v -> finish());
        // Listener para la flechita superior izquierda
        btnBack.setOnClickListener(v -> finish());
    }
}
