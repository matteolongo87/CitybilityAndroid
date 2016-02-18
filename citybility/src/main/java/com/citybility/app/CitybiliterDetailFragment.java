package com.citybility.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link CitybiliterDetailFragment.OnCitybiliterDetailFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link CitybiliterDetailFragment#newInstance}
 * factory method to create an instance of this fragment.
 *
 */
public class CitybiliterDetailFragment extends Fragment implements OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public String mCitybiliterName = "";
	public long mCitybiliterID = 0;

	private OnCitybiliterDetailFragmentInteractionListener mListener;

	/**
	 * serve in caso di resume
	 */
	public CitybiliterDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCitybiliterID = getArguments().getLong(ARG_ITEM_ID);
				
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					if(result.has("Citybiliter")){
						try {
							populateView(result.getJSONObject("Citybiliter"));
							
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

			}).citybiliter(mCitybiliterID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_citybiliter_detail, container, false);		
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
			mListener = (OnCitybiliterDetailFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCitybiliterDetailFragmentInteractionListener");
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
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

	private void populateView(JSONObject citybiliter) throws JSONException {
		Activity parent = getActivity();

		checkFollowing();
		
	    mCitybiliterName = String.format("%s %s", citybiliter.getString("FirstName"), citybiliter.getString("LastName"));
		
		ViewUtils.setImageViewHttpImage(parent, R.id.cbThumbnail, citybiliter.getString("Thumbnail"), false, false);
		ViewUtils.setImageViewContentDescription(parent, R.id.cbThumbnail, mCitybiliterName);
		ViewUtils.setTextViewText(parent, R.id.cbName, mCitybiliterName);
		
		
		ViewUtils.setTextViewText(parent, R.id.cdActive,getResources().getString(R.string.cb_active_from, CityUtils.parseJsonDateAsString(citybiliter.getLong("CreateDateUnix")*1000, "dd/MM/yyyy")));
		
		ViewUtils.setTextViewText(parent, R.id.cbFollowerNum, citybiliter.getString("Followers"));
		ViewUtils.setTextViewText(parent, R.id.cbFollowingNum, citybiliter.getString("Followings"));
		ViewUtils.setTextViewText(parent, R.id.cbPurchasedNum, citybiliter.getString("Transactions"));
		ViewUtils.setTextViewText(parent, R.id.cbDonatedNum, citybiliter.getString("Amount"));
		ViewUtils.setTextViewText(parent, R.id.cbSupportedNum, citybiliter.getString("Supported"));
		ViewUtils.setTextViewText(parent, R.id.cbPositiveFeedbackNum, citybiliter.getString("Positives"));
		

		parent.findViewById( R.id.cbFollowingItem).setOnClickListener(this);
		parent.findViewById( R.id.cbFollowerItem).setOnClickListener(this);
		parent.findViewById( R.id.followAction).setOnClickListener(this);
		parent.findViewById( R.id.unfollowAction).setOnClickListener(this);
		parent.findViewById( R.id.shareAction).setOnClickListener(this);

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

		}).citybiliterCheckFollowing(mCitybiliterID);
		
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
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.cb_follow_success_msg, mCitybiliterName));
					setUnfollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).citybiliterFollow(mCitybiliterID);
			
			break;
		case R.id.unfollowAction:

			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					CityUtils.showInfoAlert(getActivity(), getString(R.string.msg_success) , getString(R.string.cb_unfollow_success_msg, mCitybiliterName));
					setFollow();
				}

				@Override
				public void onError(int errorCode, String message) {
					CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
				}

			}).citybiliterFollow(mCitybiliterID);
			
			break;
		case R.id.shareAction:
			mListener.onShare();
			break;
		case R.id.cbFollowingItem:
			Toast.makeText(getActivity(), "NON IMPLEMENTATO", Toast.LENGTH_LONG).show();
			break;
		case R.id.cbFollowerItem:
			Toast.makeText(getActivity(), "NON IMPLEMENTATO", Toast.LENGTH_LONG).show();
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
	public interface OnCitybiliterDetailFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
		public void onShare();
	}

}
