package com.citybility.app;

import com.pit.barcodescanner.zbar.Result;
import com.pit.barcodescanner.zbar.ZBarScannerView;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QRCodeScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
	private OnQRCoceScannedFragmentInteractionListener mListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZBarScannerView(getActivity());
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
        } else {
            mFlash = false;
            mAutoFocus = true;
        }
        return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnQRCoceScannedFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnQRCoceScannelFragmentInteractionListener");
		}
	}

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
            mListener.onQRCodeScanned(rawResult.getContents());
        } catch (Exception e) {
        	Log.e(this.getClass().getName(), "Errore nella scansione del qr-code");
        	e.printStackTrace();
        }
        
        Log.d(getActivity().getClass().getName(), "Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName());
    }

 

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
    }

    public void setFlash(boolean on) {
    	mFlash = on;
        mScannerView.setFlash(mFlash);
	}
    
    public boolean getFlash() {
    	return mFlash;
	}

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    

	public void resetScanner() {
		mScannerView.startCamera();	
	}
	
    /******************************************************************************
	 * OnQRCoceScannelFragmentInteractionListener methos implemetation
	 ******************************************************************************/
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnQRCoceScannedFragmentInteractionListener {
		public void onQRCodeScanned(String qrcode);
	}


}
