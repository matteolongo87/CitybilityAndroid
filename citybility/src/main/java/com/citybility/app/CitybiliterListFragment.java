package com.citybility.app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.citybility.app.adapter.CitybiliterListAdapter;
import com.citybility.app.adapter.CitybiliterListAdapter.ListType;
import com.citybility.app.adapter.SpeudoInfiniteScrollingListListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.Constant.CitybiliterFilter;
import com.citybility.app.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CitybiliterListFragment.OnCitybiliterListFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CitybiliterListFragment#newInstance} factory method to create an instance
 * of this fragment.
 *
 */
public class CitybiliterListFragment extends Fragment implements OnItemClickListener,  OnClickListener {

	public static final String CITYBILITER_FILTER = "cb_filter";
	private CitybiliterFilter mCBFilter;
	
	private OnCitybiliterListFragmentInteractionListener mListener;
	private CitybiliterListAdapter<JSONObject> mCitybiliterListAdapter;
	private SpeudoInfiniteScrollingListListener mScrollListener;
	
	private View rootView;

	private Button activeButtom;
	private Button cityButton;
	
	private View progressBar;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CitybiliterListFragment() {
		super();
	}

	public CitybiliterListFragment(CitybiliterFilter filter) {
		this.mCBFilter = filter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
		
		if (savedInstanceState != null) {
			this.mCBFilter = CitybiliterFilter.valueOf(savedInstanceState.getString(CITYBILITER_FILTER, CitybiliterFilter.ACTIVITY.toString()));
        } else if(getArguments() != null) {
			this.mCBFilter = CitybiliterFilter.valueOf(getArguments().getString(CITYBILITER_FILTER, CitybiliterFilter.ACTIVITY.toString()));
        } 

		//default filter
		if(this.mCBFilter == null)
			this.mCBFilter = Constant.CitybiliterFilter.ACTIVITY;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_citybiliter_list, container, false);		

		activeButtom = (Button)this.rootView.findViewById(R.id.grpBtnActive);
		activeButtom.setOnClickListener(this);
		cityButton = (Button)this.rootView.findViewById(R.id.grpBtnCity);
		cityButton.setOnClickListener(this);
		
		
		/********************** SETUP ListView ************************/		
		ListView lv = (ListView)rootView.findViewById(R.id.citibiliterList);
		View mLoadingFooter = inflater.inflate(R.layout.listview_footer_loading_item, lv, false);
		progressBar = mLoadingFooter.findViewById(R.id.loadingFooterProgressBar);
		ViewUtils.hideView(progressBar);
		
		lv.addFooterView(mLoadingFooter);
		
		mCitybiliterListAdapter = new CitybiliterListAdapter<JSONObject>(getActivity(), R.layout.fragment_citybiliter_list_item, new ArrayList<JSONObject>());
		lv.setAdapter(mCitybiliterListAdapter);
		
		mScrollListener = new SpeudoInfiniteScrollingListListener(false, true){
			@Override
			protected void onLoadMore(int startIndex, int numToTake){
				
				if(startIndex > 0)
					ViewUtils.showView(progressBar);
				else
				   ((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
						
				updateCitybiliterList(startIndex, numToTake);
				
				toggleFilterButton();				
			}
		};
		
		lv.setOnScrollListener(mScrollListener);
		lv.setOnItemClickListener(CitybiliterListFragment.this);
				
		// add the padding because of bottomBar
		lv.setPadding(
					lv.getPaddingLeft(), 
					lv.getPaddingTop(), 
					lv.getPaddingRight(), 
					getResources().getDimensionPixelSize(R.dimen.bottom_bar_filling)
				);
		lv.setClipToPadding(false);
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		
	
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CITYBILITER_FILTER, this.mCBFilter.toString());
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCitybiliterListFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCitybiliterListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	
	public void resetCitybiliterList() {
		((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
		updateCitybiliterList(0, Constant.DEFAULT_NUM_TO_TAKE);
	}
		
	public void updateCitybiliterList(List<JSONObject> list) {
		mCitybiliterListAdapter.replaceList(list, ListType.NORMAL);
	}

	
	public void updateCitybiliterListFromSearchResult(List<JSONObject> list) {
		mCitybiliterListAdapter.replaceList(list, ListType.SEARCH);
	}
	
	private void updateCitybiliterList(int startIndex, int numToTake){
		CitybilityRequest req = new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("Citybiliters")) {
					try {	
						mCitybiliterListAdapter.addToList(CityUtils.convertJSONArayToArrayList(result.getJSONArray("Citybiliters")), ListType.NORMAL);	
						ViewUtils.hideView(progressBar);

						((CitybilityApplication)getActivity().getApplication()).stopLoading();
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onError(int errorCode, String message) {
				CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error), message);
				ViewUtils.hideView(progressBar);

				((CitybilityApplication)getActivity().getApplication()).stopLoading();
			}
		});
		
		switch (mCBFilter) {
		case ACTIVITY:
			req.citybiliters(CityUtils.getGPSCoordinates(), Constant.DEFAULT_DISTANCE, mCBFilter.getMode(), startIndex, numToTake, null);
			break;
		case PROXIMITY:
			req.citybiliters(CityUtils.getGPSCoordinates(), Constant.DEFAULT_DISTANCE, mCBFilter.getMode(), startIndex, numToTake, null);
			break;
		}
	}

	private void toggleFilterButton() {

		switch (this.mCBFilter) {
		case ACTIVITY:
			activeButtom.setBackgroundResource(R.drawable.group_button_left_pressed);
			activeButtom.setTextColor(getResources().getColor(android.R.color.white));

			cityButton.setBackgroundResource(R.drawable.group_button_right);
			cityButton.setTextColor(getResources().getColor(R.color.citybility_green));
			break;
		case PROXIMITY:
			cityButton.setBackgroundResource(R.drawable.group_button_right_pressed);
			cityButton.setTextColor(getResources().getColor(android.R.color.white));

			activeButtom.setBackgroundResource(R.drawable.group_button_left);
			activeButtom.setTextColor(getResources().getColor(R.color.citybility_green));
			break;
		}
		
		

	}


	/*******************************************************************
	 * OnClickListener Interface methods implementation
	 *******************************************************************/
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.grpBtnActive:
			this.mCBFilter = CitybiliterFilter.ACTIVITY;
			
			break;
		case R.id.grpBtnCity:
			this.mCBFilter = CitybiliterFilter.PROXIMITY;
			break;
		}
		
		this.mCitybiliterListAdapter.reset();
		this.mScrollListener.reset();

	}
	/******************************************************************************
	 * OnItemClickListener methos implemetation
	 ******************************************************************************/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {
			try {
				mListener.onCitybiliterSelected(mCitybiliterListAdapter.getItem(position).getLong("CitybiliterId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
	public interface OnCitybiliterListFragmentInteractionListener {
		public void onCitybiliterSelected(long id);
		public void onCitybiliterFilterChange(CitybiliterFilter filter);
	}


}
