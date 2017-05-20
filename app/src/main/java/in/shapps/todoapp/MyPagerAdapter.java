package in.shapps.todoapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by James on 1/24/2016.
 */
public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    private HashMap<Integer,List> listMap;
    private Fragment mCurrentFragment;

    public MyPagerAdapter(FragmentManager fragmentManager, HashMap<Integer,List> listMap1) {
        super(fragmentManager);
        listMap=listMap1;
        if(listMap==null)
            listMap=new HashMap<>();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        if (listMap.size() == 0) {
            return 1;
        }
        return listMap.size();
        //return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int id) {
        if (listMap.size() == 0) {
            return FirstTimeFragment.newInstance();
        }
        return ListFragment.newInstance(listMap.get(id).getId());
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if (listMap.size() == 0) {
            return "";
        }
        return listMap.get(position).getListName();
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getFragment(int key) {
        return registeredFragments.get(key);
    }

}