package com.citybility.app;

import java.util.Arrays;

import net.citybility.connect.CitybilityConnectApplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Switch;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.FacebookAPIHelper;
import com.citybility.app.util.ViewUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;


public class CodeSubmittedActivity extends Activity  implements OnClickListener  {

	
	String locationName;
	String beneficiaryName;
	String initiativeName;

	int mProblem = -1;
	
	private CallbackManager mCallbackManager;
	private long mlocationID;
	private AccessTokenTracker accessTokenTracker;
	private long mMovementId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		
		setContentView(R.layout.activity_code_submitted);

		Intent thisIntent = getIntent();
		mMovementId = thisIntent .getLongExtra("movementId", 0);
		mlocationID = thisIntent .getLongExtra("locationId", 0);
		locationName = thisIntent .getStringExtra("locationName");
		beneficiaryName = thisIntent.getStringExtra("beneficiaryName");
		initiativeName = thisIntent.getStringExtra("initiativeName");
		
		ViewUtils.setTextViewText(this, R.id.csb_mainText, Html.fromHtml(getString(R.string.csb_info, locationName, beneficiaryName, initiativeName )));

		findViewById( R.id.csb_feedbackOKAction).setOnClickListener(this);
		findViewById( R.id.csb_feedbackKOAction).setOnClickListener(this);
		findViewById( R.id.csb_btnShare).setOnClickListener(this);
		findViewById( R.id.csb_btnClose).setOnClickListener(this);
		
		String firstName = CitybilityConnectApplication.getFirstName();
		setTitle(getResources().getString(R.string.title_activity_code_submitted, firstName));

		mCallbackManager = CallbackManager.Factory.create();
		
		accessTokenTracker = new AccessTokenTracker() {

			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
	             AccessToken.setCurrentAccessToken(currentAccessToken);
			}
	    };
	}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    accessTokenTracker.stopTracking();
	}
	
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.csb_feedbackOKAction:
			ViewUtils.setImageViewImageResource(this, R.id.feedbackOKImage, R.drawable.ic_feedback_ok_green);
			ViewUtils.setImageViewImageResource(this, R.id.feedbackKOImage, R.drawable.ic_feedback_ko);
			mProblem = 0;
			break;
		case R.id.csb_feedbackKOAction:
			ViewUtils.setImageViewImageResource(this, R.id.feedbackOKImage, R.drawable.ic_feedback_ok);
			ViewUtils.setImageViewImageResource(this, R.id.feedbackKOImage, R.drawable.ic_feedback_ko_green);
			mProblem = 1;
			break;
		case R.id.csb_btnShare:

			EditText commentEditText = ((EditText)findViewById(R.id.csb_comment));
			String comment = commentEditText.getText()!=null ? commentEditText.getText().toString().trim() : "";
			
			if(mProblem == -1){
				
				CityUtils.showErrorAlert(CodeSubmittedActivity.this, getString(R.string.msg_error), getString(R.string.csb_null_feedback_msg));
			
			} else	if(comment.isEmpty()){
			
				CityUtils.showErrorAlert(CodeSubmittedActivity.this, getString(R.string.msg_error), getString(R.string.csb_null_feedback_comment_msg));
			
			} else {
			
				((CitybilityApplication) getApplication()).startLoading(this);
				new CitybilityRequest(this, new OnRequestResultListener() {
	
					@Override
					public void onSuccess(JSONObject result) {
							   new CitybilityRequest(CodeSubmittedActivity.this, new OnRequestResultListener() {
									
									@Override
									public void onSuccess(JSONObject result) {

									   if(((Switch) findViewById(R.id.csb_publishSwitch)).isChecked()) {
											try {
												JSONObject shareObj = new JSONObject();
												shareObj.put("Url", result.getString("Url"));
												shareObj.put("Caption", result.getString("Text"));
												new FacebookAPIHelper(CodeSubmittedActivity.this, mCallbackManager).postLink(shareObj, null);												
											} catch (JSONException e) {
												CityUtils.showErrorAlert(CodeSubmittedActivity.this, getString(R.string.msg_error), getString(R.string.fb_publish_error_msg));
												Log.e(this.getClass().getName(), e.getMessage());
												Log.e(this.getClass().getName(), Arrays.toString(e.getStackTrace()));
												e.printStackTrace();
											}
									   }
									  ((CitybilityApplication) getApplication()).stopLoading();
									  CodeSubmittedActivity.this.finish();	   
										 
									}
				
									@Override
									public void onError(int errorCode, String message) {
										((CitybilityApplication) getApplication()).stopLoading();
										CityUtils.showErrorAlert(CodeSubmittedActivity.this, getString(R.string.msg_error), message);
									}
								}).shareMessage(mMovementId, Constant.MessageType.TRANSSACTION.getType());
								
						 
					}
	
					@Override
					public void onError(int errorCode, String message) {
						((CitybilityApplication) getApplication()).stopLoading();
						CityUtils.showErrorAlert(CodeSubmittedActivity.this, getString(R.string.msg_error), message);
					}
				}).postFeedback(mMovementId, mProblem, comment);
				
			}
		
			break;
		case R.id.csb_btnClose:
			this.finish();
			break;
			
		}
		
	}

	
}
