package com.citybility.app.request;

import net.citybility.connect.CitybilityConnectApplication;
import net.citybility.connect.CommunicationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.citybility.app.CitybilityApplication;
import com.citybility.app.NoNetworkFragment;
import com.citybility.app.WelcomeActivity;

public class AsyncRequest extends AsyncTask<Void, Void, JSONObject> {
	private OnRequestResultListener listener;
	private Activity activity;

	public AsyncRequest() {
	}

	public AsyncRequest(Activity activity, OnRequestResultListener listener) {
		this.activity = activity;
		
		try {
			this.listener = (OnRequestResultListener) listener;
		} catch (ClassCastException e) {
			throw new ClassCastException(listener.toString() + " must implement OnRequestResultListener");
		}
	}

	@Override
	protected void onPreExecute() {
		if(!isNetworkConnected(activity.getApplicationContext())){
			this.cancel(true);
			((CitybilityApplication)activity.getApplication()).stopLoading();
			NoNetworkFragment.showDialog(activity, "fragment_no_network");
			//new NoNetworkFragment(activity).show(activity.getFragmentManager(), "fragment_no_network");
		}
	}

	@Override
	protected JSONObject doInBackground(Void... params) {

		if (this.activity == null) {
			throw new NullPointerException("You must set the Activity");
		}

		if (this.listener == null) {
			throw new NullPointerException("You must set the listener");
		}

		JSONObject result = new JSONObject();
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		Log.d("@@@ onPostExecute", "@@@ result: " + result.toString());
		try {
			switch (result.getInt("Status")) {
			case 0:
				this.listener.onSuccess(result);
				break;
			case 3:
				CitybilityConnectApplication.removeIdentity();
				this.activity.startActivity(new Intent(this.activity, WelcomeActivity.class));
				break;
			default:
				this.listener.onError(result.getInt("Status"), result.getString("Message"));
			}
		} catch (JSONException e) {
			this.listener.onError(-1, "Errore inaspettato");
			e.printStackTrace();
		}
	}

	
	private boolean isNetworkConnected(Context applicationContext) {
		return CommunicationUtils.isNetworkConnected(activity.getApplicationContext());
	}
	
	public OnRequestResultListener getListener() {
		return listener;
	}

	public void setListener(OnRequestResultListener listener) {
		this.listener = listener;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}


	public interface OnRequestResultListener {

		public void onSuccess(JSONObject result);
		public void onError(int errorCode, String Message);
		
	}

}