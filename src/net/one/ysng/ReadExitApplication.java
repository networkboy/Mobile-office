package net.one.ysng;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ReadExitApplication extends Application {

	private List<Activity> activityList = new LinkedList<Activity>(); 
	private static ReadExitApplication instance;

	private ReadExitApplication(){
	}
	
	//����ģʽ�л�ȡΨһ��ExitApplicationʵ�� 
	public static ReadExitApplication getInstance(){
		if(null == instance){
			instance = new ReadExitApplication();
		}
		return instance; 
	}
	
	//���Activity��������
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//��������Activity��finish
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		System.exit(0);
	}
}