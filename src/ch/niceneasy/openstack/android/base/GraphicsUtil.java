package ch.niceneasy.openstack.android.base;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class GraphicsUtil {

	public static int getCameraPhotoOrientation(Context context, Uri imageUri,
			String imagePath) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imagePath);

			ExifInterface exif = new android.media.ExifInterface(
					imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}

			Log.i("RotateImage", "Exif orientation: " + orientation);
			Log.i("RotateImage", "Rotate value: " + rotate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

	public static int getOrientation(Context context, Uri imageUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return getCameraPhotoOrientation(context, imageUri, filePath);
	}

	public static String getOriginalFilePath(Context context, Uri imageUri) {
		if (imageUri.getScheme().equals("file")) {
			return imageUri.getPath();
		}
		String[] filePathColumn = { MediaStore.Images.Media.DATA,  MediaStore.Files.FileColumns.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		return cursor.getString(columnIndex);
	}	
	
//	public static Bitmap checkOrientation(Context context, Uri imageUri) {
//		InputStream imageStream = context.getContentResolver()
//				.openInputStream(imageUri);
//		final Bitmap selectedImage = BitmapFactory
//				.decodeStream(imageStream);
//
//	        int width = bitmapOrg.getWidth();
//
//	        int height = bitmapOrg.getHeight();
//
//
//	        int newWidth = 200;
//
//	        int newHeight  = 200;
//
//	        // calculate the scale - in this case = 0.4f
//
//	         float scaleWidth = ((float) newWidth) / width;
//
//	         float scaleHeight = ((float) newHeight) / height;
//
//	         Matrix matrix = new Matrix();
//
//	         matrix.postScale(scaleWidth, scaleHeight);
//	         matrix.postRotate(x);
//
//	         Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
//
//	         iv.setScaleType(ScaleType.CENTER);
//	         iv.setImageBitmap(resizedBitmap);
//	    }
//	}

}
