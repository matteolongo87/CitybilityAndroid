package com.citybility.app.adapter;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CitybiliterBaseListAdapter<T> extends ArrayAdapter<T> {

	private final Activity context;
	private List<T> items;

	public CitybiliterBaseListAdapter(Activity context, int resource, List<T> objects) {
		super(context, resource, objects);
		this.context = context;
		this.items = objects;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		View rowView = convertView;
		
		if(rowView == null){
		      LayoutInflater inflater = this.context.getLayoutInflater();
		      rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		// Fill the row
		JSONObject obj = (JSONObject) items.get(position);
		((TextView)rowView.findViewById(android.R.id.text1)).setText(obj.toString());
		
		return rowView;	
	}

	public void replaceList(List<T> newList){
		this.reset();
		this.items.clear();
		this.addToList(newList);
	}
	
	public void addToList(List<T> newList){
		this.items = newList;
		this.addAll(newList);
		this.notifyDataSetChanged();
	}
	public void reset() {
		this.clear();
	}

	public List<T> getItems() {
		return items;
	}
	
	
}
