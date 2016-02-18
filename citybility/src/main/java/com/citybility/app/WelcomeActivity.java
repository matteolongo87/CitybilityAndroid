package com.citybility.app;

import net.citybility.connect.CitybilityConnectApplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;

public class WelcomeActivity extends CitybilityBaseActivity implements OnClickListener{

	SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		checkIntro();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
				
		findViewById(R.id.btnSignIn).setOnClickListener(this);
	    findViewById(R.id.btnSignUp).setOnClickListener(this);

	    ((CitybilityApplication)getApplication()).initGCM(this);
	    checkIsLoggedIn();
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    ((CitybilityApplication)getApplication()).initGCM(this);
		checkIsLoggedIn();
	}
	
	@Override
	public void onClick(View v) {
		final int id = v.getId();
	    switch (id) {
	    case R.id.btnSignIn:
	    	Intent loginIntent = new Intent(this, LoginActivity.class);
	    	startActivity(loginIntent);
	        break;
	    case R.id.btnSignUp:
	    	Intent registerIntent = new Intent(this, SignupActivity.class);
	    	startActivity(registerIntent);
	        break;
	    }
		
	}

	private void checkIntro() {
		if(mPrefs.getBoolean(Constant.SHOW_INTRO_PARAM, true)){
			startActivity(new Intent(this, IntroActivity.class));
			finish();
		}
	}
	
	private void checkIsLoggedIn() {
	    if(CitybilityConnectApplication.isLoggedIn()){
	    	startActivity(new Intent(getApplicationContext(), CityUtils.DEFAULT_ACTIVITY_CLASS));
	    	finish();
	    }		
	}

}	
