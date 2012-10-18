package lab.mobile.my_netsoul;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	
	public ContactAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return ContactManager.getInstance().getCount();
	}

	@Override
	public Object getItem(int position) {
		return ContactManager.getInstance().getContactByPosition(position);
	}

	@Override
	public long getItemId(int position) {
//		Contact c = ContactManager.getInstance().getContact(position);
//		if (c != null)
//			return c.id;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contactrow, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.pict);
            holder.user = (TextView) convertView.findViewById(R.id.textUser);
            holder.status = (TextView) convertView.findViewById(R.id.textStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            
        Contact c = (Contact) this.getItem(position);
        Log.d("getView", "item: pos: " + position + ", " + c);
        if (c != null && holder != null) {
        	Log.d("getView", "Setted");
        	holder.img.setImageBitmap(c.getPhoto());
        	holder.user.setText(c.login);
        	holder.status.setText(c.status + (c.is_typing ? " and is typing ..." : ""));
        }
        //convertView.postInvalidate();
        convertView.invalidate();
		return convertView;
	}
	
	static class ViewHolder {
		ImageView img;
        TextView user;
        TextView status;
    }

}
