package com.citybility.app.util;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;

public class BitmapUtils {

	


	/*******************************************************
	 * Bitmap base64 encoding/deconding
	 *******************************************************/
	
	public static String encodeTobase64(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[] byteArray = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
		return imageEncoded;
	}

	public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}



	/*******************************************************
	 * Image manupulation
	 *******************************************************/
	public static Bitmap createSquareThumbnail(Bitmap tmpBitmap, int dimension) {

		int xDiff = 0;
		int yDiff = 0;
		
		if(tmpBitmap.getWidth()>tmpBitmap.getHeight()){ // landscape
			xDiff = tmpBitmap.getWidth()-tmpBitmap.getHeight();
		} else { // portrait
			yDiff = tmpBitmap.getHeight()-tmpBitmap.getWidth();
		}
		
		Matrix matrix = new Matrix();
		RectF souceRect = new RectF(xDiff/2, yDiff/2, tmpBitmap.getWidth()-xDiff, tmpBitmap.getHeight()-yDiff);
		boolean canRappresent = matrix.setRectToRect(souceRect,  new RectF(0, 0, dimension, dimension), Matrix.ScaleToFit.CENTER);

		return canRappresent ? Bitmap.createBitmap(tmpBitmap, (int)souceRect.left, (int)souceRect.top, (int)souceRect.right, (int)souceRect.bottom, matrix, true) : null;

	}


	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromGallery(Intent data, int reqWidth, int reqHeight, ContentResolver contentResolver) throws IOException {

		Bitmap sampledBitmap = null;
		Uri pictureUri = data.getData();
		if(pictureUri !=null) {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(pictureUri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			BitmapFactory.decodeFileDescriptor(fileDescriptor, new Rect(), options);
	
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
	
			sampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, new Rect(), options);
			parcelFileDescriptor.close();
		}
		return sampledBitmap;
	}


public static Bitmap decodeSampledBitmapFromCamera(Intent data) {
	Bitmap sampledBitmap = null; 
	boolean filter = true;
	int dstHeight;
	int dstWidth;
	
	sampledBitmap = (Bitmap)data.getParcelableExtra("data");
	if(sampledBitmap.getWidth()>sampledBitmap.getHeight()){ // landscape
		dstHeight =  Constant.SQUARE_THUMBNAIL_DIM;
		dstWidth =  Constant.SQUARE_THUMBNAIL_DIM;
	} else { // portrait
		dstHeight =  Constant.SQUARE_THUMBNAIL_DIM;
		dstWidth =  Constant.SQUARE_THUMBNAIL_DIM;	
	}
	
	sampledBitmap = Bitmap.createScaledBitmap(sampledBitmap, dstWidth, dstHeight, filter);
	return sampledBitmap;
}


public static Bitmap decodeSampledBitmapFromStream(String strURL, int reqWidth, int reqHeight) throws IOException {
	Bitmap sampledBitmap = null;
	if(strURL !=null) {
		InputStream imgInputStream = (InputStream) new URL(strURL).getContent();
		 
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeStream(imgInputStream, null, options);
		
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
	    // Decode bitmap with inSampleSize set
	    imgInputStream = (InputStream) new URL(strURL).getContent();
	    options.inJustDecodeBounds = false;
		sampledBitmap = BitmapFactory.decodeStream(imgInputStream, null, options);
	}
	return sampledBitmap;
}


}
