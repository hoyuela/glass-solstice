package com.discover.mobile.bank.ui.buttongroup;

import android.widget.Button;

import com.discover.mobile.common.net.NetworkServiceCall;

public class ServerCallButton {

	private final Button button;

	private final NetworkServiceCall<?> call;

	public ServerCallButton(final Button button, final NetworkServiceCall<?> call){
		this.button = button;
		this.call = call;
	}

	/**
	 * @return the button
	 */
	public Button getButton() {
		return this.button;
	}

	/**
	 * @return the call
	 */
	public NetworkServiceCall<?> getCall() {
		return this.call;
	}

}
