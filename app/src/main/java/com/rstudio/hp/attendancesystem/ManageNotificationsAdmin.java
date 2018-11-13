package com.rstudio.hp.attendancesystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ManageNotificationsAdmin extends AppCompatActivity {

    private int position;
    private RecyclerView recyclerView;
    FloatingActionButton floatingButton;
    NotificationAdaptorAdmin mAdaptor;
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
        mAdaptor = new NotificationAdaptorAdmin(options);
        recyclerView = findViewById(R.id.AdminNotifications_RecylcerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdaptor);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageNotificationsAdmin.this, CreateNewNotification.class));
            }
        });
       new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
           @Override
           public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
               return false;
           }

           @Override
           public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
               position = viewHolder.getAdapterPosition();
               deleteNotification();
           }
       }).attachToRecyclerView(recyclerView);
    }

    private void deleteNotification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageNotificationsAdmin.this);
        builder.setTitle("Confirm");
        builder.setMessage("Delete this notification ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                mAdaptor.deleteItem(position);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdaptor.notifyDataSetChanged();
               dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
