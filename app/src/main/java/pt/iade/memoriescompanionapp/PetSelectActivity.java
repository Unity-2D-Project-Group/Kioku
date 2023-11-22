package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PetSelectActivity extends AppCompatActivity {

    private Button petSelectButton3;
    private Button myProfileButton3;
    private Button petButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setupComponents();
    }

    public void setupComponents() {
        petSelectButton3 = (Button) findViewById(R.id.petSelectButton3);
        petSelectButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PetSelectActivity.this, PetSelectActivity.class);
                startActivity(intent);
                Log.d("Pet Select Button", "redirected to pet select screen");
            }
        });

        petButton3 = (Button) findViewById(R.id.petButton3);
        petButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PetSelectActivity.this, MainActivity.class);
                startActivity(intent);
                Log.d("My Profile Button", "redirected to my profile screen");
            }
        });

        myProfileButton3 = (Button) findViewById(R.id.myProfileButton3);
        myProfileButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PetSelectActivity.this, MyProfileActivity.class);
                startActivity(intent);
                Log.d("My Profile Button", "redirected to my profile screen");
            }
        });
    }
}