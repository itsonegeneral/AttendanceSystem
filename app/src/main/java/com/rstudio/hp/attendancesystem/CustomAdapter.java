package com.rstudio.hp.attendancesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {

    public ArrayList dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        CheckBox checkBox;
    }

    public CustomAdapter(ArrayList data, Context context) {
        super(context, R.layout.students_list_view, data);
        this.dataSet = data;
        this.mContext = context;

    }
    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public  ItemStudentAttendance getItem(int position) {
        return (ItemStudentAttendance)dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.students_list_view, parent, false);
            viewHolder.txtName =  convertView.findViewById(R.id.tv_listView_studentName);
            viewHolder.checkBox =  convertView.findViewById(R.id.checkbox_listView_student);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    ItemStudentAttendance item = (ItemStudentAttendance)dataSet.get(getPosition);
                    item.setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }

            });
            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        ItemStudentAttendance item = getItem(position);

        viewHolder.checkBox.setTag(position); // This line is important.
        viewHolder.txtName.setText(Integer.toString(position+1)+". "+item.studentName);
        viewHolder.checkBox.setChecked(item.present);

        return result;
    }
}