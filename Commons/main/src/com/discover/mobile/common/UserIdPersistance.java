package com.discover.mobile.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;

public class UserIdPersistance {
	
	private static final String TAG = UserIdPersistance.class.getSimpleName();
	
	private UserCredentials currentCredentials;
		
	public UserIdPersistance(Context context){
		currentCredentials = new UserCredentials( context );
	}
	
	public void saveId(String newId) {
		currentCredentials.userId = newId;
	}
	
	public String getUserId() {
		return currentCredentials.getUserId();
	}
	
	public boolean getButtonState() {
		return currentCredentials.getSavedState();
	}
	
	public void saveButtonStateAs(boolean wasSaved) {
		currentCredentials.saveButtonStateAs(wasSaved);
	}

	private class UserCredentials extends Activity implements Serializable {
		private static final long serialVersionUID = -8462997307297475408L;
		
		Context upperContext;
		
		private static final String FILE_NAME = "Discover"; //$NON-NLS-1$
		private static final String COULD_NOT_OPEN = "Accessing persitance file failed."; //$NON-NLS-1$
		
		private ObjectOutputStream objectOutputStream;
		private ObjectInputStream objectInputStream;
		
		String userId = ""; //$NON-NLS-1$
		boolean  wasSaved = false;

		UserCredentials(Context context){
			upperContext = context;
			openConnectionToFile();
			loadObjectFromFile();	
			closeConnectionToFile();
		}
		
		public String getUserId() {
			return userId;
		}
		
		public boolean getSavedState() {
			return wasSaved;
		}
		
		public void saveButtonStateAs(boolean state) {
			wasSaved = state;
			saveObject();
		}
		
		private void openConnectionToFile() {
			try {
				FileOutputStream fos = upperContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
				FileInputStream fis  = upperContext.openFileInput(FILE_NAME);
				objectInputStream = new ObjectInputStream(fis);
				objectOutputStream = new ObjectOutputStream(fos);
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void closeConnectionToFile() {
			try {
				objectInputStream.close();
				objectOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void loadObjectFromFile() {
			try {
				openConnectionToFile();

					UserCredentials temp = (UserCredentials)objectInputStream.readObject();
				
					this.userId = temp.userId;
					this.wasSaved = temp.wasSaved;
				
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void saveObject() {
			openConnectionToFile();
			writeObjectToFile();
			closeConnectionToFile();
		}
		
		private void writeObjectToFile() {
			try {
				objectOutputStream.writeObject(currentCredentials);
				objectOutputStream.flush();
				objectOutputStream.reset();
				objectOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
