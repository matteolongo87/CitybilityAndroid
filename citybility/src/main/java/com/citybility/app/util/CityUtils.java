package com.citybility.app.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.citybility.services.CommunicationManagerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.citybility.app.ProjectTabsActivity;
import com.citybility.app.R;

public class CityUtils {
	
	public static final Class<? extends Activity> DEFAULT_ACTIVITY_CLASS = ProjectTabsActivity.class;


	public static ArrayList<JSONObject> convertJSONArayToArrayList(JSONArray array) throws JSONException{
		ArrayList<JSONObject> aList = new ArrayList<JSONObject>();
		for(int i=0; i< array.length(); i++){
			aList.add(array.getJSONObject(i));
		}
		return aList;
	}
	

	public static String parseJsonDateAsString(Long millis, String pattern) throws IllegalArgumentException {
	    return  new SimpleDateFormat(pattern).format(new Date(millis));
	}	
	
	
	public static double[] getGPSCoordinates(){
		Location location = CommunicationManagerService.getCurrentlocation();
		double[] gps = null;
		if (location != null) {
			gps = new double[2];
			gps[0] = location.getLatitude();
			gps[1] = location.getLongitude();
		}
		return gps;
	}
	
	public static String formatDistance(double distance){
		return distance<1 ? String.format("%.0f", distance*1000) : String.format("%.1fKm",distance);
	}


	public static int convertDp2Px(int dp, DisplayMetrics displayMetrics) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
	}


    public static void showInfoAlert(Context context, String title, String message) {
    	showAlert(context, 0, title, message, null);
    }
 	
    public static void showInfoAlert(Context context, String title, String message, DialogInterface.OnClickListener listener) {
    	showAlert(context, android.R.drawable.ic_dialog_info, title, message, listener);
    } 	

    public static void showWarningAlert(Context context, String title, String message) {
    	showAlert(context, 0, title, message, null);
    }

    public static void showWarningAlert(Context context, String title, String message, DialogInterface.OnClickListener listener) {
    	showAlert(context, android.R.drawable.ic_dialog_info, title, message, listener);
    } 	
    public static void showErrorAlert(Context context, String title, String message) {
    	showErrorAlert(context, title, message, false);
    }
    /**
     * 
     * @param context the context
     * @param title the message title
     * @param message the message text
     * @param finishIfActivity finish the activity IF AND ONLY IF it is true and contest is instance of Activity class  
     */
    public static void showErrorAlert(Context context, String title, String message, boolean finishIfActivity) {
    	DialogInterface.OnClickListener listener = null;
    	if(finishIfActivity){
    		if(context instanceof Activity){
    			final Activity act = (Activity)context; 
    			listener = new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						act.finish();
					}
    			};
    		}
    	}
    	showAlert(context, 0, title, message, listener);
    }
 	
    public static void showErrorAlert(Context context, String title, String message, DialogInterface.OnClickListener listener) {
    	showAlert(context, android.R.drawable.ic_dialog_alert, title, message, listener);
    } 	

	public static void showAlert(Context context, int iconId, String title, String message, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(R.string.ok, listener);
		if (iconId != 0)
			dialog.setIcon(iconId);
		dialog.show();
	}

	public static void showWebAlert(final Context context, final String title, String url, DialogInterface.OnClickListener listener) {

		final AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(title).setPositiveButton(R.string.accept, listener);
		
		WebView wv = new WebView(context);
		wv.loadUrl(url);
		
		wv.setWebChromeClient(new WebChromeClient() {
			ProgressDialog pd = null;
			    
			@Override
            public void onProgressChanged(WebView view, int progress)  {
				if(pd==null)
					showProgressDialog();

            	pd.setProgress(progress);
            	if(progress==100){
            		pd.dismiss();
            		dialog.show();
            	}
            }

			private void showProgressDialog() {
				pd = new ProgressDialog(context);
			    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    pd.show();
			}
        });

		dialog.setView(wv);
	}


}
