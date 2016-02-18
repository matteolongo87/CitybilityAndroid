package com.citybility.app.provider;

import net.citybility.connect.APIWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchContentProvider extends ContentProvider {
	
	private String[] SEARCH_SUGGEST_COLUMNS = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA };

	private static final String CAMPAIGN_BASE_PATH = "campaigns";
	private static final String PROJECT_BASE_PATH = "projects";
	private static final String CITYBILITERS_BASE_PATH = "citybiliters";
	
	
	
	public SearchContentProvider() {
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String query = uri.getLastPathSegment().toLowerCase();
		

		MatrixCursor mc=null;
		JSONObject result = new JSONObject();
		
		if(query!= null && query.length()>2 && !query.equalsIgnoreCase(SearchManager.SUGGEST_URI_PATH_QUERY)){
			try {

				if(this.getType(uri).equals(CAMPAIGN_BASE_PATH)){
					result = APIWrapper.getInstance().campaignLocationSearch(query);
				} else if(this.getType(uri).equals(PROJECT_BASE_PATH)){
					result = APIWrapper.getInstance().initiativeSearch(query);
				} else if(this.getType(uri).equals(CITYBILITERS_BASE_PATH)){
					result = APIWrapper.getInstance().citybiliterSearch(query);
				}
			
				if(result.has("Results")){
					JSONObject resultItem = null; 
					JSONArray searchResults = result.getJSONArray("Results");
					mc = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 10);
					
					for (int i=0 ; i<searchResults.length(); i++) {
				    	  resultItem = searchResults.getJSONObject(i);
							try {
								if(this.getType(uri).equals(CAMPAIGN_BASE_PATH)){
									mc.addRow(new Object[] {(long)(Math.random()*100000), resultItem.getString("Tag"), resultItem.getString("Tag")});
								} else if(this.getType(uri).equals(PROJECT_BASE_PATH)){
									mc.addRow(new Object[] {(long)(Math.random()*100000), resultItem.getString("Tag"), resultItem.getString("Tag")});
								} else if(this.getType(uri).equals(CITYBILITERS_BASE_PATH)){
									mc.addRow(new Object[] {(long)(Math.random()*100000), resultItem.getString("DisplayName"), resultItem.getString("DisplayName")});
								}	
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return mc;
	}

	@Override
	public String getType(Uri uri) {
		return uri.getPathSegments().get(0);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
