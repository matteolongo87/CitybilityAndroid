package com.citybility.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.citybility.app.adapter.SupporterListAdapter;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.HttpImageLoader.FITMODE;
import com.citybility.app.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link ProjectDetailFragment.OnProjectDetailFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link ProjectDetailFragment#newInstance}
 * factory method to create an instance of this fragment.
 *
 */
public class ProjectDetailFragment extends Fragment implements OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public String mInitiativeName = "";
	public long mInitiativeId = 0;

	private JSONObject mInitiative;
	private OnProjectDetailFragmentInteractionListener mListener;
	
	private ArrayList<JSONObject> supporterList;

	/**
	 * serve in caso di resume
	 */
	public ProjectDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mInitiativeId = getArguments().getLong(ARG_ITEM_ID);
        
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					if(result.has("Initiative")){
						try {
							mInitiative = result.getJSONObject("Initiative");
							populateView(mInitiative);
						} catch (JSONException e) {
							CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), getString(R.string.server_error_msg), true);
							e.printStackTrace();
						} finally {
							((CitybilityApplication)getActivity().getApplication()).stopLoading();
						}
					}
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), message, true);
				}

			}).initiative(mInitiativeId, CityUtils.getGPSCoordinates());
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);		
		return rootView;
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProjectDetailFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnProjectDetailFragmentInteractionListener");
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
    
 
	
	private void populateView(final JSONObject project) throws JSONException {
		final Activity parent = getActivity();
			
		checkFollowing();
		
		parent.getActionBar().setTitle(project.getString("InitiativeName"));

		JSONArray photoUrlArray = null;
		try {
			photoUrlArray= project.getJSONArray("Photo");
		} catch (JSONException jse) {}
		String photoUrl = photoUrlArray != null ?photoUrlArray.getString(0) : project.getString("Photo");
		ViewUtils.setImageViewHttpImage(parent, R.id.prjImage, photoUrl, false, false, FITMODE.FITX);

		ViewUtils.setTextViewText(parent, R.id.prjName, project.getString("InitiativeDescription"));
		ViewUtils.setTextViewText(parent, R.id.prjDescription, project.getString("InitiativeNote"));

		ViewUtils.setTextViewText(parent, R.id.locLine1, project.getString("BeneficiaryName"));
		ViewUtils.setTextViewText(parent, R.id.locLine2, project.getString("BeneficiaryAddress"));
		
		int progress = project.getInt("MoneyPercentage");
		((ProgressBar) parent.findViewById(R.id.progressBar)).setProgress(progress);
		ViewUtils.setTextViewText(parent, R.id.progressRate,String.valueOf(progress)+"%");
		
		ViewUtils.setTextViewText(parent, R.id.srLine1, getResources().getString(R.string.donated_and_total, project.getString("Amount"), project.getString("Target")));
	
		String dateRange = ""; 
		try{
		dateRange = getResources().getString(R.string.date_from_to, CityUtils.parseJsonDateAsString(project.getLong("FromUnix")*1000, "dd/MM/yyyy"), CityUtils.parseJsonDateAsString(project.getLong("ToUnix")*1000, "dd/MM/yyyy"));
		} catch (Exception e) {
			dateRange = getResources().getString(R.string.error);
		}
		
		ViewUtils.setTextViewText(parent, R.id.dtLine1, dateRange);

		ViewUtils.setTextViewText(parent, R.id.ctLine1, getResources().getString(R.string.citibiliters_num, project.getInt("Citybiliters")));

		parent.findViewById( R.id.shareAction).setOnClickListener(this);
		parent.findViewById( R.id.followAction).setOnClickListener(this);
		parent.findViewById( R.id.unfollowAction).setOnClickListener(this);
		parent.findViewById( R.id.locItem).setOnClickListener(this);
		
		
		
		JSONArray supporters = project.getJSONArray("Supporters");
		
		if(supporters.length() == 0) {
			getActivity().findViewById(R.id.prjSupportersItem).setVisibility(View.GONE);
		} else{
			supporterList = CityUtils.convertJSONArayToArrayList(supporters);
			SupporterListAdapter<JSONObject> supporterstListAdapter = new SupporterListAdapter<JSONObject>(getActivity(), R.layout.fragment_project_list_item, supporterList);
			ListView suppListView = (ListView)getActivity().findViewById(R.id.prjSupporterList);
			suppListView.setAdapter(supporterstListAdapter);
			ViewUtils.setListViewHeightBasedOnChildren(suppListView);
			
			suppListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
					try {
						JSONObject supporter = supporterList.get(position);
						Intent supporterIntent = new Intent(getActivity(), CampaignDetailActivity.class);
						supporterIntent.putExtra(CampaignDetailFragment.ARG_ITEM_ID, supporter.getLong("LocationId"));
						startActivity(supporterIntent);
					} catch (Exception e) {
						CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) ,  getString(R.string.generic_error_msg));
						e.printStackTrace();
					}
				}

			});
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
		}).initiativeCheckFollowing(mInitiativeId);
		
	}


	private void setUnfollow(){
		getActivity().findViewById(R.id.followAction).setVisibility(View.GONE);
		getActivity().findViewById(R.id.unfollowAction).setVisibility(View.VISIBLE);
	}
	
	private void setFollow(){
		getActivity().findViewById(R.id.unfollowAction).setVisibility(View.GONE);
		getActivity().findViewById(R.id.followAction).setVisibility(View.VISIBLE);
	}
	

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.followAction:
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.prj_follow_success_msg, mInitiativeName));
					setUnfollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).initiativeFollow(mInitiativeId);
			
			break;
		case R.id.unfollowAction:
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.prj_unfollow_success_msg, mInitiativeName));
					setFollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).initiativeUnfollow(mInitiativeId);
			
			break;
		case R.id.shareAction:
			mListener.onShare();
			break;
		case R.id.locItem:
			try {
				JSONArray locationGPS = mInitiative.getJSONArray("GPS");
			
				Intent mapIntent = new Intent(getActivity(), MapActivity.class);
				mapIntent.putExtra(MapActivity.ARG_COORDINATES_TYPE, MapActivity.CoordinatesType.PROJECT.getType());
				mapIntent.putExtra(MapActivity.ARG_LAT, locationGPS.getDouble(0));
				mapIntent.putExtra(MapActivity.ARG_LNG, locationGPS.getDouble(1));
				mapIntent.putExtra(MapActivity.ARG_TITLE, mInitiative.getString("BeneficiaryName"));
				mapIntent.putExtra(MapActivity.ARG_SNIPPET, mInitiative.getString("BeneficiaryAddress"));
				startActivity(mapIntent);
			} catch (JSONException jsonE) {
				Log.e(getTag(), "Errore nel parsing JSON: "+jsonE.getMessage());
				jsonE.printStackTrace();
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
	public interface OnProjectDetailFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
		public void onShare();
	}

}
