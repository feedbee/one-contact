package com.feedbee.android.OneContact;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * The configuration screen for the OneContactWidgetProvider widget.
 */
public class WidgetConfigure extends Activity
	implements OnClickListener, RadioGroup.OnCheckedChangeListener
{
	public static final String DEBUG_TAG = "OneContactWidgetConfigure";
	
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final int VIEW_ID_START = 0x7f071000;
	public static final int WIDGET_ACTION_TYPE_CONTACT = 0;
	public static final int WIDGET_ACTION_TYPE_CALL = 1;
	
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	ContactInfo contact = null;
	PhoneInfo[] lastPhoneList = null;
	
	public WidgetConfigure()
	{
		super();
		Log.d(AppActivity.DEBUG_TAG, "Conf: constructor");
	}
	
	@Override
	public void onCreate(Bundle icicle)
	{
		Log.d(AppActivity.DEBUG_TAG, "Conf: onCreate");
		super.onCreate(icicle);
		
		// Set the result to CANCELED.  This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		this.setResult(RESULT_CANCELED);
		
		// Set the view layout resource to use.
		this.setContentView(R.layout.widget_configure);
		
		// Bind the action for the save button.
		this.findViewById(R.id.wc_btn_pick).setOnClickListener(this);
		this.findViewById(R.id.wc_btn_quit).setOnClickListener(this);
		this.findViewById(R.id.wc_btn_apply).setOnClickListener(this);
		((RadioGroup)this.findViewById(R.id.wc_phone_list)).setOnCheckedChangeListener(this);
		
		// Find the widget id from the intent. 
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			this.appWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		// If they gave us an intent without the widget id, just bail.
		if (this.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
		{
			finish();
		}
		
		// Initialization
		WidgetConfig wc = Storage.loadWidgetConfig(WidgetConfigure.this, this.appWidgetId);
		if (wc != null)
		{
			this.loadContact(wc.getContactUri());
		}
	}
	
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.wc_btn_pick:
				this.launchContactPicker();
				return;
			case R.id.wc_btn_apply:
				this.onApplyClick();
				return;
			case R.id.wc_btn_quit:
				this.finish();
				return;
		}
	}
	
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		Log.d(AppActivity.DEBUG_TAG, "Conf:onCheckedChanged");
		if (checkedId != View.NO_ID)
		{
			this.findViewById(R.id.wc_btn_apply).setEnabled(true);
		}
		else
		{
			this.findViewById(R.id.wc_btn_apply).setEnabled(false);
		}
	}
	
	protected void launchContactPicker()
	{  
		Log.d(AppActivity.DEBUG_TAG, "Conf: launchContactPicker");
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
				ContactsContract.Contacts.CONTENT_URI);  
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
	}
	
	protected void loadContact(Uri contactLookupUri)
	{
		final Context context = WidgetConfigure.this;
		
		// Get contact
		ContactInfo contactInfo = ContactInfo.getContactInfo(context, contactLookupUri);
		this.contact = contactInfo;
		
		// Load contact photo
		Bitmap b = ContactInfo.getContactPhoto(context, new Long(contactInfo.id).toString());
		ImageView iv = (ImageView)this.findViewById(R.id.wc_cnt_photo);
		if (b != null)
		{
			iv.setImageBitmap(b);
		}
		else
		{
			iv.setImageResource(R.drawable.photo_default);
		}
		
		// Load contact name
		TextView t = (TextView)this.findViewById(R.id.wc_cnt_name);
		String name = contactInfo.displayName;
		t.setText(name);
		
		// Load contact phones
		this.lastPhoneList = contactInfo.phoneList;
		RadioGroup rg = (RadioGroup)this.findViewById(R.id.wc_phone_list);
		rg.removeAllViews();
		
		int idStart = VIEW_ID_START;
		RadioButton newRadioButton = new RadioButton(this);
		newRadioButton.setText(context.getString(R.string.l_open_contact));
		newRadioButton.setId(idStart++);
		LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
			RadioGroup.LayoutParams.WRAP_CONTENT,
			RadioGroup.LayoutParams.WRAP_CONTENT);
		rg.addView(newRadioButton, 0, layoutParams);
		
		for (PhoneInfo phoneInfo: contactInfo.phoneList)
		{
			newRadioButton = new RadioButton(this);
			newRadioButton.setText(phoneInfo.getLabel(context) + ": " + phoneInfo.getNumber());
			newRadioButton.setId(idStart++);
			rg.addView(newRadioButton, rg.getChildCount(), layoutParams);
		}
	}
	
	protected void onContactPicked(Uri uri)
	{
		Log.d(AppActivity.DEBUG_TAG, "Conf: onContactPicked");
		this.loadContact(uri);
	}
	
	protected void onApplyClick()
	{
		Log.d(AppActivity.DEBUG_TAG, "Conf: onApplyClick");
		// When the button is clicked, save the string in our prefs and return that they
		// clicked OK.
		
		// Save
		final Context context = WidgetConfigure.this;
		/*Storage.saveContactUri(context, this.appWidgetId, this.contact.getLookupUri());*/
		int id = ((RadioGroup)this.findViewById(R.id.wc_phone_list))
				.getCheckedRadioButtonId() - VIEW_ID_START;
		ActionData actionData = null;
		if (id > 0 && id <= this.lastPhoneList.length)
		{
			/*Storage.saveActionData(context, this.appWidgetId,
					new ActionData(this.lastPhoneList[id - 1].number, WIDGET_ACTION_TYPE_CALL));*/
			actionData = new ActionData(this.lastPhoneList[id - 1].number, WIDGET_ACTION_TYPE_CALL);
		}
		else if (id == 0)
		{
			actionData = new ActionData("", WIDGET_ACTION_TYPE_CONTACT);
		}
		boolean displayName = ((CheckBox)this.findViewById(R.id.wc_display_name)).isChecked();
		Storage.saveWidgetConfig(context, this.appWidgetId,
				new WidgetConfig(this.contact.getLookupUri(), actionData, displayName));
		
		// Push widget update to surface with newly set prefix
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		WidgetProvider.updateWidget(context, appWidgetManager, this.appWidgetId);
		
		// Exit: Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		this.setResult(RESULT_OK, resultValue);
		this.finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(AppActivity.DEBUG_TAG, "Conf: onActivityResult");
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
				case CONTACT_PICKER_RESULT:
					this.onContactPicked(data.getData());
					return;
			}
		}
		else
		{
			// gracefully handle failure
			Log.w(DEBUG_TAG, "Warning: activity result not ok");
		}  
	}
}