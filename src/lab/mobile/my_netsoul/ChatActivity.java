package lab.mobile.my_netsoul;

import java.util.LinkedList;

import lab.mobile.my_netsoul.ContactAdapter.ViewHolder;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity implements OnItemClickListener {
	ChatAdapter adapter;
	EditText et;
	ListView lv;
	LinkedList<Message> list;
	Contact c = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.chat);
        
        Bundle extras = getIntent().getExtras();
        String login = extras.getString("login");
        
        c = ContactManager.getInstance().getContactByPosition(extras.getInt("contact", 0));
        
        getActionBar().setTitle("Chat with " + login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setSubtitle("Available");
        // Récuperer l'event home button pour retourner sur la mainview
        
        // Create the list fragment and add it as our sole content.
//        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
//            ArrayListFragment list = new ArrayListFragment();
//            getFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
//        }
        //View view = getLayoutInflater().inflate(R.layout.chat);
        //list = new LinkedList<Message>();
        list = c.messages;
        lv = (ListView)findViewById(R.id.list);
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, Shakespeare.TITLES);
        adapter = new ChatAdapter(this, list);
        c.cAdapter = adapter;
//        runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				PictureManager.getInstance().updatePhoto(c.login);
//			}
//		});
        
        lv.setAdapter(adapter);
//        lv.smoothScrollToPosition(adapter.getCount() - 1);
        lv.setOnItemClickListener(this);
        
        et = (EditText)findViewById(R.id.message);
        et.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					Log.i("Message", "Enter hit: " + et.getText());
					pushMessage(et.getText().toString());
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					et.setText("");
				}
				return false;
			}
		});
        Button bt = (Button)findViewById(R.id.sendMessageButton);
//        bt.setOnTouchListener(new View.OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Log.i("Message", "To send: " + et.getEditableText());
//				return false;
//			}
//		});
        bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("Message", "To send: " + et.getText());
				pushMessage(et.getText().toString());
				et.setText("");
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
//				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		});
    }

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Log.i("ChatList", "Item clicked: " + id);
		
//		lv.smoothScrollToPosition(adapter.getCount() - 1);
		
	}
	
	
	
	private void pushMessage(String str) {
		if (str.length() == 0)
			return ;
		list.addLast(new Message(null, str));
		adapter.notifyDataSetChanged();
		if (MainActivity.context != null && MainActivity.context.mBoundService != null
					&& MainActivity.context.mBoundService.networktask != null)
			MainActivity.context.mBoundService.commands.sendMsg(c.login, str);
		//else
			// dialog not connected
		;
		
	}
	
	public static class ChatAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private LinkedList<Message> l;
		
		public ChatAdapter(Context context, LinkedList<Message> list) {
			mInflater = LayoutInflater.from(context);
			l = list;
		}
		
		@Override
		public int getCount() {
			return l.size();
		}

		@Override
		public Object getItem(int position) {
			return l.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			Message message = l.get(position);
			
	        if (convertView == null) {
	        	if (message != null && message.contact != null)
	        		convertView = mInflater.inflate(R.layout.chat_row_right, null);
	        	else
	        		convertView = mInflater.inflate(R.layout.chat_row, null);
	            holder = new ViewHolder();
	            holder.img = (ImageView) convertView.findViewById(R.id.pict);
	            holder.user = (TextView) convertView.findViewById(R.id.textUser);
	            holder.msg = (TextView) convertView.findViewById(R.id.textMessage);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        if (message == null)
	        	return convertView;
//	        Contact c = (Contact) this.getItem(position);
//	        Log.d("getView", "item: pos: " + position + ", " + c);
	        if (holder != null) {
	        	Log.d("getView", "Setted");
	        	holder.user.setText((message.contact != null ? message.contact.login : "Me") + ":");
	        	holder.msg.setText(message.msg);
	        	if (message.contact != null && message.contact.getPhoto() != null) {
	        		holder.img.setImageBitmap(message.contact.getPhoto());
	        	}
	        }
	        //convertView.postInvalidate();
	        convertView.invalidate();
			return convertView;
		}
		
		static class ViewHolder {
			ImageView img;
	        TextView user;
	        TextView msg;
	    }
	}
	
//	
//	public static class ArrayListFragment extends ListFragment {
//
//        @Override
//        public void onActivityCreated(Bundle savedInstanceState) {
//            super.onActivityCreated(savedInstanceState);
//            setListAdapter(new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_list_item_1, Shakespeare.TITLES));
//        }
//        
////        @Override
////        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        	//Inflate the layout for this fragment
////        	Log.i("ChatActivity", "onCreateView");
////        	return inflater.inflate(R.layout.chat, container, false);
////		}
//
//        @Override
//        public void onListItemClick(ListView l, View v, int position, long id) {
//            Log.i("FragmentList", "Item clicked: " + id);
//        }
//    }
}


final class Shakespeare {
    /**
     * Our data, part 1.
     */
    public static final String[] TITLES = 
    {
    	"Henry IV (1)",   
        "Henry V",
        "Henry VIII",       
        "Richard II",
        "Richard III",
        "Merchant of Venice",  
        "Othello","Henry IV (1)",   
        "Henry V",
        "Henry VIII",       
        "Richard II",
        "Richard III",
        "Merchant of Venice",  
        "Othello","Henry IV (1)",   
        "Henry V",
        "Henry VIII",       
        "Richard II",
        "Richard III",
        "Merchant of Venice",  
        "Othello",
            "King Lear"
    };

}
