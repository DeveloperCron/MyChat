package com.example.mychat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.authentication.LoginActivity;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.home.HomeActivity;
import com.example.mychat.services.FriendsService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private UserSingleton user;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = UserSingleton.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView logoView = findViewById(R.id.logoView);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoView, "scaleX", 1f, 1.1f);
        scaleX.setDuration(500);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoView, "scaleY", 1f, 1.1f);
        scaleY.setDuration(500);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);

        // AnimatorSet to play both animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();

        Intent friendsServiceIntent = new Intent(this, FriendsService.class);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Disposable disposable = initializeUser(currentUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            documentSnapshot -> {
                                user.setEmail(documentSnapshot.getString(Tags.UserFields.EMAIL));
                                user.setFirstName(documentSnapshot.getString(Tags.UserFields.FIRST_NAME));
                                user.setSecondName(documentSnapshot.getString(Tags.UserFields.PROFILE_PICTURE));
                                user.setUid(currentUser.getUid());

                                ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get(Tags.UserFields.FRIENDS);
                                friendsServiceIntent.putStringArrayListExtra("friendsList", (ArrayList<String>) friends);
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);

                                startService(friendsServiceIntent);
                                startActivity(intent);
                            },
                            throwable -> {
                                Log.d(Tags.FirebaseErrors.UNAVAILABLE, "Failed to initialize user.");
                            }
                    );

            compositeDisposable.add(disposable);
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    protected Single<DocumentSnapshot> initializeUser(FirebaseUser currentUser) {
        DocumentReference userDocRef = firebaseFirestore.collection(Tags.Firebase.USERS_COLLECTION).document(currentUser.getUid());
        return Single.create(emitter -> {
            userDocRef
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Log.d(Tags.FirebaseErrors.DOCUMENT_NOT_FOUND, "Retrieved document snapshot for " + currentUser.getUid() + " Successfully");
                                emitter.onSuccess(documentSnapshot);
                            }
                            }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}