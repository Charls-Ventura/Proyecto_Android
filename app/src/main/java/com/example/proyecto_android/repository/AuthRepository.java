package com.example.proyecto_android.repository;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error al registrar usuario";

                        callback.onError(error);
                    }
                });
    }

    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error al iniciar sesión";

                        callback.onError(error);
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }
}