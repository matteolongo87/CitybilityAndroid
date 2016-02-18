package com.citybility.app;

import net.citybility.connect.CitybilityConnectApplication;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.citybility.app.custom.loadingDialogFragment;
import com.citybility.app.util.Constant;
import com.professioneit.gcmclientsdk.GCMClientManager;
import com.professioneit.gcmclientsdk.NotificationHandler;
import com.professioneit.gcmclientsdk.RegistrationToBackEndTask;

public class CitybilityApplication extends CitybilityConnectApplication {

	loadingDialogFragment mLoadingDialog;


	public void startLoading(Activity activity) {
		stopLoading();
		
		FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mLoadingDialog = new loadingDialogFragment();
        mLoadingDialog.show(transaction, "txn_tag");
		
	}

	public void stopLoading() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}
	}


	public GCMClientManager initGCM(Activity act){
		return initGCM(act, null);
	}
	
	public GCMClientManager initGCM(Activity act, NotificationHandler notifHandler){
		// Register to the notification manager and to the back-end server
		GCMClientManager gcmManagerIstance = GCMClientManager.getInstance(); 
		if(gcmManagerIstance == null)
			gcmManagerIstance = GCMClientManager.newInstance(this.getApplicationContext());
		
		gcmManagerIstance.register(act,Constant.SENDER_ID, new RegistrationTask(), false);
		
		if(notifHandler != null)
			gcmManagerIstance.setNotificationHandler(notifHandler);	
		
		return gcmManagerIstance;
	}

	private class RegistrationTask extends RegistrationToBackEndTask {

		@Override
		protected HttpResponse doInBackground(String... params) {
			String regId=params[0];
			// TODO: register to back-end 
			Log.e("RegistrationTask", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Log.e("RegistrationTask", "@@@@@@@@@@@@@@@@ REGISTRATION ID: "+regId);
			Log.e("RegistrationTask", "@@@@@@@@@@@@@@@@ TODO: inviare al server citybility questa registrazione");
			Log.e("RegistrationTask", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			return null;
		}

	}

	/**
	 *  build the notification
	 * @param context
	 * @param title
	 * @param textShort
	 * @param thumbnail 
	 * @return
	 */
	public Notification buildNotification(Context context, String title, String textShort, Bitmap thumbnail) {

		GCMClientManager gcmcm = GCMClientManager.getInstance();
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent (context, gcmcm.getNotificationTargetActivityClass()), Intent.FLAG_ACTIVITY_NEW_TASK);
		
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(gcmcm.getNotificationIcon())
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(textShort))
        .setContentText(textShort)
        .setContentIntent(contentIntent);
        
        if(thumbnail != null){
        	mBuilder.setLargeIcon(thumbnail);
        }
        
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        if(gcmcm.getVibratesOnNotification()){
        	notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        
        if(gcmcm.getPlaysOnNotification()){
	        if(gcmcm.getNotificationSound() == null)
	        	notification.defaults |= Notification.DEFAULT_SOUND;
	        else 
	        	notification.sound = gcmcm.getNotificationSound();
        }
        
		return notification;		
			        
	}
	
}
