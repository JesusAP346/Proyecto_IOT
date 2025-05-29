package com.example.proyecto_iot.cliente.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatBottomSheet.ChatMessage> mensajes;

    public ChatAdapter(List<ChatBottomSheet.ChatMessage> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        ChatBottomSheet.ChatMessage mensaje = mensajes.get(position);
        return mensaje.isEnviado() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SENT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatBottomSheet.ChatMessage mensaje = mensajes.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(mensaje);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    // ViewHolder para mensajes enviados
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMensaje;
        private TextView tvHora;
        private ImageView ivStatus;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }

        public void bind(ChatBottomSheet.ChatMessage mensaje) {
            tvMensaje.setText(mensaje.getTexto());
            tvHora.setText(mensaje.getHora());

            // Configurar icono de estado del mensaje
            switch (mensaje.getStatus()) {
                case SENDING:
                    ivStatus.setImageResource(R.drawable.ic_schedule);
                    ivStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.message_time));
                    ivStatus.setVisibility(View.VISIBLE);
                    break;
                case SENT:
                    ivStatus.setImageResource(R.drawable.ic_check);
                    ivStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.message_time));
                    ivStatus.setVisibility(View.VISIBLE);
                    break;
                case DELIVERED:
                    ivStatus.setImageResource(R.drawable.ic_done_all);
                    ivStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.message_time));
                    ivStatus.setVisibility(View.VISIBLE);
                    break;
                case READ:
                    ivStatus.setImageResource(R.drawable.ic_done_all);
                    ivStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.whatsapp_blue));
                    ivStatus.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    ivStatus.setImageResource(R.drawable.ic_error);
                    ivStatus.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    ivStatus.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    // ViewHolder para mensajes recibidos
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMensaje;
        private TextView tvHora;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        public void bind(ChatBottomSheet.ChatMessage mensaje) {
            tvMensaje.setText(mensaje.getTexto());
            tvHora.setText(mensaje.getHora());
        }
    }
}