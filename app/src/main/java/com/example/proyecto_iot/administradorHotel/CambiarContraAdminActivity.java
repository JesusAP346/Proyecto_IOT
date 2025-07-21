package com.example.proyecto_iot.administradorHotel;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityCambiarContraAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CambiarContraAdminActivity extends AppCompatActivity {

    private ActivityCambiarContraAdminBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean passwordVisible1 = false;
    private boolean passwordVisible2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCambiarContraAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding.eyePassword.setOnClickListener(v -> togglePasswordVisibility(binding.inputPassword, binding.eyePassword, 1));
        binding.eyeConfirmarPassword.setOnClickListener(v -> togglePasswordVisibility(binding.inputConfirmarPassword, binding.eyeConfirmarPassword, 2));

        binding.btnCambiarContrasena.setOnClickListener(v -> validarYCambiarContra());
    }

    private void togglePasswordVisibility(android.widget.EditText editText, android.widget.ImageView icon, int field) {
        boolean isVisible = (field == 1) ? passwordVisible1 : passwordVisible2;
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye);
        }
        editText.setTextSize(17); // ← Tamaño constante
        editText.setSelection(editText.getText().length());
        if (field == 1) passwordVisible1 = !passwordVisible1;
        else passwordVisible2 = !passwordVisible2;
    }

    private void validarYCambiarContra() {
        binding.errorPassword.setVisibility(View.GONE);
        binding.errorConfirmarPassword.setVisibility(View.GONE);

        String nueva = binding.inputPassword.getText().toString().trim();
        String confirmar = binding.inputConfirmarPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nueva)) {
            binding.errorPassword.setText("Ingrese una nueva contraseña");
            binding.errorPassword.setVisibility(View.VISIBLE);
            return;
        }

        if (nueva.length() < 6) {
            binding.errorPassword.setText("Mínimo 6 caracteres");
            binding.errorPassword.setVisibility(View.VISIBLE);
            return;
        }

        if (!nueva.equals(confirmar)) {
            binding.errorConfirmarPassword.setText("Las contraseñas no coinciden");
            binding.errorConfirmarPassword.setVisibility(View.VISIBLE);
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Sesión no iniciada", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.updatePassword(nueva)
                .addOnSuccessListener(unused -> guardarCambioDeContraYRedirigir())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardarCambioDeContraYRedirigir() {
        String uid = currentUser.getUid();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("usuarios").document(uid);
        ref.update("contraCambiada", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CambiarContraAdminActivity.this, RegistroPrimeraVez.class);
                    intent.putExtra("idUsuario", uid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
}