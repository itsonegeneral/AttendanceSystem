package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenuActivity extends AppCompatActivity {

    private Button mNotifications;
    private Button regAttendance;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        setUpToolbar();

        mNotifications = findViewById(R.id.bt_adminMenu_ManageNotifications);
        regAttendance = findViewById(R.id.bt_adminMenu_registerAttendance);
        logout = findViewById(R.id.bt_logout_admin);

        mNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this,ManageNotificationsAdmin.class));
            }
        });
        regAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenuActivity.this,AdminMain.class));
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
}
