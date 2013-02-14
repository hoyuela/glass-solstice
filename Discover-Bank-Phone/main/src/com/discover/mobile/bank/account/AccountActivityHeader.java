package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;

public class AccountActivityHeader extends RelativeLayout{

	private final View view;

	private final TextView checking;

	private final TextView availableBalance;

	private final TextView currentBalance;

	private final TextView title;

	private final ImageView image;



	public AccountActivityHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.bank_account_activity_header, null);
		checking = (TextView)view.findViewById(R.id.value1);
		availableBalance = (TextView)view.findViewById(R.id.value2);
		currentBalance = (TextView)view.findViewById(R.id.value3);
		image = (ImageView)view.findViewById(R.id.title_image);
		title = (TextView) view.findViewById(R.id.title_text);

		title.setOnClickListener(onClickListener());
		addView(view);
	}

	public void addAccount(final Account account){
		title.setText(account.nickname);
		//		this.checking.setText(account.ending);
		//		this.availableBalance.setText(account.balance);
		//		this.currentBalance.setText(account.balance);
	}

	public OnClickListener onClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v){
				if(availableBalance.getVisibility() == View.VISIBLE){
					image.setBackgroundResource(R.drawable.blue_arrow_down);
					AccountActivityHeader.this.changeVisibility(View.GONE);
				}else{
					image.setBackgroundResource(R.drawable.blue_arrow_up);
					AccountActivityHeader.this.changeVisibility(View.VISIBLE);
				}
			}
		};

	}

	public void changeVisibility(final int visibility){
		view.findViewById(R.id.lable1).setVisibility(visibility);
		view.findViewById(R.id.lable2).setVisibility(visibility);
		view.findViewById(R.id.lable3).setVisibility(visibility);
		availableBalance.setVisibility(visibility);
		currentBalance.setVisibility(visibility);
		checking.setVisibility(visibility);
	}

	public ImageButton getHelp(){
		return (ImageButton) view.findViewById(R.id.help);
	}
}
