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
	
	//单例模式中获取唯一的ExitApplication实例 
	public static ReadExitApplication getInstance(){
		if(null == instance){
			instance = new ReadExitApplication();
		}
		return instance; 
	}
	
	//添加Activity到容器中
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//遍历所有Activity并finish
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		System.exit(0);
	}
}