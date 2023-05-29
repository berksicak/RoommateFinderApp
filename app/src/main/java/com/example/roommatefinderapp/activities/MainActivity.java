package com.example.roommatefinderapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.entities.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button signUp;
    private Button signIn;
    private EditText emailInput;
    private EditText passwordInput;
    private FirebaseAuth firebaseAuth;
    private User myUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp = (Button) findViewById(R.id.signUp);
        signIn = (Button) findViewById(R.id.signIn);
        emailInput = (EditText) findViewById(R.id.emailInputOnMain);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        if (intent.hasExtra("email")) {
            String data = intent.getStringExtra("email");
            emailInput.setText(data);
        }

        //giris yap
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signIn(view);
            }
        });

        //uye ol
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(MainActivity.this, SignupActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    public void signIn(View view){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        if (isEmail(email) && password.length()>=8 && !password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(MainActivity.this,
                    new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){//checking is email verified or not
                            Toast.makeText(MainActivity.this, "Giriş başarılı.", Toast.LENGTH_SHORT).show();
                            saveUserID(firebaseAuth.getCurrentUser().getUid());
                            System.out.println(firebaseAuth.getCurrentUser().getUid());
                            Intent myIntent = new Intent(MainActivity.this, BottomNavActivity.class);
                            MainActivity.this.startActivity(myIntent);
                             }
                            else
                                Toast.makeText(MainActivity.this, "Lütfen e-mail adresinizi doğrulayınız.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Girdiğiniz Bilgiler Hatalı Lütfen Kontrol Edin.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    public boolean isEmail(String str){
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private void saveUserID(String id){
        sharedPreferences = this.getSharedPreferences("com.example.roommatefinderapp" , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("userID", id);
        editor.apply();
    }

}