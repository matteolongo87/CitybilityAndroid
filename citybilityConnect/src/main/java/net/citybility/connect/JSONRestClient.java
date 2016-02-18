package net.citybility.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONRestClient {

	private static JSONRestClient instance = null;
	
	protected JSONRestClient() {
		// No instantiation
	}
	
	public static JSONRestClient getInstance() {
		if (instance == null)
			instance = new JSONRestClient();
		
		return instance;
	}
	
	/**
	 * Execute a POST request to an endpoint
	 * @param url
	 * @param query 
	 * 
	 * @return A JSONObject encapsulating the response body, if any.
	 */
	public JSONObject post(String url, JSONObject query) {
		InputStream inputStream = null;
		String result = "";
		JSONObject jsonResult;

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		String jsonQuery = query.toString();
/*
		System.out.println(url);
		System.out.println(query);
		System.out.println(jsonQuery);
*/
		try {

			if (DebugLog.DEBUG) {
				Log.d("*** URL: ", url);
				Log.d("*** QUERY: ", query.toString(4));
			}
			StringEntity se = new StringEntity(jsonQuery);
			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = inputStreamToString(inputStream);
            
			jsonResult = new JSONObject(result);
		
		} catch (JSONException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			jsonResult = new JSONObject();
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		} catch (IOException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		}
		return jsonResult;
	}

	/**
	 * Execute a GET request to an endpoint
	 * @param url
	 * @param query 
	 * 
	 * @return A JSONObject encapsulating the response body, if any.
	 */
	public JSONObject get(String url) {
		InputStream inputStream = null;
		String result = "";
		JSONObject jsonResult;

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {

			httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = inputStreamToString(inputStream);
            
			jsonResult = new JSONObject(result);
		
		} catch (JSONException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		} catch (IOException e) {
			jsonResult = new JSONObject();
			e.printStackTrace();
		}
		return jsonResult;
	}

	
	private static String inputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder("");
        while((line = bufferedReader.readLine()) != null)
        	result.append(line);

        inputStream.close();
        return result.toString();
    }  
}
