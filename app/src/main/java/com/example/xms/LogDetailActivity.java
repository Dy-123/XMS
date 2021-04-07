package com.example.xms;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class LogDetailActivity extends Activity {

    RecyclerView rv;

    FirebaseUser user;
    DocumentReference documentReference;
    FirebaseFirestore fbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logdetail);

        rv = findViewById(R.id.rv);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fbd = FirebaseFirestore.getInstance();
        documentReference = fbd.collection("UserLogDetail").document(user.getUid().trim());


        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> details = new ArrayList<>();
                Map<String,Object> map = documentSnapshot.getData();
                if(map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        details.add(entry.getValue().toString().trim() + "  " +entry.getKey().trim());
                        rv.setLayoutManager(new LinearLayoutManager(LogDetailActivity.this));
                        MyAdapter adapter = new MyAdapter(LogDetailActivity.this,details);
                        rv.setAdapter(adapter);
                    }
                }else{
                    Toast.makeText(LogDetailActivity.this,"No record found",Toast.LENGTH_SHORT).show();
                }

            }
        });






    }
}
