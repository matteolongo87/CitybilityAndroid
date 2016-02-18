package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citybility.app.R;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;

public class CampaignListAdapter<T> extends CitybiliterBaseListAdapter<T> {

	private final Activity context;
	private List<T> items;

	public CampaignListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.context = context;
		this.items = objects;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		/* NON riutilizzare le view (almeno fino ad quanso non si implementa il caching delle immagini)
		View rowView = convertView;
		
		if(rowView == null){
		      LayoutInflater inflater = this.context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.fragment_campaign_list_item, parent, false);
		}
		*/

        LayoutInflater inflater = this.context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_campaign_list_item, parent, false);
		
		// Fill the row
		JSONObject campaign = (JSONObject) items.get(position);
		try {
			ViewUtils.setImageViewHttpImage(rowView, R.id.cmpThumbnail, campaign.getString("Thumbnail"), false, false);
			ViewUtils.setImageViewContentDescription(rowView, R.id.cmpThumbnail, campaign.getString("CampaignName"));
			ViewUtils.setTextViewText(rowView, R.id.cmpName, campaign.getString("CampaignName"));
			ViewUtils.setTextViewText(rowView, R.id.activityName, campaign.getString("LocationName"));
			ViewUtils.setTextViewText(rowView, R.id.activityDescription, campaign.getString("LocationNote"));
			ViewUtils.setTextViewText(rowView, R.id.cmpDistanceInfo, CityUtils.formatDistance(campaign.getDouble("Distance")));
			ViewUtils.setTextViewText(rowView, R.id.cmpHeartInfo, campaign.getString("BeneficiaryName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rowView;
		
	}

	public List<T>  getItems() {
		return items;
	}
	
}
