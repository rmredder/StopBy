package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_button);

        Button mbutton=(Button) findViewById(R.id.editButton);
        mbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent =new Intent(v.getContext(),EditProfileActivity.class );
                startActivity(myIntent);
            }
        });
    }
}
