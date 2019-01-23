package com.rstudio.hp.attendancesystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AdminMain extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String[] studentsName;
    ListView list;
    ArrayList<ItemStudentAttendance> rowItem;
    CustomAdapter cAdapter;
    Button submitbtn, selectDateBtn;
    TextView selectedDate;
    DatabaseReference db_stud, db_total;
    String date, batch, sem;
    boolean flag = false, dateKey = false, dbKey = false, tdbKey;
    private static final String TAG = "AdminMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        setupToolbar();
        setValues();
        setUpDatePicker();
        setStudentsList();
        list = findViewById(R.id.listView_adminPage);

        if (studentsName == null) {
            Snackbar.make(findViewById(android.R.id.content), "Student List Not Found", Snackbar.LENGTH_INDEFINITE).show();
        } else {
            for (int i = 0; i < studentsName.length; i++) {
                ItemStudentAttendance student = new ItemStudentAttendance(studentsName[i], true);
                rowItem.add(student);
            }
        }

        if (sem.equals("S3") && batch.equals("BCA")) {
            rowItem.get(34).setSelected(false);     //Uncheck Vipin All the time
        }
        cAdapter = new CustomAdapter(rowItem, getApplicationContext());
        list.setAdapter(cAdapter);

        submitbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!selectedDate.getText().toString().equals("Select date")) {
                    dateCheck();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Select date", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStudentsList() {
        switch (batch) {
            case "BCA": {
                bcaSwitch(sem);
                break;
            }
            case "Bsc CS": {
                bscSwitch(sem);
            }
        }
    }

    private void dateCheck() {
        final ProgressDialog pg = new ProgressDialog(AdminMain.this);
        pg.setMessage("Validating Date");
        pg.setCanceledOnTouchOutside(false);
        pg.setCancelable(false);
        pg.show();
        final DatabaseReference dbDate = FirebaseDatabase.getInstance().getReference("Dates").child(batch).child(sem);
        dbDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    pg.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Attendance has been already sumbitted", Snackbar.LENGTH_LONG).show();
                } else {
                    dbDate.child(date).setValue(true);

                    //proceed to register attendance if datecheck is ok
                    incrementDay();

                    pg.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(findViewById(android.R.id.content), databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();
                pg.dismiss();
                dateKey = false;
            }
        });

    }

    private void incrementDay() {
        final DatabaseReference total = FirebaseDatabase.getInstance().getReference("Total Days").child(batch).child(sem);
        total.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if the attendance is not getting registered for the first time
                // this if will run
                if (dataSnapshot.exists()) {
                    long getTotal = (long) dataSnapshot.child("Days").getValue();
                    long setTotal = getTotal + 1;
                    total.child("Days").setValue(setTotal);
                    total.child("Last Updated").setValue(date);
                    updateAttendance();

                } else {
                    //in case the attendance is getting registered first time , confirm and proceed with alert
                    alertBuild();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void updateAttendance() {
        ArrayList<ItemStudentAttendance> selList = cAdapter.dataSet;
        for (int i = 0; i < selList.size(); i++) {
            ItemStudentAttendance it = selList.get(i);                  //updating student attendance
            if (it.isSelected()) {
                updateStudAttendance((i + 1));
            }
        }
        Snackbar.make(findViewById(android.R.id.content), "Attendance Updated Successfully", Snackbar.LENGTH_SHORT)
                .show();
    }


    private void updateStudAttendance(int roll) {
        final DatabaseReference db_roll = FirebaseDatabase.getInstance().getReference("CASA");
        final String r = Integer.toString(roll);
        db_roll.child(batch).child(sem).child(r).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long temp_at = (long) dataSnapshot.getValue();
                    db_roll.child(batch).child(sem).child(r).setValue((temp_at + 1));
                } else {
                    db_roll.child(batch).child(sem).child(r).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminMain.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void alertBuild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMain.this);
        builder.setTitle("Total Days data not found in Database !");
        builder.setMessage("Are you submitting for the first time ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Total Days")
                        .child(batch).child(sem);
                ref.child("Days").setValue(1);
                ref.child("Last Updated").setValue(date);
                updateAttendance();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AdminMain.this, "Day not added", Toast.LENGTH_SHORT).show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Dates")
                        .child(batch)
                        .child(sem).child(date);
                ref.removeValue();
                dialog.dismiss();
                tdbKey = false;
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.adminMainPageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setValues() {
        rowItem = new ArrayList<>();
        submitbtn = findViewById(R.id.bt_adminMain_submitattendance);
        selectedDate = findViewById(R.id.tv_adminDateView);
        batch = getIntent().getExtras().getString("batch");
        sem = getIntent().getExtras().getString("sem");
    }

    private void setUpDatePicker() {
        selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(i, i1, i2);
        date = DateFormat.getDateInstance().format(c.getTime());
        selectedDate.setText(date);
    }


    private void bcaSwitch(String sem) {
        switch (sem) {
            case "S1": {
                studentsName = getResources().getStringArray(R.array.students2018bca);
                return;
            }
            case "S2": {
                studentsName = getResources().getStringArray(R.array.students2018bca);
                break;
            }
            case "S3": {
                studentsName = getResources().getStringArray(R.array.students2017bca);
                Arrays.sort(studentsName);
                break;
            }
            case "S4": {
                 studentsName = getResources().getStringArray(R.array.students2017bca);
                Arrays.sort(studentsName);
                break;
            }
            case "S5": {
                studentsName = getResources().getStringArray(R.array.students2016bca);
                break;
            }
            case "S6": {
                studentsName = getResources().getStringArray(R.array.students2016bca);
                break;
            }
        }
    }

    private void bscSwitch(String sem) {
        switch (sem) {
            case "S1": {
                studentsName = getResources().getStringArray(R.array.students2018cs);
                break;
            }
            case "S2": {
                 studentsName = getResources().getStringArray(R.array.students2018cs);
                break;
            }
            case "S3": {
                  studentsName = getResources().getStringArray(R.array.students2017cs);
                break;
            }
            case "S4": {
                  studentsName = getResources().getStringArray(R.array.students2017cs);
                break;
            }
            case "S5": {
                  studentsName = getResources().getStringArray(R.array.students2016cs);
                break;
            }
            case "S6": {
                  studentsName = getResources().getStringArray(R.array.students2016cs);
                break;
            }
        }
    }


}
