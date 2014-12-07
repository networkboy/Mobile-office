package net.chh.picbrowser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.one.ysng.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Launch extends Activity implements OnGestureListener {
	private static final String TAG = "Launch";
	private static final int RIGHT = 1;
	private static final int LEFT = 0;
	private static final int DELETE = 3;

	private GestureDetector detector;
	private int No = 0;
	private int count = 0;
	private int currentNo;
	private ArrayList<String> path = new ArrayList<String>();
	private ArrayList<String> externalPath = new ArrayList<String>();
	public int screenWidth;
	public int screenHeight;
	private ViewFlipper flipper;
	private Button deleteBtn = null;
	private Matrix mt = null;
	private ImageView mImage = null;
	private boolean deleteSign = false;
	private float wp;
	private float hp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_large_image);

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		this.deleteBtn = (Button) super.findViewById(R.id.large_image_delete);
		detector = new GestureDetector(this);
		initFlipper();

		this.deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deleteChildAt(count);
			}
		});
	}

	private void initFlipper() {
		No = getIntent().getIntExtra("No", 0);
		path = getIntent().getStringArrayListExtra("path");
		externalPath = getIntent().getStringArrayListExtra("externalPath");

		currentNo = No;

		if (path.size() > 1) {
			for (int i = 0, j = No; i < 2; i++, j++) {
				mImage = new ImageView(this);
				mImage.setImageBitmap(getBitmap(j));
				mImage.setBackgroundColor(Color.BLACK);
				flipper.addView(mImage);
			}
		} else {
			mImage = new ImageView(this);
			mImage.setImageBitmap(getBitmap(No));
			mImage.setBackgroundColor(Color.BLACK);
			flipper.addView(mImage);
		}
		flipper.setDisplayedChild(0);
	}

	private int getChild() {
		count = (count + 1) % 2;
		Log.i(TAG, "" + count);
		return count;
	}

	private int exifInterfaceSet(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int tag = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
				degree = 90;
			} else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
				degree = 180;

			} else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
				degree = 270;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		return degree;
	}

	private Bitmap getBitmap(int num) {
		Bitmap bm = BitmapFactory.decodeFile(path.get(num));
		mt = new Matrix();

		int height = bm.getHeight();
		int width = bm.getWidth();
		int degree = exifInterfaceSet(path.get(num));

		if (degree == 90) {
			height = bm.getWidth();
			width = bm.getHeight();
		} else if (degree == 180) {
			height = bm.getHeight();
			width = bm.getWidth();
		} else if (degree == 270) {
			height = bm.getHeight();
			width = bm.getWidth();
		}

		if (width > height) {
			wp = ((float) screenWidth) / width;
		} else {
			wp = ((float) screenWidth) / width;
			hp = ((float) screenHeight) / height;
		}

		if (degree != 0 && degree != 270) {
			mt.setRotate(degree);
			if (width > height)
				mt.postScale(wp, wp);
			else
				mt.postScale(wp, hp);
			bm = Bitmap.createBitmap(bm, 0, 0, height, width, mt, true);
		} else {
			if (width > height)
				mt.postScale(wp, wp);
			else
				mt.postScale(wp, hp);
			bm = Bitmap.createBitmap(bm, 0, 0, width, height, mt, true);
		}

		return bm;
	}

	private int loop(int direction) {
		if (direction == 1) {
			if (++currentNo == path.size() - 1) {
				currentNo = 0;
			}
		} else if (direction == 0) {
			if (--currentNo == -1) {
				currentNo = path.size() - 1;
			}
		} else {
			if (currentNo == path.size()) {
				currentNo = 0;
			}
		}

		return currentNo;
	}

	private void slide(int direction) {
		mImage = (ImageView) flipper.getChildAt(getChild());

		switch (direction) {
		case RIGHT:
			mImage.setImageBitmap(getBitmap(loop(direction)));
			flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			flipper.showNext();
			break;
		case LEFT:
			mImage.setImageBitmap(getBitmap(loop(direction)));
			flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			flipper.showPrevious();
			break;
		case DELETE:
			if (path.size() == 0) {
				this.finish();
			} else {
				mImage.setImageBitmap(getBitmap(currentNo));
				flipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_out));
				flipper.showNext();
			}
			break;
		}

	}

	private void deleteChildAt(int num) {
		File file = null;
		ContentResolver resolver = getContentResolver();

		try {
			resolver.delete(Uri.parse(externalPath.get(currentNo)), null, null);
			externalPath.remove(externalPath.get(currentNo));
			file = new File(path.get(currentNo));
			file.delete();
			path.remove(path.get(currentNo));
			slide(DELETE);
		} catch (Exception e) {
			Log.i("error", e.getMessage());
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return detector.onTouchEvent(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (path.size() > 1) {
			if (e1.getX() - e2.getX() > 120) { // ÏòÓÒ
				slide(RIGHT);
			} else if (e2.getX() - e1.getX() > 120) { // Ïò×ó
				slide(LEFT);
			}
		}

		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (this.deleteSign) {
			this.deleteBtn.setVisibility(View.GONE);
			this.deleteSign = false;
		} else {
			this.deleteBtn.setVisibility(View.VISIBLE);
			this.deleteSign = true;
		}

		return false;
	}

}
