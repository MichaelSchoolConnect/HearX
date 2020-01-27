package com.lebogang.hearx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PlayGameActivity extends AppCompatActivity {

    private static final String TAG = PlayGameActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_main);

            if(savedInstanceState == null){
                checkOrientation();
            }else{

            }


        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void checkOrientation(){
        Log.e(TAG, "checkOrientation()");
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            //SensorFragment fragment = new SensorFragment();
            fragmentTransaction.add(R.id.fragment_container, SensorFragment.newInstance());
            fragmentTransaction.commit();
        } else {
            // In portrait
            showToastMessage();
        }
    }

    //Show an error message if device is in Portrait mode.
    private void showToastMessage(){
        Toast.makeText(this, R.string.layout_error, Toast.LENGTH_SHORT).show();
    }
}
