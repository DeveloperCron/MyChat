package com.example.mychat.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.classes.UserSingleton;
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
    private FirebaseFirestore _firestore;
    private UserSingleton _userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _firebaseAuth = FirebaseAuth.getInstance();
        _firestore = FirebaseFirestore.getInstance();
        _userSingleton = UserSingleton.getInstance();
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
                            FirebaseUser _currentUser = _firebaseAuth.getCurrentUser();
                            initializeUser(_currentUser);

                            startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            Log.w(Tags.FirebaseErrors.WRONG_PASSWORD, "signInWithEmail:failure, maybe user entered wrong credentials", task.getException());
                        }
                    }
            );
        });

        _binding.loginButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
    }

    protected void initializeUser(FirebaseUser _firebaseUser) {
        String _userUID = _firebaseUser.getUid(); // Get UID as a string

        Map<String, String> _user = new HashMap<>();
        _user.put("first_name", _firebaseUser.getEmail());
        _user.put("second_name", _firebaseUser.getEmail());
        _user.put("email", _firebaseUser.getEmail());
        _userSingleton.setFirstName(_firebaseUser.getEmail());
        _userSingleton.setSecondName(_firebaseUser.getEmail());
        _userSingleton.setEmail(_firebaseUser.getEmail());
        _userSingleton.setUid(_userUID);
//        _user.put("photo_url", String.valueOf(_firebaseUser.getPhotoUrl()));

        _firestore.collection(Tags.Firebase.USERS_COLLECTION)
                .document(_userUID)  // Use UID directly as the document ID
                .set(_user)
                .addOnSuccessListener(unused -> Log.d(Tags.FirebaseErrors.UNAVAILABLE, "initializeUser:success"))
                .addOnFailureListener(e -> Log.w(Tags.FirebaseErrors.UNAVAILABLE, "initializeUser:failure", e));
    }

}