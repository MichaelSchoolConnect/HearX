package com.lebogang.hearx;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class SensorViewModel extends ViewModel {

    public static final String NAME_KEY = "score";

    private MutableLiveData<String> score;

    public SavedStateHandle mState;

    public SensorViewModel() {
        score = new MutableLiveData<>();
    }

    // Expose an immutable LiveData
    public LiveData<String> getScore() {
        // getLiveData obtains an object that is associated with the key wrapped in a LiveData
        // so it can be observed for changes.
        return score;
    }

    public void saveScore(String score) {
        // Sets a new value for the object associated to the key. There's no need to set it
        // as a LiveData.
        mState.set(NAME_KEY, score);
    }
}
