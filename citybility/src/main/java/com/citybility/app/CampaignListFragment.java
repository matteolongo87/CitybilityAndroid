package com.citybility.app;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.citybility.app.adapter.CampaignListAdapter;
import com.citybility.app.adapter.SpeudoInfiniteScrollingListListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.Constant.CampaignFilter;
import com.citybility.app.util.Constant.CampaignOrdering;
import com.citybility.app.util.ViewUtils;

public class CampaignListFragment extends ListFragment {

	public static final String CAMPAIGN_FILTER = "cmp_filter";
	public static final String ARG_CAT_ID = "category_id";
	public static final String ARG_CAT_NAME = "category_name";
	private CampaignFilter mCmpFilter;
	private String mCategotyId;
	
	private OnCampaignsListFragmentInteractionListener mListener;
	private CampaignListAdapter<JSONObject> mCampaignListAdapter;
	@SuppressWarnings("unused")
	private String mCategotyName;
	
	private View progressBar;
	private SpeudoInfiniteScrollingListListener mScrollListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CampaignListFragment() {
		super();
	}

	public CampaignListFragment(CampaignFilter filter) {
		this.mCmpFilter = filter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
		
		
		if (savedInstanceState != null) {
			this.mCmpFilter = CampaignFilter.valueOf(savedInstanceState.getString(CAMPAIGN_FILTER, CampaignFilter.HOME.toString()));
			this.mCategotyId = savedInstanceState.getString(ARG_CAT_ID, "");
			this.mCategotyName = savedInstanceState.getString(ARG_CAT_NAME, "");
        } else if(getArguments() != null) {
			this.mCmpFilter = CampaignFilter.valueOf(getArguments().getString(CAMPAIGN_FILTER, CampaignFilter.HOME.toString()));
			this.mCategotyId = getArguments().getString(ARG_CAT_ID, "");	
			this.mCategotyName = getArguments().getString(ARG_CAT_NAME, "");
        } 

		//default filter
		if(this.mCmpFilter == null)
			this.mCmpFilter = Constant.CampaignFilter.HOME;
	}

    @Override 
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListShownNoAnimation(true);
    }
    
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		/********************** SETUP ListView ************************/		
		ListView lv = this.getListView();
		View mLoadingFooter = getActivity().getLayoutInflater().inflate(R.layout.listview_footer_loading_item, lv, false);
		progressBar = mLoadingFooter.findViewById(R.id.loadingFooterProgressBar);
		ViewUtils.hideView(progressBar);
		
		lv.addFooterView(mLoadingFooter);

		mCampaignListAdapter = new CampaignListAdapter<JSONObject>(getActivity(), R.layout.fragment_campaign_list_item, new ArrayList<JSONObject>());

		lv.setAdapter(mCampaignListAdapter);
		
		mScrollListener = new SpeudoInfiniteScrollingListListener(false, true){
			@Override
			protected void onLoadMore(int startIndex, int numToTake){
				
				unpdateCampaignList(startIndex, numToTake);
				
			}

		};
		lv.setOnScrollListener(mScrollListener);

		// add the padding because of bottomBar
		lv.setPadding(
					lv.getPaddingLeft(), 
					lv.getPaddingTop(), 
					lv.getPaddingRight(), 
					getResources().getDimensionPixelSize(R.dimen.bottom_bar_filling)
				);
		lv.setClipToPadding(false);
	
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CAMPAIGN_FILTER, mCmpFilter.toString());
        outState.putString(ARG_CAT_ID, mCategotyId);
        outState.putString(ARG_CAT_NAME, mCategotyId);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCampaignsListFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCampaignsListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			try {
				mListener.onCampaignSelected(this.mCmpFilter, mCampaignListAdapter.getItem(position).getLong("LocationId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void unpdateCampaignList(int startIndex, int numToTake) {
		 
		if(startIndex > 0)
			ViewUtils.showView(progressBar);
		else
		   ((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
			
		CitybilityRequest req = new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("CampaignsLocations")) {
					try {

						List<JSONObject> tmp = mCampaignListAdapter.getItems();
						tmp.addAll(CityUtils.convertJSONArayToArrayList(result.getJSONArray("CampaignsLocations")));
						mCampaignListAdapter = new CampaignListAdapter<JSONObject>(getActivity(), R.layout.fragment_campaign_list_item, tmp);
						setListAdapter(mCampaignListAdapter);
						
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
			}

		});
		
		if(mCategotyId != null && !mCategotyId.isEmpty()) // after clicking in the categories list item
			req.campaignsLocations(Long.parseLong(mCategotyId), CityUtils.getGPSCoordinates(), Constant.DEFAULT_DISTANCE, CampaignOrdering.PROXIMITY.getOrder(), startIndex, numToTake, null);
		else { 
			// by CityShpos tabs
			switch (this.mCmpFilter) {
			case HOME:
				req.campaignsLocations(0, CityUtils.getGPSCoordinates(), 0, CampaignOrdering.HOME.getOrder(), startIndex, numToTake, null);
				break;
			case NEARBY:
				req.campaignsLocations(0, CityUtils.getGPSCoordinates(), Constant.DEFAULT_DISTANCE, CampaignOrdering.PROXIMITY.getOrder(), startIndex, numToTake, null);
				break;
			case RECENTS:
				req.campaignsLocations(0, CityUtils.getGPSCoordinates(), 0, CampaignOrdering.RECENTS.getOrder(), startIndex, numToTake, null);
				break;
			default:
				break;
			}
			
		}
		
	}
	
	public void updateCampaignList(List<JSONObject> newCampaignsList){
			this.mCampaignListAdapter.replaceList(newCampaignsList);
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
	public interface OnCampaignsListFragmentInteractionListener {
		public void onCampaignSelected(CampaignFilter type, long id);
	}

}
