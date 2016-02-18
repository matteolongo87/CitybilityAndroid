package com.citybility.app.request;

import java.util.Date;

import net.citybility.connect.APIWrapper;

import org.json.JSONObject;

import android.app.Activity;

import com.citybility.app.request.AsyncRequest.OnRequestResultListener;

public class CitybilityRequest {

	private OnRequestResultListener mListener;
	private Activity mActivity;

	public CitybilityRequest() {
	}

	public CitybilityRequest(Activity activity, OnRequestResultListener listener) {

		this.mActivity = activity;
		
		try {
			this.mListener = (OnRequestResultListener) listener;
		} catch (ClassCastException e) {
			throw new ClassCastException(listener.toString() + " must implement OnRequestResultListener");
		}
	}

	public void signup(final Date birthday, final String email, final String password, final String name, final String surname, final String gender, final String phone, final String base64ThumbNail) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().signup(birthday, email, password, name, surname, gender, phone, base64ThumbNail);
			}
		}.execute();

	}

	public void citybilitySave(final String base64ThumbNail) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybilitySave(base64ThumbNail);
			}
		}.execute();

	}

	public void signup(final String facebookAccessToken, final int provider) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().signup(facebookAccessToken, provider);
			}
		}.execute();

	}

	public void login(final String email, final String password) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().login(email, password);
			}

		}.execute();
	}

	public void forgotPassword(final String email) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().forgotPassword(email);
			}

		}.execute();
	}

	
	public void campaignsLocations(final long categoryId, final double[] gps, final int distance, final int mode, final int numToSkip, final int numToTake, final String pattern) {
		new AsyncRequest(mActivity, mListener) {
			
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignsLocations(categoryId, gps, distance, mode, numToSkip, numToTake, pattern);
			}

		}.execute();
	}

	public void campaignLocation(final long locationId, final double[] gps) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignLocation(locationId, gps);
			}

		}.execute();
	}
	
	public void campaignLocationFollow(final long locationId) {
		campaignLocationFollow(0, locationId);
	}

	public void campaignLocationFollow(final long citybiliterId, final long locationId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignLocationFollow(citybiliterId, locationId);
			}

		}.execute();
	}

	public void campaignLocationUnfollow(final long locationId) {
		campaignLocationUnfollow(0, locationId);
	}

	public void campaignLocationUnfollow(final long citybiliterId, final long locationId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignLocationUnfollow(citybiliterId, locationId);
			}

		}.execute();
	}

	public void campaignLocationCheckFollowing(final long locationId) {
		campaignLocationCheckFollowing(0, locationId);
	}
	
	public void campaignLocationCheckFollowing(final long citybiliterId, final long locationId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignLocationCheckFollowing(citybiliterId, locationId);
			}

		}.execute();
	}
	
	public void campaignLocationSearch(final String pattern) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().campaignLocationSearch(pattern);
			}

		}.execute();
		
	}
	
	public void categories() {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().categories();
			}

		}.execute();
	}

	public void initiatives(final double[] gps, final int distance, final int mode, final int numToSkip, final int numToTake, final String pattern) {
			new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiatives(gps, distance, mode, numToSkip, numToTake, pattern);
			}

		}.execute();
	}
	
	public void initiative(final long initiativeId, final double[] gps) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiative(initiativeId, gps);
			}

		}.execute();
	}

	public void initiativeFollow(final long initiativeId) {
		 initiativeFollow(0, initiativeId);
	}
		
	public void initiativeFollow(final long citybiliterId, final long initiativeId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiativeFollow(citybiliterId, initiativeId);
			}

		}.execute();
	}

	public void initiativeUnfollow(final long initiativeId) {
		 initiativeUnfollow(0, initiativeId);
	}
	
	public void initiativeUnfollow(final long citybiliterId, final long initiativeId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiativeUnfollow(citybiliterId, initiativeId);
			}

		}.execute();
	}

	public void initiativeCheckFollowing(final long initiativeId) {
		initiativeCheckFollowing(0, initiativeId);
	}

	public void initiativeCheckFollowing(final long citybiliterId, final long initiativeId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiativeCheckFollowing(citybiliterId, initiativeId);
			}

		}.execute();
	}
	
	
	public void initiativeLocationSearch(final String pattern) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().initiativeSearch(pattern);
			}

		}.execute();
		
	}
	

	public void citybiliters(final double[] gps, final int distance, final int mode, final int numToSkip, final int numToTake, final String pattern) {
			new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliters( gps, distance,  mode, numToSkip, numToTake, pattern);
			}

		}.execute();
	}

	public void citybiliter(final long citybiliterId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliter(citybiliterId);
			}

		}.execute();
	}


	public void citybiliterFollowings(final long citybiliterId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterFollowings(citybiliterId);
			}

		}.execute();
	}


	public void citybiliterFollowers(final long citybiliterId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterFollowers(citybiliterId);
			}

		}.execute();
	}


	public void citybiliterFollow(final long followId) {
		citybiliterFollow(0, followId);
	}

	public void citybiliterFollow(final long citybiliterId, final long followId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterFollow(citybiliterId, followId);
			}

		}.execute();
	}

	public void citybiliterUnfollow(final long citybiliterId) {
		citybiliterUnfollow(0, citybiliterId);
	}
	
	public void citybiliterUnfollow(final long followId, final long citybiliterId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterUnfollow(followId, citybiliterId);
			}

		}.execute();
	}


	public void citybiliterCheckFollowing(final long followId) {
		citybiliterCheckFollowing(0, followId);
	}
	
	public void citybiliterCheckFollowing(final long citybiliterId, final long followId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterCheckFollowing(citybiliterId, followId);
			}

		}.execute();
	}
	
	public void citybiliterSearch(final String pattern) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterSearch(pattern);
			}

		}.execute();
	}


	public void citybiliterSupported(final long citybiliterId, final int numToSkip, final int numToTake) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().citybiliterSupported(citybiliterId, numToSkip, numToTake);
			}

		}.execute();
	}


	/**
	 * 
	 * @param status  -1=All, 0=New, 1= Read
	 */
	public void userMessages(final int status) {
			new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().userMessages(status);
			}

		}.execute();
	}

	public void userMessage(final long messageId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().userMessage(messageId);
			}

		}.execute();
	}
	
	public void userMessageRead(final long messageId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().userMessageRead(messageId);
			}

		}.execute();
	}
	
	public void userMessageDelete(final long messageId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().userMessageDelete(messageId);
			}

		}.execute();
	}

	public void profile() {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().profile();
			}

		}.execute();
	}

	public void logout() {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().logout();
			}

		}.execute();
	}
	

	public void verifyCode(final String qrcode) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().verifyCode(qrcode);
			}

		}.execute();
	}

	public void registerTransaction(final Activity activity, final String qrcode, final String token,  final long citybiliterId) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().registerTransaction(activity, qrcode, token, citybiliterId);
			}

		}.execute();
		
	}
	
	public void shareMessage(final long senderId, final long type ) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().shareMessage(senderId, type);
			}

		}.execute();
		
	}
	
	public void inviteMessage(final long senderId, final long type) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().inviteMessage(senderId, type);
			}

		}.execute();
	}
	
	/**
	 * 
	 * @param movementId
	 * @param problem 0|1, <-- 0=nessun problema 1=problemi
	 */
	public void postFeedback(final long movementId, final int problem, final String comment) {
		new AsyncRequest(mActivity, mListener) {
			@Override
			protected JSONObject doInBackground(Void... params) {
				super.doInBackground();
				return APIWrapper.getInstance().postFeedback(movementId, problem, comment);
			}

		}.execute();
	}
	
	
	
}
