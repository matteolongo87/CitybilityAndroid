package com.citybility.app;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citybility.app.adapter.ProjectPagerAdapter;
import com.citybility.app.custom.PageIndicatorsView;
import com.citybility.app.util.Constant.ProjectFilter;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProjectTabsFragment extends Fragment implements OnPageChangeListener {

	private ProjectPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private PageIndicatorsView mPageIndicatorsView;

	private OnProjectTabsFragmentInteractionListener mListener;

	public ProjectTabsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_project_tabs, container, false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new ProjectPagerAdapter(getFragmentManager(), getActivity());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(this);

		// set the page indicator
		mPageIndicatorsView = (PageIndicatorsView) rootView.findViewById(R.id.pagerIndicators);
		mPageIndicatorsView.setPointNumber(mViewPager.getAdapter().getCount());
		mPageIndicatorsView.setActivePoint(1);

		// set the title
		getActivity().getActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem()));

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProjectTabsFragmentInteractionListener) activity;
			mListener.onPageSelected(0, ProjectPagerAdapter.filters[0], getResources().getString(ProjectPagerAdapter.titleIds[0]));
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnProjectTabsFragmentInteractionListener");
		}
	}

	public void updateCurrentPageList(List<JSONObject> newProgectList){
		updatePageList(this.mViewPager.getCurrentItem(), newProgectList);
	}
	
	public void updatePageList(int index, List<JSONObject> newProgectList){
		((ProjectListFragment) mSectionsPagerAdapter.getItem(index)).updateProjectList(newProgectList);
	}
	public ViewPager getViewPager() {
		return mViewPager;
	}

	public void setViewPager(ViewPager viewPager) {
		this.mViewPager = viewPager;
	}

	/*******************************************************************
	 * OnPageChangeListener Interface methods implementations
	 *******************************************************************/
	@Override
	public void onPageSelected(int position) {

		this.mPageIndicatorsView.setActivePoint(position + 1);
		this.mPageIndicatorsView.updateIndicators();
		mListener.onPageSelected(position, this.mSectionsPagerAdapter.getPageFilter(position), this.mSectionsPagerAdapter.getPageTitle(position));
	}

	@Override
	public void onPageScrollStateChanged(int arg0)  {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)  {}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnProjectTabsFragmentInteractionListener {
		public void onPageSelected(int position, ProjectFilter filter, CharSequence title);
	}
}
