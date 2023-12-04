package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
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
    private Button reduceStats;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;

    private TextView hygieneText;
    private TextView happynessText;
    private TextView hungrynessText;
    private TextView fruitText;
    private TextView currentRoom;

    private boolean bathroom = false;
    private boolean forest = false;
    private boolean gym = false;

    private int hygiene;
    private int happyness;
    private int hungryness;
    private int fruit;

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

    private long steps = 0;
    Handler customHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        customHandler = new Handler();
//        customHandler.postDelayed(petAction, 60000);

        setupComponents();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (forest && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long curTime = System.currentTimeMillis();
                    // only allow one update every 100ms.
                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        x = Float.parseFloat(String.valueOf(event.values[0]));
                        y = Float.parseFloat(String.valueOf(event.values[1]));
                        z = Float.parseFloat(String.valueOf(event.values[2]));

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                        if (speed > SHAKE_THRESHOLD && forest) {
                            Log.d("sensor", "shake detected w/ speed: " + speed);
                            fruit = fruit + 1;
                            fruitText.setText(String.valueOf(fruit));
                        }

                        last_x = x;
                        last_y = y;
                        last_z = z;
                    }
                }

                    if (gym && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                        float[] values = event.values;
                        int value = -1;

                        if (values.length > 0) {
                            value = (int) values[0];
                        }

                        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                            steps = steps + 1;
                            Log.d("STEP", "step detected");
                        }
                    }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d("ACCURACY_CHANGE", sensor.toString() + " - " + accuracy);
            }
        }, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (forest) {
            ImageView img = (ImageView) findViewById(R.id.background);
            img.setImageResource(R.drawable.forest);
        }
    }

//    private Runnable petAction = new Runnable()
//    {
//        public void run()
//        {
//            if(hungryness > 60 && happyness > 60 && hygiene > 60){
//                Toast.makeText(getApplicationContext(), "You're a good pet owner", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(getApplicationContext(), "You're a shit as a pet owner XD", Toast.LENGTH_LONG).show();
//            }
//            Log.d("Action", "Pet did an action");
//            customHandler.postDelayed(this, 60000);
//        }
//    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bathroom) {
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
        forest = true;
        hygiene = 100;
        happyness = 100;
        hungryness = 100;
        fruit = 0;

        hygieneText = (TextView)findViewById(R.id.hygieneText);
        happynessText = (TextView)findViewById(R.id.happynessText);
        hungrynessText = (TextView)findViewById(R.id.hungrynessText);
        fruitText = (TextView)findViewById(R.id.fruitText);
        currentRoom = (TextView)findViewById(R.id.currentRoom);

        hygieneText.setText(String.valueOf(hygiene));
        happynessText.setText(String.valueOf(happyness));
        hungrynessText.setText(String.valueOf(hungryness));
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
                if (hungryness < 100 && fruit > 0) {
                    hungryness = hungryness + 5;
                    hungrynessText.setText(String.valueOf(hungryness));
                    fruit = fruit - 1;
                    fruitText.setText(String.valueOf(fruit));
                    Log.d("Feed Button", "pet fed");
                } else if (hungryness >= 100 && fruit > 0) {
                    Toast.makeText(getApplicationContext(), "Pet Already Fed", Toast.LENGTH_LONG).show();
                    Log.d("Feed Button", "pet already fed");
                } else if (fruit == 0) {
                    Toast.makeText(getApplicationContext(), "No fruits", Toast.LENGTH_LONG).show();
                    Log.d("Feed Button", "no fruit");
                }
            }
        });

        reduceStats = (Button) findViewById(R.id.reduceStats);
        reduceStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Reduce Stats Button", "clicked the reduce stats button");
                if (hygiene > 50 || happyness > 50 || hungryness > 50) {
                    hygiene = 50;
                    hygieneText.setText(String.valueOf(hygiene));
                    happyness = 50;
                    happynessText.setText(String.valueOf(happyness));
                    hungryness = 50;
                    hungrynessText.setText(String.valueOf(hungryness));
                    Log.d("Reduce Stats Button", "set stats to 50");
                } else {
                    hygiene = 0;
                    hygieneText.setText(String.valueOf(hygiene));
                    happyness = 0;
                    happynessText.setText(String.valueOf(happyness));
                    hungryness = 0;
                    hungrynessText.setText(String.valueOf(hungryness));
                    Log.d("Reduce Stats Button", "set stats to 0");
                }
            }
        });

        leftArrowButton = (ImageButton) findViewById(R.id.leftArrow);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (forest) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.bathroom);
                    forest = false;
                    bathroom = true;
                    currentRoom.setText("Bathroom");
                    Log.d("BATHROOM", "bathroom true");
                }
                else if (gym) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    forest = true;
                    gym = false;
                    currentRoom.setText("Forest");
                    Log.d("FOREST", "forest true");
                }
                Log.d("LEFT ARROW", "pressed left arrow");
            }
        });

        rightArrowButton = (ImageButton) findViewById(R.id.rightArrow);
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (forest) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.gym);
                    forest = false;
                    gym = true;
                    currentRoom.setText("Gym");
                    Log.d("GYM", "gym true");
                }
                else if (bathroom) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    forest = true;
                    bathroom = false;
                    currentRoom.setText("Forest");
                    Log.d("FOREST", "forest true");
                }
                Log.d("RIGHT ARROW", "pressed right arrow");
            }
        });
    }
}