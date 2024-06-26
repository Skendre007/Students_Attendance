package com.saurabh.studentsattendance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etStudentName, etRollNumber;
    private Button btnAddAttendance, btnShowData;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etStudentName = findViewById(R.id.etStudentName);
        etRollNumber = findViewById(R.id.etRollNumber);
        btnAddAttendance = findViewById(R.id.btnAddAttendance);
        btnShowData = findViewById(R.id.btnShowData);

        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        btnAddAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAttendance();
            }
        });

        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DisplayDataActivity.class));
            }
        });
    }

    private void addAttendance() {
        String studentName = etStudentName.getText().toString().trim();
        String rollNumber = etRollNumber.getText().toString().trim();

        if (TextUtils.isEmpty(studentName) || TextUtils.isEmpty(rollNumber)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String id = databaseReference.push().getKey();

        Map<String, String> attendanceData = new HashMap<>();
        attendanceData.put("studentName", studentName);
        attendanceData.put("rollNumber", rollNumber);
        attendanceData.put("date", date);

        if (id != null) {
            databaseReference.child(id).setValue(attendanceData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Attendance Added", Toast.LENGTH_SHORT).show();
                    etStudentName.setText("");
                    etRollNumber.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add attendance", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
