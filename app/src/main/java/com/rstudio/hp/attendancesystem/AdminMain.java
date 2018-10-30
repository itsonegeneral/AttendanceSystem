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
    boolean flag = false, dateKey = false;
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
        Arrays.sort(studentsName);
        for (int i = 0; i < studentsName.length; i++) {
            ItemStudentAttendance student = new ItemStudentAttendance(studentsName[i], true);
            rowItem.add(student);
        }
        //  rowItem.get(34).setSelected(false);
        cAdapter = new CustomAdapter(rowItem, getApplicationContext());
        list.setAdapter(cAdapter);

        submitbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!selectedDate.getText().toString().equals("Select date")) {
                    if (dateCheck()) {
                        incrementDay();
                        ArrayList<ItemStudentAttendance> selList = cAdapter.dataSet;
                        for (int i = 0; i < selList.size(); i++) {
                            ItemStudentAttendance it = selList.get(i);
                            if (it.isSelected()) {
                                updateStudAttendance((i + 1));
                            }
                        }
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Select date", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        // setupDatabase();
    }

    private void setStudentsList() {
        String batch = getIntent().getExtras().getString("batch");
        String sem = getIntent().getExtras().getString("sem");
        switch (batch) {
            case "BCA": {
                bcaSwitch(sem);
                break;
            }
            case "B.Com": {
                break;
            }
        }
    }


    private void incrementDay() {
        db_total = FirebaseDatabase.getInstance().getReference("Total Days").child(batch).child(sem);
        db_total.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long getTotal = (long) dataSnapshot.getValue();
                    long setTotal = getTotal + 1;
                    db_total.setValue(setTotal);
                    Snackbar.make(findViewById(android.R.id.content), "Days updated", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Total Days Column not found in DB", Snackbar.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

    }

    private boolean dateCheck() {
        dateKey = true;
        final ProgressDialog pg = new ProgressDialog(AdminMain.this);
        pg.setMessage("Validating Date");
        pg.setCanceledOnTouchOutside(false);
        pg.setCancelable(false);
        pg.show();
        final DatabaseReference dbDate = FirebaseDatabase.getInstance().getReference("Dates");
        dbDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    pg.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Attendance has been already sumbitted", Snackbar.LENGTH_LONG).show();
                    dateKey = false;
                } else {
                    boolean reg = true;
                    dbDate.child(date).setValue(reg);
                    pg.dismiss();
                    dateKey = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(findViewById(android.R.id.content), databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();
                pg.dismiss();
                dateKey = false;
            }
        });
        return dateKey;
    }

    private void updateStudAttendance(int roll) {
        final DatabaseReference db_roll = FirebaseDatabase.getInstance().getReference("CASA");
        final String r = Integer.toString(roll);
        db_roll.child(batch).child(sem).child(r).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    long temp_at = (long) dataSnapshot.getValue();
                    db_roll.child(batch).child(r).setValue((temp_at + 1));
                } catch (RuntimeException re) {
                    db_roll.child(batch).child(r).setValue(1);
                    re.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminMain.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void alertBuild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Total Days data not found in DB!!");
        builder.setMessage("Do you want to create new Column ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Total Days")
                        .child(batch).child(sem);
                ref.setValue(1);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AdminMain.this, "Day not added", Toast.LENGTH_SHORT).show();
                // Do nothing
                dialog.dismiss();
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
                studentsName = getResources().getStringArray(R.array.studentsbcas1);
                return;
            }
            case "S2": {
                studentsName = getResources().getStringArray(R.array.studentsbcas2);
                break;
            }
            case "S3": {
                studentsName = getResources().getStringArray(R.array.studentsbcas3);
                break;
            }
            case "S4": {
                studentsName = getResources().getStringArray(R.array.studentsbcas4);
                break;
            }
            case "S5": {
                // studentsName = getResources().getStringArray(R.array.studentsbcas5);
                break;
            }
            case "S6": {
                //studentsName = getResources().getStringArray(R.array.studentsbcas6);
                break;
            }
        }
    }
}
