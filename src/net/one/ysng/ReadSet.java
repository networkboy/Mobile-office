package net.one.ysng;

import net.one.ysng.R;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ReadSet extends Activity {
	private ImageButton imBtnW;
	private ImageButton imBtnE;
	private ImageButton imBtnP;
	private ImageButton homeBtn;
	private RelativeLayout titleBar;
	private SharedPreferencesSkin shareSearchDate;
	private SharedPreferencesSkin shareSkinData;
	private ReadConstant mConstant;

	private ListView list1;
	private ListView list2;

	private String array1[] = {"换  肤","分  享"};
	private String array2[] = {"关  于"};


	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);              //去掉界面上的图标
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_set);



		this.mConstant = new ReadConstant();
		this.shareSearchDate = new SharedPreferencesSkin(ReadSet.this,this.mConstant.SEARCHDATE);
		this.shareSkinData = new SharedPreferencesSkin(ReadSet.this,this.mConstant.FILENAME);

		this.imBtnW = (ImageButton)super.findViewById(R.id.read_set_list_btn_0);
		this.imBtnE = (ImageButton)super.findViewById(R.id.read_set_list_btn_1);
		this.imBtnP = (ImageButton)super.findViewById(R.id.read_set_list_btn_2);
		this.titleBar = (RelativeLayout) super.findViewById(R.id.read_set_titlebar);
		this.homeBtn = (ImageButton) super.findViewById(R.id.read_set_home_back);

		list1 = (ListView) findViewById(R.id.list1);
		list2 = (ListView) findViewById(R.id.list3);
		list1.setAdapter(new MyListAdapter(array1));
		list2.setAdapter(new MyListAdapter(array2));


		this.changeSkin();

		if(!this.shareSearchDate.contains(this.mConstant.SEARCHWORD)){		
			this.shareSearchDate.putInt(this.mConstant.SEARCHWORD, R.drawable.switch_on);
			this.shareSearchDate.putInt(this.mConstant.SEARCHEXCEL, R.drawable.switch_on);
			this.shareSearchDate.putInt(this.mConstant.SEARCHPDF, R.drawable.switch_on);		
		}
       
		//this.imBtnW.setBackground(getResources().getDrawable(R.drawable.main_bg_0));
		//4.0不支持setBackground
		this.imBtnW.setBackgroundDrawable(getResources().getDrawable(this.shareSearchDate.getInt(this.mConstant.SEARCHWORD)));
		this.imBtnE.setBackgroundDrawable(getResources().getDrawable(this.shareSearchDate.getInt(this.mConstant.SEARCHEXCEL)));
		this.imBtnP.setBackgroundDrawable(getResources().getDrawable(this.shareSearchDate.getInt(this.mConstant.SEARCHPDF)));

		this.imBtnW.setOnClickListener(new Clicker());
		this.imBtnE.setOnClickListener(new Clicker());
		this.imBtnP.setOnClickListener(new Clicker());
		this.homeBtn.setOnClickListener(new Clicker());

		this.list1.setOnItemClickListener(new MyItemClickListener1());
		this.list2.setOnItemClickListener(new MyItemClickListener3());

		ReadExitApplication.getInstance().addActivity(this);
	}

	private class Clicker implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.read_set_list_btn_0 :
				if(shareSearchDate.getInt(mConstant.SEARCHWORD)==R.drawable.switch_on){
					imBtnW.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
					shareSearchDate.putInt(mConstant.SEARCHWORD, R.drawable.switch_off);
				}else {
					imBtnW.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
					shareSearchDate.putInt(mConstant.SEARCHWORD, R.drawable.switch_on);
				}		
				break;
			case R.id.read_set_list_btn_1 :
				if(shareSearchDate.getInt(mConstant.SEARCHEXCEL)==R.drawable.switch_on){
					imBtnE.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
					shareSearchDate.putInt(mConstant.SEARCHEXCEL, R.drawable.switch_off);
				}else {
					imBtnE.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
					shareSearchDate.putInt(mConstant.SEARCHEXCEL, R.drawable.switch_on);
				}
				break;
			case R.id.read_set_list_btn_2 :
				if(shareSearchDate.getInt(mConstant.SEARCHPDF)==R.drawable.switch_on){
					imBtnP.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_off));
					shareSearchDate.putInt(mConstant.SEARCHPDF, R.drawable.switch_off);
				}else {
					imBtnP.setBackgroundDrawable(getResources().getDrawable(R.drawable.switch_on));
					shareSearchDate.putInt(mConstant.SEARCHPDF, R.drawable.switch_on);
				}
				break;
			case R.id.read_set_home_back:
				//				Intent it = new Intent(ReadSet.this,Main.class);
				Intent it = new Intent(ReadSet.this,ReadMainActivity.class);
				startActivity(it);
				finish();			
				break;
			default:
				break;
			}
		}
	}

	private class MyItemClickListener1 implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int postion,long id) {
			switch (postion) {
			case 0:
				Intent it = new Intent();
				it.setClass(getApplicationContext(), ReadSkin.class);
				startActivity(it);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				finish();
				break;
			case 1:
				shareMyApp(ReadSet.this);
				break;

			default:
				break;
			}
			
		}
	}



	private class MyItemClickListener3 implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int postion,long id) {
			Intent it = new Intent();
			it.setClass(getApplicationContext(), ReadAbout.class);
			startActivity(it);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			finish();
		}
	}



	/************************************************************************/
	/***** 换肤 /
	 ************************************************************************/
	private void changeSkin() {
		this.shareSkinData = new SharedPreferencesSkin(ReadSet.this,
				this.mConstant.FILENAME);
		if (this.shareSkinData.contains(this.mConstant.SHOWTITLEBG)) { // 如果数据已经存储过，就直接取出来用
			this.titleBar.setBackgroundColor(getResources().getColor(
					this.shareSkinData.getInt(this.mConstant.SHOWTITLEBG)));
		} else { // 如果第一次使用则设置默认值，蓝色
			this.titleBar.setBackgroundColor(getResources().getColor(
					R.color.show_title_cyan));
			this.shareSkinData.putInt(this.mConstant.SHOWTITLEBG,
					R.color.show_title_cyan);
		}
	}

	/************************************************************************/
	/*****                               分享App
	/************************************************************************/
	public void shareMyApp(Context context) {
		String shareStr = "分享一个很好用的移动办公软件 来自One团队";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, shareStr);
		context.startActivity(Intent.createChooser(intent, "分享到"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.changeSkin();
	}

	/************************************************************************/
	/****** 响应物理键盘点击事件 
	/************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//			Intent it = new Intent(ReadSet.this, ReadSearch.class);
			Intent it = new Intent(ReadSet.this,ReadMainActivity.class);
			startActivity(it);
			this.finish();
		}
		return false;

	}

	private class MyListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private String[] array;

		public MyListAdapter(String[] array) {
			inflater = LayoutInflater.from(ReadSet.this);
			this.array = array;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return array.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = inflater.inflate(R.layout.item, null);
			TextView tv = (TextView) convertView.findViewById(R.id.text);// 由于圆角listview一般都是固定的迹象，所以在这里没有做优化处理，需要的话可自行
			tv.setText(array[position]);
			if (array.length == 1) {
				setBackgroundDrawable(convertView,R.drawable.list_round_selector);
			} else if (array.length == 2) {
				if (position == 0) {
					setBackgroundDrawable(convertView,R.drawable.list_top_selector);
				} else if (position == array.length - 1) {
					setBackgroundDrawable(convertView,R.drawable.list_bottom_selector);
				}
			} else {
				if (position == 0) {
					setBackgroundDrawable(convertView,R.drawable.list_top_selector);
				} else if (position == array.length - 1) {
					setBackgroundDrawable(convertView,R.drawable.list_bottom_selector);
				} else {
					setBackgroundDrawable(convertView,R.drawable.list_rect_selector);
				}
			}
			return convertView;
		}

		private void setBackgroundDrawable(View view, int resID) {
			view.setBackgroundDrawable(getResources().getDrawable(resID));
		}
	}


}
