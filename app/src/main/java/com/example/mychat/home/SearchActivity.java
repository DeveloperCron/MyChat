package com.example.mychat.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.adapters.SearchViewAdapter;
import com.example.mychat.classes.ChatUser;
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

    protected void initializeActivity(List<ChatUser> _friendsList) {
        SearchViewAdapter searchViewAdapter = new SearchViewAdapter(_friendsList);
        _binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        _binding.recycler.setAdapter(searchViewAdapter);

        _binding.backButton.setOnClickListener(v -> finish());
        _binding.recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(_binding.recycler.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    // Handle item click here
                    ChatUser chatUser = _friendsList.get(position);
                    Log.d(Tags.Debugger.KEY, "Item clicked at position: " + chatUser.getUsername());
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Optional: Handle other touch events if needed
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Optional: Handle disallow intercept if needed
            }
        });
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