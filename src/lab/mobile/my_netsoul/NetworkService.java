package lab.mobile.my_netsoul;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends Service  {
	private final static String pref_IP = "pref_network_ip";
	private final static String pref_Port = "pref_network_port";
	
	NetworkTask networktask;
	Commands commands;
	
	public class NetworkBinder extends Binder {
		NetworkService getService() {
            return NetworkService.this;
        }
    }

	@Override
    public void onCreate() {
		
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        networktask = new NetworkTask(); // HERE 
        //networktask.execute(); // AND HERE
        networktask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        commands = new Commands(this);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	networktask.SendDataToNetwork("exit");
    	networktask.cancel(true);
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new NetworkBinder();
	public boolean connected = false;

    
    public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
    	private Socket socket;
    	InputStream is;
        OutputStream os;
        Dispatcher dispatch;
        String data = new String();
        
        @Override
        protected void onPreExecute() {
            Log.d("AsyncTask", "onPreExecute");
        }
        
		@Override
		protected Boolean doInBackground(Void... params) {
			dispatch = new Dispatcher(this, NetworkService.this);
			boolean result = false;
            try {
                Log.d("AsyncTask", "doInBackground: Creating socket");
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                
                SocketAddress sockaddr = new InetSocketAddress(sharedPref.getString(pref_IP, "127.0.0.1"), Integer.parseInt(sharedPref.getString(pref_Port, "4242")));
                //SocketAddress sockaddr = new InetSocketAddress("163.5.255.5", Integer.parseInt(sharedPref.getString(pref_Port, "4242")));
                socket = new Socket();
                socket.connect(sockaddr, 1500);
                
                if (socket.isConnected()) { 
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    Log.d("AsyncTask", "doInBackground: Socket created, streams assigned");
                    Log.d("AsyncTask", "doInBackground: Waiting for inital data...");
//                    byte[] buffer = new byte[4096];
//                    int read = is.read(buffer, 0, 4096);
//                    while(read != -1) {
//                        byte[] tempdata = new byte[read];
//                        System.arraycopy(buffer, 0, tempdata, 0, read);
//                        publishProgress(tempdata);
//                        
//                        read = is.read(buffer, 0, 4096);
//                    }
                    byte[] buffer = new byte[4096];
                    int read = 1;
                    String b = new String();
                    while(read != -1) {
                    	read = is.read(buffer, 0, 4096);
                    	if (read > 0) {
	                        byte[] tempdata = new byte[read];
	                        System.arraycopy(buffer, 0, tempdata, 0, read);
	                        publishProgress(tempdata);
                    	}
                        
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("AsyncTask", "doInBackground: IOException: "  + e.getMessage());
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("AsyncTask", "doInBackground: Exception: " + e.getMessage());
                result = true;
            } finally {
                try {
                    is.close();
                    os.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("AsyncTask", "doInBackground: Finished");
            }
            return result;
		}
		
		public void SendDataToNetwork(String cmd) {
            try {
                if (socket.isConnected()) {
                	if (!cmd.endsWith("\n"))
                		cmd = cmd.concat("\n");
                    Log.i("AsyncTask", "SendDataToNetwork: Writing " + cmd);
                    os.write(cmd.getBytes());
                    Log.d("AsyncTask", "SendDataToNetwork: Writing OK");
                } else {
                    Log.e("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                }
            } catch (Exception e) {
                Log.e("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception");
            }
        }
		
		@Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.d("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
                for (byte[] bs : values) {
                	String line = new String(bs);
                	Log.d("AsyncTask", "BS: [" + line + "]");
                	data += line;
				}
                while (data.indexOf('\n') >= 0) {
	                String line = data.substring(0, data.indexOf('\n'));
	                data = data.substring(data.indexOf('\n') + 1);
	                Log.d("AsyncTask", "Line: " + line);
	            	dispatch.doDispatch(line);
                }
            }
        }
        @Override
        protected void onCancelled() {
            Log.e("AsyncTask", "Cancelled.");
//            btnStart.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.e("AsyncTask", "onPostExecute: Completed with an Error.");
//                textStatus.setText("There was a connection error.");
            } else {
                Log.d("AsyncTask", "onPostExecute: Completed.");
            }
//            btnStart.setVisibility(View.VISIBLE);
        }
    	
    }
}
