package com.rstudio.hp.attendancesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

public class AdminMenuActivity extends AppCompatActivity {

    private CardView update_card;
    private Button mNotifications;
    private Button regAttendance;
    private TextView tv_sem,tv_name;
    private Button logout;
    private Button viewAttendace;
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
        checkVersion();
        logout = findViewById(R.id.bt_logout_admin);

        update_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.rstudio.hp.attendancesystem&hl=en"));
                startActivity(i);
            }
        });
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

        viewAttendace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this,ViewAttendanceAdmin.class);
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
        update_card = findViewById(R.id.sampleidCard);
        mNotifications = findViewById(R.id.bt_adminMenu_ManageNotifications);
        regAttendance = findViewById(R.id.bt_adminMenu_registerAttendance);
        tv_name = findViewById(R.id.tv_adminMenuName);
        tv_sem = findViewById(R.id.tv_adminMenuSem);
        pgDialog = new ProgressDialog(AdminMenuActivity.this);
        viewAttendace = findViewById(R.id.bt_adminMenu_ViewAttendance);
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
                    String name =(String) dataSnapshot.child("name").getValue();
                    String tname = "Name : "+name;
                    tv_name.setText(tname);
                    batch =(String) dataSnapshot.child("batch").getValue();
                    sem = (String) dataSnapshot.child("sem").getValue();
                    tv_sem.setText("Current Access : "+batch+" ,"+sem);
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
    private void checkVersion(){
        final int version = 8;
        DatabaseReference vc = FirebaseDatabase.getInstance().getReference("Version");
        vc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    try {
                        long mVer = dataSnapshot.getValue(Long.class);
                        if (mVer > version) {
                            Animation anim = AnimationUtils.loadAnimation(AdminMenuActivity.this, R.anim.move_up);
                            update_card.setVisibility(View.VISIBLE);
                            anim.setDuration(700);
                            update_card.startAnimation(anim);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
