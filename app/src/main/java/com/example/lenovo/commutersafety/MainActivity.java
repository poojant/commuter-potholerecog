package com.example.lenovo.commutersafety;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String uid;
    public int check=0;

    private TextView textViewName;

    private Button logout;
    private Button maps;
    private Button createWishList;
    private Button showWishList;
    private Button showDonatedItems;
    private Button agentChatBot;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    DatabaseReference dataReference;

    private Button buttonData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonData = (Button) findViewById(R.id.save_data_activity);
        buttonData.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        dataReference = FirebaseDatabase.getInstance().getReference();

        textViewName = (TextView)findViewById(R.id.textViewDashboardName);
        maps = (Button)findViewById(R.id.maps);
        logout = (Button)findViewById(R.id.buttonDashboardLogout);
        createWishList = (Button)findViewById(R.id.buttonDashboardCreateWishList);
        showWishList = (Button)findViewById(R.id.buttonDashboardShowWishList);
        showDonatedItems = (Button)findViewById(R.id.buttonDashboardShowDonate);
        agentChatBot = (Button)findViewById(R.id.buttonDashboardAgentChatBox);

        maps.setOnClickListener(this);
        createWishList.setOnClickListener(this);
        showDonatedItems.setOnClickListener(this);
        showWishList.setOnClickListener(this);
        agentChatBot.setOnClickListener(this);
        logout.setOnClickListener(this);

        if(UserName.user_Type.equals("guest")){

            showWishList.setVisibility(View.INVISIBLE);
        }

        if(UserName.user_Type.equals("admin")){

            createWishList.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onClick(View view) {
        if(view == logout){
            firebaseAuth.signOut();
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


        if(view == agentChatBot){
            //finish();
           /* Intent intent = new Intent(Dashboard.this, AgentChatBot.class);
            startActivity(intent);*/

            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        }

        if(view == maps){
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }

        if(view == createWishList){
            Intent intent = new Intent(MainActivity.this, CapturePictureActivity.class);
            startActivity(intent);
        }

        if(view==buttonData){
            Intent intent = new Intent(MainActivity.this, ZoneDetailActivity.class);
            startActivity(intent);
        }
    }

}
