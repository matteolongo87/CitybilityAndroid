package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citybility.app.R;
import com.citybility.app.util.ViewUtils;

public class NotificationListAdapter<T> extends CitybiliterBaseListAdapter<T> {

	private final Activity mContext;
	private List<T> mItems;
	private LayoutInflater mInflater;
	
	public NotificationListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.mContext = context;
		this.mItems = objects; 
		this.mInflater  = this.mContext.getLayoutInflater();
	}

	
	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		try {
			convertView = mInflater.inflate(R.layout.fragment_notification_list_item, parent, false);
			JSONObject notif = (JSONObject) mItems.get(position);
			boolean isRead = notif.getInt("Status")==1;
			// Fill the row
			int foreColor= Color.parseColor(isRead ? notif.getString("ForeColor") : notif.getString("UnreadForeColor"));
			int backColor= Color.parseColor(isRead ? notif.getString("BackColor") : notif.getString("UnreadBackColor"));
			
			convertView.setBackgroundColor(backColor);
			
			ViewUtils.setImageViewHttpImage(convertView, R.id.notificationThumb, notif.getString("Thumbnail"), false, false);
			setTextViewStyledText(convertView, R.id.notificationSubTitle, notif.getString("TextShort"), foreColor);
			setTextViewStyledText(convertView, R.id.notificationTitle, notif.getString("Title"), foreColor);
			
		} catch (JSONException e) {
			convertView = null;
			e.printStackTrace();
		}
		return convertView;
		
	}

	
	private void setTextViewStyledText(View parent, int viewId, CharSequence text, int foreColor){

		TextView textView = ((TextView) parent.findViewById(viewId));
		if (textView != null) {
			textView.setTextColor(foreColor);
			textView.setText(text);
		}
	}

	public void setViewAsRead(int position) throws JSONException {
		((JSONObject) mItems.get(position)).put("Status", 1); 
		this.notifyDataSetChanged();
	}

	public void setViewAsRead(View view, int position) throws JSONException {
		JSONObject notif = (JSONObject) mItems.get(position); 
		int foreColor = Color.parseColor(notif.getString("ForeColor"));
		int backColor= Color.parseColor(notif.getString("BackColor"));
		
		view.setBackgroundColor(backColor);
		setTextViewStyledText(view, R.id.notificationTitle, notif.getString("Title"), foreColor);
		setTextViewStyledText(view, R.id.notificationSubTitle,  notif.getString("TextShort"), foreColor);
		setViewAsRead(position);
	}



}
