package net.citybility.services;

import java.util.ArrayList;
import java.util.List;

import net.citybility.connect.APIWrapper;
import net.citybility.connect.CitybilityConnectApplication;
import net.citybility.connect.DaoHelper;
import net.citybility.connect.DebugLog;
import net.citybility.connect.JSONRestClient;
import net.citybility.connect.CommunicationUtils;
import net.citybility.dao.Call;
import net.citybility.dao.CallDao;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Classe CommunicationManagerService di AdAgio
 * 
 * @author ProfessioneIT
 * 
 */
public class CommunicationManagerService extends Service {

	enum State {
		STOPPED, STARTING, STARTED
	}

	public static State status = State.STOPPED;
	List<Call> callQueue = new ArrayList<Call>();
	private static CommunicationManagerService mConn;
	private static Location mCurrentlocation;
	private static final JSONRestClient jsonRestClient = JSONRestClient.getInstance();
	private static DoRequest doRequest; 
	private static CallDao callDao = DaoHelper.getIstance(CitybilityConnectApplication.getInstance()).getCallDao();

	public static CommunicationManagerService getIstance() {
		return mConn;
	}

	@Override
	public void onCreate() {
		synchronized (this) {
			super.onCreate();
			mConn = this;
			if (CommunicationUtils.isRunningInEmulator()){
				mCurrentlocation = new Location(LOCATION_SERVICE);
				mCurrentlocation.setLatitude(41.889187);
				mCurrentlocation.setLongitude(12.498257200000012);
			} else {
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, locationListener);

				// inizializzo mCurrentlocation l'ultima location nota
				List<String> providers = locationManager.getProviders(true);
				for (int i = providers.size() - 1; i >= 0; i--) {
					if ((mCurrentlocation = locationManager.getLastKnownLocation(providers.get(i))) != null)
						break;
				}

			}

			TelephonyManager myTelephonyManager = (TelephonyManager)this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			myTelephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE); 

		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		synchronized (this) {
			super.onStartCommand(intent, flags, startId);
			status = State.STARTED;
			// this.consumeCallQueue(); 
			return 1;
		}
	}

	private void consumeCallQueue() {
		callQueue = callDao.loadAll();
		for (Call call: callQueue){
			synchronized (this) {
				doRequest = new DoRequest(call);
				doRequest.execute();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*****************************************************************************
	 * Localizzazione
	 *****************************************************************************/

	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {

		private static final int TWO_MINUTES = 1000 * 60 * 2;

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			if (isBetterLocation(location, mCurrentlocation)) {
				mCurrentlocation = location;
			}
		}

		/**
		 * Determines whether one Location reading is better than the current
		 * Location fix
		 * 
		 * @param location
		 *            The new Location that you want to evaluate
		 * @param currentBestLocation
		 *            The current Location fix, to which you want to compare the
		 *            new one
		 */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location,
			// use the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must
				// be worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return true;
			}
			return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}
	};


	/*****************************************************************************
	 * Connettività
	 *****************************************************************************/

	PhoneStateListener callStateListener = new PhoneStateListener(){
		public void onDataConnectionStateChanged(int state){
			switch(state){
			case TelephonyManager.DATA_CONNECTED:
				if(DebugLog.DEBUG)
					Log.d("CommunicationManagerService", "Connessione ristabilita. Chiamo consumeCallQueue");
				CommunicationManagerService.getIstance().consumeCallQueue();
				break;
			}   
		}
	};



	/*****************************************************************************
	 * Metodi statici
	 *****************************************************************************/

	public static boolean isStopped() {
		return (status == State.STOPPED);
	}

	public static synchronized String makeRequest(Activity activity, String url, JSONObject query, boolean isPost, boolean useQueue) {
		String result = "";
		if(useQueue){
			callDao.insert(new Call(null, url, query.toString(), isPost));
			
			if (status == State.STARTED) {
				if(DebugLog.DEBUG)
					Log.d("CommunicationManagerService", "@@@@@@@@@@@@@@@@ chiamo consumeCallQueue() da dentro makeRequest, State.STARTED");
				getIstance().consumeCallQueue();
			}

			if (isStopped()) {
				if(DebugLog.DEBUG){
					Log.d("CommunicationManagerService", "@@@@@@@@@@@@@@@@ chiamo startService ");
				}
				activity.startService(new Intent(activity, CommunicationManagerService.class));
			}

		} else {
			if(DebugLog.DEBUG)
				Log.d("CommunicationManagerService", "@@@@@@@@@@@@@@@@ eseguo la call senza accodamento da dentro makeRequest");
			if (isPost)
				jsonRestClient.post(url, query);
			else
				jsonRestClient.get(url);
		}
		return result;
	}


	public static Location getCurrentlocation() {
		return mCurrentlocation;
	}

	private static long nowTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	protected class DoRequest extends AsyncTask<Void, Void, JSONObject> {
		private Call request;

		public DoRequest(Call request) {
			this.request = request;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {

			JSONObject result = new JSONObject();
			APIWrapper aw = APIWrapper.getInstance();

			try {
				JSONObject newRequest = new JSONObject(request.getQuery());

				newRequest.put("TimestampUnix", nowTimestamp());
				newRequest.put("Identity", aw.getIdentity());
				
				if(DebugLog.DEBUG)
					Log.d("CommunicationManagerService", "@@@@@@@@@@@@@@@@ eseguo la call accodata in prima battuta in doInBackground");
				if (request.getUsePost())
					result = jsonRestClient.post(request.getUrl(), newRequest);
				else
					result = jsonRestClient.get(request.getUrl());

				Log.d("SERVICE","Request result: "+ result.toString(4));
				
				if(!result.isNull("Status")){
					switch (result.getInt("Status")) {
					case 0:
						callDao.delete(request);
						break;
						
					case 1:
						// TODO: Gestire il caso del TOKEN NON VALIDO
						callDao.delete(request);
						break;

					case 2:
					case 3:
						JSONObject resultLogin = aw.login(null, null);						
						if (!resultLogin.isNull("Status") && resultLogin.getInt("Status") == 0) {
							newRequest.put("TimestampUnix", nowTimestamp());
							newRequest.put("Identity", aw.getIdentity());

							callDao.update(new Call(request.getId(), request.getUrl(), newRequest.toString(), request.getUsePost()));

							if(DebugLog.DEBUG)
								Log.d("CommunicationManagerService", "@@@@@@@@@@@@@@@@ eseguo la call accodata in seconda battuta in doInBackground");

							if (request.getUsePost())
								result = jsonRestClient.post(request.getUrl(), newRequest);
							else
								result = jsonRestClient.get(request.getUrl());

							if (!result.isNull("Status") && result.getInt("Status") == 0)
								callDao.delete(request);
						}
						break;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return result;
		}
	} 

}
