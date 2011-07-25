package com.feedbee.android.OneContact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class Storage
{
	public static final String PREFS_NAME
			= "com.feedbee.android.OneContact.OneContactWidget";
	public static final int FORMAT_VERSION
			= 2;
	
	private Storage(){}
	
	// Write the config to the SharedPreferences object for this widget
	public static void saveWidgetConfig(Context context, int appWidgetId, WidgetConfig widgetConfig)
	{
		// Serialize
		String config;
		try
		{
			config = toString(widgetConfig);
		} catch (IOException e)
		{
			return;
		}
		
		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefs.putInt("OneCnt_Widget_Version_" + appWidgetId, FORMAT_VERSION);
		prefs.putString("OneCnt_Widget_Config_" + appWidgetId, config);
		prefs.commit();
	}
	
	// Read the config from the SharedPreferences object for this widget.
	// If there is no preference saved, return null
	public static WidgetConfig loadWidgetConfig(Context context, int appWidgetId)
	{
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		int version = prefs.getInt("OneCnt_Widget_Version_" + appWidgetId, 0);
		if (version != FORMAT_VERSION)
		{
			return null;
		}
		
		String data = prefs.getString("OneCnt_Widget_Config_" + appWidgetId, null);
		if (data == null || data == "")
		{
			return null;
		}
		
		// Deserialize
		WidgetConfig wc = null;
		try
		{
			wc = (WidgetConfig)fromString(data);
		}
		catch (Exception e)
		{
			return null;
		}
		
		return wc;
	}
	
	/**
	 * @deprecated
	 */
	public static void saveContactUri(Context context, int appWidgetId, Uri lookupUri)
	{
		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefs.putString("OneCnt_Widget_" + appWidgetId, lookupUri.toString());
		prefs.commit();
	}
	
	
	/**
	 * @deprecated
	 */
	public static Uri loadContactUri(Context context, int appWidgetId)
	{
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String uriStr = prefs.getString("OneCnt_Widget_" + appWidgetId, null);
		if (uriStr == null)
		{
			return null;
		}
		
		Uri uri = Uri.parse(uriStr);
		
		return uri;
	}
	
	/**
	 * @deprecated
	 */
	public static void saveActionData(Context context, int appWidgetId,
			ActionData actionData)
	{
		SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefs.putInt("OneCnt_Widget_Version_" + appWidgetId, FORMAT_VERSION);
		prefs.putString("OneCnt_Widget_Data_" + appWidgetId, actionData.getData());
		prefs.putInt("OneCnt_Widget_Type_" + appWidgetId, actionData.getType());
		prefs.commit();
	}
	
	/**
	 * @deprecated
	 */
	public static ActionData loadActionData(Context context, int appWidgetId)
	{
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		int version = prefs.getInt("OneCnt_Widget_Version_" + appWidgetId, 0);
		if (version != FORMAT_VERSION)
		{
			return null;
		}
		String data = prefs.getString("OneCnt_Widget_Data_" + appWidgetId, null);
		int actionType = prefs.getInt("OneCnt_Widget_Type_" + appWidgetId, 0);
		if (actionType == 0 || data == null || data == "")
		{
			return null;
		}
		
		return new ActionData(data, actionType);
	}
	
	// http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
	
    /** Read the object from Base64 string. */
	private static Object fromString(String s)
		throws IOException,	ClassNotFoundException
	{
		byte [] data = Base64Coder.decode(s);
		ObjectInputStream ois = new ObjectInputStream( 
				new ByteArrayInputStream(data));
		Object o  = ois.readObject();
		ois.close();
		
		return o;
	}
	/** Write the object to a Base64 string. */
	private static String toString(Serializable o) throws IOException
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(o);
	    oos.close();
	    
	    return new String(Base64Coder.encode(baos.toByteArray()));
	}
}
