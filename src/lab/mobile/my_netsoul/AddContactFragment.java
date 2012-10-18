package lab.mobile.my_netsoul;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class AddContactFragment extends DialogFragment {
	String login = null;
	
	
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
	// Use this instance of the interface to deliver action events
    static NoticeDialogListener mListener;
        
    /* Call this to instantiate a new NoticeDialog.
     * @param activity  The activity hosting the dialog, which must implement the
     *                  NoticeDialogListener to receive event callbacks.
     * @returns A new instance of NoticeDialog.
     * @throws  ClassCastException if the host activity does not
     *          implement NoticeDialogListener
     */
    public static AddContactFragment newInstance(Activity activity) {
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events with it
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
        AddContactFragment frag = new AddContactFragment();
        return frag;
    }
    
    public String getLogin() {
    	return login;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_add, null);
        builder.setView(v);
        builder.setMessage(R.string.dialog_add_contact)
               .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   	EditText login = (EditText)v.findViewById(R.id.username);
                   		AddContactFragment.this.login = login.getText().toString();
               			mListener.onDialogPositiveClick(AddContactFragment.this);
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogNegativeClick(AddContactFragment.this);
                   }
               });
        return builder.create();
    }
}
