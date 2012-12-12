package com.discover.mobile.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Class Description of UserIdPersistence
 * 
 * UserIdPersistence handles the saving and retrieving of a user's ID in
 * persistent, internal storage.
 * 
 * It is a class that utilizes an inner class to abstract away the fact that the
 * inner class extends Activity. It provides a higher level set of methods that
 * let you save and retrieve credentials easily.
 * 
 * The UserIdPersistence class allows you to get and set the user ID to be saved
 * along with the state of the save ID button/check box/switch on the login
 * screen.
 * 
 * This class uses its nested class ObjectReaderWriter to save the user ID and
 * button state to a file. ObjectReaderWriter manages a private file that is
 * associated with the current install of the application. If the application is
 * ever uninstalled, this file is deleted along with it. ObjectReaderWriter is
 * an activity that saves a SerializableData to file, as a serialized object. A
 * SerializableData is a serialized class that stores the user ID and the state
 * of the save button. It should be able to be extended easily with more
 * attributes.
 * 
 * @author scottseward
 * 
 */

public class UserIdPersistance {

	private static final String TAG = UserIdPersistance.class.getSimpleName();

	private ObjectReaderWriter objectReaderWriter;
	private SerializableData credentials;

	public UserIdPersistance(Context context) {
		objectReaderWriter = new ObjectReaderWriter(context);
		credentials = objectReaderWriter.getObjectFromFile();
	}

	public void saveId(String newId) {
		credentials.setUserId(newId);
		objectReaderWriter.saveObject(credentials);
	}

	public String getUserId() {
		return credentials.getUserId();
	}

	public boolean getButtonState() {
		return credentials.getSaveState();
	}

	public void saveButtonState(boolean wasSaved) {
		credentials.setSaveState(wasSaved);
		objectReaderWriter.saveObject(credentials);
	}

	private class ObjectReaderWriter extends Activity {

		private static final String FILE_NAME = "Discover"; //$NON-NLS-1$

		private Context upperContext = null;

		private ObjectOutputStream objectOutputStream = null;
		private ObjectInputStream objectInputStream = null;

		ObjectReaderWriter(Context context) {
			upperContext = context;
		}

		private void openConnectionToFile() {
			try {
				FileOutputStream fos = upperContext.openFileOutput(FILE_NAME,
						Context.MODE_PRIVATE);
				FileInputStream fis = upperContext.openFileInput(FILE_NAME);
				objectInputStream = new ObjectInputStream(fis);
				objectOutputStream = new ObjectOutputStream(fos);
			} catch (StreamCorruptedException e) {
				Log.e(TAG,
						"Stream Corrupted Exception While Opening File: " + e);//$NON-NLS-1$
			} catch (FileNotFoundException e) {
				Log.e(TAG, "File Not Found Exception While Opening File: " + e);//$NON-NLS-1$
			} catch (IOException e) {
				Log.e(TAG, "IO Exception While Opening File: " + e);//$NON-NLS-1$
			}
		}

		private void closeConnectionToFile() {
			try {
				objectInputStream.close();
				objectOutputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "IO Exception While Closing File: " + e);//$NON-NLS-1$
			}
		}

		public SerializableData getObjectFromFile() {
			SerializableData credentials = new SerializableData();
			try {
				FileInputStream fis = upperContext.openFileInput(FILE_NAME);
				objectInputStream = new ObjectInputStream(fis);
				credentials = (SerializableData) objectInputStream.readObject();
				clearIdIfNotSaved(credentials);
				fis.close();
				objectInputStream.close();
				return credentials;
			} catch (OptionalDataException e) {
				Log.e(TAG, "Optional Data Exception While Loading Object: " + e);//$NON-NLS-1$
			} catch (ClassNotFoundException e) {
				Log.e(TAG,
						"Class Not Found Exception While Loading Object: " + e);//$NON-NLS-1$
			} catch (IOException e) {
				Log.e(TAG, "IO Exception While Loading Object: " + e);//$NON-NLS-1$
			}
			return credentials;
		}
		
		private void clearIdIfNotSaved(SerializableData credentials) {
			if(!credentials.getSaveState() && !"".equals(credentials.getUserId()))
				credentials.setUserId("");
		}

		public void saveObject(SerializableData writeableObject) {
			try {
				ObjectOutputStream oos = 
						new ObjectOutputStream(upperContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
				oos.writeObject(writeableObject);
				oos.flush();
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void writeObjectToFile(SerializableData writeableObject) {
			try {
				objectOutputStream.writeObject(writeableObject);
				objectOutputStream.flush();
				objectOutputStream.reset();
			} catch (IOException e) {
				Log.e(TAG, "IO Exception While Saving Object: " + e); //$NON-NLS-1$
			}
		}
	}

}
