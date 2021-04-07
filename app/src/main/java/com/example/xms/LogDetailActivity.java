package com.example.xms;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LogDetailActivity extends Activity {

    FirebaseUser user;
    DocumentReference documentReference;
    FirebaseFirestore fbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logdetail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fbd = FirebaseFirestore.getInstance();
        documentReference = fbd.collection("UserLogDetail").document(user.getUid().trim());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });






    }
}
