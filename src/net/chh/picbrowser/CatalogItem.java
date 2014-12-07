package net.chh.picbrowser;

import android.graphics.Bitmap;

public class CatalogItem {
	private String textview;
	private Bitmap bitmap;
	private String imagepath;
	private String id;
	private long magic;
	private long parseId;
	private boolean needLoadImage;

	public CatalogItem() {
		this.needLoadImage = false;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setTextview(String textview) {
		this.textview = textview;
	}

	public Bitmap getBitmap() {
		return this.bitmap;
	}

	public String getTextView() {
		return this.textview;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setMagic(long magic) {
		this.magic = magic;
	}

	public long getMagic() {
		return magic;
	}

	public void setParseId(long parseId) {
		this.parseId = parseId;
	}

	public long getParseId() {
		return parseId;
	}

	public void setNeedLoadImage(boolean needLoadImage) {
		this.needLoadImage = needLoadImage;
	}

	public boolean isNeedLoadImage() {
		return needLoadImage;
	}
}