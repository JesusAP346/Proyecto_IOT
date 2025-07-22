package com.example.proyecto_iot.cliente.chatV2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.chat.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClienteActivity extends AppCompatActivity {

    private static final String TAG = "ChatClienteActivity";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int NOTIFICATION_ID = 1001;

    private RecyclerView recyclerViewMessages;
    private String adminName = "Soporte";
    private MessageClienteAdapter messageAdapter;
    private List<Message> messageList;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private Date lastMessageTimestamp = null;
    private boolean isActivityVisible = false;

    private TextView tvEmptyChat;
    private Toolbar toolbar;
    private View messagesContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration messageListener;

    private String chatId;
    private String currentClientId;
    private String adminId; // ID del administrador con quien se está chateando

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_cliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getIntentData();
        initializeViews();
        initializeFirebase();
        requestNotificationPermission();
        setupRecyclerView();
        setupToolbar();
        setupSendButton();
        setupKeyboardListener();
        initializeOrCreateChat();
        if (!TextUtils.isEmpty(adminId)) {
            loadAdminName();
        }

        loadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
        // Cancelar notificaciones cuando el usuario está viendo el chat
        cancelChatNotifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private void getIntentData() {
        // Obtener tanto chatId como adminId del Intent
        chatId = getIntent().getStringExtra("chatId");
        adminId = getIntent().getStringExtra("adminId");

        // Si no se proporciona adminId, podrías usar un valor por defecto
        // o buscarlo en la base de datos
        if (TextUtils.isEmpty(adminId)) {
            // Aquí podrías implementar lógica para obtener el adminId
            // Por ejemplo, buscar el primer administrador disponible
            findAvailableAdmin();
        } else if (TextUtils.isEmpty(chatId)) {
            // Si no hay chatId pero sí adminId, generar uno nuevo
            generateChatId();
        }
    }

    private void findAvailableAdmin() {
        // Buscar un administrador disponible
        db.collection("usuarios")
                .whereEqualTo("idRol", "Administrador") // Ajusta según tu estructura
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot adminDoc = queryDocumentSnapshots.getDocuments().get(0);
                        adminId = adminDoc.getId();

                        // Obtener el nombre del administrador encontrado
                        String nombre = adminDoc.getString("nombre");
                        String apellido = adminDoc.getString("apellido");

                        if (!TextUtils.isEmpty(nombre)) {
                            if (!TextUtils.isEmpty(apellido)) {
                                adminName = nombre + " " + apellido;
                            } else {
                                adminName = nombre;
                            }

                            // Actualizar el título
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(adminName);
                            }
                        }

                        generateChatId();
                        loadMessages();
                    } else {
                        Toast.makeText(this, "No hay administradores disponibles", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar administrador", e);
                    Toast.makeText(this, "Error al conectar con soporte", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void generateChatId() {
        // Generar un chatId único basado en los IDs de cliente y admin
        if (currentClientId != null && adminId != null) {
            // Ordenar los IDs para asegurar consistencia
            if (currentClientId.compareTo(adminId) < 0) {
                chatId = currentClientId + "_" + adminId;
            } else {
                chatId = adminId + "_" + currentClientId;
            }
        }
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyChat = findViewById(R.id.tv_empty_chat);
        toolbar = findViewById(R.id.toolbar);
        messagesContainer = findViewById(R.id.messages_container);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            currentClientId = auth.getCurrentUser().getUid();
        } else {
            Log.e(TAG, "No hay usuario autenticado");
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones concedido");
            } else {
                Log.d(TAG, "Permiso de notificaciones denegado");
                Toast.makeText(this, "Las notificaciones están deshabilitadas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageClienteAdapter(messageList, currentClientId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setHasFixedSize(false);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(adminName);
        }
    }

    private void loadAdminName() {
        if (TextUtils.isEmpty(adminId)) {
            return;
        }

        db.collection("usuarios").document(adminId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ajusta estos nombres de campos según tu estructura de datos
                        String nombre = documentSnapshot.getString("nombres");
                        String apellido = documentSnapshot.getString("apellidos");

                        if (!TextUtils.isEmpty(nombre)) {
                            if (!TextUtils.isEmpty(apellido)) {
                                adminName = nombre + " " + apellido;
                            } else {
                                adminName = nombre;
                            }
                        } else {
                            // Si no hay nombre, usar email o ID
                            String email = documentSnapshot.getString("email");
                            if (!TextUtils.isEmpty(email)) {
                                adminName = email.split("@")[0]; // Usar la parte antes del @
                            } else {
                                adminName = "Administrador"; // Fallback
                            }
                        }

                        // Actualizar el título del toolbar
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(adminName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar nombre del administrador", e);
                    // Mantener el título por defecto
                });
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void setupKeyboardListener() {
        final View rootView = findViewById(R.id.main);
        keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private int initialHeight = -1;
            private boolean wasKeyboardOpen = false;

            @Override
            public void onGlobalLayout() {
                if (initialHeight == -1) {
                    initialHeight = rootView.getHeight();
                }

                int currentHeight = rootView.getHeight();
                int heightDiff = initialHeight - currentHeight;

                boolean isKeyboardOpen = heightDiff > 200;

                if (isKeyboardOpen != wasKeyboardOpen) {
                    wasKeyboardOpen = isKeyboardOpen;

                    if (isKeyboardOpen) {
                        scrollToBottom();
                    }
                }
            }
        };

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    private void scrollToBottom() {
        if (messageList != null && !messageList.isEmpty()) {
            recyclerViewMessages.post(() -> {
                recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
            });
        }
    }

    private void initializeOrCreateChat() {
        if (TextUtils.isEmpty(chatId)) {
            return;
        }

        // Verificar si el chat ya existe, si no, crearlo
        db.collection("chats").document(chatId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Crear nuevo chat
                        createNewChat();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al verificar chat", e);
                });
    }

    private void createNewChat() {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("clienteId", currentClientId);
        chatData.put("adminId", adminId);
        chatData.put("createdAt", new Date());
        chatData.put("lastMessage", "");
        chatData.put("lastMessageTime", new Date());
        chatData.put("isActive", true);

        db.collection("chats").document(chatId).set(chatData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Chat creado exitosamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al crear chat", e);
                });
    }

    private void loadMessages() {
        if (TextUtils.isEmpty(chatId)) {
            return;
        }

        showLoading(true);

        messageListener = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    showLoading(false);

                    if (error != null) {
                        Log.e(TAG, "Error al cargar mensajes: ", error);
                        Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        List<Message> newMessages = new ArrayList<>();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                message.setMessageId(doc.getId());
                                newMessages.add(message);
                            }
                        }

                        // Verificar si hay mensajes nuevos del administrador
                        checkForNewAdminMessages(newMessages);

                        messageList.clear();
                        messageList.addAll(newMessages);
                        messageAdapter.updateMessageList(messageList);

                        if (messageList.isEmpty()) {
                            tvEmptyChat.setVisibility(View.VISIBLE);
                            tvEmptyChat.setText("¡Hola! Escribe tu mensaje para comenzar a chatear con soporte.");
                            recyclerViewMessages.setVisibility(View.GONE);
                        } else {
                            tvEmptyChat.setVisibility(View.GONE);
                            recyclerViewMessages.setVisibility(View.VISIBLE);
                            scrollToBottom();
                        }

                        markMessagesAsRead();
                        Log.d(TAG, "Mensajes cargados: " + messageList.size());
                    }
                });
    }

    private void checkForNewAdminMessages(List<Message> newMessages) {
        if (messageList.isEmpty()) {
            // Primera carga, no mostrar notificaciones
            return;
        }

        // Obtener el último mensaje conocido
        Message lastKnownMessage = messageList.isEmpty() ? null : messageList.get(messageList.size() - 1);
        Date lastKnownTimestamp = lastKnownMessage != null ? lastKnownMessage.getTimestamp() : null;

        // Buscar mensajes nuevos del administrador
        for (Message message : newMessages) {
            if (!message.getSenderId().equals(currentClientId) && // No es nuestro mensaje
                    (lastKnownTimestamp == null || message.getTimestamp().after(lastKnownTimestamp))) {

                // Es un mensaje nuevo del administrador
                String senderName = !TextUtils.isEmpty(adminName) ? adminName : "Soporte";
                showNotification("Nuevo mensaje de " + senderName, message.getText());
                break; // Solo mostrar notificación para el último mensaje nuevo
            }
        }
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(chatId)) {
            Toast.makeText(this, "Error: No se puede enviar el mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentClientId);
        messageData.put("receiverId", adminId);
        messageData.put("text", messageText);
        messageData.put("timestamp", new Date());
        messageData.put("type", "text");
        messageData.put("isRead", false);

        etMessage.setText("");

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Mensaje enviado: " + documentReference.getId());
                    updateLastMessage(messageText);
                    scrollToBottom();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al enviar mensaje", e);
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                    etMessage.setText(messageText);
                });
    }

    private void updateLastMessage(String messageText) {
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage", messageText);
        chatUpdate.put("lastMessageTime", new Date());

        db.collection("chats")
                .document(chatId)
                .update(chatUpdate)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Último mensaje actualizado");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar último mensaje", e);
                });
    }

    private void markMessagesAsRead() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("isRead", false)
                .whereNotEqualTo("senderId", currentClientId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().update("isRead", true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al marcar mensajes como leídos", e);
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerViewMessages.setVisibility(View.GONE);
            tvEmptyChat.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showNotification(String title, String messageText) {
        // Solo mostrar notificación si la actividad no está visible
        if (isActivityVisible) {
            return;
        }

        // Verificar permisos para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Sin permiso para mostrar notificaciones");
                return;
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "chat_channel";

        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Chat Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones del chat con soporte");
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent para abrir la actividad del chat cuando se toque la notificación
        Intent intent = new Intent(this, ChatClienteActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("adminId", adminId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText));

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelChatNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }

        if (keyboardLayoutListener != null) {
            View rootView = findViewById(R.id.main);
            if (rootView != null) {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}