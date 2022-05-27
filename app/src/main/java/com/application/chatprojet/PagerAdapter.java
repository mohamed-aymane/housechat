package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    //we store all number inside this variable
    private int tabcount;

    //constructor
    public PagerAdapter(@NonNull FragmentManager fm, int behavior){
        super(fm, behavior);
        tabcount=behavior;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatFragment();
            case 1:
                return  new CallsFragment();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
