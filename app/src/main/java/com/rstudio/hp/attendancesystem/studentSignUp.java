package com.rstudio.hp.attendancesystem;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.google.firebase.auth.FirebaseAuth.*;

public class studentSignUp extends AppCompatActivity {
    Spinner spinner;
    EditText studentName,studentPass1,studentPass2,studentEmail,studentRollNo;
    Button createAccountBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database ;
    ImageView imageView;
    ProgressDialog pgBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up);
        setUpToolbar();
        setValues();
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Profile pic feature coming soon",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toobar_registration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");
    }
    private void setValues(){
        spinner = findViewById(R.id.spinnerBatch);
        spinner.setPrompt("Select Batch");
        studentName= findViewById(R.id.et_newStudentName);
        studentPass1= findViewById(R.id.et_newStudentPassword);
        studentPass2= findViewById(R.id.et_newStudentPasswordRetype);
        studentEmail = findViewById(R.id.et_newStudentEmail);
        createAccountBtn= findViewById(R.id.bt_createStudentAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        studentRollNo =findViewById(R.id.et_newStudentRollNo);
        imageView = findViewById(R.id.imageView);
        pgBar = new ProgressDialog(studentSignUp.this);

    }
    private void createUserAccount(){
        if(checkUserInput()){
            createFirebaseAccount();
        }
    }
    private void createFirebaseAccount(){
        String pass,email;
        email= studentEmail.getText().toString();
        pass = studentPass1.getText().toString().trim();
        pgBar.setMessage("Creating Account");
        pgBar.show();
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                    firebaseAuth=getInstance();
                    uploadUserDetails();
                     firebaseAuth.signOut();
                     pgBar.dismiss();
                    finish();

                }else{
                    pgBar.dismiss();
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User already exists",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Account creation failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean checkUserInput(){
        boolean key = false;
        String pass1,pass2;
        String sname,semail;
        int rollno;
        String rno=studentRollNo.getText().toString();
        if(rno.isEmpty()){
            studentRollNo.setError("Enter Roll no ");
        }else{
            rollno=Integer.parseInt(rno);
            if(rollno>40||rollno<0){
                studentRollNo.setError("Enter Valid no");
            }
        }
        pass1=studentPass1.getText().toString().trim();
        pass2=studentPass2.getText().toString().trim();
        sname = studentName.getText().toString();
        semail= studentEmail.getText().toString();
        if(sname.isEmpty()){
            studentName.setError("Name Empty");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(semail).matches()){
            studentEmail.setError("Email not valid");
        }else if(rno.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Roll No",Toast.LENGTH_SHORT).show();
        }else if(Integer.parseInt(rno)<0||Integer.parseInt(rno)>40){
            Toast.makeText(getApplicationContext(),"Enter Valid Roll No",Toast.LENGTH_SHORT).show();
        }else if(pass1.isEmpty()||pass2.isEmpty()){
            if(pass1.isEmpty()){
                studentPass1.setError("Cannot be empty");
            }else{
                studentPass2.setError("Confirm Password");
            }
        }else if(!pass1.equals(pass2)){
            studentPass2.setError("Passwords donot match");
        }else if(pass1.length()<6){
            studentPass1.setError("Min 6 chars");
        }else{
            key = true;
        }

        return key;
    }
    private void uploadUserDetails(){
        String name = studentName.getText().toString();
        String batch = "BCA";
        String no =studentRollNo.getText().toString();
        int rollno =Integer.parseInt(no);
        database = FirebaseDatabase.getInstance();
        Student student = new Student(name,batch,rollno);
        DatabaseReference uuidRef= FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        uuidRef.setValue(student);
        DatabaseReference rlist = database.getReference("Registered Students");
        rlist.child(batch).push().setValue(name);
    }
}
