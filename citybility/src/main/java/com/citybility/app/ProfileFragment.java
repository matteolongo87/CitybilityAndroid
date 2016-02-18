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
 * {@link ProfileFragment.OnProfileFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link ProfileFragment#newInstance}
 * factory method to create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {

	private OnProfileFragmentInteractionListener mListener;

	/**
	 * serve in caso di resume
	 */
	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((CitybilityApplication) getActivity().getApplication()).startLoading(getActivity());
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

		new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("Citybiliter")) {
					try {
						populateView(result.getJSONObject("Citybiliter"));
					} catch (JSONException e) {
						CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), getString(R.string.server_error_msg), true);
						e.printStackTrace();
					} finally {
						((CitybilityApplication) getActivity().getApplication()).stopLoading();
					}
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), message);
			}

		}).profile();

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListener.onFragmentViewReady();
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
			mListener = (OnProfileFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnProfileFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void populateView(JSONObject citybiliter) throws JSONException {
		Activity parent = getActivity();

		String fullName = String.format("%s %s", citybiliter.getString("FirstName"), citybiliter.getString("LastName"));

		ViewUtils.setImageViewHttpImage(parent, R.id.cbThumbnail, citybiliter.getString("Thumbnail"), false, false);
		ViewUtils.setImageViewContentDescription(parent, R.id.cbThumbnail, fullName);
		ViewUtils.setTextViewText(parent, R.id.cbName, fullName);

		ViewUtils.setTextViewText(parent, R.id.cdActive, getResources().getString(R.string.cb_active_from, CityUtils.parseJsonDateAsString(citybiliter.getLong("CreateDateUnix") * 1000, "dd/MM/yyyy")));

		ViewUtils.setTextViewText(parent, R.id.cbFollowerNum, citybiliter.getString("Followers"));
		ViewUtils.setTextViewText(parent, R.id.cbFollowingNum, citybiliter.getString("Followings"));
		ViewUtils.setTextViewText(parent, R.id.cbPurchasedNum, citybiliter.getString("Transactions"));
		ViewUtils.setTextViewText(parent, R.id.cbDonatedNum, citybiliter.getString("Amount"));
		ViewUtils.setTextViewText(parent, R.id.cbSupportedNum, citybiliter.getString("Supported"));
		ViewUtils.setTextViewText(parent, R.id.cbPositiveFeedbackNum, citybiliter.getString("Positives"));
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
	public interface OnProfileFragmentInteractionListener {
		public void onFragmentViewReady();

		public void onFragmentInteraction(Uri uri);
	}

}
