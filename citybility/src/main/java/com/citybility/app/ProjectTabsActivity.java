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

import com.citybility.app.ProjectListFragment.OnProjectListFragmentInteractionListener;
import com.citybility.app.ProjectTabsFragment.OnProjectTabsFragmentInteractionListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.Constant.ProjectFilter;

public class ProjectTabsActivity extends CitybilityFrameActivity implements OnProjectTabsFragmentInteractionListener, OnProjectListFragmentInteractionListener, OnCloseListener {

	private ProjectTabsFragment mProjectTabsFragment;
	private ProjectFilter mPrjFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.application_activity_main_layout);
		
		if (savedInstanceState == null) {
			mProjectTabsFragment = new ProjectTabsFragment();
			getFragmentManager().beginTransaction().add(R.id.container, mProjectTabsFragment).commit();
		}
		
		addBottomBarListener();
		setActiveBottomBarButton(R.id.btnProgetti, findViewById(R.id.bottomBar));
		
		((CitybilityApplication)getApplication()).startLoading(this);
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
			updateList(null);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			if(!this.isBottomBarVisible())
				this.showBottomBar();
			
			String query = intent.getDataString();
			if (query == null)
				query = intent.getStringExtra("query");
			if (query != null) {
				updateList(query);
			}    
		} 
	}

	private void updateList(String query) {
		CitybilityRequest req = new CitybilityRequest(this, new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("Initiatives")) {
					try {
						mProjectTabsFragment.updateCurrentPageList(CityUtils.convertJSONArayToArrayList(result.getJSONArray("Initiatives")));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onError(int errorCode, String message) {
				CityUtils.showErrorAlert(ProjectTabsActivity.this, getString(R.string.msg_error), message);
			}

		});

		// refresh the location
		CommunicationManagerService.getCurrentlocation();
		
		double[] gps = CityUtils.getGPSCoordinates();
		
		switch (this.mPrjFilter) {
		case HOME:
			req.initiatives(gps, 0, mPrjFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		case NEARBY:
			req.initiatives(gps, 10000, mPrjFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		case RECENTS:
			req.initiatives(gps, 0, mPrjFilter.getMode(), 0, Constant.DEFAULT_NUM_TO_TAKE, query);
			break;
		}
		
	}

	private void startProjectDetailActivity(long id) {
		Intent detailIntent = new Intent(this, ProjectDetailActivity.class);
		detailIntent.putExtra(ProjectDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}


	/*******************************************************************
	 * OnProjectTabsFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/
	@Override
	public void onPageSelected(int position, ProjectFilter filter, CharSequence title) {
		getActionBar().setTitle(title);
		this.mPrjFilter = filter;
	}
	
	/*******************************************************************
	 * OnProjectListFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/

	@Override
	public void onProjectSelected(long id) {
		this.startProjectDetailActivity(id);		
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
