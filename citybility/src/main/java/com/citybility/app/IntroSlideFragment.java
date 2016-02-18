package com.citybility.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroSlideFragment extends Fragment {

	private int mSlideNum = -1;

	public IntroSlideFragment() {
		this.mSlideNum = 3;
	}
	public IntroSlideFragment(int slideNum) {
		this.mSlideNum = slideNum;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int layout = -1;
		switch(mSlideNum){
		case 3 :
			layout = R.layout.fragment_intro_slide_3;
			break;
		case 4 :
			layout = R.layout.fragment_intro_slide_4;
			break;
		case 5 :
			layout = R.layout.fragment_intro_slide_5;
			break;
		case 6 :
			layout = R.layout.fragment_intro_slide_6;
			break;
		}
		
		View rootView = inflater.inflate(layout, container, false);
		return rootView;
	}

}
