package com.android.tfm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MonitoringActivity extends GeneralActivity{
	
	private Button btn_edit_weight;
	private Button btn_weight_evolution;
	private Button btn_bmi_evolution;
	private Button btn_activity;
	
	private TextView txt_weight;
	
	private float weight;
	
	static final int WEIGHT_DIALOG_ID = 1;
	
	private TfmDbAdapter mDbHelper;
	
	ArrayList<WeightBMIObject> weight_bmi_list = new ArrayList<WeightBMIObject>();
	
	// Se accede a las SharedPreferences para leer la altura.
	SharedPreferences sp;
	float sp_tall;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);
        
        mDbHelper = new TfmDbAdapter(this);
        mDbHelper.open();
        
        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    	sp_tall = sp.getFloat(APP_PREFERENCES_TALL, (float) 1);
    	
	    Toast.makeText(MonitoringActivity.this, sp_tall+"", Toast.LENGTH_SHORT).show();
        
        try{
        	init_components();
        	
        	btn_edit_weight.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(WEIGHT_DIALOG_ID);
                }
            });
            manage_click_btn(btn_weight_evolution, WeightEvolutionActivity.class);
            manage_click_btn(btn_bmi_evolution, BMIEvolutionActivity.class);
            manage_click_btn(btn_activity, ActivityEvolutionActivity.class);
            
        }catch (Exception e){
        	Log.e("TFM Log", "Failed to load activities", e);
        	Toast.makeText(MonitoringActivity.this, "TFM Log", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void init_components(){
    	btn_edit_weight = (Button) findViewById(R.id.btn_edit_weight);
        btn_weight_evolution = (Button) findViewById(R.id.btn_weight_evolution);
        btn_bmi_evolution = (Button) findViewById(R.id.btn_bmi_evolution);
        btn_activity = (Button) findViewById(R.id.btn_activity);
        
        txt_weight = (TextView) findViewById(R.id.txt_weight);
 
        Cursor c = mDbHelper.fetchAllWeightBMI();
        weight_bmi_list.clear();
        
        //Iteramos a traves de los registros del cursor
        if (c != null) {
        	c.moveToFirst();
        	if (c.isAfterLast() == false) {
            	WeightBMIObject weight_bmi = new WeightBMIObject();
            	weight_bmi.set_weight(c.getFloat(1));
            	weight_bmi.set_bmi(c.getFloat(2));
            	weight_bmi.set_timestamp(c.getLong(3));
                weight_bmi_list.add(weight_bmi); 
                c.moveToNext();
             }
             c.close();
        } else;
        
  
        int num_rows = weight_bmi_list.size();
        
        if(num_rows!=0){
        	// Los índices en los arraylist empiezan en 0.
            WeightBMIObject w_bmi_obj = weight_bmi_list.get(num_rows-1);

    	    weight = w_bmi_obj.get_weight();
    	    
    	    txt_weight.setText(weight+" Kg");
        } else{
        	txt_weight.setText("Peso sin especificar");	
	    }
	       
    }
    
    public void manage_click_btn(Button btn, final Class<?> cls){
    	btn.setOnClickListener(new AdapterView.OnClickListener() {      
			@Override
			public void onClick(View v) {
				// Launch the Configuration Activity
                startActivity(new Intent(MonitoringActivity.this, cls));
			}
        });
    } 

    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case WEIGHT_DIALOG_ID:
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.weight_dialog, (ViewGroup) findViewById(R.id.root));
            final EditText ewd = (EditText) layout.findViewById(R.id.edit_weight_dialog);
            
            return new AlertDialog.Builder(MonitoringActivity.this)
            .setView(layout)
            .setTitle(R.string.new_weight)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                	// Coger datos y guardarlo en la bbdd.
                	Calendar cal = Calendar.getInstance(); 
                	long now = cal.getTimeInMillis();
                	long ms = cal.MILLISECOND;
                	long sec_ms = cal.SECOND * 1000;
                	long min_ms = cal.MINUTE * 60000;
                	long h_ms = cal.HOUR * 3600000;
                	long timestamp = now - h_ms - min_ms - sec_ms - ms;
                	
                	weight = Float.parseFloat(ewd.getText().toString());
                	
                	WeightBMIObject w_bmi_obj = new WeightBMIObject(weight, sp_tall, timestamp);
//                	
                	mDbHelper.insertWeightBMI(w_bmi_obj);

                    // We forcefully dismiss and remove the Dialog, so it
                    // cannot be used again
                    MonitoringActivity.this.removeDialog(WEIGHT_DIALOG_ID);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	MonitoringActivity.this.removeDialog(WEIGHT_DIALOG_ID);
                }
            })
            .create();
        }
		return null;
    }
}
