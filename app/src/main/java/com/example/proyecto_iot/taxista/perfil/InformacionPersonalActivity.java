package com.example.proyecto_iot.taxista.perfil;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        int[] ids = {
                R.id.campoNombre,
                R.id.campoDNI,
                R.id.campoTelefono,
                R.id.campoCorreo,
                R.id.campoNacimiento,
                R.id.campoDomicilio
        };

        for (int id : ids) {
            View campo = findViewById(id);
            if (campo == null || campo.getTag() == null) continue;

            String tag = campo.getTag().toString();
            String[] partes = tag.split("\\|");

            TextView titulo = campo.findViewById(R.id.tvTitulo);
            TextView contenido = campo.findViewById(R.id.tvContenido);

            if (partes.length >= 2) {
                titulo.setText(partes[0]);
                contenido.setText(partes[1]);
            }
        }

        cargarDatos();
    }


    private void cargarDatos() {
        setCampo(R.id.campoNombre, "Nombre legal", "Roberto Tafur");
        setCampo(R.id.campoDNI, "Documento de Identidad", "71986247");
        setCampo(R.id.campoTelefono, "Número telefónico", "945 854 123");
        setCampo(R.id.campoCorreo, "Correo electrónico", "a20210535@pucp.edu.pe");
        setCampo(R.id.campoNacimiento, "Fecha de nacimiento", "31/05/04");
        setCampo(R.id.campoDomicilio, "Domicilio", "Las Cucardas 232");
    }

    private void setCampo(int viewId, String tituloText, String contenidoText) {
        View campo = findViewById(viewId);
        if (campo == null) return;

        TextView titulo = campo.findViewById(R.id.tvTitulo);
        TextView contenido = campo.findViewById(R.id.tvContenido);

        titulo.setText(tituloText);
        contenido.setText(contenidoText);

        TextView btnEditar = campo.findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(v -> {
            new EditarCampoDialogFragment(
                    tituloText,
                    contenido.getText().toString(),
                    nuevoTexto -> contenido.setText(nuevoTexto)
            ).show(getSupportFragmentManager(), "EditarCampo");
        });
    }




}