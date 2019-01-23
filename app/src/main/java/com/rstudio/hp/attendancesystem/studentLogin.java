package com.rstudio.hp.attendancesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.design.widget.Snackbar;

public class studentLogin extends AppCompatActivity {

    private static final String TAG = "studentLogin";
    EditText signinEmail_et, signinPass_et;
    TextView signup_tv, forgot_pass_tv, admin_login_tv;
     Button login_bt;
    boolean admin = false;
    ProgressDialog pgDialog;
    ImageView art_image;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        setValues();
        showAnimations();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            pgDialog.setMessage("Auto Login");
            pgDialog.show();
            isAdmin();
        }
      login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkOnline()) {
                    if (checkUserInput()) {
                        firebaseLogin();
                    }
                }
            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(studentLogin.this, studentSignUp.class));
            }
        });
        forgot_pass_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(studentLogin.this, ForgotPassword.class));
            }
        });
        admin_login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(studentLogin.this, adminLogin.class));
            }
        });

    }

    private void setValues() {
        //initialise values to variables

        this.login_bt = findViewById(R.id.bt_studentSignin);
        signup_tv = findViewById(R.id.tv_studentCreateAccount);
        signinEmail_et = findViewById(R.id.et_studentEmail);
        linearLayout = findViewById(R.id.linearLayout_studentLogin);
        signinPass_et = findViewById(R.id.et_studentPassword);
        art_image = findViewById(R.id.imageView);
        forgot_pass_tv = findViewById(R.id.tv_forgotPasswordLogin);
        forgot_pass_tv.setVisibility(View.INVISIBLE);
        pgDialog = new ProgressDialog(studentLogin.this);
        admin_login_tv = findViewById(R.id.tv_AdminLogin);
    }

    private void isAdmin() {
        String mail = FirebaseAuth.getInstance().getUid();
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference("Admins").child(mail);
        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(),"Admin account detected",Toast.LENGTH_SHORT)
                            .show();
                    finish();
                    startActivity(new Intent(studentLogin.this,adminLogin.class));
                    pgDialog.dismiss();
                } else {
                    pgDialog.dismiss();
                    pgDialog.cancel();
                    startActivity(new Intent(studentLogin.this, StudentMainPage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pgDialog.dismiss();
                Log.d(TAG, databaseError.getMessage());
            }
        });

        /* return admin; */
    }

    private boolean checkUserInput() {
        String email, pass;
        email = signinEmail_et.getText().toString().trim();
        pass = signinPass_et.getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (pass.isEmpty()) {
                signinPass_et.setError("Enter Password");
                return false;
            } else if (pass.length() < 6) {
                signinPass_et.setError("Min 6 chars");
                return false;
            } else {
                return true;
            }
        } else {
            signinEmail_et.setError("Enter a valid email");
            return false;
        }
    }    //check user login input

    private void firebaseLogin() {   //firebase login method
        final String mail, pass;
        mail = signinEmail_et.getText().toString();
        pass = signinPass_et.getText().toString();
        pgDialog.setMessage("Logging In");
        pgDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    isAdmin();
                } else {
                    forgot_pass_tv.setVisibility(View.VISIBLE);
                    pgDialog.dismiss();
                    try {
                        Snackbar.make(findViewById(android.R.id.content), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    } catch (NullPointerException npException) {
                        npException.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void showAnimations(){
        Animation anim_slide_down = AnimationUtils.loadAnimation(this,R.anim.slide_down);
        Animation anim_move_up = AnimationUtils.loadAnimation(this,R.anim.move_up);
        anim_slide_down.setDuration(700);
        anim_move_up.setDuration(700);
        art_image.startAnimation(anim_slide_down);
        linearLayout.startAnimation(anim_move_up);
    }

    private boolean checkOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;

        } else {
            Snackbar.make(findViewById(android.R.id.content), "Internet connection unavailable", Snackbar.LENGTH_SHORT).show();

            return false;

        }
    }
}
