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
	private int screenWidth; // ��Ļ���
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
	 * ����YiDu�ļ��в��ڴ�Ŀ¼�´���uit.html�ļ����洢 /***** ת�����Word�ļ� /
	 ************************************************************************/
	public void makeFile() {
		String sdStateString = android.os.Environment.getExternalStorageState(); // ��ȡ�ⲿ�洢״̬
		if (sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) { // �ж�SD���Ƿ����
			try {
				File wOneFile = new File(this.myConstant.ONE);
				if (!wOneFile.exists()) // ���Դ˳���·������ʾ���ļ���Ŀ¼�Ƿ���ڡ�����ʱ����true
				{
					wOneFile.mkdir(); // �����˳���·����ָ����Ŀ¼��
				}
				File dirFile = new File(this.myConstant.ONEFOLDER); // ��ȡYiDu�ļ��е�ַ
				if (!dirFile.exists()) { // ���������
					dirFile.mkdir(); // ����Ŀ¼
				}
				File myFile = new File(this.myConstant.ONEHTML); // ��ȡmy.html�ĵ�ַ
				if (!myFile.exists()) { // ���������
					myFile.createNewFile(); // �����ļ�
				}
				this.htmlPath = myFile.getAbsolutePath(); // ����·��
			} catch (Exception e) {
			}
		}
	}

	/************************************************************************/
	/*****
	 * ��ȡ.docx�е�����д��sdcard�ϵ�.html�ļ��� /
	 ************************************************************************/
	public void readDOCX() {
		String river = "";
		try {
			this.myFile = new File(this.htmlPath); // newһ��File,·��Ϊhtml�ļ�
			this.output = new FileOutputStream(this.myFile); // newһ����,Ŀ��Ϊhtml�ļ�
			String head = "<!DOCTYPE><html><meta charset=\"utf-8\"><body>"; // ����ͷ�ļ�,�����������utf-8,��Ȼ���������
			String end = "</body></html>";
			String tagBegin = "<p>"; // ���俪ʼ,��ǿ�ʼ?
			String tagEnd = "</p>"; // �������
			String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\">";
			String tableEnd = "</table>";
			String rowBegin = "<tr>";
			String rowEnd = "</tr>";
			String colBegin = "<td>";
			String colEnd = "</td>";
			this.output.write(head.getBytes()); // д��ͷ��
			ZipFile xlsxFile = new ZipFile(new File(this.nameStr)); // ���ն�ȡzip��ʽ��ȡ
			ZipEntry sharedStringXML = xlsxFile.getEntry("word/document.xml"); // �ҵ����������ݵ��ļ�
			InputStream inputStream = xlsxFile.getInputStream(sharedStringXML); // ���õ��ļ���
			XmlPullParser xmlParser = Xml.newPullParser();   // ʵ����pull
			xmlParser.setInput(inputStream, "utf-8");           // �����Ž�pull��

			int evtType = xmlParser.getEventType();         // �õ���ǩ���͵�״̬

			/******** ��������ֵ���ж�״̬ ***********/
			boolean isTable = false; // �Ǳ�� ����ͳ�� �� �� ��
			boolean isSize = false; // ��С״̬
			boolean isColor = false; // ��ɫ״̬
			boolean isCenter = false; // ����״̬
			boolean isRight = false; // ����״̬
			boolean isItalic = false; // ��б��
			boolean isUnderline = false; // ���»���
			boolean isBold = false; // �Ӵ�
			boolean isR = false; // ���Ǹ�r��

			int pictureIndex = 1; // docx ѹ�����е�ͼƬ�� iamge1 ��ʼ ����������1��ʼ
			while (evtType != XmlPullParser.END_DOCUMENT) {    // ѭ����ȡ��
				switch (evtType) {

				// ��ʼ��ǩ
				case XmlPullParser.START_TAG:    // �жϱ�ǩ��ʼ��ȡ
					String tag = xmlParser.getName();   // �õ���ǩ
					System.out.println(tag);

					if (tag.equalsIgnoreCase("r")) {
						isR = true;
					}
					if (tag.equalsIgnoreCase("u")) { // �ж��»���
						isUnderline = true;
					}
					if (tag.equalsIgnoreCase("jc")) { // �ж϶��뷽ʽ
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
					if (tag.equalsIgnoreCase("color")) { // �ж���ɫ
						String color = xmlParser.getAttributeValue(0);
						this.output.write(("<font color=" + color + ">")
								.getBytes());
						isColor = true;
					}
					if (tag.equalsIgnoreCase("sz")) { // �жϴ�С
						if (isR == true) {
							double size = decideSize(Integer.valueOf(xmlParser
									.getAttributeValue(0))) - 0.8;
							//int size = Integer.valueOf(xmlParser.getAttributeValue(0));
							this.output.write(("<font size=" + size + ">")
									.getBytes());
							isSize = true;
						}
					}
					/******** ���������� ********/
					if (tag.equalsIgnoreCase("tbl")) { // ��⵽tbl ���ʼ
						this.output.write(tableBegin.getBytes());
						isTable = true;
					}
					if (tag.equalsIgnoreCase("tr")) { // ��
						this.output.write(rowBegin.getBytes());
					}
					if (tag.equalsIgnoreCase("tc")) { // ��
						this.output.write(colBegin.getBytes());
					}

					if (tag.equalsIgnoreCase("pict")) { // ��⵽��ǩ pict ͼƬ
						// ZipEntry sharePicture =
						// xlsxFile.getEntry("word/media/image" + pictureIndex +
						// ".jpeg");//һ��Ϊ��ȡdocx��ͼƬ ת��Ϊ������
						ZipEntry sharePicture = xlsxFile
								.getEntry("word/media/image" + pictureIndex
										+ ".jpg");// һ��Ϊ��ȡdocx��ͼƬ ת��Ϊ������
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

						pictureIndex++; // ת��һ�ź� ����+1
					}

					if (tag.equalsIgnoreCase("b")) { // ��⵽�Ӵֱ�ǩ
						isBold = true;
					}
					if (tag.equalsIgnoreCase("p")) { // ��⵽ p ��ǩ
						if (isTable == false) { // ����ڱ���� ������
							this.output.write(tagBegin.getBytes());
						}
					}
					if (tag.equalsIgnoreCase("i")) { // б��
						isItalic = true;
					}
					// ��⵽ֵ ��ǩ
					if (tag.equalsIgnoreCase("t")) {
						if (isBold == true) { // �Ӵ�
							this.output.write("<b>".getBytes());
						}
						if (isUnderline == true) { // ��⵽�»��߱�ǩ,����<u>
							this.output.write("<u>".getBytes());
						}
						if (isItalic == true) { // ��⵽б���ǩ,����<i>
							output.write("<i>".getBytes());
						}
						river = xmlParser.nextText();
						this.output.write(river.getBytes()); // д����ֵ
						if (isItalic == true) { // ��⵽б���ǩ,������ֵ֮��,����</i>,����б��״̬=false
							this.output.write("</i>".getBytes());
							isItalic = false;
						}
						if (isUnderline == true) { // ��⵽�»��߱�ǩ,������ֵ֮��,����</u>,�����»���״̬=false
							this.output.write("</u>".getBytes());
							isUnderline = false;
						}
						if (isBold == true) { // �Ӵ�
							this.output.write("</b>".getBytes());
							isBold = false;
						}
						if (isSize == true) { // ��⵽��С����,���������ǩ
							this.output.write("</font>".getBytes());
							isSize = false;
						}
						if (isColor == true) { // ��⵽��ɫ���ô���,���������ǩ
							this.output.write("</font>".getBytes());
							isColor = false;
						}
						if (isCenter == true) { // ��⵽����,���������ǩ
							this.output.write("</center>".getBytes());
							isCenter = false;
						}
						if (isRight == true) { // ���Ҳ���ʹ��<right></right>,ʹ��div���ܻ���״��,������
							this.output.write("</div>".getBytes());
							isRight = false;
						}
					}
					break;

				case XmlPullParser.END_TAG: // ������ǩ
					String tag2 = xmlParser.getName();
					if (tag2.equalsIgnoreCase("tbl")) { // ��⵽������,���ı��״̬
						this.output.write(tableEnd.getBytes());
						isTable = false;
					}
					if (tag2.equalsIgnoreCase("tr")) { // �н���
						this.output.write(rowEnd.getBytes());
					}
					if (tag2.equalsIgnoreCase("tc")) { // �н���
						this.output.write(colEnd.getBytes());
					}
					if (tag2.equalsIgnoreCase("p")) { // p����,����ڱ���о�����
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
				evtType = xmlParser.next();   // ��ȡ��һ����ǩ
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
			river = "�����ļ���������";
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
	 * ������sdcard�ϴ���ͼƬ /
	 ************************************************************************/
	public void makePictureFile() {
		String sdString = android.os.Environment.getExternalStorageState(); // ��ȡ�ⲿ�洢״̬
		if (sdString.equals(android.os.Environment.MEDIA_MOUNTED)) { // �ж�SD���Ƿ����
			try {
				File picFile = android.os.Environment
						.getExternalStorageDirectory(); // ��ȡsd��Ŀ¼
				String picPath = picFile.getAbsolutePath() + File.separator
						+ "One"; // ����Ŀ¼
				File picDirFile = new File(picPath);
				if (!picDirFile.exists()) {
					picDirFile.mkdir();
				}
				File pictureFile = new File(picPath + File.separator
						+ presentPicture + ".jpg"); // ����jpg�ļ�,������html��ͬ
				if (!pictureFile.exists()) {
					pictureFile.createNewFile();
				}
				this.picturePath = pictureFile.getAbsolutePath(); // ��ȡjpg�ļ�����·��
			} catch (Exception e) {
				System.out.println("PictureFile Catch Exception");
			}
		}
	}

	/************************************************************************/
	/*****
	 * ����word��html�����С��ת�� /
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
	 * ����word��html��ɫ��ת�� /
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
