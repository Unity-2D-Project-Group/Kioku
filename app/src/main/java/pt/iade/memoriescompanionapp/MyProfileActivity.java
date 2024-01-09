package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import pt.iade.memoriescompanionapp.data.model.Consts;

public class MyProfileActivity extends AppCompatActivity {

    private Button petSelectButton2;
    private Button myProfileButton2;
    private Button petButton2;
    private TextView playerUsername;
    private Switch debugSwitch;
    public static boolean switchState;

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

        debugSwitch = (Switch) findViewById(R.id.debugSwitch);
        debugSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debugSwitch.isChecked()) {
                    switchState = true;
                } else {
                    switchState = false;
                }
            }
        });

        if (switchState) {
            debugSwitch.setChecked(true);
        }
    }
}