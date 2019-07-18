package com.android.tfm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends Activity{
	
	private Button btn_config;
	private Button btn_play;
	private Button btn_monitoring;
	private Button btn_messages;
	private Button btn_help;
	private Button btn_exit;
	
	private TfmDbAdapter mDbHelper;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        mDbHelper = new TfmDbAdapter(this);
	    mDbHelper.open();
        
        init_components();
        
        manage_click_btn(btn_config, ConfigurationActivity.class);
        manage_click_btn_play(btn_play, AccelerometerService.class);
        manage_click_btn(btn_monitoring, MonitoringActivity.class);
        manage_click_btn(btn_messages, MessagesActivity.class);
        manage_click_btn(btn_help, HelpActivity.class);
        manage_click_btn_exit(btn_exit);
    }
    
    public void init_components(){
    	btn_config = (Button) findViewById(R.id.btn_config);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_monitoring = (Button) findViewById(R.id.btn_monitoring);
        btn_messages = (Button) findViewById(R.id.btn_messages);
        btn_help = (Button) findViewById(R.id.btn_help);
        btn_exit = (Button) findViewById(R.id.btn_exit);
    }
    
    
    
    public void manage_click_btn(Button btn, final Class<?> cls){
    	btn.setOnClickListener(new AdapterView.OnClickListener() {      
			@Override
			public void onClick(View v) {
				// Launch Activity
                startActivity(new Intent(MenuActivity.this, cls));
			}
        });
    } 
    
    public void manage_click_btn_play(Button btn, final Class<?> cls){
    	btn.setOnClickListener(new AdapterView.OnClickListener() {      
			@Override
			public void onClick(View v) {
				// Launch Service
			    Toast.makeText(MenuActivity.this, "La lectura está en marcha", Toast.LENGTH_SHORT).show();
				startService(new Intent(MenuActivity.this, cls));
			}
        });
    }
    
    public void manage_click_btn_exit(Button btn){
    	btn.setOnClickListener(new AdapterView.OnClickListener() {      
			@SuppressWarnings("null")
			@Override
			public void onClick(View v) {
				// Launch Service
				
//				Toast.makeText(MenuActivity.this, "Borrando bbdd", Toast.LENGTH_SHORT).show();
				
				mDbHelper.rehacerBBDD();

				Toast.makeText(MenuActivity.this, "Metiendo nuevos datos", Toast.LENGTH_SHORT).show();
							
			}
        });
    } 
}
