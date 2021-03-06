package com.example.xms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText username, password;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.idusername);
        password = (EditText) findViewById(R.id.etpassword);
        register = (Button) findViewById(R.id.idlogin);

        register.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){

           }
        });


}
