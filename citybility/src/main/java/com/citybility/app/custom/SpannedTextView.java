package com.citybility.app.custom;

import com.citybility.app.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class SpannedTextView extends TextView {
	
	private int mLines;
	private int mMarginLeft;
	private String mSpannedText;
	
	public SpannedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParameters(context, attrs);
	}
	public SpannedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParameters(context, attrs);
	}
	public SpannedTextView(Context context) {
		super(context);
		initParameters(context, null);
	}

	private void initParameters(Context context, AttributeSet attrs){
		 TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpannedTextView,0, 0);

			   try {
				   mLines = a.getInteger(R.styleable.SpannedTextView_lines, 0);
				   mMarginLeft = a.getDimensionPixelSize(R.styleable.SpannedTextView_marginLeft, 0);
				   mSpannedText = a.getString(R.styleable.SpannedTextView_spannedText);
				   
			   } finally {
			       a.recycle();
			   }
			   
			   if(mSpannedText!=null && !mSpannedText.isEmpty())
				   this.setSpannedText(mSpannedText);
	}
	
	public void setSpannedText(CharSequence text){
		this.setSpannedText(mLines, mMarginLeft, text);
	}

	public void setSpannedText(int lineNumber, int leftMargin, CharSequence text){

	        SpannableString ss = new SpannableString(text);
	        ss.setSpan(new MyLeadingMarginSpan2(lineNumber, leftMargin), 0, ss.length(), 0);
	        this.setText(ss);
	        invalidate();
	        requestLayout();

	}
	
	
	
	
	/*****************************************************************
	 * INNER CLASS
	*****************************************************************/
	class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
	    private int margin;
	    private int lines;

	    MyLeadingMarginSpan2(int lines, int margin) {
	        this.margin = margin;
	        this.lines = lines;
	    }

	    @Override
	    public int getLeadingMargin(boolean first) {
	        if (first) {
	            return margin;
	        } else {
	            return 0;
	        }
	    }

	    @Override
	    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
	            int top, int baseline, int bottom, CharSequence text,
	            int start, int end, boolean first, Layout layout) {}

	    @Override
	    public int getLeadingMarginLineCount() {
	        return lines;
	    }
	};
}
