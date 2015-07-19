package net.icedeer.abysmli.iasanalyse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abysmli on 18.06.15.
 */
public class ControllerFragmentsAdapter extends FragmentPagerAdapter {
    private String[] titles = {"Graphic", "Components", "Running Log"};

    public ControllerFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new GraphicFragment();
            case 1:
                return new ComponentsFragment();
            case 2:
                return new RunningLogFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

