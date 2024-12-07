package com.example.mychat.home;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mychat.R;
import com.example.mychat.classes.User;
import com.example.mychat.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding _binding;
    private ChatFragment _chatFragment;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _firestoreDatabase;
    private FragmentManager _fragmentManager;
    private User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _chatFragment = new ChatFragment();

        _fragmentManager = getSupportFragmentManager();
        _firebaseAuth = FirebaseAuth.getInstance();
        _firestoreDatabase = FirebaseFirestore.getInstance();
        _user = User.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _fragmentManager.beginTransaction()
                .replace(_binding.fragmentContainerView.getId(), _chatFragment, null)
                .addToBackStack(null)
                .commit();

        _binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.chatItem) {
               _fragmentManager.beginTransaction()
                       .replace(_binding.fragmentContainerView.getId(), _chatFragment, null)
                       .addToBackStack(null)
                       .commit();
            }

            return true;
        });

    }
}