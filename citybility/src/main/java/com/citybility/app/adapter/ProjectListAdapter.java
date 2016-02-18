package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;
import com.citybility.app.R;

public class ProjectListAdapter<T> extends CitybiliterBaseListAdapter<T> {

	private final Activity context;
	private List<T> items;

	public ProjectListAdapter(Activity context, int resource, List<T> objects) {
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
		      rowView = inflater.inflate(R.layout.fragment_project_list_item, parent, false);
		}
		*/

	    LayoutInflater inflater = this.context.getLayoutInflater();
	    View rowView = inflater.inflate(R.layout.fragment_project_list_item, parent, false);
		
		
		// Fill the row
		JSONObject project = (JSONObject) items.get(position);
		try {
			ViewUtils.setImageViewHttpImage(rowView, R.id.prjThumbnail, project.getString("Thumbnail"), false, false);
			ViewUtils.setImageViewImageResource(rowView, R.id.prjThumbnail, R.drawable.photo_default);
			ViewUtils.setImageViewContentDescription(rowView, R.id.prjThumbnail, project.getString("InitiativeName"));
			ViewUtils.setTextViewText(rowView, R.id.prjName, project.getString("InitiativeName"));
			ViewUtils.setTextViewText(rowView, R.id.activityName, project.getString("BeneficiaryName"));				
			ViewUtils.setTextViewText(rowView, R.id.prjDistanceInfo,  CityUtils.formatDistance(project.getDouble("Distance")));
			ViewUtils.setTextViewText(rowView, R.id.prjLikeInfo, project.getString("Citybiliters"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rowView;
		
	}	
}
