package com.example.proyecto_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.model.User;

import java.util.List;

public class UserAdapter
        extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users;
    private final OnUserClickListener listener;

    public UserAdapter(
            List<User> users,
            OnUserClickListener listener
    ) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_user,
                        parent,
                        false
                );

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position
    ) {

        User user = users.get(position);

        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {

        users.clear();
        users.addAll(newUsers);

        notifyDataSetChanged();
    }

    class UserViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView textUserName;
        private final TextView textUserEmail;

        public UserViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            textUserName =
                    itemView.findViewById(
                            R.id.textUserName
                    );

            textUserEmail =
                    itemView.findViewById(
                            R.id.textUserEmail
                    );
        }

        public void bind(User user) {

            String name = user.getName();

            if (name == null ||
                    name.trim().isEmpty()) {

                name = "Usuario";
            }

            textUserName.setText(name);
            textUserEmail.setText(user.getEmail());

            itemView.setOnClickListener(
                    view -> listener.onUserClick(user)
            );
        }
    }

    public interface OnUserClickListener {

        void onUserClick(User user);
    }
}