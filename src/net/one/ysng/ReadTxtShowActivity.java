package net.one.ysng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 打开文件的方法
 * @author Administrator
 *
 */
public class ReadTxtShowActivity extends Activity {
	
	private String filePath;
	private String fileName;
	private String encoding;
	private int signActivity = 0;
	//private static final String gb2312 = "GB2312";
	private static final String utf8 = "UTF-8";
	private static final String defaultCode = "GB2312";
	private RelativeLayout titleLayout;
	private ReadConstant myConstant ;
	private SharedPreferencesSkin shareSkinData;
	private ImageButton homeBtn;
	private EditText editShow;
	private TextView fileNameView;
	//String encoding// = getCharSet(path);//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);                                                 //去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_text);
		this.titleLayout = (RelativeLayout) super.findViewById(R.id.read_text_titlebar);
		this.fileNameView = (TextView) super.findViewById(R.id.read_text_file_name);
		this.editShow = (EditText) findViewById(R.id.read_text_contents);
		this.homeBtn = (ImageButton) super.findViewById(R.id.read_text_homeBtn);
		try {
			Bundle bunde = this.getIntent().getExtras();
			filePath = bunde.getString("path");
			fileName = bunde.getString("name");
			this.signActivity = bunde.getInt("signActivity");
			//encoding=tv.getText().toString();
			refreshGUI(utf8);
		} catch (Exception e) {
		}
		this.fileNameView.setText(fileName);
		ReadExitApplication.getInstance().addActivity(this);
		
		this.homeBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {			
				Intent it = new Intent(ReadTxtShowActivity.this,ReadMainActivity.class);
				startActivity(it);
		        finish();
			}
		});
		
	}
	
	private String getCharSet(String filenameString2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
	}
	
	
	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){	
		this.shareSkinData = new SharedPreferencesSkin(this, myConstant.FILENAME);
		if(this.shareSkinData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleLayout.setBackgroundColor(getResources().getColor(shareSkinData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleLayout.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareSkinData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gb2312:
			refreshGUI(defaultCode);
			break;
		case R.id.utf8:
			refreshGUI(utf8);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void refreshGUI(String code)
	{
		
		String fileContent = getStringFromFile(code);
		editShow.setText(fileContent);
	}
	
	public String getStringFromFile(String code)
	{
		try {
			StringBuffer sBuffer = new StringBuffer();
			FileInputStream fInputStream = new FileInputStream(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, code);
			BufferedReader in = new BufferedReader(inputStreamReader);
			if(!new File(filePath).exists())
			{
				return null;
			}
			while (in.ready()) {
				sBuffer.append(in.readLine() + "\n");
			}
			in.close();
			return sBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/************************************************************************/
	/*****                                读取文件内容
	/************************************************************************/
	public byte[] readFile(String fileName) throws Exception {
		byte[] result = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			fis = new FileInputStream(file);
			result = new byte[fis.available()];
			fis.read(result);
		} catch (Exception e) {
		} finally {
			fis.close();
		}
		return result;
	}
	
	/************************************************************************/
	/*****                                保存
	/************************************************************************/
	private void write(String content){

		FileOutputStream fileOutputStream = null;
		File file = new File(this.filePath);
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes());
			fileOutputStream.close();
			Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	/************************************************************************/
	/*****             处理点击返回按钮 
	/ ************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			write(this.editShow.getText().toString());
			Intent it = new Intent();
			switch (signActivity) {
			case 0:
				it.setClass(this, ReadFileList.class);
				break;
			case 1:
				it.setClass(this, ReadSearch.class);
				break;
			case 2:
				it.setClass(this, ReadOld.class);
				break;
			default:
				break;
			}
			startActivity(it);
			this.finish();
		}
		return false;
	}
	
	

}
