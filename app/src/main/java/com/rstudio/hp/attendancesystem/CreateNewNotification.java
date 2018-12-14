package com.rstudio.hp.attendancesystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class CreateNewNotification extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText mTitle;
    private EditText mDesc;
    private Button create_btn;
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_notification);
        mTitle = findViewById(R.id.et_newNotTitle);
        mDesc = findViewById(R.id.et_newNotDescription);
        create_btn = findViewById(R.id.bt_createNewNotif);
        tv_date = findViewById(R.id.tv_newNotDate);
        setUpDatePicker();

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();
                if (!checkInput(title, desc)) {
                    // upload data to firebase
                }
            }
        });
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
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String date;
        Calendar c = Calendar.getInstance();
        c.set(i, i1, i2);
        date = DateFormat.getDateInstance().format(c.getTime());
        tv_date.setText(date);
    }
}
