package com.example.lenovo.commutersafety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextNumber;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;

    private TextView textViewLogIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button)findViewById(R.id.buttonRegisterRegister);

        editTextName=(EditText)findViewById(R.id.editTextRegisterName);
        editTextEmail = (EditText)findViewById(R.id.editTextRegisterEmail);
        editTextNumber = (EditText)findViewById(R.id.editTextRegisterCell);
        editTextPassword=(EditText)findViewById(R.id.editTextRegisterPassword);
        editTextPasswordConfirm=(EditText)findViewById(R.id.editTextRegisterPasswordConfirm);

        textViewLogIn = (TextView)findViewById(R.id.textViewRegisterLogIn);

        if(UserName.user_Type.equals("NGO")){
            editTextName.setHint("Enter Ngo Name Here");
            editTextEmail.setHint("Enter Ngo Email ID Here");
        }

        buttonRegister.setOnClickListener(this);
        textViewLogIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view == buttonRegister){
            registerUser();
        }
        if(view == textViewLogIn){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String number = editTextNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter Name Of Your College", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "Please Enter Your Cell Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password To Continue", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!TextUtils.equals(password,passwordConfirm)){
            Toast.makeText(this, "Please Enter Correct Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering user .....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if(task.isSuccessful()){
                    addUser();
                    Toast.makeText(RegisterActivity.this, "User Registered Successfully...", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext() , MainActivity.class));
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Registeration Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addUser(){
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String number = editTextNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String category = UserName.user_Type;

        if(!TextUtils.isEmpty(name) && (!TextUtils.isEmpty(email)) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(number) ){
            String UID = firebaseAuth.getUid();

            User user = new User(UID , name , email ,  password , number , category);

            databaseReference.child(UID).setValue(user);
            UserName.current_User=editTextEmail.getText().toString().trim();
            Toast.makeText(this, " All The Details Added Successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please Fill-up All The Details", Toast.LENGTH_SHORT).show();
        }
    }
}