package com.rstudio.hp.attendancesystem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button studentPage,adminPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        studentPage= findViewById(R.id.bt_studentPage);
        adminPage= findViewById(R.id.bt_adminPage);
        studentPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOnline())
                startActivity(new Intent(MainActivity.this, com.rstudio.hp.attendancesystem.studentLogin.class));
            }
        });
        adminPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOnline())
                startActivity(new Intent(MainActivity.this, com.rstudio.hp.attendancesystem.adminLogin.class));
            }
        });
    }
    private  boolean checkOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;

        } else {
            Snackbar.make(findViewById(android.R.id.content),"Internet connection unavailable",Snackbar.LENGTH_SHORT).show();

            return false;

        }
    }
}
