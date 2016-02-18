package net.citybility.connect;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import net.citybility.dao.Config;
import net.citybility.dao.ConfigDao;
import net.citybility.services.CommunicationManagerService;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class APIWrapper {

	//private static final String baseUrl = "http://devservices.citybility.net/CitybilityService.svc/";
	private static final String baseUrl = "http://stageservices.citybility.net/CitybilityService.svc/";
											 
	private static final String signupUrl = baseUrl + "Signup";
	private static final String forgotPasswordUrl = baseUrl + "ForgotPassword";
	private static final String loginUrl = baseUrl + "Login";
	private static final String logoutUrl = baseUrl + "Logout";
	
	private static final String categoriesUrl = baseUrl + "Categories";
	private static final String campaignsLocationsUrl = baseUrl + "CampaignsLocations";
	private static final String campaignLocationUrl = baseUrl + "CampaignLocation";
	private static final String campaignLocationSearchUrl = baseUrl + "CampaignLocation/Search";
	private static final String campaignLocationFollowUrl = baseUrl + "CampaignLocation/Follow";
	private static final String campaignLocationUnfollowUrl = baseUrl + "CampaignLocation/Unfollow";
	private static final String campaignLocationCheckFollowingUrl = baseUrl + "CampaignLocation/CheckFollowing";
	
	private static final String citybilitersUrl = baseUrl + "Citybiliters";
	private static final String citybiliterUrl = baseUrl + "Citybiliter";
	private static final String citybiliterSearchUrl = baseUrl + "Citybiliter/Search";
	private static final String citybiliterSaveUrl = baseUrl + "Citybiliter/Save";
	private static final String citybiliterFollowUrl = baseUrl + "Citybiliter/Follow";
	private static final String citybiliterUnfollowUrl = baseUrl + "Citybiliter/Unfollow";
	private static final String citybiliterFollowingsUrl = baseUrl + "Citybiliter/Followings";
	private static final String citybiliterFollowersUrl = baseUrl + "Citybiliter/Followers";
	private static final String citybiliterSupportedUrl = baseUrl + "Citybiliter/Supported";
	private static final String citybiliterCheckFollowingUrl = baseUrl + "Citybiliter/CheckFollowing";
	
	
	private static final String initiativesUrl = baseUrl + "Initiatives";
	private static final String initiativeUrl = baseUrl + "Initiative";
	private static final String initiativeSearchUrl = baseUrl + "Initiative/Search";
	private static final String initiativeFollowUrl = baseUrl + "Initiative/Follow";
	private static final String initiativeUnfollowUrl = baseUrl + "Initiative/Unfollow";
	private static final String initiativeCheckFollowingUrl = baseUrl + "Initiative/CheckFollowing";

	private static final String userMessagesUrl = baseUrl + "UserMessages";
	private static final String userMessageUrl = baseUrl + "UserMessage";
	private static final String userMessageReadUrl = baseUrl + "UserMessage/Read";
	private static final String userMessageDeleteUrl = baseUrl + "UserMessage/Delete";
	
	private static final String verifyCodeUrl = baseUrl + "VerifyCode";
	private static final String registerTransactionUrl = baseUrl + "RegisterTransaction";
	

	private static final String inviteMessageUrl = baseUrl + "inviteMessage";
	private static final String shareMessageUrl = baseUrl + "shareMessage";
	private static final String postFeedbackUrl  = baseUrl + "postFeedback";
	
	private static final int platform = 3;
	private static final int provider = 0;
	private static APIWrapper instance = null;
	private JSONObject query;
	private static final JSONRestClient jsonRestClient = JSONRestClient.getInstance();
	private static final ConfigDao configDao = DaoHelper.getIstance(CitybilityConnectApplication.getInstance()).getConfigDao();
	private static Credentials credentials;
	
	protected APIWrapper() {
		// No instantiation
	}

	public static APIWrapper getInstance() {
		if (instance == null)
			instance = new APIWrapper();

		return instance;
	}

	public JSONObject signup(Date birthday, String email, String password, String name, String surname, String gender, String phone, String base64Thumbnail) {
		Long birthdayEpoch = (birthday!=null) ? birthday.getTime() / 1000:-1;
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("BirthDateUnix", (birthdayEpoch>=0)?birthdayEpoch:0);
			query.put("Email", email);
			query.put("Password", md5sum(password));
			query.put("Name", name);
			query.put("Surname", surname);
			query.put("Gender", gender);
			query.put("Phone", phone);
			
			if(base64Thumbnail!=null && !base64Thumbnail.isEmpty())
				query.put("Thumbnail", base64Thumbnail);
			
			result = jsonRestClient.post(signupUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	public JSONObject signup(String facebookAccessToken, int provider) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("OAuthToken", facebookAccessToken);
			query.put("Provider", provider);
			
			result = jsonRestClient.post(signupUrl, query);
			
			if (result.getInt("Status") == 0) {
				setIdentity(result.getString("Identity"));
			}
			else
				setIdentity("");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybilitySave(String base64Thumbnail) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Thumbnail", base64Thumbnail);
			query.put("Identity", getIdentity());
			query.put("Self", 1);
			
			result = jsonRestClient.post(citybiliterSaveUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	
	public JSONObject login(String email, String password) {
		JSONObject result = new JSONObject();
		
		boolean newCredentials = true;
		
		if (email == null || password == null) {
			credentials = getCredentials();
			email = credentials.getEmail();
			password = credentials.getPassword();
			newCredentials = false;
		}
		
		try {
			query = basicQuery();
			query.put("Email", email);
			query.put("Password", md5sum(password));
			
			result = jsonRestClient.post(loginUrl, query);
			
			if (result.getInt("Status") == 0) {
				if (newCredentials) 
					setCredentials(email, password);
				setIdentity(result.getString("Identity"));
			}
			else
				setIdentity("");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject forgotPassword(String email){
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Email", email);
			
			result = jsonRestClient.post(forgotPasswordUrl, query);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject categories() {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			
			result = doRequestAndLoginIfNeeded(categoriesUrl, query);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject campaignsLocations(long categoryId, double[] gps, int distance, int mode, int numToSkip, int numToTake, String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("CategoryId", categoryId);
			if (gps != null && gps.length > 0) {
				query.accumulate("GPS", gps[0]);
				query.accumulate("GPS", gps[1]);
			}
			query.put("Distance", distance);
			query.put("Mode", mode);
			query.put("NumToSkip", numToSkip);
			query.put("NumToTake", numToTake);
			if (pattern != null && pattern.length() > 0)
				query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(campaignsLocationsUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject campaignLocation(long locationId, double[] gps) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("LocationId", locationId);

			if (gps != null && gps.length > 0) {
				query.accumulate("GPS", gps[0]);
				query.accumulate("GPS", gps[1]);
			}

			result = doRequestAndLoginIfNeeded(campaignLocationUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject campaignLocationFollow(long citibiliterId, long locationId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("LocationId", locationId);
			
			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			result = doRequestAndLoginIfNeeded(campaignLocationFollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject campaignLocationUnfollow(long citibiliterId, long locationId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("LocationId", locationId);

			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			result = doRequestAndLoginIfNeeded(campaignLocationUnfollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject campaignLocationCheckFollowing(long citibiliterId, long locationId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("LocationId", locationId);

			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			
			result = doRequestAndLoginIfNeeded(campaignLocationCheckFollowingUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject campaignLocationSearch(String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(campaignLocationSearchUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject initiatives(double[] gps, int distance, int mode, int numToSkip, int numToTake, String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			if (gps != null && gps.length > 0) {
				query.accumulate("GPS", gps[0]);
				query.accumulate("GPS", gps[1]);
			}
			query.put("Distance", distance);
			query.put("Mode", mode);
			query.put("NumToSkip", numToSkip);
			query.put("NumToTake", numToTake);
			if (pattern != null && pattern.length() > 0)
				query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(initiativesUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject initiative(long initiativeId, double[] gps) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("InitiativeId", initiativeId);
			if (gps != null && gps.length > 0) {
				query.accumulate("GPS", gps[0]);
				query.accumulate("GPS", gps[1]);
			}

			result = doRequestAndLoginIfNeeded(initiativeUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject initiativeSearch(String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(initiativeSearchUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject initiativeFollow(long citibiliterId, long initiativeId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("InitiativeId", initiativeId);

			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			result = doRequestAndLoginIfNeeded(initiativeFollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject initiativeUnfollow(long citibiliterId, long initiativeId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("InitiativeId", initiativeId);
			
			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			result = doRequestAndLoginIfNeeded(initiativeUnfollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	

	public JSONObject initiativeCheckFollowing(long citibiliterId, long initiativeId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("InitiativeId", initiativeId);
			
			if(citibiliterId > 0){
				query.put("CitybiliterId", citibiliterId);	
			} else {
				query.put("Self", 1);
			}
			
			result = doRequestAndLoginIfNeeded(initiativeCheckFollowingUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	public JSONObject citybiliters(double[] gps, int distance, int mode, int numToSkip, int numToTake, String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			if (gps != null && gps.length > 0) {
				query.accumulate("GPS", gps[0]);
				query.accumulate("GPS", gps[1]);
			}
			query.put("Distance", distance);
			query.put("Mode", mode);
			query.put("NumToSkip", numToSkip);
			query.put("NumToTake", numToTake);

			if (pattern != null && pattern.length() > 0)
				query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(citybilitersUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliter(long citybiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("CitybiliterId", citybiliterId);
			
			result = doRequestAndLoginIfNeeded(citybiliterUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterFollowings(long citybiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Id", citybiliterId);
			
			result = doRequestAndLoginIfNeeded(citybiliterFollowingsUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterFollowers(long citybiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Id", citybiliterId);
			
			result = doRequestAndLoginIfNeeded(citybiliterFollowersUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterFollow(long followId, long citibiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("CitybiliterId", citibiliterId);

			if(followId > 0){
				query.put("FollowId", followId);	
			} else {
				query.put("Self", 1);
			}		
			result = doRequestAndLoginIfNeeded(citybiliterFollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterUnfollow(long followId, long citibiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("CitybiliterId", citibiliterId);

			if(followId > 0){
				query.put("FollowId", followId);	
			} else {
				query.put("Self", 1);
			}
						
			result = doRequestAndLoginIfNeeded(citybiliterUnfollowUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterCheckFollowing(long followId, long citibiliterId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("CitybiliterId", citibiliterId);

			if(followId > 0){
				query.put("FollowId", followId);	
			} else {
				query.put("Self", 1);
			}
						
			result = doRequestAndLoginIfNeeded(citybiliterCheckFollowingUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	

	public JSONObject citybiliterSearch(String pattern) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Query", pattern);
			
			result = doRequestAndLoginIfNeeded(citybiliterSearchUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject citybiliterSupported(long citybiliterId, int numToSkip, int numToTake) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Id", citybiliterId);
			query.put("NumToSkip", numToSkip);
			query.put("NumToTake", numToTake);
			
			result = doRequestAndLoginIfNeeded(citybiliterSupportedUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	public JSONObject profile() {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Self", 1);
			
			result = doRequestAndLoginIfNeeded(citybiliterUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	/**
	 * 
	 * @param status  -1=All, 0=New, 1= Read
	 * @return
	 */
	public JSONObject userMessages(int status) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Self", 1);
			query.put("Status", status);
			result = doRequestAndLoginIfNeeded(userMessagesUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject userMessage(long messageId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("MessageId", messageId);
			
			result = doRequestAndLoginIfNeeded(userMessageUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject userMessageRead(long messageId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("MessageId", messageId);
			
			result = doRequestAndLoginIfNeeded(userMessageReadUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject userMessageDelete(long messageId) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("MessageId", messageId);
			
			result = doRequestAndLoginIfNeeded(userMessageDeleteUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	
	
	public JSONObject logout() {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			
			result = jsonRestClient.post(logoutUrl, query);
			
			if (result.getInt("Status") == 0){
				setIdentity("");
				deleteCredentials();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject verifyCode(String qrcode) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("Qrcode", qrcode);
			
			result = doRequestAndLoginIfNeeded(verifyCodeUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject registerTransaction(Activity activity, String qrcode, String token,  long citybiliterId) {
		JSONObject result = new JSONObject();
		query = new JSONObject();
		long currentEpoch = nowTimestamp();

		try {
			query.put("TimestampUnix", currentEpoch);
			query.put("RefDateUnix", currentEpoch);
			query.put("Platform", platform);
			query.put("Provider", provider);
			query.put("Identity", getIdentity());
			query.put("CitybiliterId", citybiliterId);
			query.put("Qrcode", qrcode);
			query.put("Token", token);
			query.put("KeepAlive", 1);
			query.put("Simulation", 0);

			result = doRequestAndLoginIfNeeded(registerTransactionUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject queuedRegisterTransaction(Activity activity, String qrcode, String token,  long citybiliterId) {
		JSONObject result = new JSONObject();
		query = new JSONObject();
		long currentEpoch = nowTimestamp();

		try {
			query.put("TimestampUnix", currentEpoch);
			query.put("RefDateUnix", currentEpoch);
			query.put("Platform", platform);
			query.put("Identity", getIdentity());
			query.put("CitybiliterId", citybiliterId);
			query.put("Qrcode", qrcode);
			query.put("Token", token);
			query.put("KeepAlive", 1);
			query.put("Simulation", 0);
			
			CommunicationManagerService.makeRequest(activity, registerTransactionUrl, query, true, true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	

	public JSONObject inviteMessage(long senderId, long type) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("SenderId", senderId);
			query.put("Type", type);
			
			result = doRequestAndLoginIfNeeded(inviteMessageUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject shareMessage(long senderId, long type) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("SenderId", senderId);
			query.put("Type", type);
			
			result = doRequestAndLoginIfNeeded(shareMessageUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject postFeedback(long movementId, int problem, String comment) {
		JSONObject result = new JSONObject();

		try {
			query = basicQuery();
			query.put("Identity", getIdentity());
			query.put("MovementId", movementId);
			query.put("Problem", problem);
			query.put("Comment", comment);
			
			result = doRequestAndLoginIfNeeded(postFeedbackUrl, query);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	

	/*********************************************************************************
	 * private utility method
	 **********************************************************************************/
	
	private String md5sum(String secret) {
		MessageDigest digester;
		String result = "";
		try {
			digester = MessageDigest.getInstance("MD5");
			digester.update(secret.getBytes(Charset.forName("UTF-8")));
			byte[] digest = digester.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			result = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private long nowTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	private JSONObject basicQuery() throws JSONException {
		query = new JSONObject();
		query.put("TimestampUnix", nowTimestamp());
		query.put("Platform", platform);
		query.put("Provider", provider);
		
		return query;
	}
	
	private void setIdentity(String identity) {
		configDao.insertOrReplace(new Config(Param.IDENTITY, identity));
	}
	
	public String getIdentity() {
		String result = "";
		Config identity = configDao.load(Param.IDENTITY);
		if (identity != null)
			result = identity.getValue();
		return result;
	}
	
	private void setCredentials(String login, String password) {
		credentials = new Credentials();
		if (
				configDao.insertOrReplace(new Config(Param.EMAIL, login)) > 0 
				&& 
				configDao.insertOrReplace(new Config(Param.PASSWORD, password)) > 0
			) {
			credentials.setEmail(login);
			credentials.setPassword(password);
		}
	}
	/*
	private String getEmail(){
		Credentials cred = getCredentials();
		return cred!=null ? cred.getEmail() : null; 
	}
	*/
	
	private Credentials getCredentials() {
		credentials = new Credentials();
		Config email = configDao.load(Param.EMAIL);
		if (email != null)
			credentials.setEmail(email.getValue());
		else
			credentials.setEmail(null);
		Config password = configDao.load(Param.PASSWORD);
		if (password != null)
			credentials.setPassword(password.getValue());
		else
			credentials.setPassword(null);
		
		return credentials;
	}
	

	private Credentials deleteCredentials() {
		
		Config email = configDao.load(Param.EMAIL);
		if (email != null)
			configDao.delete(email);
		
		Config password = configDao.load(Param.PASSWORD);
		if (password != null)
		configDao.delete(password);
		
		return credentials;
	}
	
	private class Credentials {
		private String email;
		private String password;
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
	}
	
	private JSONObject doRequestAndLoginIfNeeded(String url, JSONObject query) throws JSONException{
		JSONObject result = new JSONObject();
		result = jsonRestClient.post(url, query);
		
		if (result.has("Status") && result.getInt("Status") == 3) {
			credentials = getCredentials();
			String email = credentials.getEmail();
			String password = credentials.getPassword();
			if(email != null && password != null){
				result = login(email, password);
				query.put("Identity", getIdentity());
				result = jsonRestClient.post(url, query);
			}
		}
		
		return result;
	}
}
