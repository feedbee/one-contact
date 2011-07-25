package com.feedbee.android.OneContact;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider
{
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.d(AppActivity.DEBUG_TAG, "onUpdate");
		
		final int N = appWidgetIds.length;
		for (int i=0; i<N; i++)
		{
			updateWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}
	
	public void onReceive(Context context, Intent intent)
	{
		Log.d(AppActivity.DEBUG_TAG, "onReceive");
		super.onReceive(context, intent);
	}
	
	public static void updateWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId)
	{
		Log.d(AppActivity.DEBUG_TAG, "Update widget " + appWidgetId);
		
		WidgetConfig widgetConfig = getRelatedConfig(context, appWidgetId);
		if (widgetConfig == null)
		{
			return;
		}
		
		ContactInfo contactInfo = ContactInfo.getContactInfo(context, widgetConfig.getContactUri());
		
		if (contactInfo != null)
		{
			RemoteViews views = new RemoteViews(context.getPackageName(),
					widgetConfig.displayName ? R.layout.widget : R.layout.widget_nolabel);
			
			Bitmap b = ContactInfo.getContactPhoto(context, new Long(contactInfo.getId()).toString());
			if (b != null)
			{
				views.setImageViewBitmap(widgetConfig.displayName ? R.id.w_photo : R.id.wn_photo, b);
			}
			
			if (widgetConfig.displayName)
			{
				String name = contactInfo.getDisplayName();
				views.setTextViewText(R.id.w_name, name);
			}
			
			ActionData actionData = widgetConfig.getActionData();
			
			Intent intent = null;
			if (actionData == null || actionData.getType() == WidgetConfigure.WIDGET_ACTION_TYPE_CONTACT)
			{
				Uri contactLookupUri = widgetConfig.getContactUri();
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(contactLookupUri);
			}
			else // call
			{
				intent = new Intent("android.intent.action.CALL",
						Uri.parse("tel:" + Uri.encode(actionData.getData())));
			}
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 1);
			if (widgetConfig.displayName)
			{
				views.setOnClickPendingIntent(R.id.w_name, pendingIntent);
				views.setOnClickPendingIntent(R.id.w_photo, pendingIntent);
			}
			else
			{
				views.setOnClickPendingIntent(R.id.wn_photo, pendingIntent);
			}
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	protected static WidgetConfig getRelatedConfig(Context context, int appWidgetId)
	{
		WidgetConfig wc = Storage.loadWidgetConfig(context, appWidgetId);
		
		return wc;
	}
}
