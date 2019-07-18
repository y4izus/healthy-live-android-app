package com.android.tfm;

import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HelpActivity extends ReadRawFileActivity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
       
        // Read raw file into string and populate TextView
        InputStream iFile = getResources().openRawResource(R.raw.help);
        try {
            TextView helpText = (TextView) findViewById(R.id.txt_help_text);
            String strFile = inputStreamToString(iFile);
            helpText.setText(strFile);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "InputStreamToString failure", e);
        }
    }
}
