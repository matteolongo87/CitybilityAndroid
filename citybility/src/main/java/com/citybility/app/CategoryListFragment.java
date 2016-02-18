package com.citybility.app;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.citybility.app.adapter.CategoryListAdapter;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CategoryListFragment extends ListFragment {

	private OnCategorieslistFragmentInteractionListener mListener;

	private List<JSONObject> mCategoriesList;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CategoryListFragment() { 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Get categories
		 new CitybilityRequest(getActivity(), new OnRequestResultListener() {

             @Override
             public void onSuccess(JSONObject result) {
            	 if(result.has("Categories")){
            		 try {

						mCategoriesList = CityUtils.convertJSONArayToArrayList(result.getJSONArray("Categories"));
						CategoryListAdapter<JSONObject> adapter = new CategoryListAdapter<JSONObject>(getActivity(), R.layout.fragment_category_list_item, mCategoriesList);
						setListAdapter(adapter);
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
            	 }
             }

             @Override
             public void onError(int errorCode, String message) {
            	 CityUtils.showErrorAlert(getActivity(), getString(R.string.msg_error) , message);
             }

         }).categories();
		 
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		// add the padding because of bottomBar
		ListView lv = this.getListView();
		lv.setPadding(
					lv.getPaddingLeft(), 
					lv.getPaddingTop(), 
					lv.getPaddingRight(), 
					getResources().getDimensionPixelSize(R.dimen.bottom_bar_filling)
				);
		lv.setClipToPadding(false);
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCategorieslistFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCategorieslistFragmentInteractionListener");
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
				mListener.onCategoriesListFragmentInteraction(mCategoriesList.get(position).getString("CategoryId"), mCategoriesList.get(position).getString("Description"));
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
	public interface OnCategorieslistFragmentInteractionListener {
		public void onCategoriesListFragmentInteraction(String id, String name);
	}

}
