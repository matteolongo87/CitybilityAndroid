package com.citybility.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.citybility.app.adapter.ImageSliderPagerAdapter;
import com.citybility.app.custom.PageIndicatorsView;

public class ImageSliderFragment extends Fragment implements ViewPager.OnPageChangeListener {
	public static final String ARG_IMAGE_START_POSITION = "IMAGE_START_POSITION";
	public static final String ARG_URL_ARRAY = "URL_ARRAY";
	private String[] imageUrls;
	private PageIndicatorsView mPageIndicatorsView;
	private ImageSliderPagerAdapter mSectionsPagerAdapter;
	private int mStartPosition;
	private ViewPager mViewPager;

	public String[] getImageUrls() {
		return this.imageUrls;
	}

	public ViewPager getViewPager() {
		return this.mViewPager;
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		if (getArguments().containsKey("IMAGE_START_POSITION"))
			this.mStartPosition = getArguments().getInt("IMAGE_START_POSITION");
		if (getArguments().containsKey("URL_ARRAY"))
			this.imageUrls = getArguments().getStringArray("URL_ARRAY");
	}

	public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
		View localView = paramLayoutInflater.inflate(R.layout.fragment_image_slider, paramViewGroup, false);
		this.mSectionsPagerAdapter = new ImageSliderPagerAdapter(getFragmentManager(), getActivity(), this.imageUrls);
		this.mViewPager = ((ViewPager) localView.findViewById(R.id.imageSliderPager));
		this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
		this.mViewPager.setCurrentItem(this.mStartPosition);
		this.mPageIndicatorsView = ((PageIndicatorsView) localView.findViewById(R.id.pagerIndicators));
		this.mPageIndicatorsView.setPointNumber(this.imageUrls.length);
		this.mPageIndicatorsView.setActivePoint(1 + this.mStartPosition);
		return localView;
	}

	public void onPageScrollStateChanged(int paramInt) {
	}

	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
	}

	public void onPageSelected(int paramInt) {
		if (this.mPageIndicatorsView != null) {
			this.mPageIndicatorsView.setActivePoint(paramInt + 1);
			this.mPageIndicatorsView.updateIndicators();
		}
	}

	public void setImageUrls(String[] paramArrayOfString) {
		this.imageUrls = paramArrayOfString;
	}

	public void setViewPager(ViewPager paramViewPager) {
		this.mViewPager = paramViewPager;
	}
}
