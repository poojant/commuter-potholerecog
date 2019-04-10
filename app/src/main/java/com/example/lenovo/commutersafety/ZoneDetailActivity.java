package com.example.lenovo.commutersafety;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ZoneDetailActivity extends AppCompatActivity{

private RecyclerView rView;
private DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_detail);

        dbref = FirebaseDatabase.getInstance().getReference().child("Zones");
        dbref.keepSynced(true);

        rView = (RecyclerView)findViewById(R.id.RView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Zone , ZoneViewHolder >firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Zone, ZoneViewHolder>
                (Zone.class,R.layout.zone_row,ZoneViewHolder.class ,dbref) {
            @Override
            protected void populateViewHolder(ZoneViewHolder viewHolder, Zone model, int position) {
                viewHolder.setTitle(model.getZoneTitle());
                viewHolder.setDescription(model.getZoneData());
                viewHolder.setImage(getApplicationContext(),model.getZoneImage());

            }
        };

        rView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ZoneViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public ZoneViewHolder(View itemView){
            super(itemView);
            mview=itemView;
        }

        public void setTitle(String title){
            TextView zone_title = (TextView)mview.findViewById(R.id.textZoneTitle);
            zone_title.setText(title);
        }

        public void setDescription(String description){
            TextView zone_description = (TextView)mview.findViewById(R.id.textZoneDesc);
            zone_description.setText(description);
        }

        public void setImage(Context ctx , String image){
            ImageView zone_image = (ImageView)mview.findViewById(R.id.zone_Images);
            Picasso.with(ctx).load(image).into(zone_image);

        }

    }
}
