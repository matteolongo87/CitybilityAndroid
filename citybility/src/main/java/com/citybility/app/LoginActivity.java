package com.citybility.app;

import net.citybility.connect.CitybilityConnectApplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.ViewUtils;

public class LoginActivity extends CitybilityBaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.btnSignIn).setOnClickListener(this);
		findViewById(R.id.accountAction).setOnClickListener(this);
		findViewById(R.id.passwordForgottenAction).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btnSignIn:
			
			((CitybilityApplication)getApplication()).startLoading(this);
			if(validateForm()) {
				String email = ViewUtils.getEditTextText(this, R.id.lgEmailField);
				String password = ViewUtils.getEditTextText(this, R.id.lgPasswordField);
				
				new CitybilityRequest(this, new OnRequestResultListener() {
	
					@Override
					public void onSuccess(JSONObject result) {
						new CitybilityRequest(LoginActivity.this, new OnRequestResultListener() {
	
							@Override
							public void onSuccess(JSONObject result) {
								if(result.has("Citybiliter")){
									try {
										JSONObject user = result.getJSONObject("Citybiliter");
										CitybilityConnectApplication.setCitibilityID(user.getLong("CitybiliterId"));
									    CitybilityConnectApplication.setFirstName(user.getString("FirstName"));
									    CitybilityConnectApplication.setLastName(user.getString("LastName"));
										startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
										finish();
									} catch (JSONException e) {
										((CitybilityApplication)getApplication()).stopLoading();
										e.printStackTrace();
									}
								}
							}
	
							@Override
							public void onError(int errorCode, String message) {
								((CitybilityApplication)getApplication()).stopLoading();
								CityUtils.showErrorAlert(LoginActivity.this, getString(R.string.msg_error) , message);
							}
	
						}).profile();
						
					}
	
					@Override
					public void onError(int errorCode, String message) {
						((CitybilityApplication)getApplication()).stopLoading();
						CityUtils.showErrorAlert(LoginActivity.this, getString(R.string.msg_error) , message);
					}
	
				}).login(email, password);
			} else {
				((CitybilityApplication) getApplication()).stopLoading();
			}
			break;
		case R.id.accountAction:
			Intent registerIntent = new Intent(this, SignupActivity.class);
			startActivity(registerIntent);
			break;
		case R.id.passwordForgottenAction:
			FragmentManager fm = getFragmentManager();
			ForgottenPasswordFragment editNameDialog = new ForgottenPasswordFragment(this);
			editNameDialog.show(fm, "fragment_edit_name");
			break;
		}

	}
	

	private boolean validateForm() {
		Boolean isFocused = false;

		EditText regEmailField = ViewUtils.getEditText(this, R.id.lgEmailField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this,regEmailField, !isFocused);
		if(!isFocused) // show the first error on email field
			isFocused |= !ViewUtils.validateEmailField(this,regEmailField, !isFocused);
				
		EditText regPasswordField = ViewUtils.getEditText(this, R.id.lgPasswordField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this,regPasswordField, !isFocused);

		return !isFocused;
	}

	

}
