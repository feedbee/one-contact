package com.feedbee.android.OneContact;

import android.net.Uri;

public class WidgetConfig implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	String contactUri;
	ActionData actionData;
	boolean displayName;
	
	public WidgetConfig(Uri contactUri, ActionData actionData,
			boolean displayName) {
		super();
		this.contactUri = contactUri.toString();
		this.actionData = actionData;
		this.displayName = displayName;
	}

	public Uri getContactUri() {
		return Uri.parse(contactUri);
	}

	public void setContactUri(Uri contactUri) {
		this.contactUri = contactUri.toString();
	}

	public ActionData getActionData() {
		return actionData;
	}

	public void setActionData(ActionData actionData) {
		this.actionData = actionData;
	}

	public boolean isDisplayName() {
		return displayName;
	}

	public void setDisplayName(boolean displayName) {
		this.displayName = displayName;
	}
}
