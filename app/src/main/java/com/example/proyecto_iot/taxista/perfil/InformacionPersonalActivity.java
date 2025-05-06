package com.example.proyecto_iot.taxista.perfil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class InformacionPersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_personal);


        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //  Esto cierra la actividad y regresa a la anterior
            }
        });




        configurarCampos(); // para cargar datos desde los tags
    }

    private void configurarCampos() {
        int[] campos = {
                R.id.campoNombre,
                // agrega aquí los demás si les das IDs únicos
        };

        for (int id : campos) {
            View campo = findViewById(id);  // <- no se necesita pasar view
            if (campo == null || campo.getTag() == null) continue;

            String tag = (String) campo.getTag(); // Ej: "Nombre legal|Roberto Tafur"
            String[] partes = tag.split("\\|");

            TextView titulo = campo.findViewById(R.id.tvTitulo);
            TextView contenido = campo.findViewById(R.id.tvContenido);

            if (partes.length >= 2) {
                titulo.setText(partes[0]);
                contenido.setText(partes[1]);
            }
        }
    }


}