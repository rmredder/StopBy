package com.squad.stopby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Post extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton hangoutButton;
    private RadioButton studyButton;
    private EditText timeField;
    private EditText placeField;
    private Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        hangoutButton = (RadioButton) findViewById(R.id.hangoutButton);
        studyButton = (RadioButton) findViewById(R.id.studyButton);
        timeField = (EditText) findViewById(R.id.timeField);
        placeField = (EditText) findViewById(R.id.placeField);
        postButton = (Button) findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //transfer data from Post to MapsActivity
                String event = getValueOfClickedButton();
                String time = timeField.getText().toString();
                String place = placeField.getText().toString();
                post2Map(event, time, place);
            }
        });
    }

    public void post2Map(String event, String time, String place) {
        Intent intent= new Intent(this, MapsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("event", event);
        extras.putString("time", time);
        extras.putString("place", place);
        intent.putExtras(extras);
        startActivity(intent);
    }

    //To get the value of the clicked radio button
    public String getValueOfClickedButton() {
        int clickedButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton clickedButton = (RadioButton) findViewById(clickedButtonId);
        return clickedButton.getText().toString();
    }
}
