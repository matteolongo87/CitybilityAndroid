package com.citybility.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link ForgottenPasswordFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the
 * {@link NoNetworkFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class NoNetworkFragment extends DialogFragment implements OnClickListener {

	private View mDialogView;
	private AlertDialog mDialog;
	private Activity mActivity;
	
	private static NoNetworkFragment mNoNetworkDialogFragment;
	

	public static void showDialog(Activity activity, String string) {
		if(mNoNetworkDialogFragment == null){
			mNoNetworkDialogFragment = new NoNetworkFragment(activity);
			mNoNetworkDialogFragment.show(activity.getFragmentManager(), "fragment_no_network");
		}
	}

	private NoNetworkFragment(Activity activity) {
		mActivity = activity; 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
        
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    LayoutInflater i = getActivity().getLayoutInflater();

	    mDialogView = i.inflate(R.layout.fragment_no_network, null) ;

	    mDialog = new AlertDialog.Builder(getActivity())
	            .setView(mDialogView)
	            .create();
	     
	    // per la customizzazione
	    // dialog.setOnShowListener(new CustomizerOnShowListener());       

	    mDialogView.findViewById(R.id.close).setOnClickListener(this);
	    mDialogView.findViewById(R.id.btnRefresh).setOnClickListener(this);
	    mDialogView.findViewById(R.id.btnNetConf).setOnClickListener(this);

	    return mDialog;
	}


	@Override
	public void onCancel(DialogInterface dialog){
		mNoNetworkDialogFragment = null;
	}
	
	/****************************************************************** 
	 * OnClickListener method implementation
	 ******************************************************************/
	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.btnRefresh:
			Intent current = mActivity.getIntent();
			mActivity.finish();
			mActivity.startActivity(current);
			mDialog.cancel();
			break;
		case R.id.btnNetConf:
			mActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
			break;
		case R.id.close:
			mDialog.cancel();
		}
		
	}    

   
	
}
