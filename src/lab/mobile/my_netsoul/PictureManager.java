package lab.mobile.my_netsoul;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

// https://www.epitech.eu/intra/photos/leroy_v.jpg
public class PictureManager {
//	
//	private LruCache<String, Bitmap> mDiskCache;
//	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
//	private static final String DISK_CACHE_SUBDIR = "thumbnails";

	private static PictureManager _instance = null;

//	public static File getCacheDir(Context context, String uniqueName) {
//	    // Check if media is mounted or storage is built-in, if so, try and use external cache dir
//	    // otherwise use internal cache dir
//	    final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
//	            || !Environment.isExternalStorageRemovable() ?
//	                    context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
//
//	    return new File(cachePath + File.separator + uniqueName);
//	}
	
//	public void addBitmapToCache(String key, Bitmap bitmap) {
//	    
////	    if (!mDiskCache.containsKey(key)) {
//	        mDiskCache.put(key, bitmap);
////	    }
//	}
//
//	public Bitmap getBitmapFromDiskCache(String key) {
//	    return mDiskCache.get(key);
//	}
	
	private PictureManager() {
//		File cacheDir = getCacheDir(MainActivity.context, DISK_CACHE_SUBDIR);
//	    mDiskCache = DiskLruCache.openCache(this, cacheDir, DISK_CACHE_SIZE);
	}

	public static PictureManager getInstance() {
		if (_instance == null) {
			synchronized (PictureManager.class) {
				if (_instance == null)
					_instance = new PictureManager();
			}
		}
		return _instance;
	}
	
	
	public void updatePhoto(String login) {
		DownloadTask dt = new DownloadTask();
		Log.i("updatePhoto", "Update photo of " + login);
		//dt.execute(login);
		dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, login);
		Log.i("updatePhoto", "dt status: " + dt.getStatus());
	}


//	class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
//		private String lo = null;
//		
//		@Override
//        protected void onPreExecute() {
//            Log.i("DownloadTask", "onPreExecute");
//        }
//		
//		@Override
//        protected void onCancelled() {
//            Log.e("DownloadTask", "Cancelled.");
//        }
//
//		protected Bitmap doInBackground(String... login) {
//			URL url = null;
//			Log.i("DownloadTask", "params: " + login + " " + login.length);
//			if (login.length <= 0)
//				return null;
//			lo = login[0];
//			try {
//				Log.i("DownloadTask", "Try to get Img for: " + login[0]);
//				url = new URL("http://www.epitech.eu/intra/photos/" + login[0] + ".jpg");
//			} catch (MalformedURLException e) {
//				Log.e("DownloadTask", "MalformedURLException");
//				e.printStackTrace();
//			}
//			HttpURLConnection connection;
//			Bitmap img = null;
//			try {
//				connection = (HttpURLConnection) url.openConnection();
//				InputStream is = connection.getInputStream();
//				img = BitmapFactory.decodeStream(is);  
//				onProgressUpdate(1);
//			} catch (IOException e) {
//				Log.e("getImage", "IOException: " + e.getMessage());
//				e.printStackTrace();
//			}
//			//imageView.setImageBitmap(img );
//			return img;
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//			//update progress if you have progress dialog
//		}
//
//		protected void onPostExecute(Bitmap result) {
//			Contact c = ContactManager.getInstance().getContactByLogin(lo);
//			if (c != null && result != null) {
//				c.setPhoto(result);
//				Log.i("DownloadTask", "Photo of " + c.login + " updated with " + result.getWidth() 
//						+ "px by " + result.getHeight() + "px");
//			}
//		}
//	}


}
