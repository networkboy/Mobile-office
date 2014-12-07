package net.one.ysng;

import net.chh.graphic.Scan;
import net.one.ysng.ReadExitApplication;
import net.one.ysng.R;
import net.one.ysng.ReadFileList;
import net.one.ysng.ReadOld;
import net.one.ysng.ReadSearch;
import net.one.ysng.ReadSet;
import net.ysng.reader.MenuImageView;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ReadMainActivity extends Activity {
	private long exitTime = 0;
	private ReadConstant myConstant ;
	private SharedPreferencesSkin shareData;

	private RelativeLayout main_layout;

	private MenuImageView myView1 = null;
	private MenuImageView myView2 = null;
	private MenuImageView myView3 = null;
	private MenuImageView myView4 = null;
	private MenuImageView myView5 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.read_main);

		this.shareData = new SharedPreferencesSkin(this, this.myConstant.FILENAME);

		this.main_layout = (RelativeLayout) super.findViewById(R.id.main_activity_layout);
		this.myView1 = (MenuImageView)super.findViewById(R.id.main_scan);
		this.myView2 = (MenuImageView)super.findViewById(R.id.main_settings);
		this.myView3 = (MenuImageView)super.findViewById(R.id.main_recent);
		this.myView4 = (MenuImageView)super.findViewById(R.id.main_search);
		this.myView5 = (MenuImageView)super.findViewById(R.id.main_local);

		//	this.setLayout();

		this.myView1.setOnClickListener(new MyClickListener());
		this.myView2.setOnClickListener(new MyClickListener());
		this.myView3.setOnClickListener(new MyClickListener());
		this.myView4.setOnClickListener(new MyClickListener());
		this.myView5.setOnClickListener(new MyClickListener());

		this.changeSkin();

		ReadExitApplication.getInstance().addActivity(this);		
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
	}

	private void setLayout(){
		int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth() - 10; // 获得屏幕宽度
		int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight() - 10; // 获得屏幕g高度度
		int viewHeight = (screenHeight - 20)/3;

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(viewHeight,viewHeight);
		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(viewHeight*2,viewHeight);
		layoutParams.setMargins(1, 1, 1, 1);
		this.myView1.setLayoutParams(layoutParams);
		this.myView1.setLayoutParams(layoutParams);
		this.myView2.setLayoutParams(layoutParams);
		this.myView3.setLayoutParams(layoutParams);
		this.myView4.setLayoutParams(layoutParams);
		this.myView5.setLayoutParams(layoutParams1);
	}

	/************************************************************************/
	/*****                               换肤
	/************************************************************************/
	private void changeSkin(){
		if(this.shareData.contains(this.myConstant.MAINBG)){	//如果数据已经存储过，就直接取出来用
			this.main_layout.setBackgroundDrawable(getResources().getDrawable(this.shareData.getInt(this.myConstant.MAINBG)));
		}else {    //如果第一次使用则设置默认值，蓝色
			this.main_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_0_gray));
			this.shareData.putInt(myConstant.MAINBG, R.drawable.background_0_gray);
		}
	}

	/************************************************************************/
	/*****           监听MenuImageView实现OnClickListener接口
	/************************************************************************/
	private class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View view) {
			String sdStateString = android.os.Environment.getExternalStorageState();
			if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))            //判断SD卡是否存在
			{	
				Intent it =null;
				switch (view.getId()) {
				case R.id.main_scan:
					it = new Intent(ReadMainActivity.this,Scan.class);
					startActivity(it) ;	// 跳转	
					finish();	
					break;
				case R.id.main_settings:
					it = new Intent(ReadMainActivity.this,ReadSet.class);
					startActivity(it) ;	// 跳转			
					break;
				case R.id.main_recent:
					it = new Intent(ReadMainActivity.this, ReadOld.class);
					startActivity(it) ;	// 跳转	
					break;
				case R.id.main_search:
					it = new Intent(ReadMainActivity.this,ReadSearch.class);
					startActivity(it) ;	// 跳转	
					break;
				case R.id.main_local:
					it = new Intent(ReadMainActivity.this,ReadFileList.class);
					startActivity(it) ;	// 跳转	
					break;
				default:
					break;
				}
			}else{
				String title = "提示";
				String meg = "SD卡不存在";
				createDialog(title, meg);
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//	getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/************************************************************************/
	/*****                         连续按两次返回按钮退出程序
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                               
				exitTime = System.currentTimeMillis();
			}else{
				this.exitApp();
			}
		}
		return false;
	}


	/************************************************************************/
	/*****                               创建Dialog
	/************************************************************************/
	public void createDialog(String title,String meg){
		Dialog dialog = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(meg)
		.setPositiveButton("确认",new DialogInterface.OnClickListener() {		                            			
			public void onClick(DialogInterface dialog, int which) {		                            				
				dialog.dismiss();
			}
		})
		.create();

		Window window = dialog.getWindow();  
		window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.mystyle);  //添加动画  
		dialog.show();
	}

	/************************************************************************/
	/*****                        退出程序
	/************************************************************************/
	private void exitApp(){
		ReadExitApplication.getInstance().exit();	
	}


}
