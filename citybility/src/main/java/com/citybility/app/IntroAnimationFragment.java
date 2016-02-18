package com.citybility.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class IntroAnimationFragment extends Fragment {
	
	private int FADE_DURATION_LONG = 4000;
	private OnIntroAnimationFragmentInteractionListener mListener;
	private View backgroundImageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_intro_animation, container, false);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){

		
		backgroundImageView = view.findViewById(R.id.background);

		//fade in
		AlphaAnimation fadeIn = new  AlphaAnimation(0, 1);
		fadeIn.setDuration(FADE_DURATION_LONG);	
		fadeIn.setStartOffset(200);
		fadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mListener.onAnimationEnd();
			}
		});
		
		backgroundImageView.startAnimation(fadeIn);
		 
		 backgroundImageView.setAlpha(0f);
		 backgroundImageView.animate().setDuration(FADE_DURATION_LONG).alpha(1f).start();
		
	}

	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnIntroAnimationFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnIntroAnimationFragmentInteractionListener");
		}
	}
	
	
	
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 */
	public interface OnIntroAnimationFragmentInteractionListener {
		public void onAnimationEnd();
	}
}
