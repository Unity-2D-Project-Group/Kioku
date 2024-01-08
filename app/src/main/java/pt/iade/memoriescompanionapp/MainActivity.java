package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import pt.iade.memoriescompanionapp.classes.APIMsg;
import pt.iade.memoriescompanionapp.classes.APIPet;
import pt.iade.memoriescompanionapp.classes.APIUser;
import pt.iade.memoriescompanionapp.classes.APIUserInfo;
import pt.iade.memoriescompanionapp.data.Result;
import pt.iade.memoriescompanionapp.data.model.Consts;
import pt.iade.memoriescompanionapp.utilities.WebRequest;

public class MainActivity extends AppCompatActivity {

    private Button petSelectButton;
    private Button myProfileButton;
    private Button petButton;
    private Button feed;
    private Button stepButton;
    private Button reduceStats;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;
    private TextView hygieneText;
    private TextView happinessText;
    private TextView fullnessText;
    private TextView fruitText;
    private TextView fruitTextLabel;
    private TextView currentRoom;
    private ImageView petImage;

    // 1 = Bathroom; 2 = Forest; 3 = Gym
    public static int currentLocation = 2;

    public static int hygiene = 100;
    public static int happiness = 100;
    public static int fullness = 100;
    public static int fruit = 0;
    public static int activePet;

    private SensorManager sensorManager;
    private Sensor accelSensor;
    private Sensor stepSensor;
    private static final int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float x;
    private float y;
    private float z;
    private float last_x;
    private float last_y;
    private float last_z;

