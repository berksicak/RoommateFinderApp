package com.example.roommatefinderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.entities.User;
import com.example.roommatefinderapp.enums.Status;
import com.example.roommatefinderapp.enums.Year;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView nameText, departmentText, yearText, durationText, distanceText, statusText;
    private Button showMapBtn, matchBtn;
    private ImageView imageView;
    private DocumentReference collectionReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        nameText = (TextView) findViewById(R.id.text_name);
        departmentText = (TextView) findViewById(R.id.text_department);
        yearText = (TextView) findViewById(R.id.text_year);
        durationText = (TextView) findViewById(R.id.text_duration);
        distanceText  = (TextView) findViewById(R.id.text_distance);
        statusText = (TextView) findViewById(R.id.text_status);
        showMapBtn = (Button) findViewById(R.id.show_map_button);
        matchBtn = (Button) findViewById(R.id.match_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        Intent intent = getIntent();

        if (intent.hasExtra("lookAtToProfileID")) {
            String data = intent.getStringExtra("lookAtToProfileID");
            collectionReference = FirebaseFirestore.getInstance().collection("users").document(data);

            Task<DocumentSnapshot> query = collectionReference.get();
            query.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
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
                            User user = new User(data, document.get("name").toString(), document.get("surname").toString(), document.get("phoneNo").toString(), year
                                    , document.get("department").toString(), status, Integer.valueOf(document.get("distanceToCampus").toString()),
                                    document.get("duration").toString());
                            nameText.setText(user.getName()+" "+ user.getSurname());
                            departmentText.setText(user.getDepartment());
                            yearText.setText(user.getYear().getYear());
                            durationText.setText(user.getDuration());
                            distanceText.setText(String.valueOf(user.getDistanceToCampus()));
                            statusText.setText(user.getStatus().getStatus());

                            StorageReference photoRef = storageReference.child("users/"+ data );

                            final long ONE_MEGABYTE = 1024 * 1024;
                            photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bitmap);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                    }
                }
            });
        }
    }
}