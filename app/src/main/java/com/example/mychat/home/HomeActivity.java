package com.example.mychat.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mychat.R;
import com.example.mychat.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding _binding;
    private ChatFragment _chatFragment;
    private FirebaseAuth _firebaseAuth;
    private FirebaseFirestore _firestore;
    private FragmentManager _fragmentManager;
//    private UserSingleton _userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _chatFragment = new ChatFragment();
        _firebaseAuth = FirebaseAuth.getInstance();
        _fragmentManager = getSupportFragmentManager();
        _firestore = FirebaseFirestore.getInstance();
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