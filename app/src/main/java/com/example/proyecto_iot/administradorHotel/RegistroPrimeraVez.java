package com.example.proyecto_iot.administradorHotel;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.FotoItem;
import com.example.proyecto_iot.administradorHotel.entity.Hotel;
import com.example.proyecto_iot.administradorHotel.entity.ServicioHotel;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.databinding.ActivityRegistroPrimeraVezBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroPrimeraVez extends AppCompatActivity {

    ActivityRegistroPrimeraVezBinding binding;

    FirebaseFirestore db;
    FirebaseUser currentUser;
    private boolean isMenuVisible = false;
    private final List<String> referencias = new ArrayList<>();

    // Lista unificada para todas las fotos
    private List<FotoItem> todasLasFotos = new ArrayList<>();
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> archivoLauncher;
    private boolean guardandoServicio = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroPrimeraVezBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        inicializarLaunchers();
        configurarEventos();

        // ¡IMPORTANTE! Agregar esta línea para configurar las validaciones en tiempo real
        configurarValidacionTiempoReal();

        // Retroceder al presionar el botón de la flecha
        binding.btnRegistrarHotel.setOnClickListener(v -> finish());

        binding.btnRegistrarHotel.setOnClickListener(v -> {

            // Prevenir múltiples clics
            if (guardandoServicio) {
                return;
            }
            // USAR LAS VALIDACIONES QUE YA CREASTE
            if (!validarCampos()) {
                return; // Si las validaciones fallan, no continuar
            }

            // Si llegamos aquí, todos los campos son válidos
            String nombre = binding.inputNombre.getText().toString().trim();
            String direccion = binding.inputDireccion.getText().toString().trim();

            guardarHotel(nombre, direccion, referencias);
        });

        binding.btnAgregarReferencia.setOnClickListener(v -> {
            String texto = binding.inputReferencia.getText().toString().trim();
            if (!texto.isEmpty() && !referencias.contains(texto)) {
                referencias.add(texto);
                agregarChipReferencia(texto);
                binding.inputReferencia.setText("");
            }
        });

    }

    ///////////////////////TODO ESTO ES PA LAS FOTOS///////////////////////////////////7
    private void inicializarLaunchers() {
        // Launcher para galería
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                agregarFoto(new FotoItem(imageUri));
                            }
                        }
                        ocultarMenu();
                    }
                });

        // Launcher para cámara
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                if (imageBitmap != null) {
                                    agregarFoto(new FotoItem(imageBitmap));
                                }
                            }
                        }
                        ocultarMenu();
                    }
                });

        // Launcher para archivos
        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri fileUri = result.getData().getData();
                            if (fileUri != null) {
                                agregarFoto(new FotoItem(fileUri));
                            }
                        }
                        ocultarMenu();
                    }
                });
    }

    //Para mostrar/ocultar el menu de para la selccion de fotos
    private void configurarEventos() {
        // Click en el botón principal para mostrar/ocultar menú
        binding.btnAgregarFoto.setOnClickListener(v -> {
            if (isMenuVisible) {
                ocultarMenu();
            } else {
                mostrarMenu();
            }
        });

        // Click en el fondo para cerrar el menú
        binding.overlayBackground.setOnClickListener(v -> ocultarMenu());

        // Opciones del menú
        binding.opcionGaleria.setOnClickListener(v -> abrirGaleria());
        binding.opcionTomarFoto.setOnClickListener(v -> abrirCamara());
        binding.opcionSeleccionarArchivo.setOnClickListener(v -> abrirSelectorArchivos());
    }


    // Menú de Selección de Fotos //////////////////////////////////////////////////7

    private void mostrarMenu() {
        // Esperar a que el layout esté completamente dibujado
        binding.btnAgregarFoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.btnAgregarFoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                posicionarMenuEncima();
            }
        });

        // Si el layout ya está dibujado, posicionar inmediatamente
        if (binding.btnAgregarFoto.getHeight() > 0) {
            posicionarMenuEncima();
        }
    }

    private void ocultarMenu() {
        if (isMenuVisible) {
            // Animación de salida
            binding.menuOpciones.animate()
                    .alpha(0f)
                    .scaleY(0.8f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        binding.overlayBackground.setVisibility(View.GONE);
                        binding.menuOpciones.setVisibility(View.GONE);
                    })
                    .start();

            isMenuVisible = false;
        }
    }

    //Menú de opciones para la seleccion  de fotos
    private void posicionarMenuEncima() {
        // Obtener las dimensiones de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calcular la posición del botón en la pantalla
        int[] buttonLocation = new int[2];
        binding.btnAgregarFoto.getLocationOnScreen(buttonLocation);

        // Medir el menú para obtener su altura
        binding.menuOpciones.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int menuHeight = binding.menuOpciones.getMeasuredHeight();

        // Calcular la posición Y del menú (por encima del botón)
        binding.btnAgregarFoto.getLocationOnScreen(buttonLocation);
        int buttonTopY = buttonLocation[1];
        int statusBarHeight = getStatusBarHeight();
        int menuTopY = buttonTopY - menuHeight;
        // Asegurar que el menú no se salga de la pantalla por arriba
        if (menuTopY < statusBarHeight + 50) { // 50dp de margen mínimo desde el top
            menuTopY = statusBarHeight + 50;
        }

        // Configurar la posición del menú
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.menuOpciones.getLayoutParams();
        params.topMargin = menuTopY;
        binding.menuOpciones.setLayoutParams(params);

        // Mostrar el overlay y el menú con animación suave
        binding.overlayBackground.setVisibility(View.VISIBLE);
        binding.menuOpciones.setVisibility(View.VISIBLE);
        binding.menuOpciones.setAlpha(0f);
        binding.menuOpciones.setScaleY(0.8f);
        binding.menuOpciones.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        isMenuVisible = true;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //Abrir Galería, Cámara y Archivos //////////////////////////////////////////////
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                camaraLauncher.launch(intent);
            } else {
                Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirSelectorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        archivoLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////Lógica para Agregar, eliminar y mostrar (previsualizar) las fotos/////////////////////

    // Metodo unificado para agregar fotos
    private void agregarFoto(FotoItem fotoItem) {
        todasLasFotos.add(fotoItem);
        crearVistaPreviaFoto(fotoItem, todasLasFotos.size() - 1);
        binding.containerFotos.setVisibility(View.VISIBLE);
    }

    // Metodo unificado para crear vista previa
    private void crearVistaPreviaFoto(FotoItem fotoItem, int index) {
        // Crear CardView como contenedor principal
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (50 * getResources().getDisplayMetrics().density));
        cardParams.setMargins(0, 2, 0, 2);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2);
        cardView.setRadius(6);

        // Asignar un tag con el índice para identificar la vista
        cardView.setTag(index);

        // Crear LinearLayout horizontal dentro del CardView
        LinearLayout container = new LinearLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.setPadding(6, 6, 6, 6);

        // ImageView para mostrar la foto
        ImageView imageView = new ImageView(this);
        int imageSize = (int) (38 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, 8, 0);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.image_border));

        // Cargar imagen según el tipo
        if (fotoItem.bitmap != null) {
            imageView.setImageBitmap(fotoItem.bitmap);
        } else if (fotoItem.uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(fotoItem.uri);
                Bitmap bitmapFromUri = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmapFromUri);
            } catch (FileNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_gallery);
            }
        }

        // Click en la imagen para abrir en pantalla completa
        imageView.setOnClickListener(v -> mostrarImagenCompleta(fotoItem.uri, fotoItem.bitmap));

        // Espaciador para empujar el botón hacia la derecha
        View spacer = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        spacer.setLayoutParams(spacerParams);

        // Botón eliminar
        MaterialButton btnEliminar = new MaterialButton(this);
        btnEliminar.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_delete));
        btnEliminar.setIconSize(42);
        btnEliminar.setIconTint(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        btnEliminar.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent));
        btnEliminar.setCornerRadius(8);
        btnEliminar.setStrokeWidth(0);
        btnEliminar.setMinWidth(0);
        btnEliminar.setMinHeight(0);
        btnEliminar.setText("");

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        btnEliminar.setLayoutParams(btnParams);
        btnEliminar.setPadding(0, 0, 0, 0);

        // Evento para eliminar foto - CORREGIDO
        btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));

        // Agregar vistas al contenedor
        container.addView(imageView);
        container.addView(spacer);
        container.addView(btnEliminar);
        cardView.addView(container);

        // Agregar al contenedor principal
        binding.containerFotos.addView(cardView);
    }
    //metodo para mostrar la imagen en la vista previa
    private void mostrarImagenCompleta(Uri imageUri, Bitmap bitmap) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // FORZAR que ocupe toda la pantalla incluyendo barras del sistema
            dialog.getWindow().setFlags(
                    android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // Ocultar completamente las barras del sistema
            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

        // Obtener dimensiones REALES de la pantalla (incluyendo TODO)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int realScreenWidth = displayMetrics.widthPixels;
        int realScreenHeight = displayMetrics.heightPixels;

        // Crear layout que FORZOSAMENTE ocupe toda la pantalla real
        RelativeLayout mainLayout = new RelativeLayout(this);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
                realScreenWidth, realScreenHeight)); // Usar dimensiones reales explícitas
        mainLayout.setBackgroundColor(Color.parseColor("#CC000000")); // Tu fondo semitransparente oscuro

        // Forzar que el layout se extienda más allá de los límites normales
        mainLayout.setPadding(-50, -50, -50, -50); // Padding negativo para extender más

        // ImageView para la imagen completa con tamaño CONTROLADO
        ImageView fullImageView = new ImageView(this);

        // Calcular tamaño máximo para la imagen (80% de la pantalla)
        int maxWidth = (int) (realScreenWidth * 0.8);
        int maxHeight = (int) (realScreenHeight * 0.6);

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                maxWidth, maxHeight);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageParams.setMargins(50, 50, 50, 50); // Compensar el padding negativo
        fullImageView.setLayoutParams(imageParams);
        fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fullImageView.setAdjustViewBounds(true);

        // Cargar imagen con redimensionamiento
        if (bitmap != null) {
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, maxWidth, maxHeight);
            fullImageView.setImageBitmap(resizedBitmap);
        } else if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = redimensionarBitmap(originalBitmap, maxWidth, maxHeight);
                fullImageView.setImageBitmap(resizedBitmap);
            } catch (FileNotFoundException e) {
                fullImageView.setImageResource(R.drawable.ic_gallery);
            }
        }

        // Botón cerrar
        MaterialButton btnCerrar = new MaterialButton(this);
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                (int) (48 * getResources().getDisplayMetrics().density),
                (int) (48 * getResources().getDisplayMetrics().density));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.setMargins(66, 98, 16, 66); // Compensar padding negativo + margen normal
        btnCerrar.setLayoutParams(btnParams);

        btnCerrar.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel));
        btnCerrar.setIconSize((int) (32 * getResources().getDisplayMetrics().density)); // X más grande
        btnCerrar.setIconTint(ContextCompat.getColorStateList(this, android.R.color.white));
        btnCerrar.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent)); // Sin círculo negro
        btnCerrar.setCornerRadius((int) (24 * getResources().getDisplayMetrics().density));
        btnCerrar.setText("");
        btnCerrar.setPadding(0, 0, 0, 0);
        btnCerrar.setMinWidth(0);
        btnCerrar.setMinHeight(0);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        mainLayout.setOnClickListener(v -> dialog.dismiss());

        mainLayout.addView(fullImageView);
        mainLayout.addView(btnCerrar);

        dialog.setContentView(mainLayout);
        dialog.show();
    }
    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calcular ratio para mantener proporción
        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // Metodo  para eliminar fotos
    private void eliminarFoto(CardView cardView) {
        // Encontrar el índice de la vista en el contenedor
        int indexEnContenedor = binding.containerFotos.indexOfChild(cardView);

        if (indexEnContenedor >= 0 && indexEnContenedor < todasLasFotos.size()) {
            // Eliminar de la lista
            todasLasFotos.remove(indexEnContenedor);

            // Eliminar la vista del contenedor
            binding.containerFotos.removeView(cardView);

            // Si no quedan fotos, ocultar el contenedor
            if (todasLasFotos.isEmpty()) {
                binding.containerFotos.setVisibility(View.GONE);
            }

            // Actualizar los listeners de los botones restantes
            actualizarListenersEliminacion();
        }
    }

    // Metodo para actualizar los listeners de eliminación después de eliminar una foto
    private void actualizarListenersEliminacion() {
        for (int i = 0; i < binding.containerFotos.getChildCount(); i++) {
            View childView = binding.containerFotos.getChildAt(i);
            if (childView instanceof CardView) {
                CardView cardView = (CardView) childView;
                LinearLayout container = (LinearLayout) cardView.getChildAt(0);

                // Encontrar el botón eliminar
                MaterialButton btnEliminar = null;
                for (int j = 0; j < container.getChildCount(); j++) {
                    View view = container.getChildAt(j);
                    if (view instanceof MaterialButton) {
                        btnEliminar = (MaterialButton) view;
                        break;
                    }
                }

                if (btnEliminar != null) {
                    // Actualizar el listener con la referencia correcta
                    btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));
                }
            }
        }
    }

    ////////////////////////METODOS PARA EL GUARDADO DE IMAGENES EN AWS//////////////////////////

    //METODO PRINCIPAL PARA SUBIR TODAS LAS IMÁGENES
    private void subirTodasLasImagenes(Consumer<List<String>> onTodasSubidas, Consumer<String> onError) {
        if (todasLasFotos.isEmpty()) {
            // No hay imágenes, continuar con lista vacía
            onTodasSubidas.accept(new ArrayList<>());
            return;
        }

        List<String> urlsImagenes = new ArrayList<>();
        AtomicInteger contador = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        // Mostrar progreso
        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnRegistrarHotel.setText("Subiendo imágenes...");

        for (int i = 0; i < todasLasFotos.size(); i++) {
            FotoItem fotoItem = todasLasFotos.get(i);
            final int index = i;

            subirImagenAWS(fotoItem, index,
                    // OnSuccess
                    url -> {
                        urlsImagenes.add(url);
                        int completadas = contador.incrementAndGet();

                        // Actualizar progreso
                        runOnUiThread(() -> {
                            binding.btnRegistrarHotel.setText("Subiendo " + completadas + "/" + todasLasFotos.size());
                        });

                        // Si todas las imágenes se subieron
                        if (completadas == todasLasFotos.size()) {
                            runOnUiThread(() -> {
                                onTodasSubidas.accept(urlsImagenes);
                            });
                        }
                    },
                    // OnError
                    error -> {
                        errores.incrementAndGet();
                        int completadas = contador.incrementAndGet();

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error subiendo imagen " + (index + 1) + ": " + error, Toast.LENGTH_SHORT).show();

                            // Si todas las imágenes se procesaron (exitosas + errores)
                            if (completadas == todasLasFotos.size()) {
                                if (errores.get() == todasLasFotos.size()) {
                                    // Todas fallaron
                                    onError.accept("Error al subir todas las imágenes");
                                } else {
                                    // Algunas se subieron exitosamente
                                    onTodasSubidas.accept(urlsImagenes);
                                }
                            }
                        });
                    }
            );
        }
    }

    private void subirImagenAWS(FotoItem fotoItem, int index, Consumer<String> onUrlObtenida, Consumer<String> onError) {
        File file = null;

        try {
            if (fotoItem.bitmap != null) {
                // Es de cámara - convertir bitmap a file
                String fileName = "camera_image_" + System.currentTimeMillis() + "_" + index;
                file = bitmapToFile(fotoItem.bitmap, fileName);
            } else if (fotoItem.uri != null) {
                // Es de galería o archivo - obtener path
                file = uriToFile(fotoItem.uri, "selected_image_" + System.currentTimeMillis() + "_" + index);
            }

            if (file == null || !file.exists()) {
                onError.accept("No se pudo procesar la imagen");
                return;
            }

            // VERIFICAR TAMAÑO DEL ARCHIVO
            long fileSize = file.length();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (fileSize > maxSize) {
                onError.accept("La imagen es demasiado grande. Máximo 10MB");
                return;
            }

            // Crear request body con tipo MIME correcto
            MediaType mediaType = MediaType.parse("image/jpeg");
            RequestBody reqFile = RequestBody.create(file, mediaType);

            // IMPORTANTE: Usar el nombre correcto del archivo
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

            // Configurar Retrofit con timeouts más largos
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://mm5k8l79xd.execute-api.us-west-2.amazonaws.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AwsService service = retrofit.create(AwsService.class);

            // Realizar llamada con mejor manejo de errores
            service.subirImagen(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    // Log detallado de la respuesta
                    Log.d("AWS_UPLOAD", "Response code: " + response.code());
                    Log.d("AWS_UPLOAD", "Response message: " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse uploadResponse = response.body();
                        Log.d("AWS_UPLOAD", "Upload response: " + uploadResponse.toString());

                        if (uploadResponse.isSuccess() && uploadResponse.getUrl() != null && !uploadResponse.getUrl().isEmpty()) {
                            onUrlObtenida.accept(uploadResponse.getUrl());
                        } else {
                            String errorMsg = uploadResponse.getMessage() != null ?
                                    uploadResponse.getMessage() : "Respuesta sin URL válida";
                            onError.accept("Error en la respuesta: " + errorMsg);
                        }
                    } else {
                        // Obtener el cuerpo del error
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                Log.e("AWS_UPLOAD", "Error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e("AWS_UPLOAD", "Error reading error body", e);
                        }

                        onError.accept("Error HTTP " + response.code() + ": " + response.message() +
                                (errorBody.isEmpty() ? "" : "\nDetalle: " + errorBody));
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.e("AWS_UPLOAD", "Network error", t);
                    onError.accept("Error de conexión: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("AWS_UPLOAD", "Exception in subirImagenAWS", e);
            onError.accept("Error al procesar imagen: " + e.getMessage());
        }
    }

    // Metodo mejorado para convertir bitmap a file
    private File bitmapToFile(Bitmap bitmap, String fileName) {
        try {
            // Crear directorio temporal
            File dir = new File(getCacheDir(), "temp_images");
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("FILE_CREATION", "No se pudo crear directorio temporal");
                return null;
            }

            // Crear archivo con extensión .jpg
            File file = new File(dir, fileName + ".jpg");

            // Comprimir y guardar bitmap en máxima calidad JPEG (100%)
            FileOutputStream fos = new FileOutputStream(file);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);  // <<<<< FULL HD
            fos.flush();
            fos.close();

            if (!compressed) {
                Log.e("FILE_CREATION", "Error al comprimir bitmap");
                return null;
            }

            if (!file.exists() || file.length() == 0) {
                Log.e("FILE_CREATION", "Archivo no se creó correctamente");
                return null;
            }

            Log.d("FILE_CREATION", "Archivo HD creado: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            return file;
        } catch (Exception e) {
            Log.e("FILE_CREATION", "Error creando archivo HD desde bitmap", e);
            return null;
        }
    }

    // 6. METODO AUXILIAR PARA CONVERTIR URI A FILE
    private File uriToFile(Uri uri, String fileName) {
        try {
            // Crear directorio temporal
            File dir = new File(getCacheDir(), "temp_images");
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    Log.e("FILE_CREATION", "No se pudo crear directorio temporal");
                    return null;
                }
            }

            // Crear archivo destino con extensión .jpg
            File file = new File(dir, fileName + ".jpg");

            // Copiar contenido del URI al archivo
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("FILE_CREATION", "No se pudo abrir InputStream del URI");
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096]; // Buffer más grande
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Verificar que el archivo se creó correctamente
            if (!file.exists() || file.length() == 0) {
                Log.e("FILE_CREATION", "Archivo no se creó correctamente desde URI");
                return null;
            }

            Log.d("FILE_CREATION", "Archivo creado desde URI: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            return file;
        } catch (Exception e) {
            Log.e("FILE_CREATION", "Error creating file from URI", e);
            return null;
        }
    }

    private void guardarHotel(String nombre, String direccion, List<String> referencias) {

        // Validar que haya al menos 4 fotos
        if (todasLasFotos.size() < 4) {
            mostrarError(binding.errorTipoFotos, "Debes agregar al menos 4 fotos del hotel");
            return;
        }

        // Estado de guardado
        guardandoServicio = true;
        binding.btnRegistrarHotel.setEnabled(false);
        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnRegistrarHotel.setText("Guardando...");

        subirTodasLasImagenes(
                // OnSuccess - todas las imágenes subidas
                urlsImagenes -> {
                    String idAdministrador = (currentUser != null) ? currentUser.getUid() : "desconocido";

                    // Crear objeto Hotel con imágenes subidas
                    Hotel hotel = new Hotel(nombre, direccion, referencias, urlsImagenes, idAdministrador);

                    runOnUiThread(() -> {
                        binding.btnRegistrarHotel.setText("Guardando servicio...");
                    });




                    db.collection("hoteles")
                            .add(hotel)
                            .addOnSuccessListener(documentReference -> {
                                String idGenerado = documentReference.getId();

                                // Guardar ese ID dentro del mismo documento (campo 'id')
                                documentReference.update("id", idGenerado)
                                        .addOnSuccessListener(unused -> {

                                            // 🔥 NUEVO: guardar también el idHotel en el usuario
                                            db.collection("usuarios")
                                                    .document(idAdministrador)
                                                    .update("idHotel", idGenerado)
                                                    .addOnSuccessListener(userUpdateSuccess -> {
                                                        Toast.makeText(this, "Hotel registrado correctamente", Toast.LENGTH_SHORT).show();

                                                        guardandoServicio = false;
                                                        binding.btnRegistrarHotel.setEnabled(true);
                                                        binding.progressBarGuardar.setVisibility(View.GONE);
                                                        binding.btnRegistrarHotel.setText("Registrar hotel");

                                                        limpiarImagenesTemporales();

                                                        // Ir a pantalla principal del admin
                                                        Intent intent = new Intent(this, PagPrincipalAdmin.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    })
                                                    .addOnFailureListener(userUpdateError -> {
                                                        Toast.makeText(this, "Hotel guardado, pero no se pudo asignar al usuario: " + userUpdateError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    });

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al guardar ID del hotel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar el hotel: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                guardandoServicio = false;
                                binding.btnRegistrarHotel.setEnabled(true);
                                binding.progressBarGuardar.setVisibility(View.GONE);
                                binding.btnRegistrarHotel.setText("Registrar hotel");

                                Intent intent = new Intent(this, RegistroPrimeraVez.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                },

                // OnError - error subiendo imágenes
                error -> {
                    Toast.makeText(this, "Error subiendo imágenes: " + error, Toast.LENGTH_SHORT).show();

                    guardandoServicio = false;
                    binding.btnRegistrarHotel.setEnabled(true);
                    binding.progressBarGuardar.setVisibility(View.GONE);
                    binding.btnRegistrarHotel.setText("Registrar hotel");

                    Intent intent = new Intent(this, RegistroPrimeraVez.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
        );
    }


    private boolean validarCampos() {
        boolean esValido = true;

        // Limpiar errores previos
        binding.errorTipoNombre.setVisibility(View.GONE);
        binding.errorTipoDireccion.setVisibility(View.GONE);
        binding.errorTipoReferencia.setVisibility(View.GONE);
        binding.errorTipoFotos.setVisibility(View.GONE);

        // Validar nombre
        String nombre = binding.inputNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            binding.errorTipoNombre.setText("Por favor, ingrese el nombre del hotel");
            binding.errorTipoNombre.setVisibility(View.VISIBLE);
            esValido = false;
        } else if (nombre.length() < 3) {
            binding.errorTipoNombre.setText("El nombre debe tener al menos 3 caracteres");
            binding.errorTipoNombre.setVisibility(View.VISIBLE);
            esValido = false;
        } else if (nombre.length() > 50) {
            binding.errorTipoNombre.setText("El nombre no puede exceder 50 caracteres");
            binding.errorTipoNombre.setVisibility(View.VISIBLE);
            esValido = false;
        } else if (nombre.matches("^[0-9]+$")) {
            binding.errorTipoNombre.setText("El nombre no puede contener solo números");
            binding.errorTipoNombre.setVisibility(View.VISIBLE);
            esValido = false;
        }

        // Validar dirección
        String direccion = binding.inputDireccion.getText().toString().trim();
        if (direccion.isEmpty()) {
            binding.errorTipoDireccion.setText("Por favor, ingrese la dirección del hotel");
            binding.errorTipoDireccion.setVisibility(View.VISIBLE);
            esValido = false;
        } else if (direccion.length() < 5) {
            binding.errorTipoDireccion.setText("La dirección debe tener al menos 5 caracteres");
            binding.errorTipoDireccion.setVisibility(View.VISIBLE);
            esValido = false;
        }

        // Validar referencias
        if (referencias.isEmpty()) {
            binding.errorTipoReferencia.setText("Agregue al menos una referencia");
            binding.errorTipoReferencia.setVisibility(View.VISIBLE);
            esValido = false;
        }

        // Validar mínimo 4 fotos
        if (todasLasFotos.size() < 4) {
            binding.errorTipoFotos.setText("Debe agregar al menos 4 fotos del hotel");
            binding.errorTipoFotos.setVisibility(View.VISIBLE);
            esValido = false;
        }

        return esValido;
    }

    private void mostrarError(TextView errorView, String mensaje) {
        errorView.setText(mensaje);
        errorView.setVisibility(View.VISIBLE);

        // Opcional: hacer scroll hacia el error
        errorView.getParent().requestChildFocus(errorView, errorView);
    }
    private void configurarValidacionTiempoReal() {
        // Validación en tiempo real para el nombre del hotel
        binding.inputNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.errorTipoNombre.getVisibility() == View.VISIBLE) {
                    binding.errorTipoNombre.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validación en tiempo real para la dirección del hotel
        binding.inputDireccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.errorTipoDireccion.getVisibility() == View.VISIBLE) {
                    binding.errorTipoDireccion.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validación en tiempo real para referencias (solo borra el error si escribe algo)
        binding.inputReferencia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.errorTipoReferencia.getVisibility() == View.VISIBLE) {
                    binding.errorTipoReferencia.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    // 9. METODO PARA LIMPIAR IMÁGENES TEMPORALES
    private void limpiarImagenesTemporales() {
        try {
            File tempDir = new File(getCacheDir(), "temp_images");
            if (tempDir.exists()) {
                File[] files = tempDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void agregarChipReferencia(String texto) {
        // Contenedor externo
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setBackgroundResource(R.drawable.customedittext);
        chip.setPadding(16, 12, 16, 12);
        chip.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chipParams.setMargins(0, 8, 0, 0);
        chip.setLayoutParams(chipParams);

        // Texto
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // Icono eliminar
        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.ic_delete);
        icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
        iconParams.setMargins(16, 0, 0, 0);
        icono.setLayoutParams(iconParams);

        icono.setOnClickListener(v -> {
            referencias.remove(texto);
            binding.layoutReferenciasDinamicas.removeView(chip);
        });

        chip.addView(textView);
        chip.addView(icono);
        binding.layoutReferenciasDinamicas.addView(chip);
    }



}