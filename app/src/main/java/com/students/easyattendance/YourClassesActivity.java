package com.students.easyattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YourClassesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private List<ClassData> classDataList;
    private CustomClassAdapter classesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_classes);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("Users");

        ListView classesListView = findViewById(R.id.classesListView);

        // Initialize class data list and adapter
        classDataList = new ArrayList<>();
        classesAdapter = new CustomClassAdapter(this, classDataList);
        classesListView.setAdapter(classesAdapter);

        classesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ClassData selectedClass = classDataList.get(position);
                // Redirect the user to the MarkAttendance activity
                Intent intent = new Intent(YourClassesActivity.this, MarkAttendance.class);
                intent.putExtra("className", selectedClass.getClassName());
                startActivity(intent);
            }
        });

        Button createClassButton = findViewById(R.id.createClassButton);
        createClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateClassDialog();
            }
        });

        // Retrieve user's classes from Realtime Database and populate the list
        retrieveAndPopulateClasses();
    }

    private void retrieveAndPopulateClasses() {
        String userId = mAuth.getCurrentUser().getUid();
        usersReference.child(userId).child("YourClasses").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getValue() != null) {
                        Map<String, Object> classesData = (Map<String, Object>) task.getResult().getValue();
                        classDataList.clear(); // Clear existing data
                        for (String className : classesData.keySet()) {
                            String batch = classesData.get(className).toString(); // Update this based on your data structure
                            ClassData classData = new ClassData(className, batch);
                            classDataList.add(classData);
                        }
                        classesAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showCreateClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Class");

        // Create the dialog layout programmatically
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);

        EditText classNameEditText = new EditText(this);
        classNameEditText.setHint("Class Name");
        dialogLayout.addView(classNameEditText);

        EditText batchEditText = new EditText(this);
        batchEditText.setHint("Batch");
        dialogLayout.addView(batchEditText);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Add", (dialogInterface, i) -> {
            String className = classNameEditText.getText().toString().trim();
            String batch = batchEditText.getText().toString().trim();

            if (!className.isEmpty() && !batch.isEmpty()) {
                // Add the class to Realtime Database
                addClassToDatabase(className, batch);
                dialogInterface.dismiss();
            } else {
                Toast.makeText(YourClassesActivity.this, "Please enter class name and batch", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addClassToDatabase(String className, String batch) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userClassesRef = usersReference.child(userId)
                .child("YourClasses");

        // Create a Map object to store the class data
        Map<String, Object> classData = new HashMap<>();
        classData.put("className", className);
        classData.put("batch", batch);

        userClassesRef.child(className).setValue(classData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(YourClassesActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the classes list
                        retrieveAndPopulateClasses();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(YourClassesActivity.this, "Error creating class", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
