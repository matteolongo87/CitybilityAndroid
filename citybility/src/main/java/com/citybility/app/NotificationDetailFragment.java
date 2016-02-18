package com.citybility.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link NotificationDetailFragment.OnNotificationDetailFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link NotificationDetailFragment#newInstance}
 * factory method to create an instance of this fragment.
 *
 */
public class NotificationDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	private JSONObject notif;

	private OnNotificationDetailFragmentInteractionListener mListener;
	private long mNotifId;

	/**
	 * serve in caso di resume
	 */
	public NotificationDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			this.mNotifId = getArguments().getLong(ARG_ITEM_ID); 
		}
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_notification_detail, container, false);		

		if(this.mNotifId > 0){
		
			new CitybilityRequest(getActivity(), new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					if(result.has("UserMessage")){
						try {
							notif =result.getJSONObject("UserMessage");
							populateView();
							
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
					
					((CitybilityApplication)getActivity().getApplication()).stopLoading();
				}

			}).userMessage(mNotifId);
			
			
		}
		return rootView;
	}

	private void populateView() throws JSONException {
		Activity parent = getActivity();
		ViewUtils.setTextViewText(parent, R.id.ntd_title, this.notif.getString("Title"));
		ViewUtils.setTextViewText(parent, R.id.ntd_subTitle, this.notif.getString("TextShort"));
	}

	@Override
	public void onViewCreated (View view, Bundle savedInstanceState){
		try {
			if(this.mNotifId >0 && this.notif != null && this.notif.getInt("Status")==1){
				new CitybilityRequest(getActivity(), new OnRequestResultListener() {

					@Override
					public void onSuccess(JSONObject result) {
					}

					@Override
					public void onError(int errorCode, String message) {
					}

				}).userMessageRead(mNotifId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
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
			mListener = (OnNotificationDetailFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnNotificationDetailFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
	public interface OnNotificationDetailFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
	}

}
