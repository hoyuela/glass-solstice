package com.discover.mobile.test.login;

import com.discover.mobile.R;
import com.discover.mobile.login.LoggedInLandingPage;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<com.discover.mobile.login.LoginActivity>{
	
	private Instrumentation instrument;
	
	private LoginActivity activity;
	
	private final static String VALID_USERNAME = "uid4545"; //$NON-NLS-1$
	
	private final static String VALID_PASSWORD = "ccccc"; //$NON-NLS-1$

	public LoginActivityTest(Class<LoginActivity> activityClass) {
		super(activityClass);
	}
	
	public LoginActivityTest(){
		super("com.discover.mobile.login", LoginActivity.class); //$NON-NLS-1$
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		this.setActivityInitialTouchMode(true);
		instrument = this.getInstrumentation();
		activity = this.getActivity();
	}
	
	public void testLoginOffered(){
		final EditText username = (EditText) activity.findViewById(R.id.username);
		final EditText password = (EditText) activity.findViewById(R.id.password);
		final Button login = (Button) activity.findViewById(R.id.login_button);
		
		assertEquals("Username is field is invisible", View.VISIBLE, username.getVisibility()); //$NON-NLS-1$
		assertEquals("Password is field is invisible", View.VISIBLE, password.getVisibility()); //$NON-NLS-1$
		assertEquals("Login is field is invisible", View.VISIBLE, login.getVisibility()); //$NON-NLS-1$
	}
	
	public void testRememberMeIsNotSelected(){
		final ToggleButton rememberMe = (ToggleButton) activity.findViewById(R.id.toggle_button_save_user_id);

		if(!rememberMe.isChecked()){
			final EditText username = (EditText) activity.findViewById(R.id.username);
			final EditText password = (EditText) activity.findViewById(R.id.password);
			
			assertTrue("Username is not empty", username.getText().toString().isEmpty()); //$NON-NLS-1$
			assertTrue("Pasword is not empty", password.getText().toString().isEmpty()); //$NON-NLS-1$
		}
	}
	
	public void testValidUser(){
		final Instrumentation.ActivityMonitor nextActivityMonitor = 
				instrument.addMonitor(LoggedInLandingPage.class.getName(), null, false);
		setLoginFields(VALID_USERNAME, VALID_PASSWORD);
		attemptLogin();
		
		Activity nextActivity = nextActivityMonitor.getLastActivity();
		
		assertNotNull("Logged in Activity is null", nextActivity); //$NON-NLS-1$
		assertTrue("Activity is not an instance of LoggedInLandingPage", nextActivity instanceof LoggedInLandingPage); //$NON-NLS-1$
	}
	
	public void testRegistrationClicked(){
		final Instrumentation.ActivityMonitor nextActivityMonitor = 
				instrument.addMonitor(RegistrationAccountInformationActivity.class.getName(), null, false);
		instrument.addMonitor(nextActivityMonitor);
		
		final TextView registrationButton = (TextView)activity.findViewById(R.id.register_text);
		TouchUtils.tapView(this, registrationButton);
		Activity nextActivity = nextActivityMonitor.getLastActivity();
		
		assertNotNull("Registration Activity is null", nextActivity); //$NON-NLS-1$
		assertTrue("Activity is not an instance of Registration Account Information Activity",nextActivity instanceof RegistrationAccountInformationActivity); //$NON-NLS-1$
	}
	
	public void testInvalidUsername(){
		setLoginFields("fakeUserName", ""); //$NON-NLS-1$ //$NON-NLS-2$
		attemptLogin();
		
		final TextView error = (TextView)this.getActivity().findViewById(R.id.error_text_view);
		assertEquals("Error Alert is not correct", getString(R.string.login_error), error.getText().toString()); //$NON-NLS-1$
	}
	
	private String getString(int id){
		return this.getActivity().getResources().getString(id);
	}
	
	private void setLoginFields(final String username, final String password){		
		final EditText user = (EditText) activity.findViewById(R.id.username);
		final EditText pass = (EditText) activity.findViewById(R.id.password);
		
		activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				user.setText(username);
				pass.setText(password);
			}
		});
	}
	
	private void attemptLogin(){
		final Button login = (Button) activity.findViewById(R.id.login_button);
		TouchUtils.tapView(this, login);
	}	
	
	@Override
	protected void tearDown() throws Exception{
		this.getActivity().finish();
		super.tearDown();
	}

}
