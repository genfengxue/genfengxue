package com.genfengxue.windenglish.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ItemsDialog extends DialogFragment {

	private String[] items;
	private OnClickListener itemClickListener;
	
	public ItemsDialog(String[] items, OnClickListener itemClickListener) {
		this.items = items;
		this.itemClickListener = itemClickListener;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		builder.setItems(items, itemClickListener);
		return builder.create();
	}
	
}
