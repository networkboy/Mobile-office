package net.wxwen.mail;
import net.one.ysng.ReadExitApplication;
import net.one.ysng.R;
import net.one.ysng.ReadEmailType;
import net.one.ysng.ReadMainActivity;
import net.one.ysng.ReadOld;
import net.wxwen.mail.*;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;
/************************************************************************
 *   Intent传输数据协议（type）
 *   0：163Mail  1: 126Mail   2 :sinaMail 3：Gmail  4：QQMail  
 *   5：其他Mail  
/************************************************************************/
public class LandMail extends Activity {
	private static final String FILENAME = "one";
	private Button send; 
	private EditText userid; 
	private EditText password;
	private ImageButton homeBtn = null;
	private CheckBox check =null;
	private String filePath;
	private int mailType;
	private boolean checkSign = true;

	private SharedPreferencesSkin shareData;
	private SharedPreferencesSkin shareMailUserData;
	private ReadConstant myConstant = new ReadConstant();
	private RelativeLayout titleBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);               //去掉界面上的图标
		super.setContentView(R.layout.land_mail);

		this.shareData = new SharedPreferencesSkin(LandMail.this, myConstant.SKINVIEW);
		this.titleBar = (RelativeLayout)super.findViewById(R.id.land_mail_titlebar);
		this.check = (CheckBox)super.findViewById(R.id.land_chex);
		this.changeSkin();

		Intent it = this.getIntent();
		Bundle bundle = it.getExtras();
		this.filePath = bundle.getString("path");
		this.mailType = bundle.getInt("type");

		send = (Button) findViewById(R.id.land_btn); 
		userid = (EditText) findViewById(R.id.id_edit); 
		password = (EditText) findViewById(R.id.code_edit); 
		this.homeBtn = (ImageButton)super.findViewById(R.id.land_mail_home_back);
		ReadExitApplication.getInstance().addActivity(this);
		send.setOnClickListener(new View.OnClickListener() {
			@Override 
			public void onClick(View v) { 
				// TODO Auto-generated method stub                    

				User user =new User();
				user.setUserEmail(userid.getText().toString());
				user.setUserPwd(password.getText().toString());


				LoginDAO Ldi=new LoginDAOImpl();
//				String serverSmtp=Ldi.whichSmtp(user.getUserEmail());
				String serverSmtp=Ldi.decideSmtp(mailType);

			
	

				if(null!=serverSmtp){
					//					boolean isLogin=Ldi.isLogin(user,serverSmtp);
					boolean isLogin=Ldi.isLogin(user,mailType );

					if(true==isLogin){
						putUser(user.getUserEmail(), user.getUserPwd()); //存储账户
						
						SharedPreferencesSkin shareData=new SharedPreferencesSkin(LandMail.this, myConstant.MAILUSER);
						shareData.putString(myConstant.USERID, user.getUserEmail());
						shareData.putString(myConstant.USERCODE, user.getUserPwd());
						shareData.putString(myConstant.USERSTEMP, serverSmtp);
						
						Toast.makeText(LandMail.this,"登录成功！", 8000).show();
						Intent intent = new Intent();
						//						intent.putExtra("name",user.getUserEmail());
						//						intent.putExtra("pass",user.getUserPwd());
						intent.putExtra("mailType", mailType);
						intent.putExtra("path",filePath);
						intent.putExtra("checkSign",checkSign);
						intent.setClass(LandMail.this,SendMail.class);//调用下一个activty
						startActivity(intent);
					}else{
						Toast.makeText(LandMail.this,"登录失败！用户名密码有问题", 8000).show();
					}
				}else{
					Toast.makeText(LandMail.this, "邮件名称格式错误", 8000).show();
				}
			}
		}); 

		this.homeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(LandMail.this,ReadMainActivity.class);
				startActivity(it);
				finish();	
			}
		});
		
		this.check.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View view) {
				if(check.isChecked()){
					checkSign = true;
				}else {
					checkSign = false;
				}		
			}
		});
	}

	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){	
		this.shareData = new SharedPreferencesSkin(LandMail.this, myConstant.FILENAME);
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleBar.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}

	/************************************************************************
	 * 功能 ： 存储邮箱账户信息
	 * 
	 *@param String
	 ************************************************************************/
	private void putUser(String userId,String userCode){
		this.shareMailUserData = new SharedPreferencesSkin(getApplication(), this.myConstant.MAILUSER);
		this.shareMailUserData.putString(this.myConstant.USERID, userId);
		this.shareMailUserData.putString(this.myConstant.USERCODE ,userCode);
	}

}




