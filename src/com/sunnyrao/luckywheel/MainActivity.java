package com.sunnyrao.luckywheel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private LuckyWheel mLuckyWheel;
	private ImageView mStartButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLuckyWheel = (LuckyWheel) findViewById(R.id.id_lucky_wheel);
		mStartButton = (ImageView) findViewById(R.id.id_btn_start);
		
		mStartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mLuckyWheel.isStart()) {
					int rand = (int) (Math.random() * 1000);
					int index;
					if (rand >= 0 && rand < 10) {
						index = 3;
					} else if (rand < 100) {
						index = 1;
					} else if (rand < 200) {
						index = 0;
					} else if (rand < 500) {
						index = 4;
					} else if (rand < 750) {
						index = 2;
					} else {
						index = 5;
					}
					mLuckyWheel.luckyStart(index);
					mStartButton.setImageResource(R.drawable.stop);
				} else {
					if (!mLuckyWheel.isShouldEnd()) {
						mLuckyWheel.luckyEnd();
						mStartButton.setImageResource(R.drawable.start);
					}
				}
			}
		});
	}
}
