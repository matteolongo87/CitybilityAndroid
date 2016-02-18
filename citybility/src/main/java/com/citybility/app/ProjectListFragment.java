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

import com.citybility.app.adapter.ProjectListAdapter;
import com.citybility.app.adapter.SpeudoInfiniteScrollingListListener;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.Constant.ProjectFilter;
import com.citybility.app.util.Constant.ProjectOrdering;
import com.citybility.app.util.ViewUtils;

public class ProjectListFragment extends ListFragment {

	public static final String PROJECT_FILTER = "prj_filter";
	private ProjectFilter mPrjFilter;
	
	private OnProjectListFragmentInteractionListener mListener;
	private ProjectListAdapter<JSONObject> mProjectListAdapter;

	private View progressBar;
	private SpeudoInfiniteScrollingListListener mScrollListener;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ProjectListFragment() {
		super();
	}

	public ProjectListFragment(ProjectFilter filter) {
		this.mPrjFilter = filter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			this.mPrjFilter = ProjectFilter.valueOf(savedInstanceState.getString(PROJECT_FILTER, ProjectFilter.HOME.toString()));
        } else if(getArguments() != null) {
			this.mPrjFilter = ProjectFilter.valueOf(getArguments().getString(PROJECT_FILTER, ProjectFilter.HOME.toString()));
        } 

		//default filter
		if(this.mPrjFilter == null)
			this.mPrjFilter = Constant.ProjectFilter.HOME;
	}


    @Override 
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListShownNoAnimation(true);
    }
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
		
		/********************** SETUP ListView ************************/		
		ListView lv = this.getListView();
		View mLoadingFooter = getActivity().getLayoutInflater().inflate(R.layout.listview_footer_loading_item, lv, false);
		progressBar = mLoadingFooter.findViewById(R.id.loadingFooterProgressBar);
		ViewUtils.hideView(progressBar);
		
		lv.addFooterView(mLoadingFooter);

		mProjectListAdapter = new ProjectListAdapter<JSONObject>(getActivity(), R.layout.fragment_project_list_item, new ArrayList<JSONObject>());

		lv.setAdapter(mProjectListAdapter);
		
		mScrollListener = new SpeudoInfiniteScrollingListListener(false, true){
			@Override
			protected void onLoadMore(int startIndex, int numToTake){
				
				unpdateProjectList(startIndex, numToTake);
				
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
        outState.putString(PROJECT_FILTER, this.mPrjFilter.toString());
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProjectListFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnProjectListFragmentInteractionListener");
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
				mListener.onProjectSelected(mProjectListAdapter.getItem(position).getLong("InitiativeId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	private void unpdateProjectList(int startIndex, int numToTake) {

		if(startIndex > 0)
			ViewUtils.showView(progressBar);
		else
		   ((CitybilityApplication)getActivity().getApplication()).startLoading(getActivity());
			
		CitybilityRequest req = new CitybilityRequest(getActivity(), new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				if (result.has("Initiatives")) {
					try {

						List<JSONObject> tmp = mProjectListAdapter.getItems();
						tmp.addAll(CityUtils.convertJSONArayToArrayList(result.getJSONArray("Initiatives")));
						mProjectListAdapter = new ProjectListAdapter<JSONObject>(getActivity(), R.layout.fragment_project_list_item, tmp);
						setListAdapter(mProjectListAdapter);
						
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
		
		switch (this.mPrjFilter) {
		case HOME:
			req.initiatives(CityUtils.getGPSCoordinates(), 0, ProjectOrdering.POPULAR.getOrder(), startIndex, numToTake, null);
			break;
		case NEARBY:
			req.initiatives(CityUtils.getGPSCoordinates(), 10000, ProjectOrdering.PROXIMITY.getOrder(), startIndex, numToTake, null);
			break;
		case RECENTS:
			req.initiatives(CityUtils.getGPSCoordinates(), 0, ProjectOrdering.RECENTS.getOrder(), startIndex, numToTake, null);
			break;
		}
		
	}
	
	public void updateProjectList(List<JSONObject> newProjectList){
			this.mProjectListAdapter.replaceList(newProjectList);
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
	public interface OnProjectListFragmentInteractionListener {
		public void onProjectSelected(long id);
	}

}
