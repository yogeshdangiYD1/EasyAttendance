package com.students.easyattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (!isPasswordValid(password)) {
                    // Password does not meet complexity requirements
                    Toast.makeText(Register.this, "Password must be at least 6 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Register.this, "Registered successfully, verification link sent to your mail", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, Login.class));
                                    finish(); // Close the registration activity
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Toast.makeText(Register.this, "Weak password. Make sure your password includes at least 6 characters, including one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                        Toast.makeText(Register.this, "Invalid email address.", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,}$");
    }
}
