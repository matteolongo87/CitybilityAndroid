package com.citybility.app;

import android.os.Bundle;

public class ImageSliderActivity extends CitybilityBaseActivity {
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.application_activity_main_layout_simple);
		if (paramBundle == null) {
			Bundle localBundle = new Bundle();
			localBundle.putInt("IMAGE_START_POSITION", getIntent().getIntExtra("IMAGE_START_POSITION", 0));
			localBundle.putCharSequenceArray("URL_ARRAY", getIntent().getCharSequenceArrayExtra("URL_ARRAY"));
			
			ImageSliderFragment localImageSliderFragment = new ImageSliderFragment();
			localImageSliderFragment.setArguments(localBundle);
			getFragmentManager().beginTransaction().add(R.id.container, localImageSliderFragment).commit();
		}
	}
}
