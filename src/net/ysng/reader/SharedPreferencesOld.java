package net.ysng.reader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesOld {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	public SharedPreferencesOld(Context context,String flag) {
		//本程序调用
		sharedPreferences=context.getSharedPreferences(flag, context.MODE_PRIVATE);
		editor=sharedPreferences.edit();
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
	
	
	public Map<String, ?> getAll(){
		return sharedPreferences.getAll();
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
	
	public void putString(String path){
		Collection<?> values =this.getAll().values();
		Iterator<?> iter = values.iterator();
		//clear();
		while(iter.hasNext()){
			String filePath = (String)iter.next();
			if(path.equals(filePath)){
				return;
			}
		}	
		editor.putString(new java.util.Date().toString(), path);
		editor.commit();
	}

}
