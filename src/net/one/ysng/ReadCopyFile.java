package net.one.ysng;

import java.io.File;
import java.io.FileOutputStream;

import android.util.Log;

public class ReadCopyFile { 
	
	private File fromFile = null;
	public ReadCopyFile(){
	}
	
	public File getFromFile() {
		return fromFile;
	}
	public void setFromFile(File fromFile) {
		this.fromFile = fromFile;
	}

	public  boolean pasteFile(File file )
	{
		String toFilePath = null;;
		if(file.isFile()){
			toFilePath = file.getParentFile().getAbsolutePath() + File.separator  +"[copy] " +fromFile.getName();
		}else {
			toFilePath = file.getAbsolutePath() + File.separator  +"[copy] "+fromFile.getName() ;
		}
		
		File toFile = new File(toFilePath);
		if (!fromFile.exists()) {
			return false;
		}
		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() ) {
			toFile.delete();
		}
		try{
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); //将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
			return true;
		}catch(Exception ex){
			Log.e("readfile", ex.getMessage());
			return false;
		}
	}

}
