package com.rstudio.hp.attendancesystem;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class adminLogin extends AppCompatActivity {

    int chance=5;
    final int USERID=7907;
    final int pin = 8241;
    EditText _et_adminLoginPass,_et_adminLoginUser;
    Button loginbtn;
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
                            finish();
                            startActivity(new Intent(adminLogin.this, AdminMenuActivity.class));
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
        }
        else if((Integer.parseInt(u)==USERID)){
            if((Integer.parseInt(p)==pin)) {
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        if(chance<1){
            Snackbar.make(findViewById(android.R.id.content),"0 chances remaining",Snackbar.LENGTH_SHORT).show();
            loginbtn.setClickable(false);
            return false;
        }
        Snackbar.make(findViewById(android.R.id.content),chance+" chances remaining",Snackbar.LENGTH_SHORT).show();
        return false;
    }
}
