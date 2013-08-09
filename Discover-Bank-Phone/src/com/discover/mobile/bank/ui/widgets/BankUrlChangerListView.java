package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class BankUrlChangerListView extends ListView{

	public BankUrlChangerListView(final Context context) {
		super(context);
		initUi();
	}

	public BankUrlChangerListView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initUi();
	}

	public BankUrlChangerListView(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		initUi();
	}

	private void initUi(){
		//setAdapter(new BankUrlChangerAdapter(getContext()));
	}



}
