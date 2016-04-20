package com.jhone.demo.barcode;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * @类功能说明: 生成二维码图片
 */
public class CreateQRImage
{
	/**
	 * @方法功能说明: 生成二维码图片
	 *
	 * @参数: @param url 要转换的地址或字符串,可以是中文
	 * @return void
	 * @throws
	 */

	//要转换的地址或字符串,可以是中文
	public static void createQRImage(String url, ImageView sweepIV, int size)
	{
		try
		{
			//判断URL合法性
			if (TextUtils.isEmpty(url))
			{
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, size, size, hints);
			int[] pixels = new int[size * size];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < size; y++)
			{
				for (int x = 0; x < size; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * size + x] = 0xff000000;
					}
					else
					{
						pixels[y * size + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
			//显示到一个ImageView上面
			sweepIV.setImageBitmap(bitmap);
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
	}
}
