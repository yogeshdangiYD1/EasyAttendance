package com.students.easyattendance;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView welcomeTextView;
    private CardView classesCardView;
    private CardView attendanceCardView;
    private Button helpButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        welcomeTextView = findViewById(R.id.welcomeTextView);
        classesCardView = findViewById(R.id.classesCardView);
        attendanceCardView = findViewById(R.id.attendanceCardView);
        helpButton = findViewById(R.id.helpButton);
        logoutButton = findViewById(R.id.logoutButton);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();

            // Set the welcome text based on username or email
            if (userName != null && !userName.isEmpty()) {
                welcomeTextView.setText("Welcome, " + userName);
            } else {
                welcomeTextView.setText("Welcome, " + userEmail);
            }
        }

        // Set click listener for classes block
        classesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YourClassesActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for attendance block
        attendanceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YourAttendanceActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for Help button (if needed)
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // I will add it later
                Toast.makeText(MainActivity.this, "Comming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Redirect to login activity or any other desired action
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }
}
