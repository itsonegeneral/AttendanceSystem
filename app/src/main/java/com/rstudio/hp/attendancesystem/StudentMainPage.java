package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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


    int unreadNotifications = 0;
    private static final String TAG = "StudentMainPage";
    TextView welcometv, percentage, status, rollnoTV, presentDaysTv, totalDaysTv;
    FirebaseAuth firebaseAuth;
    long i, mtotal;
    long rollno;
    FirebaseDatabase database;
    DatabaseReference reference;
    String batch, rollnoRef;
    FirebaseUser firebaseUser;
    ProgressBar pgBar;
    Menu mMenu;
    MenuItem menuItem;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);
        setToolbar();
        setUpValues();
        FirebaseUser uaer = FirebaseAuth.getInstance().getCurrentUser();
        setActionBarColor();
        if (uaer == null) {
            Toast.makeText(getApplicationContext(), "User null ", Toast.LENGTH_SHORT).show();
        } else {
            loadUserDetails();
            loadNotifications();
        }
    }

    private void loadNotifications() {
        final String userID = firebaseAuth.getUid();
        final FirebaseFirestore notif = FirebaseFirestore.getInstance();
        firestore.collection("StudentNotifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(StudentMainPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    unreadNotifications =0;
                    Toast.makeText(StudentMainPage.this, "Got " + documentSnapshot.getString("Notdate"), Toast.LENGTH_SHORT).show();
                    final DocumentReference dR =
                            notif.collection("StudentNotifications")
                                    .document(documentSnapshot.getId())
                                    .collection("UsersStatus")
                                    .document(userID);
                          dR.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    try {
                                        boolean ifRead = documentSnapshot.getBoolean("ifRead");
                                        if (!ifRead) {
                                            setNotificationUnread();
                                            unreadNotifications++;
                                        }
                                    } catch (NullPointerException ne) {
                                        Map<String, Object> rMap = new HashMap<>();
                                        rMap.put("ifRead", false);
                                        dR.set(rMap);
                                        ne.printStackTrace();
                                        setNotificationUnread();
                                        unreadNotifications++;
                                    }

                                }
                            });
                    if (unreadNotifications == 0) {
                        setNotificationRead();
                    }else{
                        Snackbar.make(findViewById(android.R.id.content),"You have "+unreadNotifications+ " unread notifications",Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void loadUserDetails() {
        pgBar.setVisibility(View.VISIBLE);
        DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference("Total Days");
        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mtotal = (long) dataSnapshot.getValue();
                String total = Long.toString(mtotal);
                totalDaysTv.setText(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference sdRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        sdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student s = dataSnapshot.getValue(Student.class);
                String name = s.name;
                char[] n = name.toCharArray();
                if (Character.isLowerCase(n[0])) {
                    n[0] = Character.toUpperCase(n[0]);
                    name = String.valueOf(n);
                }
                welcometv.setText(name);
                rollno = s.rollno;
                String rollNo = Long.toString(rollno);
                rollnoTV.setText(rollNo);
                rollnoRef = Long.toString(rollno);
                try {
                    DatabaseReference presentRef = FirebaseDatabase.getInstance().getReference().child("CASA").child("BCA").child(rollnoRef);
                    presentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long pDays = (long) dataSnapshot.getValue();
                            double perc = (double) pDays / (double) mtotal;
                            perc = perc * 100.0;
                            int tper = (int) perc;
                            String a = Long.toString(pDays);
                            presentDaysTv.setText(a);
                            if (perc < 75) {
                                status.setText("You must attend more classes");
                            } else if (perc > 90) {
                                status.setText("Great !");
                            } else {
                                status.setText("You are going good");
                            }
                            String percent = Integer.toString(tper) + "%";
                            percentage.setText(percent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    pgBar.setVisibility(View.GONE);
                } catch (NullPointerException npe) {
                    DatabaseReference presentRef = FirebaseDatabase.getInstance().getReference().child("CASA").child("BCA");
                    presentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(rollnoRef)) {

                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Your Registered roll no is not found on server ! Contact Developer", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Snackbar.make(findViewById(android.R.id.content), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                    pgBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setNotificationUnread() {
      //  menuItem.setIcon(R.drawable.ic_notifications_unread);
    }

    private void setNotificationRead() {
        try {
            menuItem.setIcon(R.drawable.ic_notifications_white_24dp);
        }catch(NullPointerException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    private void setActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.LTGRAY);
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
                try {
                    loadUserDetails();
                    loadNotifications();
                } catch (RuntimeException re) {
                    re.printStackTrace();
                    throw re;
                }
                break;
            }
            case R.id.item_settingsmenu: {
                Toast.makeText(StudentMainPage.this, "Under development", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item_profilemenu: {
                Toast.makeText(StudentMainPage.this, "Under development", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.item_notifications: {
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
    }


}
