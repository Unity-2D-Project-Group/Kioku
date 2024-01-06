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
                            fruit = fruit + 1;
                            fruitText.setText(String.valueOf(fruit));
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
                    if (happiness < 100 && fullness > 30 && hygiene > 30) {
                        happiness = happiness + 5;
                    }
                    happinessText.setText(String.valueOf(happiness));

                    if (fullness > 0) {
                        fullness = fullness - 2;
                    }
                    fullnessText.setText(String.valueOf(fullness));

                    if (hygiene > 0) {
                        hygiene = hygiene - 2;
                    }
                    hygieneText.setText(String.valueOf(hygiene));
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
                    if (hygiene < 100) {
                        hygiene = hygiene + 1;
                    }
                    hygieneText.setText(String.valueOf(hygiene));
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

        activePet = PetSelectActivity.currentPet;
        if (activePet == 1) {
            petImage.setImageDrawable(getResources().getDrawable(R.drawable.bluekirby));
        } else if (activePet == 2) {
            petImage.setImageDrawable(getResources().getDrawable(R.drawable.kirby));
        }

        petSelectButton = (Button) findViewById(R.id.petSelectButton);
        petSelectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PetSelectActivity.class);
                startActivity(intent);
                Log.d("Pet Select Button", "redirected to pet select screen");
            }
        });

        petButton = (Button) findViewById(R.id.petButton);
        petButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Pet Button", "redirected to pet screen");
            }
        });

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
                if (fullness < 100 && fruit > 0) {
                    fullness = fullness + 5;
                    fullnessText.setText(String.valueOf(fullness));
                    fruit = fruit - 1;
                    fruitText.setText(String.valueOf(fruit));
                    Log.d("Feed Button", "pet fed");
                } else if (fullness >= 100 && fruit > 0) {
                    Toast.makeText(getApplicationContext(), "Pet Already Fed", Toast.LENGTH_LONG).show();
                    Log.d("Feed Button", "pet already fed");
                } else if (fruit == 0) {
                    Toast.makeText(getApplicationContext(), "No fruits", Toast.LENGTH_LONG).show();
                    Log.d("Feed Button", "no fruit");
                }
            }
        });

        stepButton = (Button) findViewById(R.id.stepButton);
        stepButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Log.d("Step Button", "clicked the step button");
            if (happiness < 100 && fullness > 30 && hygiene > 30) {
                happiness = happiness + 5;
            }
            happinessText.setText(String.valueOf(happiness));

            if (fullness > 0) {
                fullness = fullness - 2;
            }
            fullnessText.setText(String.valueOf(fullness));

            if (hygiene > 0) {
                hygiene = hygiene - 2;
            }
            hygieneText.setText(String.valueOf(hygiene));
            }
        });

        reduceStats = (Button) findViewById(R.id.reduceStats);
        reduceStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Reduce Stats Button", "clicked the reduce stats button");
                if (hygiene > 50 || happiness > 50 || fullness > 50) {
                    hygiene = 50;
                    hygieneText.setText(String.valueOf(hygiene));
                    happiness = 50;
                    happinessText.setText(String.valueOf(happiness));
                    fullness = 50;
                    fullnessText.setText(String.valueOf(fullness));
                    Log.d("Reduce Stats Button", "set stats to 50");
                } else {
                    hygiene = 0;
                    hygieneText.setText(String.valueOf(hygiene));
                    happiness = 0;
                    happinessText.setText(String.valueOf(happiness));
                    fullness = 0;
                    fullnessText.setText(String.valueOf(fullness));
                    Log.d("Reduce Stats Button", "set stats to 0");
                }
            }
        });

        leftArrowButton = (ImageButton) findViewById(R.id.leftArrow);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (currentLocation == 2) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.bathroom);
                    currentLocation = 1;
                    currentRoom.setText("Bathroom");
                    feed.setVisibility(View.GONE);
                    fruitText.setVisibility(View.GONE);
                    fruitTextLabel.setVisibility(View.GONE);
                    leftArrowButton.setVisibility(View.GONE);
                    Log.d("BATHROOM", "bathroom true");
                }
                else if (currentLocation == 3) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    currentLocation = 2;
                    currentRoom.setText("Forest");
                    feed.setVisibility(View.VISIBLE);
                    fruitText.setVisibility(View.VISIBLE);
                    fruitTextLabel.setVisibility(View.VISIBLE);
                    stepButton.setVisibility(View.GONE);
                    rightArrowButton.setVisibility((View.VISIBLE));
                    Log.d("FOREST", "forest true");
                }
                Log.d("LEFT ARROW", "pressed left arrow");
            }
        });

        rightArrowButton = (ImageButton) findViewById(R.id.rightArrow);
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (currentLocation == 2) {
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
                else if (currentLocation == 1) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    currentLocation = 2;
                    currentRoom.setText("Forest");
                    feed.setVisibility(View.VISIBLE);
                    fruitText.setVisibility(View.VISIBLE);
                    fruitTextLabel.setVisibility(View.VISIBLE);
                    leftArrowButton.setVisibility(View.VISIBLE);
                    Log.d("FOREST", "forest true");
                }
                Log.d("RIGHT ARROW", "pressed right arrow");
            }
        });

        stepButton.setVisibility(View.GONE);
    }
}