package com.citybility.app;

import com.pit.barcodescanner.core.CameraUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.citybility.app.QRCodeScannerFragment.OnQRCoceScannedFragmentInteractionListener;
import com.citybility.app.custom.CustomClickableSpan;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;

public class QRCodeScannerActivity extends CitybilityBaseActivity implements OnQRCoceScannedFragmentInteractionListener {

	private QRCodeScannerFragment mFragmentScanner;
	private ImageView mFlashButtonImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode_scanner);

		mFragmentScanner = (QRCodeScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment);

		mFlashButtonImage = (ImageView) findViewById(R.id.qrc_btn_flash);
		this.setupFlashLightAction();

		TextView qrManualCodeMsgTV = (TextView) findViewById(R.id.qrc_manualTypingMsg);
		this.setupLinkInTextView(qrManualCodeMsgTV);

	}

	private void setupFlashLightAction() {

		if (CameraUtils.isFlashSupported(this)) {
			// set default icon e flash
			if (mFragmentScanner.getFlash()) {
				flashOn();
			} else {
				flashOff();
			}

			mFlashButtonImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mFragmentScanner.getFlash()) {
						flashOff();
					} else {
						flashOn();
					}

				}
			});
		} else { // flash not supported
			mFlashButtonImage.setVisibility(View.GONE);
		}

	}

	private void flashOn() {
		mFragmentScanner.setFlash(true);
		mFlashButtonImage.setImageResource(R.drawable.ic_light_on);
	}

	private void flashOff() {
		mFragmentScanner.setFlash(false);
		mFlashButtonImage.setImageResource(R.drawable.ic_light_off);
	}

	private void setupLinkInTextView(TextView view) {
		ClickableSpan clickableSpan = new CustomClickableSpan(getResources().getColor(R.color.citybility_green), false) {

			@Override
			public void onClick(View textView) {
				startActivity(new Intent(QRCodeScannerActivity.this, CodeTypingActivity.class));
				QRCodeScannerActivity.this.finish();
			}
		};

		Spannable spannableMSG = new SpannableString(view.getText());

		spannableMSG.setSpan(clickableSpan, 23, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		view.setText(spannableMSG);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/*******************************************************************
	 * OnQRCoceScannelFragmentInteractionListener Interface methods
	 * implementation
	 *******************************************************************/

	@Override
	public void onQRCodeScanned(final String qrcode) {

		try {

			if (qrcode!=null && !qrcode.isEmpty()) {
				new CitybilityRequest(this, new OnRequestResultListener() {

					@Override
					public void onSuccess(JSONObject result) {
						if (result.has("CampaignLocation")) {

							Intent tokenIntent = new Intent(QRCodeScannerActivity.this, PinTypingActivity.class);
							try {
								JSONObject campaignLocation = result.getJSONObject("CampaignLocation");
								tokenIntent.putExtra("locationName", campaignLocation.getString("LocationName"));
								tokenIntent.putExtra("beneficiaryName", campaignLocation.getString("BeneficiaryName"));
								tokenIntent.putExtra("initiativeName", campaignLocation.getString("InitiativeName"));
							} catch (JSONException e) {
								e.printStackTrace();
							}

							tokenIntent.putExtra("qrcode", qrcode);
							startActivity(tokenIntent);
						}
					}

					@Override
					public void onError(int errorCode, String message) {
						CityUtils.showErrorAlert(QRCodeScannerActivity.this, getString(R.string.msg_error),  getString(R.string.qrc_error_msg), errorListener);	
					}
					
				}).verifyCode(qrcode);
			} else {
				CityUtils.showErrorAlert(QRCodeScannerActivity.this, getString(R.string.msg_error),  getString(R.string.qrc_invalid_msg), errorListener);
			}
		} catch (NumberFormatException e) {
			Log.e(this.getClass().getSimpleName(), "Codice non valido");
			CityUtils.showErrorAlert(QRCodeScannerActivity.this, getString(R.string.msg_error), getString(R.string.qrc_invalid_msg), errorListener);
		}
	}
	
	private DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mFragmentScanner.resetScanner();				
		}
		
	};

}
