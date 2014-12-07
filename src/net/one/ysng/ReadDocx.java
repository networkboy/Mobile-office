package net.one.ysng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.ysng.reader.ReadConstant;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

public class ReadDocx {

	public String nameStr = null;
	// public Range range = null;
	private String htmlPath = null;
	private String picturePath;
	public int presentPicture = 0;
	private int screenWidth; // 屏幕宽度
	private FileOutputStream output;
	private File myFile;
	private ReadConstant myConstant;

	public ReadDocx(String path, int with) {
		nameStr = path;
		screenWidth = with;

		this.makeFile();
		this.readDOCX();

	}

	/************************************************************************/
	/*****
	 * 创建YiDu文件夹并在此目录下创建uit.html文件来存储 /***** 转换后的Word文件 /
	 ************************************************************************/
	public void makeFile() {
		String sdStateString = android.os.Environment.getExternalStorageState(); // 获取外部存储状态
		if (sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) { // 判断SD卡是否存在
			try {
				File wOneFile = new File(this.myConstant.ONE);
				if (!wOneFile.exists()) // 测试此抽象路径名表示的文件或目录是否存在。存在时返回true
				{
					wOneFile.mkdir(); // 创建此抽象路径名指定的目录。
				}
				File dirFile = new File(this.myConstant.ONEFOLDER); // 获取YiDu文件夹地址
				if (!dirFile.exists()) { // 如果不存在
					dirFile.mkdir(); // 创建目录
				}
				File myFile = new File(this.myConstant.ONEHTML); // 获取my.html的地址
				if (!myFile.exists()) { // 如果不存在
					myFile.createNewFile(); // 创建文件
				}
				this.htmlPath = myFile.getAbsolutePath(); // 返回路径
			} catch (Exception e) {
			}
		}
	}

