package com.rstudio.hp.attendancesystem;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationAdaptor extends FirestoreRecyclerAdapter<NotificationClass, NotificationAdaptor.NotificationHolder> {

    CardView cardView;
    boolean admin;
    public NotificationAdaptor(@NonNull FirestoreRecyclerOptions<NotificationClass> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull NotificationClass model) {
        holder.description.setText(model.getNotdescription());
        holder.date.setText(model.getNotdate());
        holder.title.setText(model.getNottitle());
    }



    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_layout, viewGroup, false);
        return new NotificationHolder(v);
    }


    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView description;


        public NotificationHolder(View v) {

            super(v);
            cardView = v.findViewById(R.id.cardView_Notification);
            title = v.findViewById(R.id.tv_Title_NotificationLayout);
            date = v.findViewById(R.id.tv_Date_NotificationLayout);
            description = v.findViewById(R.id.tv_Description_NotificationLayout);
        }

    }
}
