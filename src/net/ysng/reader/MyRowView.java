package net.ysng.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MyRowView extends TextView implements Cloneable{

	public MyRowView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return super.length();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
	}

	@Override
	public void setTextColor(int color) {
		// TODO Auto-generated method stub
		super.setTextColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}
    

	
	/*****************************************************************************************
	 * 功能：重写clone()方法，采用原型模式，产生新的对象（拷贝）
	 * 
	 * @return     TextView
	 * @throws   CloneNotSupportedException
	 *****************************************************************************************/
	@Override
	public MyRowView clone() throws CloneNotSupportedException {
		MyRowView textView = null;
		try {
			    textView = (MyRowView)super.clone();
		} catch (Exception e) {
		}
		return textView;
	}

}
