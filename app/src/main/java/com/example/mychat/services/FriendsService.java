package com.example.mychat.services;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mychat.classes.StoredUser;
import com.example.mychat.constants.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FriendsService extends Service {
    private final IBinder binder = new FriendsBinder();
    private FirebaseFirestore _firestore;
    private List<StoredUser> _friends;
    private BehaviorSubject<List<StoredUser>> friendsSubject;


    public class FriendsBinder extends Binder {
        public FriendsService getService() {
            return FriendsService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _firestore = FirebaseFirestore.getInstance();
        _friends = new ArrayList<>();
        friendsSubject = BehaviorSubject.createDefault(_friends);  // Initialize with an empty list
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> friendsList = intent.getStringArrayListExtra("friendsList");
        Log.d(Tags.DEBUGGER.DEBUG_KEY, String.valueOf(friendsList.size()));
        if (friendsList != null && !friendsList.isEmpty()) {
            fetchFriends(friendsList);
        }
        return START_NOT_STICKY;
    }

    private void fetchFriends(List<String> friendsList) {
        for (String friendId : friendsList) {
            _firestore.collection(Tags.FIREBASE_TAGS.USERS_COLLECTION).document(friendId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String first_name = documentSnapshot.getString(Tags.USER_FIELDS.FIRST_NAME);
                                String second_name = documentSnapshot.getString(Tags.USER_FIELDS.SECOND_NAME);
                                String email = documentSnapshot.getString(Tags.USER_FIELDS.EMAIL);
                                String photo = documentSnapshot.getString(Tags.USER_FIELDS.PROFILE_PICTURE);

                                StoredUser friend = new StoredUser(photo, first_name, second_name, email, friendId);
                                _friends.add(friend);
                            }
                        }
                    });

        }

        friendsSubject.onNext(_friends);
    }

    public BehaviorSubject<List<StoredUser>> getFriendsObservable() {
        return friendsSubject;  // Allow subscribers to observe changes
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}