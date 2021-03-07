package com.example.xms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    private EditText email, password;
    private Button login;

    private String smail, spassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        login = (Button) findViewById(R.id.btnlogin);

        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser()!=null){
//            Intent i = new Intent(LoginActivity.this,UserDashboardActivity.class);
//            startActivity(i);
//            finish();
//        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()==true){
                    mAuth.signInWithEmailAndPassword(smail,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this,UserDashboardActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(LoginActivity.this,task.getException().getMessage()+"...try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"Enter Valid Details",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean validate() {

        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();


        boolean valid = true;
        if (TextUtils.isEmpty(smail) || !smail.contains(".com") || !smail.contains("@")) {
            email.setError("Enter a valid email");
            valid = false;
        }
        if (TextUtils.isEmpty(spassword) || spassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            valid = false;
        }
        return valid;
    }
}


