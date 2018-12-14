package com.rstudio.hp.attendancesystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateNewNotification extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText mTitle;
    private EditText mDesc;
    private Button create_btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notifRef = db.collection("StudentNotifications");
    private TextView tv_date;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_notification);
        setValues();
        setToolbar();
        setUpDatePicker();

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();
                if (checkInput(title, desc)) {
                    // upload data to firebase
                    NotificationClass not = new NotificationClass(title, desc, date, 1);

                    // adding data to Student Notification Collection
                    // Change to test Collection for testing
                    notifRef.add(not).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Snackbar.make(findViewById(android.R.id.content), "Notification Sent Successful", Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Notification Sent Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void setValues() {
        mTitle = findViewById(R.id.et_newNotTitle);
        mDesc = findViewById(R.id.et_newNotDescription);
        create_btn = findViewById(R.id.bt_createNewNotif);
        tv_date = findViewById(R.id.tv_newNotDate);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_CreateNewNotification);
        setSupportActionBar(toolbar);
        ;
        getSupportActionBar().setTitle("New Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean checkInput(String title, String desc) {
        if (title.isEmpty()) {
            mTitle.setError("Title is required");
            return false;
        } else if (desc.isEmpty()) {
            mDesc.setError("Description is required");
            return false;
        }
        return true;
    }

    private void setUpDatePicker() {
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        // setting the present date as default
        date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tv_date.setText(date);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(i, i1, i2);
        date = DateFormat.getDateInstance().format(c.getTime());
        tv_date.setText(date);
    }
}
