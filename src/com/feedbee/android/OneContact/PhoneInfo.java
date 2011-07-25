package com.feedbee.android.OneContact;

import android.content.Context;

public class PhoneInfo
{
	protected String number;
	protected int type;
	protected String label;
	
	public PhoneInfo(String number, int type, String label)
	{
		this.number = number;
		this.type = type;
		this.label = label;
	}
	
	public String getNumber()
	{
		return this.number;
	}
	public int getType()
	{
		return this.type;
	}
	public String getRawLabel()
	{
		return this.label;
	}
	
	public String getLabel(Context context)
	{
		if (this.label != null && this.label != "")
		{
			return this.label;
		}
		
		String[] arr = context.getResources().getStringArray(R.array.phone_type);
		if (this.type > -1 && this.type < arr.length)
		{
			return arr[this.type - 1];
		}
		
		return null;
	}
}
