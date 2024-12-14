package com.example.mychat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mychat.authentication.LoginActivity;
import com.example.mychat.classes.UserSingleton;
import com.example.mychat.constants.Tags;
import com.example.mychat.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private UserSingleton user;
    private CompositeDisposable _compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = UserSingleton.getInstance();
        _compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Disposable disposable = initializeUser(currentUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            documentSnapshot -> {
                                user.setEmail(documentSnapshot.getString(Tags.USER_FIELDS.EMAIL));
                                user.setFirstName(documentSnapshot.getString(Tags.USER_FIELDS.FIRST_NAME));
                                user.setSecondName(documentSnapshot.getString(Tags.USER_FIELDS.SECOND_NAME));
                                user.setPhotoUrl(documentSnapshot.getString(Tags.USER_FIELDS.PROFILE_PICTURE));
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            },
                            throwable -> {
                                Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Failed to initialize user.");
                            }
                    );

            _compositeDisposable.add(disposable);
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    protected Single<DocumentSnapshot> initializeUser(FirebaseUser currentUser) {
        DocumentReference userDocRef = firebaseFirestore.collection(Tags.FirebaseTags.USERS_COLLECTION).document(currentUser.getUid());
        return Single.create(emitter -> {
            userDocRef
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Log.d(Tags.FirebaseTags.FIREBASE_FIRESTORE_TAG, "Retrieved document snapshot for " + currentUser.getUid() + " Successfully");
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
        _compositeDisposable.clear();
    }
}