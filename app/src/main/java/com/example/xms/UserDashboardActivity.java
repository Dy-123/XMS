package com.example.xms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserDashboardActivity extends Activity {

    private Button profile,entry,exit,userLog,peoplePresent;

    private String fname,lname,pNum;

    String timestamp;
    FirebaseFirestore fbd;
    FirebaseUser user;
//    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        profile = findViewById(R.id.btnProfile);
        entry = findViewById(R.id.btnentry);
        exit = findViewById(R.id.btnexit);
        userLog = findViewById(R.id.btnUserLog);
        peoplePresent = findViewById(R.id.btnPeopleInside);

        fbd = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

//        documentReference = fbd.collection("users").document(user.getUid().toString().trim());
//
//        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                fname = documentSnapshot.get("firstname").toString().trim();
//                lname = documentSnapshot.get("lastname").toString().trim();
//                pNum = documentSnapshot.get("phone").toString().trim();
//            }
//        });


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
                timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                Map<String,String> detail = new HashMap<>();
                detail.put(timestamp.trim(),"Entry");
                fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());

//                Map<String,String> personPresent = new HashMap<>();
//
//                personPresent.put(fname + " " + lname, pNum + " " + user.getEmail() );

                fbd.collection("personPresent").document(user.getUid().trim()).set(detail);

                Toast.makeText(UserDashboardActivity.this,"Entry Detail Recorded",Toast.LENGTH_SHORT).show();


            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(new Date());


                Map<String,String> detail = new HashMap<>();
                detail.put(timestamp.trim(),"Exit");
                fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());

                fbd.collection("personPresent").document(user.getUid().trim()).delete();

                Toast.makeText(UserDashboardActivity.this,"Exit Detail Recorded",Toast.LENGTH_SHORT).show();


            }
        });

        userLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboardActivity.this,LogDetailActivity.class);
                startActivity(i);
            }
        });

        peoplePresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbd.collection("personPresent").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }

                            Toast.makeText(UserDashboardActivity.this,"Number of people present is = " + count,Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
            }
        });


    }

}
