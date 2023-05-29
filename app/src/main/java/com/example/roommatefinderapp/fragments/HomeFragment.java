package com.example.roommatefinderapp.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.adapters.RecyclerAdapter;
import com.example.roommatefinderapp.entities.User;
import com.example.roommatefinderapp.enums.Status;
import com.example.roommatefinderapp.enums.Year;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private CollectionReference collectionReference;
    private List<User> userList;
    private RecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        collectionReference = FirebaseFirestore.getInstance().collection("users");

        Task<QuerySnapshot> query = collectionReference.get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.get("name") != null && document.get("surname") != null
                                && document.get("phoneNo") != null && document.get("status") != null && document.get("department") != null
                                && document.get("year") != null && document.get("distanceToCampus") != null && document.get("duration") != null) {
                            Year year = null;
                            Status status = null;
                            for (Year y : Year.values()) {
                                if (y.getYear().equals(document.get("year"))) {
                                    year = y;
                                }
                            }
                            for (Status s : Status.values()) {
                                if (s.getStatus().equals(document.get("status")))
                                    status = s;
                            }
                            User user = new User(getUID(), document.get("name").toString(), document.get("surname").toString(), document.get("phoneNo").toString(), year
                                    , document.get("department").toString(), status, Integer.valueOf(document.get("distanceToCampus").toString()),
                                    document.get("duration").toString());
                            userList.add(user);
                        }
                    }
                    adapter = new RecyclerAdapter(userList, getContext());
                    recyclerView.setHasFixedSize(true);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return view;
    }


    public String getUID(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("com.example.roommatefinderapp", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("userID", null);
        System.out.println(uid);
        return uid;
    }
}