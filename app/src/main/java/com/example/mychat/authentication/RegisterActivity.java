package com.example.mychat.authentication;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychat.R;
import com.example.mychat.classes.User;
import com.example.mychat.databinding.ActivityRegisterBinding;
import com.example.mychat.utils.GetTextUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding _binding;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _firebaseFirestore;
    private User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _firebaseAuth = FirebaseAuth.getInstance();
        _firebaseFirestore = FirebaseFirestore.getInstance();
        _user = User.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = GetTextUtils.getTextFromInput(_binding.emailInput);
                String password = GetTextUtils.getTextFromInput(_binding.passwordInput);
            }
        });
    }
}