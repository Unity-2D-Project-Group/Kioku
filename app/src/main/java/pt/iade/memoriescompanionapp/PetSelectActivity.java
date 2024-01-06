package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class PetSelectActivity extends AppCompatActivity {

    public static int currentPet = 1;
    private Button petSelectButton3;
    private Button myProfileButton3;
    private Button petButton3;
    private ImageButton selectPetButton1;
    private ImageButton selectPetButton2;

    public static int hygieneLevel;
    public static int happinessLevel;
    public static int fullnessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_select);

        setupComponents();
    }

    public void setupComponents() {
        petSelectButton3 = (Button) findViewById(R.id.petSelectButton3);
        petSelectButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Pet Select Button", "redirected to pet select screen");
            }
        });

        petButton3 = (Button) findViewById(R.id.petButton3);
        petButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PetSelectActivity.this, MainActivity.class);
                startActivity(intent);
                Log.d("Pet Button", "redirected to pet screen");
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

        hygieneLevel = MainActivity.hygiene;
        happinessLevel = MainActivity.happiness;
        fullnessLevel = MainActivity.fullness;

        selectPetButton1 = (ImageButton) findViewById(R.id.selectPetButton1);
        selectPetButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPet == 1) {
                    Log.d("Pet Select Button 1", "pet already selected");
                } else if (currentPet == 2) {
                    currentPet = 1;
                    MainActivity.hygiene = 100;
                    MainActivity.happiness = 100;
                    MainActivity.fullness = 100;
                    Log.d("Pet Select Button 1", "pet selected");
                }
            }
        });

        selectPetButton2 = (ImageButton) findViewById(R.id.selectPetButton2);
        selectPetButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPet == 2) {
                    Log.d("Pet Select Button 2", "pet already selected");
                } else if (currentPet == 1) {
                    currentPet = 2;
                    MainActivity.hygiene = 100;
                    MainActivity.happiness = 100;
                    MainActivity.fullness = 100;
                    Log.d("Pet Select Button 2", "pet selected");
                }
            }
        });
    }
}