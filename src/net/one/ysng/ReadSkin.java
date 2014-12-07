package net.one.ysng;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.xml.simpleparser.NewLineHandler;

import net.ysng.reader.*;


import android.R.integer;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

public class ReadSkin extends Activity {

	private SharedPreferencesSkin shareData;
	private RelativeLayout titleBar;
	private ImageButton homeBtn;
	private ImageButton skin_red ;
	private ImageButton skin_cyan ;
	private ImageButton skin_black;
	private ImageView  red_imView;
	private ImageView  cyan_imView;
	private ImageView  black_imView;
	private Gallery gallery = null;
	private ImageView imageView =null;
	private List<Map<String, Integer>> list = new ArrayList<Map<String,Integer>>();
	private SimpleAdapter simpleAdapter = null;
	private int picIndex[] = new int[]{R.drawable.background_0_gray,R.drawable.background_1,R.drawable.background_2,
			R.drawable.background_3};
	private int skinSign = 0;

	private ReadConstant myConstant = new ReadConstant();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);                      //去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_skin);

		this.titleBar = (RelativeLayout) super.findViewById(R.id.readskin_titlebar);
		this.homeBtn = (ImageButton)super.findViewById(R.id.skin_home_back);
		this.skin_red= (ImageButton) super.findViewById(R.id.skin_red_imbtn);
		this.skin_cyan = (ImageButton) super.findViewById(R.id.skin_cyan_imbtn);
		this.skin_black = (ImageButton) super.findViewById(R.id.skin_black_imbtn);
		this.gallery = (Gallery) super.findViewById(R.id.myGallery);
		this.initAdapter();
		this.gallery.setAdapter(simpleAdapter);

		this.gallery.setOnItemClickListener(new OnItemClickListenerImpl()) ;

		this.red_imView = (ImageView)super.findViewById(R.id.skin_red_imview);
		this.cyan_imView = (ImageView)super.findViewById(R.id.skin_cyan_imview);
		this.black_imView = (ImageView)super.findViewById(R.id.skin_black_imview);

		this.shareData = new SharedPreferencesSkin(ReadSkin.this, myConstant.FILENAME);
		this.changeSkin();

		this.skin_red.setOnClickListener(new Clicker());
		this.skin_cyan.setOnClickListener(new Clicker());
		this.skin_black.setOnClickListener(new Clicker());
		ReadExitApplication.getInstance().addActivity(this);

	}

	private void initAdapter(){
		Field [] fields = R.drawable.class.getDeclaredFields() ;
		for(int x=0;x<fields.length;x++){
			if(fields[x].getName().startsWith("background_")){
				Map<String,Integer> map = new HashMap<String, Integer>();
				try {
					map.put("imgs", fields[x].getInt(R.drawable.class));
				} catch (Exception e){
					Toast.makeText(this, "initAdapter方法中出现错误", Toast.LENGTH_LONG);
				} 
				this.list.add(map);
			}	
		}
		this.simpleAdapter = new SimpleAdapter(this,this.list,R.layout.gallery_layout,new String[]{"imgs"},new int[]{R.id.imgs});
	}

	private class ViewFactoryImpl implements ViewFactory{
		@Override
		public View makeView() {
			ImageView img = new ImageView(ReadSkin.this) ; 
			//img.setBackgroundColor(0xFFFFFFFF) ;
			img.setScaleType(ImageView.ScaleType.CENTER) ;
			img.setLayoutParams(new ImageSwitcher.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			return img;
		}	
	}

	private class OnItemClickListenerImpl implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			//Toast.makeText(getApplicationContext(), position + "N"+ id, Toast.LENGTH_SHORT).show();  
			shareData.putInt(myConstant.MAINBG, picIndex[position]);	
		}	
	}

	/************************************************************************/
	/*****                                  换肤
	/************************************************************************/
	private void changeSkin(){
		if(this.shareData.contains(myConstant.SHOWTITLEBG)){	//如果数据已经存储过，就直接取出来用
			this.titleBar.setBackgroundColor(getResources().getColor(shareData.getInt(myConstant.SHOWTITLEBG)));	
		}else {    //如果第一次使用则设置默认值，蓝色
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));
			this.shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan);
		}

		if(this.shareData.contains(myConstant.SKINVIEW)){	
			switch (this.shareData.getInt(myConstant.SKINVIEW)) {
			case 0 :
				cyan_imView.setVisibility(View.VISIBLE);
				this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));	
				break;
			case 1 :
				black_imView.setVisibility(View.VISIBLE);
				this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_black));	
				break;
			case 2 :
				red_imView.setVisibility(View.VISIBLE);
				this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_red));	
				break;
			default:
				break;
			}	
		}else {  //默认值
			cyan_imView.setVisibility(View.VISIBLE); 
			this.skinSign =0;
			this.shareData.putInt(myConstant.SKINVIEW, skinSign);
		}	
	}

	class Clicker  implements OnClickListener{	
		@Override
		public void onClick(View v) { 
			hideSkinIcon();
			shareData.clear();    
			switch (v.getId()) {
			case R.id.skin_cyan_imbtn :	
				cyan_imView.setVisibility(View.VISIBLE); 
				skinSign = 0;
				break;
			case R.id.skin_black_imbtn :	
				black_imView.setVisibility(View.VISIBLE); 
				skinSign = 1;
				break;
			case R.id.skin_red_imbtn :
				red_imView.setVisibility(View.VISIBLE); 
				skinSign = 2;
				break;
			case R.id.skin_home_back :	
				Intent it = new Intent(ReadSkin.this,ReadMainActivity.class);
				startActivity(it);
				finish();	
				break;
			default:
				break;
			}	
			shareData.putInt(myConstant.SKINVIEW, skinSign);
			changeSkin(skinSign);
		}
	}

	private void hideSkinIcon(){
		switch (this.shareData.getInt(myConstant.SKINVIEW)){
		case 0 :
			cyan_imView.setVisibility(View.GONE);		
			break;
		case 1 :
			black_imView.setVisibility(View.GONE);
			break;
		case 2 :
			red_imView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	private void changeSkin(int skinSign){
		shareData.putInt(myConstant.SKINVIEW, skinSign);
		switch (skinSign) {
		case 0 :	
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_cyan));	
			shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_cyan); //各界面标题栏背景颜色
			break;
		case 1 :
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_black));	
			shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_black);
			break;
		case 2 :
			this.titleBar.setBackgroundColor(getResources().getColor(R.color.show_title_red));	
			shareData.putInt(myConstant.SHOWTITLEBG, R.color.show_title_red);
			break;
		default:
			break;
		}	
	}
	/************************************************************************/
	/*****          响应物理键盘点击事件
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it = new Intent(this,ReadSet.class);
			startActivity(it);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);//从右往左滑动
			this.finish();
		}
		return false;	

	}
}
