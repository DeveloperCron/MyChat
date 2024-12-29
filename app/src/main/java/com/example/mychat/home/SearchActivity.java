package com.example.mychat.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mychat.adapters.SearchViewAdapter;
import com.example.mychat.classes.StoredUser;
import com.example.mychat.constants.Tags;
import com.example.mychat.databinding.ActivitySearchBinding;
import com.example.mychat.services.FriendsService;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding _binding;
    private FriendsService _friendsService;
    private boolean isBound = false;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FriendsService.FriendsBinder binder = (FriendsService.FriendsBinder) service;
            _friendsService = binder.getService();
            isBound = true;

            compositeDisposable.add(_friendsService.getFriendsObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(friends -> {
                        initializeActivity(friends);
                        Log.d(Tags.Debugger.KEY, "Friends list received: " + friends.size());
                    }));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
    }

    protected void initializeActivity(List<StoredUser> _friendsList) {
        SearchViewAdapter searchViewAdapter = new SearchViewAdapter(_friendsList);
        _binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        _binding.recycler.setAdapter(searchViewAdapter);

        _binding.backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, FriendsService.class);
        bindService(serviceIntent, _serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(_serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}