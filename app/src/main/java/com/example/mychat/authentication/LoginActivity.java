package com.example.mychat.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.databinding.ActivityLoginBinding;
import com.example.mychat.home.HomeActivity;
import com.example.mychat.services.FriendsService;
import com.example.mychat.utils.GetTextUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding _binding;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _firestore;
    private SharedPreferences _sharedPreferences;
    private UserSingleton _userSingleton;
    private Intent _friendsServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _firebaseAuth = FirebaseAuth.getInstance();
        _firestore = FirebaseFirestore.getInstance();
        _userSingleton = UserSingleton.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _friendsServiceIntent = new Intent(this, FriendsService.class);

        // Getting data from sharedPreferences
        String sharedEmail = getFromShared(Tags.SharedPreferences.EMAIL);
        String sharedPassword = getFromShared(Tags.SharedPreferences.PASSWORD);

        if (sharedEmail != null && sharedPassword != null) {
           _binding.emailInput.getEditText().setText(sharedEmail);
           _binding.passwordInput.getEditText().setText(sharedPassword);
        }

        _binding.loginButton.setOnClickListener(v -> {
            String email = GetTextUtils.getTextFromInput(_binding.emailInput);
            String password = GetTextUtils.getTextFromInput(_binding.passwordInput);

            // Firebase authentication
            _firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser _currentUser = _firebaseAuth.getCurrentUser();

                            assert _currentUser != null;
                            initializeUser(_currentUser);

                            insertInShared(email, password);
                            startService(_friendsServiceIntent);
                            startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(Tags.FirebaseErrors.WRONG_PASSWORD, "signInWithEmail:failure, maybe user entered wrong credentials...", task.getException());
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        _binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });
    }

    protected String getFromShared(String tag){
        _sharedPreferences = getApplicationContext().getSharedPreferences(Tags.SharedPreferences.AUTHENTICATION, Context.MODE_PRIVATE);
        if (_sharedPreferences.contains(tag)) {
            return _sharedPreferences.getString(tag, "");
        }
        return null;
    }

    protected void insertInShared(String email, String password){
        _sharedPreferences = getApplicationContext().getSharedPreferences(Tags.SharedPreferences.AUTHENTICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putString(Tags.SharedPreferences.EMAIL, email);
        _editor.putString(Tags.SharedPreferences.PASSWORD, password);
        _editor.apply();
    }
    protected void initializeUser(FirebaseUser _currentUser) {
        DocumentReference userDocRef = _firestore.collection(Tags.Firebase.USERS_COLLECTION).document(_currentUser.getUid());
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    _userSingleton = documentSnapshot.toObject(UserSingleton.class);
                    ArrayList<String> _friends = (ArrayList<String>) documentSnapshot.get(Tags.UserFields.FRIENDS);
                    _friendsServiceIntent.putStringArrayListExtra("friendsList", (ArrayList<String>) _friends );
                } else {
                    Log.d(Tags.FirebaseErrors.DOCUMENT_NOT_FOUND, "Failed to retrieve document");
                }
            } else {
                Log.w(Tags.FirebaseErrors.DOCUMENT_NOT_FOUND, "Error getting document.", task.getException());
            }
        });
    }
}