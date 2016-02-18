package com.citybility.app;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import net.citybility.connect.CitybilityConnectApplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.citybility.app.custom.GreenDatePicker;
import com.citybility.app.custom.RoundedImageView;
import com.citybility.app.request.AsyncRequest.OnRequestResultListener;
import com.citybility.app.request.CitybilityRequest;
import com.citybility.app.util.BitmapUtils;
import com.citybility.app.util.CityUtils;
import com.citybility.app.util.Constant;
import com.citybility.app.util.ImagePickUpUtil;
import com.citybility.app.util.ViewUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class SignupActivity extends CitybilityBaseActivity implements OnClickListener {

	private String mGenter;

	private RoundedImageView profilePhoto;
	private Button btnMale, btnFemale;
	private Bitmap mProfileImage;

	private String[] PERMISSION = { "email", "user_birthday" };

	private String mEmail;
	private String mPassword;
	private TextView birthDateField;

	private CallbackManager mCallbackManager;

	private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
		
		@Override
		public void onSuccess(LoginResult loginResult) {

			((CitybilityApplication) getApplication()).startLoading(SignupActivity.this);   
			// App code
			try {
				String accessToken = loginResult.getAccessToken().getToken();
				
				new CitybilityRequest(SignupActivity.this, new OnRequestResultListener() {

					@Override
					public void onSuccess(JSONObject result) {

						new CitybilityRequest(SignupActivity.this, new OnRequestResultListener() {

							@Override
							public void onSuccess(JSONObject result) {
								if (result.has("Citybiliter")) {
									try {
										JSONObject user = result.getJSONObject("Citybiliter");
										CitybilityConnectApplication.setCitibilityID(user.getLong("CitybiliterId"));
										CitybilityConnectApplication.setFirstName(user.getString("FirstName"));
										CitybilityConnectApplication.setLastName(user.getString("LastName"));
										startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
										finish();
									} catch (JSONException e) {
										e.printStackTrace();
										((CitybilityApplication) getApplication()).stopLoading();
									}
								}
							}

							@Override
							public void onError(int errorCode, String message) {
								CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), message);
								((CitybilityApplication) getApplication()).stopLoading();
							}

						}).profile();

					}

					@Override
					public void onError(int errorCode, String message) {
						CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), message);
						((CitybilityApplication) getApplication()).stopLoading();
					}

				}).signup(accessToken, Constant.PROVIDER_FACEBOOK);
				

			} catch (Exception e) {
				e.printStackTrace();
				((CitybilityApplication) getApplication()).stopLoading();
			}

		}

		@Override
		public void onCancel() {
			Log.d(this.getClass().getName(), "Facebook login annullata");
			((CitybilityApplication) getApplication()).stopLoading();
		}

		@Override
		public void onError(FacebookException exception) {
			CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), getString(R.string.fb_login_error_msg));
			((CitybilityApplication) getApplication()).stopLoading();
			Log.e(this.getClass().getName(), "Facebook login error message: " + exception.getMessage());
			Log.e(this.getClass().getName(), "Facebook login error stack: " + exception.getStackTrace());
			exception.printStackTrace();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());

		setContentView(R.layout.activity_signup);

		btnMale = (Button) findViewById(R.id.grpBtnMale);
		btnMale.setOnClickListener(this);

		btnFemale = (Button) findViewById(R.id.grpBtnFemale);
		btnFemale.setOnClickListener(this);

		profilePhoto = (RoundedImageView) findViewById(R.id.profilePhoto);
		profilePhoto.setOnClickListener(this);

		findViewById(R.id.btnSignUp).setOnClickListener(this);

		TextView agreementsInfo = (TextView) findViewById(R.id.agreementsInfo);

		this.setupAgreementsInfoTextView(agreementsInfo);

		birthDateField = (TextView) findViewById(R.id.regBirthDateField);
		birthDateField.setFocusable(false);
		birthDateField.setFocusableInTouchMode(false);
		birthDateField.setKeyListener(null);
		birthDateField.setOnClickListener(this);

		mCallbackManager = CallbackManager.Factory.create();
		LoginButton loginButton = (LoginButton) findViewById(R.id.btnFacebookLogin);
		loginButton.setReadPermissions(Arrays.asList(PERMISSION));
		loginButton.registerCallback(mCallbackManager, mFacebookCallback);
		loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {

		case R.id.grpBtnMale:
			setGenter("M");
			break;

		case R.id.grpBtnFemale:
			setGenter("F");
			break;

		case R.id.btnSignUp:
			try {

				((CitybilityApplication) getApplication()).startLoading(this);

				if(validateForm()) {
					
					Date birthday = ViewUtils.getEditTextDate(this, R.id.regBirthDateField);
					mEmail = ViewUtils.getEditTextText(this, R.id.regEmailField);
					mPassword = ViewUtils.getEditTextText(this, R.id.regPasswordField);
					String name = ViewUtils.getEditTextText(this, R.id.regNameField);
					String surname = ViewUtils.getEditTextText(this, R.id.regSurnameField);
					String gender = this.mGenter;
					String phone = ViewUtils.getEditTextText(this, R.id.regPhoneField);
					String image = mProfileImage != null ? BitmapUtils.encodeTobase64(mProfileImage) : null;
	
					new CitybilityRequest(this, new OnRequestResultListener() {
	
						@Override
						public void onSuccess(JSONObject result) {
	
							CityUtils.showInfoAlert(SignupActivity.this, getString(R.string.msg_success), getString(R.string.reg_success_msg), new DialogInterface.OnClickListener() {
	
								@Override
								public void onClick(DialogInterface dialog, int which) {
									autoLogin(mEmail, mPassword);
								}
	
							});
						}
	
						@Override
						public void onError(int errorCode, String message) {
							if(message.toLowerCase().contains("mail")){
								EditText regEmailField = ViewUtils.getEditText(SignupActivity.this, R.id.regEmailField);
								regEmailField.setError(message);
								regEmailField.setFocusable(true);
								regEmailField.setFocusableInTouchMode(true);
								regEmailField.requestFocus();
							} else {
							CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), message);
							}
							((CitybilityApplication) getApplication()).stopLoading();
						}
	
					}).signup(birthday, mEmail, mPassword, name, surname, gender, phone, image);
				} else {
					((CitybilityApplication) getApplication()).stopLoading();
				}

			} catch (ParseException e) {
				((CitybilityApplication) getApplication()).stopLoading();
				CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), getString(R.string.generic_error_msg));
				e.printStackTrace();
			}
			break;

		case R.id.profilePhoto:
			ImagePickUpUtil.openMediaSelector(this);
			break;

		case R.id.agreementsInfo:
			CityUtils.showWebAlert(this, getString(R.string.reg_cb_policy_title), Constant.PRIVACY_POLICY_URL, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}

			});
			break;

		case R.id.regBirthDateField:
			// this.showDatePickerDialog(findViewById(R.id.regBirthDateField));
			this.showDatePicker();
			break;
		}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(CitybilityConnectApplication.isLoggedIn()){
	    	startActivity(new Intent(getApplicationContext(), CityUtils.DEFAULT_ACTIVITY_CLASS));
	    }	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		mCallbackManager.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case Constant.PICTURE_TAKEN_FROM_GALLERY:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap tmpBitmap = BitmapUtils.decodeSampledBitmapFromGallery(data, Constant.SQUARE_THUMBNAIL_DIM, Constant.SQUARE_THUMBNAIL_DIM, getContentResolver());
					setPhoto(tmpBitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case Constant.PICTURE_TAKEN_FROM_CAMERA:
			if (resultCode == RESULT_OK) {
				if (data.hasExtra("data")) {
					Bitmap tmpBitmap = BitmapUtils.decodeSampledBitmapFromCamera(data);
					setPhoto(tmpBitmap);
				}
			}

			break;
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	private void setPhoto(Bitmap photoBitmap) {

		if (photoBitmap != null) {
			mProfileImage = BitmapUtils.createSquareThumbnail(photoBitmap, Constant.SQUARE_THUMBNAIL_DIM);
			if (mProfileImage != null) {
				profilePhoto.setShape(RoundedImageView.CIRCULAR_SHAPE);
				profilePhoto.setImageBitmap(mProfileImage);
			}
			photoBitmap.recycle();
		} else {
			CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), getString(R.string.taking_from_camera_error));
		}
	}

	private void setupAgreementsInfoTextView(TextView view) {

		ForegroundColorSpan serviceSpan = new ForegroundColorSpan(getResources().getColor(R.color.citybility_green));

		ForegroundColorSpan privacySpan = new ForegroundColorSpan(getResources().getColor(R.color.citybility_green));

		Spannable spannableAgInfo = new SpannableString(view.getText());

		spannableAgInfo.setSpan(serviceSpan, 33, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableAgInfo.setSpan(privacySpan, 56, 81, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		view.setText(spannableAgInfo);
		view.setMovementMethod(LinkMovementMethod.getInstance());
		view.setOnClickListener(this);
	}

	private void setGenter(String gender) {

		mGenter = gender;

		if (gender.equalsIgnoreCase("M")) {

			btnMale.setBackgroundResource(R.drawable.group_button_left_pressed);
			btnMale.setTextColor(getResources().getColor(android.R.color.white));

			btnFemale.setBackgroundResource(R.drawable.group_button_right);
			btnFemale.setTextColor(getResources().getColor(R.color.citybility_green));
			
			btnMale.setError(null);
			btnFemale.setError(null);

		} else if (gender.equalsIgnoreCase("F")) {

			btnFemale.setBackgroundResource(R.drawable.group_button_right_pressed);
			btnFemale.setTextColor(getResources().getColor(android.R.color.white));

			btnMale.setBackgroundResource(R.drawable.group_button_left);
			btnMale.setTextColor(getResources().getColor(R.color.citybility_green));
			
			btnMale.setError(null);
			btnFemale.setError(null);

		}

	}

	private void autoLogin(String email, String password) {
		new CitybilityRequest(this, new OnRequestResultListener() {

			@Override
			public void onSuccess(JSONObject result) {
				new CitybilityRequest(SignupActivity.this, new OnRequestResultListener() {

					@Override
					public void onSuccess(JSONObject result) {
						if (result.has("Citybiliter")) {
							try {
								JSONObject user = result.getJSONObject("Citybiliter");
								CitybilityConnectApplication.setCitibilityID(user.getLong("CitybiliterId"));
								CitybilityConnectApplication.setFirstName(user.getString("FirstName"));
								CitybilityConnectApplication.setLastName(user.getString("LastName"));
								startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
								finish();
							} catch (JSONException e) {
								((CitybilityApplication) getApplication()).stopLoading();
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onError(int errorCode, String message) {
						((CitybilityApplication) getApplication()).stopLoading();
						CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), message);
					}

				}).profile();

			}

			@Override
			public void onError(int errorCode, String message) {
				((CitybilityApplication) getApplication()).stopLoading();
				CityUtils.showErrorAlert(SignupActivity.this, getString(R.string.msg_error), message);
			}

		}).login(email, password);

	}

	private boolean validateForm() {
		Boolean isFocused = false;
		EditText nameField = ViewUtils.getEditText(this, R.id.regNameField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this, nameField, !isFocused);

		EditText surnameField = ViewUtils.getEditText(this, R.id.regSurnameField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this, surnameField, !isFocused);

		EditText regEmailField = ViewUtils.getEditText(this, R.id.regEmailField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this, regEmailField, !isFocused);

		EditText regPasswordField = ViewUtils.getEditText(this, R.id.regPasswordField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this, regPasswordField, !isFocused);

		EditText regBirthDateField = ViewUtils.getEditText(this, R.id.regBirthDateField);
		isFocused |= !ViewUtils.validateMandatoryTextField(this, regBirthDateField, !isFocused);

		if (this.mGenter == null || this.mGenter.isEmpty()) {
			btnFemale.setError(getString(R.string.form_mandatory_field_missing));
			if (!isFocused) {
				btnFemale.setFocusable(true);
				btnFemale.setFocusableInTouchMode(true);
				btnFemale.requestFocus();
				isFocused = true;
			}
		}

		return !isFocused;
	}


	/**
	 * show custom DatePicker
	 */
	private void showDatePicker() {

		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		View customView = inflater.inflate(R.layout.date_picker_dialog, null);
		dialogBuilder.setView(customView);

		final GreenDatePicker datePicker = (GreenDatePicker) customView.findViewById(R.id.dialog_datepicker);
		final AlertDialog dialog = dialogBuilder.create();

		// Initialize datepicker in dialog atepicker
		Calendar choosenDate = Calendar.getInstance();
		datePicker.init(choosenDate.get(Calendar.YEAR), choosenDate.get(Calendar.MONTH), choosenDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar choosenDate = Calendar.getInstance();
				choosenDate.set(year, monthOfYear, dayOfMonth);
			}
		});

		Button endBtn = (Button) customView.findViewById(R.id.btnEnd);
		endBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String dateFormat = "%02d/%02d/%4d";
				birthDateField.setText(String.format(dateFormat, datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear()));
				birthDateField.setError(null);
				dialog.dismiss();
			}
		});

		// Finish
		dialog.show();
	}

}
