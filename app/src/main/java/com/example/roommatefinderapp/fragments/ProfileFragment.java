package com.example.roommatefinderapp.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.example.roommatefinderapp.activities.MainActivity.currentUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.activities.MapsActivity;
import com.example.roommatefinderapp.activities.UpdatePasswordActivity;
import com.example.roommatefinderapp.entities.User;
import com.example.roommatefinderapp.enums.Status;
import com.example.roommatefinderapp.enums.Year;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private Spinner yearSpinner, statusSpinner;
    private ImageView imageView;
    private EditText nameInput, surnameInput, phoneNoInput, departmentInput, rangeInput, durationInput;
    private Button uploadImageBtn, saveBtn, changePassowrdBtn, addLocationBtn;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int GALLERY_PERM_CODE = 103;
    private Bitmap myImage ;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        yearSpinner = (Spinner) view.findViewById(R.id.yearSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.years_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        statusSpinner = (Spinner) view.findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusSpinner.setAdapter(adapter1);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        nameInput = (EditText) view.findViewById(R.id.nameInput);
        surnameInput = (EditText) view.findViewById(R.id.surnameInput);
        phoneNoInput = (EditText) view.findViewById(R.id.phoneNoInput);
        uploadImageBtn = (Button) view.findViewById(R.id.loadImageButton);
        saveBtn = (Button) view.findViewById(R.id.saveButton);
        departmentInput = (EditText) view.findViewById(R.id.departmentInput);
        rangeInput = (EditText) view.findViewById(R.id.rangeInput);
        durationInput = (EditText) view.findViewById(R.id.durationInput);
        changePassowrdBtn = (Button) view.findViewById(R.id.changePassword);
        addLocationBtn = (Button) view.findViewById(R.id.addLocation);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //to fill inputs with the data from shared pref
        getCurrentUser();

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context1 = view.getContext();
                Intent intent = new Intent(context1, MapsActivity.class);
                context1.startActivity(intent);
            }
        });

        changePassowrdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), UpdatePasswordActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString();
                String surname = surnameInput.getText().toString();
                String phoneNo = phoneNoInput.getText().toString();
                String yearstr = yearSpinner.getSelectedItem().toString();
                String department = departmentInput.getText().toString();
                String statusstr = statusSpinner.getSelectedItem().toString();
                int range;
                String duration = durationInput.getText().toString();
                Year year = null;
                Status status = null;
                for (Year y : Year.values()){
                    if(y.getYear().equals(yearstr)){
                        year = y;
                    }
                }
                for(Status s: Status.values()){
                    if(s.getStatus().equals(statusstr))
                        status = s;
                }
                if(name.length()>1 && surname.length()>1 && department.length()>0 && duration.length()>0) {
                    if (isNumeric(phoneNo) && phoneNo.length()==11){
                        if(isNumeric(rangeInput.getText().toString())){
                            range = Integer.valueOf(rangeInput.getText().toString());
                            User newUser = new User(firebaseAuth.getCurrentUser().getUid(), name, surname, phoneNo, year, department, status, range, duration);
                            updateUserDB(newUser);
                        }
                        else
                            Toast.makeText(getContext(), "Uzaklık sadece tam sayı kabul etmektedir.",Toast.LENGTH_SHORT);
                    }
                    else
                        Toast.makeText(getContext(), "Telefon numarasını başına 0 koyarak rakamla giriniz.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), "Bütün bilgiler doldurulmalıdır.", Toast.LENGTH_SHORT).show();

            }
        });

        activityResultLauncher = registerForActivityResult (new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageBitmap(bitmap);
                                myImage = bitmap;
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                uploadImageBtn.setText("FOTOĞRAF DEĞİŞTİR");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);
            }
        });

        return view;
    }

    private void askGalleryPermissions(){
        if (ContextCompat.checkSelfPermission (getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }else{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(pickPhoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);
            } else {
                Toast.makeText(getActivity(), "Gallery Permission is Required to Select an Image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public void updateUserDB(User user){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUserID());
        hashMap.put("name", user.getName());
        hashMap.put("surname", user.getSurname());
        hashMap.put("phoneNo", user.getPhoneNo());
        hashMap.put("year", user.getYear().getYear());
        hashMap.put("department", user.getDepartment());
        hashMap.put("status", user.getStatus().getStatus());
        hashMap.put("distanceToCampus", user.getDistanceToCampus());
        hashMap.put("duration", user.getDuration());


        //upload image to storage
        if(myImage!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.child("users/"+user.getUserID()).putBytes(data);

        }

        firebaseFirestore.collection("users").document(user.getUserID())
                .set(hashMap)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
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
        SharedPreferences sharedPref = getActivity().getSharedPreferences("com.example.roommatefinderapp", Context.MODE_PRIVATE);
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
                    if (document.get("department") == null){
                        currentUser = new User(getUID(), document.get("name").toString(), document.get("surname").toString(), null, null
                                , null, null, 0,
                                null);
                        return;
                    }

                    currentUser = new User(getUID(), document.get("name").toString(), document.get("surname").toString(), document.get("phoneNo").toString(), year
                            , document.get("department").toString(), status, Integer.valueOf(document.get("distanceToCampus").toString()),
                            document.get("duration").toString());

                    nameInput.setText(currentUser.getName());
                    surnameInput.setText(currentUser.getSurname());
                    phoneNoInput.setText(currentUser.getPhoneNo());
                    yearSpinner.setSelection(getYearNo(year));
                    departmentInput.setText(currentUser.getDepartment());
                    statusSpinner.setSelection(getStatusNo(status));
                    rangeInput.setText(String.valueOf(currentUser.getDistanceToCampus()));
                    durationInput.setText(currentUser.getDuration());

                    StorageReference photoRef = storageReference.child("users/"+ uid );

                    final long ONE_MEGABYTE = 1024 * 1024;
                    photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bitmap);
                            myImage = bitmap;
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

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

    private int getYearNo(Year year){
        if(year.getYear().equals("Hazırlık"))
            return 0;
        else if (year.getYear().equals("1. Sınıf"))
            return 1;
        else if (year.getYear().equals("2. Sınıf"))
            return 2;
        else if (year.getYear().equals("3. Sınıf"))
            return 3;
        else
            return 4;
    }

    private int getStatusNo(Status status){
        if(status.getStatus().equals("Kalacak Ev/Oda arıyor"))
            return 0;
        else if(status.getStatus().equals("Ev/Oda arkadaşı arıyor"))
            return 1;
        else
            return 2;
    }
}