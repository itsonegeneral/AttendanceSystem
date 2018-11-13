package com.rstudio.hp.attendancesystem;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NotificationAdaptorAdmin extends FirestoreRecyclerAdapter<NotificationClass, NotificationAdaptorAdmin.NotificationHolder> {

    private CardView cardView;
    public NotificationAdaptorAdmin(@NonNull FirestoreRecyclerOptions<NotificationClass> options) {
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_layout_admin, viewGroup, false);
        return new NotificationHolder(v);
    }
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView description;


        public NotificationHolder(View v) {

            super(v);
            cardView = v.findViewById(R.id.cardView_NotificationAdmin);
            title = v.findViewById(R.id.tv_Title_NotificationLayoutAdmin);
            date = v.findViewById(R.id.tv_Date_NotificationLayoutAdmin);
            description = v.findViewById(R.id.tv_Description_NotificationLayoutAdmin);
        }

    }
}
