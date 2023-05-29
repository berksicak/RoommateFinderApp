package com.example.roommatefinderapp.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private Button signupButton, loadImageButton;
    private EditText nameInput, surnameInput, passwordInput, emailInput;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int GALLERY_PERM_CODE = 103;
    private FirebaseAuth firebaseAuth;
    private Bitmap myImage ;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameInput = (EditText) findViewById(R.id.nameInputOnSignup);
        passwordInput = (EditText) findViewById(R.id.passwordInputOnSignup);
        surnameInput = (EditText) findViewById(R.id.surnameInputOnSignup);
        emailInput = (EditText) findViewById(R.id.emailInput);
        signupButton = (Button) findViewById(R.id.signupButtonOnSignup);
        loadImageButton = (Button) findViewById(R.id.loadImageButton);
        imageView = (ImageView) findViewById(R.id.imageViewOnSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        //signup actions
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString().trim();
                String surname = surnameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                if (name.length() > 0 && surname.length() > 0) {
                    if (isEmail(email)) {
                        if (password.length() >= 8 && !password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(SignupActivity.this, "Kayıt işlemini tamamlamak için gelen e-posta üzerinden doğrulayınız", Toast.LENGTH_SHORT).show();

                                                                String id = firebaseAuth.getCurrentUser().getUid();
                                                                User user1 = new User(id, name, surname, email, myImage);

                                                                writeUserToFirebase(user1);

                                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                                intent.putExtra("email", user.getEmail());
                                                                SignupActivity.this.startActivity(intent);

                                                            } else {
                                                                Toast.makeText(SignupActivity.this, "Kayıt işlemi başarısız", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else
                                                Toast.makeText(SignupActivity.this, "Kayıt işemi başarısız, tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else
                            Toast.makeText(SignupActivity.this, "Şifre Büyük ve Küçük Harf içermeli ayrıca minimum 8 karakterden oluşmalı.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignupActivity.this, "Geçerli email giriniz.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SignupActivity.this, "Bütün bilgiler doldurulmalıdır.", Toast.LENGTH_SHORT).show();
            }
        });


        activityResultLauncher = registerForActivityResult (new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageBitmap(bitmap);
                                myImage = bitmap;
                               /* ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                params.width = 300; // genişlik 500 piksel olsun
                                params.height = 300; // yükseklik 500 piksel olsun
                                imageView.setLayoutParams(params);*/
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                loadImageButton.setText("FOTOĞRAF DEĞİŞTİR");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);
            }
        });
    }

    private void askGalleryPermissions(){
        if (ContextCompat.checkSelfPermission (this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
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
                Toast.makeText(this, "Gallery Permission is Required to Select an Image.", Toast.LENGTH_SHORT).show();
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

    public boolean isEmail(String str){
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private void writeUserToFirebase(User user){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUserID());
        hashMap.put("name", user.getName());
        hashMap.put("surname", user.getSurname());
        hashMap.put("email", user.getEmail());

        //upload image to storage
        if(myImage!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.child("users/"+user.getUserID()).putBytes(data);

        }

        firebaseFirestore.collection("users").document(user.getUserID())
                .set(hashMap)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<Void>() {
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

}
