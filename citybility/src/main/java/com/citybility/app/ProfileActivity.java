package com.citybility.app;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.citybility.app.custom.RoundedImageView;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.BitmapUtils;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.ImagePickUpUtil;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class ProfileActivity extends CitybilityFrameActivity implements ProfileFragment.OnProfileFragmentInteractionListener,  OnClickListener{
	
	private RoundedImageView mProfilePhoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.application_activity_main_layout);
		
		((CitybilityApplication)getApplication()).startLoading(this);
		
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState == null) {
			ProfileFragment profileFragment = new ProfileFragment();
			getFragmentManager().beginTransaction().add(R.id.container, profileFragment).commit();
		}


		addBottomBarListener();
		setActiveBottomBarButton(R.id.btnProfilo, findViewById(R.id.bottomBar));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		 
  		switch (requestCode) {
  		
  		case Constant.PICTURE_TAKEN_FROM_GALLERY:
  			if (resultCode == RESULT_OK) {	
  				try {
  					Bitmap tmpBitmap = BitmapUtils.decodeSampledBitmapFromGallery(data, Constant.SQUARE_THUMBNAIL_DIM, Constant.SQUARE_THUMBNAIL_DIM, getContentResolver());
  					changePhoto(tmpBitmap);
  				} catch (Exception e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  			}
  			break;

  		case Constant.PICTURE_TAKEN_FROM_CAMERA:
  			if (resultCode == RESULT_OK) {
  				if (data.hasExtra("data")) {
  					Bitmap tmpBitmap = BitmapUtils.decodeSampledBitmapFromCamera(data);
  					changePhoto(tmpBitmap);
  				}
  			}

  			break;
  		}

	}
	

	private void changePhoto(Bitmap photoBitmap) {

		if (photoBitmap != null) {

			((CitybilityApplication)getApplication()).startLoading(ProfileActivity.this);

			final Bitmap profileImage = BitmapUtils.createSquareThumbnail(photoBitmap, Constant.SQUARE_THUMBNAIL_DIM);
			new CitybilityRequest(this, new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					RoundedImageView profilePhoto = (RoundedImageView) findViewById(R.id.cbThumbnail);
					if (profilePhoto != null) {
						profilePhoto.setShape(RoundedImageView.CIRCULAR_SHAPE);
						profilePhoto.setImageBitmap(profileImage);
					}
					((CitybilityApplication)getApplication()).stopLoading();
				}

				@Override
				public void onError(int errorCode, String message) {
					((CitybilityApplication)getApplication()).stopLoading();
					CityUtils.showErrorAlert(ProfileActivity.this, getString(R.string.msg_error), message);
				}

			}).citybilitySave(BitmapUtils.encodeTobase64(profileImage));

			photoBitmap.recycle();

			((CitybilityApplication)getApplication()).stopLoading();
		} else {
			CityUtils.showErrorAlert(ProfileActivity.this, getString(R.string.msg_error), getString(R.string.taking_from_camera_error));
		}
	}
	@Override
	public void onClick(View v) {
		final int id = v.getId();
	    switch (id) {
	    
	    case R.id.cbFollowingItem:
			// TODO: Implementare lista Following
			break;
			
		case R.id.cbFollowerItem:
			// TODO: Implementare lista Follower
			break;
			
		case R.id.cbThumbnailModif:
		case R.id.cbThumbnail:
			ImagePickUpUtil.openMediaSelector(this);
			break;
			
	    case R.id.logoutAction:
	    	new CitybilityRequest(this, new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					// logout from facebook
					callFacebookLogout();
		
					// start welcome activity
					Intent welcome = new Intent(getApplicationContext(), WelcomeActivity.class);
					welcome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(welcome);
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(ProfileActivity.this, getString(R.string.msg_error), message);
				}

			}).logout();
	        break;
	    }
		
	}
	protected void callFacebookLogout() {
		 FacebookSdk.sdkInitialize(this.getApplicationContext()); 
		 LoginManager.getInstance().logOut(); 
	}

	/*******************************************************************
	 * OnProjectDetailFragmentInteractionListener Interface methods implementations
	 *******************************************************************/		
	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO gestire eventuale interazione tra fragment e activity	
	}

	@Override
	public void onFragmentViewReady() {

	   mProfilePhoto = (RoundedImageView) findViewById(R.id.cbThumbnail);
	   mProfilePhoto.setOnClickListener(this);
	   findViewById(R.id.cbThumbnailModif).setOnClickListener(this);
		   
	   findViewById(R.id.cbFollowingItem).setOnClickListener(this);
	   findViewById(R.id.cbFollowerItem).setOnClickListener(this);
	   findViewById(R.id.logoutAction).setOnClickListener(this);

	}

}
