package com.genfengxue.windenglish.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.genfengxue.windenglish.R;

public class ConfirmationDialog extends DialogFragment {

	private String message, yesText, nerualText, noText;
	private OnClickListener yesListener;
	private OnClickListener nerualListener;
	private OnClickListener noListener;

	public ConfirmationDialog(String message, String yesText,
			OnClickListener yesListener, String nerualText,
			OnClickListener nerualListener, String noText,
			OnClickListener noListener) {
		this.message = message;
		this.yesText = yesText;
		this.nerualText = nerualText;
		this.noText = noText;
		this.yesListener = yesListener;
		this.nerualListener = nerualListener;
		this.noListener = noListener;
	}
	
	public ConfirmationDialog(String message,
			OnClickListener yesListener,
			OnClickListener noListener) {
		this(message, null, yesListener, null, null, null, noListener);
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		if (message != null) {
			builder.setMessage(message);
		}
		
		builder.setCancelable(false);
		if (yesText == null) {
			builder.setPositiveButton(R.string.yes, yesListener);
		} else {
			builder.setPositiveButton(yesText, yesListener);
		}
		
		if (noText == null) {
			builder.setNegativeButton(R.string.no, noListener);
		} else {
			builder.setNegativeButton(noText, noListener);
		}
		
		if (nerualText != null) {
			builder.setNeutralButton(nerualText, nerualListener);
		}
		
		return builder.create();
	}
	
	public static final OnClickListener DEAF_LISTENER = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
	};
}
