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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button petSelectButton;
    private Button myProfileButton;
    private Button petButton;
    private ImageButton leftArrowButton;
    private ImageButton rightArrowButton;

    private boolean bathroom = false;
    private boolean forest = false;
    private boolean gym = false;

    private SensorManager sensorManager;
    private Sensor accelSensor;

    private static final int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float x;
    private float y;
    private float z;
    private float last_x;
    private float last_y;
    private float last_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupComponents();

        if (forest) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (accelSensor == sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
                        long curTime = System.currentTimeMillis();
                        // only allow one update every 100ms.
                        if ((curTime - lastUpdate) > 100) {
                            long diffTime = (curTime - lastUpdate);
                            lastUpdate = curTime;

                            x = Float.parseFloat(String.valueOf(event.values[0]));
                            y = Float.parseFloat(String.valueOf(event.values[1]));
                            z = Float.parseFloat(String.valueOf(event.values[2]));

                            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                            if (speed > SHAKE_THRESHOLD) {
                                Log.d("sensor", "shake detected w/ speed: " + speed);
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
        }

        if (forest) {
            ImageView img = (ImageView) findViewById(R.id.background);
            img.setImageResource(R.drawable.forest);
        }
    }

    public void setupComponents() {
        forest = true;

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