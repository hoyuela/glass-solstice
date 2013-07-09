package com.discover.mobile.bank.ui.widgets;

import android.content.Context;

public interface CustomProgressDialog {
	void startProgressDialog(boolean isProgressDialogCancelable, Context context);
	
	void stopProgressDialog();
}
