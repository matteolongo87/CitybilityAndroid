package com.citybility.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;


public class IntroSliderFragment extends Fragment implements OnClickListener {
	

	enum Direction {BACK, FOWARD};
	
	
	private final int SLIDER_START = 3;
	private final int SLIDER_END = 6;
	
	Button mBtnSlideNext, mBtnSlideBack;
	private int mCurrentSlider = SLIDER_START;

	Animation mFadeInAnimation;
	Animation mFadeOutAnimation;
	
	SharedPreferences mPrefs;
	
	Direction mDirection = Direction.FOWARD;
			
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_intro_slider, container, false);		

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.slideFragment, new IntroSlideFragment(mCurrentSlider)).commit();
		}
			
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mFadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
		mFadeInAnimation.setDuration(1000);
		mFadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
		mFadeOutAnimation.setDuration(1000);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		mBtnSlideNext = (Button)view.findViewById(R.id.btnSlideNext);
		mBtnSlideBack = (Button)view.findViewById(R.id.btnSlideBack);
		mBtnSlideNext.setOnClickListener(this);
		mBtnSlideBack.setOnClickListener(this);
	}

	private void loadNextSlider() {
		mDirection = Direction.FOWARD;
		mCurrentSlider++;
		changeSlideWithAnimation();		
		handleNavigationButtons();
	}

	private void loadPreviousSlider() {
		mDirection = Direction.BACK;
		mCurrentSlider--;
		changeSlideWithAnimation();
		handleNavigationButtons();
	}
	
	private void changeSlideWithAnimation(){

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.object_fade_in, R.anim.object_fade_out);
		
		IntroSlideFragment introSlideFragment = new IntroSlideFragment(mCurrentSlider);
	
		ft.replace(R.id.slideFragment, introSlideFragment, "introSlideFragment");
		// Start the animated transition.
		ft.commit();		
	}

	private void handleNavigationButtons() {
		switch (mCurrentSlider) {
		case SLIDER_START + 1:
			if (mDirection.equals(Direction.FOWARD)) {
				mBtnSlideBack.setVisibility(View.VISIBLE);
				mBtnSlideBack.startAnimation(mFadeInAnimation);
			}
			break;
		case SLIDER_START:
			mBtnSlideBack.startAnimation(mFadeOutAnimation);
			mBtnSlideBack.setVisibility(View.GONE);
			break;
		}

	}
		

	/*******************************************************************
	 * OnClickListener Interface methods implementations
	 *******************************************************************/
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnSlideNext:
			if(mCurrentSlider == SLIDER_END){
				mPrefs.edit().putBoolean(Constant.SHOW_INTRO_PARAM, false).commit(); 
				startCitibility();
			} else {
				loadNextSlider();
			}
		break;
		case R.id.btnSlideBack:
			if(mCurrentSlider != SLIDER_START){
				loadPreviousSlider();
			}
		break;
		}
		
	}

	private void startCitibility() {
		final ImageView logo = (ImageView)getActivity().findViewById(R.id.logo);
		int centerIndp = CityUtils.convertDp2Px(50, getResources().getDisplayMetrics());
		ScaleAnimation superZoomAnim = new ScaleAnimation(1, 20, 1, 20, centerIndp, centerIndp);
		superZoomAnim.setDuration(500);
		superZoomAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				logo.setVisibility(View.INVISIBLE);
				getActivity().startActivity(new Intent(getActivity(), WelcomeActivity.class));
			}
		});
		
		logo.startAnimation(superZoomAnim);
		
	}
	
}
