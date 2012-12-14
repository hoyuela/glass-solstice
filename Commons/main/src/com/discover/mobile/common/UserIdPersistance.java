package com.discover.mobile.common;

import java.io.FileNotFoundException;
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
 * persistent, internal, private storage.
 * 
 * It is a class that utilizes an inner class to abstract away the fact that the
 * inner class extends Activity. It provides a higher level set of methods that
 * let you save and retrieve the attributes of the persistent object easily.
 * 
 * The UserIdPersistence class allows you to get and set the user ID to be saved
 * along with the state of the save ID button/check box/switch on the login
 * screen.
 * 
 * This class uses its nested class ObjectReaderWriter to save the user ID and
 * button state to a file. ObjectReaderWriter manages a private file that is
 * associated with the current install of the application. If the application is
 * ever un-installed, this file is deleted along with it. ObjectReaderWriter is
 * an activity that saves a SerializableData object to file.
 * A SerializableData object is a serialized class that stores the user ID and the state
 * of the save button. It should be able to be extended easily with more attributes.
 * 
 * If this class gets much larger it should be run on another thread (not the UI thread).
 * 
 * @author scottseward
 * 
 */

public class UserIdPersistance {

	private static final String TAG = UserIdPersistance.class.getSimpleName();

	private ObjectReaderWriter objectReaderWriter;
	private SerializableData credentials;

	/**
	 * When a UserIdPersistance object is created, it automatically loads the saved object from file,
	 * or is initialized with an empty object. This is so that its getter methods can immediately return usable
	 * data.
	 * 
	 * @param context The context in which the persistence file is to be accessed.
	 */
	public UserIdPersistance(Context context) {
		objectReaderWriter = new ObjectReaderWriter(context);
		credentials = objectReaderWriter.getObjectFromFile();
	}

	/**
	 * Write a user ID to persistent storage.
	 * 
	 * @param newId The user ID to be written to storage.
	 */
	public void saveId(String newId) {
		credentials.setUserId(newId);
		objectReaderWriter.saveObject(credentials);
	}

	/**
	 * Gets the user ID that was loaded from file.
	 * 
	 * @return A String representing the saved ID.
	 */
	public String getUserId() {
		return credentials.getUserId();
	}

	/**
	 * Gets the saved state of the save-user-ID button that was loaded from file.
	 * 
	 * @return A boolean value representing the saved state of the save-user-ID button.
	 */
	public boolean getButtonState() {
		return credentials.getSaveState();
	}

	/**
	 * Write the state of the save-user-ID button to file.
	 * 
	 * @param wasSaved A boolean representing the state of the save-user-ID button.
	 */
	public void saveButtonState(boolean wasSaved) {
		credentials.setSaveState(wasSaved);
		objectReaderWriter.saveObject(credentials);
	}

	/**
	 * The class responsible for saving and loading a SerializableData object to protected, persistent, storage.
	 * 
	 * @author scottseward
	 *
	 */
	private class ObjectReaderWriter extends Activity {

		private static final String FILE_NAME = "Discover"; //$NON-NLS-1$

		private final Context upperContext;

		/**
		 * Setup the ObjectReaderWriter with the context that it will be accessing the file from.
		 * 
		 * @param context The context where the file is to be accessed from.
		 */
		ObjectReaderWriter(Context context) {
			upperContext = context;
		}

		/**
		 * This method gets the first serializable object from the file.
		 * 
		 * @return A SerializeableData object.
		 */
		private SerializableData getObjectFromFile() {
			SerializableData credentials = new SerializableData();
			try {
				ObjectInputStream objectInputStream
							= new ObjectInputStream(upperContext.openFileInput(FILE_NAME));
				credentials = (SerializableData)objectInputStream.readObject();
				clearIdIfNotSaved(credentials);
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
		
		/**
		 * If an ID is saved in the file and the user un-checks save-user-ID, then this will clear the ID.
		 * 
		 * This effectively deletes the user ID if the save-user-ID check mark is not checked and the user quits the app
		 * 
		 * @param credentials A SerializableData object with some data.
		 */
		private void clearIdIfNotSaved(SerializableData credentials) {
			if(!credentials.getSaveState() && !"".equals(credentials.getUserId()))
				credentials.setUserId("");
		}

		/**
		 * Writes a serializable object to file, overwriting any previous object.
		 * 
		 * @param writeableObject A serializeable data object to write to file.
		 */
		private void saveObject(SerializableData writeableObject) {
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
	}

}
