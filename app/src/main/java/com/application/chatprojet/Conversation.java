package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Conversation extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarofconversation;
    private ImageButton backbuttonconversation;
    private CardView cardviewofreceiveuser;
    private ImageView recieveuserimageview;
    private TextView nameofrecieveuser;
    private RecyclerView recyclerviewofconversation;
    private EditText getmessage;
    private ImageButton imagesendmessage;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String currenttime;
    //for getting the current time we add calender
    private Calendar calendar;
    //we add simpledataformat because we need a time in exact format
    private SimpleDateFormat simpleDateFormat;

    private MessagesAdapter messagesAdapter;
    private ArrayList<Messages> messagesArrayList;


    private Intent intent;
    private String enteredmessage;
    private String recievername;
    private String sendername;
    private String recieveduid;
    private String senderuid;
    private String senderroom;
    private String recieverroom;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        toolbarofconversation=findViewById(R.id.toolbarofconversation);
        backbuttonconversation=findViewById(R.id.backbuttonconversation);
        cardviewofreceiveuser=findViewById(R.id.cardviewofreceiveuser);
        recieveuserimageview=findViewById(R.id.recieveuserimageview);
        nameofrecieveuser=findViewById(R.id.nameofrecieveuser);
        getmessage=findViewById(R.id.messagesent);
        imagesendmessage=findViewById(R.id.imagesendmessage);


        messagesArrayList=new ArrayList<>();
        recyclerviewofconversation=findViewById(R.id.recyclerviewofconversation);



        LinearLayoutManager linearLayoutManage=new LinearLayoutManager(this);
        linearLayoutManage.setStackFromEnd(true);
        recyclerviewofconversation.setLayoutManager(linearLayoutManage);

        messagesAdapter=new MessagesAdapter(Conversation.this,messagesArrayList);
        recyclerviewofconversation.setAdapter(messagesAdapter);


        //get Data from intent
        intent=getIntent();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        // a is used to change  the format inside hours and minutes and it is for 24 hour format
        simpleDateFormat=new SimpleDateFormat("hh:mm a");

        senderuid=firebaseAuth.getUid();

        recieveduid=getIntent().getStringExtra("receiveruid");//intent.putExtra("receiveruid",model.getUid());
        recievername=getIntent().getStringExtra("name");    //intent.putExtra("name",model.getName());

        //to show messages between  us
        //we create two room using senduid +recieveruid
        //and the second room using recieveduid+senderuid
        //it help to face the message and  help to assign layout that
        //let me show the sender message in the right and  the reciever in the left side
        //also it help to display message one to one chat


        //create the sender room
        senderroom=senderuid+recieveduid;
        //create the reciever room
        recieverroom=recieveduid+senderuid;

        //we pass the path where we store message
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");

        messagesAdapter=new MessagesAdapter(Conversation.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                //we put : and not = because it is for each loop
                //we take message one by one
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    //we get value of messages
                    Messages messages=snapshot1.getValue(Messages.class);
                    //messagesArrayList hold all the message
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backbuttonconversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Conversation.this,ChatActivity.class);
                startActivity(intent);
            }
        });

        //set the name and image on the reciever user
        nameofrecieveuser.setText(recievername);
        //we pass image acces token inside imageuri
        String uri=intent.getStringExtra("imageuri");//intent.putExtra("imageuri",model.getImage());

        if(uri.isEmpty()) {
            Toast.makeText(getApplicationContext(), "j'ai pas recu", Toast.LENGTH_SHORT).show();
        }
        else {
            //load image in imageview
            Picasso.get().load(uri).into(recieveuserimageview);
        }


        //click on button send mesage
        imagesendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check th user if he enter the message or not
                enteredmessage=getmessage.getText().toString();
                if (enteredmessage.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Entrez votre message", Toast.LENGTH_SHORT).show();
                }

                else {
                    //for getting current time
                    //date for timestamp
                    Date date=new Date();
                    currenttime=simpleDateFormat.format(calendar.getTime());
                    //To work with real time database i create the class Messages
                    //we pass four paramters the message ,currentuid=who send the message ,timestamp,time for message
                    Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);

                    //we store this four parameters in our database
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")//create the place where we store the message
                            .child(senderroom)//create the room senderroom contain senderuid +recieveruid
                            .child("messages")
                            .push()//post the values of  the messages
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()//if this happen with successfuly then  we send  the message on the reciever room
                                    .child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });
                    getmessage.setText(null);

                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null){
            messagesAdapter.notifyDataSetChanged();
        }
    }
}