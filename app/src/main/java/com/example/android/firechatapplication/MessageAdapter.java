package com.example.android.firechatapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List <Message> messages;
    Context context;



    //I need a constructor where I will get the messages
    //I will need to have the messages passed to messages

    public MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView username;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageTextView);
            username = itemView.findViewById(R.id.usernameTextView);
            image = itemView.findViewById(R.id.photoImageView);

        }
    }
    @NonNull
    @Override
    //This will inflate the layout
    //Return the inflated view
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.message_row, null, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.message.setText(message.getMessage());
        holder.username.setText(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
