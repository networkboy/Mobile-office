package net.ysng.reader;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
public class ZoomListenter implements OnTouchListener {

	private int mode = 0;
	float oldDist;
	float textSize = 0;
	TextView textView = null;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode1 = NONE;
	private float oldDist1;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		textView = (TextView) v;
		if (textSize == 0) {
			textSize = textView.getTextSize();
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = 1;
			break;
		case MotionEvent.ACTION_UP:
			mode = 0;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode -= 1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			mode += 1;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode >= 2) {
				float newDist = spacing(event);
				if (newDist > oldDist + 1) {
					zoom(newDist / oldDist);
					oldDist = newDist;
				}
				if (newDist < oldDist - 1) {
					zoom(newDist / oldDist);
					oldDist = newDist;
				}
			}
			break;
		}
		return false;
	}

	private void zoom(float f) {
		textView.setTextSize(textSize *= f);
		
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

}