    protected static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupComponents();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACTIVITY_RECOGNITION }, MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (currentLocation == 2 && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long curTime = System.currentTimeMillis();
                    // only allow one update every 100ms.
                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        x = Float.parseFloat(String.valueOf(event.values[0]));
                        y = Float.parseFloat(String.valueOf(event.values[1]));
                        z = Float.parseFloat(String.valueOf(event.values[2]));

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                        if (speed > SHAKE_THRESHOLD && currentLocation == 2) {
                            Log.d("sensor", "shake detected w/ speed: " + speed);
                            AddFruit();
                        }
                        last_x = x;
                        last_y = y;
                        last_z = z;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("ACCURACY_CHANGE", sensor.toString() + " - " + accuracy);
            }
        }, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (currentLocation == 3 && (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)) {
                    Log.d("sensor", "step detected");
                    stepStatsUpdate();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("ACCURACY_CHANGE", sensor.toString() + " - " + accuracy);
            }
        }, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (currentLocation == 2) {
            ImageView img = (ImageView) findViewById(R.id.background);
            img.setImageResource(R.drawable.forest);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentLocation == 1) {
            switch (event.getAction()) {
                case (MotionEvent.ACTION_MOVE):
                    Log.d("SWIPE", "Action was MOVE");
                    bathStatsUpdate();
                    if (Integer.valueOf((String)hygieneText.getText()) < 100) {

                        if (activePet == 1) {
                            petImage.setImageDrawable(getResources().getDrawable(R.drawable.pet1bath));
                        } else if (activePet == 2) {
                            petImage.setImageDrawable(getResources().getDrawable(R.drawable.pet2bath));
                        }
                    } else {
                        petImageReset();
                    }
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        } else {
            return false;
        }
    }

    public void setupComponents() {
        hygieneText = (TextView)findViewById(R.id.hygieneText);
        happinessText = (TextView)findViewById(R.id.happinessText);
        fullnessText = (TextView)findViewById(R.id.fullnessText);
        fruitText = (TextView)findViewById(R.id.fruitText);
        fruitTextLabel = (TextView)findViewById(R.id.fruit);
        currentRoom = (TextView)findViewById(R.id.currentRoom);
        petImage = (ImageView)findViewById(R.id.petImage);

        hygieneText.setText(String.valueOf(hygiene));
        happinessText.setText(String.valueOf(happiness));
        fullnessText.setText(String.valueOf(fullness));
        fruitText.setText(String.valueOf(fruit));

        petSelectButton = (Button) findViewById(R.id.petSelectButton);
        petSelectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PetSelectActivity.class);
                startActivity(intent);
                Log.d("Pet Select Button", "redirected to pet select screen");
            }
        });

        petButton = (Button) findViewById(R.id.petButton);
        petButton.setClickable(false);

        myProfileButton = (Button) findViewById(R.id.myProfileButton);
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intent);
                Log.d("My Profile Button", "redirected to my profile screen");
            }
        });

        feed = (Button) findViewById(R.id.feed);
        feed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Feed Button", "clicked the feed button");
                feedStatsUpdate();
            }
        });

        stepButton = (Button) findViewById(R.id.stepButton);
        stepButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.d("Step Button", "clicked the step button");
            stepStatsUpdate();
            }
        });

        reduceStats = (Button) findViewById(R.id.reduceStats);
        reduceStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Reduce Stats Button", "clicked the reduce stats button");
                reduceStats();
            }
        });

        leftArrowButton = (ImageButton) findViewById(R.id.leftArrow);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (currentLocation == 2) {
                    bathroomScreenSetup();
                }
                else if (currentLocation == 3) {
                    forestScreenSetup();
                }
                Log.d("LEFT ARROW", "pressed left arrow");
            }
        });

        rightArrowButton = (ImageButton) findViewById(R.id.rightArrow);
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (currentLocation == 2) {
                    gymScreenSetup();
                }
                else if (currentLocation == 1) {
                    forestScreenSetup();
                }
                Log.d("RIGHT ARROW", "pressed right arrow");
            }
        });

        stepButton.setVisibility(View.GONE);
        GetPetStats();
        GetUserInfo();
        UpdateStats();
        petImageReset();
    }

    private void petImageReset() {
        if (Consts.currentPet.pet_id == 1) {
            petImage.setImageDrawable(getResources().getDrawable(R.drawable.pet1));
        } else if (Consts.currentPet.pet_id == 2) {
            petImage.setImageDrawable(getResources().getDrawable(R.drawable.pet2));
        }
    }

    private void forestScreenSetup() {
        ImageView img = (ImageView) findViewById(R.id.background);
        img.setImageResource(R.drawable.forest);
        currentLocation = 2;
        currentRoom.setText("Forest");
        feed.setVisibility(View.VISIBLE);
        fruitText.setVisibility(View.VISIBLE);
        fruitTextLabel.setVisibility(View.VISIBLE);
        stepButton.setVisibility(View.GONE);
        rightArrowButton.setVisibility(View.VISIBLE);
        leftArrowButton.setVisibility(View.VISIBLE);
        petImageReset();
        Log.d("FOREST", "forest true");
    }

    private void bathroomScreenSetup() {
        ImageView img = (ImageView) findViewById(R.id.background);
        img.setImageResource(R.drawable.bathroom);
        currentLocation = 1;
        currentRoom.setText("Bathroom");
        feed.setVisibility(View.GONE);
        fruitText.setVisibility(View.GONE);
        fruitTextLabel.setVisibility(View.GONE);
        stepButton.setVisibility(View.GONE);
        leftArrowButton.setVisibility(View.GONE);
        Log.d("BATHROOM", "bathroom true");
    }

    private void gymScreenSetup() {
        ImageView img = (ImageView) findViewById(R.id.background);
        img.setImageResource(R.drawable.gym);
        currentLocation = 3;
        currentRoom.setText("Gym");
        feed.setVisibility(View.GONE);
        fruitText.setVisibility(View.GONE);
        fruitTextLabel.setVisibility(View.GONE);
        stepButton.setVisibility(View.VISIBLE);
        rightArrowButton.setVisibility(View.GONE);
        Log.d("GYM", "gym true");
    }

    private void AddFruit(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/users/fruits"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String result = webRequest.performPatchRequest(params);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();
            GetUserInfo();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void feedStatsUpdate(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/feed"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

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
            if (msg.msg != "Feed successfuly done!") {
                Toast.makeText(this,msg.msg, Toast.LENGTH_SHORT).show();
            }
            GetUserInfo();
            GetPetStats();
            UpdateStats();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
    private void reduceStats(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/reduceStats"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String result = webRequest.performPatchRequest(params);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();
            GetUserInfo();
            GetPetStats();
            UpdateStats();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void stepStatsUpdate() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/exercise"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

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
            if (msg.msg != "Exercise successfuly done!") {
                Toast.makeText(this,msg.msg, Toast.LENGTH_SHORT).show();
            }
            GetPetStats();
            UpdateStats();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void bathStatsUpdate() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/bath"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String result = webRequest.performPatchRequest(params);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();

            GetPetStats();
            UpdateStats();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public static void GetPetStats() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/pets/current"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String pet = webRequest.performGetRequest(params);
                Gson gson = new Gson();

                JsonObject jsonObject = JsonParser.parseString(pet).getAsJsonObject();

                // Define the Java class you want to convert the JSON data into
                Consts.currentPet = gson.fromJson(jsonObject, APIPet.class);
                return "Done";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    @SuppressLint("SetTextI18n")
    private void UpdateStats(){
        petImageReset();
        hygieneText.setText(Consts.currentPet.hygiene.toString());
        happinessText.setText(Consts.currentPet.happiness.toString());
        fullnessText.setText(Consts.currentPet.hungry.toString());
    }

    private void GetUserInfo() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        @SuppressLint("SetTextI18n") Future<String> future = executorService.submit(() -> {
            try {
                WebRequest webRequest = new WebRequest(
                        new URL(WebRequest.LOCALHOST + "/users/"));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", Consts.currentUser.getUserId());

                String user = webRequest.performGetRequest(params);
                Gson gson = new Gson();

                JsonObject jsonObject = JsonParser.parseString(user).getAsJsonObject();

                // Define the Java class you want to convert the JSON data into
                APIUserInfo userObject = gson.fromJson(jsonObject, APIUserInfo.class);
                fruitText.setText(userObject.fruits.toString());
                return "Done";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        });

        try {
            String result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}