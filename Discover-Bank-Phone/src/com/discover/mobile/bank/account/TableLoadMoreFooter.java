package com.discover.mobile.bank.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

public class TableLoadMoreFooter extends RelativeLayout{

	private final View load;

	private final TextView go;

	private final TextView empty;

	public TableLoadMoreFooter(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final View view = LayoutInflater.from(context).inflate(R.layout.table_load_more_footer, null);

		load = view.findViewById(R.id.load_more);
		go = (TextView) view.findViewById(R.id.go_to_top);
		empty = (TextView) view.findViewById(R.id.message);
		hideAll();
		addView(view);
	}


	public void showLoading(){
		load.setVisibility(View.GONE);
		load.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_full_circle));
		go.setVisibility(View.INVISIBLE);
		empty.setVisibility(View.INVISIBLE);
	}

	public void showDone(){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.VISIBLE);
		empty.setVisibility(View.INVISIBLE);
	}

	public void showEmpty(final String message){
		load.setVisibility(View.GONE);
		load.clearAnimation();
		go.setVisibility(View.INVISIBLE);
		empty.setVisibility(View.VISIBLE);
		empty.setText(message);
	}

	public void hideAll(){
		load.setVisibility(View.GONE);

		load.clearAnimation();
		go.setVisibility(View.GONE);
		empty.setVisibility(View.GONE);
	}

	/**
	 * @return the load
	 */
	public View getLoad() {
		return load;
	}

	/**
	 * @return the top
	 */
	public TextView getGo() {
		return go;
	}

}
