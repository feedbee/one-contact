package com.feedbee.android.OneContact;

import java.io.InputStream;
import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactInfo
{
	protected long id;
	protected String displayName;
	protected Uri lookupUri;
	protected PhoneInfo[] phoneList;
	
	public ContactInfo(long id, String displayName, Uri lookupUri, PhoneInfo[] phoneList)
	{
		this.id = id;
		this.displayName = displayName;
		this.lookupUri = lookupUri;
		this.phoneList = phoneList;
	}
	
	public long getId()
	{
		return this.id;
	}
	public String getDisplayName()
	{
		return this.displayName;
	}
	public Uri getLookupUri()
	{
		return this.lookupUri;
	}
	public PhoneInfo[] getPhoneList()
	{
		return this.phoneList;
	}
	
	// Static
	
	public static ContactInfo getContactInfo(Context context, Uri lookupUri)
	{
		Long id = new Long(0);
		String name = "";
		boolean hasNumber = false;
		
		
		
		// ContactsContract.Data ->
		// ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
		// .GIVEN_NAME
		Cursor c = context.getContentResolver().query(lookupUri, new String[]{
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER}, null, null, null);
		try
		{
			c.moveToFirst();
			id = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
			name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			hasNumber = (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1);
		}
		finally
		{
		    c.close();
		}
		
		if (id.equals(0))
		{
			return null;
		}

		ArrayList<PhoneInfo> phl = null;
		if (hasNumber)
		{
			phl = new ArrayList<PhoneInfo>(10);
			Cursor phones = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER,
							ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.LABEL},
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[]{id.toString()}, null);
			while (phones.moveToNext())
			{
				String phoneNumber = phones.getString(phones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
				int phoneType = phones.getInt(phones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.TYPE));
				String phoneLabel = phones.getString(phones.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.LABEL));
				PhoneInfo ph = new PhoneInfo(phoneNumber, phoneType, phoneLabel);
				phl.add(ph);
			} 
			phones.close();
		}
		else
		{
			phl = new ArrayList<PhoneInfo>(0);
		}
		
		return new ContactInfo(id, name, lookupUri, phl.toArray(new PhoneInfo[]{}));
	}
	
	public static Bitmap getContactPhoto(Context context, String contactId)
	{
		ContentResolver contentResolver = context.getContentResolver();
		
		// content://com.android.contacts/contacts/1
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
		
		if (contactUri != null)
		{
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
			        contentResolver, contactUri);
			if (input != null)
			{
			    return BitmapFactory.decodeStream(input);
			}
		}
		else
		{
		    Log.d(AppActivity.DEBUG_TAG, "No photo Uri");
		}
		
		return null;
	}
}
