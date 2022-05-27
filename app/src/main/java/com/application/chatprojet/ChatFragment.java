package com.application.chatprojet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ChatFragment extends Fragment {

   private ImageView imageviewofuser;
   private LinearLayoutManager linearLayoutManager;
   private RecyclerView recyclerView;

   //get objects
   private FirebaseFirestore firebaseFirestore;
   private FirebaseAuth firebaseAuth;

   //
   FirestoreRecyclerAdapter<FireBaseModel,NoteViewHolder>  chatAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.chatfragment,container,false);
        recyclerView=v.findViewById(R.id.recyclerview);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //fetch data from cloudstore
        //Users the path where the all users store
        //we fetch all user expect me
        Query query=firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());

        //we set query becouse we fetch data according query
        FirestoreRecyclerOptions<FireBaseModel> allusername= new FirestoreRecyclerOptions.Builder<FireBaseModel>().setQuery(query,FireBaseModel.class).build();

        chatAdapter=new FirestoreRecyclerAdapter<FireBaseModel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FireBaseModel model) {

                //set text which i get data from firebasemodel
                holder.nameofuser.setText(model.getName());

                //we store image in the uri so we should fetch the uri first
                String uri =model.getImage();
                Picasso.get().load(uri).into(imageviewofuser);

                //the condition if the user is online the color should change for status
                //but if he isnt online he keep the color for default
                if(model.getStatus().equals("Online")){
                    //we show status on holder
                    holder.statusofuser.setText(model.getStatus());
                    holder.statusofuser.setTextColor(Color.RED);


                }
                else {
                    holder.statusofuser.setText(model.getStatus());

                }

                //when you click on the itemview
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(),Conversation.class);
                        //we pass data inside intent
                        //because when we create code for conversation
                        //we should crate a room on database so uid will help me to create room
                        intent.putExtra("name",model.getName());
                        intent.putExtra("receiveruid",model.getUid());
                        intent.putExtra("imageuri",model.getImage());

                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //everytime it ll return the new object of this and it ll set the data
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatview,parent,false);
                return new NoteViewHolder(view);
            }
        };


        recyclerView.setHasFixedSize(true);
        //we create linearlayoutmanager because our recylable linear
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        return v;


    }
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView nameofuser;
        private TextView statusofuser;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            nameofuser=itemView.findViewById(R.id.nameofuser);
            statusofuser=itemView.findViewById(R.id.statusofuser);
            imageviewofuser=itemView.findViewById(R.id.imageviewofuser);
        }
    }

    //To show the users we should add the chat adapter listener
    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null){
            chatAdapter.stopListening();
        }
    }
}
