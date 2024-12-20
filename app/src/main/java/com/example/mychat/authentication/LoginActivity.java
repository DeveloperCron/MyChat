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
import com.example.mychat.utils.GetTextUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding _binding;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _fireStoreDatabase;
    private SharedPreferences _sharedPreferences;
    private UserSingleton _userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _firebaseAuth = FirebaseAuth.getInstance();
        _fireStoreDatabase = FirebaseFirestore.getInstance();
        _userSingleton = UserSingleton.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();


        // Getting data from sharedPreferences
        String sharedEmail = getFromShared(Tags.SharedPreferencesTags.EMAIL_TAG);
        String sharedPassword = getFromShared(Tags.SharedPreferencesTags.PASSWORD_TAG);

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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Tags.FirebaseTags.FIREBASE_AUTH_TAG, "signInWithEmail:success");
                            // You can navigate to another screen or update UI here
                            FirebaseUser _currentUser = _firebaseAuth.getCurrentUser();

                            assert _currentUser != null;
                            initializeUser(_currentUser);

                            insertInShared(email, password);
                            startActivity(new Intent(this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(Tags.FirebaseTags.FIREBASE_AUTH_TAG, "signInWithEmail:failure", task.getException());
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
        _sharedPreferences = getApplicationContext().getSharedPreferences(Tags.SharedPreferencesTags.AUTHENTICATION_TAG, Context.MODE_PRIVATE);
        if (_sharedPreferences.contains(tag)) {
            return _sharedPreferences.getString(tag, "");
        }
        return null;
    }

    protected void insertInShared(String email, String password){
        _sharedPreferences = getApplicationContext().getSharedPreferences(Tags.SharedPreferencesTags.AUTHENTICATION_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putString(Tags.SharedPreferencesTags.EMAIL_TAG, email);
        _editor.putString(Tags.SharedPreferencesTags.PASSWORD_TAG, password);
        _editor.apply();
    }
    protected void initializeUser(FirebaseUser _currentUser) {
        DocumentReference userDocRef = _fireStoreDatabase.collection(Tags.FirebaseTags.USERS_COLLECTION).document(_currentUser.getUid());
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    _userSingleton = documentSnapshot.toObject(UserSingleton.class);
                    Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Retrieved document snapshot for " + _currentUser.getUid() + " Successfully");
                } else {
                    Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Failed to retrieve document");
                }
            } else {
                Log.w(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Error getting document.", task.getException());}
        });
    }
}