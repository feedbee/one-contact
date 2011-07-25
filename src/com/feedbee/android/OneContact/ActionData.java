package com.feedbee.android.OneContact;

public class ActionData implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected String data;
	protected int type;
	
	public ActionData(String data, int type)
	{
		super();
		this.data = data;
		this.type = type;
	}
	
	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}
	
	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
}
