package net.one.ysng;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


import net.chh.picbrowser.Preview;
import net.wxwen.mail.SendMail;
import net.ysng.reader.MyDialog;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;
import net.ysng.reader.ZoomListenter;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadWordShowActiviy extends Activity {

	private boolean editOr = true;
	private String filePath = null;
	public String htmlPath = null;
	public String fileName;
	private String input;
	private int characterNum;
	private int programNum;
	private int pictureNum;
	private File oneFile;;
	private int screenWidth; // 屏幕宽度
	private static int count = 1;
	private int sign = 1;
	private int signActivity = 0;
	private boolean screenShotSign = false;
	private boolean editSign = false;
	private WebView view;
	final Activity context = this;
	private TextView file_name = null;
	private EditText wordEdit = null;
	private Button toScreenShortBtn;
	private ImageButton homeBtn;
	private ImageButton screenBtn;
	private ImageButton screenCancelBtn;
	private ImageButton screenOkBtn;
	private RelativeLayout titleLayout;
	private RelativeLayout screenshort_layout;
	private ReadConstant myConstant;
	private SharedPreferencesSkin shareSkinData;
	private SharedPreferencesSkin shareMailUserData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.word_show);
		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth() - 10; // 获得屏幕宽度
		this.view = (WebView) findViewById(R.id.read_show_view);
		this.file_name = (TextView) super.findViewById(R.id.read_show_file_name);
		this.toScreenShortBtn = (Button) super.findViewById(R.id.read_show_toscreenshortBtn);
		this.homeBtn = (ImageButton) super.findViewById(R.id.read_show_homeBtn);
		this.screenBtn = (ImageButton) super.findViewById(R.id.read_show_screenshortbtn);
		this.screenCancelBtn = (ImageButton) super.findViewById(R.id.read_show_screenshort_cancel_btn);
		this.screenOkBtn = (ImageButton) super.findViewById(R.id.read_show_screenshort_ok_btn);
		this.titleLayout = (RelativeLayout) super.findViewById(R.id.read_show_titlebar);
		this.screenshort_layout = (RelativeLayout) super.findViewById(R.id.read_show_screenshort_layout);
		this.wordEdit = (EditText) super.findViewById(R.id.read_word_show_edit);

		this.screenBtn.setOnClickListener(new MyClickListener());
		this.homeBtn.setOnClickListener(new MyClickListener());
		this.toScreenShortBtn.setOnClickListener(new MyClickListener());
		this.screenCancelBtn.setOnClickListener(new MyClickListener());
		this.screenOkBtn.setOnClickListener(new MyClickListener());

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		this.filePath = bundle.getString("path");
		this.fileName = bundle.getString("name");
		this.signActivity = bundle.getInt("signActivity");

		this.ReadWay(fileName);
		Log.i("ReadWord", "4");
		this.file_name.setText(this.fileName);
		this.view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		this.view.setHorizontalScrollBarEnabled(false);// 取消Horizontal
		// ScrollBar显示
		WebSettings webSettings = view.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);//

		ReadExitApplication.getInstance().addActivity(this);
		this.oneFile = new File(this.myConstant.ONEFOLDER);
		this.view.getSettings().setDefaultTextEncodingName("UTF-8"); // 设置编码方式
		this.readProgress(); // 加载进度条
		this.view.loadUrl("file:///" + this.myConstant.ONEHTML);
	}

	/************************************************************************/
	/***** * 换肤 
	/************************************************************************/
	private void changeSkin() {
		this.shareSkinData = new SharedPreferencesSkin(
				ReadWordShowActiviy.this, myConstant.FILENAME);
		if (this.shareSkinData.contains(myConstant.SHOWTITLEBG)) { // 如果数据已经存储过，就直接取出来用
			this.titleLayout.setBackgroundColor(getResources().getColor(
					shareSkinData.getInt(myConstant.SHOWTITLEBG)));
		} else { // 如果第一次使用则设置默认值，蓝色
			this.titleLayout.setBackgroundColor(getResources().getColor(
					R.color.show_title_cyan));
			this.shareSkinData.putInt(myConstant.SHOWTITLEBG,
					R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/****** 定义内部类实现OnClickListener接口 
	/************************************************************************/
	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.read_show_screenshortbtn:
				screenShot(screenBtn);
				break;
			case R.id.read_show_toscreenshortBtn:
				toscreenshort();
				break;
			case R.id.read_show_homeBtn:
				Intent it = new Intent(ReadWordShowActiviy.this,ReadMainActivity.class);
				startActivity(it);
				break;
			case R.id.read_show_screenshort_ok_btn:
				Intent intent = new Intent(ReadWordShowActiviy.this, Preview.class);
				startActivity(intent);
				screenBtn.setVisibility(View.GONE);
				screenshort_layout.setVisibility(View.GONE);
				screenShotSign = false;
				break;
			case R.id.read_show_screenshort_cancel_btn:
				createDialog("提示","放弃转换？",0);//1:提示放弃转换对话框
				break;
			default:
				break;
			}
		}
	}

	/************************************************************************/
	/******            编辑模式
	/************************************************************************/
	private void toShowEdit() {
		this.editSign = true;
		int ii = 0;
		String filePathTxt = this.myConstant.ONETXT;
		try {
			InputStream instream = new FileInputStream(filePathTxt);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// 分行读取
				while ((line = buffreader.readLine()) != null) {
					// input += line + "\n";
					if (ii == 0) {
						// input.append(line) ;
						input = line;
						ii++;
					} else {
						input += line;
						// input.append(line) ;
					}
				}
			}
			instream.close();

		} catch (java.io.FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.wordEdit.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
		this.wordEdit.setText(Html.fromHtml(input, imageGetter, null));
		this.wordEdit.setOnTouchListener(new ZoomListenter());

	}

	final Html.ImageGetter imageGetter = new Html.ImageGetter() {

		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			drawable = Drawable.createFromPath(source);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			return drawable;
		}

	};

	/************************************************************************/
	/****** 截图（转换）按钮响应方法 /
	 ************************************************************************/
	private void toscreenshort() {
		this.screenBtn.setVisibility(View.VISIBLE);
		this.screenshort_layout.setVisibility(View.VISIBLE);
		this.screenShotSign = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
	}

	/************************************************************************/
	/*****
	 * 分享文件 /
	 ************************************************************************/
	public void shareMyFile(Context context) {
		String shareStr = "分享一个文件 来自One团队";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, shareStr);
		context.startActivity(Intent.createChooser(intent, "分享到"));
	}

	/************************************************************************/
	/*****
	 * 加载进度条 /
	 ************************************************************************/
	private void readProgress() {
		this.view.setWebViewClient(new WebViewClient() {
			ProgressDialog prDialog;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				prDialog = ProgressDialog.show(ReadWordShowActiviy.this, null,
						"loading, please wait...");
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				prDialog.dismiss();
				super.onPageFinished(view, url);
			}
		});

	}

	/************************************************************************/
	/*****                      选择解析类 
	/ ************************************************************************/
	public void ReadWay(String fileStr) {
		if (fileStr.endsWith(".doc")) { // .doc文件
			ReadDoc readDoc = new ReadDoc(filePath, screenWidth);
			this.characterNum = readDoc.hwpf.characterLength();
			this.programNum = readDoc.range.numParagraphs();
			this.pictureNum = readDoc.pictures.size();

		}

		else if (fileStr.endsWith(".docx")) {
			ReadDocx readDocx = new ReadDocx(filePath, screenWidth);
			this.pictureNum = readDocx.presentPicture;
		}

	}

	/************************************************************************/
	/****** 发送文件 
	/************************************************************************/
	private void sendFile() {
		Intent it = new Intent();
		if (getMialUserInfo()) {
			it.setClass(getApplication(), SendMail.class);
		} else {
			it.setClass(getApplication(), ReadEmailType.class);
		}
		Bundle bundle = new Bundle();
		bundle.putInt("sign", this.sign);
		bundle.putString("path", filePath);
		it.putExtras(bundle);
		startActivity(it);
	}

	private boolean getMialUserInfo() {
		this.shareMailUserData = new SharedPreferencesSkin(getApplication(),
				this.myConstant.MAILUSER);
		if (this.shareMailUserData.contains(this.myConstant.USERID)) {
			this.sign = 1; //用户已保存数据标记
			return true;
		} else {
			this.sign = 0;  //用户未保存数据标记
			return false;
		}
	}

	/************************************************************************/
	/*****
	 * 组件被销毁前调用 /
	 ************************************************************************/
	@Override
	public void onDestroy() {
		 this.deleteFile(this.oneFile);
		super.onDestroy();
	}

	/************************************************************************/
	/*****
	 * 阅读结束删除YIDU文件夹下的所有文件 /
	 ************************************************************************/
	private void deleteFile(File file) {
		
		if(file==null||file.listFiles().length==0){
			return;
		}
		final File allFile[] = file.listFiles();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (File f : allFile) {
					f.delete();
				}
			}
		}).start();
	}

	/************************************************************************/
	/****** 创建底部菜单项 
	/************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.read_show, menu);
		return true;
	}

	/************************************************************************/
	/******底部菜单单击事件 
	/************************************************************************/
	// 注 ---->
	// showDialog()调用createDialog()和onPrepareDialog()，其中createDialog()调用onCreateDialog()。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.show_menu_about_file:
			showDialog(0);
			break;
		case R.id.show_menu_edit:
			if (editOr) {
				view.setVisibility(View.GONE);
				wordEdit.setVisibility(View.VISIBLE);
				toShowEdit();
				editOr = false;
			} else {
				view.setVisibility(View.VISIBLE);
				wordEdit.setVisibility(View.GONE);
				editOr = true;
			}
			break;
		case R.id.show_menu_send_file:
			sendFile();
			break;
		case R.id.show_menu_share:
			shareMyFile(this);
			break;
		case R.id.show_menu_night:
			this.view.setBackgroundColor(Color.parseColor("#404040"));
			break;
		case R.id.show_menu_exit:
			new MyDialog(this).createDialog("退出", "确定要退出？");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void nightTest() {

		// 用户调整显示模式时，直接修改背景色就轻易实现了白天模式与夜间模式的转换：

		// ShowModeView.Background.SetValue(SolidColorBrush.ColorProperty,
		// ColorDefines.TopBackgroundColorNight);
	}

	/************************************************************************/
	/****** 创建Dialog 
	/ ************************************************************************/
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return buildDialogProgram(this);
		}
		return null;
	}

	/************************************************************************/
	/***** 创建关于文件Dialog 
	 /************************************************************************/
	private Dialog buildDialogProgram(Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(this.getResources().getString(R.string.aboutprogram));
		String programInfo = "文件名称 : " + this.fileName + "\n";
		programInfo = programInfo
				+ this.getResources().getString(R.string.word)
				+ this.characterNum + "\n";
		programInfo = programInfo
				+ this.getResources().getString(R.string.paragrap)
				+ this.programNum + "\n";
		programInfo = programInfo
				+ this.getResources().getString(R.string.pictures)
				+ this.pictureNum + "\n";

		builder.setMessage(programInfo);
		builder.setPositiveButton(
				this.getResources().getString(R.string.gotit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		return builder.create();
	}

	/************************************************************************/
	/*****             处理点击返回按钮 
	/ ************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {	
			if(this.screenShotSign){
				this.screenBtn.setVisibility(View.GONE);
				this.screenshort_layout.setVisibility(View.GONE);
				this.screenShotSign = false;
			}else {
				if(this.editSign){
					this.createDialog("提示", "是否保存？",1);//1:提示是否保存对话框
				}else {
					this.returnActivity();
				}		
				
			}
			
	}
		return false;
	}
	
	/************************************************************************/
	/*****             返回上一界面 
	/ ************************************************************************/
	private void returnActivity(){
		Intent it = new Intent();
		switch (this.signActivity) {
		case 0:
			it.setClass(ReadWordShowActiviy.this, ReadFileList.class);
			break;
		case 1:
			it.setClass(ReadWordShowActiviy.this, ReadSearch.class);
			break;
		case 2:
			it.setClass(ReadWordShowActiviy.this, ReadOld.class);
			break;
		default:
			break;
		}
		startActivity(it);
		this.finish();
		
	}

	/************************************************************************/
	/***** * 截图
	/************************************************************************/
	private void screenShot(View v) {
		String fname = this.myConstant.SHOTFOLDER + File.separator + "_"+ count + ".png";
		this.titleLayout.setVisibility(View.INVISIBLE);
		this.screenBtn.setVisibility(View.GONE);
		this.screenshort_layout.setVisibility(View.GONE);
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();

		View view = v.getRootView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight + 10, width,
				height - statusBarHeight - 10);

		if (b != null) {
			try {
				File sdfile = new File(this.myConstant.SHOTFOLDER);
				if (!sdfile.exists()) {
					sdfile.mkdir();
				}

				FileOutputStream out = new FileOutputStream(fname);
				b.compress(Bitmap.CompressFormat.PNG, 100, out);
				Uri data = Uri.parse("file://"
						+ Environment.getExternalStorageDirectory()
						+ "/One/OneScreenshot/_" + count + ".PNG");
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						data));

				Toast.makeText(this, "截图成功！", Toast.LENGTH_SHORT).show();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "截图失败！", Toast.LENGTH_SHORT).show();
		}

		this.titleLayout.setVisibility(View.VISIBLE);
		this.screenBtn.setVisibility(View.VISIBLE);
		this.screenshort_layout.setVisibility(View.VISIBLE);

	}

	private void imageToPdf(String name) {
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (new ReadImagetoPDF(dm, name).ImagetoPDF()) {
			Toast.makeText(this, "文件存储地址为" + this.myConstant.PDFFOLDER,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "转换失败......", Toast.LENGTH_SHORT).show();
		}
	}

	/************************************************************************/
	/***** * 创建Dialog 
	/ ************************************************************************/
	public void createDialog(String title, String meg,final int sign) {

		Dialog dialog = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(meg)
		.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (sign) {
				case 0:
					screenBtn.setVisibility(View.GONE);
					screenshort_layout.setVisibility(View.GONE);
					screenShotSign = false;
					break;
				case 1:
					write(wordEdit.getText().toString());
					returnActivity();
					break;
				default:
					break;
				}		
			}
		})
		.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(sign==1){
					returnActivity();
				}
			}
		}).create();

		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		dialog.show();
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
	/*****                                保存
	/************************************************************************/
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(!editOr){
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			Toast.makeText(this, "响应触摸事件", Toast.LENGTH_LONG).show();	
			insertText(this.wordEdit,"[注释：]");
			}

		}	
		return false;		
	}
    private int getEditTextCursorIndex(EditText mEditText){  
		return this.wordEdit.getSelectionStart();  
		}  
		/**向EditText指定光标位置插入字符串*/  
    private void insertText(EditText mEditText, String mText){  
    	this.wordEdit.getText().insert(getEditTextCursorIndex(this.wordEdit), mText);   
		}  
	


}
