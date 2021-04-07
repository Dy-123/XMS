package com.example.xms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDashboardActivity extends Activity {

    private Button profile,entry,exit,userLog;
    String timestamp;
    FirebaseFirestore fbd;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        profile = findViewById(R.id.btnProfile);
        entry = findViewById(R.id.btnentry);
        exit = findViewById(R.id.btnexit);
        userLog = findViewById(R.id.btnUserLog);

        fbd = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboardActivity.this,UserDetailActivity.class);
                startActivity(i);
            }
        });

        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(new Date());

                Map<String,String> detail = new HashMap<>();

                detail.put(timestamp.trim(),"Entry");

                fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());

//                Toast.makeText(UserDashboardActivity.this,timestamp.toString().trim(),Toast.LENGTH_SHORT).show();

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(new Date());


                Map<String,String> detail = new HashMap<>();

                detail.put(timestamp.trim(),"Exit");

                fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());

            }
        });

        userLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboardActivity.this,LogDetailActivity.class);
                startActivity(i);
            }
        });


    }

}
