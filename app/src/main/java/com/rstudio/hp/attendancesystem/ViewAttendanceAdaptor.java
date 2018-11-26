package com.rstudio.hp.attendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAttendanceAdaptor extends ArrayAdapter<String> {
    private String[] name;
    private Context context;
    public String[] percentage;

    public ViewAttendanceAdaptor(Context context, String[] name, String[] percentage) {
        super(context, R.layout.listview_view_attendance,name);
        this.context = context;
        this.name = name;
        this.percentage = percentage;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_view_attendance, null, true);

        TextView name_tv = rowView.findViewById(R.id.listAdaptor_Name);
        TextView rollno_tv = rowView.findViewById(R.id.listAdaptor_rollno);
        TextView percentage_tv = rowView.findViewById(R.id.listAdaptor_percentage);
        name_tv.setText(name[position]);
        String roll = Integer.toString(position+1)+". ";
        rollno_tv.setText(roll);
        percentage_tv.setText(percentage[position]);
        return rowView;

    }

    ;
}
