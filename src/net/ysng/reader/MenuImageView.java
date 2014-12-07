package net.ysng.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MenuImageView extends ImageView {
	/**
	 * MenuImageView对象的宽度
	 */
	private int vWidth;
	/**
	 * MenuImageView对象的高度
	 */
	private int vHeight;
	/**
	 * 正在缩小标志过程进行标志
	 */
	private boolean isReduce = false;
	/**
	 * 从缩小恢复到正常过程进行标志
	 */
	private boolean isRecover = false;
	/**
	 * 缩放倍数
	 * 
	 */
	private float minScal = 0.85f;

	public MenuImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 在XML文件布置空间的时候需要含有AttributeSet参数的这个构造器，不然会报错
	 * 
	 * @param context
	 * @param attrs
	 */
	public MenuImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		super.onDraw(canvas);
			initi();
		

		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
	}

	private void initi() {
		vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		vHeight = getHeight() - getPaddingTop() - getPaddingBottom();
	//	Log.i("vWidth + vWidth", vWidth + " " + vHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		///	Log.i("ACTION_DOWN", "ACTION_DOWN");
			handler.sendEmptyMessage(1);

			break;
		case MotionEvent.ACTION_UP:
		//	Log.i("ACTION_UP", "ACTION_UP");
			handler.sendEmptyMessage(2);

			break;

		default:
			break;
		}
		return true;// return super.onTouchEvent(event);的话捕捉不到UP动作
	}

	Handler handler = new Handler() {
		float s = 0;// 缩放倍数
		int count = 0;// 计数器
		Matrix matrix = new Matrix();

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			matrix.set(getImageMatrix());
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (isRecover) {
					// 如果ImageView处于从小图恢复到大图时候，进入等待
					handler.sendEmptyMessage(1);
				} else {
					isReduce = true;
					// 进行两次开方，为了是把minScal分四次缩放，是的缩放过程变慢
					count = 0;
					s = (float) Math.sqrt(Math.sqrt(minScal));
					doScale(s, matrix);
					handler.sendEmptyMessage(3);
				//	Log.i("postScale", "缩小" + " 倍数：" + s + matrix.toString());
				}

			} else if (msg.what == 2) {
				// 用1去除，求得倒数，用来还原
				if (isReduce) {
					handler.sendEmptyMessage(2);
				} else {
					isRecover = true;
					count = 0;
					s = (float) (Math.sqrt(Math.sqrt(1f / minScal)));
					doScale(s, matrix);
					handler.sendEmptyMessage(3);
				//	Log.i("postScale", "恢复");
				}

			} else if (msg.what == 3) {
				// 对缩分四次进行，达到更明显的缩放效果。
				doScale(s, matrix);
				if (count < 4) {
					handler.sendEmptyMessage(3);
				} else {
					// 缩放结束后，把两个状态量都设置为false，让另一个相反操作--缩（放）可以进入操作
					isReduce = false;
					isRecover = false;

				}
				count++;
			//	Log.i("count", "count " + count);
			}
		}

	};

	private void doScale(float size, Matrix matrix) {
		matrix.postScale(size, size, (int) vWidth * 0.5f, (int) vHeight * 0.5f);
		MenuImageView.this.setImageMatrix(matrix);
		//Log.i("scale", "scale " + size);
	}

}
