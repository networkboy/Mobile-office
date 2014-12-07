package net.one.ysng;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.artifex.mupdf.MuPDFActivity;



import net.ysng.reader.FileSortByName;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesOld;
import net.ysng.reader.SharedPreferencesSkin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ReadFileList extends Activity {
	
	private ReadCopyFile myCopyFile = null;

	private int pic[] = {R.drawable.file_icon_folder,R.drawable.file_icon_doc,R.drawable.file_icon_xls,
			R.drawable.file_icon_pdf,R.drawable.file_icon_ppt,R.drawable.file_icon_txt,R.drawable.file_icon_zip,
			R.drawable.file_icon_apk,R.drawable.file_icon_default};
	private ArrayList<HashMap<String, Object>> recordItem;	
	private List<File> file_list = null;
	private ListView listV = null;
	private SimpleAdapter adapter = null;
	private ImageButton homeBtn;
	private RelativeLayout titleBar;
	private SharedPreferencesOld sharedOld ;
	private SharedPreferencesSkin shareData;
	private ReadConstant myConstant ;
	private String oldSharedNum = "old";
	private File presentFile; 	
	private boolean copyFileSign = false;
	private int scrollPos;
	private int scrollTop;



	public void onCreate(Bundle savedInstanceState) {	
		requestWindowFeature(Window.FEATURE_NO_TITLE);               //ȥ�������ϵ�ͼ��
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_file_list);
		this.listV = (ListView)findViewById(R.id.reader_list);
		this.titleBar = (RelativeLayout)super.findViewById(R.id.reader_titlebar);
		this.homeBtn = (ImageButton)super.findViewById(R.id.reader_home_back);
		super.registerForContextMenu(this.listV);                                  //ע�������Ĳ˵�
		this.file_list = new ArrayList<File>();
        this.myConstant = new ReadConstant();
		sharedOld = new SharedPreferencesOld(ReadFileList.this, myConstant.OLDNAME);
		shareData = new SharedPreferencesSkin(ReadFileList.this, myConstant.SKINVIEW);
		this.list_files();

		this.homeBtn.setOnClickListener(new OnClickner());
		ReadExitApplication.getInstance().addActivity(this);

	}

	/************************************************************************/
	/*****                                  ����
	/************************************************************************/
	private void changeSkin(){	
		this.shareData = new SharedPreferencesSkin(ReadFileList.this, myConstant.FILENAME);
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//��������Ѿ��洢������ֱ��ȡ������
			this.titleBar.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //�����һ��ʹ��������Ĭ��ֵ����ɫ
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/*****          ��дonResume()����
	/************************************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
	}

	/************************************************************************/
	/*****          ʵ��OnClickListener�ӿڣ�ʵ��Home��ť����
	/************************************************************************/
	class OnClickner implements OnClickListener{
		@Override
		public void onClick(View v) {	
			Intent it = new Intent(ReadFileList.this,ReadMainActivity.class);
			startActivity(it);
			finish();	
		}	
	}

	/************************************************************************/
	/*****          ��ȡSD����·����ȡ��·���������ļ� ����fill()����
	/************************************************************************/
	private void list_files(){
		File pathFile = android.os.Environment.getExternalStorageDirectory();  //��ȡ��Ŀ¼
		this.presentFile = pathFile;
		File[] file = pathFile.listFiles();                                                                 //��ȡ��·���������ļ�
		this.fill(file);
	}

	/************************************************************************/
	/*****          ΪListView������� ʵ��ListView��ʾ
	/************************************************************************/
	public void fill(File[] file){

		this.recordItem = new ArrayList<HashMap<String, Object>>();
		List<File> list = new ArrayList<File>();
		List<File> list_folder = new ArrayList<File>();
		List<File> list_file = new ArrayList<File>();
		this.file_list.clear();
		for(File f: file){
			if(!f.getName().startsWith(".")){                        //ȥ����"."��ͷ���ļ�
				list.add(f);
			}else {
				continue;
			}
		}
		FileSortByName fileSortByName = new FileSortByName();  //ʵ����
		List<File> newList = fileSortByName.getFile(list);                //����������

		for(File f: newList){                                                              //���ļ��к��ļ��ֿ�
			if(f.isDirectory()){     
				list_folder.add(f);
			}else {
				list_file.add(f);
			}
		}

		this.fileArray(list_folder);
		this.fileArray(list_file);

		this.adapter = new SimpleAdapter(this, 
				this.recordItem, 
				R.layout.read_file_list_layout, 
				new String[]{"file_icon","file_name","file_time"}, 
				new int[]{R.id.file_icon, R.id.file_name,R.id.file_time});

		this.listV.setOnScrollListener(scrollListener);  
		this.listV.setAdapter(adapter);
		this.listV.setSelectionFromTop(scrollPos, scrollTop); 

		this.listV.setOnItemClickListener(new Clicker());     //����
	}

	/************************************************************************/
	/*****         ��¼LIstView��λ��
	/************************************************************************/
	private OnScrollListener scrollListener = new OnScrollListener() {        
		@Override  
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {  
		}  

		@Override  
		public void onScrollStateChanged(AbsListView view, int scrollState) {  

			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
				// scrollPos��¼��ǰ�ɼ���List���˵�һ�е�λ��   
				scrollPos = listV.getFirstVisiblePosition();   
			}  
			if (recordItem != null) {   

				View v=listV .getChildAt(0);                   
				scrollTop=(v==null)?0:v.getTop();  
			}    
		}  
	};  

	/************************************************************************/
	/*****          �жϸ��ļ����ļ��л�������
	/************************************************************************/
	private int Invalid(File f){
		if(f.isDirectory()){     
			return 0;            //������ļ��з���0
		}
		else{
			String filename = f.getName().toLowerCase();//���ַ���ȫת��ΪСд
			if(filename.endsWith(".doc")||filename.endsWith(".docx")){
				return 1;       //�����Word�ļ�����1
			}
			else if(filename.endsWith(".xls")||filename.endsWith(".xlsx")){
				return 2;        //�����Excel�ļ�����2
			}
			else if(filename.endsWith(".pdf")){
				return 3;        //�����PDF�ļ�����3
			}
			else if(filename.endsWith(".ppt")){
				return 4;        //�����PPT�ļ�����4
			}
			else if(filename.endsWith(".txt")){
				return 5;        //�����TXT�ļ�����4
			}
			else if(filename.endsWith(".zip")){
				return 6;        //�����ZIP�ļ�����4
			}
			else if(filename.endsWith(".apk")){
				return 7;        //�����APK�ļ�����4
			}
			else {
				return 8;
			}
		}
	}

	/************************************************************************/
	/*****          ʵ��ListView���ݼ����ķ���
	/************************************************************************/
	private class Clicker implements OnItemClickListener{

		public void onItemClick(AdapterView<?> arg0, View arg1, int postion,long id) {
			File file = file_list.get(postion);
			openFile(file);
		}
	}

	/************************************************************************/
	/*****          ���ļ����ļ���
	/************************************************************************/
	private void openFile(File file){
		this.presentFile = file;
		if(file.isDirectory()){                                   //������ļ��� 
			File[] files = file.listFiles();                      //����listFiles()������ø��ļ����������ļ�
			ReadFileList.this.fill(files);                             //����file()����			
		}
		else{                                                       //����Ƿ��ļ���	 
			this.toShowFile(file);		
		}   
	}

	/************************************************************************/
	/*****            ѡ����ʾ��ʽ
	/************************************************************************/
	public void  toShowFile(File myFile){
		String fileName = myFile.getName();
		String filePath = myFile.getAbsolutePath();
		
		Intent it = new Intent();	

		if(fileName.endsWith(".doc")||fileName.endsWith(".docx")){                         //word�ļ�	
			it.setClass(ReadFileList.this, ReadWordShowActiviy.class);	
		}else if(fileName.endsWith(".xls")){
			it.setClass(ReadFileList.this, ReadExcelShowActivity.class);		
		}else if(fileName.endsWith(".xlsx")){
			it.setClass(ReadFileList.this, ReadExcelShowActivity.class);				
		}else if (fileName.endsWith(".pdf")) {		
			Uri uri = Uri.parse(myFile.getAbsolutePath());
			it.setClass(ReadFileList.this, MuPDFActivity.class);
			it.setAction(Intent.ACTION_VIEW);
			it.setData(uri);		
		}else if(fileName.endsWith(".txt")){
			it.setClass(this, ReadTxtShowActivity.class);	
		}else if(fileName.endsWith(".apk")){
			getApkFileIntent(filePath);
		}
		else{
			return ;
		}
		
		this.sharedOld.putString(filePath);
		Bundle bundle = new Bundle();				   
		bundle.putString("path", filePath);
		bundle.putString("name", fileName);
		bundle.putInt("signActivity", 0);
		it.putExtras(bundle);
		startActivity(it);	
		finish();

	}

	/************************************************************************/
	/*****              ���������ǰ����
	/************************************************************************/
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	/************************************************************************/
	/*****          ��Ӧ������̵���¼�
	/************************************************************************/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(this.presentFile.isDirectory()){
				if(this.presentFile.equals(android.os.Environment.getExternalStorageDirectory())){		
					Intent it = new Intent(this,ReadMainActivity.class);
					startActivity(it);
					this.finish();	
				}
				else{
					this.presentFile = this.presentFile.getParentFile();
					File file = this.presentFile;
					File[] files = file.listFiles();
					this.fill(files);
				}
			}

			if(this.presentFile.isFile()){
				if(this.presentFile.getParentFile().isDirectory()){
					this.presentFile = this.presentFile.getParentFile();
					File file = this.presentFile;
					File[] files = file.listFiles();
					fill(files);
				}
			}
		}
		return false;	
	}
	
	//Android��ȡһ�����ڴ�APK�ļ���intent  
    private  Intent getApkFileIntent(String param) {  
  
        Intent intent = new Intent();    
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        intent.setAction(android.content.Intent.ACTION_VIEW);    
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri,"application/vnd.android.package-archive");   
        return intent;  
    }  

	/************************************************************************/
	/*****                  ������Ҫ��ʾ�������Ĳ˵���
	/************************************************************************/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("ѡ�����");
		super.getMenuInflater().inflate(R.menu.file_list_menu, menu);	  
	}

	/************************************************************************/
	/*****                   ѡ�������Ĳ˵�ʱ�����˷���
	/************************************************************************/
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
		final int id=(int) menuInfo.id;
		File file = this.file_list.get(id);

		switch (item.getItemId()) {
		case R.id.file_list_open_file :
			this.openFile(file);
			break;
		case R.id.file_list_copy_file :
			this.copyFile(file);
			break;
		case R.id.file_list_paste_file :
			this.pasteFile(file);
			break;
		case R.id.file_list_delite_file:
			toDeleteFile(file);
			this.file_list.remove(id);
			this.recordItem.remove(id);
			this.adapter.notifyDataSetChanged();
			break;
		default: 
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	/************************************************************************/
	/*****                   �����ļ�
	/************************************************************************/
	private void copyFile(File file){
		this.myCopyFile = new ReadCopyFile();
		myCopyFile.setFromFile(file);	
		this.copyFileSign = true;
	}
	
	/************************************************************************/
	/*****                   ճ���ļ�
	/************************************************************************/
	private void pasteFile(File file){
		if(this.copyFileSign){
			boolean isTrue = this.myCopyFile.pasteFile(file);
			if(isTrue){		
				this.file_list.add(file);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("file_icon", pic[Invalid(file)]);
				map.put("file_name", "[copy] " +file.getName());
				map.put("file_time", getFileLastModifiedTime(file));
				this.recordItem.add(map);
				this.adapter.notifyDataSetChanged();
				
				Toast.makeText(getApplication(), "�������", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplication(), "���ļ����ܸ���", Toast.LENGTH_SHORT).show();
			}
			this.copyFileSign = false;
		}else {
			Toast.makeText(getApplication(), "����ѡ���Ƶ��ļ�", Toast.LENGTH_SHORT).show();
		}
	}

	/************************************************************************/
	/*****                  �����Ĳ˵�ѡ��ر�ʱ�����˲���
	/************************************************************************/
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
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

	/************************************************************************/
	/*****                  ����ļ�����޸�ʱ��
	/************************************************************************/
	private static String getFileLastModifiedTime(File file) {
		long time=file.lastModified();
		SimpleDateFormat formatter = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String result=formatter.format(time);
		return result;
	}

	private void toDeleteFile(final File file){
		new Thread(new Runnable() {	
			@Override
			public void run() {
				deleteFile(file);						
			}
		}
        ).start();
	}
	
	/************************************************************************/
	/*****             ɾ���ļ�
   /************************************************************************/
	private boolean deleteFile(final File file){		
		if (file.isDirectory()) {
			File fileList[] = file.listFiles();
			for (File f: fileList) {
				boolean success = deleteFile(f);
				if (!success) {
					return false;
				}
			}
		}	
		return file.delete();	
	}

}

