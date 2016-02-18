package com.citybility.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.HttpImageLoader;
import com.citybility.app.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link CampaignDetailFragment.OnCampaignDetailFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link CampaignDetailFragment#newInstance}
 * factory method to create an instance of this fragment.
 *
 */
public class CampaignDetailFragment extends Fragment implements OnClickListener{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public String mCampaignName = "";
	public long mLocationId = 0;

	private OnCampaignDetailFragmentInteractionListener mListener;

	private JSONObject mCampaign;
	/**
	 * serve in caso di resume
	 */
	public CampaignDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			
			mLocationId = getArguments().getLong(ARG_ITEM_ID);
			
			
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					if(result.has("CampaignLocation")){
						try {
							mCampaign = result.getJSONObject("CampaignLocation");
							populateView();
						} catch (Exception e) {
							CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), getString(R.string.server_error_msg), true);
							e.printStackTrace();
							((CitybilityApplication)getActivity().getApplication()).stopLoading();
						}
					}
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), message, true);
				}

			}).campaignLocation(mLocationId, CityUtils.getGPSCoordinates());
		}
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_campaign_detail, container, false);
		return rootView;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCampaignDetailFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCampaignDetailFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

	private void populateView() throws JSONException{
		final Activity parent = getActivity();
			mCampaignName = mCampaign.getString("CampaignName");
		    
			parent.getActionBar().setTitle(mCampaignName);

			checkFollowing();

			JSONArray photoUrlArray = null;
			try {
				photoUrlArray= mCampaign.getJSONArray("Thumbnail");
			} catch (JSONException jse) {}
			String photoUrl = photoUrlArray != null ?photoUrlArray.getString(0) : mCampaign.getString("Thumbnail");
			ViewUtils.setImageViewHttpImage(parent, R.id.cs_image, photoUrl, false, false);

			ViewUtils.setSpannedTextViewText(parent, R.id.cs_description, mCampaign.getString("CampaignDescription"));

			// beneficiary
			ViewUtils.setTextViewText(parent, R.id.cs_beneficiaryName, getResources().getString(R.string.cs_support, mCampaign.getString("BeneficiaryName")));
			ViewUtils.setTextViewText(parent, R.id.cs_initiativeName, mCampaign.getString("InitiativeName"));
			SpannableStringBuilder sb = new SpannableStringBuilder(mCampaign.getString("InitiativeDescription")).append(" [...]");
			ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.citybility_green)); 
			sb.setSpan(fcs, sb.length()-5, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			ViewUtils.setTextViewText(parent, R.id.cs_initiativeDescription, sb);
			ViewUtils.setTextViewText(parent, R.id.cs_locLine1, mCampaign.getString("LocationName"));
			ViewUtils.setTextViewText(parent, R.id.cs_locLine2, mCampaign.getString("LocationAddress"));
			ViewUtils.setTextViewText(parent, R.id.cs_locLine3, getResources().getString(R.string.distance_from_here, CityUtils.formatDistance(mCampaign.getDouble("Distance"))));


			String dateRange = ""; 
			try{
			dateRange = getResources().getString(R.string.date_from_to, CityUtils.parseJsonDateAsString(mCampaign.getLong("FromUnix")*1000, "dd/MM/yyyy"), CityUtils.parseJsonDateAsString(mCampaign.getLong("ToUnix")*1000, "dd/MM/yyyy"));
			} catch (Exception e) {
				dateRange = getResources().getString(R.string.error);
			}
			
			ViewUtils.setTextViewText(parent, R.id.cs_dtLine1, dateRange);
			
			ViewUtils.setTextViewText(parent, R.id.cs_fbLine1, getResources().getString(R.string.citibiliters_num, mCampaign.getInt("Citybiliters")));

			ViewUtils.setTextViewText(parent, R.id.cs_dnLine1, getResources().getString(R.string.donated_amount, mCampaign.getInt("Amount")));


			parent.findViewById( R.id.cs_shareAction).setOnClickListener(this);
			parent.findViewById( R.id.cs_followAction).setOnClickListener(this);
			parent.findViewById( R.id.cs_unfollowAction).setOnClickListener(this);
			parent.findViewById( R.id.cs_locItem).setOnClickListener(this);
			parent.findViewById( R.id.cs_npItem).setOnClickListener(this);
			
			// insert photos in gallery
			JSONArray photos = mCampaign.getJSONArray("Gallery");
			if(photos.length() > 0){

				getActivity().findViewById(R.id.cs_galleryItem).setVisibility(View.VISIBLE);
				
				ImageView imageView = null;
				int height = CityUtils.convertDp2Px(90, getResources().getDisplayMetrics());
				int width = LayoutParams.MATCH_PARENT;
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
				lp.setMargins(0, 0, 5, 0);
				
				String[] photoUrls = new String[photos.length()];
				for (int i = 0; i < photos.length(); i++) {
					photoUrls[i] = photos.getString(i);
				}

				LinearLayout photoGallery = (LinearLayout) getActivity().findViewById(R.id.cs_photoGallery);
				final String[] imageUrls = photoUrls;
				for (int i = 0; i < photos.length(); i++) {
					imageView = new ImageView(getActivity());
					imageView.setLayoutParams(lp);
					imageView.setAdjustViewBounds(true);

					final Integer position = new Integer(i);
					imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent imageSliderIntent = new Intent(getActivity(), ImageSliderActivity.class);
							imageSliderIntent.putExtra(ImageSliderFragment.ARG_IMAGE_START_POSITION, position.intValue());
							imageSliderIntent.putExtra(ImageSliderFragment.ARG_URL_ARRAY, imageUrls);
							startActivity(imageSliderIntent);
						}
					});
					photoGallery.addView(imageView);
					new HttpImageLoader(getActivity(), photos.getString(i), imageView, false, true).load();
				}
			} else {
				getActivity().findViewById(R.id.cs_galleryItem).setVisibility(View.GONE);
			}

		
	}

	private void checkFollowing() {

		new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				try {
					
					if(result.has("Following") && result.getInt("Following")>0) 
						setUnfollow();
					
				} catch (JSONException e) {
					e.printStackTrace();
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), getString(R.string.server_error_msg), true);
				} finally {
					((CitybilityApplication)getActivity().getApplication()).stopLoading();
					
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), message, true);
				((CitybilityApplication)getActivity().getApplication()).stopLoading();
			}

		}).campaignLocationCheckFollowing(mLocationId);
		
	}
	
	private void setUnfollow(){
		getActivity().findViewById(R.id.cs_followAction).setVisibility(View.GONE);
		getActivity().findViewById(R.id.cs_unfollowAction).setVisibility(View.VISIBLE);
	}
	
	private void setFollow(){
		getActivity().findViewById(R.id.cs_unfollowAction).setVisibility(View.GONE);
		getActivity().findViewById(R.id.cs_followAction).setVisibility(View.VISIBLE);
	}
	
	

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.cs_followAction:

			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.cs_follow_success_msg, mCampaignName));
					setUnfollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).campaignLocationFollow(mLocationId);
			
			break;
		case R.id.cs_unfollowAction:

			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.cs_unfollow_success_msg, mCampaignName));
					setFollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).campaignLocationUnfollow( mLocationId);
			
			break;
		case R.id.cs_shareAction:
			mListener.onShare();
			break;
		case R.id.cs_locItem:
			
			try {
				JSONArray locationGPS = mCampaign.getJSONArray("GPS");
			
				Intent mapIntent = new Intent(getActivity(), MapActivity.class);
				mapIntent.putExtra(MapActivity.ARG_COORDINATES_TYPE, MapActivity.CoordinatesType.CAMPAIGN.getType());
				mapIntent.putExtra(MapActivity.ARG_LAT, locationGPS.getDouble(0));
				mapIntent.putExtra(MapActivity.ARG_LNG, locationGPS.getDouble(1));
				mapIntent.putExtra(MapActivity.ARG_TITLE, mCampaign.getString("LocationName"));
				mapIntent.putExtra(MapActivity.ARG_SNIPPET, mCampaign.getString("LocationAddress"));
				
				startActivity(mapIntent);
			} catch (JSONException jsonE) {
				Log.e(getTag(), "Errore nel parsing JSON: "+jsonE.getMessage());
				jsonE.printStackTrace();
			}
			break;
		case R.id.cs_npItem:
			try {
				Intent beneficiaryIntent = new Intent(getActivity(), ProjectDetailActivity.class);
				beneficiaryIntent.putExtra(ProjectDetailFragment.ARG_ITEM_ID, mCampaign.getLong("InitiativeId"));
				startActivity(beneficiaryIntent);
			} catch (JSONException e) {
				CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) ,  getString(R.string.generic_error_msg));
				e.printStackTrace();
			}
			break;
		}
		
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnCampaignDetailFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
		public void onGalleryClick(String[] imagesUrl, int position);
		public void onShare();
	}

	
}
