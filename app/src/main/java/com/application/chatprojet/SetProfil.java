package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfil extends AppCompatActivity {
    private CardView getuserimage;
    private ImageView getuserimageview;

    private EditText getusername;
    private android.widget.Button btnsaveprofil;
    private ProgressBar progressBarProfil;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    //status code to indicate that image was selected
    private static int PICK_IMAGE=123;

    //when can user select the image
    private Uri imagepath;
    // the uri of image
    private String imageUri;
    private String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profil);

        getuserimage=findViewById(R.id.userimage);
        getuserimageview=findViewById(R.id.userimageview);
        getusername=findViewById(R.id.username);
        btnsaveprofil=findViewById(R.id.btnSaveProfil);
        progressBarProfil=findViewById(R.id.ProgressBarProfile);


        firebaseAuth=FirebaseAuth.getInstance();
        //this storagereference it is help me to save image on database
        firebaseStorage=FirebaseStorage.getInstance();
        //how we store image by using storagereference that
        // it is link to firebaseStorage
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();




        //get image from from galery
        getuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                // startActivityForResult is used after the user select the image
                // you should again open this select activity that
                //make easy to return to set profile activity
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        btnsaveprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=getusername.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ton nom d utilisateur est vide",Toast.LENGTH_SHORT).show();
                    progressBarProfil.setVisibility(View.INVISIBLE);
                }

                //send data to firebase
                else{


                    progressBarProfil.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    //after send data i make progressbar invisible
                    progressBarProfil.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(SetProfil.this,ChatActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });

    }

    //this function it is used to show that image is selected by user on imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //PICK_IMAGE it is mean picking image is fine
        // RESULT_OK it is mean the user is selected the image
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imagepath=data.getData();//give the uri inside this imagepath
            getuserimageview.setImageURI(imagepath);//the image select by the user

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //this function it is used to send data to firebase
    private void sendDataForNewUser(){
        sendDataToRealTimeDataBase();

    }
    //this function is used to  send data on real time
    //Than we create a class UserProfile
    private void sendDataToRealTimeDataBase(){
        name=getusername.getText().toString().trim();

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        //the object of database reference
        //it will create the reference by the name of id
        //store data by using the id
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile userProfile=new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(),"Profil utilisateur ajoute avec succes",Toast.LENGTH_SHORT).show();

        //send image on database
        sendImagetoStorage();
    }
    //this function is used to send data on storage for store image
    //I didn't send image in the real time because
    // if user create a new profile on the application
    //he want to see his profile he want to see only his name and the image
    //so we store the mane in real-time database and the image in storage
    //also on chatactivty I will show the image and name of user
    private void sendImagetoStorage(){
        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");


        Bitmap bitmap=null;
        try{
            //show that image contain something or not
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        //now we compress the image
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
       //25 is the quality of paramter
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);

        byte[] data=byteArrayOutputStream.toByteArray();

        //putting image on database storage
        //we pass variables data because it contain the file compress images
        UploadTask uploadTask=imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              //take the uri of image to store on overcloud store
              //which use to help face the useriamge
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imageUri=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI obtenir avec succes",Toast.LENGTH_SHORT).show();
                        sendDataTocloudFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Echec de l obtention de l URI",Toast.LENGTH_SHORT).show();

                    }
                });

                Toast.makeText(getApplicationContext(),"L image est telechargee avec succes",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),"L image n a pas ete telechargee avec succes",Toast.LENGTH_SHORT).show();

            }
        });

    }

    //send data on cloud store
    private  void sendDataTocloudFirestore(){
        //Users the path where the all users store and we fetch them by the id document(firebaseAuth.getUid())
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        //we use Map to store users ids and data
        Map<String,Object> userdata=new HashMap<>();
        //the key and value format  that i use to store  data
        userdata.put("name",name);
        //because i will fetch the all user
        //also i want to fetch that user's image
        userdata.put("image",imageUri);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(),"Les donnees sur Cloud FireStore envoient avec succes",Toast.LENGTH_SHORT).show();


            }
        });

    }
}