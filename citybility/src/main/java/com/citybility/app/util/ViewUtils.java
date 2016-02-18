package com.citybility.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.citybility.app.R;
import com.citybility.app.custom.SpannedTextView;
import com.citybility.app.util.HttpImageLoader.FITMODE;

public class ViewUtils {


	/*
	 * getEditText
	 */
	public static EditText getEditText(Activity parent, int viewId) {
		return((EditText) parent.findViewById(viewId));
	}

	public static EditText getEditText(View parent, int viewId) {
		return ((EditText) parent.findViewById(viewId));
	}
	
	/*
	 * getEditTextDate
	 */
	public static Date getEditTextDate(View parent, int viewId) throws ParseException {
		Date result = null;
		EditText view = ((EditText) parent.findViewById(viewId));
		if (view != null) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			result = df.parse(view.getText().toString());
		}
		return result;
	}

	public static Date getEditTextDate(Activity activity, int viewId) throws ParseException {
		Date result = null;
		EditText view = ((EditText) activity.findViewById(viewId));
		if (view != null && !view.getText().toString().isEmpty()) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			result = df.parse(view.getText().toString());
		}
		return result;
	}

	
	
	/*
	 * getEditTextText
	 */
	public static String getEditTextText(Activity parent, int viewId) {
		CharSequence result = "";
		EditText view = ((EditText) parent.findViewById(viewId));
		if (view != null) {
			result = view.getText();
		}
		return result.toString();
	}

	public static String getEditTextText(View parent, int viewId) {
		CharSequence result = "";
		TextView view = ((TextView) parent.findViewById(viewId));
		if (view != null) {
			result = view.getText();
			view.setVisibility(View.VISIBLE);
		}
		return result.toString();
	}

	/*
	 * setSpannedTextViewText
	 */
	public static boolean setSpannedTextViewText(View parent, int viewId, CharSequence text) {
		boolean result = false;
		SpannedTextView view = ((SpannedTextView) parent.findViewById(viewId));
		if (view != null) {
			view.setSpannedText(text);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}

	public static boolean setSpannedTextViewText(Activity activity, int viewId, CharSequence text) {
		boolean result = false;
		SpannedTextView view = ((SpannedTextView) activity.findViewById(viewId));
		if (view != null) {
			view.setSpannedText(text);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}

	
	public static boolean setTextViewText(View parent, int viewId, CharSequence text) {
		boolean result = false;
		TextView view = ((TextView) parent.findViewById(viewId));
		if (view != null) {
			view.setText(text);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}

	public static boolean setTextViewText(Activity activity, int viewId, CharSequence text) {
		boolean result = false;
		TextView view = ((TextView) activity.findViewById(viewId));
		if (view != null) {
			view.setText(text);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}


	/*
	 * setImageViewContentDescription
	 */
	public static boolean setImageViewContentDescription(Activity activity, int viewId, String contentDescription) {
		boolean result = false;
		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			view.setContentDescription(contentDescription);
			result = true;
		}
		return result;
	}

	public static boolean setImageViewContentDescription(View parent, int viewId, String contentDescription) {
		boolean result = false;
		ImageView view = ((ImageView) parent.findViewById(viewId));
		if (view != null) {
			view.setContentDescription(contentDescription);
			result = true;
		}
		return result;
	}

	/*
	 * setImageViewContentDescription
	 */
	public static boolean setImageViewImageResource(Activity activity, int viewId, int resource) {
		boolean result = false;
		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			view.setImageResource(resource);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}

	public static boolean setImageViewImageResource(View parent, int viewId, int resource) {
		boolean result = false;
		ImageView view = ((ImageView) parent.findViewById(viewId));
		if (view != null) {
			view.setImageResource(resource);
			result = true;
			view.setVisibility(View.VISIBLE);
		}
		return result;
	}
	/*
	 * setImageViewHttpImage
	 */


	public static void setScaledImageViewHttpImage(Activity activity, int viewId, String url, boolean useLoagingImage, boolean useDefault, FITMODE mode) {

		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			HttpImageLoader imageLoader = new HttpImageLoader(activity, url, view, useLoagingImage, useDefault);
			imageLoader.setFitMode(mode);
			imageLoader.load();
		}
	}


	public static void setScaledImageViewHttpImage(Activity activity, int viewId, String url, boolean useLoagingImage, boolean useDefault) {

		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			new HttpImageLoader(activity, url, view, useLoagingImage, useDefault).load();
		}
	}
	
	public static void setImageViewHttpImage(Activity activity, int viewId, String url, boolean useLoagingImage, boolean useDefault, FITMODE mode) {

		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			HttpImageLoader imageLoader = new HttpImageLoader(activity, url, view, useLoagingImage, useDefault);
			imageLoader.setFitMode(mode);
			imageLoader.load();
		}
	}
	public static void setImageViewHttpImage(Activity activity, int viewId, String url, boolean useLoagingImage, boolean useDefault) {

		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			new HttpImageLoader(activity, url, view, useLoagingImage, useDefault).load();
		}
	}

	public static void setImageViewHttpImage(Activity activity, int viewId, String url, boolean useLoagingImage, int defaultDrawableId) {

		ImageView view = ((ImageView) activity.findViewById(viewId));
		if (view != null) {
			new HttpImageLoader(activity, url, view, useLoagingImage, defaultDrawableId).load();
		}
	}
	
	public static void setImageViewHttpImage(View parent, int viewId, String url, boolean useLoagingImage, boolean useDefault) {

		ImageView view = ((ImageView) parent.findViewById(viewId));
		if (view != null) {
			new HttpImageLoader(parent.getContext(), url, view, useLoagingImage, useDefault).load();
		}
	}
	public static void setImageViewHttpImage(View parent, int viewId, String url, boolean useLoagingImage, int defaultDrawableId) {

		ImageView view = ((ImageView) parent.findViewById(viewId));
		if (view != null) {
			new HttpImageLoader(parent.getContext(), url, view, useLoagingImage, defaultDrawableId).load();
		}
	}
	
	/*
	 * setNotImplementedAction - TMP method
	 */
	public static void setNotImplementedAction(Activity activity, int viewId) {
		final Context ctx = activity;
		View view = ((View) activity.findViewById(viewId));
		if (view != null) {
			view.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					Toast.makeText(ctx, "NON IMPLEMENTATO", Toast.LENGTH_LONG).show();
	
				}
			});
		}
	}
	public static void setNotImplementedAction(Context context, View view) {
		final Context ctx = context;
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ctx, "NON IMPLEMENTATO", Toast.LENGTH_LONG).show();

			}
		});
	}

	/**
	 * setListViewHeightBasedOnChildren - Set the Height of the ListView dynamically.
	 * Hack to fix the issue of not showing all the items of the ListView when placed inside a ScrollView 
	 * @param listView the ListView to fix
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}

	public static void showView(View view) {
		if(view != null)
			view.setVisibility(View.VISIBLE);
	}
	
	public static void hideView(View view) {
		if(view != null)
			view.setVisibility(View.GONE);
	}
	
	
	public static boolean validateMandatoryTextField(Activity activity, EditText editText, boolean focus){
		boolean isOk = true;
		String text = editText.getText()!=null ? editText.getText().toString(): "";
		if(text.isEmpty()){
			isOk = false;
			editText.setError(activity.getString(R.string.form_mandatory_field_missing));
			if(focus){
				editText.setFocusable(true);
				editText.setFocusableInTouchMode(true);
				editText.requestFocus();
			}
		} 
		return isOk;
	}

	public static boolean validateEmailField(Activity activity, EditText editText, boolean focus){
		boolean isOk = true;
		String email = editText.getText()!=null ? editText.getText().toString(): "";
		if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
			isOk = false;
			editText.setError(activity.getString(R.string.form_email_ko));
			if(focus){
				editText.setFocusable(true);
				editText.setFocusableInTouchMode(true);
				editText.requestFocus();
			}
		} 
		return isOk;
	}
}
