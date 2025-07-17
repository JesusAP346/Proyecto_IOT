package com.example.proyecto_iot.administradorHotel.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Chat> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public ChatAdapter(List<Chat> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChatList(List<Chat> newChatList) {
        this.chatList = newChatList;
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserId;
        private TextView tvLastMessage;
        private TextView tvLastMessageTime;
        private TextView tvChatStatus;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
            tvChatStatus = itemView.findViewById(R.id.tv_chat_status);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onChatClick(chatList.get(position));
                    }
                }
            });
        }

        public void bind(Chat chat) {
            // Mostrar el userId temporalmente mientras se carga el nombre
            tvUserId.setText("Cargando...");

            // Buscar y mostrar el nombre del usuario
            buscarYMostrarNombreUsuario(chat.getUserId());

            tvLastMessage.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "Sin mensajes");

            // Formatear fecha
            if (chat.getLastMessageTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvLastMessageTime.setText(sdf.format(chat.getLastMessageTime()));
            } else {
                tvLastMessageTime.setText("");
            }

            // Mostrar estado del chat
            tvChatStatus.setText(chat.isActive() ? "Activo" : "Inactivo");
            tvChatStatus.setTextColor(chat.isActive() ?
                    itemView.getContext().getResources().getColor(android.R.color.holo_green_dark) :
                    itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }

        private void buscarYMostrarNombreUsuario(String userId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            if (usuario != null) {
                                String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
                                tvUserId.setText(nombreCompleto);
                            } else {
                                tvUserId.setText("Usuario desconocido");
                            }
                        } else {
                            tvUserId.setText("Usuario no encontrado");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ChatAdapter", "Error al obtener usuario", e);
                        tvUserId.setText("Error al cargar usuario");
                    });
        }
    }
}