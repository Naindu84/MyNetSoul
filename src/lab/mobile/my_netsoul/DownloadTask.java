package lab.mobile.my_netsoul;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
	private String lo = null;
	
	@Override
    protected void onPreExecute() {
        Log.i("DownloadTask", "onPreExecute");
    }
	
	@Override
    protected void onCancelled() {
        Log.e("DownloadTask", "Cancelled.");
    }

	protected Bitmap doInBackground(String... login) {
		URL url = null;
		Log.i("DownloadTask", "params: " + login + " " + login.length);
		if (login.length <= 0)
			return null;
		lo = login[0];
		try {
			Log.i("DownloadTask", "Try to get Img for: " + login[0]);
			url = new URL("http://www.epitech.eu/intra/photos/" + login[0] + ".jpg");
		} catch (MalformedURLException e) {
			Log.e("DownloadTask", "MalformedURLException");
			e.printStackTrace();
		}
		HttpURLConnection connection;
		Bitmap img = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			InputStream is = connection.getInputStream();
			img = BitmapFactory.decodeStream(is);  
			onProgressUpdate(1);
		} catch (IOException e) {
			Log.e("getImage", "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		//imageView.setImageBitmap(img );
		return img;
	}

	protected void onProgressUpdate(Integer... progress) {
		//update progress if you have progress dialog
	}

	protected void onPostExecute(Bitmap result) {
		Contact c = ContactManager.getInstance().getContactByLogin(lo);
		if (c != null && result != null) {
			c.setPhoto(result);
			if (MainActivity.context != null && MainActivity.context.adapter != null)
				MainActivity.context.adapter.notifyDataSetChanged();
			Log.i("DownloadTask", "Photo of " + c.login + " updated with " + result.getWidth() 
					+ "px by " + result.getHeight() + "px");
		}
	}
}