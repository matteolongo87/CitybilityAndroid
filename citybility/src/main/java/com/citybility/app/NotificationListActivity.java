package com.citybility.app;

import android.content.Intent;
import android.os.Bundle;

import com.citybility.app.NotificationListFragment.OnNotificationListFragmentInteractionListener;

public class NotificationListActivity extends CitybilityFrameActivity implements OnNotificationListFragmentInteractionListener {

	NotificationListFragment mNotificationListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((CitybilityApplication)getApplication()).startLoading(this);
		
		setContentView(R.layout.application_activity_main_layout);
	
		if (savedInstanceState == null) {
			mNotificationListFragment = new NotificationListFragment();
			getFragmentManager().beginTransaction().add(R.id.container, mNotificationListFragment).commit();
		}
		
		addBottomBarListener();
		setTitle(getResources().getString(R.string.title_activity_notification_list));
	}

	
	
	private void startProjectDetailActivity(long id) {
		Intent detailIntent = new Intent(this, NotificationDetailActivity.class);
		detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}

	@Override
	protected void  onNewIntent(Intent intent){
		super.onNewIntent(intent);
	
	
	}

	/*******************************************************************
	 * OnNotificationFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/

	@Override
	public void onNotificationSelected(long id) {
		this.startProjectDetailActivity(id);		
	}

}
