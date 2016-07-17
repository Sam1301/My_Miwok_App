package com.example.android.miwok;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A FragmentPagerAdapter for viewpager in {@link MainActivity}
 */
public class CategoryAdapter extends FragmentPagerAdapter {

    /**
     * Crete new {@link CategoryAdapter} object.
     *
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * To return fragment for every page
     * @param position position of page
     * @return fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NumbersFragment();
            case 1:
                return new FamilyFragment();
            case 2:
                return new ColorsFragment();
            case 3:
                return new PhrasesFragment();
        }
        return null;
    }

    /**
     * To return the number of pages in viewpager
     * @return number of pages
     */
    @Override
    public int getCount() {
        return 4;
    }
}
