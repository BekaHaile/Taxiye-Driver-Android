package com.fugu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 2/1/18.
 */

public class FuguChannelsPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();

    /**
     * Constructor
     *
     * @param fm        fragment manager
     * @param fragments the fragments to include
     * @param titles    the titles array
     */
    public FuguChannelsPagerAdapter(final FragmentManager fm, final ArrayList<Fragment> fragments,
                                    final ArrayList<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(final int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return mTitles.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * Returns included fragments
     *
     * @return included fragments
     */
    public ArrayList<Fragment> getFragments() {
        return mFragments;
    }
}
