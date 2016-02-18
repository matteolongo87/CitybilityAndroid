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
import com.citybility.app.util.ViewUtils;

public class CategoryListAdapter<T> extends ArrayAdapter<T> {

	private Activity context;
	public List<T> items;

	public CategoryListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.context = context;
		this.items = objects;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		View rowView = convertView;
		
		if(rowView == null){
		      LayoutInflater inflater = this.context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.fragment_category_list_item, null);
		}
		
		try {
			// Fill the row
			JSONObject category = (JSONObject)items.get(position);
			ViewUtils.setTextViewText(rowView, R.id.catName, category.getString("Description"));
			ViewUtils.setTextViewText(rowView, R.id.catCount, category.getString("Occurrences"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rowView;
		
	}

}
