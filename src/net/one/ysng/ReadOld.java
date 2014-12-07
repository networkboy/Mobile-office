package net.one.ysng;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.artifex.mupdf.MuPDFActivity;


import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesOld;
import net.ysng.reader.SharedPreferencesSkin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class ReadOld extends Activity {
	private ListView listV = null;
	private List<File> file_list = null;
	private int pic[] = {R.drawable.file_icon_doc,R.drawable.file_icon_xls,R.drawable.file_icon_pdf,R.drawable.file_icon_txt};
	private ArrayList<HashMap<String, Object>> recordItem;
	private SimpleAdapter adapter = null;
	private HashMap<String, ?> oldMap;
	private SharedPreferencesOld sharedOld ;
	private SharedPreferencesSkin shareData;
	private ReadConstant myConstant ;
	private ImageButton homeBtn;
	private RelativeLayout titleBar;



	public void onCreate(Bundle savedInstanceState) {	
		requestWindowFeature(Window.FEATURE_NO_TITLE);               //去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_old);

		this.listV = (ListView)findViewById(R.id.read_old_list);
		this.titleBar = (RelativeLayout)super.findViewById(R.id.read_old_titlebar);
		this.homeBtn = (ImageButton)super.findViewById(R.id.read_old_home_back);		
		this.file_list = new ArrayList<File>();
		this.recordItem = new ArrayList<HashMap<String, Object>>();
		this.sharedOld = new SharedPreferencesOld(ReadOld.this, myConstant.OLDNAME);
		this.shareData = new SharedPreferencesSkin(ReadOld.this, myConstant.SKINVIEW);
		this.oldMap = (HashMap<String, ?>)sharedOld.getAll();

		this.listV.setOnItemClickListener(new Clicker());     //监听
		this.homeBtn.setOnClickListener(new BtnClickner());
		super.registerForContextMenu(this.listV);                                  //注册上下文菜单
		this.changeSkin();
		this.fill();
		ReadExitApplication.getInstance().addActivity(this);
	}

	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){	
		this.shareData = new SharedPreferencesSkin(ReadOld.this, myConstant.FILENAME);
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleBar.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/*****          为ListView添加内容 实现ListView显示
	/************************************************************************/
	public void fill(){

		Collection<?> values =this.oldMap.values();
		Iterator<?> iter = values.iterator();

		while(iter.hasNext()){
			Object filePath = iter.next();
			File file = new File((String)filePath);
			this.file_list.add(file);
			String fileName = file.getName();
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("file_icon", this.pic[Invalid(fileName)]);
			map.put("file_name", fileName);
			map.put("file_time", getFileLastModifiedTime(file));

			this.recordItem.add(map);		

		}	

		this.adapter = new SimpleAdapter(this,
				this.recordItem, 
				R.layout.read_file_list_layout, 
				new String[]{"file_icon","file_name","file_time"}, 
				new int[]{R.id.file_icon, R.id.file_name,R.id.file_time});

		this.listV.setAdapter(adapter);
	}


	/************************************************************************/
	/*****          判断文件类型
	/************************************************************************/
	private int Invalid(String filename){
		if(filename.endsWith(".doc")||filename.endsWith(".docx")){
			return 0;       //如果是Word文件返回1
		}
		else if(filename.endsWith(".xls")||filename.endsWith(".xlsx")){
			return 1;        //如果是Excel文件返回2
		}
		else if(filename.endsWith(".pdf")){
			return 2;        //如果是PDF文件返回3
		}else if(filename.endsWith(".txt")){
			return 3;
		}
		else {
			return 0;
		}
	}

	/************************************************************************/
	/*****          实现ListView内容监听接口
	/************************************************************************/
	private class Clicker implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View arg1, int postion,long id) {
			File file = file_list.get(postion);
			toShowFile(file);		
		}
	}

	/************************************************************************/
	/*****          实现ListView内容监听接口
	/************************************************************************/
	private class BtnClickner implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {	
			Intent it = new Intent(ReadOld.this,ReadMainActivity.class);
			startActivity(it);
			finish();	
		}	
	}

	/************************************************************************
	/****** 选择显示方式                                                                    
    /************************************************************************/
	public void toShowFile(File myFile) {

		String fileName = myFile.getName();
		String filePath = myFile.getAbsolutePath();
		this.sharedOld.putString(filePath);
		Intent it = new Intent();

		if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) { // .doc文件
			it.setClass(ReadOld.this, ReadWordShowActiviy.class);
		} else if (fileName.endsWith(".xls")|| fileName.endsWith(".xlsx")) {
			it.setClass(ReadOld.this, ReadExcelShowActivity.class);
		} else if (fileName.endsWith(".pdf")) {
			Uri uri = Uri.parse(filePath);
			it.setClass(ReadOld.this, MuPDFActivity.class);
			it.setAction(Intent.ACTION_VIEW);
			it.setData(uri);
		} else if(fileName.endsWith(".txt")){
			it.setClass(this, ReadTxtShowActivity.class);	
		}else {
			return;
		}

		Bundle bundle = new Bundle();
		bundle.putString("path", filePath);
		bundle.putString("name", fileName);
		bundle.putInt("signActivity",2);
		it.putExtras(bundle);


		startActivity(it);
		finish();

	}

	/************************************************************************/
	/*****          响应物理键盘点击事件
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent it = new Intent(ReadOld.this,Main.class);
			Intent it = new Intent(ReadOld.this,ReadMainActivity.class);
			startActivity(it);
			this.finish();
		}
		return false;	

	}

	/************************************************************************/
	/*****                  设置所要显示的上下文菜单项
	/************************************************************************/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("选择操作");
		super.getMenuInflater().inflate(R.menu.read_old_contextmenu, menu);	  
	}

	/************************************************************************/
	/*****                   选中上下文菜单时触发此方法
	/************************************************************************/
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
		final int id=(int) menuInfo.id;
		File file = file_list.get(id);

		switch (item.getItemId()) {
		case R.id.item_open_file :
			this.toShowFile(file);
			break;
		case R.id.item_delite_notes:
			this.recordItem.remove(id);
			this.adapter.notifyDataSetChanged();
			break;
		case R.id.item_delite_file:
			this.deleteFile(file);
			this.recordItem.remove(id);
			this.adapter.notifyDataSetChanged();
			break;
		case R.id.item_delite_all_notes:
			Toast.makeText(this, "选择的是清空记录" , Toast.LENGTH_SHORT).show();
			this.recordItem.clear();
			this.sharedOld.clear();
			this.adapter.notifyDataSetChanged();
			break;
		default: 
			break;
		}
		return super.onContextItemSelected(item);
	}

	/************************************************************************/
	/*****                  获得文件最后修改时间
	/************************************************************************/
	private static String getFileLastModifiedTime(File file) {
		long time=file.lastModified();
		SimpleDateFormat formatter = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String result=formatter.format(time);
		return result;
	}
	
	/************************************************************************/
	/*****              阅读结束删除One文件夹下的所有文件
	/************************************************************************/
	private void deleteFile(File file){
		if(file.isDirectory()){
			File allFile[] = file.listFiles();
			for(File f: allFile){
				f.delete();
			}
		}
		file.delete();
	}

}

