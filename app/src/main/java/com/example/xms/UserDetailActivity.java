package com.example.xms;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.grpc.Context;

import static android.content.ContentValues.TAG;


public class UserDetailActivity extends Activity {

    private TextView fname,lname,phone;
    private ImageView profilePic;

    FirebaseUser user;
    FirebaseFirestore fdb;
    StorageReference storageRef;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        fname = findViewById(R.id.txtfname);
        lname = findViewById(R.id.txtlname);
        phone = findViewById(R.id.txtphone);
        profilePic = findViewById(R.id.userprofilePic);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){

            fdb = FirebaseFirestore.getInstance();
            DocumentReference docRef = fdb.collection("users").document(user.getUid().trim());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    fname.setText(documentSnapshot.get("firstName").toString().trim());
                    lname.setText(documentSnapshot.get("lastName").toString().trim());
                    phone.setText(documentSnapshot.get("phone").toString().trim());
                }
            });
        }else{
            Toast.makeText(UserDetailActivity.this,"Please login the user",Toast.LENGTH_SHORT).show();
        }

        storageRef = FirebaseStorage.getInstance().getReference().child("profilePicture/"+user.getUid().trim()+".jpg");
        Glide.with(this).load(storageRef).into(profilePic);                                                                 // TODO Resolve glide error


    }



}
