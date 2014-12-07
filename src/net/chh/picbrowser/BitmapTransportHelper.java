package net.chh.picbrowser;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images;

public class BitmapTransportHelper {
	public static final int COMMAND_GET_PICTURE = 1;
	public static final int COMMAND_DEL_PICTURE = 2;
	public static final int COMMAND_NONE = 0;

	private static BitmapTransportHelper instance;
	private final MiniThumbFile mMiniThumbFile;
	private byte[] sMiniThumbData = null;
	private Uri EXT_URI = Images.Media.EXTERNAL_CONTENT_URI;

	private BitmapTransportHelper() {
		EXT_URI = Images.Media.EXTERNAL_CONTENT_URI;
		mMiniThumbFile = new MiniThumbFile(EXT_URI);
		sMiniThumbData = new byte[MiniThumbFile.BYTES_PER_MINTHUMB];
	}

	public static BitmapTransportHelper getInstance() {
		if (instance == null) {
			instance = new BitmapTransportHelper();
		}
		return instance;
	}

	public Bitmap decodeBytes(byte[] buff) {
		return BitmapFactory.decodeByteArray(buff, 0, buff.length);
	}

	public byte[] compressBitmap(Bitmap pic) {
		int buffLen = pic.getWidth() * pic.getHeight();
		byte[] transbp = new byte[buffLen];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pic.compress(Bitmap.CompressFormat.PNG, 100, baos);
		transbp = baos.toByteArray();
		return transbp;
	}

	public Bitmap drawBitMap(long magic, long id, Context context) {
		return mMiniThumbFile.getThumbnail(context.getContentResolver(), id,
				Images.Thumbnails.MICRO_KIND, magic, sMiniThumbData);
	}

	public Bitmap getBitmapFromBundle() {

		return null;
	}

}
