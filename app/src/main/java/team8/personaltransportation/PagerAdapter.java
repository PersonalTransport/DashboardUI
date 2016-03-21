package team8.personaltransportation;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

/**
 * Created by justin on 3/20/16.
 */
public class PagerAdapter extends FragmentPagerAdapter{

    public PagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int arg0){
        switch (arg0){
            case 0:
                return new Fragment();
            case 1:
                return new FullscreenActivity2();
            default:
                break;
        }
        return null;
    }
    @Override
    public int getCount(){
        return 2;
    }
}
