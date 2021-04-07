package com.example.xms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {


    private EditText fname, lname, email, password, phone;

    private ImageView profilePic;
    private Button camera;
//    private Button gallery;
    private int permissioncode = 1 ;

    private Button register;

    private String sfname,slname,smail,spassword,sphone;

    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    private String userID;
    StorageReference storageRef;
    Uri fileloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText) findViewById(R.id.etname);
        lname = (EditText) findViewById(R.id.etsname);
        email = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        phone = (EditText) findViewById(R.id.etphone);
        register = (Button) findViewById(R.id.btnregister);

        profilePic = (ImageView) findViewById(R.id.profilePic);
        camera = (Button) findViewById(R.id.btncamera);
//        gallery = (Button) findViewById(R.id.btngallery);

        sfname = fname.getText().toString().trim();
        slname = lname.getText().toString().trim();
        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();
        sphone = phone.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

//        if(mAuth.getCurrentUser()!=null){
//            Intent i = new Intent(RegisterActivity.this,UserDashboardActivity.class);
//            startActivity(i);
//            finish();
//        }

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {                  //Checking if camera permission has been provided or not
//                  Toast.makeText(RegisterActivity.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                } else {                                                                                                                                                  // if camera permission has not been provided then
                    requestCameraPermission();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()==true){
                    mAuth.createUserWithEmailAndPassword(smail,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();

                                userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fdb.collection("users").document(userID);                                  // creating a document reference of userID inside users collection
                                Map<String,Object> user = new HashMap<>();
                                user.put("firstName",sfname);                                                                                                 // put method of map will insert a mapping in map
                                user.put("lastName",slname);
                                user.put("email",smail);
                                user.put("phone",sphone);
                                user.put("password",spassword);

                                StorageReference fileUp = storageRef.child("profilePicture/"+userID+".jpg");                                                  // create a child reference
                                // TODO : Compress the image size otherwise app will close on large image size while uploading in firebase
                                UploadTask uploadTask = fileUp.putFile(fileloc);                                            // fileUp.putFile(fileloc) => will upload the file to firebase storage asynchronously

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                        user.put("profilePic",fileUp.getDownloadUrl().toString());                 //TODO: uploading the url of firebase storage to databse
                                    }
                                });

//                                user.put("profilePic",fileUp.getDownloadUrl().toString());

                                documentReference.set(user);                                                           // create or overwrite the document in firebase store
//                                OR
//                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                      Log.d(TAG,"data store in database for" + userID);
//                                    }
//                                });

                                Intent i = new Intent(RegisterActivity.this,UserDashboardActivity.class);
                                startActivity(i);

                            }else{
                                Toast.makeText(RegisterActivity.this,task.getException().getMessage()+"...try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(RegisterActivity.this,"Enter Valid Details",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean validate() {

        sfname = fname.getText().toString().trim();
        slname = lname.getText().toString().trim();
        smail = email.getText().toString().trim();
        spassword = password.getText().toString().trim();
        sphone = phone.getText().toString().trim();

        boolean valid = true;
        if (TextUtils.isEmpty(smail) || !smail.contains(".") || !smail.contains("@")) {
            email.setError("Enter a valid email");
            valid = false;
        }
        if (TextUtils.isEmpty(spassword) || spassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            valid = false;
        }
        if(TextUtils.isEmpty(sfname)){
            fname.setError("Enter a valid First Name");
            valid = false;
        }
        if(TextUtils.isEmpty(slname)){
            lname.setError("Enter a valid Last Name");
            valid = false;
        }
        if(TextUtils.isEmpty(sphone)){
            phone.setError("Enter a valid phone number");
            valid = false;
        }

//        // user has selected a profilePic or not
//        if(!fileloc.isAbsolute()){
//            Toast.makeText(RegisterActivity.this,"Please upload a photo",Toast.LENGTH_SHORT).show();
//            valid = false;
//        }

        return valid;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)                                                                            // calling and defining alert dialog
                    .setTitle("Camera Permission Needed")
                    .setMessage("This permission is need for capturing the profile photo")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterActivity.this,
                                    new String[] {Manifest.permission.CAMERA}, permissioncode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()                                                                                               // to create the dialog
                    .show();                                                                                                // to show the dialog
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, permissioncode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissioncode)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Smile ...", Toast.LENGTH_SHORT).show();
//                Camera();                                                                                                 // capturing the image and displaying directly to imageView in thumbnail thus very low quality of image view and no way to upload it in database
                dispatchTakePictureIntent();                                                                                // capturing the image and then saving it to local storage and displaying the image on imageView

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void Camera(){                                                                          // It will open the camera
//    Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    startActivityForResult(cam,123);                                                                // Launch an activity for which you would like a result when it finished.
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 123){
//            Bitmap img = (Bitmap) data.getExtras().get("data");                                     // bitmap image
//            profilePic.setImageBitmap(img);
//        }
//    }


    //  https://developer.android.com/training/camera/photobasics

    //  https://developer.android.com/training/camera/photobasics#TaskPath
    //  returns a unique file name for a new photo using a date-time stamp
    String currentPhotoPath;                                                                            // to store the absolute directory
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          // getting timestamp which will used to give unique file name to the photo
        String imageFileName = "JPEG_" + timeStamp + "_";                                               // file name for image
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);                          // getting storage directory where image file will be stored
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();                                                      // image.getAbsolutePath() return the absolute path of the image created
        return image;                                                                                    // this will return the image will be used in dispatchTakePictureIntent()
    }

    // With this method available to create a file for the photo, you can now create and invoke the Intent like this:
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {                             // to check if camera is present in the device or not
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.xms.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, 123);                             // Launch an activity for which you would like a result when it finished here the intent is to capture a image from camera
            }
        }
    }

    /* Now configuring the FileProvider. In your app's manifest, add a provider to your application. */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                profilePic.setImageURI(Uri.fromFile(f));

                fileloc = Uri.fromFile(f);
            }
        }
    }
}
