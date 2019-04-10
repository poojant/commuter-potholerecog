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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogin;

    private EditText editTextEmail;

    private EditText editTextPassword;

    private TextView textViewLoginRegister;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonLogin = (Button)findViewById(R.id.buttonLoginLogin);

        editTextEmail = (EditText)findViewById(R.id.editTextLoginEmail);

        editTextPassword=(EditText)findViewById(R.id.editTextLoginPassword);

        textViewLoginRegister = (TextView)findViewById(R.id.textViewLoginRegister);

        buttonLogin.setOnClickListener(this);
        textViewLoginRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            logInUser();
        }
        if(view == textViewLoginRegister){
            startActivity(new Intent(this , RegisterActivity.class));
        }
    }

    private void logInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password To Continue", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Authenticating, Please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

