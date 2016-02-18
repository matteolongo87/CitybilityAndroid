package com.citybility.app;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.citybility.app.CampaignListFragment.OnCampaignsListFragmentInteractionListener;
import com.citybility.app.util.Constant.CampaignFilter;
import com.citybility.app.R;

public class CampaignsByCategoryActivity extends CitybilityFrameActivity implements OnCampaignsListFragmentInteractionListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_activity_main_layout);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(CampaignListFragment.CAMPAIGN_FILTER, getIntent().getStringExtra(CampaignListFragment.CAMPAIGN_FILTER));
			arguments.putString(CampaignListFragment.ARG_CAT_ID, getIntent().getStringExtra(CampaignListFragment.ARG_CAT_ID));
			arguments.putString(CampaignListFragment.ARG_CAT_NAME, getIntent().getStringExtra(CampaignListFragment.ARG_CAT_NAME));
			
			CampaignListFragment fragment = new CampaignListFragment();
			fragment.setArguments(arguments);
			
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();

			addBottomBarListener();
			setActiveBottomBarButton(R.id.btnCampagne, findViewById(R.id.bottomBar));
			setTitle(getIntent().getStringExtra(CampaignListFragment.ARG_CAT_NAME));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) { // Up Button Navigation
			Intent HomeActivityIntent = new Intent(this, CampaignTabsActivity.class);
			NavUtils.navigateUpTo(this, HomeActivityIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/*******************************************************************
	 * OnCampaignslistFragmentInteractionListener Interface methods implementations
	 *******************************************************************/		

	@Override
	public void onCampaignSelected(CampaignFilter type, long id) {
		Intent detailIntent = new Intent(this, CampaignDetailActivity.class);
		detailIntent.putExtra(CampaignDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}


}
