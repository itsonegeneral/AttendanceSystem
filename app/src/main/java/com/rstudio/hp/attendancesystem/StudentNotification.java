package com.rstudio.hp.attendancesystem;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class StudentNotification extends AppCompatActivity {

    private static final String TAG = "StudentNotification";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference notRef = firestore.collection("StudentNotifications");
    TextView test;
    boolean readNot;
    NotificationAdaptor mAdaptor;
    ArrayList<Integer> unreadPositions;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notification);

        setUpToolbar();
        setActionBarColor();
        loadData();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        notRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){

                }else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        DocumentReference docRef = notRef.document(documentSnapshot.getId())
                                .collection("UsersStatus")
                                .document(userID);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                try{
                                    boolean read = documentSnapshot.getBoolean("ifRead");
                                }catch(NullPointerException e){

                                }
                            }
                        });
                    }
                }
            }
        });
    }
    private void loadData(){
        final String userID = firebaseAuth.getUid();
        Query query = notRef;
        FirestoreRecyclerOptions<NotificationClass> options = new FirestoreRecyclerOptions.Builder<NotificationClass>()
                .setQuery(query,NotificationClass.class)
                .build();

        mAdaptor = new NotificationAdaptor(options);
        RecyclerView recyclerView = findViewById(R.id.notifications_RecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdaptor);



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

    public void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_studentNotification);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");
    }
    private void setActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.LTGRAY);
        }
    }
}
