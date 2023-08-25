package com.students.easyattendance;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.students.easyattendance.R;
import com.students.easyattendance.Student;

import java.util.Calendar;

public class MarkAttendance extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference classRef;
    private String className;
    private TableLayout studentAttendanceTable;
    private DatePickerDialog datePickerDialog; // Added for date selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        classRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid()).child("YourClasses");

        className = getIntent().getStringExtra("className");

        studentAttendanceTable = findViewById(R.id.studentAttendanceTable);

        Button addStudentButton = findViewById(R.id.addStudentButton);

        addStudentButton.setOnClickListener(view -> showAddStudentDialog());

        retrieveAndDisplayStudents();
    }

    private void retrieveAndDisplayStudents() {
        classRef.child(className).child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    studentAttendanceTable.setVisibility(View.VISIBLE);
                    studentAttendanceTable.removeAllViews(); // Clear existing rows
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        Student student = studentSnapshot.getValue(Student.class);
                        addTableRow(student);
                    }
                } else {
                    studentAttendanceTable.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void addTableRow(Student student) {
        TableRow tableRow = new TableRow(this);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(student.getStudentName());
        tableRow.addView(nameTextView);

        TextView rollNumberTextView = new TextView(this);
        rollNumberTextView.setText(student.getRollNumber());
        tableRow.addView(rollNumberTextView);

        // Add buttons for marking attendance (P and A) here
        Button presentButton = new Button(this);
        presentButton.setText("P");
        tableRow.addView(presentButton);

        Button absentButton = new Button(this);
        absentButton.setText("A");
        tableRow.addView(absentButton);

        // Set click listeners for the P and A buttons to handle attendance marking
        presentButton.setOnClickListener(v -> showDatePickerDialog(student, "Present"));
        absentButton.setOnClickListener(v -> showDatePickerDialog(student, "Absent"));

        // Add the TableRow to the studentAttendanceTable
        studentAttendanceTable.addView(tableRow);
    }

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Student");

        // Create the dialog layout programmatically
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);

        EditText studentNameEditText = new EditText(this);
        studentNameEditText.setHint("Student Name");
        dialogLayout.addView(studentNameEditText);

        EditText rollNumberEditText = new EditText(this);
        rollNumberEditText.setHint("Roll Number");
        dialogLayout.addView(rollNumberEditText);

        EditText emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        dialogLayout.addView(emailEditText);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Add", (dialogInterface, i) -> {
            String studentName = studentNameEditText.getText().toString().trim();
            String rollNumber = rollNumberEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (!studentName.isEmpty() && !rollNumber.isEmpty() && !email.isEmpty()) {
                // Add the student to Firebase
                addStudentToFirebase(studentName, rollNumber, email);
                dialogInterface.dismiss();
            } else {
                Toast.makeText(MarkAttendance.this, "Please enter student details", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addStudentToFirebase(String studentName, String rollNumber, String email) {
        DatabaseReference classStudentsRef = classRef.child(className).child("Students");

        Student student = new Student(studentName, rollNumber, email);

        // Push the student to the class's "Students" collection
        classStudentsRef.push().setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MarkAttendance.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    retrieveAndDisplayStudents(); // Refresh the student list
                })
                .addOnFailureListener(e -> Toast.makeText(MarkAttendance.this, "Error adding student", Toast.LENGTH_SHORT).show());
    }

    private void showDatePickerDialog(Student student, String status) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    markAttendance(student, status, selectedDate);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void markAttendance(Student student, String status, String selectedDate) {
        DatabaseReference attendanceRef = classRef.child(className).child("Attendance").child(selectedDate);

        String rollNumber = student.getRollNumber();

        attendanceRef.child(rollNumber).setValue(status);

        Toast.makeText(this, "Attendance marked for " + student.getStudentName() + " on " + selectedDate, Toast.LENGTH_SHORT).show();
    }
}
