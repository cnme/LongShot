package com.back.screenshot;

import java.io.File;
import java.io.IOException;

import com.back.screenshot.R;
import com.back.screenshot.utils.ImageGet;
import com.back.screenshot.utils.Utils;

import android.R.integer;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private ScrollView scrollView;
	private EditText et;
	private TextView tv;
	private LinearLayout open_layout, take_layout, save_layout;
	private int width, height, imgWidth, imgHeight;
	private ContentResolver contentresolver;
	private long currenttime;
	private File file;
	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Init();

	}

	private void Init() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		height = displayMetrics.heightPixels;
		contentresolver = MainActivity.this.getContentResolver();
		scrollView = (ScrollView) findViewById(R.id.scro);
		et = (EditText) findViewById(R.id.et);
		open_layout = (LinearLayout) findViewById(R.id.open_layout);
		take_layout = (LinearLayout) findViewById(R.id.take_layout);
		save_layout = (LinearLayout) findViewById(R.id.save_layout);

		save_layout.setOnClickListener(this);
		open_layout.setOnClickListener(this);
		take_layout.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 插图
		if (resultCode == RESULT_OK && requestCode == 1) {
			Uri uri = data.getData();

			Editable eb = et.getEditableText();
			// 获得光标所在位置
			int startPosition = et.getSelectionStart();
			eb.insert(
					startPosition,
					Html.fromHtml("<br/><img src='" + uri.toString()
							+ "'/><br/>", imageGetter, null));
		}

		if (resultCode == RESULT_OK && requestCode == 2) {

			Editable eb = et.getEditableText();
			// 获得光标所在位置
			int startPosition = et.getSelectionStart();
			eb.insert(
					startPosition,
					Html.fromHtml("<br/><img src='" + uri.toString()
							+ "'/><br/>", imageGetter, null));

		}
	}

	private ImageGetter imageGetter = new ImageGetter() {

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

	private Bitmap getimage(ContentResolver cr, Uri uri) {
		try {
			Bitmap bitmap = null;
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// options.inJustDecodeBounds=true,图片不加载到内存中
			newOpts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					newOpts);

			newOpts.inJustDecodeBounds = false;

			imgWidth = newOpts.outWidth;
			imgHeight = newOpts.outHeight;
			int scale = 1;
			// 缩放比,1表示不缩放 int scale = 1;

			if (newOpts.outWidth > width) {
				scale = (int) (width / imgWidth);
				newOpts.outWidth = width;
				//newOpts.outHeight = newOpts.outHeight * scale;
			}/*
			 * else if (imgHeight > height) { scale = (int) (height /
			 * imgHeight); newOpts.outHeight = newOpts.outHeight * scale; }
			 */

			Toast.makeText(MainActivity.this, imgWidth + "::" + width, 3000)
					.show();
			newOpts.inSampleSize = scale;// 设置缩放比例
			newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
			newOpts.inPurgeable = true;// 同时设置才会有效
			newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
			bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					newOpts);
			return bitmap;
		} catch (Exception e) {
			System.out.println("文件不存在");
			return null;
		}
	}

	private Drawable getMyDrawable(Bitmap bitmap) {
		@SuppressWarnings("deprecation")
		Drawable drawable = new BitmapDrawable(bitmap);

		drawable.setBounds(0, 0, imgWidth, imgHeight);
		return drawable;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.open_layout:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
			break;
		case R.id.take_layout:
			currenttime = System.currentTimeMillis();
			// 初始化图像文件存放路径
			file = new File(Environment.getExternalStorageDirectory(),
					currenttime + ".jpg");
			if (file.exists()) {
				file.delete();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			uri = Uri.fromFile(file);
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(camera, 2);
			break;
		case R.id.save_layout:
			Utils.saveToSdCard(Utils.compressImage(Utils.getBitmapByView(
					MainActivity.this, scrollView)));
			break;
		}

	}

}
