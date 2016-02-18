package com.citybility.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.citybility.app.R;
import com.citybility.app.custom.AnimationsContainer;
import com.citybility.app.custom.AnimationsContainer.FramesSequenceAnimation;

public class HttpImageLoader {

	public static enum FITMODE {
		FITX, FITY
	};

	private String url;
	private ImageView view;
	private boolean useDefaultLoading = true;
	private boolean useDefaultImage = true;
	private Context mContext;
	private int loadingDrawableId = 0;
	private int defaultDrawableId = 0;

	private FITMODE mfitMode;

	public HttpImageLoader(Context context, String url, ImageView view) {
		this(context, url, view, true, true);
	}

	public HttpImageLoader(Context context, String url, ImageView view, boolean useDefaultLoading, boolean useDefaultImage) {

		this.mContext = context;
		this.url = url;
		this.view = view;
		this.useDefaultLoading = useDefaultLoading;
		this.useDefaultImage = useDefaultImage;
	}

	public HttpImageLoader(Context context, String url, ImageView view, int loadingDrawableId, boolean useDefaultImage) {
		this(context, url, view, false, useDefaultImage);
		this.loadingDrawableId = loadingDrawableId;
	}

	public HttpImageLoader(Context context, String url, ImageView view, boolean useDefaultLoading, int defaultDrawableId) {
		this(context, url, view, useDefaultLoading, false);
		this.defaultDrawableId = defaultDrawableId;
	}

	public HttpImageLoader(Context context, String url, ImageView view, int loadingDrawableId, int defaultDrawableId) {
		this(context, url, view, false, false);
		this.loadingDrawableId = loadingDrawableId;
		this.defaultDrawableId = defaultDrawableId;
	}

	public void load() {
		new ImageLoadTask().execute();
	}

	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
		private Bitmap bitmap;
		private FramesSequenceAnimation animation = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (useDefaultLoading) {
				animation = AnimationsContainer.getInstance().createImageLoadingAnimation(view);
				animation.start();
			} else if (loadingDrawableId != 0) {
				view.setImageResource(loadingDrawableId);
				view.invalidate();
			}
		}

		protected Bitmap doInBackground(String... args) {
			try {
	
				Bitmap tmpBitmap = getBitmapFromURL(url, view.getMeasuredWidth(), view.getMeasuredHeight());
				 
				 if(mfitMode != null && tmpBitmap != null){
					  // Create a matrix for the scaling and add the scaling data
				    float scale = mfitMode == FITMODE.FITX ? (float)view.getWidth()/(float)tmpBitmap.getWidth() : (float)view.getHeight()/(float)tmpBitmap.getHeight();
				    Matrix matrix = new Matrix();
					matrix.postScale(scale, scale);
					
					this.bitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
					
				} else {
					 this.bitmap = tmpBitmap;
				}
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "Error form image "+url);
				Log.e(this.getClass().getName(), Arrays.toString(e.getStackTrace()));
			} catch (OutOfMemoryError err) {
				Log.e(this.getClass().getName(), "L'immagine "+url+" è troppo grande e causa un OutOfMemoryError");
			}
		
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			if (useDefaultLoading && animation != null) {
				animation.stop();
			}

			if (image != null) {
				view.setImageBitmap(image);
				view.invalidate();
			} else {
				if (useDefaultImage) {
					view.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_default));
					view.invalidate();
				} else if (defaultDrawableId != 0) {
					view.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), defaultDrawableId));
					view.invalidate();
				}
			}

		}
	}

	public static Bitmap getBitmapFromURL(String strURL) throws MalformedURLException, IOException {
		InputStream imgInputStream = (InputStream) new URL(strURL).getContent();
		 return BitmapFactory.decodeStream(imgInputStream);
	}

	public static Bitmap getBitmapFromURL(String strURL, int reqWidth, int reqHeight) throws MalformedURLException, IOException {
		 return BitmapUtils.decodeSampledBitmapFromStream(strURL, reqWidth, reqHeight);
	}
	
	public void setFitMode(FITMODE mode) {
		this.mfitMode = mode;
	}
}
