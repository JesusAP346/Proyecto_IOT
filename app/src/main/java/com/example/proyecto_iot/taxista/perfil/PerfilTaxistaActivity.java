package com.example.proyecto_iot.taxista.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PerfilTaxistaActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_taxista);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView btnEditar = findViewById(R.id.btnEditar);

        btnEditar.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(PerfilTaxistaActivity.this);
            View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_taxista, null);

            //ImageView btnCerrar = sheetView.findViewById(R.id.btnCerrar);
            TextView btnCambiar = sheetView.findViewById(R.id.btnCambiarFoto);
            imageView = sheetView.findViewById(R.id.ivFoto);

            Button btnListo = sheetView.findViewById(R.id.btnListo);

            /*btnCerrar.setOnClickListener(e -> {
                dialog.dismiss();
            });*/

            btnCambiar.setOnClickListener(e -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 100);  // 100 es un requestCode arbitrario
            });


            btnListo.setOnClickListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                // Aquí podrías guardar cambios si fuese necesario
            });

            dialog.setContentView(sheetView);
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Aquí puedes mostrarla en un ImageView por ejemplo:
            imageView.setImageURI(selectedImageUri);
        }
    }

}
