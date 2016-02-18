package com.citybility.app.custom;

import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public abstract class CustomClickableSpan extends ClickableSpan {

	private boolean underlineText = true;
	private int textColor = 0;
	private Align align = Align.LEFT;
	private Style style;
	private float textSize;
	private Typeface typeface;
	

	public CustomClickableSpan() {
		super();
	}
	
	public CustomClickableSpan( int textColor, boolean underlineText) {
		super();
		this.textColor = textColor;
		this.underlineText = underlineText;
	}

	public CustomClickableSpan(boolean underlineText, int textColor, float textSize) {
		super();
		this.underlineText = underlineText;
		this.textColor = textColor;
		this.textSize = textSize;
	}

	public CustomClickableSpan(Style style) {
		super();
		this.style = style;
	}
	
	@Override
	public abstract void onClick(View widget);
	
	 @Override 
     public void updateDrawState(TextPaint ds){
         ds.setTextAlign(align);
		 ds.setUnderlineText(underlineText);

		 if(textColor != 0)
			 ds.setColor(textColor);
		 
         if(this.style != null)
        	 ds.setStyle(style);
         
         if(this.textSize > 0)
		 	ds.setTextSize(textSize);

		 if(this.typeface != null)
			 ds.setTypeface(typeface);
		 
       }

	public boolean isUnderlineText() {
		return underlineText;
	}

	public void setUnderlineText(boolean underlineText) {
		this.underlineText = underlineText;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}
	 
	 
	 
}
