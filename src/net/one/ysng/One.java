package net.one.ysng;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class One extends Activity implements AnimationListener {

	//private ImageView imageView = null;
	private Animation alphaAnimation = null;
	private RelativeLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);              //去掉界面上的图标
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  //去掉电量显示
		super.setContentView(R.layout.welcome);
	//	imageView = (ImageView) findViewById(R.id.imageview);
		this.layout = (RelativeLayout)super.findViewById(R.id.my_welcome);
		alphaAnimation = AnimationUtils.loadAnimation(this,R.anim.welcome_alpha);

		alphaAnimation.setFillEnabled(true); // 启动Fill保持
		alphaAnimation.setFillAfter(true); // 设置动画的最后一帧是保持在View上面
		this.layout.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this); // 为动画设置监听
		ReadExitApplication.getInstance().addActivity(this);
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		// 动画结束时结束欢迎界面并转到软件的主界面
//			Intent intent = new Intent(ReadWelcome.this, MainTest.class);
			//Intent intent = new Intent(ReadWelcome.this, Main.class);
		Intent intent = new Intent(One.this, ReadMainActivity.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 在欢迎界面屏蔽BACK键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
}