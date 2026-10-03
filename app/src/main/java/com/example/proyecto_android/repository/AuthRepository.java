package com.example.proyecto_android.repository;

import com.example.proyecto_android.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    public AuthRepository() {

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void register(
            String name,
            String email,
            String password,
            AuthCallback callback
    ) {

        firebaseAuth
                .createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        String error =
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Error al registrar usuario";

                        callback.onError(error);
                        return;
                    }

                    FirebaseUser firebaseUser =
                            firebaseAuth.getCurrentUser();

                    if (firebaseUser == null) {
                        callback.onError(
                                "No se pudo obtener el usuario registrado"
                        );
                        return;
                    }

                    User user = new User(
                            firebaseUser.getUid(),
                            name,
                            email
                    );

                    firestore.collection("users")
                            .document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                callback.onSuccess();
                            })
                            .addOnFailureListener(exception -> {

                                firebaseUser.delete()
                                        .addOnCompleteListener(deleteTask -> {

                                            firebaseAuth.signOut();

                                            callback.onError(
                                                    "No se pudo completar el registro. Intenta nuevamente."
                                            );
                                        });
                            });
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