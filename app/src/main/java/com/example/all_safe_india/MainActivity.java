package com.example.all_safe_india;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 20;
    Button buttonChoose, buttonUpload, signout;
    ImageView imageView;
    EditText edittext;
    private Uri mImageuri;
    private StorageReference mStoragereference;
    private DatabaseReference mDatabasereference;
    ProgressBar mprogressbar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        buttonChoose = findViewById(R.id.buttonChoose);
        buttonUpload = findViewById(R.id.buttonUplaod);
        edittext = findViewById(R.id.Image_file_name);
        signout = findViewById(R.id.sign_out);
        imageView = findViewById(R.id.imageview);
        mprogressbar = findViewById(R.id.progress_bar);

        mStoragereference = FirebaseStorage.getInstance().getReference("Images");
        mDatabasereference = FirebaseDatabase.getInstance().getReference("Images");


        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFile();
                mprogressbar.setVisibility(View.VISIBLE);




            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Intent intent = new Intent(MainActivity.this , Login.class);
                startActivity(intent);
                finish();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select an Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST || resultCode == RESULT_OK || data != null || data.getData() != null) {

            mImageuri = data.getData();

            Picasso.get().load(mImageuri).into(imageView);
        }
    }

    private String getFileExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void UploadFile()
    {
       if(mImageuri != null)
       {
          StorageReference filereference = mStoragereference.child(System.currentTimeMillis()
          +" "+getFileExt(mImageuri));

          filereference.putFile(mImageuri)
                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Toast.makeText(MainActivity.this, "Upload Sucessfull", Toast.LENGTH_SHORT).show();

                  String uploadId = mDatabasereference.push().getKey();
                  Upload upload = new Upload(edittext.getText().toString().trim() ,
                          taskSnapshot.getUploadSessionUri().toString());
                  mDatabasereference.child(uploadId).setValue(upload);
              }
          })
           .addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {

                   Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
               }
           });

       }
       else
       {
           Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
       }
    }


}



















