package com.example.mysampleapp;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.mysampleapp.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener,View.OnTouchListener {

    ImageButton buttonTopLeft, buttonTopRight, buttonBottomLeft, buttonBottomRight;
    private ImageView imageViewArrow;
    private ObjectAnimator animationArrow;

    private GestureDetector gestureDetector;

    private boolean isDoubleTap;
    private  float longPressLb, longPressUb, doubleTapLb, doubleTapUb;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private float[] rotationMatrix;
    private float[] orientationAngles;
    private boolean isEnabled;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTopLeft = findViewById(R.id.buttonTopLeft);
        buttonBottomLeft = findViewById(R.id.buttonBottomLeft);
        buttonTopRight = findViewById(R.id.buttonTopRight);
        buttonBottomRight = findViewById(R.id.buttonBottomRight);
        imageViewArrow = findViewById(R.id.imageView);
        animationArrow = ObjectAnimator.ofFloat(imageViewArrow, View.ROTATION, 0f, 360f).setDuration(2000);
        animationArrow.setRepeatCount(Animation.INFINITE);
        animationArrow.setInterpolator(new LinearInterpolator());
        animationArrow.start();



        gestureDetector = new GestureDetector(getApplicationContext(), new GestureListener());
        buttonTopLeft.setOnTouchListener(this);
        buttonBottomRight.setOnTouchListener(this);
        buttonBottomLeft.setOnTouchListener(this);
        buttonTopRight.setOnTouchListener(this);

        accelerometerReading = new float[3];
        magnetometerReading = new float[3];
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);


        rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);


        orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);


        float f [] = SensorManager.getOrientation(rotationMatrix,orientationAngles);
        if((int)f[1]==-1){
            disableAll();
        }else {
            enableAll();
        }
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        Log.d("orientation", String.valueOf(f[1]));


    }

    private void disableAll() {
        if (isEnabled) {
            buttonTopRight.setClickable(false);
            buttonTopLeft.setClickable(false);
            buttonBottomLeft.setClickable(false);
            buttonBottomRight.setClickable(false);
            animationArrow.setRepeatCount(Animation.ABSOLUTE);

            isEnabled = false;

        }


    }
    private void enableAll(){
        if(!isEnabled){
            buttonTopRight.setClickable(true);
            buttonTopLeft.setClickable(true);
            buttonBottomLeft.setClickable(true);
            buttonBottomRight.setClickable(true);
            animationArrow.setRepeatCount(Animation.INFINITE);
            animationArrow.start();
            isEnabled =!isEnabled;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.buttonBottomLeft:
                longPressLb = 223;
                longPressUb = 226;

                break;
            case R.id.buttonBottomRight:

                longPressLb = 133;
                longPressUb = 136;
                break;
            case R.id.buttonTopLeft:
                longPressLb = 313;
                longPressUb = 316;
                break;
            case R.id.buttonTopRight:
                longPressLb = 43;
                longPressUb = 46;
                break;
        }
        doubleTapLb = (longPressLb + 180)%360;
        doubleTapUb = (longPressUb + 180)%360;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {


            isDoubleTap = false;

            animationArrow.resume();

            animationArrow.removeAllUpdateListeners();

        }


        return gestureDetector.onTouchEvent(motionEvent);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ValueAnimator.AnimatorUpdateListener animatorUpdateListenerLongPress, animatorUpdateListenerDoubleTap;

        public GestureListener(){
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            isDoubleTap = true;
            return super.onSingleTapUp(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }


        @Override
        public void onLongPress(MotionEvent e) {
            if (!isDoubleTap) {
                animatorUpdateListenerLongPress = new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        Log.d("valueLongPress", String.valueOf((float) valueAnimator.getAnimatedValue()));
                        if ((float) valueAnimator.getAnimatedValue() <= longPressUb && (float) valueAnimator.getAnimatedValue() >= longPressLb) {
                            animationArrow.pause();
                        }
                    }
                };
                animationArrow.addUpdateListener(animatorUpdateListenerLongPress);
            } else {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    animatorUpdateListenerDoubleTap = new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {

                            Log.d("valueDoubleTap", String.valueOf((float) valueAnimator.getAnimatedValue()));
                            if ((float) valueAnimator.getAnimatedValue() >= doubleTapLb && (float) valueAnimator.getAnimatedValue() <= doubleTapUb) {
                                animationArrow.pause();
                            }

                        }

                    };
                    animationArrow.addUpdateListener(animatorUpdateListenerDoubleTap);
                }
            }

            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {


            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return super.onDoubleTap(e);
        }
    }

}
