package com.example.proyecto_iot.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.PagPrincipalSuperAdmin;
import com.example.proyecto_iot.administradorHotel.PagPrincipalAdmin;
import com.example.proyecto_iot.administradorHotel.RegistroPrimeraVez;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    GoogleSignInClient googleSignInClient;

    private static final String TAG = "LoginActivity";

    // ActivityResultLauncher para Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                } else {
                    hideLoadingDialog();
                    Toast.makeText(this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Necesitas agregar esto a strings.xml
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Botones y campos
        TextView textCrearCuenta = findViewById(R.id.registro_nuevo_usuario);
        TextView txtForgotPassword = findViewById(R.id.forgotPassID);
        Button btnIngresar = findViewById(R.id.btnIngresar);
        Button btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn); // Nuevo botón
        TextInputEditText editTextUsuario = findViewById(R.id.editTextUsuario);
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);

        // Navegar a crear cuenta
        textCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Navegar a recuperar contraseña
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Botón ingresar con email/password
        btnIngresar.setOnClickListener(v -> {
            String correo = editTextUsuario.getText() != null ? editTextUsuario.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";

            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            signInWithEmailPassword(correo, password);
        });

        // Botón Google Sign-In
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        showLoadingDialog();

        // Primero cerrar sesión de Google para forzar la selección de cuenta
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Después de cerrar sesión, iniciar el proceso de login
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Google sign in succeeded");
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            hideLoadingDialog();
            Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkUserInDatabase(user, true); // true indica que es login con Google
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        hideLoadingDialog();
                        Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithEmailPassword(String correo, String password) {
        showLoadingDialog();

        auth.signInWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        checkUserInDatabase(user, false); // false indica que es login normal
                    } else {
                        hideLoadingDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserInDatabase(FirebaseUser user, boolean isGoogleSignIn) {
        db.collection("usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    hideLoadingDialog();

                    if (documentSnapshot.exists()) {
                        // Usuario existe, proceder con el login normal
                        redirectUserByRole(documentSnapshot);
                    } else if (isGoogleSignIn) {
                        // Usuario no existe y es Google Sign-In, crear usuario automáticamente como Cliente
                        createGoogleUser(user);
                    } else {
                        // Usuario no existe y es login normal
                        Toast.makeText(this, "Datos de usuario no encontrados", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    Toast.makeText(this, "Error al cargar sesión", Toast.LENGTH_SHORT).show();
                });
    }

    private void createGoogleUser(FirebaseUser user) {
        showLoadingDialog();

        // Crear usuario automáticamente como Cliente
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", user.getDisplayName() != null ? user.getDisplayName() : "Usuario Google");
        userData.put("correo", user.getEmail());
        userData.put("idRol", "Cliente");
        userData.put("fechaCreacion", System.currentTimeMillis());
        userData.put("loginMethod", "google");

        db.collection("usuarios").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    hideLoadingDialog();
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();

                    // Redirigir a la pantalla de cliente
                    Intent intent = new Intent(LoginActivity.this, ClienteBusquedaActivity.class);
                    intent.putExtra("idUsuario", user.getUid());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                });
    }

    private void redirectUserByRole(com.google.firebase.firestore.DocumentSnapshot documentSnapshot) {
        String rol = documentSnapshot.getString("idRol");

        if ("Cliente".equalsIgnoreCase(rol)) {
            String idUsuario = documentSnapshot.getId();
            Intent intent = new Intent(LoginActivity.this, ClienteBusquedaActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            startActivity(intent);
            finish();
        } else if ("Administrador".equals(rol)) {
            String idUsuario = documentSnapshot.getId();
            String idHotel = documentSnapshot.getString("idHotel");

            if (idHotel == null || idHotel.isEmpty()) {
                Intent intent = new Intent(LoginActivity.this, RegistroPrimeraVez.class);
                intent.putExtra("idUsuario", idUsuario);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, PagPrincipalAdmin.class);
                intent.putExtra("idUsuario", idUsuario);
                intent.putExtra("idHotel", idHotel);
                startActivity(intent);
            }
            finish();
        } else if ("Taxista".equals(rol)) {
            String idUsuario = documentSnapshot.getId();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            startActivity(intent);
            finish();
        } else if ("SuperAdmin".equals(rol)) {
            Intent intent = new Intent(LoginActivity.this, PagPrincipalSuperAdmin.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Rol no permitido", Toast.LENGTH_SHORT).show();
            auth.signOut();
        }
    }

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // Método público para cerrar sesión completa (Firebase + Google)
    public static void signOutCompletely(AppCompatActivity activity) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, gso);

        // Cerrar sesión de Firebase
        auth.signOut();

        // Cerrar sesión de Google y revocar acceso
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(revokeTask -> {
                // Redirigir al login
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si hay un usuario ya logueado
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            checkUserInDatabase(currentUser, false);
        }
    }
}