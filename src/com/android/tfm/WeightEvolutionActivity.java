package com.android.tfm;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class WeightEvolutionActivity extends Activity
{
 
    private XYPlot mySimpleXYPlot;
    
    private TfmDbAdapter mDbHelper;
 
    @SuppressWarnings("null")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_evolution);
        
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        
        mDbHelper = new TfmDbAdapter(this);
        mDbHelper.open();

     // Leer peso de la bbdd
        ArrayList<WeightBMIObject> weight_bmi_list = new ArrayList<WeightBMIObject>();
        
        Cursor c = mDbHelper.fetchAllWeightBMI();
        
        //Iteramos a traves de los registros del cursor
        if (c != null) {
        	c.moveToFirst();
        	while (c.isAfterLast() == false) {
            	WeightBMIObject weight_bmi = new WeightBMIObject();
            	weight_bmi.set_weight(c.getFloat(1));
            	weight_bmi.set_bmi(c.getFloat(2));
            	weight_bmi.set_timestamp(c.getLong(3));
                weight_bmi_list.add(weight_bmi); 
                c.moveToNext();
             }
             c.close();
        } else c.close();
        mDbHelper.close();
        int num_rows = weight_bmi_list.size();
        
	    Toast.makeText(WeightEvolutionActivity.this, num_rows+"", Toast.LENGTH_SHORT).show();

        Number [] weight_array = new Number [num_rows];
        Number [] timestamps   = new Number [num_rows];
  		
        for (int i=0 ; i<num_rows; i++){
        	weight_array[i] = (Number)weight_bmi_list.get(i).get_weight();
        	
  			timestamps[i]   = (Number)weight_bmi_list.get(i).get_timestamp();
        }
        
//        // Vector que recoge los pesos
//        Number[] weight_array = {74.3, 74.5, 74.4, 74.6, 74.5};
//        
//        // Vector que recoge las fechas
//        Number[] timestamps = {
//                978307200000L,  // 1/1/2001
//                1009843200000L, // 1/1/2002
//                1041379200000L, // 1/1/2003
//                1072915200000L, // 1/1/2004
//                1104537600000L  // 1/1/2005
//        };
 
        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(timestamps),
                Arrays.asList(weight_array),
                "Peso según Fecha");
 		
        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        mySimpleXYPlot.getBorderPaint().setStrokeWidth(1);
        mySimpleXYPlot.getBorderPaint().setAntiAlias(false);
        mySimpleXYPlot.getBorderPaint().setColor(Color.WHITE);
 
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        formatter.setFillPaint(lineFill);
        mySimpleXYPlot.getGraphWidget().setPaddingRight(2);
        mySimpleXYPlot.addSeries(series2, formatter);
 
        // draw a domain tick for each year:
        mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, timestamps.length);
 
        // customize our domain/range labels
        mySimpleXYPlot.setDomainLabel("Fecha");
        mySimpleXYPlot.setRangeLabel("Peso en Kg");
 
        // get rid of decimal points in our range labels:
        mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("###.#"));
 
        mySimpleXYPlot.setDomainValueFormat(new MyDateFormat());
 
        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();
    }
 
    private class MyDateFormat extends Format {
    	// create a simple date format that draws on the year portion of our timestamp.
        // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
        // for a full description of SimpleDateFormat.
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
 
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            long timestamp = ((Number) obj).longValue();
            Date date = new Date(timestamp);
            return dateFormat.format(date, toAppendTo, pos);
        }
 
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;
        }
    }
}