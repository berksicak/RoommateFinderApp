package com.example.roommatefinderapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roommatefinderapp.R;
import com.example.roommatefinderapp.activities.MapsActivity;
import com.example.roommatefinderapp.activities.ViewProfileActivity;
import com.example.roommatefinderapp.entities.User;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private List<User> users;
    private int pos;

    public RecyclerAdapter(List<User> users, Context context) {
        this.users = users;
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private TextView nameText, distanceText, durationText, departmentText;
        private ImageView profileImage, mapImage;
        private Button matchBtn;
        private ConstraintLayout constraintLayout;
        private CardView cardView;


        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.text_name);
            distanceText = (TextView) view.findViewById(R.id.text_distance);
            durationText = (TextView) view.findViewById(R.id.text_duration);
            profileImage = (ImageView) view.findViewById(R.id.image_profile);
            mapImage = (ImageView) view.findViewById(R.id.image_map);
            matchBtn = (Button) view.findViewById(R.id.match_button);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraintLayout);
            cardView = (CardView) view.findViewById(R.id.card_view);
            departmentText = (TextView) view.findViewById(R.id.text_department);

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User myUser = users.get(pos);
                    Context context1 = view.getContext();
                    Intent intent = new Intent(context1, ViewProfileActivity.class);
                    intent.putExtra("lookAtToProfileID", myUser.getUserID());
                    context1.startActivity(intent);
                }
            });

            mapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void setData(User user, int position){
            nameText.setText(user.getName()+" "+user.getSurname());
            distanceText.setText("Okula " + String.valueOf(user.getDistanceToCampus()) + " km uzaklıktadır.");
            durationText.setText(user.getDuration() + " süreliğine.");
            departmentText.setText(user.getDepartment() + " bölümünde " + user.getYear().getYear() + " öğrencisi");
            pos = position;
        }
    }
    @NonNull
    @Override
    public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        return new RecyclerViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.RecyclerViewHolder holder, int position) {
        User user = users.get(position);
        holder.setData(user, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
