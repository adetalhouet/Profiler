package ets.genielog.appwatcher_3.profiling.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This is an helper class managing the {@link SharedPreferences}. For now, this
 * is where we save the token used to establish the IPC connection.
 * 
 * @author alexisdet
 */
public class PreferencesHelper {
	/** The activity context */
	private Context mContext;

	/** Singleton instance */
	private static PreferencesHelper instance = null;

	/** Shared Preferences */
	public SharedPreferences mPref;

	/** Editor for Shared preferences */
	private Editor mEditor;

	/** Shared Preferences Keys : token */
	private static final String TOKEN = "token";

	/**
	 * Get an instance for ProfilerHelper
	 * 
	 * @param context
	 *            - the activity context
	 * @return ProfilerHelper object
	 */
	public static PreferencesHelper getInstance(Context context) {
		if (instance == null) {
			instance = new PreferencesHelper(context);
		}
		return instance;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - the activity context
	 */
	public PreferencesHelper(Context context) {
		this.mContext = context;
		this.mPref = mContext.getSharedPreferences(Config.PREF_PROFILING_NAME,
				0);
	}

	/**
	 * This method sets the token
	 * 
	 * @param token
	 *            - token to set
	 */
	public void setToken(String token) {
		mEditor = mPref.edit();
		mEditor.putString(TOKEN, token);
		mEditor.commit();
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		if (mPref.getString(TOKEN, "").length() == 0)
			setToken(java.util.UUID.randomUUID().toString());
		return mPref.getString(TOKEN, "");
	}

	/**
	 * This method delete the preferences
	 */
	public void deletePreferences() {
		mEditor = mPref.edit();
		mEditor.clear();
		mEditor.commit();
	}

}
