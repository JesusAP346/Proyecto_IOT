package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.login.RegisterFragment;

public class RegistroTaxistaActivity extends AppCompatActivity {

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

        loadFragment(steps[currentStep]);

        findViewById(R.id.btnSiguiente).setOnClickListener(v -> {
            if (currentStep < steps.length - 1) {
                currentStep++;
                loadFragment(steps[currentStep]);
                updateButtonText();
            }
        });

        findViewById(R.id.btnAtras).setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                loadFragment(steps[currentStep]);
                updateButtonText();
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
}
