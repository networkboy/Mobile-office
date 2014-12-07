package net.ysng.reader;

import java.io.File;

import android.R.integer;


public class ReadConstant {	
	public static final String FILENAME = "oneSkin";
	public static final String OLDNAME = "oneOld";
	public static final String MAINLISTBG = "mainListBg";
	public static final String SHOWTITLEBG = "showTitleBg"; 
	public static final String MAINBG= "mainBg";
	public static final String SKINVIEW = "skinView";
	public static final String LASTNUM = "lastNum";
	public static final String TITLEBG = "titleBg";
	public static final String MAILUSER = "mailUser";
	public static final String USERID = "UserId";
	public static final String USERCODE = "UserCode";
	public static final String USERSTEMP= "UserStemp";
	public static final String SEARCHDATE = "SearchDate";
	public static final String SEARCHWORD = "SearchWord";
	public static final String SEARCHEXCEL= "SearchExcel";
	public static final String  SEARCHPDF = "SearchPdf";
	public static final File    PARENTPATH = android.os.Environment.getExternalStorageDirectory();
	public static final String ONE = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()  + File.separator + "One" ;
	public static final String ONEFOLDER= ONE + File.separator + "one";
	public static final String SHOTFOLDER= ONE + File.separator + "OneScreenshot";
	public static final String ONEHTML= ONEFOLDER + File.separator + "one.html";
	public static final String ONETXT= ONEFOLDER + File.separator + "one.txt";
	public static final String PDFFOLDER = ONE+ File.separator + "Pdf";
	public static  int signModule  = 0;
	
}
