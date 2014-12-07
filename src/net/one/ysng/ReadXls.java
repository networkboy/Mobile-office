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
	private int screenWidth; //屏幕宽度
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
	/*****             读取.xls中的内容写到sdcard上的.html文件中
	/************************************************************************/
	public ArrayList<ArrayList<String>> readXLS() throws Exception {
		this.strList =new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		 list_set = new ArrayList<ArrayList<String>>();

		String excelFileName =filePath;
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(excelFileName)); // 获整个Excel

			for ( int sheetIndex=0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {   //表循环
				ArrayList<ArrayList<String>> listy = new ArrayList<ArrayList<String>>();
				HSSFSheet sheet = workbook.getSheetAt(sheetIndex);          // 获所有的sheet
				String sheetName = workbook.getSheetName(sheetIndex);   // sheetName
				if (sheet != null) {
					int firstRowNum = sheet.getFirstRowNum();                 // 第一行
					int lastRowNum = sheet.getLastRowNum() ;                  // 最后一行
					for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {               //行循环	
						HSSFRow row = sheet.getRow(rowNum);                // 获得当前行
						ArrayList<String> chlist = new ArrayList<String>();
						ArrayList<String> chlisty = new ArrayList<String>();
						this.StrNumIndex = 0;
						ArrayList<String> chlist_set = new ArrayList<String>();
						int listSize = 0;
						if (row != null) {                                                      // 如果行不为空，

							short firstCellNum = row.getFirstCellNum();        // 该行的第一个单元格
							short lastCellNum = row.getLastCellNum();         // 该行的最后一个单元格							
							for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {       // 循环该行的每一个单元格
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
									HSSFPalette palette = workbook.getCustomPalette(); // 类HSSFPalette用于求颜色的国际标准形式
									HSSFColor hColor = palette.getColor(cellStyle.getFillForegroundColor());
									HSSFColor hColor2 = palette.getColor(cellStyle.getFont(workbook).getColor());
									String bgColor = convertToStardColor(hColor);                                               // 背景颜色
									short boldWeight = (short) (cellStyle.getFont(workbook).getBoldweight()/1.5);                   // 字体粗细
									short fontHeight = (short) (cellStyle.getFont(workbook).getFontHeight() / 5); // 字体大小
									String fontColor = convertToStardColor(hColor2);                                           // 字体颜色
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
			throw new Exception("文件 " + excelFileName + " 没有找到!");
		} catch (IOException e) {
			throw new Exception("文件 " + excelFileName + " 处理错误("+ e.getMessage() + ")!");
		}
		return list;
	}

	/**
	 * 取得单元格的值
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
	 * 单元格背景色转换
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