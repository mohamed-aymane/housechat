package com.application.chatprojet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
//we create this class to retrieve message inside the recyclerview
public class MessagesAdapter extends RecyclerView.Adapter {

    private Context context;
    //it hold all the message
    private ArrayList<Messages> messagesArrayList;
    //to know if the user send the message or not
    private int ITEM_SEND=1;
    private int ITEM_RECIEVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // we attach the viewholder
        if(viewType==ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sendmessage,parent,false);
            return  new SenderViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.recievemessage,parent,false);
            return  new RecieverViewHolder(view);

        }
    }

    //set Data
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //we take position of messages
        Messages messages=messagesArrayList.get(position);

        if(holder.getClass()==SenderViewHolder.class) {
            //we create the object of SenderViewHolder
            SenderViewHolder viewHolder = (SenderViewHolder)holder;

            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
        }
        else {
            //we create the object of RecieverViewHolder
            RecieverViewHolder viewHolder = (RecieverViewHolder)holder;

            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());

        }
    }

    //we add this function to know who send message and who receive
    @Override
    public int getItemViewType(int position) {
        //we take position of messages
        Messages messages=messagesArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SEND;
        }
        else {
            return  ITEM_RECIEVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }



    class  SenderViewHolder extends RecyclerView.ViewHolder{
        TextView textViewmessage;
        TextView timeofmessage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
        }
    }

    class  RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView textViewmessage;
        TextView timeofmessage;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
        }
    }
}
