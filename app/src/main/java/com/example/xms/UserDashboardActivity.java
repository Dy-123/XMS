package com.example.xms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentId;
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

    private Button profile,entry,exit,userLog,peoplePresent,social;

    private String fname,lname,pNum;

    String timestamp;
    FirebaseFirestore fbd;
    FirebaseUser user;
    DocumentReference persDetail;


    public static final int requestCode = 2019123;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        social =findViewById(R.id.social);
        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://github.com/SE-IIT2019123/SE-IIT2019123");
            }
        });

        profile = findViewById(R.id.btnProfile);
        entry = findViewById(R.id.btnentry);
        exit = findViewById(R.id.btnexit);
        userLog = findViewById(R.id.btnUserLog);
        peoplePresent = findViewById(R.id.btnPeopleInside);

        fbd = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboardActivity.this,UserDetailActivity.class);
                startActivity(i);
            }
        });



//        ActivityCompat.shouldShowRequestPermissionRationale(UserDashboardActivity.this, Manifest.permission.ACCESS_WIFI_STATE);

        if( ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},requestCode);
        }

//        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);                                  // TODO: Resolve the incosistent BSSID info
//        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//        Toast.makeText(UserDashboardActivity.this, wifiInfo.toString()  ,Toast.LENGTH_LONG).show();


        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                persDetail = fbd.collection("personPresent").document(user.getUid().trim());
                persDetail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Toast.makeText(UserDashboardActivity.this,"Already Inside",Toast.LENGTH_SHORT).show();
                        }else{
                            timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                            Map<String,String> detail = new HashMap<>();
                            detail.put(timestamp.trim(),"Entry");
                            fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());
                            fbd.collection("personPresent").document(user.getUid().trim()).set(detail);
                            Toast.makeText(UserDashboardActivity.this,"Entry Detail Recorded",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                persDetail = fbd.collection("personPresent").document(user.getUid().trim());
                persDetail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            timestamp = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(new Date());
                            Map<String,String> detail = new HashMap<>();
                            detail.put(timestamp.trim(),"Exit");
                            fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());
                            fbd.collection("personPresent").document(user.getUid().trim()).delete();
                            Toast.makeText(UserDashboardActivity.this,"Exit Detail Recorded",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserDashboardActivity.this,"Already Outside",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}
