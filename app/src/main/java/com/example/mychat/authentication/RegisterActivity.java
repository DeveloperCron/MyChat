package com.example.mychat.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.constants.Tags;
import com.example.mychat.databinding.ActivityRegisterBinding;
import com.example.mychat.home.HomeActivity;
import com.example.mychat.utils.GetTextUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding _binding;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _firebaseAuth = FirebaseAuth.getInstance();
        _firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _binding.registerButton.setOnClickListener(v -> {
            String email = GetTextUtils.getTextFromInput(_binding.emailInput);
            String password = GetTextUtils.getTextFromInput(_binding.passwordInput);

            _firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    this, task -> {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(Tags.FirebaseTags.FIREBASE_AUTH_TAG, "createUserWithEmailAndPassword:success");
                            FirebaseUser _currentUser = _firebaseAuth.getCurrentUser();

                            assert _currentUser != null;
                            initializeUser(_currentUser);

                            startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            Log.w(Tags.FirebaseTags.FIREBASE_AUTH_TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
            );
        });

        _binding.loginButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    protected void initializeUser(FirebaseUser _firebaseUser) {
        String _userUID = _firebaseUser.getUid(); // Get UID as a string

        Map<String, String> _user = new HashMap<>();
        _user.put("name", _firebaseUser.getEmail());
        _user.put("photo_url", String.valueOf(_firebaseUser.getPhotoUrl()));

        _firebaseFirestore.collection(Tags.FirebaseTags.USERS_COLLECTION)
                .document(_userUID)  // Use UID directly as the document ID
                .set(_user)
                .addOnSuccessListener(unused -> Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "initializeUser:success"))
                .addOnFailureListener(e -> Log.w(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "initializeUser:failure", e));
    }

}