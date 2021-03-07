package com.example.xms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {


    private EditText fname, lname, email, password, phone;
//    private ImageView profilePic;
    private Button register;

    private String sfname,slname,smail,spassword,sphone;

    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText) findViewById(R.id.etname);
        lname = (EditText) findViewById(R.id.etsname);
        email = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        phone = (EditText) findViewById(R.id.etphone);
        register = (Button) findViewById(R.id.btnregister);

        sfname = fname.getText().toString().trim();
        slname = lname.getText().toString().trim();
        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();
        sphone = phone.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseFirestore.getInstance();

//        if(mAuth.getCurrentUser()!=null){
//            Intent i = new Intent(RegisterActivity.this,UserDashboardActivity.class);
//            startActivity(i);
//            finish();
//        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()==true){
                    mAuth.createUserWithEmailAndPassword(smail,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();

                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fdb.collection("users").document(userID);
                                Map<String,Object> user = new HashMap<>();
                                user.put("fname",sfname);
                                user.put("lname",slname);
                                user.put("email",smail);
                                user.put("phone",sphone);
//                                user.put("profilePic",profilePic);

                                documentReference.set(user);
//                                OR
//                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                      Log.d(TAG,"data store in database for" + userID);
//                                    }
//                                });

                                Intent i = new Intent(RegisterActivity.this,UserDashboardActivity.class);
                                startActivity(i);

                            }else{
                                Toast.makeText(RegisterActivity.this,task.getException().getMessage()+"...try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(RegisterActivity.this,"Enter Valid Details",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean validate() {

        sfname = fname.getText().toString().trim();
        slname = lname.getText().toString().trim();
        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();
        sphone = phone.getText().toString().trim();

        boolean valid = true;
        if (TextUtils.isEmpty(smail) || !smail.contains(".com") || !smail.contains("@")) {
            email.setError("Enter a valid email");
            valid = false;
        }
        if (TextUtils.isEmpty(spassword) || spassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            valid = false;
        }
        if(TextUtils.isEmpty(sfname)){
            fname.setError("Enter a valid First Name");
            valid = false;
        }
        if(TextUtils.isEmpty(slname)){
            lname.setError("Enter a valid Last Name");
            valid = false;
        }
        if(TextUtils.isEmpty(sphone)){
            phone.setError("Enter a valid phone number");
            valid = false;
        }
        return valid;
    }

}
