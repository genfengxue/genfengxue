package com.genfengxue.windenglish.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.genfengxue.windenglish.R;

public class ConfirmationDialog extends DialogFragment {

	private String text;
	private DialogInterface.OnClickListener yesListener, noListener;
	
	public ConfirmationDialog(String text,
			DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener) {
		this.text = text;
		this.yesListener = yesListener;
		this.noListener = noListener;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		builder.setMessage(text);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes, yesListener);
		builder.setNegativeButton(R.string.no, noListener);
		return builder.create();
	}
	
	public static final OnClickListener DEAF_LISTENER = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
	};
}
