package net.one.ysng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.artifex.mupdf.MuPDFActivity;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesOld;
import net.ysng.reader.SharedPreferencesSkin;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class ReadSearch extends Activity {
	private ListView listV = null;
	private List<File> file_list = null;
	private List<File> wordList = null;
	private List<File> excelList = null;
	private List<File> pdfList = null;
	private int pic[] = { R.drawable.file_icon_doc, R.drawable.file_icon_xls,R.drawable.file_icon_pdf };
	private ArrayList<HashMap<String, Object>> recordItem;
	private  SimpleAdapter adapter = null;
	private HashMap<String, ?> oldMap;
	private RelativeLayout titleBar;
	private LinearLayout searchLayout;
	private SharedPreferencesOld sharedOld;
	private SharedPreferencesSkin shareSkinData;
	private SharedPreferencesSkin shareSearchData;
	private ReadConstant myConstant;
	private EditText searchEdit;
	private ImageButton homeBtn;
	private ImageButton searchBtn;
	//private Button setBtn;
	private boolean showWord;
	private boolean showExcel;
	private boolean showPdf;
	private boolean showSearchLayout = true;


	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ȥ�������ϵ�ͼ��
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_search);

		this.listV = (ListView) findViewById(R.id.read_search_list);
		this.titleBar = (RelativeLayout) super.findViewById(R.id.read_search_titlebar);
		this.searchLayout = (LinearLayout) super.findViewById(R.id.searh_layout);
		this.homeBtn = (ImageButton) super.findViewById(R.id.read_search_home_back);
		this.searchEdit = (EditText) super.findViewById(R.id.read_search_edit);
		this.searchBtn = (ImageButton) super.findViewById(R.id.read_search_btn);
		//this.setBtn = (Button)super.findViewById(R.id.read_search_set);
		this.file_list = new ArrayList<File>();
		this.wordList = new ArrayList<File>();
		this.excelList = new ArrayList<File>();
		this.pdfList = new ArrayList<File>();
		this.recordItem = new ArrayList<HashMap<String, Object>>();
		this.sharedOld = new SharedPreferencesOld(ReadSearch.this,myConstant.OLDNAME);
		this.shareSearchData = new SharedPreferencesSkin(ReadSearch.this,myConstant.SEARCHDATE);
		this.listV.setOnItemClickListener(new Clicker()); // ����
		super.registerForContextMenu(this.listV); // ע�������Ĳ˵�
		this.showInfo();
		this.changeSkin();
		this.fill();
		ReadExitApplication.getInstance().addActivity(this);

		this.homeBtn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				//				Intent it = new Intent(ReadSearch.this,Main.class);
				Intent it = new Intent(ReadSearch.this,ReadMainActivity.class);
				startActivity(it);
				finish();			
			}
		});

		//		this.setBtn.setOnClickListener(new OnClickListener() {		
		//			@Override
		//			public void onClick(View v) {
		//				Intent it = new Intent(ReadSearch.this,ReadSet.class);	
		//				startActivity(it);
		//				finish();			
		//			}
		//		});

		this.searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean searchSign = true;
				String str = searchEdit.getText().toString();
				if(str.length()!=0){ 
					for(int i=0;i<file_list.size();i++){
						if(file_list.get(i).getName().startsWith(str)){	
							listV.setSelectionFromTop(i, i); 
							searchSign = false;
						}
					}	
					if(searchSign){
						Toast.makeText(getApplication(), "�ļ�������", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplication(), "�������ļ���", Toast.LENGTH_SHORT).show();
					
				}
			}
		});
	}

	private void showInfo(){
		if(this.shareSearchData.contains(this.myConstant.SEARCHWORD)){
			if(this.shareSearchData.getInt(this.myConstant.SEARCHWORD)==R.drawable.switch_on){
				this.showWord = true;
			}else {
				this.showWord = false;
			}

			if(this.shareSearchData.getInt(this.myConstant.SEARCHEXCEL)==R.drawable.switch_on){
				this.showExcel = true;
			}else {
				this.showExcel = false;
			}

			if(this.shareSearchData.getInt(this.myConstant.SEARCHPDF)==R.drawable.switch_on){
				this.showPdf = true;
			}else {
				this.showPdf = false;
			}
		}else {
			this.showPdf = true;
			this.showExcel = true;
			this.showWord = true;
		}
	}

	/************************************************************************/
	/*****          ��дonResume()����
	/************************************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
		this.showInfo();
	}

	/************************************************************************/
	/***** ���� /
	 ************************************************************************/
	private void changeSkin() {
		this.shareSkinData = new SharedPreferencesSkin(ReadSearch.this,
				myConstant.FILENAME);
		if (this.shareSkinData.contains(myConstant.SHOWTITLEBG)) { // ��������Ѿ��洢������ֱ��ȡ������
			this.titleBar.setBackgroundColor(getResources().getColor(
					shareSkinData.getInt(myConstant.SHOWTITLEBG)));
		} else { // �����һ��ʹ��������Ĭ��ֵ����ɫ
			this.titleBar.setBackgroundColor(getResources().getColor(
					R.color.show_title_cyan));
			this.shareSkinData.putInt(myConstant.SHOWTITLEBG,
					R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/******   ΪListView������� ʵ��ListView��ʾ
   / ************************************************************************/
	public void fill() {
		//final ProgressDialog proDialog = ProgressDialog.show(ReadSearch.this, "�����ļ�","�����ĵȴ�...");
		searchFile(myConstant.PARENTPATH);
		//proDialog.dismiss();
		if(showWord){
			fileArray(this.wordList);		
		}
		if(showExcel){
			fileArray(this.excelList);
		}	
		if(showPdf){
			fileArray(this.pdfList);
		}


		this.adapter = new SimpleAdapter(this, this.recordItem,
				R.layout.read_file_list_layout, 
				new String[] { "file_icon", "file_name","file_time" },
				new int[] { R.id.file_icon,
				R.id.file_name, R.id.file_time });

		this.listV.setAdapter(adapter);

	}


	private void searchFile(File file) {
		if (file.isDirectory()&&!file.getName().startsWith(".")){
			File fileList[] = file.listFiles();
			for (File f : fileList) {
				if(f.isDirectory()){
					searchFile1(f);
				}else {
					tests(f);
				}
			}
		}
	}
	private void searchFile1(File file) {   
		if (file.isDirectory()&&!file.getName().startsWith(".")){
			File fileList[] = file.listFiles();
			for (File f : fileList) {
				if(f.isDirectory()){
					searchFile2(f);
				}else {
					tests(f);
				}
			}
		}
	}
	private void searchFile2(File file) {   
		if (file.isDirectory()&&!file.getName().startsWith(".")){
			File fileList[] = file.listFiles();
			for (File f : fileList) {
				if(f.isDirectory()){
					searchFile3(f);
				}else {
					tests(f);
				}
			}
		}
	}
	private void searchFile3(File file) {   
		if (file.isDirectory()&&!file.getName().startsWith(".")){
			File fileList[] = file.listFiles();
			for (File f : fileList) {	
				tests(f);
			}
		}
	}


	private boolean tests(File file) {
		String fileName = file.getName();
		if(fileName.endsWith(".doc")||fileName.endsWith(".docx")){
			wordList.add(file);
		}
		else if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
			excelList.add(file);
		}
		else if(fileName.endsWith(".pdf")){
			pdfList.add(file);
		}
		return false;

	}

	/************************************************************************/
	/****** �ж��ļ����� 
	/************************************************************************/
	private int Invalid(File file) {
		int index =0;
		String fileName = file.getName();
		if(fileName.endsWith(".doc")||fileName.endsWith(".docx")){		
			index =  0; //�����Word�ļ�����1
		}
		else if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){

			index = 1; //�����Excel�ļ�����2
		}
		else if(fileName.endsWith(".pdf")){
			index =  2; //�����PDF�ļ�����3
		}

		return index;
	}

	/************************************************************************
	 ****** ʵ��ListView���ݼ����ķ��� 
	 ************************************************************************/
	private class Clicker implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
				long id) {
			File file = file_list.get(postion);
			toShowFile(file);
		}
	}

	/************************************************************************
	 ****** ѡ����ʾ��ʽ                                                                    
	 ************************************************************************/
	public void toShowFile(File myFile) {

		String fileName = myFile.getName();
		String filePath = myFile.getAbsolutePath();
		
		Intent it = new Intent();

		if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) { // .doc�ļ�
			it.setClass(ReadSearch.this, ReadWordShowActiviy.class);
		} else if (fileName.endsWith(".xls")|| fileName.endsWith(".xlsx")) {
			it.setClass(this, ReadExcelShowActivity.class);
		} else if (fileName.endsWith(".pdf")) {
			Uri uri = Uri.parse(filePath);
			it.setClass(ReadSearch.this, MuPDFActivity.class);
			it.setAction(Intent.ACTION_VIEW);
			it.setData(uri);
		} else if(fileName.endsWith(".txt")){
			it.setClass(this, ReadTxtShowActivity.class);	
		}else {
			return;
		}

		
		this.sharedOld.putString(filePath);
		
		Bundle bundle = new Bundle();
		bundle.putString("path", filePath);
		bundle.putString("name", fileName);
		bundle.putInt("signActivity",1);
		it.putExtras(bundle);


		startActivity(it);
		finish();

	}

	/************************************************************************/
	/****** ��Ӧ������̵���¼� 
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//			Intent it = new Intent(ReadSearch.this, Main.class);
			Intent it = new Intent(ReadSearch.this,ReadMainActivity.class);
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
		if(this.showSearchLayout){
			this.searchLayout.setVisibility(View.VISIBLE);
			this.showSearchLayout = false;	
		}else {
			this.searchLayout.setVisibility(View.GONE);
			this.showSearchLayout = true;
		}

		return false;
	}
	/************************************************************************/
	/***** * ������Ҫ��ʾ�������Ĳ˵��� 
	/ ************************************************************************/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("ѡ�����");
		super.getMenuInflater().inflate(R.menu.read_search_meun, menu);
	}

	/************************************************************************/
	/***** * ѡ�������Ĳ˵�ʱ�����˷��� 
	 /************************************************************************/
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		final int id = (int) menuInfo.id;
		File file = file_list.get(id);

		switch (item.getItemId()) {
		case R.id.search_open_file:
			this.toShowFile(file);
			break;
		case R.id.search_delite_file:
			this.deleteFile(file);
			this.recordItem.remove(id);
			this.adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/************************************************************************/
	/****** �����Ĳ˵�ѡ��ر�ʱ�����˲��� 
	 /************************************************************************/
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
	}

	/************************************************************************/
	/****** ����ļ�����޸�ʱ�� 
	/ ************************************************************************/
	private static String getFileLastModifiedTime(File file) {
		long time = file.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = formatter.format(time);
		return result;
	}

	/************************************************************************/
	/***** * �Ķ�����ɾ��One�ļ����µ������ļ� 
	/ ************************************************************************/
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			File allFile[] = file.listFiles();
			for (File f : allFile) {
				f.delete();
			}
		}
		file.delete();
	}


	/************************************************************************/
	/*****                  �ļ�����
	/************************************************************************/
	private void fileArray(List<File> lFiles)
	{
		List<File> list_zm = new ArrayList<File>();                //����ĸ��ͷ
		List<File> list_num = new ArrayList<File>();              //�����ֿ�ͷ
		List<File> list_qt = new ArrayList<File>();                 //��������ͷ

		for(File f: lFiles){
			char num = f.getName().charAt(0);                          //����ļ��������ַ�
			if((num>=65&&num<=90)||(num>=97&&num<=122))   //��ĸ
			{
				list_zm.add(f);
			}else if(num>=48&&num<=57){                                        //����
				list_num.add(f);
			}else {                                                                                  //����
				list_qt.add(f);
			}
		}

		for(File f : list_zm){
			this.file_list.add(f);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("file_icon", pic[Invalid(f)]);
			map.put("file_name", f.getName());
			map.put("file_time", getFileLastModifiedTime(f));
			this.recordItem.add(map);
		}
		for(File f : list_num){
			this.file_list.add(f);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("file_icon", pic[Invalid(f)]);
			map.put("file_name", f.getName());
			map.put("file_time", getFileLastModifiedTime(f));
			this.recordItem.add(map);
		}
		for(File f : list_qt){
			this.file_list.add(f);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("file_icon", pic[Invalid(f)]);
			map.put("file_name", f.getName());
			map.put("file_time", getFileLastModifiedTime(f));
			this.recordItem.add(map);
		}
	}

}
