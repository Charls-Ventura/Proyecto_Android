package com.example.proyecto_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.adapter.UserAdapter;
import com.example.proyecto_android.model.User;
import com.example.proyecto_android.viewmodel.AuthViewModel;
import com.example.proyecto_android.viewmodel.UsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerUsers;
    private Button btnLogout;

    private UserAdapter userAdapter;

    private AuthViewModel authViewModel;
    private UsersViewModel usersViewModel;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        initializeViews();

        if (!loadCurrentUser()) {
            return;
        }

        setupRecyclerView();
        setupViewModels();
        setupLogout();
    }

    private void initializeViews() {

        recyclerUsers =
                findViewById(R.id.recyclerUsers);

        btnLogout =
                findViewById(R.id.btnLogout);
    }

    private boolean loadCurrentUser() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (currentUser == null) {

            goToLogin();

            return false;
        }

        currentUserId = currentUser.getUid();

        return true;
    }

    private void setupRecyclerView() {

        userAdapter = new UserAdapter(
                new ArrayList<>(),
                this::openChat
        );

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerUsers.setAdapter(userAdapter);
    }

    private void setupViewModels() {

        authViewModel =
                new ViewModelProvider(this)
                        .get(AuthViewModel.class);

        usersViewModel =
                new ViewModelProvider(this)
                        .get(UsersViewModel.class);

        usersViewModel.getUsers().observe(
                this,
                users -> {

                    if (users != null) {
                        userAdapter.updateUsers(users);
                    }
                }
        );

        usersViewModel.getErrorMessage().observe(
                this,
                error -> {

                    if (error == null ||
                            error.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            error,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        usersViewModel.startListeningForUsers(
                currentUserId
        );
    }

    private void setupLogout() {

        btnLogout.setOnClickListener(view -> {

            authViewModel.logout();

            goToLogin();
        });
    }

    private void openChat(User user) {

        if (user == null ||
                user.getUid() == null ||
                user.getUid().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "No se pudo abrir la conversación.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        UsersActivity.this,
                        ChatActivity.class
                );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_ID,
                user.getUid()
        );

        intent.putExtra(
                ChatActivity.EXTRA_RECEIVER_NAME,
                user.getName()
        );

        startActivity(intent);
    }

    private void goToLogin() {

        Intent intent =
                new Intent(
                        UsersActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}