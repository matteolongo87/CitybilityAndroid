package com.citybility.app.adapter;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.citybility.app.util.Constant;

public class SpeudoInfiniteScrollingListListener implements OnScrollListener {

	// The minimum amount of items to have below your current scroll position
	// before loading more
	private int loadingThreshold = Constant.DEFAULT_NUM_TO_TAKE;
	// The current offset index of data you have loaded
	private int pageIndex = 0;
	// The total number of items in the dataset after the last load
	private int previousTotalItemCount = 0;
	// True if we are still waiting for the last set of data to load.
	private boolean loading = false;
	// Sets the starting page index
	private boolean stopped = false;
	
	private int totalItems;
	private int visibleItems;


	private boolean hasHeader = false;
	private boolean hasFooter = false;
	private int nMumNotListItem = 0;
	
	public SpeudoInfiniteScrollingListListener(boolean hasHeader, boolean hasFooter){
		this.hasHeader = hasHeader;
		this.hasFooter = hasFooter;

		if(hasHeader) nMumNotListItem++;
		if(hasFooter) nMumNotListItem++;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//Log.d(this.getClass().getName(), "@@@@@ SCROLLING firstVisibleItem: " + firstVisibleItem + " - visibleItems: " + visibleItems + " - totalItem:" + totalItems + " - previousTotalItemCount: " + previousTotalItemCount + " -loading: "	+ loading);
		
		if (!stopped) {

			totalItems = totalItemCount - nMumNotListItem; 
			visibleItems = visibleItemCount - nMumNotListItem;
			
	        // If it’s still loading, we check to see if the dataset count has changed, if so we conclude it has finished loading and 
			// update the current page number and total item count.
			if (loading && (totalItems > previousTotalItemCount)) {
				//	Log.d(this.getClass().getName(), "@@@@@ loading && (totalItem > previousTotalItemCount)");
				previousTotalItemCount = totalItems;
				pageIndex++;
				loading = false;
			}

			//Log.d(this.getClass().getName(), "@@@@@ SCROLLING (totalItems - visibleItems): " + (totalItems - visibleItems) + " - (firstVisibleItem + loadingThreshold): " + (firstVisibleItem + loadingThreshold));
			// Log.d(this.getClass().getName(), "@@@@@ firstVisibleItem " + firstVisibleItem + " - visibleItems:" + visibleItems+ " - totalItems:" + totalItems+ " - (firstVisibleItem + visibleItems)%loadingThreshold:" + ((firstVisibleItem + visibleItems)%loadingThreshold));
		
			// If it isn’t currently loading, we check to see if we have breached the visibleThreshold and need to reload more data.
			if (!loading && ( totalItems==0 || (firstVisibleItem + visibleItems)%loadingThreshold == 0)) {
				//Log.d(this.getClass().getName(), "@@@@@ onLoadMore(" + (totalItems) + ", " + loadingThreshold + ")");
				onLoadMore(totalItems, loadingThreshold);

				loading = true;
			}
		}

	}

	protected void onLoadMore(int startIndex, int numToTake) {
		throw new IllegalStateException("YOU MUST OVVERRIDE loadMore(int startIndex, int numToTake)");
	}

	public void reset() {
		this.previousTotalItemCount = 0;
		this.pageIndex = 0;
		this.loading = false;
		this.stopped = false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void stop() {
		this.stopped = true;
	}

	public boolean hasHeader() {
		return hasHeader;
	}
	public boolean hasFooter() {
		return hasFooter;
	}
}
