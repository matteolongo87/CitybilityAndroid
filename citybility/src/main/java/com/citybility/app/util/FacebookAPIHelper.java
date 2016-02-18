package com.citybility.app.util;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.citybility.app.CitybilityApplication;
import com.citybility.app.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class FacebookAPIHelper {
   private final String TAG = this.getClass().getName();
      
   public enum ShareType {
       NONE,
       SHARE_LINK,
       SHARE_LINK_SILENT,
       SHARE_PHOTO,
       SHARE_PHOTO_SILENT,
       SHARE_STATUS_UPDATE,
       SHARE_STATUS_UPDATE_SILENT,
       SHARE_STORY,
       SHARE_STORY_SILENT
   }
   

   private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "publish_actions");
   private static final String PUBLISH_PERMISSION = "publish_actions";
    
  // private ProgressDialog progressDialog;

	private Activity mActivity;
	private CallbackManager mCallbackManager;
	
	
	public FacebookAPIHelper(Activity act, CallbackManager facebookCallback) {
		this.mActivity = act;
		this.mCallbackManager = facebookCallback;
	}
	
	


	   /***********************************************************************
	    * Facebook Callbacks
	    ***********************************************************************/
		@SuppressWarnings("hiding")
		private class  FacebookLoginCallback<LoginResult> implements FacebookCallback<LoginResult> {

			private ShareType mShareAction;
			private JSONObject mShareObject;
			FacebookCallback<Sharer.Result> mCallback;
			
			public FacebookLoginCallback(ShareType shareAction, JSONObject shareObject, FacebookCallback<Sharer.Result> callback) {
				super();
				this.mShareAction = shareAction;
				this.mShareObject = shareObject;
				this.mCallback = callback;
			}

			@Override
			public void onSuccess(LoginResult result) {
				shareContent(mShareAction, mShareObject, mCallback);
			}

			@Override
			public void onCancel() {
				((CitybilityApplication) mActivity.getApplication()).stopLoading();
				Log.d(TAG,"login annullata");
			}
	
			@Override
			public void onError(FacebookException error) {
				((CitybilityApplication) mActivity.getApplication()).stopLoading();
				CityUtils.showErrorAlert(mActivity, mActivity.getString(R.string.msg_error), mActivity.getString(R.string.fb_login_error_msg));
				Log.e(TAG, "Facebook login error message: " + error.getMessage());
				Log.e(TAG, "Facebook login error stack: " + error.getStackTrace());
				error.printStackTrace();

			}
				
		};
		
		private FacebookCallback<Sharer.Result> defalutFacebookShareCallback =  new FacebookCallback<Sharer.Result>() {

			@Override
			public void onSuccess(Sharer.Result result) {
				((CitybilityApplication) mActivity.getApplication()).stopLoading();     
				if(result != null && result.getPostId() != null && !result.getPostId().isEmpty()){
					CityUtils.showInfoAlert(mActivity, mActivity.getString(R.string.msg_success), mActivity.getString(R.string.fb_publish_success_msg)); 
				} 
			}

			@Override
			public void onCancel() {
				((CitybilityApplication) mActivity.getApplication()).stopLoading();
				Log.d(this.getClass().getName(), "Condivisione annullata");
			}

			@Override
			public void onError(FacebookException error) { 
				((CitybilityApplication) mActivity.getApplication()).stopLoading();
		        CityUtils.showErrorAlert(mActivity, mActivity.getString(R.string.msg_error), mActivity.getString(R.string.fb_publish_error_msg));
				Log.e(this.getClass().getName(), error.getMessage());
				Log.e(this.getClass().getName(), Arrays.toString(error.getStackTrace()));
			}

		};
	
	
	 /***********************************************************************
     * Public utility methods
	 * @param action 
	 * @param shareObject the json object containing:
	 * 	- "Picture": the URL of thumbnail image that will appear on the post
	 * 	- "Url"  the link to be shared
	 * 	- "Caption" the title of the content in the link
	 * 	- "Description" the escription of the content, usually 2-4 sentences
	 * @param callback
	 *            the FacebookCallback
     ***********************************************************************/
   
	public void loginAndShareContent(ShareType action,JSONObject shareObject, FacebookCallback<Sharer.Result> callback) {
		LoginManager.getInstance().registerCallback(this.mCallbackManager, new FacebookLoginCallback<LoginResult>(action, shareObject, callback));
		LoginManager.getInstance().logInWithPublishPermissions(this.mActivity, Arrays.asList(PUBLISH_PERMISSION));
	}

	 /**
	  * @param shareObject the json object containing:
	  * 	- "Picture": the URL of thumbnail image that will appear on the post
	  * 	- "Url"  the link to be shared
	  * 	- "Caption" the title of the content in the link
	  * 	- "Description" the escription of the content, usually 2-4 sentences
	 * @throws JSONException 
	  */
	   public void postLink(JSONObject shareObject, FacebookCallback<Sharer.Result> callback) throws JSONException {
		 this.shareContent(ShareType.SHARE_LINK, shareObject, callback!=null ? callback : defalutFacebookShareCallback);
	   }

	 
	 /**
	  * Post a photo showing the facebook Share or Feed Dialog
	  * @param shareObject a json object containing :
	  * 	- "Picture": the URL of thumbnail image that will appear on the post
	  * 	- "Url"  the link to be shared
	  * 	- "Caption" the title of the content in the link
	  * 	- "Description" the escription of the content, usually 2-4 sentences
	  * @param callback
	  * @throws JSONException
	  */
	   public void postPhoto(JSONObject shareObject, FacebookCallback<Sharer.Result> callback) throws JSONException {
		   this.shareContent(ShareType.SHARE_PHOTO, shareObject, callback!=null ? callback : defalutFacebookShareCallback);
	   }

	/**
	 * Post a content showing the facebook Share or Feed Dialog
	 * 
	 * @param action
	 * @param shareObject a json object containing : 
	 * 	- "Picture": the URL of thumbnail image that will appear on the post
	 * 	- "Url"  the link to be shared
	 * 	- "Caption" the title of the content in the link
	 * 	- "Description" the escription of the content, usually 2-4 sentences
	 * @param callback the FacebookCallback
	 */
	@SuppressWarnings("serial")
	public void shareContent(ShareType action, JSONObject shareObject, FacebookCallback<Sharer.Result> callback) {

		try {
			if (this.isLoggedIn() && hasPublishPermission()) {

				ShareDialog shareDialog = new ShareDialog(this.mActivity);
				// this part is optional
				shareDialog.registerCallback(this.mCallbackManager, callback);

				if (!ShareDialog.canShow(ShareLinkContent.class)) {

					((CitybilityApplication) mActivity.getApplication()).stopLoading();
					CityUtils.showErrorAlert(mActivity, mActivity.getString(R.string.msg_error), mActivity.getString(R.string.fb_publish_error_msg));

				} else {

					switch (action) {
					case SHARE_LINK:
						ShareLinkContent linkShareContent = new ShareLinkContent.Builder()
							.setContentUrl((shareObject.has("Url") ? Uri.parse(shareObject.getString("Url")) : Uri.parse("")))
							.setImageUrl(shareObject.has("Picture") ? Uri.parse(shareObject.getString("Picture")) : null)
							.setContentTitle(shareObject.has("Caption") ? shareObject.getString("Caption") : "")
							.setContentDescription(shareObject.has("Text") ? shareObject.getString("Text") : "")
							.build();
						shareDialog.show(linkShareContent);
						break;
					case SHARE_PHOTO:
						SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(shareObject.getString("Picture"))).build();
						SharePhotoContent photoShareContent = new SharePhotoContent.Builder().addPhoto(photo).setContentUrl(Uri.parse(shareObject.getString("Url"))).build();
						shareDialog.show(photoShareContent);
						break;
					case SHARE_LINK_SILENT:
					case SHARE_PHOTO_SILENT:
					case SHARE_STATUS_UPDATE:
					case SHARE_STATUS_UPDATE_SILENT:
					case SHARE_STORY:
					case SHARE_STORY_SILENT:
						// TODO
						break;
					default:
						// none
						break;
					}

				}

			} else { // if(this.isLoggedIn() && hasPublishPermission(){
				this.loginAndShareContent(action, shareObject, callback);
			}
		} catch (Exception e) {
			((CitybilityApplication) mActivity.getApplication()).stopLoading();
			CityUtils.showErrorAlert(mActivity, mActivity.getString(R.string.msg_error), mActivity.getString(R.string.fb_publish_error_msg));
			e.printStackTrace();
		}
	}


  
	
   /***********************************************************************
    * Private utility methods
    ***********************************************************************/

   private boolean isLoggedIn() {
       return AccessToken.getCurrentAccessToken() != null;
   }


   private boolean hasPublishPermission() {
	   AccessToken accessTocken = AccessToken.getCurrentAccessToken();
       return accessTocken != null && accessTocken.getPermissions().contains(PUBLISH_PERMISSION);
   }
  

	/***********************************************************************
	 * InnerClass
	 ***********************************************************************/
	


}
