package com.citybility.app;

import net.citybility.services.CommunicationManagerService;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

import com.citybility.app.CampaignListFragment.OnCampaignsListFragmentInteractionListener;
import com.citybility.app.CampaignTabsFragment.OnCampaignTabsFragmentInteractionListener;
import com.citybility.app.CategoryListFragment.OnCategorieslistFragmentInteractionListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.Constant.CampaignFilter;

public class CampaignTabsActivity extends CitybilityFrameActivity implements OnCampaignTabsFragmentInteractionListener, OnCampaignsListFragmentInteractionListener, OnCategorieslistFragmentInteractionListener, OnCloseListener {

	private CampaignTabsFragment mCampaignTabsFragment;
	private Menu mMenu;
	private CampaignFilter mCmpFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.application_activity_main_layout);
		
		if (savedInstanceState == null) {
			mCampaignTabsFragment = new CampaignTabsFragment();
			getFragmentManager().beginTransaction().add(R.id.container, mCampaignTabsFragment).commit();
		}

		addBottomBarListener();
		setActiveBottomBarButton(R.id.btnCampagne, findViewById(R.id.bottomBar));
		
		((CitybilityApplication)getApplication()).startLoading(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mMenu = menu;
		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnCloseListener(this);

		hideBottomBarOnSearch(searchView, findViewById(R.id.container));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_search:
			return true;
		case R.id.action_notifiche:
			startActivity( new Intent(this, NotificationListActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if(intent.getAction()==null){
			updateList(null);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			if(!this.isBottomBarVisible())
				this.showBottomBar();
			
			String query = intent.getDataString();
			if (query == null)
				query = intent.getStringExtra("query");
			if (query != null) {
				this.updateList(query);
			}

		} 
	}

	
	private void updateList(String query) {
		CitybilityRequest req = new CitybilityRequest(this, new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("CampaignsLocations")) {
					try {
						mCampaignTabsFragment.updateCurrentPageList(CityUtils.convertJSONArayToArrayList(result.getJSONArray("CampaignsLocations")));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onError(int errorCode, String message) {
				CityUtils.showErrorAlert(CampaignTabsActivity.this, getString(R.string.msg_error), message);
			}

		});

		// refresh the location
		CommunicationManagerService.getCurrentlocation();
		
		double[] gps = CityUtils.getGPSCoordinates();
		
		switch (this.mCmpFilter) {
		case HOME:
			req.campaignsLocations(0, gps, 0, mCmpFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		case NEARBY:
			req.campaignsLocations(0, gps, 10000, mCmpFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		case RECENTS:
			req.campaignsLocations(0, gps, 0, mCmpFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		default:
			break;
		}		
	}
	
	private void startCampaignDetailActivity(long id) {

		Intent detailIntent = new Intent(this, CampaignDetailActivity.class);
		detailIntent.putExtra(CampaignDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);

	}

	/*******************************************************************
	 * OnCampaignTabsFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/
	@Override
	public void onPageSelected(int position, CampaignFilter filter, CharSequence title) {
		getActionBar().setTitle(title);
		mCmpFilter = filter;

		// Category
		if (mMenu != null) {
			MenuItem searchItem = mMenu.findItem(R.id.action_search);
			if (filter == CampaignFilter.CATEGORY)
				searchItem.setVisible(false);
			else
				searchItem.setVisible(true);
		}

	}

	/*******************************************************************
	 * OnCampaignslistFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/
	@Override
	public void onCampaignSelected(CampaignFilter parentList, long id) {
		this.startCampaignDetailActivity(id);
	}

	/*******************************************************************
	 * OnCategorieslistFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/

	@Override
	public void onCategoriesListFragmentInteraction(String id, String name) {

		Intent campaignFoundIntent = new Intent(this, CampaignsByCategoryActivity.class);
		campaignFoundIntent.putExtra(CampaignListFragment.CAMPAIGN_FILTER, CampaignFilter.CATEGORY.toString());
		campaignFoundIntent.putExtra(CampaignListFragment.ARG_CAT_ID, id);
		campaignFoundIntent.putExtra(CampaignListFragment.ARG_CAT_NAME, name);
		startActivity(campaignFoundIntent);
	}
	
	/*******************************************************************
	 * OnCloseListener Interface methods
	 * implementation
	 *******************************************************************/
	
	@Override
	public boolean onClose() {
		updateList(null);
		return false;
	}


}
