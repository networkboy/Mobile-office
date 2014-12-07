package net.one.ysng;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


import net.chh.picbrowser.Preview;
import net.one.ysng.R;
import net.one.ysng.ReadXls;
import net.wxwen.mail.SendMail;
import net.ysng.reader.CustomScrollView;
import net.ysng.reader.MultiPointTouchListener;
import net.ysng.reader.MyDialog;
import net.ysng.reader.MyRowView;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ReadExcelShowActivity extends Activity {

	private RelativeLayout titleLayout;
	private LinearLayout mGridHeaderLayout;
	private LinearLayout mSheetBarLayout;
	private LinearLayout mColumnHeaderLayout;
	private LinearLayout mRowHeaderLayout;
	private LinearLayout mContentLayout;
	private LinearLayout mExcelLayout;
	private LinearLayout mUpLayout;
	private LinearLayout mDownLayout;
	private HorizontalScrollView mColumnHeaderScroll;
	private ScrollView mRowHeaderScroll;
	private CustomScrollView mContentScroll;
	private ScrollView myScroll;
	private FixedGridMultiPointTouchListener mOnTouchListener;
	private boolean mScalable;
	private ArrayList<ArrayList<String>> list =null;
	private ArrayList<ArrayList<String>> list_set = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<ArrayList<String>>> myList  = null;
	private ArrayList<String> listX=null;
	private ArrayList<Button> listSheetBar=null;
	private MyRowView myRowView =null;
	private Button toScreenShortBtn;
	private ImageButton  homeBtn;
	private ImageButton screenBtn;
	private ImageButton screenCancelBtn;
	private ImageButton screenOkBtn;
	private TextView titleFileName = null;
	private ReadConstant myConstant = new ReadConstant();
	private RelativeLayout screenshort_layout;
	private SharedPreferencesSkin shareData;
	private SharedPreferencesSkin shareMailUserData;
	private LinearLayout sheetLayout = null;

	private static final int HEADER_WIDTH = 160;
	private static final int HEADER_HEIGHT = 40;
	private static final int CONTENT_WIDTH = 160;
	private static final int CONTENT_HEIGHT = 40;
	private  int  conNubner = 0;
	private int   gridNumber = 0;
	private int   signActivity = 0;
	private int count = 1;
	private int screenShotNum = 0;
	private int sign = 1;
	private boolean signX;
	private String filePath;
	private String fileName ;
	private static final String tiStr[] = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
		"Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK",
		"AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};
	private int StrNum[] = null;



	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		requestWindowFeature(Window.FEATURE_NO_TITLE);              //去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.excel_show);

		this.list =  new ArrayList<ArrayList<String>>();
		this.myList = new ArrayList<ArrayList<ArrayList<String>>>();
		this.listX = new ArrayList<String>();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		this.filePath = bundle.getString("path");
		this.fileName = bundle.getString("name");
		this.signActivity = bundle.getInt("signActivity");
		this.intWidget(this.filePath,this.fileName);

		this.myRowView = new MyRowView(this);
		this.mExcelLayout = (LinearLayout)super.findViewById(R.id.excel_test_layouts);
		this.mUpLayout = (LinearLayout)super.findViewById(R.id.layout_1);
		this.mDownLayout = (LinearLayout)super.findViewById(R.id.layout_2);
		this.titleLayout = (RelativeLayout) super.findViewById(R.id.excel_test_titlebar);
		this.homeBtn = (ImageButton)super.findViewById(R.id.excel_test_homeBtn);
		this.toScreenShortBtn =(Button)super.findViewById(R.id.excel_test_toscreenshortBtn);
		this.screenBtn = (ImageButton)super.findViewById(R.id.excel_test_screenshortbtn);
		this.screenCancelBtn = (ImageButton) super.findViewById(R.id.excel_test_screenshort_cancel_btn);
		this.screenOkBtn = (ImageButton) super.findViewById(R.id.excel_test_screenshort_ok_btn);
		this.titleFileName = (TextView) super.findViewById(R.id.excel_test_file_name);
		this.sheetLayout = (LinearLayout)findViewById(R.id.excel_test_sheetbar);
		this.screenshort_layout = (RelativeLayout) super.findViewById(R.id.excel_test_screenshort_layout);
		this.changeSkin();
		try {	
			this.addmUpLayout();
			this.addmDownLayout();
			//this.addScroll();
			//this.addSheetBarLayout();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}


		mScalable = true;

		mOnTouchListener = new FixedGridMultiPointTouchListener();
		mContentScroll.setOnTouchListener(mOnTouchListener);
		ReadExitApplication.getInstance().addActivity(this);
		this.titleFileName.setText(this.fileName);
		
		this.homeBtn.setOnClickListener(new MyClickListener());
		this.toScreenShortBtn.setOnClickListener(new MyClickListener());
		this.screenBtn.setOnClickListener(new MyClickListener());
		this.screenCancelBtn.setOnClickListener(new MyClickListener());
		this.screenOkBtn.setOnClickListener(new MyClickListener());

	}

	private void intWidget(String path,String name) {
		if(name.endsWith(".xls")){  
			this.signX = true;
			ReadXls readXls = new ReadXls(path);	
			try {
				this.list = readXls.readXLS();
				this.gridNumber = this.list.size()+50;
				this.list_set = readXls.list_set;
				this.StrNum = readXls.StrNum;	
				this.myList = readXls.strList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(name.endsWith(".xlsx")){
			this.signX = false;
			ReadXlsx readXlsx = new ReadXlsx(path);
			try {
				this.listX = readXlsx.readXLSX();
				this.gridNumber = readXlsx.gridNumber + 50;
				this.StrNum = readXlsx.StrNum;	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){	
		this.shareData = new SharedPreferencesSkin(ReadExcelShowActivity.this,myConstant.FILENAME);
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleLayout.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleLayout.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}
	
	/************************************************************************/
	/****** 定义内部类实现OnClickListener接口 
	/************************************************************************/
	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.excel_test_homeBtn:
				Intent it = new Intent(ReadExcelShowActivity.this,ReadMainActivity.class);
				startActivity(it);
				finish();			
				break;
			case R.id.excel_test_toscreenshortBtn:
					screenBtn.setVisibility(View.VISIBLE);
					screenshort_layout.setVisibility(View.VISIBLE);
				break;
			case R.id.excel_test_screenshortbtn:
				screenShot(screenBtn);
				break;
			case R.id.excel_test_screenshort_cancel_btn:
				createDialog("提示","放弃转换？",0);//1:提示放弃转换对话框
				break;
			case R.id.excel_test_screenshort_ok_btn:				
				Intent intent = new Intent(ReadExcelShowActivity.this, Preview.class);
				startActivity(intent);
				screenBtn.setVisibility(View.GONE);
				screenshort_layout.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	}


	private void addmUpLayout() throws CloneNotSupportedException{
		addGridHeader();
		addColumnHeader();	
	}
	private void addmDownLayout() throws CloneNotSupportedException{
		addRowHeader();
		addContent();	
		Log.i("addContent()", "addContent()后");
	}
	private void addSheetBarLayout(){
		addSheetBar();
	}
	private void addScroll(){
		myScroll = new ScrollView(this) {
			public boolean onTouchEvent(MotionEvent ev) {
				return false;
			};
		};
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(1, 1, 1, 1);
		myScroll.setLayoutParams(layoutParams);
		myScroll.setVerticalScrollBarEnabled(false);
		myScroll.setVerticalFadingEdgeEnabled(false);
		myScroll.addView(mUpLayout);
		myScroll.addView(mDownLayout);


	}
	
	/*****************************************************************************************
	 * 功能：添加SheetBar布局
	 * 
	 * @param mSheetBarLayout ：SheetBa布局
	 *                sheetLayout              ：SheetBa布局
	 * @return     null
	 * @throws CloneNotSupportedException 
	 *****************************************************************************************/
	private void addSheetBar(){
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 0);
		mSheetBarLayout = getSheetBar();
		mSheetBarLayout.setLayoutParams(layoutParams);
		mSheetBarLayout.setBackgroundColor(getResources().getColor(R.color.excel_header));
		sheetLayout.addView(mSheetBarLayout);

	}

	/*****************************************************************************************
	 * 功能：设置heetBar布局
	 * 
	 * @return     LinearLayout
	 * @throws null
	 *****************************************************************************************/
	private LinearLayout getSheetBar(){

		LinearLayout sheetLayout = new LinearLayout(this);
		sheetLayout.setOrientation(LinearLayout.HORIZONTAL);

		for(int i=0; i<3; i++){
			LinearLayout.LayoutParams layoutParams = null;
			layoutParams = new LinearLayout.LayoutParams(120, HEADER_HEIGHT);	

			layoutParams.setMargins(0, 0, 0, 0);
			Button header = new Button(this);		  
			header.setLayoutParams(layoutParams);
			header.setBackgroundDrawable(getResources().getDrawable(R.drawable.sheetbar_unselected));
			header.setGravity(Gravity.CENTER);
			header.setText("Sheet");
			sheetLayout.addView(header);
		}
		return sheetLayout;

	}

	/*****************************************************************************************
	 * 功能：给第一个布局里加入Excel左上角布局
	 * 
	 * @param mGridHeaderLayout ：Excel左上角布局
	 *                mUpLayout              ：第一布局
	 * @return     null
	 * @throws CloneNotSupportedException 
	 *****************************************************************************************/
	private void addGridHeader() throws CloneNotSupportedException {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(1, 1, 1, 1);
		mGridHeaderLayout = getGridHeader();
		mGridHeaderLayout.setLayoutParams(layoutParams);
		mUpLayout.addView(mGridHeaderLayout);
	}

	/*****************************************************************************************
	 * 功能：给第个布局里加入Excel列表头布局
	 * 
	 * @param mColumnHeaderLayout ：Excel列表头布局
	 *                mUpLayout              ：第一布局
	 * @return     null
	 * @throws CloneNotSupportedException 
	 *****************************************************************************************/
	private void addColumnHeader() throws CloneNotSupportedException {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(1, 1, 1, 1);
		mColumnHeaderScroll = new HorizontalScrollView(this) {
			public boolean onTouchEvent(MotionEvent ev) {
				return false;
			};
		}; 
		mColumnHeaderScroll.setLayoutParams(layoutParams);
		mColumnHeaderScroll.setHorizontalScrollBarEnabled(false);
		mColumnHeaderScroll.setHorizontalFadingEdgeEnabled(false);
		mColumnHeaderLayout = getColumnHeader();
		mColumnHeaderScroll.addView(mColumnHeaderLayout);
		mUpLayout.addView(mColumnHeaderScroll);
	}

	/*****************************************************************************************
	 * 功能：给第二个布局里加入Excel行号头布局
	 * 
	 * @param mRowHeaderLayout ：Excel行号头布局
	 *                mDownLayout              ：第一布局
	 * @return     null
	 * @throws CloneNotSupportedException 
	 *****************************************************************************************/
	private void addRowHeader() throws CloneNotSupportedException {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		layoutParams.setMargins(1, 1, 1, 1);
		mRowHeaderScroll = new ScrollView(this) {
			public boolean onTouchEvent(MotionEvent ev) {
				return false;
			};
		};
		mRowHeaderScroll.setLayoutParams(layoutParams);
		mRowHeaderScroll.setVerticalScrollBarEnabled(false);
		mRowHeaderScroll.setVerticalFadingEdgeEnabled(false);
		mRowHeaderLayout = getRowHeader();
		mRowHeaderScroll.addView(mRowHeaderLayout);
		mDownLayout.addView(mRowHeaderScroll);
	}

	/*****************************************************************************************
	 * 功能：给第二个布局里加入Excel内容布局
	 * 
	 * @param mContentLayout ：Excel内容布局
	 *                mDownLayout              ：第一布局
	 * @return     null
	 * @throws CloneNotSupportedException 
	 *****************************************************************************************/
	private void addContent() throws CloneNotSupportedException {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		layoutParams.setMargins(1, 1, 1, 1);
		mContentScroll = new CustomScrollView(this) {
			public void scrollTo(int x, int y) {
				super.scrollTo(x, y);
				mRowHeaderScroll.scrollTo(0, y);
				mColumnHeaderScroll.scrollTo(x, 0);
			};

			public void fling(int velocityX, int velocityY) {
				super.fling(velocityX, velocityY);
				mRowHeaderScroll.fling(velocityY);
				mColumnHeaderScroll.fling(velocityX);
			};
		};
		mContentScroll.setFlingEnabled(false);
		mContentScroll.setLayoutParams(layoutParams);
		if(signX){
			mContentLayout = getContent();		
		}else{ 
			mContentLayout = getContentX();
		}

		mContentScroll.addView(mContentLayout);
		mDownLayout.addView(mContentScroll);
	}

	/*****************************************************************************************
	 * 功能：设置Excel左上角布局布局
	 * 
	 * @param    headerLayout ：Excel左上角布局
	 * @return     LinearLayout
	 * @throws   CloneNotSupportedException 
	 *****************************************************************************************/
	protected LinearLayout getGridHeader() throws CloneNotSupportedException {
		LinearLayout headerLayout = new LinearLayout(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, HEADER_HEIGHT);
		layoutParams.setMargins(0, 0, 0, 0);

		MyRowView textView = myRowView.clone();
		//MyEditText textView = myEditText.clone();
		//EditText textView = new EditText(this);
		textView.setBackgroundColor(getResources().getColor(R.color.excel_header));
		textView.setLayoutParams(layoutParams);
		textView.setGravity(Gravity.CENTER);
		textView.setText("    ");
		headerLayout.addView(textView);
		return headerLayout;
	}

	/*****************************************************************************************
	 * 功能：设置Excel表头布局
	 * 
	 * @param    headerLayout ：Excel表头布局
	 * @return     LinearLayout
	 * @throws   CloneNotSupportedException 
	 *****************************************************************************************/
	protected LinearLayout getColumnHeader() throws CloneNotSupportedException{
		LinearLayout headerLayout = new LinearLayout(this);
		headerLayout.setOrientation(LinearLayout.HORIZONTAL);
		for(int i=0; i<26; i++){
			LinearLayout.LayoutParams layoutParams = null;
			layoutParams = new LinearLayout.LayoutParams(this.StrNum[i]*18, HEADER_HEIGHT);	
			//  layoutParams = new LinearLayout.LayoutParams(120, CONTENT_HEIGHT);
			layoutParams.setMargins(0, 0, 1, 0);
			MyRowView header = myRowView.clone();		  
			header.setBackgroundColor(getResources().getColor(R.color.excel_header));
			header.setLayoutParams(layoutParams);
			header.setGravity(Gravity.CENTER);
			header.setText(tiStr[i]);
			headerLayout.addView(header);
		}
		return headerLayout;
	}

	/*****************************************************************************************
	 * 功能：设置Excel行头布局
	 * 
	 * @param    headerLayout ：Excel行头布局
	 * @return     LinearLayout
	 * @throws   CloneNotSupportedException 
	 *****************************************************************************************/
	protected LinearLayout getRowHeader() throws CloneNotSupportedException {
		LinearLayout headerLayout = new LinearLayout(this);
		headerLayout.setOrientation(LinearLayout.VERTICAL);

		//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


		for(int i=1; i< this.gridNumber;i++ ){
			//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.StrNum[i]*17, CONTENT_HEIGHT);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, CONTENT_HEIGHT);
			layoutParams.setMargins(0, 0, 0, 1);
			MyRowView header = myRowView.clone();
			header.setBackgroundColor(getResources().getColor(R.color.excel_header));
			header.setLayoutParams(layoutParams);
			header.setGravity(Gravity.CENTER);
			header.setText(""+ i);
			headerLayout.addView(header);
		}
		return headerLayout;
	}

	/*****************************************************************************************
	 * 功能：设置Excel内容布局xls
	 * 
	 * @param    contentLayout ：Excel内容布局
	 * @return     LinearLayout
	 * @throws   CloneNotSupportedException 
	 *****************************************************************************************/
	protected LinearLayout getContent() throws CloneNotSupportedException {
		LinearLayout contentLayout = new LinearLayout(this);
		contentLayout.setOrientation(LinearLayout.VERTICAL);

		for(int r=0; r< this.gridNumber; r++){
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			ArrayList<String> mList = new ArrayList<String>();
			ArrayList<String> mList_set = new ArrayList<String>();
			if(r<list.size()){
				mList= this.list.get(r);
				mList_set = this.list_set.get(r);
			}
			for(int c=0; c<26; c++){
				LinearLayout.LayoutParams layoutParams = null;
				layoutParams = new LinearLayout.LayoutParams(this.StrNum[c]*18, HEADER_HEIGHT);	
				//				 layoutParams = new LinearLayout.LayoutParams(120, CONTENT_HEIGHT);
				layoutParams.setMargins(0, 0, 1, 1);
				MyRowView content = myRowView.clone();
				//			MyEditText content = myEditText.clone();
				//EditText content = new EditText(this);
				content.setBackgroundColor(Color.WHITE);
				content.setTextSize(12);
				content.setLayoutParams(layoutParams);

				if(c<mList.size()&&mList!=null){
					content.setText(mList.get(c));
				}else {
					content.setText(" ");
				}
				row.addView(content);
			}
			contentLayout.addView(row);
		}

		return contentLayout;
	}

	/*****************************************************************************************
	 * 功能：设置Excel内容布局xlsx
	 * 
	 * @param    contentLayout ：Excel内容布局
	 * @return     LinearLayout
	 * @throws   CloneNotSupportedException 
	 *****************************************************************************************/
	protected LinearLayout getContentX() throws CloneNotSupportedException {
		int index = 0;
		LinearLayout contentLayout = new LinearLayout(this);
		contentLayout.setOrientation(LinearLayout.VERTICAL);

		for(int r=0; r< this.gridNumber; r++){
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);	
			for(int c=0; c<26; c++){

				MyRowView content = myRowView.clone();
				content.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams layoutParams = null;
				layoutParams = new LinearLayout.LayoutParams(this.StrNum[c]*18, HEADER_HEIGHT);		
				//		layoutParams = new LinearLayout.LayoutParams(120, HEADER_HEIGHT);
				layoutParams.setMargins(0, 0, 1, 1);
				content.setLayoutParams(layoutParams);

				if(index<listX.size()){
					content.setText(listX.get(index++));
				}else {
					content.setText("  ");    	  
				}	

				row.addView(content);
			}
			contentLayout.addView(row);
		}
		return contentLayout;
	}

	/************************************************************************/
	/*****                               分享文件
	/************************************************************************/
	public void shareMyFile(Context context) {
		String shareStr = "分享一个文件 来自One团队";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, shareStr);
		context.startActivity(Intent.createChooser(intent, "分享到"));
	}

	/************************************************************************/
	/*****               发送文件
	/************************************************************************/
	private void sendFile(){
		Intent it = new Intent();
		if(getMialUserInfo()){
			it.setClass(getApplication(), SendMail.class);
		}else {
			it.setClass(getApplication(), ReadEmailType.class);
		}
		Bundle bundle = new Bundle();	
		bundle.putInt("sign", this.sign);
		bundle.putString("path", filePath);
		it.putExtras(bundle);
		startActivity(it);				
	}

	private boolean getMialUserInfo(){
		this.shareMailUserData = new SharedPreferencesSkin(getApplication(), this.myConstant.MAILUSER);
		if(this.shareMailUserData.contains(this.myConstant.USERID)){
			this.sign = 1;
			return true;  
		}else {
			this.sign = 0;
			return false;
		}	  
	}

	/************************************************************************/
	/*****                             创建底部菜单项
		/************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.read_excel_show, menu);
		return true;
	}

	/************************************************************************/
	/*****                               底部菜单单击事件
		/************************************************************************/
	//注 ----> showDialog()调用createDialog()和onPrepareDialog()，其中createDialog()调用onCreateDialog()。
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.excel_show_menu_about_file:
			showDialog(0);	
			break;
		case R.id.excel_show_menu_send_file:
			sendFile();
			break;
		case R.id.excel_show_menu_share:
			shareMyFile(this);
			break;
		case R.id.excel_show_menu_night:
			this.mDownLayout.setBackgroundColor(Color.parseColor("#404040"));
			break;
		case R.id.excel_show_menu_exit:
			new MyDialog(this).createDialog("退出","确定要退出？");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/************************************************************************/
	/*****                              创建Dialog
		/************************************************************************/
	@Override
	protected Dialog onCreateDialog(int id){
		switch(id){
		case 0:
			return buildDialogProgram(this);	
		}
		return null;
	}


	/************************************************************************/
	/*****                              创建关于文件Dialog
		/************************************************************************/
	private Dialog buildDialogProgram(Context context){

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(this.getResources().getString(R.string.aboutprogram));
		//builder.setIcon(this.getResources().getDrawable(R.drawable.my_logo));
		String programInfo = "文件名称 : " + this.fileName +"\n";
		//programInfo = programInfo + "文件大小："+  + "\n";


		builder.setMessage(programInfo);
		builder.setPositiveButton(this.getResources().getString(R.string.gotit), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		return builder.create();
	}


	/************************************************************************/
	/*****          响应物理键盘点击事件
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it = new Intent();
			switch (signActivity) {
			case 0:
				it.setClass(ReadExcelShowActivity.this,ReadFileList.class);
				break;
			case 1:
				it.setClass(ReadExcelShowActivity.this,ReadSearch.class);
				break;
			case 2:
				it.setClass(ReadExcelShowActivity.this,ReadOld.class);
				break;
			default:
				break;
			}
			startActivity(it);
			this.finish();
		}
		return false;	
	}


	/*****************************************************************************************************/
	private class FixedGridMultiPointTouchListener extends MultiPointTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(!super.onTouch(v, event)){
				//				return mContentScroll.onTouchEvent(event);
				return mExcelLayout.onTouchEvent(event);
			}
			return true;
		}

		@Override
		protected boolean onScale(float oldScale, float lastScale, float newScale, final float clearScale, float midX, float midY){
			if(!mScalable){
				return false;
			}
		//	Log.e("测试...", "onScale: " + oldScale + ", " + lastScale + ", " + newScale + ", " + clearScale + ", " + midX + ", " + midY);

			ScaleAnimation animation = new ScaleAnimation(lastScale, newScale, lastScale, newScale);
			//Log.e(LOG_TAG, animation + " ==== scaling: " + lastScale + "==>" + newScale);
			animation.setAnimationListener(new ScaleAnimation.AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					//					printSize(FixedGrid.this, "gird");
					//					printSize(mRowHeaderScroll, "rows");
					//					printSize(mRowHeaderLayout, "rowl");
					//					FixedGrid.this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
				}

				private void printSize(View v, String tag){
			//		Log.e("测试...", tag + ": " + v.getWidth() + ", " + v.getHeight());
				}
			});
			animation.setFillAfter(true);
			animation.setDuration(1);
			//			startAnimation(animation);
			LayoutAnimationController controller = new LayoutAnimationController(animation);
			//			if(newScale < 1){
			//				//setLayoutParams(new LinearLayout.LayoutParams((int)(800 / newScale), (int)(535 / newScale)));
			//			}else{
			//				//setLayoutParams(new LinearLayout.LayoutParams(800, (int)(535 / newScale)));
			//			}
			mExcelLayout.setLayoutAnimation(controller);
			animation.reset();
			mExcelLayout.clearAnimation();
			mExcelLayout.invalidate();

			return true;
		}
	}

	/************************************************************************/
	/*****                 截图
	/************************************************************************/
	private void screenShot(View v) {
		//String fname = "/sdcard/Screenshot/_" + count + ".png";
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

	/************************************************************************/
	/****** 创建Dialog 
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
					break;
				case 1:
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
	/*****             返回上一界面 
	/ ************************************************************************/
	private void returnActivity(){
		Intent it = new Intent();
		switch (this.signActivity) {
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

	private void imageToPdf(String fileNmae){
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(new ReadImagetoPDF(dm,fileNmae).ImagetoPDF()){
			Toast.makeText(this, "文件存储地址为" + this.myConstant.PDFFOLDER , Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "转换失败......", Toast.LENGTH_SHORT).show();
		}
	}



}
