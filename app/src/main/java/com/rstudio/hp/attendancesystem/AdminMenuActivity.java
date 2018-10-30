package com.rstudio.hp.attendancesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMenuActivity extends AppCompatActivity {

    private Button mNotifications;
    private Button regAttendance;
    private Button logout;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
    private DatabaseReference db_ref = firebaseDatabase.getReference("Admins").child(firebaseAuth.getUid());
    private ProgressDialog pgDialog;
    private String batch,sem;
    private static final String TAG = "AdminMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        setUpToolbar();
        setValues();
        loadAccess();
        logout = findViewById(R.id.bt_logout_admin);

        mNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this,ManageNotificationsAdmin.class);
                intent.putExtra("batch",batch);

                startActivity(intent);
            }
        });
        regAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this,AdminMain.class);
                intent.putExtra("batch",batch);
                intent.putExtra("sem",sem);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminMenuActivity.this,studentLogin.class));
            }
        });
    }
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_AdminMenuPage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Tools");
    }
    private void setValues(){
        mNotifications = findViewById(R.id.bt_adminMenu_ManageNotifications);
        regAttendance = findViewById(R.id.bt_adminMenu_registerAttendance);
        pgDialog = new ProgressDialog(AdminMenuActivity.this);
    }
    private void loadAccess(){
        pgDialog.setMessage("Loading");
        pgDialog.setCancelable(false);
        pgDialog.show();
        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"Database Error! Please Report",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error. No Data");
                    pgDialog.cancel();
                }else{
                    batch =(String) dataSnapshot.child("batch").getValue();
                    sem = (String) dataSnapshot.child("sem").getValue();
                    pgDialog.cancel();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                pgDialog.cancel();
            }
        });
    }
}
