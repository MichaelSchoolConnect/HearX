package com.lebogang.hearx.model;

import com.lebogang.hearx.R;

/**
 * Created by anupamchugh on 26/12/15.
 */
public enum ModelObject {

    RED(R.string.app_name, R.layout.arrow_right),
    BLUE(R.string.app_name, R.layout.arrow_up),
    GREEN(R.string.app_name, R.layout.arrow_down),
    YELLOW(R.string.app_name, R.layout.arrow_left);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
