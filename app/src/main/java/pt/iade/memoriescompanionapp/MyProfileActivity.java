package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pt.iade.memoriescompanionapp.data.model.Consts;
import pt.iade.memoriescompanionapp.data.model.LoggedInUser;

public class MyProfileActivity extends AppCompatActivity {

    private Button petSelectButton2;
    private Button myProfileButton2;
    private Button petButton2;
    private TextView playerUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setupComponents();
    }

    public void setupComponents() {
        petSelectButton2 = (Button) findViewById(R.id.petSelectButton2);
        petSelectButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, PetSelectActivity.class);
                startActivity(intent);
                Log.d("Pet Select Button", "redirected to pet select screen");
            }
        });

        petButton2 = (Button) findViewById(R.id.petButton2);
        petButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
                startActivity(intent);
                Log.d("Pet Button", "redirected to pet screen");
            }
        });

        myProfileButton2 = (Button) findViewById(R.id.myProfileButton2);
        myProfileButton2.setClickable(false);

        playerUsername = (TextView) findViewById(R.id.usernameShow);
        playerUsername.setText(Consts.currentUser.getDisplayName());
    }
}