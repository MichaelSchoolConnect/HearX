package com.lebogang.tiltspot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;


public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;

    /**
     * The constructor needs a Context reference. The context is saved as a member variable of the class
     * since it’s used later to access the individual page layouts from the enum class
     */
    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    /**
     * In this case, we use the enum and inflate the particular enum value’s associated layout.
     * Then, we add the newly inflated layout to the ViewGroup(collection of Views) maintained by
     * the PagerAdapter, and then we return that layout. The object being returned by this method is
     * also used later on, as the second parameter in the isViewFromObject method
     */
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;
    }

    /**
     * It simply returns the number of views that will be maintained by the ViewPager.
     * For this example, the count is the number of enum values in the model object
     */
    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    /**
     * This method checks whether a particular object belongs to a given position, which is made simple.
     * As noted earlier, the second parameter is of type Object and is the same as the return value
     * from the instantiateItem method
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];

        return mContext.getString(customPagerEnum.getTitleResId());
    }

}
