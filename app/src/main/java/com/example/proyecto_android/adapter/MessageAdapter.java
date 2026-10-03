package com.example.proyecto_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<Message> messages;
    private final String currentUserId;

    public MessageAdapter(
            List<Message> messages,
            String currentUserId
    ) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        }

        return TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        if (viewType == TYPE_SENT) {

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(
                            R.layout.item_message_sent,
                            parent,
                            false
                    );

            return new SentMessageViewHolder(view);
        }

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_message_received,
                        parent,
                        false
                );

        return new ReceivedMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {

        Message message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {

            ((SentMessageViewHolder) holder).bind(message);

        } else if (holder instanceof ReceivedMessageViewHolder) {

            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {

        messages.clear();
        messages.addAll(newMessages);

        notifyDataSetChanged();
    }

    private String formatTime(long timestamp) {

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                );

        return formatter.format(
                new Date(timestamp)
        );
    }

    class SentMessageViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView textMessage;
        private final TextView textTime;

        public SentMessageViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            textMessage =
                    itemView.findViewById(R.id.textMessage);

            textTime =
                    itemView.findViewById(R.id.textTime);
        }

        public void bind(Message message) {

            textMessage.setText(message.getText());
            textTime.setText(
                    formatTime(message.getTimestamp())
            );
        }
    }

    class ReceivedMessageViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView textMessage;
        private final TextView textTime;

        public ReceivedMessageViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            textMessage =
                    itemView.findViewById(R.id.textMessage);

            textTime =
                    itemView.findViewById(R.id.textTime);
        }

        public void bind(Message message) {

            textMessage.setText(message.getText());
            textTime.setText(
                    formatTime(message.getTimestamp())
            );
        }
    }
}