package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDetailsActivity extends AppCompatActivity {

    //0-easy, 1-medium, 2-hard
    int gameDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Button btnStartGame = (Button) findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean dataIsValidAndSaved = validateAndSaveInputData();

                if(dataIsValidAndSaved){
                    Intent userDetailsIntent = new Intent(v.getContext(), GameplayActivity.class);
                    userDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    userDetailsIntent.putExtra("level_difficulty", gameDifficulty);
                    startActivity(userDetailsIntent);
                }
            }
        });
    }

    private boolean validateAndSaveInputData() {

        boolean userDetailsIsEmpty = false;

        EditText txtUserId = (EditText) findViewById(R.id.txtUserId);
        RadioGroup rgGameDifficulty = (RadioGroup) findViewById(R.id.rdgGameDifficulty);
        EditText txtNotes = (EditText) findViewById(R.id.txtNotes);


        if(txtUserId.getText().length() == 0 || rgGameDifficulty.getCheckedRadioButtonId() == -1)
        {
            TextView txtError = (TextView) findViewById(R.id.txtMissingUserData) ;
            txtError.setVisibility(View.VISIBLE);
            userDetailsIsEmpty = true;
        }

        if(!userDetailsIsEmpty){

            //Get user ID value
            String userId = txtUserId.getText().toString();

            //Get radio button value
            int radioButtonID = rgGameDifficulty.getCheckedRadioButtonId();
            View radioButton = rgGameDifficulty.findViewById(radioButtonID);
            int idx = rgGameDifficulty.indexOfChild(radioButton);
            gameDifficulty=idx;
            RadioButton r = (RadioButton)  rgGameDifficulty.getChildAt(idx);
            String gameDifficultyValue = r.getText().toString();

            //Get notes value
            String notes = txtNotes.getText().toString();

            //Get timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());


            UserDetailsHolders userDetailsHolders = UserDetailsHolders.getInstance();
            userDetailsHolders.setUserDetails(userId, gameDifficultyValue, notes,timeStamp);

            return true;
        }

        return false;
    }
}
