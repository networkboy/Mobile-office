package net.ysng.reader;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MultiPointTouchListener implements OnTouchListener {
	private static final String LOG_TAG = "MultiPointTouchListener";

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float mLastDist;
	private float mOldScale = 1;
	private float mLastScale;


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			mLastDist = oldDist;
			if (oldDist > 10f) {
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == ZOOM) {
				mOldScale = mLastScale;
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				return onMove(event.getX() - start.x, event.getY() - start.y);
			} else if (mode == ZOOM) {
//				Log.e(LOG_TAG, "zooming");
				float lastDist = spacing(event);
//				Log.e(LOG_TAG, "oldDist: " + oldDist);
//				Log.e(LOG_TAG, "lastDist: " + lastDist);
				if (lastDist > 10f) {
					float lastScale = mLastScale;
					mLastScale = lastDist / oldDist * mOldScale;
//					Log.e(LOG_TAG, "lastScale: " + lastScale);
//					Log.e(LOG_TAG, "mLastScale: " + mLastScale);
					float clearScale = lastDist / mLastDist;
					mLastDist = lastDist;
//					Log.e(LOG_TAG, "scalexxx: " + (mLastScale - lastScale));
					if(Math.abs(mLastScale - lastScale) > 0.001){
						return onScale(mOldScale, lastScale, mLastScale, clearScale, mid.x, mid.y);
					}
					return true;
				}
			}
			break;
		}

		return false;
	}
	
	protected boolean onMove(float x, float y){
		return false;
	}
	
	protected boolean onScale(float oldScale, float lastScale, float newScale, float clearScale, float midX, float midY){
		return false;
	}

	protected void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		//Log.e(LOG_TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
