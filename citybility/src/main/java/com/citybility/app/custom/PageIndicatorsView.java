package com.citybility.app.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

import com.citybility.app.R;

public class PageIndicatorsView extends View {

	private final int PADDING_TOP = 3;
	private final int PADDING_BOTTOM = 2;
	private final int PADDING_LEFT = 2;
	private final int PADDING_RIGHT = 2;

	private int activePoint = 0;
	private Paint mPaint;
	private Paint mPaintBorder;
	private Canvas mCanvas;
	private ViewPager viewPager;

	// parameters
	private int pointRadius = 8;
	private int pointMargin = 20;
	private int pointNumber = 0;
	private int pointColor = Color.parseColor("#FFFFFF");
	private int activePointColor = Color.parseColor("#888888");
	private int pointBorderColor = -1;
	private int pointBorderWidth = -1;
	private boolean stepMode = false;

	public PageIndicatorsView(Context context, int numPoints) {
		super(context);
		this.pointNumber = numPoints;
		this.init(context, null);
	}

	public PageIndicatorsView(Context context, int numPoints, int active) {
		this(context, numPoints);
		this.activePoint = active;
		this.init(context, null);
	}

	public PageIndicatorsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs);
	}

	public PageIndicatorsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		try {
			pointColor = getResources().getColor(R.color.indicatorView_point_colors);
			activePointColor = getResources().getColor(R.color.indicatorView_active_point_colors);
		} catch (Exception e) {
			// don't care... will use the default ones
		}

		// readParameters
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PageIndicatorsView, 0, 0);

			try {

				this.pointRadius = a.getDimensionPixelSize(R.styleable.PageIndicatorsView_point_radius, pointRadius);
				this.pointMargin = a.getDimensionPixelSize(R.styleable.PageIndicatorsView_point_margin, pointMargin);
				this.pointNumber = a.getInteger(R.styleable.PageIndicatorsView_point_number, pointNumber);
				this.pointColor = a.getColor(R.styleable.PageIndicatorsView_point_color, pointColor);
				this.activePointColor = a.getColor(R.styleable.PageIndicatorsView_active_point_color, activePointColor);
				this.pointBorderColor = a.getColor(R.styleable.PageIndicatorsView_point_border_color, pointBorderColor);
				this.pointBorderWidth = a.getDimensionPixelSize(R.styleable.PageIndicatorsView_point_border_width, pointBorderWidth);
				this.stepMode = a.getBoolean(R.styleable.PageIndicatorsView_step_mode, stepMode);
			} finally {
				a.recycle();
			}
		}

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);

		if (pointBorderWidth >= 0) {
			mPaintBorder = new Paint();
			mPaintBorder.setStyle(Paint.Style.STROKE); 
			mPaintBorder.setColor(pointBorderColor);
			mPaintBorder.setStrokeWidth(pointBorderWidth);
			mPaintBorder.setAntiAlias(true);
			this.invalidate();
		}

		if (this.pointNumber > 0)
			this.setPointNumber(pointNumber);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mCanvas = canvas;
		this.drawIndicators();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = PADDING_LEFT + pointNumber * (pointRadius * 2)+ (pointNumber-1) * pointMargin + PADDING_RIGHT;
		int h = PADDING_TOP + pointRadius*2 + PADDING_BOTTOM;

		setMeasuredDimension(w, h);
	}

	private void drawIndicators() {
		if (mCanvas != null) {
			invalidate();

			int y = PADDING_TOP + pointRadius;
			int x = PADDING_LEFT + pointRadius;
			for (int i = 0; i < pointNumber; i++) {

				if (this.activePoint > 0 && (this.activePoint == i + 1 || (this.stepMode && this.activePoint > i)))
					mPaint.setColor(activePointColor);
				else
					mPaint.setColor(pointColor);

				this.mCanvas.drawCircle(x, y, pointRadius, mPaint);
				if(mPaintBorder != null){
					this.mCanvas.drawCircle(x, y, pointRadius, mPaintBorder);
				}
				x += (pointRadius * 2 + pointMargin);
			}
		}

	}

	public void updateIndicators() {
		this.drawIndicators();
	}

	public int getPointRadius() {
		return pointRadius;
	}

	public void setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
	}

	public int getPointMargin() {
		return pointMargin;
	}

	public void setPointMargin(int pointMargin) {
		this.pointMargin = pointMargin;
	}

	public int getPointNumber() {
		return pointNumber;
	}

	public void setPointNumber(int pointNumber) {
		this.pointNumber = pointNumber;
	}

	public int getActivePoint() {
		return activePoint;
	}

	public void setActivePoint(int activePoint) {
		this.activePoint = activePoint;
	}

	public int getPointColor() {
		return pointColor;
	}

	public void setPointColor(int pointColor) {
		this.pointColor = pointColor;
	}

	public int getActivePointColor() {
		return activePointColor;
	}

	public void setActivePointColor(int activePointColor) {
		this.activePointColor = activePointColor;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		if (this.viewPager != null) {
			this.pointNumber = this.viewPager.getAdapter().getCount();
			this.activePoint = this.viewPager.getCurrentItem() + 1;
			this.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					setActivePoint(position + 1);
					updateIndicators();
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

			});
		}
	}

}
