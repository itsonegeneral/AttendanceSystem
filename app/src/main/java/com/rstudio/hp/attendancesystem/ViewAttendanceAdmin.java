package com.rstudio.hp.attendancesystem;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAttendanceAdmin extends AppCompatActivity {

    private static final String TAG = "ViewAttendanceAdmin";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CASA");
    ListView listView;
    String sem ,batch;
    private String[] namelist ;
    public String[] percentage;
    private int totalDays;
    public ProgressBar pgBar;
    ViewAttendanceAdaptor viewAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_admin);
        setValues();
        getTotalDays();
        pgBar.setVisibility(View.VISIBLE);
        ref.child(batch).child(sem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int i=0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        long mAttend = snapshot.getValue(Long.class);
                        double percent = (  (double)mAttend/ (double)totalDays  )*100;
                        int t=(int) percent ;
                        percentage[i] = Integer.toString(t);
                        Log.d(TAG, "Added" + percentage[i]+ " " + i) ;
                        i++;
                    }
                    viewAdaptor.percentage = percentage;
                    listView.setAdapter(viewAdaptor);
                    pgBar.setVisibility(View.GONE);
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Error !",Snackbar.LENGTH_LONG).show();
                    pgBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgBar.setVisibility(View.GONE);
            }
        });
        setUpToolbar();
    }

    private void setValues() {
        listView = findViewById(R.id.list_viewAttendance);
        sem = getIntent().getExtras().getString("sem");
        batch = getIntent().getExtras().getString("batch");
        namelist = getResources().getStringArray(R.array.studentsbcas3);
        viewAdaptor = new ViewAttendanceAdaptor(this,namelist,percentage);
        percentage = new String[100];
        pgBar= findViewById(R.id.pgBar_viewAttendance);
    }

    private void getTotalDays(){
        DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference("Total Days").child(batch).child(sem);
        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalDays =  dataSnapshot.getValue(Integer.class);
                } else {
                    Log.d(TAG, "DB Not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_adminViewAttendance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Current Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
