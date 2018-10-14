package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ManageNotificationsAdmin extends AppCompatActivity {

    FloatingActionButton floatingButton;
    NotificationAdaptor mAdaptor;
    CollectionReference reference = FirebaseFirestore.getInstance().collection("StudentNotifications");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notifications_admin);
        setUpToolbar();

        floatingButton = findViewById(R.id.ftbt_AdminManageNotifications);

        Query query = reference;

        FirestoreRecyclerOptions<NotificationClass> options = new FirestoreRecyclerOptions.Builder<NotificationClass>()
                .setQuery(query, NotificationClass.class)
                .build();
        mAdaptor = new NotificationAdaptor(options,true);
        RecyclerView recyclerView = findViewById(R.id.AdminNotifications_RecylcerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdaptor);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageNotificationsAdmin.this, CreateNewNotification.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdaptor.stopListening();
    }

    public void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.adminManageNotification_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Notifications");
    }
}
