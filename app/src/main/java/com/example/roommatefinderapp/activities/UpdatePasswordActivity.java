package com.example.roommatefinderapp.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.roommatefinderapp.R;

import androidx.appcompat.app.AppCompatActivity;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText passwordInput1;
    private EditText passwordInput2;
    private ImageView showHidePassword1;
    private ImageView showHidePassword2;
    private Button buttonUpdate;
    private EditText oldPassword;
    private ImageView showHidePassword0;

    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        passwordInput1 = findViewById(R.id.passwordInput1);
        passwordInput2 = findViewById(R.id.passwordInput2);
        showHidePassword1 = findViewById(R.id.showHidePassword1);
        showHidePassword2 = findViewById(R.id.showHidePassword2);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        oldPassword = findViewById(R.id.oldPasswordInput);
        showHidePassword0 = findViewById(R.id.showHidePassword0);

        showHidePassword0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility1();
            }
        });

        showHidePassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility1();
            }
        });

        showHidePassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility2();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void togglePasswordVisibility1() {
        if (isPasswordVisible1) {
            passwordInput1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            passwordInput1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

        isPasswordVisible1 = !isPasswordVisible1;
    }

    private void togglePasswordVisibility2() {
        if (isPasswordVisible2) {
            passwordInput2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showHidePassword2.setImageResource(R.drawable.ic_visibility_off);
        } else {
            passwordInput2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

        isPasswordVisible2 = !isPasswordVisible2;
    }

    private void updatePassword() {
        String oldPasswordText = oldPassword.getText().toString();
        String password1 = passwordInput1.getText().toString();
        String password2 = passwordInput2.getText().toString();

        if (password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Lütfen şifreleri girin.", Toast.LENGTH_SHORT).show();
        } else if (!password1.equals(password2)) {
            Toast.makeText(this, "Şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show();
            } else {
                if (password1.length() >= 8 && !password1.equals(password1.toLowerCase()) && !password1.equals(password1.toUpperCase())){
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();

                    String userEmail = user.getEmail();
                    mAuth.signInWithEmailAndPassword(userEmail, oldPasswordText)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.updatePassword(password1)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(this, "Şifre güncellendi.", Toast.LENGTH_SHORT).show();
                                                    Intent myIntent = new Intent(UpdatePasswordActivity.this, BottomNavActivity.class);
                                                    UpdatePasswordActivity.this.startActivity(myIntent);
                                                }
                                                else {
                                                    Toast.makeText(this, "Şifre güncellenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, "Eski şifreyi hatalı girdiniz.", Toast.LENGTH_SHORT).show();
                                }
                            });
            }
                else
                    Toast.makeText(UpdatePasswordActivity.this, "Şifre Büyük ve Küçük Harf içermeli ayrıca minimum 8 karakterden oluşmalı.", Toast.LENGTH_SHORT).show();
        }
    }
    }

