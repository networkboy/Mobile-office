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
		requestWindowFeature(Window.FEATURE_NO_TITLE);              //ȥ�������ϵ�ͼ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  //ȥ��������ʾ
		super.setContentView(R.layout.welcome);
	//	imageView = (ImageView) findViewById(R.id.imageview);
		this.layout = (RelativeLayout)super.findViewById(R.id.my_welcome);
		alphaAnimation = AnimationUtils.loadAnimation(this,R.anim.welcome_alpha);

		alphaAnimation.setFillEnabled(true); // ����Fill����
		alphaAnimation.setFillAfter(true); // ���ö��������һ֡�Ǳ�����View����
		this.layout.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this); // Ϊ�������ü���
		ReadExitApplication.getInstance().addActivity(this);
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		// ��������ʱ������ӭ���沢ת�������������
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
		// �ڻ�ӭ��������BACK��
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
}