package com.example.xms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.grpc.Context;

import static android.content.ContentValues.TAG;

public class UserDetailActivity extends Activity {

    private TextView fname,lname,phone;
    private ImageView profilePic;
    private Button camera,update,loginCredentials,back;
    private final int permissioncode = 1 ;
    private boolean clickstatus;

    private String ifname,ilname,iphone;
    private String ffname,flname,fphone;

    private File localFile;

    FirebaseUser user;
    DocumentReference docRef;
    FirebaseFirestore fdb;
    StorageReference storageRef,fileLocation;
    Uri fileloc;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        fname = findViewById(R.id.txtfname);
        lname = findViewById(R.id.txtlname);
        phone = findViewById(R.id.txtphone);
        profilePic = (ImageView) findViewById(R.id.userprofilePic);
        camera = findViewById(R.id.btnudcamera);
        update = findViewById(R.id.btnupdate);
        loginCredentials = findViewById(R.id.btnlogincredentials);
        back = findViewById(R.id.btnBack);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){

            fdb = FirebaseFirestore.getInstance();
            docRef = fdb.collection("users").document(user.getUid().trim());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    ilname = documentSnapshot.get("firstName").toString().trim();
                    ifname = documentSnapshot.get("lastName").toString().trim();
                    iphone = documentSnapshot.get("phone").toString().trim();

                    fname.setText(ilname);
                    lname.setText(ifname);
                    phone.setText(iphone);

                }
            });
        }else{
            Toast.makeText(UserDetailActivity.this,"Please login the user",Toast.LENGTH_SHORT).show();
        }

        storageRef = FirebaseStorage.getInstance().getReference();
        fileLocation = storageRef.child("profilePicture/"+user.getUid().trim()+".jpg");
//        Glide.with(this).load(fileLocation.toString().trim()).into(profilePic);                                                                 // TODO why glide method is not working ?

        // Alternate to glide by downloading it locally in tempFile

        localFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            localFile = File.createTempFile("images"+user.getUid().trim()+timeStamp, "jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileLocation.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                profilePic.setImageURI(Uri.fromFile(localFile));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {                  //Checking if camera permission has been provided or not
                    dispatchTakePictureIntent();
                } else {                                                                                                                                                  // if camera permission has not been provided then
                    requestCameraPermission();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ffname = fname.getText().toString().trim();
                flname = lname.getText().toString().trim();
                fphone = phone.getText().toString().trim();

                int count = 0 ;

                if( ifname != ffname ){

                    docRef.update("firstName",ffname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(UserDetailActivity.this,"First Name Updated",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserDetailActivity.this,"Error in Updating First Name",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    count++;
                }

                if( ilname != flname){

                    docRef.update("lastName",flname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(UserDetailActivity.this,"Last Name Updated",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserDetailActivity.this,"Error in Updating Last Name",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    count++;
                }

                if( iphone != fphone ){

                    docRef.update("phone",fphone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(UserDetailActivity.this,"Phone Number Updated",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserDetailActivity.this,"Error in Updating Phone Number",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    count++;
                }

                if( clickstatus == true ){
                    fileLocation.delete();
                    StorageReference fileUp = storageRef.child("profilePicture/"+user.getUid().trim()+".jpg");                                                  // create a child reference
                    // TODO : Compress the image size otherwise app will close on large image size while uploading in firebase
                    UploadTask uploadTask = fileUp.putFile(fileloc);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(UserDetailActivity.this,"Error in Uploading Profile Photo please Re-upload it",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        }
                    });

                    count++;
                }

                if(count !=0 ){
                    Toast.makeText(UserDetailActivity.this,"Profile Update Successful",Toast.LENGTH_SHORT).show();
                }

            }
        });

        loginCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDetailActivity.this,UpdateLoginCredentialsActivity.class);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDetailActivity.this,UserDashboardActivity.class);
                startActivity(i);
            }
        });


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
                            ActivityCompat.requestPermissions(UserDetailActivity.this,
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
                clickstatus = true;
            }
        }
    }

}
