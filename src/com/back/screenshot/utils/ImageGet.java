package com.back.screenshot.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html.ImageGetter;

public class ImageGet {
	private static ContentResolver contentresolver;
	private static Activity activity;

	public ImageGet(ContentResolver contentResolver, Activity activity) {
		this.contentresolver = contentResolver;
		this.activity = activity;
	};

	private static ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			try {
				Uri uri = Uri.parse(source);
				Bitmap bitmap = getimage(contentresolver, uri);
				return getMyDrawable(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	};

	private static Bitmap getimage(ContentResolver cr, Uri uri) {
		try {
			Bitmap bitmap = null;
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// options.inJustDecodeBounds=true,图片不加载到内存中
			newOpts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(cr.openInputStream(uri), null, newOpts);

			newOpts.inJustDecodeBounds = false;
			int imgWidth = newOpts.outWidth;
			int imgHeight = newOpts.outHeight;
			// 缩放比,1表示不缩放
			int scale = 1;

			if (imgWidth > imgHeight && imgWidth > Utils.getWidth(activity)) {
				scale = (int) (imgWidth / Utils.getWidth(activity));
			} else if (imgHeight > imgWidth
					&& imgHeight > Utils.getHeight(activity)) {
				scale = (int) (imgHeight / Utils.getHeight(activity));
			}
			newOpts.inSampleSize = scale;// 设置缩放比例
			bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					newOpts);
			return bitmap;
		} catch (Exception e) {
			System.out.println("文件不存在");
			return null;
		}
	}

	private static Drawable getMyDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);

		int imgHeight = drawable.getIntrinsicHeight();
		int imgWidth = drawable.getIntrinsicWidth();

		drawable.setBounds(0, 0, imgWidth, imgHeight);
		return drawable;
	}

}
