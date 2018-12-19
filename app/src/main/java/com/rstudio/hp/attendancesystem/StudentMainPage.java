package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class StudentMainPage extends AppCompatActivity {

    private CardView update_card;
    private boolean doubleBackToExitPressedOnce = false;
    private int unreadNotifications = 0;
    private static final String TAG = "StudentMainPage";
    private TextView welcometv, percentage, status, rollnoTV, presentDaysTv, totalDaysTv, batch_sem_tv;
    private FirebaseAuth firebaseAuth;
    private long i, mtotal;
    private long rollno;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String batch, rollnoRef;
    private FirebaseUser firebaseUser;
    private ProgressBar pgBar;
    private Menu mMenu;
    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);
        setToolbar();
        setUpValues();
        versionCheck();
        FirebaseUser uaer = FirebaseAuth.getInstance().getCurrentUser();
        setActionBarColor();
        if (uaer == null) {
            Toast.makeText(getApplicationContext(), "User null ", Toast.LENGTH_SHORT).show();
        } else {
            loadUserDetails();
            //      loadNotifications();
        }


    }

    private void versionCheck() {
        DatabaseReference vc = FirebaseDatabase.getInstance().getReference("Version");
        final int VERSION = 5;
        vc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long mVersion = dataSnapshot.getValue(Long.class);
                    if (mVersion > VERSION) {
                        Animation anim = AnimationUtils.loadAnimation(StudentMainPage.this, R.anim.move_up);
                        update_card.setVisibility(View.VISIBLE);
                        anim.setDuration(700);
                        update_card.startAnimation(anim);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        update_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rstudio.hp.attendancesystem&hl=en"));
                startActivity(i);
            }
        });
    }


    private void loadUserDetails() {
        pgBar.setVisibility(View.VISIBLE);

        final DatabaseReference sdRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        sdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadExistingUser(dataSnapshot);
                } else {
                    moveUserToNewDB(sdRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgBar.setVisibility(View.GONE);
            }
        });

    }


    private void loadExistingUser(DataSnapshot dataSnapshot) {
        Student s = dataSnapshot.getValue(Student.class);
        String name = s.name;
        String batch = s.batch;
        String sem = s.sem;
        char[] n = name.toCharArray();
        if (Character.isLowerCase(n[0])) {
            n[0] = Character.toUpperCase(n[0]);
            name = String.valueOf(n);
        }
        batch_sem_tv.setText(batch + ", " + sem);
        welcometv.setText(name);
        rollno = s.rollno;
        String rollNo = Long.toString(rollno);
        rollnoTV.setText(rollNo);
        rollnoRef = Long.toString(rollno);
        loadTotalDays(batch, sem);
        DatabaseReference presentRef = FirebaseDatabase.getInstance().getReference("CASA").child(batch).child(sem).child(rollnoRef);
        presentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long pDays = (long) dataSnapshot.getValue();
                    double perc = (double) pDays / (double) mtotal;
                    perc = perc * 100.0;
                    int tper = (int) perc;
                    String a = Long.toString(pDays);
                    presentDaysTv.setText(a);
                    if (perc < 75) {
                        long bunked = mtotal - pDays;
                        long total_to_bunked = bunked * 4;
                        long to_attend = total_to_bunked - mtotal;
                        String at_string = Long.toString(to_attend);
                        percentage.setTextColor(Color.RED);
                        status.setText("You must attend " + at_string + " classes");
                    } else if (perc >= 75) {
                        percentage.setTextColor(Color.parseColor("#00bc10"));
                        long total = (mtotal / 3) * 4;
                        long absent = mtotal - pDays;
                        long bunkable = total - mtotal - absent;
                        String bunk = Long.toString(bunkable);
                        status.setText("Great! You can bunk " + bunk + " classes");
                    } else {
                        status.setText("You are going good");
                    }
                    String percent = Integer.toString(tper) + "%";
                    percentage.setText(percent);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Your Roll no is not found in database, Contact Admin",
                            Snackbar.LENGTH_INDEFINITE).show();
                    status.setText("Maybe your attendance isn't registered");
                    status.setTextSize(15);
                    percentage.setText("0%");
                    percentage.setTextColor(Color.RED);
                    presentDaysTv.setText("-");
                    presentDaysTv.setTextColor(Color.RED);
                    status.setTextColor(Color.RED);
                }
                pgBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pgBar.setVisibility(View.GONE);
                Log.d(TAG, databaseError.getMessage());
            }
        });

    }

    private void loadTotalDays(String bat, String sem) {
        DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference("Total Days").child(bat).child(sem);
        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mtotal = (long) dataSnapshot.getValue();
                    String total = Long.toString(mtotal);
                    totalDaysTv.setText(total);
                } else {
                    totalDaysTv.setText("ERR 01");
                    totalDaysTv.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void moveUserToNewDB(final DatabaseReference newRef) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    student.sem = "S3";
                    newRef.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ref.removeValue();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "No data found in DB moveUserToNewDB");
                }
                pgBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Move Data Failed");
                pgBar.setVisibility(View.GONE);
            }
        });
    }

    private void setActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#057817"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mMenu = menu;
        menuItem = mMenu.findItem(R.id.item_notifications);
        inflater.inflate(R.menu.studentmainpagemenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout: {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(StudentMainPage.this, studentLogin.class));
                break;
            }
            case R.id.item_refresh_page: {
                Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_SHORT).show();
                loadUserDetails();
                break;
            }
            case R.id.item_settingsmenu: {
                Toast.makeText(StudentMainPage.this, "Under development", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item_profilemenu: {
                startActivity(new Intent(StudentMainPage.this, StudentProfile.class));
                break;
            }
            case R.id.item_notifications: {
                //Toast.makeText(getApplicationContext(), "Coming Soon !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentMainPage.this, StudentNotification.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.studenttoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Attendance Details");
    }

    private void setUpValues() {
        update_card = findViewById(R.id.cardView_Update);
        status = findViewById(R.id.tv_studentAttendanceStatus);
        percentage = findViewById(R.id.tv_studentPercentage);
        pgBar = findViewById(R.id.pgBar_studentMainPage);
        pgBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        welcometv = findViewById(R.id.et_mainpage_studentname);
        presentDaysTv = findViewById(R.id.tv_presentDaysStudMain);
        totalDaysTv = findViewById(R.id.tv_totalDaysStudMain);
        rollnoTV = findViewById(R.id.tv_studentMainRollno);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(firebaseUser.getUid());
        batch_sem_tv = findViewById(R.id.tv_studentMainBatchSem);
        setUserImage();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void setUserImage() {
        final ImageView imageView = findViewById(R.id.userImage);
        final SharedPreferences pref = getSharedPreferences("user_gender", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        if (pref.getString("gender", "male").equals("female")) {
            imageView.setImageResource(R.drawable.user_female);
        } else {
            imageView.setImageResource(R.drawable.user);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString("gender", "male").equals("male")) {
                    editor.putString("gender", "female");
                    imageView.setImageResource(R.drawable.user_female);
                    editor.apply();
                } else {
                    editor.putString("gender", "male");
                    imageView.setImageResource(R.drawable.user);
                    editor.apply();
                }
            }
        });
    }


}
