package com.example.lenovo.commutersafety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectionCategory extends AppCompatActivity implements View.OnClickListener {

    Button student , faculty , NGO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_category);

        student = (Button) findViewById(R.id.buttonSelectionStudent);
        faculty = (Button) findViewById(R.id.buttonSelectionFaculty);
        NGO = (Button) findViewById(R.id.buttonSelectionNGO);

        student.setOnClickListener(this);
        faculty.setOnClickListener(this);
        NGO.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view==student){
            UserName.user_Type="commuter";
            Intent intent = new Intent(SelectionCategory.this, LoginActivity.class);
            startActivity(intent);
        }

        if(view==faculty){
            UserName.user_Type="admin";
            Intent intent = new Intent(SelectionCategory.this, LoginActivity.class);
            startActivity(intent);
        }

        if(view==NGO){
            UserName.user_Type="guest";
            Intent intent = new Intent(SelectionCategory.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}
