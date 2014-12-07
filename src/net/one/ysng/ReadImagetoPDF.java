package net.one.ysng;

import java.io.File;

import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.util.DisplayMetrics;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import net.ysng.reader.ReadConstant;

public class ReadImagetoPDF {

	private ReadConstant myConstant ;
	private 	DisplayMetrics dm;
	private String fileName;

	public ReadImagetoPDF(	DisplayMetrics dm,String fileName){
		this.dm = dm;
		this.fileName = fileName;
	}

	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so now it can be smoked
		return dir.delete();
	}

	public boolean ImagetoPDF() {
		File mDirectory = new File(this.myConstant.SHOTFOLDER );
		File[] myImg = null;

		myImg = mDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				// TODO Auto-generated method stub
				if (name.toLowerCase().endsWith(".png")||name.toLowerCase().endsWith(".jpeg"))
					return true;
				return false;
			}
		});

		List<String> filename = new ArrayList<String>();

		for (File f : myImg) {
			filename.add(f.getName());
		}

		Document document = new Document(PageSize.A4);
		//DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		//getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 窗口的宽度
		int screenWidth = this.dm.widthPixels;
		// 窗口高度
		int screenHeight = this.dm.heightPixels;
		try {

			// step 2:
			// we create a writer that listens to the document
			// and directs a PDF-stream to file
			File pdfFile = new File(this.myConstant.PDFFOLDER);
			if(!pdfFile.exists())                          //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
			{
				pdfFile.mkdir();                              //创建此抽象路径名指定的目录。
			}  			
			PdfWriter.getInstance(document, new FileOutputStream(this.myConstant.PDFFOLDER + File.separator + this.fileName+".pdf"));
			
			// writer.setImagepath("../../images/kerstmis/");

			// step 3: we open the document
			document.open();

			// step 4: we add content
			for (int i = 0; i < myImg.length; i++) {
				Image jpg = Image.getInstance(myImg[i].getAbsolutePath());
				float imgWidth = jpg.getWidth();
				float imgHeight = jpg.getHeight();
				
				float newWidth = (float) (screenWidth * 0.9);
				float ratio=newWidth/imgWidth;;
				float newHeight = ratio * imgHeight;
				jpg.scaleAbsolute(newWidth, newHeight);
				jpg.scaleToFit(screenWidth, screenHeight - 72);
				jpg.setAlignment(Image.ALIGN_MIDDLE);
				document.add(jpg);
			}

			document.close();
			deleteDir(mDirectory);
			return true;

		} catch (DocumentException de) {
			return false;
		} catch (IOException ioe) {
			//Log.e("ioe", ioe.getMessage());
			return false;
		}

		// step 5: we close the document
		

	}
}
