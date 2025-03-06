package com.example.stepcounter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.hardware.SensorManager;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import android.os.CountDownTimer;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private Button pauseButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private ProgressBar progressBar;
    private float stepLengthInMeters = 0.762f;
    private int stepCountTarget = 5000;

    private int initialStepCount = 0;
    private boolean isFirstReading = true;  // Flag to check first sensor reading

    // Timer
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private static final long TIMER_DURATION = 60000; // 1 minute (60,000 ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the UI elements
        stepCountTextView = findViewById(R.id.stepCountTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        pauseButton = findViewById(R.id.pauseButton);
        progressBar = findViewById(R.id.progressBar);

        // Retrieve the system's SensorManager service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Set the maximum value of the progress bar to the user's step goal
        progressBar.setMax(stepCountTarget);

        if (stepCounterSensor == null) {
            stepCountTextView.setText("Step Counter Sensor not available");
        }

        // Request Permission for Activity Recognition (API 29+)
        requestActivityRecognitionPermission();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) sensorEvent.values[0];

            if (isFirstReading) {
                initialStepCount = totalSteps;  // Store first reading
                isFirstReading = false;         // Prevent resetting again
            }

            stepCount = totalSteps - initialStepCount;  // Calculate steps from reset point
            stepCountTextView.setText("Steps: " + stepCount);
            progressBar.setProgress(stepCount * 30);  // Adjust multiplier as needed

            float distanceInKm = stepCount * stepLengthInMeters / 1000;
            distanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", distanceInKm));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // No implementation needed
    }

    private void requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) { // Activity Recognition Permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Step counter is now enabled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied! Step counter may not work.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onPauseButtonclicked(View view) {
        if (isTimerRunning) {
            // Stop Timer
            countDownTimer.cancel();
            isTimerRunning = false;
            pauseButton.setText("Start Timer");
            timeTextView.setText("00:00"); // Reset display

            // Stop the step counter sensor when stopping the timer
            sensorManager.unregisterListener(this, stepCounterSensor);

        } else {
            // Start Timer
            isTimerRunning = true;
            pauseButton.setText("Stop Timer");

            // Reset step count when timer stops
            initialStepCount = stepCount + initialStepCount;  // Store last step count as base
            stepCount = 0;  // Reset displayed steps
            stepCountTextView.setText("Steps: 0");
            progressBar.setProgress(0);
            distanceTextView.setText("0.00 km");
            isFirstReading = true;  // Allow fresh reset

            // Register the sensor listener to start step counting
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int min = seconds / 60;
                    seconds = seconds % 60;
                    timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", min, seconds));
                }

                public void onFinish() {
                    timeTextView.setText("00:00");
                    isTimerRunning = false;
                    pauseButton.setText("Start Timer");
                    Toast.makeText(MainActivity.this, "Time is up!", Toast.LENGTH_SHORT).show();

                    // Stop the step counter sensor when the timer finishes
                    sensorManager.unregisterListener(MainActivity.this, stepCounterSensor);

                }
            }.start();
        }
    }
}
