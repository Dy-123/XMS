package com.example.xms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class UpdateLoginCredentialsActivity extends Activity {

    private TextView cemail,nemail,rnemail,npass,rnpass;
    private Button update,back;

    private String snmail,srnmail,snpass,srnpass;

    FirebaseUser user;
    FirebaseFirestore fdb;
    DocumentReference docRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatelogincredentials);

        cemail = findViewById(R.id.txtcureentemail);
        nemail = findViewById(R.id.txtnewemail);
        rnemail = findViewById(R.id.txtreenternewemail);
        npass = findViewById(R.id.txtnewpass);
        rnpass = findViewById(R.id.txtreenternewpass);
        update = findViewById(R.id.btnupdatecred);
        back = findViewById(R.id.btnupdateback);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        docRef = fdb.collection("users").document(user.getUid().trim());

        cemail.setText(user.getEmail().trim());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                snmail = nemail.getText().toString().trim();
                srnmail = rnemail.getText().toString().trim();
                snpass = npass.getText().toString().trim();
                srnpass = rnpass.getText().toString().trim();

                int count=0;

                if(TextUtils.isEmpty(snmail)==false){
                    if(validmail()==true){
                        user.updateEmail(snmail);
                        docRef.update("email",snmail);

                        count++;
                    }

                }

                if(TextUtils.isEmpty(snpass)==false){
                    if(validpass()==true){
                        user.updatePassword(snpass);
                        docRef.update("password",snpass);
                        count++;
                    }
                }

                if(count!=0){
                    Toast.makeText(UpdateLoginCredentialsActivity.this,"Login Credentials Updated",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateLoginCredentialsActivity.this,UserDashboardActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateLoginCredentialsActivity.this,UserDetailActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private boolean validmail(){
        boolean vm = true;

        if ( !snmail.contains(".") || !snmail.contains("@")) {
            nemail.setError("Enter a valid email");
            vm = false;
        }

//        https://stackoverflow.com/questions/6819604/comparison-of-two-strings-doesnt-work-in-android
//        if(snmail != srnmail){
//            rnemail.setError("Re-enter correct email");
//            vm = false;
//        }

        if(snmail.equals(srnmail) == false ){
            rnemail.setError("Re-enter correct email");
            vm = false;
        }

        return  vm;
    }

    private boolean validpass(){
        boolean vp = true;

        if (snpass.length() < 8 ) {
            npass.setError("Password must be at least 8 characters long");
            vp = false;
        }

        if( snpass.equals(srnpass) == false){
            rnpass.setError("Password Not Matched");
            vp = false;
        }


        return  vp;
    }

}
