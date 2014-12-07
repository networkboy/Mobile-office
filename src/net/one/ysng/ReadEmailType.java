package net.one.ysng;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.NewsAddress;


import net.wxwen.mail.LandMail;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ReadEmailType extends Activity {

	private int emailType[] = new int[]{R.drawable.w163_mail_icon_n,
			R.drawable.w126_mail_icon,R.drawable.gmail_icon,R.drawable.qq_mail_icon, 
			R.drawable.other_mail};
	private String filePath;
	
	private ListView listView = null;
	private SimpleAdapter simpleAdapter;
	private List<Map<String,String>> list = new  ArrayList<Map<String,String>>();
	private SharedPreferencesSkin shareData;
	private ReadConstant myConstant = new ReadConstant();
	private RelativeLayout titleBar;
	private ImageButton homeBtn=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);               //去掉界面上的图标
		super.setContentView(R.layout.choose_mail_type);

		Intent it = this.getIntent();
		Bundle bundle = it.getExtras();
		//int o =bundle.getInt("sign");
		this.filePath = bundle.getString("path");
		
		shareData = new SharedPreferencesSkin(ReadEmailType.this, myConstant.SKINVIEW);
		this.titleBar = (RelativeLayout)super.findViewById(R.id.mail_type_titlebar);
		this.homeBtn = (ImageButton)super.findViewById(R.id.mail_type_home_back);
		this.listView = (ListView) super.findViewById(R.id.type_list);
		this.changeSkin();
		

		for(int i =0;i<this.emailType.length;i++){
			Map<String,String> map = new HashMap<String, String>();
			map.put("type", String.valueOf(this.emailType[i]));
			this.list.add(map);
		}

		this.simpleAdapter = new SimpleAdapter(this, 
				this.list, 
				R.layout.email_data,
				new String[]{"type"}, 
				new int[]{R.id.email_data});

		this.listView.setAdapter(this.simpleAdapter);
		this.listView.setOnItemClickListener(new OnItemClickListenerDemo());	
		ReadExitApplication.getInstance().addActivity(this);
		
		this.homeBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent it = new Intent(ReadEmailType.this,ReadMainActivity.class);
				startActivity(it);
				finish();	
			}
		});
	}
	
	
	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){	
		this.shareData = new SharedPreferencesSkin(ReadEmailType.this, myConstant.FILENAME);
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleBar.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan );
		}
	}

	/************************************************************************
	 *                                     按钮监听
	 *   Intent传输数据协议（type）
	 *   0：163Mail  1: 126Mail   2 :sinaMail 3：Gmail  4：QQMail  
	 *   5：其他Mail  
	/************************************************************************/
	private class OnItemClickListenerDemo implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?>parent,View view,int position,long id){

			Intent it = new Intent(ReadEmailType.this,LandMail.class);	
			Bundle bundle = new Bundle();	
			bundle.putString("path", filePath);
			bundle.putInt("type", position);
			it.putExtras(bundle);
			startActivity(it);	

		}

	}


}
