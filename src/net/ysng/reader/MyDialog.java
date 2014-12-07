package net.ysng.reader;



import net.one.ysng.ReadExitApplication;
import net.one.ysng.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;


public class MyDialog  {
	private Context context;

	public MyDialog(Context context) { 
		this.context = context; 
		} 
	
	/************************************************************************/
	/*****                               创建Dialog
	/************************************************************************/
	public  void createDialog(String title,String meg){

		Dialog dialog = new AlertDialog.Builder(this.context)
		.setTitle(title)
		.setMessage(meg)
		.setPositiveButton("确认",new DialogInterface.OnClickListener() {		                            			
			public void onClick(DialogInterface dialog, int which) {	
					ReadExitApplication.getInstance().exit();					   
			}
		})
		.setNeutralButton("取消", new DialogInterface.OnClickListener() {		                            			
			public void onClick(DialogInterface dialog, int which) {	
				dialog.dismiss();
			}
		})
		.create();
		
		Window window = dialog.getWindow();  
		window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.mystyle);  //添加动画  
		dialog.show();
		
		
}
}
