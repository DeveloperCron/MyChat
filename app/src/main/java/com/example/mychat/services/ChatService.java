package com.example.mychat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mychat.constants.Tags;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatService extends Service {
    FirebaseDatabase _database;
    private final String _uid;

    public ChatService(String uid) {
        _database = FirebaseDatabase.getInstance();
        _uid = uid;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseReference _ref = _database.getReference(Tags.Firebase.CHATS);
        _ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String chatKey = snap.child(Tags.Firebase.CHAT_KEY).getValue(String.class);
                    if (chatKey != null && chatKey.contains(_uid)) {
                        Log.d(Tags.Debugger.KEY, String.valueOf(snap));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Tags.Debugger.KEY, String.valueOf(error));
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}