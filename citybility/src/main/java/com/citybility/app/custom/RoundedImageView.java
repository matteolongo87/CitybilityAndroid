package com.citybility.app.custom;

import com.citybility.app.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
	
	public static final int CIRCULAR_SHAPE = 0;
	public static final int RECTANGULAR_SHAPE = 1;
	
	private int viewWidth = 100;
	private int viewHeight = 100;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private BitmapShader shader;
	
	// parameters
	private int shape = 0; // O = circular, 1 = rectangular
	private int borderRadius = 10;
	private int borderWidth = 0;
	private String borderColor = "#CCCCCC";
	
	

	public RoundedImageView(Context context) {
		super(context);
		init(context, null);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		// readParameters
		if(attrs != null){
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedImageView,0, 0);

		 try {
			   shape = a.getInteger(R.styleable.RoundedImageView_shape, 0);
			   borderRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_radius, 10);
			   borderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, 0);
			   borderColor = a.getString(R.styleable.RoundedImageView_border_color);
			   if(borderColor == null || borderColor.isEmpty())
				   borderColor = "#CCCCCC";
			   
		   } finally {
		       a.recycle();
		   }
		}
		
		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);
		paintBorder = new Paint();		
		setBorderColor(borderColor);
		paintBorder.setAntiAlias(true);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		// load the bitmap and scale the bitmap
		loadScaledBitmap(this.viewWidth, this.viewHeight);
		// init shader
		if (image != null) {
			shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);
			
			if(this.shape == 0) {
				int circleCenter = viewWidth / 2;
				// circleCenter is the x or y of the view's center
				// radius is the radius in pixels of the cirle to be drawn
				// paint contains the shader that will texture the shape
				if(this.borderWidth > 0)
					canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth, paintBorder);
				canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter, paint);
			} else {
				if(this.borderWidth > 0)
					canvas.drawRoundRect(new RectF(0, 0, viewWidth, viewHeight), this.borderRadius, this.borderRadius, paintBorder);
				canvas.drawRoundRect(new RectF(borderWidth, borderWidth, viewWidth - borderWidth, viewHeight - borderWidth), this.borderRadius, this.borderRadius, paint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		viewWidth = width - (borderWidth * 2);
		viewHeight = height - (borderWidth * 2);
		setMeasuredDimension(width, height);
	}

	private void loadScaledBitmap(int width, int height) {
		float computedDim = 0.0F;
		BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();
		if (bitmapDrawable != null){
			if(width>height){
				computedDim = (float)bitmapDrawable.getBounds().height()/(float)(bitmapDrawable.getBounds().width()/(float)width);
				image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, (int)computedDim, false);
			} else {
				computedDim = (float)bitmapDrawable.getBounds().width()/((float)bitmapDrawable.getBounds().height()/(float)height);
				image = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), (int)computedDim, height, false);
				
			}
		}
	}

	
	/********* getter / setter ***************/
	

	public int getShape() {
		return shape;
	}

	public void setShape(int shape) {
		this.shape = shape;
	}

	public int getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
		if (paintBorder != null)
			paintBorder.setColor(Color.parseColor(borderColor));
		this.invalidate();
	}

	
}