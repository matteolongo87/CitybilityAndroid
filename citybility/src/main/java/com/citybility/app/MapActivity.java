package com.citybility.app;

import java.util.Locale;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.citybility.app.util.CityUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends CitybilityFrameActivity  implements OnMapReadyCallback {

	public static enum CoordinatesType {
		CAMPAIGN(0), PROJECT(1), CITYBILITER(2);

		private final int type;

		private CoordinatesType(int value) {
			this.type = value;
		}

		public int getType() {
			return type;
		}
	}

	public static final String ARG_COORDINATES_TYPE = "arg_coordinates_type";
	public static final String ARG_LAT = "arg_lat";
	public static final String ARG_LNG = "arg_lng";
	public static final String ARG_TITLE = "arg_title";
	public static final String ARG_SNIPPET = "arg_snippet";
	

	private int cooType;
	private LatLng mPosition;
	private String mTitle;
	private String mSnippet;
	private GoogleMap mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_activity_main_layout);
		
		MapFragment mapFragment = new MapFragment();
		
		getFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();

		mapFragment.getMapAsync(this);
		
		Intent thisIntent = getIntent();
		cooType = thisIntent.getIntExtra(ARG_COORDINATES_TYPE, -1);
		double lat = thisIntent.getDoubleExtra(ARG_LAT, -1);
		double lng = thisIntent.getDoubleExtra(ARG_LNG, -1); 
		if(lat > 0 && lng > 0) {
			mPosition= new LatLng(lat, lng);
			mTitle = thisIntent.getStringExtra(ARG_TITLE);
			mSnippet = thisIntent.getStringExtra(ARG_SNIPPET);
		} else {
			CityUtils.showErrorAlert(this, getString(R.string.error), getString(R.string.msg_no_valid_coordinates) , new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MapActivity.this.finish();
					
				}
			});
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

		addBottomBarListener();
		switch (cooType) {
		case 0:
			setActiveBottomBarButton(R.id.btnCampagne, findViewById(R.id.bottomBar));
			break;
		case 1:
			setActiveBottomBarButton(R.id.btnProgetti, findViewById(R.id.bottomBar));
			break;
		case 2:
			setActiveBottomBarButton(R.id.btnCitibiliter, findViewById(R.id.bottomBar));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.action_naviga:
			if (mMap != null) {
				double[] myLocation = CityUtils.getGPSCoordinates();
				if (myLocation != null) {
					// Start navigation 
					//Uri gmmIntentUri =Uri.parse(String.format("google.navigation:q=%f,%f",mPosition.latitude, mPosition.longitude));
					// or
					// start maps
					Uri gmmIntentUri = Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", myLocation[0], myLocation[1], mPosition.latitude, mPosition.longitude));

					Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
					mapIntent.setPackage("com.google.android.apps.maps");
					
					if (mapIntent.resolveActivity(getPackageManager()) != null) {
						startActivity(mapIntent);
						finish();
					} else {
						CityUtils.showErrorAlert(this, getString(R.string.msg_warning), getString(R.string.msg_no_navigation_apps));
					}
				} else {
					CityUtils.showErrorAlert(this, getString(R.string.msg_warning), getString(R.string.msg_no_gps_signal));
				}
			}
			break;

		default:
			break;
		}
		;

		return super.onOptionsItemSelected(item);
	}

	/*********************************************************
	 * OnMapReadyCallback IMPLEMENTATION
	 *********************************************************/
	@Override
	public void onMapReady(GoogleMap map) {
		
		if(mPosition!=null){

	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition, 13));

	        map.addMarker(new MarkerOptions()
	                .title(mTitle)
	                .snippet(mSnippet)
	                .position(mPosition)
	                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_marker)));
	        
	        mMap = map;
	    }
	}
}
