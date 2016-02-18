package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.citybility.app.R;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;

public class SupporterListAdapter<T> extends ArrayAdapter<T> {

	private final Activity context;
	private List<T> items;

	public SupporterListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.context = context;
		this.items = objects;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		View rowView = convertView;
		
		if(rowView == null){
		      LayoutInflater inflater = this.context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.fragment_supporter_list_item, null);
		}
		
		// Fill the row
		JSONObject campaign = (JSONObject) items.get(position);
		try {
			ViewUtils.setImageViewHttpImage(rowView, R.id.cmpThumbnail, campaign.getString("Thumbnail"), false, false);
			ViewUtils.setImageViewContentDescription(rowView, R.id.cmpThumbnail, campaign.getString("CampaignName"));
			ViewUtils.setTextViewText(rowView, R.id.cmpName, campaign.getString("CampaignName"));
			ViewUtils.setTextViewText(rowView, R.id.locName, campaign.getString("LocationName"));
			ViewUtils.setTextViewText(rowView, R.id.locDescription, campaign.getString("LocationDescription"));
			ViewUtils.setTextViewText(rowView, R.id.suppDistanceInfo, CityUtils.formatDistance(campaign.getDouble("Distance")));
			ViewUtils.setTextViewText(rowView, R.id.suppCitybilitersInfo, "n.p.");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rowView;
		
	}
	
	public void updateList(List<T> newList){
		this.clear();
		for(T obj:newList)
			this.add(obj);
		this.items = newList;
		this.notifyDataSetChanged();
	}
	
}
