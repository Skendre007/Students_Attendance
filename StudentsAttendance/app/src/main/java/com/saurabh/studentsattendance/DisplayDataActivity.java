package com.saurabh.studentsattendance;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayDataActivity extends AppCompatActivity {

    private ListView listViewData;
    private DatabaseReference databaseReference;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private List<String> keysList;
    private Button btnDeleteSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        listViewData = findViewById(R.id.listViewData);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        dataList = new ArrayList<>();
        keysList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

        listViewData.setAdapter(adapter);
        listViewData.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                keysList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String studentName = snapshot.child("studentName").getValue(String.class);
                    String rollNumber = snapshot.child("rollNumber").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);

                    String displayText = "Name: " + studentName + "\nRoll Number: " + rollNumber + "\nDate: " + date;
                    dataList.add(displayText);
                    keysList.add(snapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        listViewData.setOnItemClickListener((parent, view, position, id) -> {
            int selectedCount = listViewData.getCheckedItemCount();
            btnDeleteSelected.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
        });

        btnDeleteSelected.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Selected Attendance")
                .setMessage("Are you sure you want to delete the selected records?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSelectedAttendance())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteSelectedAttendance() {
        for (int i = listViewData.getChildCount() - 1; i >= 0; i--) {
            if (listViewData.isItemChecked(i)) {
                String key = keysList.get(i);
                databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(DisplayDataActivity.this, "Failed to delete some records", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        Toast.makeText(DisplayDataActivity.this, "Selected records deleted", Toast.LENGTH_SHORT).show();
        listViewData.clearChoices();
        btnDeleteSelected.setVisibility(View.GONE);
    }
}
