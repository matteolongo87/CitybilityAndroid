package com.citybility.app;

import java.util.Stack;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.citybility.app.custom.PageIndicatorsView;


public abstract class keypadActivity extends CitybilityBaseActivity implements OnClickListener {

	private PageIndicatorsView mPageIndicatorsView;
	private Stack<Integer> mTyped = new Stack<Integer>();
	
	protected abstract void onNumberTyped(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code_typing);

		findViewById(R.id.tc_btn0).setOnClickListener(this);
		findViewById(R.id.tc_btn1).setOnClickListener(this);
		findViewById(R.id.tc_btn2).setOnClickListener(this);
		findViewById(R.id.tc_btn3).setOnClickListener(this);
		findViewById(R.id.tc_btn4).setOnClickListener(this);
		findViewById(R.id.tc_btn5).setOnClickListener(this);
		findViewById(R.id.tc_btn6).setOnClickListener(this);
		findViewById(R.id.tc_btn7).setOnClickListener(this);
		findViewById(R.id.tc_btn8).setOnClickListener(this);
		findViewById(R.id.tc_btn9).setOnClickListener(this);
		findViewById(R.id.tc_btncanc).setOnClickListener(this);

		// get the page indicator
		mPageIndicatorsView = (PageIndicatorsView) findViewById(R.id.codeIndicators);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.tc_btn0:
			hasTypedANumber(0);
			break;
		case R.id.tc_btn1:
			hasTypedANumber(1);
			break;
		case R.id.tc_btn2:
			hasTypedANumber(2);
			break;
		case R.id.tc_btn3:
			hasTypedANumber(3);
			break;
		case R.id.tc_btn4:
			hasTypedANumber(4);
			break;
		case R.id.tc_btn5:
			hasTypedANumber(5);
			break;
		case R.id.tc_btn6:
			hasTypedANumber(6);
			break;
		case R.id.tc_btn7:
			hasTypedANumber(7);
			break;
		case R.id.tc_btn8:
			hasTypedANumber(8);
			break;
		case R.id.tc_btn9:
			hasTypedANumber(9);
			break;

		case R.id.tc_btncanc:
			hasTypedACancel();
			break;
		}
		mPageIndicatorsView.setActivePoint(mTyped.size());

	}

	
	private void hasTypedANumber(int num){
		if(mTyped.size() < mPageIndicatorsView.getPointNumber()){
			mTyped.push(num);
			mPageIndicatorsView.setActivePoint(mTyped.size());
			this.onNumberTyped();
		}
	}
	private void hasTypedACancel(){
		if(mTyped.size() > 0){
			mTyped.pop();
			mPageIndicatorsView.setActivePoint(mTyped.size());
		}
	}

	protected String getTypedAsString(){
		StringBuilder sb = new StringBuilder();
		for(int num:mTyped){
			sb.append(num);
		}
		return sb.toString();
	}
	protected int getTypedSize(){
		return this.mTyped.size();
	}
}
