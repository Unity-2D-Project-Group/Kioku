package pt.iade.memoriescompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button petSelectButton;
    private Button myProfileButton;
    private Button petButton;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;

    private TextView hygieneText;
    private TextView happynessText;
    private TextView hungrynessText;
    private TextView fruitText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        leftArrowButton = (ImageButton) findViewById(R.id.leftArrow);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (forest) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.bathroom);
                    forest = false;
                    bathroom = true;
                    Log.d("BATHROOM", "bathroom true");
                }
                else if (gym) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    forest = true;
                    gym = false;
                    Log.d("FOREST", "forest true");
                }
                Log.d("LEFT ARROW", "pressed left arrow");
            }
        });

        rightArrowButton = (ImageButton) findViewById(R.id.rightArrow);
        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (forest) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.gym);
                    forest = false;
                    gym = true;
                    Log.d("GYM", "gym true");
                }
                else if (bathroom) {
                    ImageView img = (ImageView) findViewById(R.id.background);
                    img.setImageResource(R.drawable.forest);
                    forest = true;
                    bathroom = false;
                    Log.d("FOREST", "forest true");
                }
                Log.d("RIGHT ARROW", "pressed right arrow");
            }
        });
    }
}