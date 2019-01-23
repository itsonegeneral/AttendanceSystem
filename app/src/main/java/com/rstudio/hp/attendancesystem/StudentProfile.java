package com.rstudio.hp.attendancesystem;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StudentProfile extends AppCompatActivity {

    private ImageView bt_editName, bt_editSem,bt_editRollNo;
    private TextView tv_UserName,tv_UserRollNo;
    private EditText et_editName;
    boolean isNameEditing = false, isSemEditing = false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference("Users");
    private ProgressBar pgBar;
    private String userName, userSem;
    private Student student;
    private Spinner semSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        setValues();
        setToolbar();
        loadUserDetails();
        setListeners();
        setActionBarColor();

    }

    private void setListeners() {
        bt_editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNameEditing) {
                    tv_UserName.setVisibility(View.VISIBLE);
                    bt_editName.setBackgroundResource(R.drawable.ic_edit_black_24dp);
                    et_editName.setVisibility(View.GONE);
                    isNameEditing = false;
                    if (et_editName.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No Changes Made", Toast.LENGTH_SHORT).show();
                    } else if (!et_editName.getText().toString().equals(userName)) {
                        updateName(et_editName.getText().toString());
                    }
                } else {
                    tv_UserName.setVisibility(View.INVISIBLE);
                    et_editName.setVisibility(View.VISIBLE);
                    bt_editName.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    isNameEditing = true;
                }
            }
        });

        bt_editSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSemEditing) {
                    semSpinner.setEnabled(false);
                    bt_editSem.setBackgroundResource(R.drawable.ic_edit_black_24dp);
                    if (!semSpinner.getSelectedItem().toString().equals(student.sem)) {
                        updateSem(semSpinner.getSelectedItem().toString());
                    }else{
                        Toast.makeText(getApplicationContext(), "No Changes Made", Toast.LENGTH_SHORT).show();
                    }

                    isSemEditing = false;
                } else {
                    bt_editSem.setBackgroundResource(R.drawable.ic_done_black_24dp);
                    semSpinner.setEnabled(true);
                    isSemEditing = true;
                }
            }
        });
    }

    private void loadUserDetails() {
        pgBar.setVisibility(View.VISIBLE);
        String uuid = mAuth.getUid();
        db_ref.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    student = dataSnapshot.getValue(Student.class);
                    userName = student.name;
                    userSem = student.sem;
                    semSpinner.setPrompt(userSem);
                    et_editName.setHint(userName);
                    tv_UserName.setText(userName);
                    String roll = String.valueOf(student.rollno);
                    tv_UserRollNo.setText(roll);
                }

                setSpinner();

                pgBar.setVisibility(View.GONE);
            }

            private void setSpinner() {
                String[] arr= getResources().getStringArray(R.array.sem_array);
                List<String> list = Arrays.asList(arr);
                ArrayList<String> ulist = new ArrayList<>();
                ulist.add(userSem);
                ulist.addAll(list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentProfile.this,R.layout.spinner_item_profile,ulist);
                semSpinner.setAdapter(adapter);
                semSpinner.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgBar.setVisibility(View.GONE);
            }
        });



    }


    private void updateName(final String newName) {
        student.name = newName;
        db_ref.child(mAuth.getUid()).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Changes Updated", Toast.LENGTH_SHORT).show();
                    tv_UserName.setText(newName);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSem(final String newSem) {
        student.sem = newSem;
        db_ref.child(mAuth.getUid()).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sem Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setValues() {
        semSpinner = findViewById(R.id.spinner_studentProfileSem);
        bt_editSem = findViewById(R.id.ic_editUserSem);
        pgBar = findViewById(R.id.pgBar_studentProfile);
        bt_editName = findViewById(R.id.ic_editUserName);
        tv_UserName = findViewById(R.id.tv_studentNameProfile);
        et_editName = findViewById(R.id.et_updateProfileName);
        tv_UserRollNo = findViewById(R.id.tv_studentRollNoProfile);
        et_editName.setVisibility(View.GONE);
        pgBar.setVisibility(View.GONE);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_studentProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#057817"));
        }
    }
}
