package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.login.RegisterFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class RegistroTaxistaActivity extends AppCompatActivity {
    private TextView txtPaso;
    private ProgressBar progressBar;

    private int currentStep = 0;
    private final Fragment[] steps = new Fragment[]{
            new RegistroInformacionPersonalFragment(),
            new RegistroInformacionPlacaFragment(),
            new RegistroInformacionAutoFragment()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_taxista);
        txtPaso = findViewById(R.id.txtPaso);
        progressBar = findViewById(R.id.progressBar);
        updateUI();



        loadFragment(steps[currentStep]);

        findViewById(R.id.btnSiguiente).setOnClickListener(v -> {
            if (currentStep < steps.length - 1) {
                currentStep++;
                loadFragment(steps[currentStep]);
                updateUI();
            }
        });

        findViewById(R.id.btnAtras).setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                loadFragment(steps[currentStep]);
                updateUI();
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }

    private void updateButtonText() {
        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        if (currentStep == steps.length - 1) {
            btnSiguiente.setText("Finalizar");
        } else {
            btnSiguiente.setText("Siguiente");
        }
    }

    private void updateUI() {
        ExtendedFloatingActionButton btnSiguiente = findViewById(R.id.btnSiguiente);
        if (currentStep == steps.length - 1) {
            btnSiguiente.setText("Finalizar");
        } else {
            btnSiguiente.setText("Siguiente");
        }
        txtPaso.setText((currentStep + 1) + " de " + steps.length);
        progressBar.setProgress(currentStep + 1);
    }

}
