package com.citybility.app;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.citybility.app.R;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.FacebookAPIHelper;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class CitybiliterDetailActivity extends CitybilityFrameActivity implements CitybiliterDetailFragment.OnCitybiliterDetailFragmentInteractionListener{

	private long mCitybiliterID;
	private CallbackManager mCallbackManager;
	private AccessTokenTracker accessTokenTracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		
		setContentView(R.layout.application_activity_main_layout);
		
		((CitybilityApplication)getApplication()).startLoading(this);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(CitybiliterDetailFragment.ARG_ITEM_ID, getIntent().getLongExtra(CitybiliterDetailFragment.ARG_ITEM_ID, 0));
			
			CitybiliterDetailFragment fragment = new CitybiliterDetailFragment();
			fragment.setArguments(arguments);
			
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
		}
		
		addBottomBarListener();
		setActiveBottomBarButton(R.id.btnCitibiliter, findViewById(R.id.bottomBar));

		mCallbackManager = CallbackManager.Factory.create();
		
		
		accessTokenTracker = new AccessTokenTracker() {

				@Override
				protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
		             AccessToken.setCurrentAccessToken(currentAccessToken);
				}
		    };
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) { // Up Button Navigation
			Intent HomeActivityIntent = new Intent(this, CitybiliterListActivity.class);
			NavUtils.navigateUpTo(this, HomeActivityIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onDestroy() {
	    super.onDestroy();
	    accessTokenTracker.stopTracking();
	}
	/*******************************************************************
	 * OnCitybiliterDetailFragmentInteractionListener Interface methods implementations
	 *******************************************************************/		
	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO gestire eventuale interazione tra fragment e activity
		
	}

	@Override
	public void onShare() {

		((CitybilityApplication) getApplication()).startLoading(this);
		new CitybilityRequest(this, new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {

				try {
					new FacebookAPIHelper(CitybiliterDetailActivity.this, mCallbackManager).postLink(result, null);
				} catch (JSONException e) {
					((CitybilityApplication) getApplication()).stopLoading();
					CityUtils.showErrorAlert(CitybiliterDetailActivity.this, getString(R.string.msg_error), getString(R.string.fb_publish_error_msg));
					Log.e(this.getClass().getName(), e.getMessage());
					Log.e(this.getClass().getName(), Arrays.toString(e.getStackTrace()));
					e.printStackTrace();
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				((CitybilityApplication) getApplication()).stopLoading();
				CityUtils.showErrorAlert(CitybiliterDetailActivity.this, getString(R.string.msg_error), message);
			}
		}).inviteMessage(mCitybiliterID, Constant.MessageType.USER.getType());
	}

}
