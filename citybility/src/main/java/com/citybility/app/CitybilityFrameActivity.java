package com.citybility.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.Constant;
import com.citybility.app.util.HttpImageLoader;
import com.professioneit.gcmclientsdk.GCMClientManager;
import com.professioneit.gcmclientsdk.NotificationHandler;

public class CitybilityFrameActivity extends CitybilityBaseActivity {
	
	private ScheduledExecutorService notificationBadgeUpdaterScheduler;
	private MenuItem notificationMenuItem;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.startNotificationBadgeUpdater();
	}

	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		this.startNotificationBadgeUpdater();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.notificationMenuItem = menu.findItem(R.id.action_notifiche);
		this.overrideSearchViewStyle(menu);
		this.initNotificationBadge();
		return true;
	}

	@Override
	protected void onPause(){
		this.stopNotificationBadgeUpdater();
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		this.startNotificationBadgeUpdater();
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish(){
		this.stopNotificationBadgeUpdater();
		super.finish();
	}
	

	/****************************************************
	 * ButtomBar management
	 ****************************************************/

	/**
	 * 
	 * ButtomBarClickListener inner class
	 *
	 */
	private class ButtomBarClickListener implements OnClickListener {

		private Context context;
		
		public ButtomBarClickListener(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			
			if(this.context == null)
				this.context = getApplicationContext();
			
			switch (v.getId()) {
			case R.id.btnCampagne:
				startActivity(new Intent(this.context, CampaignTabsActivity.class));
				break;
			case R.id.btnProgetti:
				startActivity(new Intent(this.context, ProjectTabsActivity.class));
				break;
			case R.id.btnRegistra:
				startActivity(new Intent(this.context, QRCodeScannerActivity.class));
				break;
			case R.id.btnCitibiliter:
				startActivity(new Intent(this.context, CitybiliterListActivity.class));
				break;
			case R.id.btnProfilo:
				startActivity(new Intent(this.context, ProfileActivity.class));
				break;

			}
			
		}
		
	}
	
	public void addBottomBarListener() {
		View bottomBar = getBottomBar();
		
		if(bottomBar != null){
			ButtomBarClickListener bbListener = new ButtomBarClickListener(this);

			bottomBar.findViewById(R.id.btnProgetti).setOnClickListener(bbListener);
			bottomBar.findViewById(R.id.btnCampagne).setOnClickListener(bbListener);
			bottomBar.findViewById(R.id.btnRegistra).setOnClickListener(bbListener);
			bottomBar.findViewById(R.id.btnCitibiliter).setOnClickListener(bbListener);
			bottomBar.findViewById(R.id.btnProfilo).setOnClickListener(bbListener);
		}
		
	}

	public void setActiveBottomBarButton(int buttonId, View parent) {
		
		if (parent != null) {
			Button button = (Button) parent.findViewById(buttonId);
			if (button != null) {

				Drawable img = null;

				switch (buttonId) {
				case R.id.btnCampagne:
					img = getResources().getDrawable(R.drawable.ic_campagne_active);
					break;
				case R.id.btnProgetti:
					img = getResources().getDrawable(R.drawable.ic_noprofit_active);
					break;
				case R.id.btnCitibiliter:
					img = getResources().getDrawable(R.drawable.ic_citybiliter_active);
					break;
				case R.id.btnProfilo:
					img = getResources().getDrawable(R.drawable.ic_profilo_active);
					break;
				}

				img.setBounds(0, 0, img.getIntrinsicHeight(), img.getIntrinsicWidth());
				button.setCompoundDrawables(null, img, null, null);
				button.setTextColor(getResources().getColor(R.color.bottomBarActiveText));
			}
		}
	}

	protected void hideBottomBarOnSearch(SearchView searchView, View viewToStech) {
		final View view = viewToStech;
		final int bottomPadding = viewToStech.getPaddingBottom();
		int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		
		if (searchTextId > 0) {
			TextView searchText = (EditText) searchView.findViewById(searchTextId);
			if (searchText != null) {
				
				searchText.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							hideBottomBar();
							view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), 0);
						} else {
							showBottomBar();
							view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomPadding);
						}

					}
				});
				searchText.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(isBottomBarVisible())
							hideBottomBar();						
					}
				});
				
			}
		}
	}
	
	public void hideBottomBar() {
		View borromBar = this.getBottomBar();
		if (borromBar != null){
			borromBar.setVisibility(View.GONE);
			
		}	
	}

	public void showBottomBar() {
		View borromBar = this.getBottomBar();
		if (borromBar != null){
			borromBar.setVisibility(View.VISIBLE);
			borromBar.invalidate();
		}
	}

	public boolean isBottomBarVisible() {
		View bb = getBottomBar();
		return  bb != null && bb.getVisibility() == View.VISIBLE;
	}
	
	public boolean isBottomBarPresent() {
		return getBottomBar() != null;
	}
	
	public View getBottomBar() {
		return findViewById(R.id.bottomBar);
	}
	
	
	
	/****************************************************
	 * Search management
	 ****************************************************/
	
	private void overrideSearchViewStyle(Menu menu) {
		MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
		if (searchViewMenuItem != null) {
			SearchView searchView = null;
			ImageView searchIcon = null;
			TextView searchText = null;

			searchView = (SearchView) searchViewMenuItem.getActionView();

			// cambio l'icona di search
			int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
			if (searchImgId > 0) {
				searchIcon = (ImageView) searchView.findViewById(searchImgId);
				if (searchIcon != null) {
					searchIcon.setImageResource(R.drawable.ic_action_search);
				}
			}

			// cambio il background dell'area di testo
			int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
			if (searchPlateId > 0) {
				View searchPlate = searchView.findViewById(searchPlateId);
				searchPlate.setBackgroundResource(R.drawable.search_field_background);
			}

			// cambio l'icona e il colore del testo dell'area di testo
			int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			if (searchTextId > 0) {
				searchText = (EditText) searchView.findViewById(searchTextId);
				if (searchText != null) {
					SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");
					Drawable icon = getResources().getDrawable(R.drawable.ic_action_search);
					int testSize = (int) (searchText.getTextSize() * 1.25);
					icon.setBounds(0, 0, testSize, testSize);
					stopHint.setSpan(new ImageSpan(icon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					stopHint.append(getString(R.string.search_hint));

					searchText.setHint(stopHint);
					searchText.setTextColor(getResources().getColor(R.color.searchFieldText));
					searchText.setHintTextColor(getResources().getColor(R.color.searchFieldHint));
				}
			}

			// close button: ImageView id/search_close_btn"
			int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
			ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonId);
			closeButtonImage.setImageResource(R.drawable.ic_action_cancel);

		}
	}


	/****************************************************
	 * Notification Badge management
	 ****************************************************/
	
	private void initNotificationBadge() {
		if(notificationMenuItem != null) {
			notificationMenuItem.setActionView(R.layout.action_notifiche_badge);
			
			// set the onclick lstener
			notificationMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onOptionsItemSelected(notificationMenuItem);
				}
			});
			
			// set a GCM notification callback 
			GCMClientManager gcmManagerIstance = GCMClientManager.getInstance(); 
			if(gcmManagerIstance == null) {
				gcmManagerIstance = ((CitybilityApplication)getApplication()).initGCM(this);
			}
			
			gcmManagerIstance.setNotificationHandler(new NotificationHandler(){

				@Override
				public void notify(Context context, int notificationId, Bundle bundle) {
					String title = bundle.containsKey("Title")?bundle.getString("Title"):"";
					String textShort = bundle.containsKey("TextShort")?bundle.getString("TextShort"):"";
					Bitmap thumbnail =  null;
					if(bundle.containsKey("Thumbnail")){
						try {
							thumbnail = HttpImageLoader.getBitmapFromURL(bundle.getString("Thumbnail"));
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					Notification notification = ((CitybilityApplication)getApplication()).buildNotification(context, title, textShort, thumbnail);
					
					// show the notification
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	        		
			        mNotificationManager.notify(notificationId, notification);
			        
			        updateNotificationBadge();
					
				}});
			
			// start the polling thread
			startNotificationBadgeUpdater();
		}	
	}

	private void startNotificationBadgeUpdater() {
		
		if (notificationMenuItem != null) {
			
			if(notificationBadgeUpdaterScheduler!=null && !notificationBadgeUpdaterScheduler.isTerminated())
				notificationBadgeUpdaterScheduler.shutdown();
			
			notificationBadgeUpdaterScheduler = Executors.newSingleThreadScheduledExecutor();

			notificationBadgeUpdaterScheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					updateNotificationBadge();
				}
			}, 0, Constant.NOTIFICATION_BADGE_UPDATING_INTERVAL, TimeUnit.MILLISECONDS);		
		}

	}	

	private void updateNotificationBadge() {
		final TextView badge = (TextView)  notificationMenuItem.getActionView().findViewById(R.id.menu_notifiche_textview);

		if (badge != null) {
			
			new CitybilityRequest(this, new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {

					if (result.has("UnreadMessages")) {
						try {
							int numNewNotification = result.getInt("UnreadMessages");
							if (numNewNotification > 0) {
								badge.setVisibility(View.VISIBLE);
								badge.setText(String.valueOf(numNewNotification));
							} else {
								badge.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							Log.e(this.getClass().getName(), "Errore un lettura userMessages");
							e.printStackTrace();
						}
					}

				}

				@Override
				public void onError(int errorCode, String message) {
					Log.e(this.getClass().getName(), "Errore un lettura userMessages");
				}
			}).userMessages(Constant.USER_MESSAGGES_STATUS_ALL);
			
	
		}
	}

	private void stopNotificationBadgeUpdater(){
		if(notificationBadgeUpdaterScheduler != null){
			notificationBadgeUpdaterScheduler.shutdownNow();
		}
	}
	

}
