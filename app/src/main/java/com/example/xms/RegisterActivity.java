package com.example.xms;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {


    private EditText fname, lname, email, password, phone;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fname = (EditText) findViewById(R.id.etname);
        lname = (EditText) findViewById(R.id.etsname);
        email = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        phone = (EditText) findViewById(R.id.etphone);
        register = (Button) findViewById(R.id.btnRegister);
    }


}
