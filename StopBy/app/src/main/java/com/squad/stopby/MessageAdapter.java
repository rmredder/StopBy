package com.squad.stopby;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yuanjian on 4/10/2018.
 */

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private Context ctx;
    private List<Message> mList;

    private DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private String key;

    public MessageAdapter(Context ctx, List<Message> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.single_message_background_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        Message msg = mList.get(position);

        holder.chat_msg.setText(msg.getMessage());

        if(msg.getFrom().equals(currentUser.getUid())) {

            //display user's profile image
            rootDatabase.child("user profile").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String profileImgUri = dataSnapshot.child("image").getValue().toString();

                    if(profileImgUri.equals("default")) {

                        Picasso.with(ctx).load(R.drawable.default1).into(holder.chat_profileImg);

                    } else {

                        Picasso.with(ctx).load(profileImgUri).into(holder.chat_profileImg);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {

            String sender_id = msg.getFrom();

            rootDatabase.child("user profile").child(sender_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String profileImgUri = dataSnapshot.child("image").getValue().toString();

                    if(profileImgUri.equals("default")) {

                        Picasso.with(ctx).load(R.drawable.default1).into(holder.chat_profileImg);

                    } else {

                        Picasso.with(ctx).load(profileImgUri).into(holder.chat_profileImg);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView chat_profileImg;
        private TextView chat_msg;

        public MessageViewHolder(View itemView) {
            super(itemView);

            chat_profileImg = (CircleImageView) itemView.findViewById(R.id.chat_profileImg);
            chat_msg = (TextView) itemView.findViewById(R.id.chat_msg);
        }
    }
}
