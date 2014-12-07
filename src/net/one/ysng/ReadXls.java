package net.one.ysng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.ss.util.CellRangeAddress;



import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;

public class ReadXls  {

	private String filePath = null;

	public String htmlPath=null;
	private String picturePath;
	private WebView view;
	private List pictures;
	private TableIterator tableIterator;
	private int presentPicture = 0;
	private int screenWidth; //��Ļ���
	private FileOutputStream output;
	private File myFile;
	private int num = 1;
	public int StrNum[];
	private int StrNumIndex = 0;
	public int maxNum = 0;
	public 	ArrayList<ArrayList<String>> list_set ;
	public  ArrayList<ArrayList<ArrayList<String>>> strList  = null;


	public ReadXls(String path){
		this.StrNum = new int[40];
	    for(int i=0;i<40;i++){
	    	this.StrNum[i] = 9;
	    }

   filePath = path;
	
	}

	/************************************************************************/
	/*****             ��ȡ.xls�е�����д��sdcard�ϵ�.html�ļ���
	/************************************************************************/
	public ArrayList<ArrayList<String>> readXLS() throws Exception {
		this.strList =new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		 list_set = new ArrayList<ArrayList<String>>();

		String excelFileName =filePath;
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(excelFileName)); // ������Excel

			for ( int sheetIndex=0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {   //��ѭ��
				ArrayList<ArrayList<String>> listy = new ArrayList<ArrayList<String>>();
				HSSFSheet sheet = workbook.getSheetAt(sheetIndex);          // �����е�sheet
				String sheetName = workbook.getSheetName(sheetIndex);   // sheetName
				if (sheet != null) {
					int firstRowNum = sheet.getFirstRowNum();                 // ��һ��
					int lastRowNum = sheet.getLastRowNum() ;                  // ���һ��
					for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {               //��ѭ��	
						HSSFRow row = sheet.getRow(rowNum);                // ��õ�ǰ��
						ArrayList<String> chlist = new ArrayList<String>();
						ArrayList<String> chlisty = new ArrayList<String>();
						this.StrNumIndex = 0;
						ArrayList<String> chlist_set = new ArrayList<String>();
						int listSize = 0;
						if (row != null) {                                                      // ����в�Ϊ�գ�

							short firstCellNum = row.getFirstCellNum();        // ���еĵ�һ����Ԫ��
							short lastCellNum = row.getLastCellNum();         // ���е����һ����Ԫ��							
							for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {       // ѭ�����е�ÿһ����Ԫ��
								HSSFCell cell = row.getCell(cellNum);			
								if (cell != null) {
									
									chlist.add((String)getCellValue(cell));
									chlisty.add((String)getCellValue(cell));
									if(((String)getCellValue(cell)).length()>this.StrNum[this.StrNumIndex]){
										this.StrNum[this.StrNumIndex] = (((String)getCellValue(cell)).length());
									}
									this.StrNumIndex++;
									
									
									/*****************************/
									HSSFCellStyle cellStyle = cell.getCellStyle();
									HSSFPalette palette = workbook.getCustomPalette(); // ��HSSFPalette��������ɫ�Ĺ��ʱ�׼��ʽ
									HSSFColor hColor = palette.getColor(cellStyle.getFillForegroundColor());
									HSSFColor hColor2 = palette.getColor(cellStyle.getFont(workbook).getColor());
									String bgColor = convertToStardColor(hColor);                                               // ������ɫ
									short boldWeight = (short) (cellStyle.getFont(workbook).getBoldweight()/1.5);                   // �����ϸ
									short fontHeight = (short) (cellStyle.getFont(workbook).getFontHeight() / 5); // �����С
									String fontColor = convertToStardColor(hColor2);                                           // ������ɫ
									chlist_set.add(fontColor);
									/*****************************/


								}								
							}
							listSize=chlist.size();
						}

						if(chlist.size()<40){//26
							for(int k = chlist.size();k<30;k++){
								chlist.add(null);
								chlist_set.add(null);
							}	
						}
                        list_set.add(chlist_set);
						list.add(chlist);	
						listy.add(chlisty);
						
						if(chlist.size()>this.maxNum){
							this.maxNum = chlist.size();
						}				
					}
				}		
	         strList.add(listy);
			}
			if(list.size()<36){
				for(int x=list.size();x<36;x++){
					ArrayList<String> chlisterr = new ArrayList<String>();
					for(int y=0;y<30;y++){//26
						chlisterr.add(null);
					}
					list.add(chlisterr);
					list_set.add(chlisterr);
				}
			}

		} catch (FileNotFoundException e) {
			throw new Exception("�ļ� " + excelFileName + " û���ҵ�!");
		} catch (IOException e) {
			throw new Exception("�ļ� " + excelFileName + " �������("+ e.getMessage() + ")!");
		}
		return list;
	}

	/**
	 * ȡ�õ�Ԫ���ֵ
	 * 
	 * @param cell
	 * @return
	 * @throws IOException
	 */
	private static Object getCellValue(HSSFCell cell) throws IOException {
		Object value = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			value = cell.getRichStringCellValue().toString();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = (Date) cell.getDateCellValue();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				value = sdf.format(date);
			} else {
				double value_temp = (double) cell.getNumericCellValue();
				BigDecimal bd = new BigDecimal(value_temp);
				BigDecimal bd1 = bd.setScale(3, bd.ROUND_HALF_UP);
				value = bd1.doubleValue();

				DecimalFormat format = new DecimalFormat("#0.###");
				value = format.format(cell.getNumericCellValue());
			}
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			value = "  ";
		}
		return value;
	}
	
	/**
	 * ��Ԫ�񱳾�ɫת��
	 * 
	 * @param hc
	 * @return
	 */
	private String convertToStardColor(HSSFColor hc) {
		StringBuffer sb = new StringBuffer("");
		if (hc != null) {
			int a = HSSFColor.AUTOMATIC.index;
			int b = hc.getIndex();
			if (a == b) {
				return null;
			}
			sb.append("#");
			for (int i = 0; i < hc.getTriplet().length; i++) {
				String str;
				String str_tmp = Integer.toHexString(hc.getTriplet()[i]);
				if (str_tmp != null && str_tmp.length() < 2) {
					str = "0" + str_tmp;
				} else {
					str = str_tmp;
				}
				sb.append(str);
			}
		}
		return sb.toString();
	}




}