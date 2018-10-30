package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;

public class adminLogin extends AppCompatActivity {

    int chance=5;
    final int USERID=7907;
    final int pin = 8241;
    EditText _et_adminLoginPass,_et_adminLoginUser;
    Button loginbtn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        setUpToolbar();
        loginbtn =findViewById(R.id.bt_adminSignin);
        _et_adminLoginPass = findViewById(R.id.et_adminLoginPass);
        _et_adminLoginUser = findViewById(R.id.et_adminLoginID);


        loginbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkUserInput()) {
                            firebaseLogin();
                        }
                    }
                });
    }
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.adminmaintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Login");
    }
    private boolean checkUserInput(){
        String p,u;
        chance --;
        u= _et_adminLoginUser.getText().toString();
        p= _et_adminLoginPass.getText().toString();

        if(u.isEmpty()|| p.isEmpty()){
            if(u.isEmpty())
            _et_adminLoginUser.setError("Enter User Id");
            if(p.isEmpty())
            _et_adminLoginPass.setError("Enter Pin");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(u).matches() ){
            _et_adminLoginUser.setError("Enter a valid email");
        }else{
            return true;
        }
        return false;
    }
    private void firebaseLogin(){
        final String email  = _et_adminLoginUser.getText().toString().trim();
        final String pass = _et_adminLoginPass.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            isAdmin(email,pass);
                        }else{
                            Toast.makeText(getApplicationContext(),"Error AE001",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(adminLogin.this,studentLogin.class));
                        }
                    }
                });
    }
    private void isAdmin(String email,String pass){

        DatabaseReference dbRef  = FirebaseDatabase.getInstance().getReference("Admins").child(firebaseAuth.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    finish();
                    startActivity(new Intent(adminLogin.this,AdminMenuActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error 107",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
