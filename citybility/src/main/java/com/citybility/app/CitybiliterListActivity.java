package com.citybility.app;

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

import com.citybility.app.CitybiliterListFragment.OnCitybiliterListFragmentInteractionListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant.CitybiliterFilter;

public class CitybiliterListActivity extends CitybilityFrameActivity implements OnCitybiliterListFragmentInteractionListener, OnCloseListener {

	CitybiliterListFragment mCitybilitersListFragment;
	private CitybiliterFilter mCBFilter = CitybiliterFilter.ACTIVITY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((CitybilityApplication)getApplication()).startLoading(this);
		
		setContentView(R.layout.application_activity_main_layout);
	
		if (savedInstanceState == null) {
			mCitybilitersListFragment = new CitybiliterListFragment(CitybiliterFilter.ACTIVITY);
			getFragmentManager().beginTransaction().add(R.id.container, mCitybilitersListFragment).commit();
		}
		
		addBottomBarListener();
		setActiveBottomBarButton(R.id.btnCitibiliter, findViewById(R.id.bottomBar));
		setTitle(getResources().getString(R.string.citybiters_list_title));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	protected void  onNewIntent(Intent intent){
		super.onNewIntent(intent);

		if(intent.getAction()==null){
			mCitybilitersListFragment.resetCitybiliterList();
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			if(!this.isBottomBarVisible())
				this.showBottomBar();
			
			String query = intent.getDataString();
			if(query==null || query.isEmpty())
				query = intent.getStringExtra("query");
			if(query==null || query.isEmpty())
				query = intent.getStringExtra("user_query");
			
			if (query != null) {

				new CitybilityRequest(this, new OnRequestResultListener() {

					@Override
					public void onSuccess(JSONObject result) {
						if (result.has("Results")) {
							try {
								mCitybilitersListFragment.updateCitybiliterListFromSearchResult(CityUtils.convertJSONArayToArrayList(result.getJSONArray("Results")));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onError(int errorCode, String message) {
						CityUtils.showErrorAlert(CitybiliterListActivity.this, getString(R.string.msg_error), message);
					}

				}).citybiliterSearch(query);

			}    
		} 
	}
	

	private void startCitybiliterDetailActivity(long id) {
		Intent detailIntent = new Intent(this, CitybiliterDetailActivity.class);
		detailIntent.putExtra(CitybiliterDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}


	/*******************************************************************
	 * OnCitybiliterFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/

	@Override
	public void onCitybiliterSelected(long id) {
		this.startCitybiliterDetailActivity(id);		
	}

	@Override
	public void onCitybiliterFilterChange(CitybiliterFilter filter) {
		this.mCBFilter = filter;
		
	}
	
	/*******************************************************************
	 * OnCloseListener Interface methods
	 * implementation
	 *******************************************************************/
	
	@Override
	public boolean onClose() {
		mCitybilitersListFragment.resetCitybiliterList();
		return false;
	}

}
