package net.ysng.reader;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSkin {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	public SharedPreferencesSkin(Context context,String flag) {
		//本程序调用
		sharedPreferences=context.getSharedPreferences(flag, context.MODE_PRIVATE);
		editor=sharedPreferences.edit();
	}
	public void putString(String key,String value){
		editor.putString(key, value);
		editor.commit();
	}
	public String getString(String key){
		return sharedPreferences.getString(key, "");
	}
	
	public void putInt(String key,int value){
		editor.putInt(key, value);
		editor.commit();
	}
	public int getInt(String key){
		return sharedPreferences.getInt(key, 0);
	}
	
	public boolean contains(String key) {
		return sharedPreferences.contains(key);
	}
	public void remove(String key){
		editor.remove(key);
		editor.commit();
	}
	public void clear(){
		editor.clear();
		editor.commit();
	}

}
