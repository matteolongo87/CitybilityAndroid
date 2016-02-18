package com.citybility.app;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citybility.app.util.HttpImageLoader;

public class ImageSliderItemFragment extends Fragment {
	private String mImageUrl;
	private View mRootView;

	public ImageSliderItemFragment() {
	}

	public ImageSliderItemFragment(String paramString) {
		this.mImageUrl = paramString;
	}
	
	@Override
	public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
		this.mRootView = paramLayoutInflater.inflate(R.layout.fragment_image_slider_item, paramViewGroup, false);
		
		ImageView localImageView = (ImageView) this.mRootView.findViewById(R.id.imageViewItem);
	
		Point outSize = new Point();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		display.getSize(outSize);
		localImageView.measure(outSize.x, outSize.y);

		new HttpImageLoader(getActivity(), this.mImageUrl, localImageView, false, true).load();
		return this.mRootView;
	}
	

}
