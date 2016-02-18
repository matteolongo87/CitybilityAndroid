package com.citybility.app;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.citybility.app.adapter.NotificationListAdapter;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link NotificationListFragment.OnCitybiliterListFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link NotificationListFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class NotificationListFragment extends Fragment implements OnItemClickListener {

	private OnNotificationListFragmentInteractionListener mListener;
	private NotificationListAdapter<JSONObject> mNotificationListAdapter;

	private View rootView;


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public NotificationListFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_notification_list, container, false);
		mNotificationListAdapter = new NotificationListAdapter<JSONObject>(getActivity(), R.layout.fragment_notification_list_item, new ArrayList<JSONObject>());
		
		/********************** SETUP ListView ************************/
		ListView lv = (ListView) rootView.findViewById(R.id.notificationList);
		lv.setOnItemClickListener(NotificationListFragment.this);
		
		// add the padding because of bottomBar
		lv.setPadding(lv.getPaddingLeft(), lv.getPaddingTop(), lv.getPaddingRight(), getResources().getDimensionPixelSize(R.dimen.bottom_bar_filling));
		lv.setClipToPadding(false);
		
		lv.setAdapter(mNotificationListAdapter);
		updateNotificationList();
		
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnNotificationListFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnNotificationListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void updateNotificationList() {

		new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("UserMessages")) {
					try {
						mNotificationListAdapter.addToList(CityUtils.convertJSONArayToArrayList(result.getJSONArray("UserMessages")));

						((CitybilityApplication) getActivity().getApplication()).stopLoading();

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
		}).userMessages(Constant.USER_MESSAGGES_STATUS_ALL);

	}

	/******************************************************************************
	 * OnItemClickListener methos implemetation
	 ******************************************************************************/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			mNotificationListAdapter.setViewAsRead(view, position);
			mNotificationListAdapter.notifyDataSetChanged();
			if (null != mListener) {
				mListener.onNotificationSelected(mNotificationListAdapter.getItem(position).getLong("MessageId"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
	public interface OnNotificationListFragmentInteractionListener {
		public void onNotificationSelected(long id);
	}

}
