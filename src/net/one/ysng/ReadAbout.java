package net.one.ysng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class ReadAbout extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {

    	requestWindowFeature(Window.FEATURE_NO_TITLE);                                                 //ȥ�������ϵ�ͼ��
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_about);
		ReadExitApplication.getInstance().addActivity(this);
	}
	
	/************************************************************************/
	/*****                  ���������ذ�ť
	/************************************************************************/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent it = new Intent(ReadAbout.this,Main.class);
			Intent it = new Intent(ReadAbout.this,ReadSet.class);
			startActivity(it);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);//�������󻬶�
			this.finish();
		}
		return false;	
	}

}
