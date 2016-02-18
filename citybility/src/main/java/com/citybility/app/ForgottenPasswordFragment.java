package com.citybility.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link ForgottenPasswordFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the
 * {@link ForgottenPasswordFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class ForgottenPasswordFragment extends DialogFragment implements OnClickListener {

	private View dialogView;
	private AlertDialog dialog;
	private Activity mActivity;
	
	public ForgottenPasswordFragment(Activity activity) {
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

	    dialogView = i.inflate(R.layout.fragment_forgotten_password, null) ;

	    dialog = new AlertDialog.Builder(getActivity())
	            .setView(dialogView)
	            .create();
	     
	    // per la customizzazione
	    // dialog.setOnShowListener(new CustomizerOnShowListener());       

	    dialogView.findViewById(R.id.btnSend).setOnClickListener(this);
	    dialogView.findViewById(R.id.btnCancel).setOnClickListener(this);
	    
	    return dialog;
	}


	/****************************************************************** 
	 * OnClickListener method implementation
	 ******************************************************************/
	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.btnSend:
			String email = ((EditText) dialogView.findViewById(R.id.emailField)).getText().toString();
        	//Log.d("@@@@@@@@@@@@ ", "email: "+email.getText());
        	if(!email.isEmpty()){
				new CitybilityRequest(mActivity, new OnRequestResultListener() {
	
					@Override
					public void onSuccess(JSONObject result) {
						try {
							dialog.cancel();
							((CitybilityApplication)mActivity.getApplication()).stopLoading();
							CityUtils.showInfoAlert(mActivity, getString(R.string.msg_success) , result.getString("Message") );
						} catch (JSONException e) {
							CityUtils.showInfoAlert(mActivity, getString(R.string.msg_success) , getString(R.string.password_forgotten_request_sent) );
							e.printStackTrace();
						}
					}
	
					@Override
					public void onError(int errorCode, String message) {
						dialog.cancel();
						((CitybilityApplication)mActivity.getApplication()).stopLoading();
						CityUtils.showErrorAlert(mActivity, getString(R.string.msg_error) , message);
					}
	
				}).forgotPassword(email);

			}
			break;
		case R.id.btnCancel:
			dialog.cancel();
			break;
		}
		
	}    
	/****************************************************************** 
	 * AlertDialog Customizzaion
	 ******************************************************************/
	class CustomizerOnShowListener implements DialogInterface.OnShowListener {

        @Override
        public void onShow(DialogInterface dialog) {
        	/*
            AlertDialog alertDialog = (AlertDialog) dialog;
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setTextColor(getResources().getColor(R.color.citybility_green));
            positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            positiveButton.setBackgroundColor(0xFFFFFFFF);
            
            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativeButton.setTextColor(getResources().getColor(R.color.citybility_gray_text));
            negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            negativeButton.setBackgroundColor(0xFFFFFFFF);
            */
        }
    }

   
	
}
