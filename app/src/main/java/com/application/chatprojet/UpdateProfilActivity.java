package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UpdateProfilActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbarupdateprofile;
    private ImageButton backbuttonupdateprofile;
    private ImageView updateuserimage;
    private EditText updateusername;
    private android.widget.Button btnUpdateProfil;
    private ProgressBar progressBarUpdate;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    //to fetch the user image
    private StorageReference storageReference;

    //access token which it help to face the userimage
    private String imageuriacesstoken;

    //when can user select the image
    private Uri imagepath;
    private static int PICK_IMAGE=123;
    private String newname;
    //we pass in intent the name
    private Intent intent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profil);

        toolbarupdateprofile=findViewById(R.id.toolbarupdateprofile);
        backbuttonupdateprofile=findViewById(R.id.backbuttonupdateprofile);
        updateuserimage=findViewById(R.id.updateuserimageview);
        updateusername=findViewById(R.id.updateusername);
        btnUpdateProfil=findViewById(R.id.btnUpdateProfil);
        progressBarUpdate=findViewById(R.id.ProgressBarUpdate);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //get data from intent
        intent=getIntent();


        backbuttonupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateProfilActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });

        //we get data from the intent
        updateusername.setText(intent.getStringExtra("nameofuser"));

        //we get object datareference to change name
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        btnUpdateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               newname=updateusername.getText().toString();
               if(newname.isEmpty()){
                   Toast.makeText(getApplicationContext(), "le nom d'utilisateur est vide", Toast.LENGTH_SHORT).show();
               }

               //user want to update the name and the image
               else if(imagepath!=null){
                   progressBarUpdate.setVisibility(View.VISIBLE);
                   //it update the name
                   UserProfile userProfile=new UserProfile(newname,firebaseAuth.getUid());
                   databaseReference.setValue(userProfile);

                   //it update the image
                   updateimagetostorage();
                   Toast.makeText(getApplicationContext(),"Il y a eu une modification",Toast.LENGTH_SHORT).show();

                   progressBarUpdate.setVisibility(View.INVISIBLE);
                   Intent intent=new Intent(UpdateProfilActivity.this,ChatActivity.class);
                   startActivity(intent);
                   finish();

               }

               else{
                   //In this case the image it is null
                   //So the user want to update only the name not the image

                   progressBarUpdate.setVisibility(View.VISIBLE);
                   //it update the name
                   UserProfile userProfile=new UserProfile(newname,firebaseAuth.getUid());
                   databaseReference.setValue(userProfile);
                   //because in my chatactivity i fect data from cloud firestore
                   //so if the user update the name in real-time database
                   //then i have to update the name also in cloud firestore

                   updatenameoncloudfirestore();
                   Toast.makeText(getApplicationContext(),"Il y a eu une modification",Toast.LENGTH_SHORT).show();

                   progressBarUpdate.setVisibility(View.INVISIBLE);
                   Intent intent=new Intent(UpdateProfilActivity.this,ChatActivity.class);
                   startActivity(intent);
                   finish();

               }
            }
        });

        //user get new image
        updateuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
        //before update any data we should show the previous userimage
        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageuriacesstoken=uri.toString();
                Picasso.get().load(uri).into(updateuserimage);
            }
        });






    }
    private void updatenameoncloudfirestore(){

        //Users the path where the all users store and we fetch them by the id document(firebaseAuth.getUid())
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

        //we use Map to store users ids and data
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("name",newname);
        //because i will fetch the all user
        //also i want to fetch that user's image
        userdata.put("image",imageuriacesstoken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(),"ton profile a ete modifier avec succes",Toast.LENGTH_SHORT).show();


            }
        });


    }

    //the function for update image
    private void updateimagetostorage(){
        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");


        Bitmap bitmap=null;
        try{
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        //now we compress the image
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
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

                        imageuriacesstoken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI obtenir avec succes",Toast.LENGTH_SHORT).show();
                        //we call this function updatenameoncloudfirestore() inside  updateimagetostorage()
                        //for give the chosse to the user if he want to updaate just the name or the image or update the both
                        //we don t update the name in cloud firestore because
                        // when we update image we should have the acces token and
                        // when we get the acces token
                        //  we should also update the access of the image on the cloud firestore
                        updatenameoncloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Echec de l obtention de l URI",Toast.LENGTH_SHORT).show();

                    }
                });

                Toast.makeText(getApplicationContext(),"L image est modifie avec succes",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),"L image n a pas ete modifier avec succes",Toast.LENGTH_SHORT).show();

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
            updateuserimage.setImageURI(imagepath);//the image select by the user

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}