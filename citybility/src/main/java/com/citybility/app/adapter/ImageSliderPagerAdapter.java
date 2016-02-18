package com.citybility.app.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import com.citybility.app.ImageSliderItemFragment;

public class ImageSliderPagerAdapter extends FragmentPagerAdapter {
	private Context mContext;
	private String[] mImageUrls;
	private Fragment[] pages;

	public ImageSliderPagerAdapter(FragmentManager paramFragmentManager, Context paramContext, String[] paramArrayOfString) {
		super(paramFragmentManager);
		this.setmContext(paramContext);
		this.mImageUrls = paramArrayOfString;
		this.pages = new Fragment[this.mImageUrls.length];
	}

	public int getCount() {
		return this.mImageUrls.length;
	}

	public Fragment getItem(int paramInt) {
		if (this.pages[paramInt] == null) {
			ImageSliderItemFragment localImageSliderItemFragment = new ImageSliderItemFragment(this.mImageUrls[paramInt]);
			this.pages[paramInt] = localImageSliderItemFragment;
		}
		return this.pages[paramInt];
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
}
