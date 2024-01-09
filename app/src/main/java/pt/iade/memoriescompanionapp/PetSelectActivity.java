package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.iade.memoriescompanionapp.classes.APIAllPets;
import pt.iade.memoriescompanionapp.classes.APIMsg;
import pt.iade.memoriescompanionapp.classes.APIPet;
import pt.iade.memoriescompanionapp.data.model.Consts;
import pt.iade.memoriescompanionapp.utilities.WebRequest;

public class PetSelectActivity extends AppCompatActivity {

    private Button petSelectButton3;
    private Button myProfileButton3;
    private Button petButton3;
    private ImageButton selectPetButton1;
    private ImageButton selectPetButton2;
    private ImageButton selectPetButton3;
    private ImageButton selectPetButton4;
    private ImageButton selectPetButton5;
    private ImageButton selectPetButton6;

    public static int hygieneLevel;
    public static int happinessLevel;
    public static int fullnessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_select);

        setupComponents();
        petButtonReset();
    }

    public void setupComponents() {
        GetAllPets();

        petSelectButton3 = (Button) findViewById(R.id.petSelectButton3);
        petSelectButton3.setClickable(false);

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
                ChangeCurrentPet(1);
            }
        });
        selectPetButton1.setClickable(false);

        selectPetButton2 = (ImageButton) findViewById(R.id.selectPetButton2);
        selectPetButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeCurrentPet(2);
            }
        });
        selectPetButton2.setClickable(false);

        selectPetButton3 = (ImageButton) findViewById(R.id.selectPetButton3);
        selectPetButton3.setClickable(false);
        selectPetButton4 = (ImageButton) findViewById(R.id.selectPetButton4);
        selectPetButton4.setClickable(false);
        selectPetButton5 = (ImageButton) findViewById(R.id.selectPetButton5);
        selectPetButton5.setClickable(false);
        selectPetButton6 = (ImageButton) findViewById(R.id.selectPetButton6);
        selectPetButton6.setClickable(false);

        for (APIPet pet: Consts.allPets.result) {
            if (pet.pet_id == 1) {
                selectPetButton1.setImageResource(R.drawable.pet1);
                selectPetButton1.setBackgroundColor(Color.parseColor("#7684FF"));
                selectPetButton1.setClickable(true);
            } else if (pet.pet_id == 2) {
                selectPetButton2.setImageResource(R.drawable.pet2);
                selectPetButton2.setBackgroundColor(Color.parseColor("#7684FF"));
                selectPetButton2.setClickable(true);
            }
        }
    }

    private void petButtonReset() {
        if (Consts.currentPet.pet_id == 1) {
            selectPetButton1.setClickable(false);
            selectPetButton1.setBackgroundColor(Color.argb(255, 118, 132, 255));
            selectPetButton2.setClickable(true);
            selectPetButton2.setBackgroundColor(Color.argb(200, 118, 132, 255));
        } else if (Consts.currentPet.pet_id == 2) {
            selectPetButton1.setClickable(true);
            selectPetButton1.setBackgroundColor(Color.argb(200, 118, 132, 255));
            selectPetButton2.setClickable(false);
            selectPetButton2.setBackgroundColor(Color.argb(255, 118, 132, 255));
        }
    }

    private void GetAllPets(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String result = webRequest.performGetRequest(params);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();

            Gson gson = new Gson();
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            Consts.allPets = gson.fromJson(jsonObject, APIAllPets.class);
            Log.d("Result", Consts.allPets.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void ChangeCurrentPet(Integer id){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/changeCurrent/"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());
                params.put("new_pet_id", id.toString());

                String result = webRequest.performPatchRequest(params);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();
            Gson gson = new Gson();
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            APIMsg msg = gson.fromJson(jsonObject, APIMsg.class);
            Toast.makeText(this,msg.msg, Toast.LENGTH_SHORT).show();
            MainActivity.GetPetStats();
            petButtonReset();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}