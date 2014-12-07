package net.one.ysng;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ysng.reader.ReadConstant;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
public class ReadDoc {

	private String filePath = null;
	public Range range = null;
	public HWPFDocument hwpf = null;
	public String htmlPath=null;
	private String picturePath;
	public List pictures;
	private TableIterator tableIterator;
	private int presentPicture = 0;
	private int screenWidth; //屏幕宽度

	private FileOutputStream output;
	private FileOutputStream outputTxt;
	private File myFile;
	private File myFileTxt;
	private File oneFile;
	private File wOneFile;
	private ReadConstant myConstant ;
	/****************************测试数据************************************/
	public String str;
	/************************************************************************/

	public ReadDoc(String path,int width){
		
		this.filePath = path;
		this.screenWidth = width;
    
		this.getRange();		
		this.makeFile();	
		this.readAndWrite();
	}

	/************************************************************************/
	/*****           创建YiDu文件夹并在此目录下创建one.html文件来存储
	/*****           转换后的Word文件
	/************************************************************************/
	public void makeFile(){
		String sdStateString = android.os.Environment.getExternalStorageState();
		if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))            //判断SD卡是否存在
		{    
			try{
				File sdFile = android.os.Environment.getExternalStorageDirectory();     // 获取扩展存储设备的文件目录  
				if(!sdFile.exists())
				{
					Toast.makeText(null, "1", Toast.LENGTH_LONG).show();
				}
				//getAbsolutePath() : 返回抽象路径名的绝对路径名字符串。
				//File.separator（静态常量）表示分隔符 Windows中为”\“，Linux中为”/“
                
				wOneFile = new File(this.myConstant.ONE);
				if(!wOneFile.exists())                          //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
				{
					wOneFile.mkdir();                              //创建此抽象路径名指定的目录。
				}  			
				oneFile = new File(this.myConstant.ONEFOLDER);
				if(!oneFile.exists())                          //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
				{
					oneFile.mkdir();                              //创建此抽象路径名指定的目录。
				}  			
				myFile = new File(this.myConstant.ONEHTML);
				this.myFileTxt = new File(this.myConstant.ONETXT);
				if(!myFile.exists())
				{
					myFile.createNewFile();   //创建文件
				}
				if(!myFileTxt.exists())
				{
					myFileTxt.createNewFile();   //创建文件
				}
				htmlPath = myFile.getAbsolutePath();

			}
			catch(Exception e){

			}
		}
	}

	/************************************************************************/
	/*****                              用来在sdcard上创建图片
	/************************************************************************/
	public void makePictureFile(){
		String sdString = android.os.Environment.getExternalStorageState();
		if(sdString.equals(android.os.Environment.MEDIA_MOUNTED)){
			try{
				File picFile = android.os.Environment.getExternalStorageDirectory();
//				String picPath = picFile.getAbsolutePath() + File.separator + "One";
				File picDirFile = new File(this.myConstant.ONEFOLDER);
				if(!picDirFile.exists()){
					picDirFile.mkdir();
				}
				File pictureFile = new File(this.myConstant.ONEFOLDER + File.separator + presentPicture + ".jpg");
				if(!pictureFile.exists()){
					pictureFile.createNewFile();
				}
				picturePath = pictureFile.getAbsolutePath();
			}
			catch(Exception e){
				System.out.println("PictureFile Catch Exception");
			}
		}
	}

	/************************************************************************/
	/*****             读取word中的内容写到sdcard上的.html文件中
	/************************************************************************/
	public void readAndWrite(){
		try{
			output = new FileOutputStream(myFile);
			outputTxt = new FileOutputStream(myFileTxt);
			String head = "<html> <body>";
			String tagBegin = "<p";
			String tagEnd = "</p>";

			// getBytes() : 是将一个字符串转化为一个字节数组。
			output.write(head.getBytes());
			outputTxt.write(head.getBytes());
			//numParagraphs() : 返回在此范围内的段落数
			int numParagraphs = range.numParagraphs();	

			for(int i = 0; i < numParagraphs; i++){
				//getParagraph() : 返回在此范围内的指定索引段
				Paragraph p = range.getParagraph(i);
                //如果是表格
				if(p.isInTable()){
					i = writeTableContent(i);		
				}
				else{
					outputTxt.write(tagBegin.getBytes());
					output.write(tagBegin.getBytes());
					writeParagraphContent(p,true);
					output.write(tagEnd.getBytes());
					outputTxt.write(tagEnd.getBytes());
				}
			}
			String end = "</body></html>";
			output.write(end.getBytes());
			output.close();
			outputTxt.write(end.getBytes());
			outputTxt.close();
		}
		catch(Exception e){
			System.out.println("readAndWrite Exception");
		}
	}
	
	/************************************************************************/
	/*****             读取表格写入.html文件中
	/************************************************************************/
	public int writeTableContent(int i) throws IOException{
		
		int temp = i;   //表格所在段落
		if(tableIterator.hasNext()){
			String tagBegin = "<p";
			String tagEnd = "</p>";
			String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\" align=\"center\">";
			String tableEnd = "</table>";
			String rowBegin = "<tr>";
			String rowEnd = "</tr>";
			String colBegin = "<td";
			String colEnd = "</td>";
			
			Table table = tableIterator.next();
			output.write(tableBegin.getBytes());
			outputTxt.write(tableBegin.getBytes());
			int rows = table.numRows();//表格行数
			
/*****************************测试（上线）*****************************************/			
			Map<Integer,Integer> cellIndexMapping=new HashMap<Integer,Integer>();
			int maxNumCellIndex=0;//单元格最多的哪一行
			int columns=0;               //单元格最多的行有多少个单元格
			for (int a = 0; a < rows; a++) {        
				TableRow tr = table.getRow(a);
				if(columns<tr.numCells()){
					maxNumCellIndex=a;
					columns=tr.numCells();
				}
			}		
			 for (int j = 0; j < columns; j++) {        
	             TableCell td = table.getRow(maxNumCellIndex).getCell(j);//取得单元格     
	             cellIndexMapping.put(td.getLeftEdge(),j+1);
	         }
     
 /*********************************测试（下线）*************************************/	
			//循环行
			for( int r = 0; r < rows; r++){
				output.write(rowBegin.getBytes());
				outputTxt.write(rowBegin.getBytes());
				TableRow row = table.getRow(r);		//获得当前表格行					
				int cols = row.numCells();                //当前行单元格数
				int rowNumParagraphs = row.numParagraphs();  //表格行段落数
				int colsNumParagraphs = 0;
				//单元格循环
				for( int c = 0; c < cols; c++){
					output.write(colBegin.getBytes());
					outputTxt.write(colBegin.getBytes());
					TableCell cell = row.getCell(c);   //获得当前单元格
/*****************************测试（上线）*****************************************/		
					TableCell td2;
					  if(c==cols-1){
				             td2 = row.getCell(c);
				            }else{
				             td2 = row.getCell(c+1);
				            }
					  //获取横跨colspan
		            int td1_edge=cell.getLeftEdge();
		            int td2_edge=td2.getLeftEdge();
		            int td1_index=cellIndexMapping.get(td1_edge).intValue();
		            int td2_index=cellIndexMapping.get(td2_edge).intValue();
		            int span=td2_index-td1_index;
		            //解决最后一个单元格跨行到最结尾
		            if(cols<columns&&c==cols-1){
		             span=columns-cols+1;
		            }
		            String ss =" align=\"center\"" + " valign=\"middle\"" + " colspan="+span+">";    
                    output.write(ss.getBytes());
                    outputTxt.write(ss.getBytes());
 /*********************************测试（下线）*************************************/										
				    int max = temp + cell.numParagraphs(); // 表格所在段落+ 单元格段落数
					colsNumParagraphs = colsNumParagraphs + cell.numParagraphs();
					//对于每一个表格按段落来读取
					for(int cp = temp; cp < max; cp++){
						Paragraph p1 = range.getParagraph(cp);  //获得当前段落	
						output.write(tagBegin.getBytes());    
						outputTxt.write(tagBegin.getBytes());    
						writeParagraphContent(p1,false);                       //按段写入
						output.write(tagEnd.getBytes());
						outputTxt.write(tagEnd.getBytes());
						temp++;
					}
					output.write(colEnd.getBytes());
					outputTxt.write(colEnd.getBytes());
				}
				int max1 = temp + rowNumParagraphs;
				for(int m = temp + colsNumParagraphs; m < max1; m++){
					Paragraph p2 = range.getParagraph(m);
					temp++;
				}
				output.write(rowEnd.getBytes());
				outputTxt.write(rowEnd.getBytes());
			}
			output.write(tableEnd.getBytes());
			outputTxt.write(tableEnd.getBytes());
		}
		i = temp;
		return i;
		
	}
	/************************************************************************/
	/*****             以段落的形式来往html文件中写内容
	/*************************************************************************/
	
	public void writeParagraphContent(Paragraph paragraph ,boolean sign) throws IOException{
		if(sign){
			//String str =">&nbsp;&nbsp;&nbsp;&nbsp;";
			String str =">";
			output.write(str.getBytes());
			outputTxt.write(str.getBytes());
		}else{
			//String str = "align=\"center\">";
			String str = ">";
		    output.write(str.getBytes());
		    outputTxt.write(str.getBytes());
		}
		Paragraph p = paragraph;
		int pnumCharacterRuns = p.numCharacterRuns();

		for( int j = 0; j < pnumCharacterRuns; j++){

			CharacterRun run = p.getCharacterRun(j);

			if(run.getPicOffset() == 0 || run.getPicOffset() >= 1000){
				if(presentPicture < pictures.size()){
					writePicture();
				}
			}
			else{
				try{
					String text = run.text();
					if(text.length() >= 2 && pnumCharacterRuns < 2){		
						output.write(text.getBytes());
						outputTxt.write(text.getBytes());
					}
					else{
						int size = run.getFontSize();					
						int color = run.getColor();
						String fontSizeBegin = "<font size=\"" + size/14.0 + "px"+ "\">";
						String fontColorBegin = "<font color=\"" + decideColor(color) + "\">";
						String fontEnd = "</font>";
						String boldBegin = "<b>";
						String boldEnd = "</b>";
						String islaBegin = "<i>";
						String islaEnd = "</i>";

						output.write(fontSizeBegin.getBytes());
						output.write(fontColorBegin.getBytes());	
						outputTxt.write(fontSizeBegin.getBytes());
						outputTxt.write(fontColorBegin.getBytes());				
						if(run.isBold()){
							output.write(boldBegin.getBytes());
							outputTxt.write(boldBegin.getBytes());
						}
						if(run.isItalic()){
							output.write(islaBegin.getBytes());
							outputTxt.write(islaBegin.getBytes());
						}

						output.write(text.getBytes());
						outputTxt.write(text.getBytes());

						if(run.isBold()){
							output.write(boldEnd.getBytes());
						}
						if(run.isItalic()){
							output.write(islaEnd.getBytes());
							outputTxt.write(islaEnd.getBytes());
						}
						output.write(fontEnd.getBytes());
						output.write(fontEnd.getBytes());
						outputTxt.write(fontEnd.getBytes());
						outputTxt.write(fontEnd.getBytes());
					}
				}
				catch(Exception e){
					System.out.println("Write File Exception");
				}
			}
		}
	}

	/************************************************************************/
	/*****             将word中的图片写入到.jpg文件中
	/************************************************************************/
	public void writePicture(){
		Picture picture = (Picture)pictures.get(presentPicture);

		byte[] pictureBytes = picture.getContent();

		Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);

		makePictureFile();
		presentPicture++;

		File myPicture = new File(picturePath);

		try{

			FileOutputStream outputPicture = new FileOutputStream(myPicture);

			outputPicture.write(pictureBytes);

			outputPicture.close();
		}
		catch(Exception e){
			System.out.println("outputPicture Exception");
		}

		String imageString = "<img src=\"" + picturePath + "\"";

		//if(bitmap.getWidth() > screenWidth){
		//设置图片显示大小
			double width =bitmap.getWidth()/1.5;
			double height =  bitmap.getHeight()/1.5;
			
			if(width>screenWidth){
				width = screenWidth/1.55;
				height = bitmap.getHeight()/(bitmap.getWidth()/width);
			}
			imageString = imageString +" " + "width=\"" + width+ "\" height=\""+ height +"\"";
		//}
		imageString = imageString + ">";

		try{
			output.write(imageString.getBytes());
			outputTxt.write(imageString.getBytes());
		}
		catch(Exception e){
			System.out.println("output Exception");
		}
	}


	/************************************************************************/
	/*****                    处理word和html颜色的转换
	/************************************************************************/
	private String decideColor(int a){
		int color = a;
		switch(color){
		case 1:
			return "#000000";
		case 2:
			return "#0000FF";
		case 3:
		case 4:
			return "#00FF00";
		case 5:
		case 6:
			return "#FF0000";
		case 7:
			return "#FFFF00";
		case 8:
			return "#FFFFFF";
		case 9:
			return "#CCCCCC";
		case 10:
		case 11:
			return "#00FF00";
		case 12:
			return "#080808";
		case 13:
		case 14: 
			return "#FFFF00";
		case 15: 
			return "#CCCCCC";
		case 16:
			return "#080808";
		default:
			return "#000000";
		}
	}

	/************************************************************************/
	/*****                    获取Word文件中的文字 图片 表格
	/************************************************************************/
	private void getRange(){
		FileInputStream in = null;
		POIFSFileSystem pfs = null;
		try{
			in = new FileInputStream(filePath);
			pfs = new POIFSFileSystem(in);
			hwpf = new HWPFDocument(pfs);
		}
		catch(Exception e){

		}
		range = hwpf.getRange();     //返回的范围涵盖了整个文档，但不包括任何页眉和页脚。
		// getPicturesTable(): 返回PicturesTable对象，也就是能够从本文档中提取图像
		//getAllPictures(): 返回在当前文档中的图片对象的列表
		//	this.string = range.text();

		pictures = hwpf.getPicturesTable().getAllPictures(); 

		tableIterator = new TableIterator(range);

	}


}


