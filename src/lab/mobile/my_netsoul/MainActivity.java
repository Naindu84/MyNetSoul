package lab.mobile.my_netsoul;

import java.util.logging.Logger;

import lab.mobile.my_netsoul.AddContactFragment.NoticeDialogListener;
import lab.mobile.my_netsoul.NetworkService.NetworkBinder;
import lab.mobile.my_netsoul.DownloadTask;

import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener, NoticeDialogListener, OnItemClickListener, OnItemLongClickListener {
	ListView lv = null;
	static MainActivity context;
	public ContactAdapter adapter;
	ArrayAdapter<CharSequence> states_adapter;
//	Menu menu = null;
	NetworkService mBoundService;
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((NetworkService.NetworkBinder)service).getService();

	        // Tell the user about this for our demo.
	        Toast.makeText(MainActivity.this, R.string.service_connected,
	                Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	        Toast.makeText(MainActivity.this, R.string.service_disconnected,
	                Toast.LENGTH_SHORT).show();
	    }
	};
	private boolean mIsBound;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.list);
        context = this;
        adapter = new ContactAdapter(getApplicationContext());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        
        ActionBar actionBar = getActionBar();
//        View mActionBarView = getLayoutInflater().inflate(R.layout.menu, null);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		 
		
//        actionBar.setCustomView(mActionBarView);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
//        Spinner spinner = (Spinner) mActionBarView.findViewById(R.id.list_state);
        
        states_adapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, R.layout.spinner_item);
        states_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				changeState(states_adapter.getItem(itemPosition).toString());
				Toast.makeText(getBaseContext(), "You selected : " + states_adapter.getItem(itemPosition)  , Toast.LENGTH_SHORT).show();
				return false;
			}
		};
		getActionBar().setListNavigationCallbacks(states_adapter, navigationListener);  
//        spinner.setAdapter(adapter);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
//        TextView login =  (TextView) findViewById(R.id.login);
//        if (login != null)
//        	login.setText(sharedPref.getString("pref_credentials_login", "Too bad"));
        startNetworkService();
        doBindService();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        stopNetworkService();
        //doUnbindService();
    }
    
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this, 
                NetworkService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    
    private void startNetworkService() {
    	Intent serviceIntent = new Intent(this, NetworkService.class);
        startService(serviceIntent);
    }
    
    private void stopNetworkService() {
    	Intent serviceIntent = new Intent(this, NetworkService.class);
        stopService(serviceIntent);
    }
    
    private void addContact() {
    	DialogFragment newFragment = AddContactFragment.newInstance(this);
        newFragment.show(getFragmentManager(), "Ajouter un contact");
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case R.id.menu_settings:
	        	Intent intent = new Intent(this, SettingsActivity.class);
	        	startActivity(intent);
	            return true;
	        case R.id.add:
	        	addContact();
	        	return true;
	        default:
            	return false;
	    }
    }

    private void changeState(String string) {
		Log.i("State", "changing my state with " + string);
		//MenuItem menu = (Me) findViewById(R.id.menu_state);
//		if (menu != null) {
//			MenuItem mi = menu.getItem(0);
//			if (mi != null)
//				mi.setTitle(string);
//		}
//		menu.setTitle(string);
		int position = states_adapter.getPosition(string);
		getActionBar().setSelectedNavigationItem(position);
		if (!string.equals("Disconnected") && mBoundService != null && mBoundService.commands != null)
			mBoundService.commands.changeStatus(string);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
//        this.menu = menu;
        return true;
    }
    
    

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Logger.getLogger("changed").info("changed for: " + key);
		if (key.equals("pref_credentials_login")) {
//			TextView login =  (TextView) findViewById(R.id.login);
//	        if (login != null)
//	        	login.setText(sharedPreferences.getString("pref_credentials_login", "Too bad"));
//	        stopNetworkService();
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.i("Add", "Res: " + ((AddContactFragment)dialog).getLogin());
		ContactManager.getInstance().addContact(((AddContactFragment)dialog).getLogin());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Contact c = ContactManager.getInstance().getContactByPosition(position);
		
		Log.d("onclick", "pos: " + position + ", id: " + id + ", objet: " + (c != null ? c.login : "null"));
		if (mBoundService != null) {
//			mBoundService.commands.sendMsg(c.login, "Ceci est un test");
//			mBoundService.commands.sendMsg(c.login, "Tutu");
		}
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		ChatFragment cf = new ChatFragment();
//		ft.replace(R.id.list, cf);
////		ft.add(R.id.messageHistoryList, cf);
//		ft.commit();
		PictureManager.getInstance().updatePhoto(c.login);
//		DownloadTask dt = new DownloadTask();
//		Log.i("updatePhoto", "Update photo of " + c.login);
//		dt.execute(c.login);
		
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("login", c.login);
		intent.putExtra("contact", position);
        startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
		DialogFragment newFragment = DeleteContactFragment.newInstance(this, position, adapter);
        newFragment.show(getFragmentManager(), "Supprimer un contact");
		return false;
	}
}
