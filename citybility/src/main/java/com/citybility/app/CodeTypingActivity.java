package com.citybility.app;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.citybility.app.custom.PageIndicatorsView;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;


public class CodeTypingActivity extends keypadActivity  {

	private PageIndicatorsView mPageIndicatorsView;
	
	private final int POINTS = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtils.setTextViewText(this, R.id.ct_mainText, getResources().getString(R.string.qrcm_info_main));
		ViewUtils.setTextViewText(this, R.id.ct_subText, getResources().getString(R.string.qrcm_info_sub));
		// get the page indicator
		mPageIndicatorsView = (PageIndicatorsView) findViewById(R.id.codeIndicators);
		mPageIndicatorsView.setPointNumber(POINTS);
		
	}

	@Override
	protected void onNumberTyped() {
		if(getTypedSize() == POINTS){

			((CitybilityApplication) getApplication()).startLoading(this);
			
			final String qrcode = getTypedAsString(); 
			
			new CitybilityRequest(this, new OnRequestResultListener() {

				@Override
				public void onSuccess(JSONObject result) {
					if(result.has("CampaignLocation")){

						Intent tokenIntent = new Intent(CodeTypingActivity.this, PinTypingActivity.class);
						
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
						CodeTypingActivity.this.finish();
					}
				}

				@Override
				public void onError(int errorCode, String message) {
					((CitybilityApplication) getApplication()).stopLoading();
					CityUtils.showErrorAlert(CodeTypingActivity.this, getString(R.string.msg_error), message);
				}

			}).verifyCode(qrcode);
		}
	}

	
}