	/************************************************************************/
	/*****
	 * 读取.docx中的内容写到sdcard上的.html文件中 /
	 ************************************************************************/
	public void readDOCX() {
		String river = "";
		try {
			this.myFile = new File(this.htmlPath); // new一个File,路径为html文件
			this.output = new FileOutputStream(this.myFile); // new一个流,目标为html文件
			String head = "<!DOCTYPE><html><meta charset=\"utf-8\"><body>"; // 定义头文件,我在这里加了utf-8,不然会出现乱码
			String end = "</body></html>";
			String tagBegin = "<p>"; // 段落开始,标记开始?
			String tagEnd = "</p>"; // 段落结束
			String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\">";
			String tableEnd = "</table>";
			String rowBegin = "<tr>";
			String rowEnd = "</tr>";
			String colBegin = "<td>";
			String colEnd = "</td>";
			this.output.write(head.getBytes()); // 写入头部
			ZipFile xlsxFile = new ZipFile(new File(this.nameStr)); // 按照读取zip格式读取
			ZipEntry sharedStringXML = xlsxFile.getEntry("word/document.xml"); // 找到里面存放内容的文件
			InputStream inputStream = xlsxFile.getInputStream(sharedStringXML); // 将得到文件流
			XmlPullParser xmlParser = Xml.newPullParser();   // 实例化pull
			xmlParser.setInput(inputStream, "utf-8");           // 将流放进pull中

			int evtType = xmlParser.getEventType();         // 得到标签类型的状态

			/******** 布尔类型值，判断状态 ***********/
			boolean isTable = false; // 是表格 用来统计 列 行 数
			boolean isSize = false; // 大小状态
			boolean isColor = false; // 颜色状态
			boolean isCenter = false; // 居中状态
			boolean isRight = false; // 居右状态
			boolean isItalic = false; // 是斜体
			boolean isUnderline = false; // 是下划线
			boolean isBold = false; // 加粗
			boolean isR = false; // 在那个r中

			int pictureIndex = 1; // docx 压缩包中的图片名 iamge1 开始 所以索引从1开始
			while (evtType != XmlPullParser.END_DOCUMENT) {    // 循环读取流
				switch (evtType) {

				// 开始标签
				case XmlPullParser.START_TAG:    // 判断标签开始读取
					String tag = xmlParser.getName();   // 得到标签
					System.out.println(tag);

					if (tag.equalsIgnoreCase("r")) {
						isR = true;
					}
					if (tag.equalsIgnoreCase("u")) { // 判断下划线
						isUnderline = true;
					}
					if (tag.equalsIgnoreCase("jc")) { // 判断对齐方式
						String align = xmlParser.getAttributeValue(0);
						if (align.equals("center")) {
							this.output.write("<center>".getBytes());
							isCenter = true;
						}
						if (align.equals("right")) {
							this.output.write("<div align=\"right\">"
									.getBytes());
							isRight = true;
						}
					}
					if (tag.equalsIgnoreCase("color")) { // 判断颜色
						String color = xmlParser.getAttributeValue(0);
						this.output.write(("<font color=" + color + ">")
								.getBytes());
						isColor = true;
					}
					if (tag.equalsIgnoreCase("sz")) { // 判断大小
						if (isR == true) {
							double size = decideSize(Integer.valueOf(xmlParser
									.getAttributeValue(0))) - 0.8;
							//int size = Integer.valueOf(xmlParser.getAttributeValue(0));
							this.output.write(("<font size=" + size + ">")
									.getBytes());
							isSize = true;
						}
					}
					/******** 处理表格数据 ********/
					if (tag.equalsIgnoreCase("tbl")) { // 检测到tbl 表格开始
						this.output.write(tableBegin.getBytes());
						isTable = true;
					}
					if (tag.equalsIgnoreCase("tr")) { // 行
						this.output.write(rowBegin.getBytes());
					}
					if (tag.equalsIgnoreCase("tc")) { // 列
						this.output.write(colBegin.getBytes());
					}

					if (tag.equalsIgnoreCase("pict")) { // 检测到标签 pict 图片
						// ZipEntry sharePicture =
						// xlsxFile.getEntry("word/media/image" + pictureIndex +
						// ".jpeg");//一下为读取docx的图片 转化为流数组
						ZipEntry sharePicture = xlsxFile
								.getEntry("word/media/image" + pictureIndex
										+ ".jpg");// 一下为读取docx的图片 转化为流数组
						InputStream pictIS = xlsxFile
								.getInputStream(sharePicture);
						ByteArrayOutputStream pOut = new ByteArrayOutputStream();
						byte[] bt = null;
						byte[] b = new byte[1000];
						int len = 0;
						while ((len = pictIS.read(b)) != -1) {
							pOut.write(b, 0, len);
						}
						pictIS.close();
						pOut.close();
						bt = pOut.toByteArray();
						Log.i("byteArray", "" + bt);
						if (pictIS != null)
							pictIS.close();
						if (pOut != null)
							pOut.close();
						writeDOCXPicture(bt);

						pictureIndex++; // 转换一张后 索引+1
					}

					if (tag.equalsIgnoreCase("b")) { // 检测到加粗标签
						isBold = true;
					}
					if (tag.equalsIgnoreCase("p")) { // 检测到 p 标签
						if (isTable == false) { // 如果在表格中 就无视
							this.output.write(tagBegin.getBytes());
						}
					}
					if (tag.equalsIgnoreCase("i")) { // 斜体
						isItalic = true;
					}
					// 检测到值 标签
					if (tag.equalsIgnoreCase("t")) {
						if (isBold == true) { // 加粗
							this.output.write("<b>".getBytes());
						}
						if (isUnderline == true) { // 检测到下划线标签,输入<u>
							this.output.write("<u>".getBytes());
						}
						if (isItalic == true) { // 检测到斜体标签,输入<i>
							output.write("<i>".getBytes());
						}
						river = xmlParser.nextText();
						this.output.write(river.getBytes()); // 写入数值
						if (isItalic == true) { // 检测到斜体标签,在输入值之后,输入</i>,并且斜体状态=false
							this.output.write("</i>".getBytes());
							isItalic = false;
						}
						if (isUnderline == true) { // 检测到下划线标签,在输入值之后,输入</u>,并且下划线状态=false
							this.output.write("</u>".getBytes());
							isUnderline = false;
						}
						if (isBold == true) { // 加粗
							this.output.write("</b>".getBytes());
							isBold = false;
						}
						if (isSize == true) { // 检测到大小设置,输入结束标签
							this.output.write("</font>".getBytes());
							isSize = false;
						}
						if (isColor == true) { // 检测到颜色设置存在,输入结束标签
							this.output.write("</font>".getBytes());
							isColor = false;
						}
						if (isCenter == true) { // 检测到居中,输入结束标签
							this.output.write("</center>".getBytes());
							isCenter = false;
						}
						if (isRight == true) { // 居右不能使用<right></right>,使用div可能会有状况,先用着
							this.output.write("</div>".getBytes());
							isRight = false;
						}
					}
					break;

				case XmlPullParser.END_TAG: // 结束标签
					String tag2 = xmlParser.getName();
					if (tag2.equalsIgnoreCase("tbl")) { // 检测到表格结束,更改表格状态
						this.output.write(tableEnd.getBytes());
						isTable = false;
					}
					if (tag2.equalsIgnoreCase("tr")) { // 行结束
						this.output.write(rowEnd.getBytes());
					}
					if (tag2.equalsIgnoreCase("tc")) { // 列结束
						this.output.write(colEnd.getBytes());
					}
					if (tag2.equalsIgnoreCase("p")) { // p结束,如果在表格中就无视
						if (isTable == false) {
							this.output.write(tagEnd.getBytes());
						}
					}
					if (tag2.equalsIgnoreCase("r")) {
						isR = false;
					}
					break;
				default:
					break;
				}
				evtType = xmlParser.next();   // 读取下一个标签
			}
			this.output.write(end.getBytes());
		} catch (ZipException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		if (river == null) {
			river = "解析文件出现问题";
		}
	}

	public void writeDOCXPicture(byte[] pictureBytes) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0,
				pictureBytes.length);
		makePictureFile();
		this.presentPicture++;
		File myPicture = new File(this.picturePath);
		try {
			FileOutputStream outputPicture = new FileOutputStream(myPicture);
			outputPicture.write(pictureBytes);
			outputPicture.close();
		} catch (Exception e) {
			System.out.println("outputPicture Exception");
		}
		String imageString = "<img src=\"" + this.picturePath + "\"";
		if (bitmap.getWidth() > this.screenWidth) {
			imageString = imageString + " " + "width=\"" + this.screenWidth
					+ "\"";
		}
		imageString = imageString + ">";
		try {
			this.output.write(imageString.getBytes());
		} catch (Exception e) {
			System.out.println("output Exception");
		}
	}

	/************************************************************************/
	/*****
	 * 用来在sdcard上创建图片 /
	 ************************************************************************/
	public void makePictureFile() {
		String sdString = android.os.Environment.getExternalStorageState(); // 获取外部存储状态
		if (sdString.equals(android.os.Environment.MEDIA_MOUNTED)) { // 判断SD卡是否存在
			try {
				File picFile = android.os.Environment
						.getExternalStorageDirectory(); // 获取sd卡目录
				String picPath = picFile.getAbsolutePath() + File.separator
						+ "One"; // 创建目录
				File picDirFile = new File(picPath);
				if (!picDirFile.exists()) {
					picDirFile.mkdir();
				}
				File pictureFile = new File(picPath + File.separator
						+ presentPicture + ".jpg"); // 创建jpg文件,方法与html相同
				if (!pictureFile.exists()) {
					pictureFile.createNewFile();
				}
				this.picturePath = pictureFile.getAbsolutePath(); // 获取jpg文件绝对路径
			} catch (Exception e) {
				System.out.println("PictureFile Catch Exception");
			}
		}
	}

	/************************************************************************/
	/*****
	 * 处理word和html字体大小的转换 /
	 ************************************************************************/
	public int decideSize(int size) {

		if (size >= 1 && size <= 12) {
			return 1;
		}
		if (size >= 13 && size <= 21) {
			return 2;
		}
		if (size >= 22 && size <= 30) {
			return 3;
		}
		if (size >= 31 && size <= 38) {
			return 4;
		}
		if (size >= 39 && size <= 46) {
			return 5;
		}
		if (size >= 47 && size <= 52) {
			return 6;
		}
		if (size >= 53) {
			return 7;
		}
		return 2;
	}

	/************************************************************************/
	/*****
	 * 处理word和html颜色的转换 /
	 ************************************************************************/
	private String decideColor(int a) {
		int color = a;
		switch (color) {
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

}
