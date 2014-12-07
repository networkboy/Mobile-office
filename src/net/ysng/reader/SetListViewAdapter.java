package net.ysng.reader;

import net.one.ysng.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SetListViewAdapter extends BaseAdapter {


	private LayoutInflater mInflater;

	public SetListViewAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder=new ViewHolder();  

			//�������Ϊ��vlist��ȡview  ֮���view���ظ�ListView
			convertView = mInflater.inflate(R.layout.read_set_list_layout, null);
			holder.viewBtn = (Button)convertView.findViewById(R.id.read_set_list_btn);
			convertView.setTag(holder);				
		}else {				
			holder = (ViewHolder)convertView.getTag();
		}				


		//��Button��ӵ����¼�  ���Button֮��ListView��ʧȥ����  ��Ҫ��ֱ�Ӱ�Button�Ľ���ȥ��
		holder.viewBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                   
			}
		});

		return convertView;
	}
	//��ȡ���������
	public  class ViewHolder {
		public TextView title;
		public TextView info;
		public Button viewBtn;
	}
}


