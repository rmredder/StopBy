package com.squad.stopby;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yuanjian on 4/13/2018.
 */

public class ChatroomViewHolder extends RecyclerView.ViewHolder{

    private View view;

    private CircleImageView chatroom_profileImg;
    private TextView chatroom_name;

    public ChatroomViewHolder(View itemView) {
        super(itemView);

        view = itemView;
    }

    public void displayProfilePic(String imgUrl) {

        chatroom_profileImg = (CircleImageView) view.findViewById(R.id.chatroom_profileImg);

        if(imgUrl.equals("default")) {

            Picasso.with(view.getContext()).load(R.drawable.default1).into(chatroom_profileImg);

        } else {

            Picasso.with(view.getContext()).load(imgUrl).into(chatroom_profileImg);

        }
    }

    public void setName(String name) {

        chatroom_name = (TextView) view.findViewById(R.id.chatroom_name);
        chatroom_name.setText(name);

    }

    public View getView() {
        return view;
    }
}
