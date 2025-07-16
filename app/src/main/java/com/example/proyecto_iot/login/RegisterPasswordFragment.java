package com.example.proyecto_iot.login;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseFirestore db;
    private Uri fotoUri; // Para almacenar la URI de la foto
    private AwsService awsService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterPasswordFragment newInstance(String param1, String param2) {
        RegisterPasswordFragment fragment = new RegisterPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey("fotoUri")) {
            String uriStr = args.getString("fotoUri");
            fotoUri = Uri.parse(uriStr);
            android.util.Log.d("Registro", "URI recibida en RegisterPasswordFragment: " + fotoUri.toString());
        } else {
            android.util.Log.d("Registro", "No se recibió ninguna URI en RegisterPasswordFragment.");
        }

        // Inicializar Retrofit para AWS
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mm5k8l79xd.execute-api.us-west-2.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        awsService = retrofit.create(AwsService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_password, container, false);

        TextInputLayout inputPassword = view.findViewById(R.id.inputPassword);
        TextInputEditText editPassword = view.findViewById(R.id.editPassword);
        UsuarioClienteViewModel viewModel = new ViewModelProvider(requireActivity()).get(UsuarioClienteViewModel.class);

        TextInputLayout inputRepeatPassword = view.findViewById(R.id.inputRepeatPassword);
        TextInputEditText editRepeatPassword = view.findViewById(R.id.editRepeatPassword);

        Button botonEnviar = view.findViewById(R.id.botonEnviar);
        Button botonRegresar = view.findViewById(R.id.botonRegresar);

        botonEnviar.setOnClickListener(v -> {
            String password = editPassword.getText() != null ? editPassword.getText().toString() : "";
            String repeatPassword = editRepeatPassword.getText() != null ? editRepeatPassword.getText().toString() : "";

            boolean isValid = true;

            // Validación de contraseña
            if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
                inputPassword.setError("Debe tener al menos 8 caracteres, una mayúscula y un número");
                isValid = false;
            } else {
                inputPassword.setError(null);
            }

            // Validación de repetición
            if (!password.equals(repeatPassword)) {
                inputRepeatPassword.setError("Las contraseñas no coinciden");
                isValid = false;
            } else {
                inputRepeatPassword.setError(null);
            }

            if (isValid) {
                Usuario usuario = viewModel.getUsuarioCliente().getValue();

                if (usuario != null) {
                    // Deshabilitar el botón mientras se procesa
                    botonEnviar.setEnabled(false);
                    botonEnviar.setText("Registrando...");

                    // Si hay una foto, subirla primero
                    if (fotoUri != null) {
                        subirFotoAWS(usuario, password);
                    } else {
                        // Si no hay foto, proceder directamente con el registro
                        registrarUsuario(usuario, password);
                    }
                } else {
                    Log.e("Registro", "El objeto usuario es null");
                }
            }
        });

        botonRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void subirFotoAWS(Usuario usuario, String password) {
        try {
            // Convertir URI a File
            File file = uriToFile(fotoUri);

            // Crear RequestBody desde el archivo
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            // Crear MultipartBody.Part con el archivo
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // Realizar la llamada a AWS
            Call<UploadResponse> call = awsService.subirImagen(body);

            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Obtener la URL de la imagen subida
                        String imageUrl = response.body().getUrl(); // Asumiendo que UploadResponse tiene un método getUrl()

                        // Asignar la URL al usuario
                        usuario.setUrlFotoPerfil(imageUrl);

                        // Proceder con el registro
                        registrarUsuario(usuario, password);

                        Log.d("AWS", "Imagen subida exitosamente: " + imageUrl);
                    } else {
                        Log.e("AWS", "Error al subir imagen: " + response.message());
                        Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();

                        // Rehabilitar el botón
                        Button botonEnviar = getView().findViewById(R.id.botonEnviar);
                        botonEnviar.setEnabled(true);
                        botonEnviar.setText("Enviar");
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.e("AWS", "Error en la llamada a AWS", t);
                    Toast.makeText(getContext(), "Error de conexión al subir imagen", Toast.LENGTH_SHORT).show();

                    // Rehabilitar el botón
                    Button botonEnviar = getView().findViewById(R.id.botonEnviar);
                    botonEnviar.setEnabled(true);
                    botonEnviar.setText("Enviar");
                }
            });

        } catch (Exception e) {
            Log.e("AWS", "Error al preparar archivo para subir", e);
            Toast.makeText(getContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();

            // Rehabilitar el botón
            Button botonEnviar = getView().findViewById(R.id.botonEnviar);
            botonEnviar.setEnabled(true);
            botonEnviar.setText("Enviar");
        }
    }

    private File uriToFile(Uri uri) throws Exception {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        File tempFile = new File(getContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    private void registrarUsuario(Usuario usuario, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuario.setIdRol("Cliente");

        auth.createUserWithEmailAndPassword(usuario.getEmail(), password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    usuario.setId(uid);

                    db.collection("usuarios").document(uid).set(usuario)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

                                LogSA log = new LogSA(
                                        "NuevoUsuario",
                                        "Nuevo Usuario",
                                        "El usuario " + usuario.getNombres() + " se registró en la App",
                                        usuario.getNombres() + usuario.getApellidos(),
                                        "Usuario",
                                        uid,
                                        usuario.getNombres() + " " + usuario.getApellidos(),
                                        new Date()
                                );

                                auth.signOut();
                                db.collection("logs").add(log);

                                requireActivity().finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error al guardar perfil", e);
                                Toast.makeText(getContext(), "Error al guardar perfil", Toast.LENGTH_SHORT).show();

                                // Rehabilitar el botón
                                Button botonEnviar = getView().findViewById(R.id.botonEnviar);
                                botonEnviar.setEnabled(true);
                                botonEnviar.setText("Enviar");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Auth", "Error al registrar en Auth", e);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Rehabilitar el botón
                    Button botonEnviar = getView().findViewById(R.id.botonEnviar);
                    botonEnviar.setEnabled(true);
                    botonEnviar.setText("Enviar");
                });
    }
}