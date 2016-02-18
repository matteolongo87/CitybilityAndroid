package com.citybility.app.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.widget.ImageView;

import com.citybility.app.R;

public class AnimationsContainer
{
  private static AnimationsContainer mInstance;
  public int FPS = 25;
  private int[] mProgressAnimFrames = {
		  R.drawable.loading0002,
		  R.drawable.loading0003,
		  R.drawable.loading0004,
		  R.drawable.loading0005,
		  R.drawable.loading0006,
		  R.drawable.loading0007,
		  R.drawable.loading0008,
		  R.drawable.loading0009,
		  R.drawable.loading0010,
		  R.drawable.loading0011,
		  R.drawable.loading0012,
		  R.drawable.loading0013,
		  R.drawable.loading0014,
		  R.drawable.loading0015,
		  R.drawable.loading0016,
		  R.drawable.loading0017,
		  R.drawable.loading0018,
		  R.drawable.loading0019,
		  R.drawable.loading0020,
		  R.drawable.loading0021,
		  R.drawable.loading0022,
		  R.drawable.loading0023,
		  R.drawable.loading0024,
		  R.drawable.loading0025,
		  R.drawable.loading0026,
		  R.drawable.loading0027,
		  R.drawable.loading0028,
		  R.drawable.loading0029,
		  R.drawable.loading0030,
		  R.drawable.loading0031,
		  R.drawable.loading0032,
		  R.drawable.loading0033,
		  R.drawable.loading0034,
		  R.drawable.loading0035,
		  R.drawable.loading0036,
		  R.drawable.loading0037,
		  R.drawable.loading0038,
		  R.drawable.loading0039,
		  R.drawable.loading0040,	
		  R.drawable.loading0055,
		  R.drawable.loading0056,
		  R.drawable.loading0057,
		  R.drawable.loading0058,
		  R.drawable.loading0059,
		  R.drawable.loading0060,
		  R.drawable.loading0061,
		  R.drawable.loading0062,
		  R.drawable.loading0063,
		  R.drawable.loading0064,
		  R.drawable.loading0065,
		  R.drawable.loading0066,
		  R.drawable.loading0067,
		  R.drawable.loading0068,
		  R.drawable.loading0069,
		  R.drawable.loading0070,
		  R.drawable.loading0071,
		  R.drawable.loading0072,
		  R.drawable.loading0073,
		  R.drawable.loading0074,
		  R.drawable.loading0075,
		  R.drawable.loading0076 };
  
  
  private int[] mSplashAnimFrames = this.mProgressAnimFrames;


  private OnAnimationStoppedListener mOnAnimationStoppedListener;
  
  public static AnimationsContainer getInstance()
  {
    if (mInstance == null)
      mInstance = new AnimationsContainer();
    return mInstance;
  }

  public FramesSequenceAnimation createImageLoadingAnimation(ImageView paramImageView)
  {
    return new FramesSequenceAnimation(paramImageView, this.mProgressAnimFrames, this.FPS);
  }

  public FramesSequenceAnimation createPageLoadingAnimation(ImageView paramImageView)
  {
    return new FramesSequenceAnimation(paramImageView, this.mSplashAnimFrames, this.FPS);
  }

  public class FramesSequenceAnimation
  {
    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;
    private int mDelayMillis;
    private int[] mFrames;
    private Handler mHandler = new Handler();
    private ImageView mImageView;
    private int mIndex;
    private boolean mIsRunning;
    private boolean mShouldRun;
    private AnimationsContainer.OnAnimationStoppedListener onAnimationStoppedListener;

    public FramesSequenceAnimation(ImageView paramArrayOfInt, int[] paramInt, int arg4)
    {
      this.mFrames = paramInt;
      this.mIndex = -1;
      this.mImageView = paramArrayOfInt;
      this.mShouldRun = false;
      this.mIsRunning = false;
      this.mDelayMillis = (1000 / FPS);
      paramArrayOfInt.setImageResource(this.mFrames[0]);
      Bitmap localBitmap = ((BitmapDrawable)paramArrayOfInt.getDrawable()).getBitmap();
      this.mBitmap = Bitmap.createBitmap(localBitmap.getWidth(), localBitmap.getHeight(), localBitmap.getConfig());
      this.mBitmapOptions = new BitmapFactory.Options();
      this.mBitmapOptions.inBitmap = this.mBitmap;
      this.mBitmapOptions.inMutable = true;
      this.mBitmapOptions.inSampleSize = 1;
    }

    private int getNext()
    {
      this.mIndex = (1 + this.mIndex);
      return this.mFrames[(this.mIndex % this.mFrames.length)];
    }

    public AnimationsContainer.OnAnimationStoppedListener getOnAnimationStoppedListener()
    {
      return this.onAnimationStoppedListener;
    }

    public void setOnAnimationStoppedListener(AnimationsContainer.OnAnimationStoppedListener paramOnAnimationStoppedListener)
    {
      this.onAnimationStoppedListener = paramOnAnimationStoppedListener;
    }
    /**
     * Starts the animation
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;

        Runnable runnable = new Runnable() {

			@Override
            public void run() {
                if (!mShouldRun || mImageView == null) {
                    mIsRunning = false;
                    if (mOnAnimationStoppedListener != null) {
                        mOnAnimationStoppedListener.animationStopped();
                    }
                    return;
                }

                mIsRunning = true;
                mHandler.postDelayed(this, mDelayMillis);

                if (mImageView.isShown()) {
                    int imageRes = getNext();
                    if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeResource(mImageView.getResources(), imageRes, mBitmapOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                        	mImageView.setImageBitmap(bitmap);
                        } else {
                        	mImageView.setImageResource(imageRes);
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                    } else {
                    	mImageView.setImageResource(imageRes);
                    }
                }

            }
        };

        mHandler.post(runnable);
    }


    /**
     * Stops the animation
     */
    public synchronized void stop() {
        mShouldRun = false;
    }
  }

  public static abstract interface OnAnimationStoppedListener
  {
    public abstract void animationStopped();
  }
}

/* Location:           /media/STUFF/DWL/ANDROID/Decompile/dex2jar-0.0.9.15/output_jar.jar
 * Qualified Name:     com.citybility.app.custom.AnimationsContainer
 * JD-Core Version:    0.6.2
 */