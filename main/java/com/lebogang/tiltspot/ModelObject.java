package com.lebogang.tiltspot;

public enum ModelObject {

    //List all the pages of the viewPager
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
