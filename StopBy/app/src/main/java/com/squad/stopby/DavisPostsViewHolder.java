package com.squad.stopby;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yuanjian on 4/10/2018.
 */

public class DavisPostsViewHolder extends RecyclerView.ViewHolder{

    private View view;

    private CircleImageView posterImg;
    private TextView posterName;
    private TextView posterMessage;

    public DavisPostsViewHolder(View itemView) {
        super(itemView);

        view = itemView;

    }

    public void setName(String name) {

        posterName = (TextView) view.findViewById(R.id.poster_name);
        posterName.setText(name);
    }

    public void displayMessage(String message) {

        posterMessage = (TextView) view.findViewById(R.id.poster_message);
        posterMessage.setText(message);

    }

    public void displayProfilePic(String imgUrl) {

        posterImg = (CircleImageView) view.findViewById(R.id.chat_profileImg);

        if(imgUrl.equals("default")) {

            Picasso.with(view.getContext()).load(R.drawable.default1).into(posterImg);

        } else {

            Picasso.with(view.getContext()).load(imgUrl).into(posterImg);

        }
    }

    public View getView() {
        return view;
    }
}
