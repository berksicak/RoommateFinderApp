package com.example.roommatefinderapp.activities;

import static android.content.ContentValues.TAG;
import static com.example.roommatefinderapp.activities.MainActivity.currentUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.roommatefinderapp.entities.User;
import com.example.roommatefinderapp.enums.Status;
import com.example.roommatefinderapp.enums.Year;
import com.example.roommatefinderapp.fragments.HomeFragment;
import com.example.roommatefinderapp.fragments.NotificationsFragment;
import com.example.roommatefinderapp.fragments.ProfileFragment;
import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.databinding.ActivityBottomNavBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class BottomNavActivity extends AppCompatActivity {
    private ActivityBottomNavBinding binding;
    private Button logoutBtn;
    private FirebaseFirestore firebaseFirestore;
    private User currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();


        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BottomNavActivity.this, MainActivity.class);
                BottomNavActivity.this.startActivity(intent);
            }
        });
        Intent intent = getIntent();

        if (intent.hasExtra("location")) {
            String location = intent.getStringExtra("location");
            getCurrentUser();
            updateUserDB(location);
            replaceFragment(new ProfileFragment());
        }
        else {
            replaceFragment(new HomeFragment());
        }
        binding.navView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_notifications:
                    replaceFragment(new NotificationsFragment());
                    break;
                case R.id.navigation_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void updateUserDB(String location) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", currentUser.getUserID());
        hashMap.put("name", currentUser.getName());
        hashMap.put("surname", currentUser.getSurname());
        hashMap.put("phoneNo", currentUser.getPhoneNo());
        hashMap.put("year", currentUser.getYear().getYear());
        hashMap.put("department", currentUser.getDepartment());
        hashMap.put("status", currentUser.getStatus().getStatus());
        hashMap.put("distanceToCampus", currentUser.getDistanceToCampus());
        hashMap.put("duration", currentUser.getDuration());
        hashMap.put("location", location);
        firebaseFirestore.collection("users").document(getUID())
                .set(hashMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            System.out.println("Clouda yükleme basarili");
                        }
                        else
                            System.out.println("clouda yükleme basarisiz");
                    }
                });
    }
    public String getUID(){
        SharedPreferences sharedPref = this.getSharedPreferences("com.example.roommatefinderapp", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("userID", null);
        System.out.println(uid);
        return uid;
    }

    public void getCurrentUser(){
        String uid = getUID();
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Year year = null;
                    Status status = null;
                    for (Year y : Year.values()){
                        if(y.getYear().equals(document.get("year"))){
                            year = y;
                        }
                    }
                    for(Status s: Status.values()){
                        if(s.getStatus().equals(document.get("status"))){
                            status = s;
                        }
                    }
                    currentUser = new User(uid, document.get("name").toString(), document.get("surname").toString(), document.get("phoneNo").toString(), year
                            , document.get("department").toString(), status, Integer.valueOf(document.get("distanceToCampus").toString()),
                            document.get("duration").toString());


                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    }

