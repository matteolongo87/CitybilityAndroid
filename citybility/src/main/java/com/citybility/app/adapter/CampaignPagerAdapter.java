package com.citybility.app.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.support.v13.app.FragmentPagerAdapter;

import com.citybility.app.CampaignListFragment;
import com.citybility.app.CategoryListFragment;
import com.citybility.app.util.Constant.CampaignFilter;
import com.citybility.app.R;

import android.content.Context;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CampaignPagerAdapter extends FragmentPagerAdapter {
	
	private Context mContext;
	private ListFragment[] pages;
	public static CampaignFilter filters[] = {CampaignFilter.HOME, CampaignFilter.NEARBY, CampaignFilter.RECENTS, CampaignFilter.CATEGORY};
	public static int titleIds[] = {R.string.cs_title_tab1, R.string.cs_title_tab2, R.string.cs_title_tab3, R.string.cs_title_tab4};
	
	
    public CampaignPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        pages = new ListFragment[4];
    }

    @Override
    public Fragment getItem(int position) {
    	switch (position) {
        case 0:
        	if(pages[0] == null)
        		pages[0] = new CampaignListFragment(filters[0]);
            return pages[0];
        case 1:
        	if(pages[1] == null)
        		pages[1] = new CampaignListFragment(filters[1]);
            return pages[1];
        case 2:
        	if(pages[2] == null)
        		pages[2] = new CampaignListFragment(filters[2]);
            return pages[2];
        case 3:
        	if(pages[3] == null)
        		pages[3] = new CategoryListFragment();
            return pages[3];
    }
    return null;
    }

    
    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(titleIds[position]);
    }
    
    public CampaignFilter getPageFilter(int position) {

        return filters[position];
    }
  
}
  
