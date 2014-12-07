/********************************************************************************
 * �ռ��˵�ַ   ����   ����  ���� ���Ͱ�ť
 *********************************************************************************/
package net.wxwen.mail;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.one.ysng.ReadExitApplication;
import net.one.ysng.R;
import net.one.ysng.ReadEmailType;
import net.one.ysng.ReadMainActivity;
import net.one.ysng.ReadOld;
import net.ysng.reader.MyDialog;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
public class SendMail extends Activity {


	private  String[] stringArray = { "@163.com", "@126.com", "@qq.com", "@sina.com", "@gmail.com" };
	private AutoCompleteTextView texttoaddtion;
	private EditText textsubject;
	private EditText textcontent;
	private TextView sendFileView;
	private TextView fileSizeView;
	private Button sendBtn;
	private ImageButton homeBtn=null;
	private  String path;
	private  boolean checkSign;
	private int mailType = 0;
	private int sgin = 0;

	private SharedPreferencesSkin shareSkinData;
	private SharedPreferencesSkin shareMailUserData;
	private ReadConstant myConstant = new ReadConstant();
	private RelativeLayout titleBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);               //ȥ�������ϵ�ͼ��
		super.setContentView(R.layout.send_mail);
		Intent it= getIntent();
		Bundle bundle = it.getExtras();
		//		final String name=it.getStringExtra("name");
		//		final String pass=it.getStringExtra("pass");
		this.mailType = bundle.getInt("sign");
		this.sgin = bundle.getInt("sign");
		this. path=bundle.getString("path");
		this. checkSign=bundle.getBoolean("checkSign");
		this.shareMailUserData = new SharedPreferencesSkin(SendMail.this, myConstant.MAILUSER);
		final String name=this.shareMailUserData.getString(this.myConstant.USERID);
		final String code=this.shareMailUserData.getString(this.myConstant.USERCODE);
		final String stmp=this.shareMailUserData.getString(this.myConstant.USERSTEMP);


		this.shareSkinData = new SharedPreferencesSkin(SendMail.this, myConstant.SKINVIEW);
		this.titleBar = (RelativeLayout)super.findViewById(R.id.send_mail_titlebar);
		this.changeSkin();
		this.sendBtn=(Button)findViewById(R.id.send_mail_sendBtn);
		this.sendFileView = (TextView) super.findViewById(R.id.tosends_file);
		this.fileSizeView = (TextView)super.findViewById(R.id.tosends_file_size);
		this.texttoaddtion=(AutoCompleteTextView)findViewById(R.id.address_edit);
		this.textsubject=(EditText)findViewById(R.id.theme_edit);
		this.textcontent=(EditText)findViewById(R.id.word_edit);
		this.homeBtn = (ImageButton)super.findViewById(R.id.send_mail_home_back);


		this.sendFileView.setText(new File(path).getName());
		this.fileSizeView.setText(this.getFileSize(path));

		this.setMyAdatper();
		
		ReadExitApplication.getInstance().addActivity(this);
		this.sendBtn.setOnClickListener(new View.OnClickListener() {	
			@Override 
			public void onClick(View v) { 
				if(isNetworkAvailable(SendMail.this)){
					if(decide(texttoaddtion.getText().toString()))
					{
						try{
							MailSenderInfo mailsend = new MailSenderInfo();    
//							mailsend.setMailServerHost("smtp.163.com");    
							mailsend.setMailServerHost(stmp);    
							mailsend.setMailServerPort("25");    
							mailsend.setValidate(true);    
							mailsend.setUserName(name);    
							mailsend.setPassword(code);//������������    
							mailsend.setFromAddress(name);    
							mailsend.setToAddress(texttoaddtion.getText().toString());    
							mailsend.setSubject(textsubject.getText().toString());    
							mailsend.setContent(textcontent.getText().toString());  
							mailsend.setAttachFileNames(path);
							SimpleMailSender sms = new SimpleMailSender();   
							// sms.sendTextMail(mailsend);//���������ʽ    
							// sms.sendHtmlMail(mailInfo);//����html��ʽ 
							boolean sendTextMail=sms.sendTextMail(mailsend);  
							if(true==sendTextMail){
								Toast.makeText(SendMail.this,"���ͳɹ�", 8000).show();
							}
							else{
								Toast.makeText(SendMail.this,"����ʧ��", 8000).show();
							}
						} catch (Exception e) { 
							Log.e("SendMail", e.getMessage(), e); 
						}
					}else{
						Toast.makeText(SendMail.this,"�˺���������", 8000).show();
					}
				}else {
					Toast.makeText(SendMail.this,"����������", 8000).show();
					showSetNetworkUI(SendMail.this);
				}

			}
		}); 
		
		this.homeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(SendMail.this,ReadMainActivity.class);
				startActivity(it);
				finish();			
			}
		});
	}

	/************************************************************************/
	/*****                                  ����
	/************************************************************************/
	private void changeSkin(){	
		this.shareSkinData = new SharedPreferencesSkin(SendMail.this, myConstant.FILENAME);
		if(this.shareSkinData.contains(myConstant.SHOWTITLEBG)){	//��������Ѿ��洢������ֱ��ȡ������
			this.titleBar.setBackgroundColor(getResources().getColor(shareSkinData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //�����һ��ʹ��������Ĭ��ֵ����ɫ
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareSkinData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/*****       Ϊ�ռ��������AutoCompleteTextView�����������
	/************************************************************************/
	private void setMyAdatper(){
		final MyAdatper adapter = new MyAdatper(this);
		texttoaddtion.setAdapter(adapter);
		texttoaddtion.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String input = s.toString();
				adapter.mList.clear();
				if (input.length() > 0) {
					for (int i = 0; i < stringArray.length; ++i) {
						adapter.mList.add(input + stringArray[i]);
					}
				}
				adapter.notifyDataSetChanged();
				texttoaddtion.showDropDown();

			}
		});

		texttoaddtion.setThreshold(1);
	}
	/************************************************************************/
	/*****              ����ļ���С
	/************************************************************************/
	private String getFileSize(String path){		
		File file = new File(path);
		String fileSize;
		int fileIntSize =  (int) file.length();
		if(fileIntSize<512000){
			fileIntSize = fileIntSize /1024 ;
			fileSize = fileIntSize +"k";
		}
		else{
			fileIntSize = fileIntSize/1024/1024;
			fileSize = fileIntSize +"M";
		}
		return fileSize;		
	}	

	/************************************************************************/
	/*****          �ж��Ƿ�����������
	/************************************************************************/
	public static boolean isNetworkAvailable(Context context) {  

		ConnectivityManager manager = (ConnectivityManager) context  
				.getApplicationContext().getSystemService(  
						Context.CONNECTIVITY_SERVICE);  

		if (manager == null) {  
			return false;  
		}  

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();  

		if (networkinfo == null || !networkinfo.isAvailable()) {  
			return false;  
		}  

		return true;  
	}  
	
	/************************************************************************/
	/*****          ���������ӽ���
	/************************************************************************/
	 public void showSetNetworkUI(final Context context) {  
	        // ��ʾ�Ի���  
	        AlertDialog.Builder builder = new Builder(context);  
	        builder.setTitle("����������ʾ")  
	                .setMessage("�������Ӳ�����,�Ƿ��������?")  
	                .setPositiveButton("����", new DialogInterface.OnClickListener() {  
	  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                        // TODO Auto-generated method stub  
	                        Intent intent = null;  
	                        // �ж��ֻ�ϵͳ�İ汾 ��API����10 ����3.0�����ϰ汾  
	                        if (android.os.Build.VERSION.SDK_INT > 10) {  
	                            intent = new Intent(  
	                                    android.provider.Settings.ACTION_WIFI_SETTINGS);  
	                        } else {  
	                            intent = new Intent();  
	                            ComponentName component = new ComponentName(  
	                                    "com.android.settings",  
	                                    "com.android.settings.WirelessSettings");  
	                            intent.setComponent(component);  
	                            intent.setAction("android.intent.action.VIEW");  
	                        }  
	                        context.startActivity(intent);  
	                    }  
	                })  
	                .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
	  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	  
	                        dialog.dismiss();  
	                    }  
	                }).show();  
	    }  

	/************************************************************************/
	/*****          �жϽ����˺��Ƿ���ȷ
	/************************************************************************/
	public boolean decide(String receiver){
		String mailreg = "\\w+@\\w+(\\.\\w+)+";
		Pattern pattern = Pattern.compile(mailreg); 	
		Matcher matcher = pattern.matcher(receiver);	
		return matcher.matches();
	}

	/************************************************************************/
	/*****         ��д
	/************************************************************************/
	@Override
	protected void onDestroy() {
		if(!this.checkSign){
			this.shareMailUserData.remove(this.myConstant.USERID);
			this.shareMailUserData.remove(this.myConstant.USERCODE);
			this.shareMailUserData.remove(this.myConstant.USERSTEMP);		
		}
		super.onDestroy();
	}
	
/************************************************************************/
/*****          ��Ӧ������̵���¼�
/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it = new Intent(SendMail.this,ReadMainActivity.class);
			startActivity(it);
			this.finish();
		}
		return false;	
	}

	/************************************************************************/
	/*****                             �����ײ��˵���
	/************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.send_mail_menu, menu);
		return true;
	}

	/************************************************************************/
	/*****                               �ײ��˵������¼�
	/************************************************************************/
	//ע ----> showDialog()����createDialog()��onPrepareDialog()������createDialog()����onCreateDialog()��
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.send_mail_changeuser:
			Intent it = new Intent(SendMail.this,ReadEmailType.class);
			Bundle bundle = new Bundle();	
			bundle.putInt("sign", 0);
			bundle.putString("path", this.path);
			it.putExtras(bundle);
			startActivity(it);
			finish();	
			break;
		case R.id.send_mail_exit:
			new MyDialog(this).createDialog("�˳�", "ȷ���˳���");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
