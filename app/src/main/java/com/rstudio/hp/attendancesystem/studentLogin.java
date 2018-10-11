package com.rstudio.hp.attendancesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.support.design.widget.Snackbar;

public class studentLogin extends AppCompatActivity {



    EditText signinEmail_et,signinPass_et;
    TextView signup_tv,forgot_pass_tv;
    Button login_bt;
    ProgressDialog pgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        setValues();
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            finish();
            startActivity(new Intent(studentLogin.this,StudentMainPage.class));
        }
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkUserInput()){
                    firebaseLogin();
                    }
                }
        });
        signup_tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(studentLogin.this,studentSignUp.class));
            }
        });
        forgot_pass_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(studentLogin.this,ForgotPassword.class));
            }
        });

    }
    private void setValues(){
        //initialise values to variables
        this.login_bt= findViewById(R.id.bt_studentSignin);
        signup_tv= findViewById(R.id.tv_studentCreateAccount);
        signinEmail_et = findViewById(R.id.et_studentEmail);
        signinPass_et = findViewById(R.id.et_studentPassword);
        forgot_pass_tv = findViewById(R.id.tv_forgotPasswordLogin);
        forgot_pass_tv.setVisibility(View.INVISIBLE);
        pgDialog = new ProgressDialog(studentLogin.this);
    }
    private boolean checkUserInput(){
        String email,pass;
        email= signinEmail_et.getText().toString().trim();
        pass=signinPass_et.getText().toString().trim();
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if(pass.isEmpty()){
                signinPass_et.setError("Enter Password");
                return false;
            }
            else if(pass.length()<6){
                signinPass_et.setError("Min 6 chars");
                return false;
            }
            else{
                return true;
            }
        }else{
            signinEmail_et.setError("Enter a valid email");
            return false;
        }
    }    //check user login input
    private void firebaseLogin(){   //firebase login method
        String mail,pass;
        mail= signinEmail_et.getText().toString();
        pass= signinPass_et.getText().toString();
        pgDialog.setMessage("Verifying User");
        pgDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pgDialog.dismiss();
                   finish();
                   startActivity(new Intent(studentLogin.this,StudentMainPage.class));
                }else{
                    forgot_pass_tv.setVisibility(View.VISIBLE);
                    pgDialog.dismiss();
                    try {
                        Snackbar.make(findViewById(android.R.id.content), task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }catch (NullPointerException npException){
                        npException.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
