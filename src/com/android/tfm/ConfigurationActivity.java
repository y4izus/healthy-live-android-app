package com.android.tfm;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigurationActivity extends GeneralActivity{
	Button btn_pick_date;
	Button btn_ok;
	
	private Spinner spinner_gender;
	
	EditText edit_name;
	TextView txt_date_display;
	EditText edit_weight;
	EditText edit_tall;
	
	int nYear;
    int nMonth;
    int nDay;
     
    public static final int DATE_DIALOG_ID = 0;
    
    String name;
    String date_of_birth;
    String gender;
    float weight;
    float tall;
    
    private TfmDbAdapter mDbHelper;
    
	/** Método que se llama cuando la actividad se crea por primera vez */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        
        mDbHelper = new TfmDbAdapter(this);
        mDbHelper.open();
        
        initComponents();
  
        btn_pick_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkValues();
                saveSharedPreferences();
                saveFirstWeightBMI();
            }
        }); 
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                nYear = year;
                nMonth = monthOfYear;
                nDay = dayOfMonth;
                updateDisplay();
            }
        };
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        nYear, nMonth, nDay);
        }
        return null;
    }
    
    /**
     * Inicializa los componentes
     */  
    private void initComponents(){
    	
    	try{
    	
	        txt_date_display = (TextView) findViewById(R.id.txt_DOB_Info);
	        
	        btn_pick_date = (Button) findViewById(R.id.btn_DOB);
	        btn_ok = (Button) findViewById(R.id.btn_ok);
	        
	        edit_name = (EditText) findViewById(R.id.edit_name);
	        edit_weight = (EditText) findViewById(R.id.edit_weight);
	        edit_tall = (EditText) findViewById(R.id.edit_tall);
	
	        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.gender_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner_gender.setAdapter(adapter);
	        
	        // Antes de elegir fecha de nacimiento se muestra la fecha actual
	        final Calendar c = Calendar.getInstance();
	        nYear  = c.get(Calendar.YEAR);
	        nMonth = c.get(Calendar.MONTH);
	        nDay   = c.get(Calendar.DAY_OF_MONTH);
	        
    	}catch (Exception e){
    		Log.e(DEBUG_TAG, "InitComponents failure", e);
		}

        // display the current date
        updateDisplay();
    }

    /** 
     * Actualiza el display de la fecha del TextView
     */
    private void updateDisplay() {
        txt_date_display.setText(
            new StringBuilder()
                    // Los meses empiezan a contar en 0, por eso se suma 1
                    .append(nDay).append("-")
                    .append(nMonth + 1).append("-")
                    .append(nYear).append(" "));
    }
    
    /**
     * Comprueba si los valores introducidos son correctos.
     */
    private void checkValues(){
    	name = edit_name.getText().toString();
    	date_of_birth = txt_date_display.getText().toString();
    	gender = spinner_gender.toString();
    	weight = Float.parseFloat(edit_weight.getText().toString());
    	tall = Float.parseFloat(edit_tall.getText().toString());
    }
    
    /** 
     * Guarda los datos de configuración de usuario en las SharedPreferences
     */
    private void saveSharedPreferences(){
    	// Se asigna al SharedPreferences APP_PREFERENCES la propiedad de usar
    	// datos compartidos únicamente por esta aplicación (MODE_PRIVATE)
    	SharedPreferences settings = getSharedPreferences (APP_PREFERENCES, MODE_PRIVATE);
    	
    	// Se indica que se va a editar su valor
    	SharedPreferences.Editor editor = settings.edit();
    	
    	// Se guardan los valores que se desee
    	editor.putString(APP_PREFERENCES_NAME, name);
    	editor.putString(APP_PREFERENCES_DOB, date_of_birth);
    	editor.putString(APP_PREFERENCES_GENDER, gender);
    	editor.putFloat(APP_PREFERENCES_WEIGHT, weight);
    	editor.putFloat(APP_PREFERENCES_TALL, tall);
    	
    	// Actualizar las SharedPreferences
    	editor.commit();
    }

    private void saveFirstWeightBMI(){
    	Calendar cal = Calendar.getInstance(); 
    	long now = cal.getTimeInMillis();
    	long ms = cal.MILLISECOND;
    	long sec_ms = cal.SECOND * 1000;
    	long min_ms = cal.MINUTE * 60000;
    	long h_ms = cal.HOUR * 3600000;
    	long timestamp = now - h_ms - min_ms - sec_ms - ms;

    	WeightBMIObject w_bmi_obj = new WeightBMIObject(weight, tall, timestamp);
    	
    	mDbHelper.insertWeightBMI(w_bmi_obj);
    	
	    Toast.makeText(ConfigurationActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();

    }
}
