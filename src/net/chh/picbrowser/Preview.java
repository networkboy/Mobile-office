package net.chh.picbrowser;


import java.io.File;
import java.util.ArrayList;

import net.one.ysng.R;
import net.one.ysng.ReadExitApplication;
import net.one.ysng.ReadImagetoPDF;
import net.one.ysng.ReadMainActivity;
import net.ysng.reader.ReadConstant;
import net.ysng.reader.SharedPreferencesSkin;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class Preview extends Activity {

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,ReadMainActivity.class);
		startActivity(i);
		finish();
		super.onBackPressed();
	}



	final static String[] proj = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
		MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
		MediaStore.Images.Media.MINI_THUMB_MAGIC };
	final static String selection = ""
			+ MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?";
	final static int SINGLE = 0;
	final static int ALL = 1;
	private GridView gridView;
	private ArrayList<CatalogItem> mList_AllItem = new ArrayList<CatalogItem>();
	protected MyAdapter myAdapter = new MyAdapter();
	private boolean beAbleToLoadPic = true;
	private String[] filePath = new String[500];
	private String pdfPath = null;
	private ArrayList<String> path = new ArrayList<String>();
	private ArrayList<String> externalPath = new ArrayList<String>();
	private int picNum = 0;
	private boolean isChice[];
	private Button cancel;
	private Button edit;
	
	private static int count = 0;
	private static boolean isEditing = false;
	private Cursor cursor = null;
	private CatalogItem catalogItem = null;
	private ReadConstant myConstant;
	private RelativeLayout  titleLayout = null;	
	private SharedPreferencesSkin shareSkinData;

	// private Matrix matrix;
	// private ExifInterface exifInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview);
		this.edit = (Button) findViewById(R.id.preview_edit_button);
		this.cancel = (Button) findViewById(R.id.preview_cancel_button);
		this.titleLayout = (RelativeLayout)super.findViewById(R.id.preview_image_edit_bar);
		ReadExitApplication.getInstance().addActivity(this);
		
		this.edit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (count % 2 == 0) {
					edit.setText("完成");
					cancel.setText("删除");
					isEditing = true;

				} else {
					edit.setText("编辑");
					cancel.setText("转换");
					isEditing = false;
					for (int i = 0; i < picNum; i++) {
						isChice[i] = false;
					}
					myAdapter.notifyDataSetChanged();
				}
				count++;
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isEditing == true) {
					deleteImage(SINGLE);
				}else{
					//在此添加转换函数
					//完成后删除所有图片
					createDialog("转换","请输入文件名");

				}
			}
		});
		

		findViews();
		fetchData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		fetchData();
		myAdapter.notifyDataSetChanged();
		this.changeSkin();
		super.onResume();
	}

	private void fetchData() {
		Log.i("Preview", "here we go");
		picNum = 0;
		String fileName = "OneScreenshot";
		cursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, selection,
				new String[] { fileName }, null);
		mList_AllItem.clear();
		path.clear();

		while (!cursor.isAfterLast()) {
			if (cursor.isBeforeFirst()) {
				cursor.moveToNext();
			} else {
				catalogItem = new CatalogItem();

				int column_bucket = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
				String bucketName = cursor.getString(column_bucket);
				int column_id = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
				int column_data = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				int column_magic = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
				String id = cursor.getString(column_id);
				String data = cursor.getString(column_data);
				long magic = cursor.getLong(column_magic);
				Uri mUri = Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
				String eUri = String.valueOf(mUri);
				externalPath.add(eUri);
				long parseId = ContentUris.parseId(mUri);

				catalogItem.setParseId(parseId);
				catalogItem.setTextview(bucketName);
				path.add(data);
				filePath[picNum] = data;
				picNum++;
				catalogItem.setId(id);
				catalogItem.setImagepath(data);
				catalogItem.setMagic(magic);

				mList_AllItem.add(catalogItem);
				cursor.moveToNext();
			}
		}
		cursor.close();

		isChice = new boolean[picNum];
		for (int i = 0; i < picNum; i++) {
			isChice[i] = false;
		}
	}

	private void findViews() {
		// TODO Auto-generated method stub
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(myAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (isEditing == true) {
					myAdapter.chiceState(arg2);

				} else {
					Intent intent = new Intent(Preview.this, Launch.class);
					intent.putExtra("No", arg2);
					// intent.putExtra("picNum", picNum);
					// //// intent.putExtra("filePath", filePath);
					intent.putExtra("path", path);
					intent.putExtra("externalPath", externalPath);
					startActivity(intent);
				}
			}
		});
	}

	private void deleteImage(int type) {
		File file = null;
		ContentResolver resolver = getContentResolver();

		switch (type) {
		case SINGLE:
			for (int i = 0; i < picNum; i++) {
				if (isChice[i] == true) {
					try {
						Uri data = Uri
								.parse("content://media/external/images/media/"
										+ mList_AllItem.get(i).getId());
						Log.i("deleteuri", "" + data);
						resolver.delete(data, null, null);

						file = new File(path.get(i));
						file.delete();
						Log.i("filepath", path.get(i));
					} catch (Exception e) {
						Log.i("error", e.getMessage());
					}
				}
			}
			picNum--;
			break;
		case ALL:
			for (int i = 0; i < picNum; i++) {
				try {
					Uri data = Uri
							.parse("content://media/external/images/media/"
									+ mList_AllItem.get(i).getId());
					Log.i("deleteuri", "" + data);
					resolver.delete(data, null, null);

					file = new File(path.get(i));
					file.delete();
					Log.i("filepath", path.get(i));
				} catch (Exception e) {
					Log.i("error", e.getMessage());
				}

			}
		default:
			break;
		}

		fetchData();
		myAdapter.notifyDataSetChanged();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// Log.i("xxx", "list size is " + mList_AllItem.size());
			return mList_AllItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mList_AllItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = new gridViewItemAlbum(Preview.this);
			}
			if (beAbleToLoadPic) {
				Bitmap mBitmap = BitmapTransportHelper.getInstance()
						.drawBitMap(mList_AllItem.get(position).getMagic(),
								mList_AllItem.get(position).getParseId(),
								Preview.this);
				// int width = mBitmap.getWidth();
				// int height = mBitmap.getHeight();
				// exifInterface = new ExifInterface();
				// matrix = new Matrix();
				// matrix.postRotate(90);
				// mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height,
				// matrix, true);
				((gridViewItemAlbum) convertView).setPic(getView(position,
						mBitmap));
			}
			return convertView;
		}

		private LayerDrawable getView(int post, Bitmap bm) {
			Bitmap bitmap_edit = null;
			LayerDrawable la = null;
			if (isChice[post] == true) {
				bitmap_edit = BitmapFactory.decodeResource(getResources(),
						R.drawable.selected);
			}
			if (bitmap_edit != null) {
				Drawable[] array = new Drawable[2];
				array[0] = new BitmapDrawable(bm);
				array[1] = new BitmapDrawable(bitmap_edit);
				la = new LayerDrawable(array);
				la.setLayerInset(0, 0, 0, 0, 0); // 第几张图离各边的间距
				la.setLayerInset(1, 50, 50, 5, 5);

			} else {
				Drawable[] array = new Drawable[1];
				array[0] = new BitmapDrawable(bm);
				la = new LayerDrawable(array);
				la.setLayerInset(0, 0, 0, 0, 0);
			}
			return la; // 返回叠加后的图
		}

		public void chiceState(int post) {
			isChice[post] = isChice[post] == true ? false : true;
			this.notifyDataSetChanged();
		}
	}

	/************************************************************************/
	/***** * 创建Dialog 
	/ ************************************************************************/
	public void createDialog(String title, String meg) {
		Dialog dialog = null;
		final EditText finame = new EditText(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		finame.setLayoutParams(layoutParams);

		dialog = new AlertDialog.Builder(this)		
		.setTitle(title)
		.setMessage(meg)
		.setView(finame)
		.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String name = finame.getText().toString();
				if("".equals(name)){
					name = "未命名";
				}
				imageToPdf(name);	
				deleteImage(ALL);								
			}
		})
		.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();		


		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		dialog.show();
	}
	private void imageToPdf(String name) {
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (new ReadImagetoPDF(dm, name).ImagetoPDF()) {
			Toast.makeText(this, "文件存储地址为" + this.myConstant.PDFFOLDER,Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "转换失败......", Toast.LENGTH_SHORT).show();
		}
	}

	/************************************************************************/
	/***** * 换肤 
	/************************************************************************/
	private void changeSkin() {
		this.shareSkinData = new SharedPreferencesSkin(this, myConstant.FILENAME);
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
	
	
	
	@Override
	protected void onDestroy() {
		this.deleteImage(ALL);
		super.onDestroy();
	}
	

}
