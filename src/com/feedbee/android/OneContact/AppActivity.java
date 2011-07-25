package com.feedbee.android.OneContact;

import android.app.Activity;
import android.os.Bundle;

public class AppActivity extends Activity
{
	public static final String DEBUG_TAG = "OneContact";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}