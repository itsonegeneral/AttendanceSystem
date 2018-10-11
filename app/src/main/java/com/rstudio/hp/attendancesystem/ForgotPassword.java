package com.rstudio.hp.attendancesystem;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    Button sendEmail;
    EditText emailET;
    LayoutInflater inflater;
    View layout_view;
    ImageView toastImage;
    TextView toastText;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setUpToolbar();
        progressBar = findViewById(R.id.pgBar_forgotPassword);
        progressBar.setVisibility(View.GONE);
        inflater = getLayoutInflater();
        layout_view = inflater.inflate(R.layout.custom_toast,(ViewGroup)findViewById(R.id.toast_layout_blue));
        sendEmail = findViewById(R.id.bt_sendResetEmailFP);
        emailET = findViewById(R.id.et_forgotPasswordEmail);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternet()) {
                    String email = emailET.getText().toString();
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    showSuccessToast();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    showErrorToast();
                                }
                            }
                        });
                    } else {
                        emailET.setError("Invalid Email");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Internet connection unavailable", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }

    private void showSuccessToast(){
        toastImage = layout_view.findViewById(R.id.toast_image);
        toastText  = layout_view.findViewById(R.id.toast_textView);
        toastImage.setImageResource(R.drawable.ic_success_tick_white);
        toastText.setText("Reset link has been sent successfully !");
        Toast t = new Toast(getApplicationContext());
    //    t.setGravity(Gravity.NO_GRAVITY,0,0);
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(layout_view);
        t.show();
    }
    private void showErrorToast(){
        LayoutInflater inf = getLayoutInflater();
        View v = inf.inflate(R.layout.custom_toast_failed,(ViewGroup) findViewById(R.id.toast_layout_red));
        toastImage = v.findViewById(R.id.toast_image_failed);
        toastText  = v.findViewById(R.id.toast_textView_failed);
        toastImage.setImageResource(R.drawable.ic_failed_sad_toast_white);
        toastText.setText("Oops ! Reset email sending failed ...");
        Toast t = new Toast(getApplicationContext());
        //t.setGravity(Gravity.NO_GRAVITY,0,0);
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(v);
        t.show();
    }
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_forgotPassword);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
