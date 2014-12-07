package net.chh.picbrowser;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;

public class MiniThumbFile {
	public static final int BYTES_PER_MINTHUMB = 10000;

	public MiniThumbFile() {
	}

	public MiniThumbFile(Uri uri) {
	}

	public Bitmap getThumbnail(ContentResolver cr, long origId, int kind,
			long thumbMagic, byte[] sMiniThumbData) {
		return Images.Thumbnails.getThumbnail(cr, origId, kind, null);
	}

	public Bitmap getThumbBitmapForEdit(ContentResolver cr, long origId) {
		return Images.Thumbnails.getThumbnail(cr, origId, Thumbnails.MINI_KIND,
				null);
	}
}
