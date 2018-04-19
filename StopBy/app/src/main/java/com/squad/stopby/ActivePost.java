package com.squad.stopby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivePost extends AppCompatActivity {
    private TextView currentPostTextView;
    private Button active_deactivateBtn;
    private Button active_mapBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_post);

        currentPostTextView = (TextView) findViewById(R.id.active_textView);
        active_deactivateBtn = (Button) findViewById(R.id.active_deactivateBtn);
        active_mapBtn = (Button) findViewById(R.id.active_mapBtn);

        String message = Post.getPostMessage();
        if (message == ""){message = "There is no active post yet!"; }
        currentPostTextView.append("\n"+message);

        active_deactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: deactivate post method here
            }
        });

        active_mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivePost.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

}
