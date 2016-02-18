package com.citybility.app;


import net.citybility.connect.CitybilityConnectApplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.citybility.app.custom.PageIndicatorsView;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;


public class PinTypingActivity extends keypadActivity  {

	private PageIndicatorsView mPageIndicatorsView;

	private final int POINTS = 4;

	private String qrcode;

	private String locationName;
	private String beneficiaryName;
	private String initiativeName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent thisIntent = getIntent();
		locationName = thisIntent.getStringExtra("locationName");
		beneficiaryName = thisIntent.getStringExtra("beneficiaryName");
		initiativeName = thisIntent.getStringExtra("initiativeName");
		
		qrcode = thisIntent.getStringExtra("qrcode");
		
		SpannableStringBuilder sb = new SpannableStringBuilder(getResources().getString(R.string.tk_info_main, locationName));
		ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.citybility_green)); 
		sb.setSpan(fcs, 10, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);		
		ViewUtils.setTextViewText(this, R.id.ct_mainText, sb);
		
		ViewUtils.setTextViewText(this, R.id.ct_subText, getResources().getString(R.string.tk_info_sub));
		
		// get the page indicator
		mPageIndicatorsView = (PageIndicatorsView) findViewById(R.id.codeIndicators);
		mPageIndicatorsView.setPointNumber(POINTS);
		mPageIndicatorsView.setPointMargin(CityUtils.convertDp2Px(40, getResources().getDisplayMetrics()));
	}

	@Override
	protected void onNumberTyped() {
		if(getTypedSize()  == POINTS){
			

			((CitybilityApplication) getApplication()).startLoading(this);

			String token = getTypedAsString(); 
			
			new CitybilityRequest(this, new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					
						Intent submittedIntent = new Intent(PinTypingActivity.this, CodeSubmittedActivity.class);

						try {							
							submittedIntent.putExtra("movementId", result.getLong("MovementId"));
							submittedIntent.putExtra("locationId", result.getLong("LocationId"));
							submittedIntent.putExtra("locationName", locationName);
							submittedIntent.putExtra("beneficiaryName", beneficiaryName);
							submittedIntent.putExtra("initiativeName", initiativeName);
						
							startActivity(submittedIntent);
							PinTypingActivity.this.finish();
						
						} catch (JSONException e) {
							((CitybilityApplication) getApplication()).stopLoading();
							CityUtils.showErrorAlert(PinTypingActivity.this, getString(R.string.msg_error), getString(R.string.fb_publish_error_msg));
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

				@Override
				public void onError(int errorCode, String message) {
					((CitybilityApplication) getApplication()).stopLoading();
					CityUtils.showErrorAlert(PinTypingActivity.this, getString(R.string.msg_error), message);
				}

			}).registerTransaction(PinTypingActivity.this, qrcode, token, CitybilityConnectApplication.getCitibilityID());
			
		}
	}


}
