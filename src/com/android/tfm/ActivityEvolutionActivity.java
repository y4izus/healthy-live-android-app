package com.android.tfm;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityEvolutionActivity extends Activity{
	
	private TfmDbAdapter mDbHelper;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_evolution);
        
        mDbHelper = new TfmDbAdapter(this);
	    mDbHelper.open();
        
        // Retrieve the TableLayout references
        TableLayout activities_table = (TableLayout) findViewById(R.id.table_activities);
        
        // Give each TableLayout a yellow header row with the column names
        initializeHeaderRow(activities_table);
        
//        XmlResourceParser mock_activities = getResources().getXml(R.xml.activity_table);
     	
        try {
//            processScores(activities_table, mock_activities);
        	processScores(activities_table);
          
        } catch (Exception e) {
            Log.e("TFM Log", "Failed to load activities", e);
        }
        
    }
    
    /**
     * Add a header {@code TableRow} to the {@code TableLayout} (styled)
     * 
     * @param scoreTable
     *            the {@code TableLayout} that the header row will be added to
     */
    private void initializeHeaderRow(TableLayout activities_table) {
        // Create the Table header row
        TableRow headerRow = new TableRow(this);
        int textColor = getResources().getColor(R.color.logo_color);
        float textSize = getResources().getDimension(R.dimen.help_text_size);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.date), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.aim), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.success), textColor, textSize);
        activities_table.addView(headerRow);
    }

    /**
     * Método que lee los valores de la base de datos y los muestra en la
     * tabla de la interfaz.
     */
    private void processScores(final TableLayout activities_table) {
    	// Sólo se van a mostrar los resultados de la semana en curso.
    	int id_last_week = 0;
    	
    	Cursor c_week = mDbHelper.fetchAllWeekActivities();
    	if (c_week != null) {
    		id_last_week = c_week.getCount();
    		c_week.moveToPosition(id_last_week);
    		
    		Cursor c_day  = mDbHelper.fetchAllDailyActivities();
    		
    		//Iteramos a traves de los registros del cursor
            if (c_day != null) {
            	c_day.moveToLast();
            	while (c_day.getLong(6) == id_last_week) {
            		String activity_date = c_day.getString(1);
    				String activity_aim = c_week.getInt(4) + "min mod o " 
    									+ c_week.getInt(5) + "min int";
    				int day_mission_accomplished = c_day.getInt(5);
    				String activity_success;
    				if(day_mission_accomplished == 1) 
    					activity_success = "SI";
    				else activity_success = "NO";
    				insertScoreRow(activities_table, activity_date, activity_aim, activity_success);
                    c_day.moveToPrevious();
                 }
            	 c_day.close();
            } 
            String activity_date = "SEMANA";
			String activity_aim = c_week.getInt(6) + "min mod o " 
								+ c_week.getInt(7) + "min int";
			int week_mission_accomplished = c_week.getInt(8);
			String activity_success;
			if(week_mission_accomplished == 1) 
				activity_success= "SI";
			else activity_success = "NO";
			insertScoreRow(activities_table, activity_date, activity_aim, activity_success);
			
    	} else {
    		final TableRow newRow = new TableRow(this);
    		TextView noResults = new TextView(this);
    		noResults.setText(getResources().getString(R.string.no_activities));
    		newRow.addView(noResults);
    		activities_table.addView(newRow);
    	}
    }
    
    /**
     * {@code processScores()} helper method -- Inserts a new score {@code
     * TableRow} in the {@code TableLayout}
     * 
     * @param activities_table
     * @param activity_date
     * @param activity_aim
     * @param activity_success
     */
    private void insertScoreRow(final TableLayout activities_table, String activity_date, String activity_aim, String activity_success) {
        final TableRow newRow = new TableRow(this);
        int textColor = getResources().getColor(R.color.title_color);
        float textSize = getResources().getDimension(R.dimen.help_text_size);
        addTextToRowWithValues(newRow, activity_date, textColor, textSize);
        addTextToRowWithValues(newRow, activity_aim, textColor, textSize);
        addTextToRowWithValues(newRow, activity_success, textColor, textSize);
        activities_table.addView(newRow);
    }

    /**
     * {@code insertScoreRow()} helper method -- Populate a {@code TableRow} with
     * three columns of {@code TextView} data (styled)
     * 
     * @param tableRow
     *            The {@code TableRow} the text is being added to
     * @param text
     *            The text to add
     * @param textColor
     *            The color to make the text
     * @param textSize
     *            The size to make the text
     */
    private void addTextToRowWithValues(final TableRow tableRow, String text, int textColor, float textSize) {
        TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setText(text);
        tableRow.addView(textView);
    }   
}
