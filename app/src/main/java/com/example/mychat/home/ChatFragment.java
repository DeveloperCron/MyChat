package com.example.mychat.home;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.adapters.UserCardAdapter;
import com.example.mychat.classes.StoredUser;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.databinding.FragmentChatBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private FirebaseFirestore _firestore;
    private List<StoredUser> _storedUsers;
    private UserCardAdapter _adapter;
    private UserSingleton _userSingleton;
    private FragmentChatBinding _binding;

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _binding.chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.chatsList.setAdapter(_adapter);

        Log.d(Tags.Debugger.DEBUG_KEY, String.valueOf(_userSingleton.getName()));
        _binding.welcomeTextView.setText("Welcome back " + _userSingleton.getFirstName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _firestore = FirebaseFirestore.getInstance();
        _userSingleton = UserSingleton.getInstance();
        _storedUsers = new ArrayList<>();
        _adapter = new UserCardAdapter(_storedUsers);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentChatBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    protected void fetchByKey(String key){}
}
