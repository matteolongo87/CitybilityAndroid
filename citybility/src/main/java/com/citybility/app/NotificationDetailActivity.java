package com.citybility.app;

import com.citybility.app.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class NotificationDetailActivity extends CitybilityFrameActivity implements NotificationDetailFragment.OnNotificationDetailFragmentInteractionListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_activity_main_layout);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(NotificationDetailFragment.ARG_ITEM_ID, getIntent().getLongExtra(NotificationDetailFragment.ARG_ITEM_ID, 0));
			
			NotificationDetailFragment fragment = new NotificationDetailFragment();
			fragment.setArguments(arguments);
			
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
		}
		
		addBottomBarListener();
	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) { // Up Button Navigation
			Intent HomeActivityIntent = new Intent(this, NotificationListActivity.class);
			NavUtils.navigateUpTo(this, HomeActivityIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/*******************************************************************
	 * OnNotificationDetailFragmentInteractionListener Interface methods implementations
	 *******************************************************************/		
	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO gestire eventuale interazione tra fragment e activity
		
	}

}
