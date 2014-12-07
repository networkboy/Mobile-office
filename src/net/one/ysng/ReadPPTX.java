package net.one.ysng;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ReadPPTX  extends Activity{
	
	private TextView  showView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.read_pptx);
		
		this.showView = (TextView)super.findViewById(R.id.read_pptx_view);
		//this.showView.setText(text);
	}
	
	

}
