package net.one.ysng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class ReadXlsx  {
	private String filePath;
	public Range range = null;
	public HWPFDocument hwpf = null;
	public String htmlPath;
	public String picturePath;
	public TableIterator tableIterator;
	public int presentPicture = 0;
	public FileOutputStream output;
	public File myFile;
	private ArrayList<String> chlist = new ArrayList<String>();
	private int index =0;
	public int StrNum[];
	public int gridNumber = 0;


	public ReadXlsx(String path){
		this.StrNum = new int[40];
	    for(int i=0;i<40;i++){
	    	this.StrNum[i] = 9;
	    }
		this.filePath = path;
	}

/*********************************************************************
/ * ÿ��ѹ���ļ��ж�����Ѱ�����ļ���ÿһ�����ļ���java�о�ʹ��ZipEntry��ʾ
/********************************************************************/
	public ArrayList<String> readXLSX() {
		//Log.i("Log", "new list");
		try{

			String str = "";
			String v = null;
			boolean flat = false;
			List<String> ls = new ArrayList<String>();
			try {

				ZipFile xlsxFile = new ZipFile(new File(this.filePath));//��ַ  
				ZipEntry sharedStringXML = xlsxFile.getEntry("xl/sharedStrings.xml");//�����ַ���
				InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);//������   Ŀ������Ĺ����ַ���
				XmlPullParser xmlParser = Xml.newPullParser();//new ������
				xmlParser.setInput(inputStream, "utf-8");//���ý���������
				int evtType = xmlParser.getEventType();//��ȡ���������¼�����  
				while (evtType != XmlPullParser.END_DOCUMENT) {//���������  �ĵ�����
					switch (evtType) {
					case XmlPullParser.START_TAG:    //��ǩ��ʼ
						String tag = xmlParser.getName();
						if (tag.equalsIgnoreCase("t")) {
							ls.add(xmlParser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:    //��ǩ����
						break;
					default:
						break;
					}
					evtType = xmlParser.next();
				}
			
				ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
				InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
				XmlPullParser xmlParsersheet = Xml.newPullParser();
				xmlParsersheet.setInput(inputStreamsheet, "utf-8");
				int evtTypesheet = xmlParsersheet.getEventType();
			
				int i = -1;
				while (evtTypesheet != XmlPullParser.END_DOCUMENT) {   
					switch (evtTypesheet) {                       
					case XmlPullParser.START_TAG: //��ǩ��ʼ                      	
						String tag = xmlParsersheet.getName();
						if (tag.equalsIgnoreCase("row")) {
						} else {
							if (tag.equalsIgnoreCase("c")) {
								String t = xmlParsersheet.getAttributeValue(null, "t");
								if (t != null) {
									flat = true;
									System.out.println(flat + "��");
									//Log.i("Log", "1");
								} else {//û������ʱ  ������n��,�����ҵ���  ����<td></td> ��ʾ�ո�
							
									//System.out.println(flat + "û��");
									flat = false;
									chlist.add(" ");
									index++;
								}
							} else{ 
								if (tag.equalsIgnoreCase("v")) {
									v = xmlParsersheet.nextText();
								
									if (v != null) {                                        	
										if (flat) {
											str = ls.get(Integer.parseInt(v));
										} else {
											str = v;
										}
										if(str.length()>this.StrNum[this.index]){
											this.StrNum[this.index] =str.length();
										}
										chlist.add(str);
										index++;
									}
								}                              	
							}
						}                               
						break;
					case XmlPullParser.END_TAG:
						if (xmlParsersheet.getName().equalsIgnoreCase("row")&& v != null) {
							if(i == 1){
//								this.output.write(rowEnd.getBytes());
//								this.output.write(rowBegin.getBytes());
							}else{
//								this.output.write(rowBegin.getBytes());
								for(;index<26;index++){
									chlist.add("");			
								}
							
							  // this.StrNumIndex = 0;
							   this.index = 0;
							   this.gridNumber++;
							}	
						}
						break;
					}
					evtTypesheet = xmlParsersheet.next();    
					//Log.i("Log", "6");
				}
				System.out.println(str);
				
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			if (str == null) {
				str = "�����ļ���������";
			}

		}catch(Exception e){
			System.out.println("readAndWrite Exception");
		}		
	return chlist;
		
	}
	

}
