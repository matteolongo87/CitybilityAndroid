package com.citybility.app.custom;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citybility.app.R;

public class loadingDialogFragment extends DialogFragment {

	AnimationDrawable mLoadingAnimation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.loadingDialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();
		if (d != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			d.getWindow().setLayout(width, height);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.loading_modal, container, false);
		return root;
	}

	/** The system calls this only when creating the layout in a dialog. */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		return dialog;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mLoadingAnimation = (AnimationDrawable) ((ImageView) view.findViewById(R.id.loadingAnimation)).getDrawable();
		if (!mLoadingAnimation.isRunning())
			mLoadingAnimation.start();
	}

	@Override
	public void dismiss() {
		if (mLoadingAnimation != null && mLoadingAnimation.isRunning())
			mLoadingAnimation.stop();
		try {
			super.dismissAllowingStateLoss();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Errore in dismiss");
		}
	}

}
