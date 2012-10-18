package lab.mobile.my_netsoul;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

public class DeleteContactFragment extends DialogFragment {
	private Integer pos_to_del = 0;
	private ContactAdapter adapt = null;
	
	public static DeleteContactFragment newInstance(Activity activity, Integer id, ContactAdapter l) {
       
        DeleteContactFragment frag = new DeleteContactFragment();
        frag.pos_to_del = id;
        frag.adapt = l;
        return frag;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_delete_contact, null);
        builder.setView(v);
        builder.setMessage(R.string.dialog_delete_contact)
               .setPositiveButton(R.string.dialog_confirmer, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
            		   ContactManager.getInstance().removeContactByPosition(pos_to_del);
            		   if (adapt != null)
            			   Log.i("notify", "Data changed !");
            			   adapt.notifyDataSetChanged();
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               });
        return builder.create();
    }
}
