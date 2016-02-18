package com.citybility.app.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.citybility.app.ProjectListFragment;
import com.citybility.app.R;
import com.citybility.app.util.Constant.ProjectFilter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ProjectPagerAdapter extends FragmentPagerAdapter {
	
	private Context mContext;
	private ListFragment[] pages;
	public static ProjectFilter filters[] = {ProjectFilter.HOME, ProjectFilter.NEARBY, ProjectFilter.RECENTS};
	public static int titleIds[] = {R.string.prj_title_tab1, R.string.prj_title_tab2, R.string.prj_title_tab3,};
	
    public ProjectPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        pages = new ListFragment[3];
    }

    @Override
    public Fragment getItem(int position) {
    	switch (position) {
        case 0:
        	if(pages[0] == null)
        		pages[0] = new ProjectListFragment(filters[0]);
            return pages[0];
        case 1:
        	if(pages[1] == null)
        		pages[1] = new ProjectListFragment(filters[1]);
            return pages[1];
        case 2:
        	if(pages[2] == null)
        		pages[2] = new ProjectListFragment(filters[2]);
            return pages[2];
    }
    return null;
    }

    
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(titleIds[position]);
    }
    
    public ProjectFilter getPageFilter(int position) {

        return filters[position];
    }
  
}
  
