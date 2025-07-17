package com.example.proyecto_iot.cliente.chat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatBottomSheet extends BottomSheetDialogFragment {

    private EditText etMensaje;
    private ImageButton btnEnviarMicrofono;
    private ImageButton btnAdjuntar;
    private ImageButton btnEmoji;
    private ImageButton btnCerrarChat;
    private RecyclerView recyclerViewMensajes;
    private TextView tvEstado;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> mensajes;
    private boolean isRecording = false;
    private String idAdministrador;
    private String idUsuarioActual;
    private PopupWindow emojiPopup;
    private String chatId;
    private String nombreAdministrador = "Administrador del hotel";

    // Firestore
    private FirebaseFirestore db;
    private ListenerRegistration messagesListener;
    private ListenerRegistration adminStatusListener;

    // Request codes para intents
    private static final int REQUEST_PICK_FILE = 100;
    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_CAMERA = 102;

    // Emojis disponibles
    private static final String[] EMOJI_LIST = {
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
            "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩",
            "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣",
            "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬",
            "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗",
            "👍", "👎", "👌", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉",
            "👆", "👇", "☝️", "✋", "🤚", "🖐️", "🖖", "👋", "🤏", "💪",
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_chat, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        initViews(view);
        initializeChat();
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initializeChat() {
        if (getArguments() != null) {
            idAdministrador = getArguments().getString("idAdministrador");
            Log.d("ChatBottomSheet", "ID Administrador: " + idAdministrador);

            // Obtener ID del usuario actual
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d("ChatBottomSheet", "ID Usuario Actual: " + idUsuarioActual);
            } else {
                Log.e("ChatBottomSheet", "Usuario no autenticado");
                return;
            }

            // Generar ID único para el chat
            chatId = generateChatId(idUsuarioActual, idAdministrador);
            Log.d("ChatBottomSheet", "Chat ID: " + chatId);

            // Obtener información del administrador
            obtenerInfoAdministrador();

            // Inicializar el chat en Firestore
            inicializarChatEnFirestore();
        } else {
            Log.e("ChatBottomSheet", "No se recibieron argumentos");
        }
    }

    private String generateChatId(String userId, String adminId) {
        // Generar ID único combinando los dos IDs de forma consistente
        return userId.compareTo(adminId) < 0 ? userId + "_" + adminId : adminId + "_" + userId;
    }

    private void obtenerInfoAdministrador() {
        db.collection("usuarios")
                .document(idAdministrador)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            nombreAdministrador = nombre;
                        }
                        Log.d("ChatBottomSheet", "Nombre administrador: " + nombreAdministrador);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error obteniendo info del administrador", e);
                });
    }

    private void inicializarChatEnFirestore() {
        // Verificar si el chat ya existe
        db.collection("chats")
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Crear nuevo chat
                        Map<String, Object> chatData = new HashMap<>();
                        chatData.put("userId", idUsuarioActual);
                        chatData.put("adminId", idAdministrador);
                        chatData.put("createdAt", Timestamp.now());
                        chatData.put("lastMessage", "");
                        chatData.put("lastMessageTime", Timestamp.now());
                        chatData.put("isActive", true);

                        db.collection("chats")
                                .document(chatId)
                                .set(chatData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ChatBottomSheet", "Chat creado exitosamente");
                                    setupMessageListener();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ChatBottomSheet", "Error creando chat", e);
                                });
                    } else {
                        Log.d("ChatBottomSheet", "Chat ya existe");
                        setupMessageListener();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error verificando chat", e);
                });
    }

    private void setupMessageListener() {
        // Listener para mensajes en tiempo real
        messagesListener = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("ChatBottomSheet", "Error escuchando mensajes", e);
                            return;
                        }

                        if (snapshots != null) {
                            mensajes.clear();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                ChatMessage message = documentToMessage(doc);
                                if (message != null) {
                                    mensajes.add(message);
                                }
                            }

                            if (chatAdapter != null) {
                                chatAdapter.notifyDataSetChanged();
                                scrollToBottom();
                            }
                        }
                    }
                });

        // Listener para estado del administrador
        setupAdminStatusListener();
    }

    private void setupAdminStatusListener() {
        adminStatusListener = db.collection("usuarios")
                .document(idAdministrador)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatBottomSheet", "Error escuchando estado del admin", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Boolean isOnline = documentSnapshot.getBoolean("isOnline");
                        Timestamp lastSeen = documentSnapshot.getTimestamp("lastSeen");

                        if (isOnline != null && isOnline) {
                            updateEstado("En línea");
                        } else if (lastSeen != null) {
                            updateEstado("Últ. vez " + formatLastSeen(lastSeen));
                        } else {
                            updateEstado("Desconectado");
                        }
                    }
                });
    }

    private String formatLastSeen(Timestamp timestamp) {
        Date date = timestamp.toDate();
        Date now = new Date();
        long diff = now.getTime() - date.getTime();

        if (diff < 60000) { // Menos de 1 minuto
            return "hace un momento";
        } else if (diff < 3600000) { // Menos de 1 hora
            return "hace " + (diff / 60000) + " min";
        } else if (diff < 86400000) { // Menos de 24 horas
            return "hace " + (diff / 3600000) + " h";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }

    private ChatMessage documentToMessage(DocumentSnapshot doc) {
        try {
            String texto = doc.getString("text");
            String senderId = doc.getString("senderId");
            Timestamp timestamp = doc.getTimestamp("timestamp");
            String type = doc.getString("type");

            if (texto == null || senderId == null || timestamp == null) {
                return null;
            }

            boolean isEnviado = senderId.equals(idUsuarioActual);
            String hora = formatTime(timestamp);

            // Determinar estado del mensaje
            ChatMessage.MessageStatus status = ChatMessage.MessageStatus.DELIVERED;
            if (isEnviado) {
                Boolean isRead = doc.getBoolean("isRead");
                if (isRead != null && isRead) {
                    status = ChatMessage.MessageStatus.READ;
                } else {
                    status = ChatMessage.MessageStatus.SENT;
                }
            }

            return new ChatMessage(texto, hora, isEnviado, status);
        } catch (Exception e) {
            Log.e("ChatBottomSheet", "Error convirtiendo documento a mensaje", e);
            return null;
        }
    }

    private String formatTime(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parent = (View) view.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parent);

        bottomSheetBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setDraggable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    private void initViews(View view) {
        etMensaje = view.findViewById(R.id.etMensaje);
        btnEnviarMicrofono = view.findViewById(R.id.btnEnviarMicrofono);
        btnAdjuntar = view.findViewById(R.id.btnAdjuntar);
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnCerrarChat = view.findViewById(R.id.btnCerrarChat);
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        tvEstado = view.findViewById(R.id.tvEstado);

        mensajes = new ArrayList<>();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(mensajes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);

        recyclerViewMensajes.setLayoutManager(layoutManager);
        recyclerViewMensajes.setAdapter(chatAdapter);
    }

    private void scrollToBottom() {
        if (recyclerViewMensajes != null && chatAdapter != null && !mensajes.isEmpty()) {
            recyclerViewMensajes.post(() -> {
                recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);
            });
        }
    }

    private void setupListeners() {
        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendButton(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEnviarMicrofono.setOnClickListener(v -> {
            String mensaje = etMensaje.getText().toString().trim();

            if (!mensaje.isEmpty()) {
                enviarMensaje(mensaje);
            } else {
                toggleRecording();
            }
        });

        btnAdjuntar.setOnClickListener(v -> mostrarOpcionesAdjuntar());
        btnEmoji.setOnClickListener(v -> mostrarSelectorEmoji());
        btnCerrarChat.setOnClickListener(v -> dismiss());
    }

    private void updateSendButton(boolean hasText) {
        if (hasText) {
            btnEnviarMicrofono.setImageResource(R.drawable.ic_send);
            btnEnviarMicrofono.setContentDescription("Enviar mensaje");
        } else {
            btnEnviarMicrofono.setImageResource(R.drawable.ic_mic);
            btnEnviarMicrofono.setContentDescription("Grabar mensaje de voz");
        }
    }

    private void enviarMensaje(String texto) {
        if (texto.trim().isEmpty()) return;

        // Crear mensaje en Firestore
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("text", texto);
        messageData.put("senderId", idUsuarioActual);
        messageData.put("receiverId", idAdministrador);
        messageData.put("timestamp", Timestamp.now());
        messageData.put("type", "text");
        messageData.put("isRead", false);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ChatBottomSheet", "Mensaje enviado: " + documentReference.getId());

                    // Actualizar último mensaje del chat
                    actualizarUltimoMensaje(texto);

                    etMensaje.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error enviando mensaje", e);
                    Toast.makeText(getContext(), "Error enviando mensaje", Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarUltimoMensaje(String texto) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("lastMessage", texto);
        updateData.put("lastMessageTime", Timestamp.now());

        db.collection("chats")
                .document(chatId)
                .update(updateData)
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error actualizando último mensaje", e);
                });
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        btnEnviarMicrofono.setImageResource(R.drawable.ic_send);
        updateEstado("Grabando...");
    }

    private void stopRecording() {
        isRecording = false;
        btnEnviarMicrofono.setImageResource(R.drawable.ic_mic);
        updateEstado("En línea");

        enviarMensajeAudio();
    }

    private void enviarMensajeAudio() {
        // Crear mensaje de audio en Firestore
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("text", "🎵 Mensaje de audio");
        messageData.put("senderId", idUsuarioActual);
        messageData.put("receiverId", idAdministrador);
        messageData.put("timestamp", Timestamp.now());
        messageData.put("type", "audio");
        messageData.put("isRead", false);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ChatBottomSheet", "Mensaje de audio enviado");
                    actualizarUltimoMensaje("🎵 Mensaje de audio");
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error enviando mensaje de audio", e);
                });
    }

    private void mostrarOpcionesAdjuntar() {
        String[] opciones = {"Cámara", "Galería", "Archivo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Adjuntar archivo")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Cámara
                            abrirCamara();
                            break;
                        case 1: // Galería
                            abrirGaleria();
                            break;
                        case 2: // Archivo
                            abrirExploradorArchivos();
                            break;
                    }
                })
                .show();
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void abrirExploradorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), REQUEST_PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String mensajeArchivo = "";
            String tipoArchivo = "";

            switch (requestCode) {
                case REQUEST_CAMERA:
                    mensajeArchivo = "📷 Foto de cámara";
                    tipoArchivo = "image";
                    break;
                case REQUEST_PICK_IMAGE:
                    mensajeArchivo = "🖼️ Imagen de galería";
                    tipoArchivo = "image";
                    break;
                case REQUEST_PICK_FILE:
                    if (data != null && data.getData() != null) {
                        String fileName = getFileName(data.getData());
                        mensajeArchivo = "📎 " + (fileName != null ? fileName : "Archivo adjunto");
                        tipoArchivo = "file";
                    } else {
                        mensajeArchivo = "📎 Archivo adjunto";
                        tipoArchivo = "file";
                    }
                    break;
            }

            enviarMensajeArchivo(mensajeArchivo, tipoArchivo);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void enviarMensajeArchivo(String textoArchivo, String tipoArchivo) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("text", textoArchivo);
        messageData.put("senderId", idUsuarioActual);
        messageData.put("receiverId", idAdministrador);
        messageData.put("timestamp", Timestamp.now());
        messageData.put("type", tipoArchivo);
        messageData.put("isRead", false);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ChatBottomSheet", "Archivo enviado");
                    actualizarUltimoMensaje(textoArchivo);
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatBottomSheet", "Error enviando archivo", e);
                });
    }

    private void mostrarSelectorEmoji() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
            return;
        }

        // Crear vista del popup
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.emoji_selector, null);
        GridLayout gridEmojis = popupView.findViewById(R.id.gridEmojis);

        // Configurar grid de emojis
        gridEmojis.setColumnCount(10);
        gridEmojis.setRowCount((int) Math.ceil(EMOJI_LIST.length / 10.0));

        // Agregar emojis al grid
        for (String emoji : EMOJI_LIST) {
            TextView tvEmoji = new TextView(getContext());
            tvEmoji.setText(emoji);
            tvEmoji.setTextSize(24);
            tvEmoji.setPadding(16, 16, 16, 16);
            tvEmoji.setOnClickListener(v -> {
                insertarEmoji(emoji);
                emojiPopup.dismiss();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(4, 4, 4, 4);
            tvEmoji.setLayoutParams(params);

            gridEmojis.addView(tvEmoji);
        }

        // Crear y mostrar popup
        emojiPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                600,
                true);
        emojiPopup.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        emojiPopup.showAsDropDown(btnEmoji, 0, -610);
    }

    private void insertarEmoji(String emoji) {
        String textoActual = etMensaje.getText().toString();
        int cursorPos = etMensaje.getSelectionStart();

        String nuevoTexto = textoActual.substring(0, cursorPos) + emoji + textoActual.substring(cursorPos);
        etMensaje.setText(nuevoTexto);
        etMensaje.setSelection(cursorPos + emoji.length());
    }

    private void updateEstado(String estado) {
        if (tvEstado != null) {
            tvEstado.setText(estado);
        }
    }

    public void marcarMensajesComoLeidos() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("receiverId", idUsuarioActual)
                .whereEqualTo("isRead", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().update("isRead", true);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Limpiar listeners
        if (messagesListener != null) {
            messagesListener.remove();
        }
        if (adminStatusListener != null) {
            adminStatusListener.remove();
        }

        // Marcar mensajes como leídos al cerrar
        marcarMensajesComoLeidos();
    }

    public static class ChatMessage {
        private String texto;
        private String hora;
        private boolean isEnviado;
        private MessageStatus status;

        public enum MessageStatus {
            SENDING, SENT, DELIVERED, READ, ERROR
        }

        public ChatMessage(String texto, String hora, boolean isEnviado, MessageStatus status) {
            this.texto = texto;
            this.hora = hora;
            this.isEnviado = isEnviado;
            this.status = status;
        }

        public String getTexto() { return texto; }
        public String getHora() { return hora; }
        public boolean isEnviado() { return isEnviado; }
        public MessageStatus getStatus() { return status; }
        public void setStatus(MessageStatus status) { this.status = status; }
    }
}