package com.example.xms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserDashboardActivity extends Activity {

    private Button profile,entry,exit,userLog,peoplePresent,social;
    private TextView txtbssid;

    private String fname,lname,pNum;
    private String BSSID,probBSSID = "02:00:00:00:00:00";

    SharedPreferences sharedPreferences;

    String timestamp;
    FirebaseFirestore fbd;
    FirebaseUser user;
    DocumentReference persDetail,macAdd;


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
        txtbssid = findViewById(R.id.txtbssid);

        fbd = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashboardActivity.this,UserDetailActivity.class);
                startActivity(i);
            }
        });

        macAdd = fbd.collection("MAC-Addresses").document("MAC");


        List<String> details = new ArrayList<>();
        macAdd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String,Object> mp= document.getData();
                        for (Map.Entry<String, Object> entry : mp.entrySet()) {
                            details.add(entry.getKey().trim());
                        }
                    }
                }
            }
        });

        Log.i("Details",details.toString());

        sharedPreferences = getSharedPreferences("XMS.MYXMSPROJECT2019123",Context.MODE_PRIVATE);

        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                txtbssid.setText(wifiInfo.getBSSID());
                BSSID = wifiInfo.getBSSID();

                if(BSSID == null ){
                    Toast.makeText(UserDashboardActivity.this,"Connect to a Secure a WiFi Network",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(i);
                }else {
                    BSSID = BSSID.trim();
                    if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UserDashboardActivity.this, "Connect to a secure WiFi", Toast.LENGTH_SHORT).show();
                    } else {

                        if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

                                SharedPreferences.Editor spref = sharedPreferences.edit();
                                spref.putString("LocationNever","Y");
                                spref.apply();

                                new AlertDialog.Builder(UserDashboardActivity.this).setTitle("Location Permission").setMessage("If you want to log the entry exit detail from this device then location permission is needed in order to check whether the device is connected to secure WiFi. ").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                            }else if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED && sharedPreferences.getString("LocationNever","N").equals("Y")) {
                                Toast.makeText(UserDashboardActivity.this, "Location Permission Needed", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",UserDashboardActivity.this.getPackageName(),null);
                                i.setData(uri);
                                startActivity(i);
                            }else{
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                            }
                        }


                        if(ContextCompat.checkSelfPermission(UserDashboardActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                            persDetail = fbd.collection("personPresent").document(user.getUid().trim());
                            persDetail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if (BSSID.equals(probBSSID) && ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(UserDashboardActivity.this, "Turn On Location", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(i);
                                    } else if (documentSnapshot.exists()) {
                                        Toast.makeText(UserDashboardActivity.this, "Already Inside", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (BSSID.equals(probBSSID) && ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && details.contains(BSSID) == true) {
                                            timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                                            Map<String, String> detail = new HashMap<>();
                                            detail.put(timestamp.trim(), "Entry");
                                            fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());
                                            fbd.collection("personPresent").document(user.getUid().trim()).set(detail);
                                            Toast.makeText(UserDashboardActivity.this, "Entry Detail Recorded", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserDashboardActivity.this, "Not on secure Connection to log the details", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                txtbssid.setText(wifiInfo.getBSSID());
                BSSID = wifiInfo.getBSSID();

                if(BSSID == null ){
                    Toast.makeText(UserDashboardActivity.this,"Connect to a Secure a WiFi Network",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(i);
                }else {

                    BSSID = BSSID.trim();
                    if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UserDashboardActivity.this, "Connect to a secure WiFi", Toast.LENGTH_SHORT).show();
                    } else {

                        if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                                SharedPreferences.Editor spref = sharedPreferences.edit();
                                spref.putString("LocationNever", "Y");
                                spref.apply();

                                new AlertDialog.Builder(UserDashboardActivity.this).setTitle("Location Permission").setMessage("If you want to log the entry exit detail from this device then location permission is needed in order to check whether the device is connected to secure WiFi. ").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                            } else if (ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED && sharedPreferences.getString("LocationNever", "N").equals("Y")) {
                                Toast.makeText(UserDashboardActivity.this, "Location Permission Needed", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", UserDashboardActivity.this.getPackageName(), null);
                                i.setData(uri);
                                startActivity(i);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                            }
                        }
                    }

                    if(ContextCompat.checkSelfPermission(UserDashboardActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                        persDetail = fbd.collection("personPresent").document(user.getUid().trim());
                        persDetail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (BSSID.equals(probBSSID) && ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(UserDashboardActivity.this, "Turn On Location", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(i);
                                } else if (documentSnapshot.exists()) {
                                    if (!BSSID.equals(probBSSID) && ContextCompat.checkSelfPermission(UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && details.contains(BSSID) == true) {
                                        timestamp = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(new Date());
                                        Map<String, String> detail = new HashMap<>();
                                        detail.put(timestamp.trim(), "Exit");
                                        fbd.collection("UserLogDetail").document(user.getUid().trim()).set(detail, SetOptions.merge());
                                        fbd.collection("personPresent").document(user.getUid().trim()).delete();
                                        Toast.makeText(UserDashboardActivity.this, "Exit Detail Recorded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(UserDashboardActivity.this, "Not on secure Connection to log the details", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(UserDashboardActivity.this, "Already Outside", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

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
