package com.lebogang.hearx;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lebogang.hearx.adapter.CustomPagerAdapter;

import java.util.Random;

public class SensorFragment extends Fragment implements SensorEventListener,
        LifecycleObserver {

    private static String TAG = SensorFragment.class.getSimpleName();
    private static int COUNTDOWN = 3000;

    private SensorViewModel mViewModel;

    private SensorManager mSensorManager;
    private Sensor sensor;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private TriggerEventListener triggerEventListener;

    private Context context = getContext();

    private float[] inclineGravity = new float[3];
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float orientation[] = new float[3];
    private float pitch;
    private float roll;

    private ImageView image;
    TextView counttime;

    private int timerCOunter = 0;
    public int scoreCounter;

    public SensorFragment(){}

    public static SensorFragment newInstance() {
        return new SensorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sensor_fragment, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try{

            getViewLifecycleOwner().getLifecycle().addObserver(this);

            setUpSensorAndTriggerEventListener();
            //initListeners();

            counttime = getActivity().findViewById(R.id.view_score);

            image = getActivity().findViewById(R.id.arrow_bottom);
            /*Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
            image.startAnimation(animation1);*/

            // Obtain the ViewModel
            mViewModel = new ViewModelProvider(this).get(SensorViewModel.class);

            // Show the ViewModel property's value in a TextView
            mViewModel.getScore().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String savedString) {
                    ((TextView)getView().findViewById(R.id.view_score))
                            .setText(getString(R.string.saved_in_vm, savedString));
                }
            });

            startCountdown();

        }catch (Exception e){
            Log.e(TAG, "Error: "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void setUpSensorAndTriggerEventListener(){
        // Setup the sensors
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Log.d(TAG, "accelerometer is null");
        }
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer == null) {
            Log.d(TAG, "magnetometer is null");
        }
        detectWindowPosition();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
     void initListeners() {
        Log.i(TAG, "initListeners()");
        //Choose SensorManager.SENSOR_DELAY_GAME to drive a higher frame rate.
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void unregisterListenerOnPause(){
        Log.i(TAG, "unregisterListenerOnPause()");
        mSensorManager.unregisterListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void unregisterListenerOnStop(){
        Log.i(TAG, "unregisterListenerOnStop()");
        mSensorManager.unregisterListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void unregisterListenerOnDestroy(){
        mSensorManager.unregisterListener(this);
    }

    //start countdown before the game begins.
    private void startCountdown(){
        chooseArrowColour();
        //if chosen begin sountdown.
        /*new CountDownTimer(COUNTDOWN,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counttime.setText(String.valueOf(timerCOunter));
                timerCOunter++;
                //while counting down check
                checkIfUserRespeondedQuickly();
            }
            @Override
            public void onFinish() {
                counttime.setText("Finished");
                showToastMessage();
                randomChoiceOfTilt();
            }
        }.start();*/
    }

    //randomly choose where to show the arrow
    private int randomChoiceOfTilt(){
        Random random = new Random();
        int a = random.nextInt(5);
        Log.i(TAG, "Random: " + a);
        if(a == 3){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            image.setImageResource(R.mipmap.ic_top_drawable_round);
        }
        return a;
    }

    //check if user tilted the device before the game has started and if true, decrement the score.
    private void checkIfUserRespeondedQuickly(){
        if(scoreCounter > 0){
            //User responded quickly
            scoreCounter--;
            Log.i(TAG, "User responded too quickly");
        }
    }

    //check how many times the user has played the game.
    private void checkGameAttemps(int attemptValue){
        Log.i(TAG, "checkGameAttemps: " + attemptValue);
        int maximumAttempt = 10;
        if(attemptValue >= maximumAttempt){
            //stop game.
            Log.i(TAG, "stop game.");
        }
    }

    //show toast when game has begun.
    private void showToastMessage(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), "Game has begun.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get arrow and colour at index and set them to users wishes.
    private void chooseArrowColour(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        View v = View.inflate(getContext(), R.layout.dialog_layout, null);
        builder.setView(v);
        ViewPager viewPager = v.findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomPagerAdapter(getActivity()));
        dialog = builder.create();
        dialog.show();
    }

    private void setArrowColour(View view){
        int id = view.getId();
        if(id == R.id.arrow_bottom){

        }

    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        //If type is accelerometer only assign values to global property mGravity
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;

            if (Math.abs(x) > Math.abs(y)) {
                if (x > 0) {
                    Log.d(TAG, "left");
                    //if tilt is greater than 50
                    if(x >= 50){
                        //increment user score
                        scoreCounter++;
                        Log.i(TAG, "scoreCounter: " + scoreCounter);
                        /**
                         * and check if it reached 10. If so, stop the game and present score
                         * to user.
                         * */
                        //mViewModel.saveScore(String.valueOf(scoreCounter));
                        checkGameAttemps(scoreCounter);
                    }
                }
            }

            if (isTiltDownward()) {
                Log.d(TAG, "downwards");
                if(x >= 50){
                    scoreCounter++;
                    Log.i(TAG, "scoreCounter: " + scoreCounter);
                    checkGameAttemps(scoreCounter);
                }
            } else if (isTiltUpward()) {
                Log.d(TAG, "upwards");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean isTiltUpward() {
        //Log.i(TAG, "isTiltUpward()");
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                /*
                 * If the roll is positive, you're in reverse landscape (landscape right), and if the roll is negative you're in landscape (landscape left)
                 *
                 * Similarly, you can use the pitch to differentiate between portrait and reverse portrait.
                 * If the pitch is positive, you're in reverse portrait, and if the pitch is negative you're in portrait.
                 *
                 * orientation -> azimut, pitch and roll
                 *
                 *
                 */

                pitch = orientation[1];
                roll = orientation[2];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on ground or not
                int inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));

                /*
                 * Float obj1 = new Float("10.2");
                 * Float obj2 = new Float("10.20");
                 * int retval = obj1.compareTo(obj2);
                 *
                 * if(retval > 0) {
                 * System.out.println("obj1 is greater than obj2");
                 * }
                 * else if(retval < 0) {
                 * System.out.println("obj1 is less than obj2");
                 * }
                 * else {
                 * System.out.println("obj1 is equal to obj2");
                 * }
                 */
                Float objPitch = new Float(pitch);
                Float objZero = new Float(0.0);
                Float objZeroPointTwo = new Float(0.2);
                Float objZeroPointTwoNegative = new Float(-0.2);

                int objPitchZeroResult = objPitch.compareTo(objZero);
                int objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch);
                int objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative);

                if (roll < 0 && ((objPitchZeroResult > 0 && objPitchZeroPointTwoResult > 0) || (objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult > 0)) && (inclination > 30 && inclination < 40))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean isTiltDownward() {
        //Log.i(TAG, "isTiltDownward()");
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                pitch = orientation[1];
                roll = orientation[2];

                inclineGravity = mGravity.clone();

                double norm_Of_g = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

                // Normalize the accelerometer vector
                inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g);
                inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g);
                inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g);

                //Checks if device is flat on groud or not
                int inclination = (int) Math.round(Math.toDegrees(Math.acos(inclineGravity[2])));

                Float objPitch = new Float(pitch);
                Float objZero = new Float(0.0);
                Float objZeroPointTwo = new Float(0.2);
                Float objZeroPointTwoNegative = new Float(-0.2);

                int objPitchZeroResult = objPitch.compareTo(objZero);
                int objPitchZeroPointTwoResult = objZeroPointTwo.compareTo(objPitch);
                int objPitchZeroPointTwoNegativeResult = objPitch.compareTo(objZeroPointTwoNegative);

                if (roll < 0 && ((objPitchZeroResult > 0 && objPitchZeroPointTwoResult > 0) || (objPitchZeroResult < 0 && objPitchZeroPointTwoNegativeResult > 0)) && (inclination > 140 && inclination < 170))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    // Detect the window position
    //@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void detectWindowPosition(){
        // Detect the window position
        switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                Log.d(TAG, "Rotation 0");
                break;
            case Surface.ROTATION_90:
                Log.d(TAG, "Rotation 90");
                break;
            case Surface.ROTATION_180:
                Log.d(TAG, "Rotation 180");
                break;
            case Surface.ROTATION_270:
                Log.d(TAG, "Rotation 270");
                break;
            default:
                Log.w(TAG, "Rotation unknown");
                break;
        }
    }

    /**
     * Convert degrees to absolute tilt value between 0-100
     */
    private int degreesToPower(int degrees) {
        // Tilted back towards user more than -90 deg
        if (degrees < -90) {
            degrees = -90;
        }
        // Tilted forward past 0 deg
        else if (degrees > 0) {
            degrees = 0;
        }
        // Normalize into a positive value
        degrees *= -1;
        // Invert from 90-0 to 0-90
        degrees = 90 - degrees;
        // Convert to scale of 0-100
        float degFloat = degrees / 90f * 100f;
        return (int) degFloat;
    }

}
