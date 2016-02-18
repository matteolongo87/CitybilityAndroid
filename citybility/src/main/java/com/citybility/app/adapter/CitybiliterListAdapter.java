package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citybility.app.R;
import com.citybility.app.util.ViewUtils;

public class CitybiliterListAdapter<T> extends CitybiliterBaseListAdapter<T> {

	private final Activity context;
	private List<T> items;
	private ListType listType = ListType.NORMAL;

	public enum ListType {
		NORMAL, SEARCH
	}

	public CitybiliterListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.context = context;
		this.items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*
		 * NON riutilizzare le view (almeno fino ad quando non si implementa il
		 * caching delle immagini) View rowView = convertView;
		 * 
		 * if(rowView == null){ LayoutInflater inflater =
		 * this.context.getLayoutInflater(); rowView =
		 * inflater.inflate(R.layout.fragment_citybiliter_list_item, parent,
		 * false); }
		 */

		LayoutInflater inflater = this.context.getLayoutInflater();
		View rowView = null;
		JSONObject citybiliter;
		

		switch(this.listType) {
		
		case NORMAL:
			rowView = inflater.inflate(R.layout.fragment_citybiliter_list_item, parent, false);
			citybiliter = (JSONObject) items.get(position);
			
			// Fill the view
			try {
				String fullName = String.format("%s %s", citybiliter.getString("FirstName"), citybiliter.getString("LastName"));

				ViewUtils.setImageViewHttpImage(rowView, R.id.cbThumbnail, citybiliter.getString("Thumbnail"), false, false);
				ViewUtils.setImageViewContentDescription(rowView, R.id.prjThumbnail, fullName);
				ViewUtils.setTextViewText(rowView, R.id.cbName, fullName);
				ViewUtils.setTextViewText(rowView, R.id.cbFeedInfo, citybiliter.getString("Feedbacks") + "%");
				ViewUtils.setTextViewText(rowView, R.id.cbLikeInfo, citybiliter.getString("Positives"));
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case SEARCH:
			rowView = inflater.inflate(R.layout.fragment_citybiliter_list_from_search_item, parent, false);
			citybiliter = (JSONObject) items.get(position);
			
			// Fill the view
			try {
				ViewUtils.setImageViewHttpImage(rowView, R.id.cbThumbnail, citybiliter.getString("Thumbnail"), false, false);
				ViewUtils.setImageViewContentDescription(rowView, R.id.prjThumbnail, citybiliter.getString("DisplayName"));
				ViewUtils.setTextViewText(rowView, R.id.cbName, citybiliter.getString("DisplayName"));
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
			
		}
		
		return rowView;

	}
	
	public void replaceList(List<T> newList, ListType lt ){
		this.listType = lt;
		Log.e(this.getClass().getName(), "@@@@@@@@@@@@@@@@@@ replaceList - TIPO LISTA: "+this.listType);
		super.replaceList(newList);
	}
	
	public void addToList(List<T> newList, ListType lt ){
		this.listType = lt;
		super.addToList(newList);
	}
	
	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

}
