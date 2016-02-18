package com.citybility.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;

import com.citybility.app.IntroAnimationFragment.OnIntroAnimationFragmentInteractionListener;

public class IntroActivity extends Activity implements OnIntroAnimationFragmentInteractionListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new IntroAnimationFragment()).commit();
		}
	}

	/******************************************************************
	 * OnIntroAnimationFragmentInteractionListener IMPLEMENTATION
	 ******************************************************************/
	@Override
	public void onAnimationEnd() {
		
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.object_fade_in_slow, R.anim.object_fade_out_slow);
		
		IntroSliderFragment introSliderFragment = new IntroSliderFragment();
	
		ft.replace(R.id.container, introSliderFragment, "introSliderFragment");
		
		 // Execute some code after 2 seconds have passed
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 ft.commit();
	         } 
	    }, 500);
				
	}	
	
}
