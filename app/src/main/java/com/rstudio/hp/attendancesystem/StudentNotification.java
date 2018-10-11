package com.rstudio.hp.attendancesystem;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class StudentNotification extends AppCompatActivity {

    private static final String TAG = "StudentNotification";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference notRef = firestore.collection("StudentNotifications");
    TextView test;
    boolean readNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notification);

        test = findViewById(R.id.tv_studNotification);
        setUpToolbar();
        setActionBarColor();
        loadData();
    }
    private void loadData(){
        final String userID = firebaseAuth.getUid();
        notRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        DocumentReference dR = firestore.collection("StudentNotifications")
                                .document(documentSnapshot.getId())
                                .collection("UsersStatus")
                                .document(userID);
                        dR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                try {
                                    readNot = documentSnapshot.getBoolean("ifRead");
                                }catch (NullPointerException N){
                                    N.printStackTrace();
                                }
                            }
                        });
                        String t = documentSnapshot.getString("Nottitle");
                        String d = documentSnapshot.getString("Notdescription");
                        String f = "\nTitle :" + t + "\nDescription :" + d + "\nReadStatus" + readNot+"\n\n";
                        stringBuffer.append(f);
                    }
                    test.setText(stringBuffer);
                }else{
                    Toast.makeText(StudentNotification.this,"Error occured while loading",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error"+ e.getMessage());
                }
            }
        });
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
