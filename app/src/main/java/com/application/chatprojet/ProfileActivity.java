 package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

 public class ProfileActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarprofile;
    private ImageButton backbuttonofprofile;
    private ImageView viewuserimage;
    private EditText viewusername;
    private TextView movetoupdateprofil;


    private FirebaseAuth firebaseAuth;
    //fetch name of user
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    //to fetch the user image
    private StorageReference storageReference;

    //access token which it help to face the userimage
    private String imageuriacesstoken;

    // private static final String TAG = "MyAppTag";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbarprofile=findViewById(R.id.toolbarprofile);
        backbuttonofprofile=findViewById(R.id.backbuttonprofile);
        viewuserimage=findViewById(R.id.viewuserimageview);
        viewusername=findViewById(R.id.viewusername);
        movetoupdateprofil=findViewById(R.id.movetoupdateprofil);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();



        backbuttonofprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });

        //the refrence of our storage
        storageReference=firebaseStorage.getReference();
        //the uri of image and by download the image we ll have the access token
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageuriacesstoken=uri.toString();
                //load image inside the image view
                Picasso.get().load(uri).into(viewuserimage);
            }
        });

        //face the name
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d(TAG, "onDataChange: 1111111111");

                UserProfile userProfile=snapshot.getValue(UserProfile.class);
                viewusername.setText(userProfile.getUsername());
                

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              //  Log.d(TAG, "onCancelled: error");
                Toast.makeText(getApplicationContext(),"Echec de la recuperation",Toast.LENGTH_SHORT).show();

            }
        });

        movetoupdateprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent=new Intent(ProfileActivity.this,UpdateProfilActivity.class);
                //we pass username in intent and we get image form the uri
                intent.putExtra("nameofuser",viewusername.getText().toString());
                startActivity(intent);
            }
        });

    }


 }