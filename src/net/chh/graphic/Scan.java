package net.chh.graphic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



import net.chh.picbrowser.Preview;
import net.one.ysng.ReadExitApplication;
import net.one.ysng.R;
import net.one.ysng.ReadMainActivity;
import net.one.ysng.ReadWordShowActiviy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Scan extends Activity {
	public static final int LARGEST_WIDTH = 480;
	public static final int LARGEST_HEIGHT = 800;
	SurfaceView sView;
	SurfaceHolder surfaceHolder;
	static int screenWidth;
	static int screenHeight;
	Camera camera;
	Camera.Parameters parameters;
	boolean isPreview = false;
	int countBack = 0;
	int count = 1;
	Matrix mt = new Matrix();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.read_scan);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();

		mt.setRotate(90);

		sView = (SurfaceView) findViewById(R.id.sView);

		surfaceHolder = sView.getHolder();

		ReadExitApplication.getInstance().addActivity(this);
		surfaceHolder.addCallback(new Callback() {

			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Log.i("xxx", "surfaceDestroyed---");
				if (camera != null) {
					if (isPreview) {
						camera.stopPreview();
						isPreview = false;
					}
					camera.release();
					camera = null;
				}
			}

			public void surfaceCreated(SurfaceHolder holder) {

				if (!isPreview) {
					Scan.this.camera = Camera.open();
				}
				if (Scan.this.camera != null && !isPreview) {
					try {
						Scan.this.parameters = Scan.this.camera.getParameters();
						Scan.this.parameters
						.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						Scan.this.parameters
						.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
						Scan.this.parameters.setPreviewFrameRate(12);

						Scan.this.parameters.setPictureFormat(PixelFormat.JPEG);
						Scan.this.parameters.set("jpeg-quality", 100);
						findBestSize();

						Scan.this.camera.setParameters(Scan.this.parameters);
						Scan.this.camera.setDisplayOrientation(90);
						Scan.this.camera.setPreviewDisplay(surfaceHolder);
						Scan.this.camera.startPreview();
						Scan.this.camera.autoFocus(null);

					} catch (IOException e) {
						Log.i("xxxxxx", e.getMessage());
					}
					isPreview = true;
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.i("xxx", "surfaceChanged---");
			}
		});

		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		sView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (Scan.this.camera != null) {
				// camera.setParameters(parameters);
				Log.i("xxx", "here");
				Scan.this.camera.takePicture(null, null, jpegCallback);
				// }

			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(count==1){		
				Intent intent = new Intent(Scan.this, ReadMainActivity.class);
				startActivity(intent);	
				finish();
			}else {
				this.createDialog("提示", "是否转换成PDF？");//1:提示是否保存对话框		
			}
		}
			return false;
		}

		/************************************************************************/
		/***** * 创建Dialog 
	/ ************************************************************************/
		public void createDialog(String title, String meg) {

			Dialog dialog = new AlertDialog.Builder(this)
			.setTitle(title)
			.setMessage(meg)
			.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Scan.this, Preview.class);
					startActivity(intent);	
					finish();
					overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}
			})
			.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(Scan.this, ReadMainActivity.class);
					startActivity(intent);	
					finish();
				}
			}).create();

			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
			window.setWindowAnimations(R.style.mystyle); // 添加动画
			dialog.show();
		}

		public void onConfigurationChanged(Configuration newConfig) {

			// Log.i("xxx", "configuration ---");
		};

		PictureCallback jpegCallback = new PictureCallback() {

			public void onPictureTaken(byte[] data, Camera camera) {

				String fname = "/sdcard/One/OneScreenshot/_" + count + ".jpeg";
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
						mt, true);
				if (bm != null) {

					try {
						File sdfile = new File("/sdcard/One/OneScreenshot");
						if (!sdfile.exists()) {
							sdfile.mkdir();
						}
						FileOutputStream out = new FileOutputStream(fname);
						bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

						Uri mUri = Uri.parse("file://"
								+ Environment.getExternalStorageDirectory()
								+ "/One/OneScreenshot/_" + count + ".jpeg");
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
								mUri));


						Toast.makeText(Scan.this, "扫描成功", Toast.LENGTH_SHORT).show();
						count++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(Scan.this, "扫描失败", Toast.LENGTH_SHORT).show();
				}
				camera.stopPreview();
				camera.startPreview();
				isPreview = true;
			}
		};

		public void findBestSize() {
			int bestWidth = 0;
			int bestHeight = 0;
			List<Camera.Size> previewSizes = Scan.this.parameters
					.getSupportedPreviewSizes();
			if (previewSizes.size() > 1) {
				Iterator<Camera.Size> cei = previewSizes.iterator();
				while (cei.hasNext()) {
					Camera.Size aSize = cei.next();
					Log.v("SNAPSHOT", "Checking " + aSize.width + " x "
							+ aSize.height);
					if (aSize.width > bestWidth && aSize.width <= screenHeight
							&& aSize.height > bestHeight
							&& aSize.height <= screenWidth) {
						// 迄今为止,它是最大的大小,且不超过屏幕尺寸
						bestWidth = aSize.width;
						bestHeight = aSize.height;
					}
				}
				if (bestHeight != 0 && bestWidth != 0) {
					Log.v("SNAPSHOT", "Using " + bestWidth + " x " + bestHeight);
					Scan.this.parameters.setPreviewSize(bestWidth, bestHeight);
					Scan.this.parameters.setPictureSize(bestWidth, bestHeight);
					sView.getLayoutParams().width = bestHeight;
					sView.getLayoutParams().height = bestWidth;

					// sView.setLayoutParams(new
					// LinearLayout.LayoutParams(bestWidth,
					// bestHeight));
					// Scan.this.parameters.setPreviewSize(144, 176);
					// sView.setLayoutParams(new LinearLayout.LayoutParams(144,
					// 176));
				}
			}

		}

	}