package com.back.screenshot.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.back.screenshot.MyApp;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class Utils {
	private Context context;

	public Utils(Context context) {
		this.context = context;
	}

	public static Bitmap getBitmapByView(Context context, ScrollView scrollView) {

		int h = 0;
		Bitmap bitmap;

		for (int i = 0; i < scrollView.getChildCount(); i++) {
			h += scrollView.getChildAt(i).getHeight();
			scrollView.getChildAt(i).setBackgroundColor(0xfffffff);
		}
		Log.i(scrollView.getWidth() + "scr", h + "");

		bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
				Bitmap.Config.RGB_565);
		final Canvas canvas = new Canvas(bitmap);
		scrollView.draw(canvas);

		return bitmap;
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		int options = 100;
		while (bos.toByteArray().length / 1024 > 1000) {
			bos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, bos);
			options -= 10;
		}
		ByteArrayInputStream ios = new ByteArrayInputStream(bos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(ios, null, null);
		return bitmap;
	}

	/**
	 * 保存图像文件至SD卡
	 */
	public static String saveToSdCard(Bitmap bitmap) {
		String files = "/sdcard/" + "send_pic.jpg";
		File file = new File(files);
		if (bitmap != null) {

			Log.i("执行", "执行");
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		Log.i("执行", file.getAbsolutePath() + "执行");
		return file.getAbsolutePath();

	}

	public static int getWidth(Activity activity) {
		/** DisplayMetrics获取屏幕信息 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;

	}

	public static int getHeight(Activity activity) {
		/** DisplayMetrics获取屏幕信息 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;

	}

}
