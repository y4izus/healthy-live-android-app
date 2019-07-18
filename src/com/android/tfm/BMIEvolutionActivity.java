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
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;

public class BMIEvolutionActivity extends Activity{
	
	private XYPlot mySimpleXYPlot;
	
	
	private LineAndPointFormatter lp_formatter_bmi;
	private LineAndPointFormatter lp_formatter_underweight;
	private LineAndPointFormatter lp_formatter_normal;
	private LineAndPointFormatter lp_formatter_preobesity;
	private LineAndPointFormatter lp_formatter_obesity;
	
	private TfmDbAdapter mDbHelper;
	
	/** Called when the activity is first created. */
    @SuppressWarnings("null")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi_evolution);
        
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
       
        makePlotPretty();
        
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
        
	    Toast.makeText(BMIEvolutionActivity.this, num_rows+"", Toast.LENGTH_SHORT).show();

        Number [] bmi_array = new Number [num_rows];
        Number [] timestamps   = new Number [num_rows];
        
        /* Valores de BMI según la World Health Organization
         * <18.5 		=> Extrema delgadez
         * 18.5 - 24.9 	=> Peso normal
         * 25 - 29.9	=> Pre-obeso
         * >=30 		=> Obeso*/
    	Number[] underweight = new Number [num_rows];
    	Number[] normal = new Number [num_rows];
    	Number[] preobesity = new Number [num_rows];
    	Number[] obesity = new Number [num_rows];
    	
  		
        for (int i=0 ; i<num_rows; i++){
        	bmi_array[i] = (Number)weight_bmi_list.get(i).get_bmi();    	
  			timestamps[i]   = (Number)weight_bmi_list.get(i).get_timestamp();
  			
  			underweight[i] = 18.5;
  			normal[i] = 24.9;
  			preobesity[i] = 29.9;
  			obesity[i] = 45;
        }
        
//        // Vector que recoge los imc
//        Number[] bmi_array = {16.3, 21, 26, 28, 30};
//        
//        // Vector que recoge las fechas
//        Number[] timestamps = {
//                978307200000L,  // 1/1/2001
//                1009843200000L, // 1/1/2002
//                1041379200000L, // 1/1/2003
//                1072915200000L, // 1/1/2004
//                1104537600000L  // 1/1/2005
//        };
        
        // OBESITY SERIE
        Paint line_fill_obesity = new Paint();
        line_fill_obesity.setColor(Color.RED);
        
        lp_formatter_obesity = new LineAndPointFormatter(
        		Color.RED,
        		Color.RED,
        		Color.RED);
        lp_formatter_obesity.setFillPaint(line_fill_obesity);
        lp_formatter_obesity.setVertexPaint(null);
        lp_formatter_obesity.getLinePaint().setShadowLayer(0, 0, 0, 0);
 
        mySimpleXYPlot.addSeries(new SimpleXYSeries(Arrays.asList(timestamps),
        		Arrays.asList(obesity),
                "Obeso"), lp_formatter_obesity);
        
        // PREOBESITY SERIE
        Paint line_fill_preobesity = new Paint();
        line_fill_preobesity.setColor(Color.YELLOW);

        lp_formatter_preobesity = new LineAndPointFormatter(
        		Color.YELLOW,
        		Color.YELLOW,
        		Color.YELLOW);
        lp_formatter_preobesity.setFillPaint(line_fill_preobesity);
        
        //Quitar los circulos de los puntos
        lp_formatter_preobesity.setVertexPaint(null);
        lp_formatter_preobesity.getLinePaint().setShadowLayer(0, 0, 0, 0);
 
        mySimpleXYPlot.addSeries(new SimpleXYSeries(Arrays.asList(timestamps),
        		Arrays.asList(preobesity),
                "Pre-Obeso"), lp_formatter_preobesity);
        
        
        // NORMAL SERIE
        Paint line_fill_normal = new Paint();
        line_fill_normal.setColor(Color.GREEN);
        
        lp_formatter_normal = new LineAndPointFormatter(
                Color.GREEN,
                Color.GREEN,
                Color.GREEN);
        lp_formatter_normal.setFillPaint(line_fill_normal);
        lp_formatter_normal.setVertexPaint(null);
        lp_formatter_normal.getLinePaint().setShadowLayer(0, 0, 0, 0);
 
        mySimpleXYPlot.addSeries(new SimpleXYSeries(Arrays.asList(timestamps),
        		Arrays.asList(normal),
                "Normal"), lp_formatter_normal);
        
        
        // UNDERWEIGHT SERIE
        
        Paint line_fill_underweight = new Paint();
        line_fill_underweight.setColor(Color.GRAY);
        
        lp_formatter_underweight = new LineAndPointFormatter(
                Color.GRAY,
                Color.GRAY,
                Color.GRAY);
        lp_formatter_underweight.setFillPaint(line_fill_underweight);
        lp_formatter_underweight.setVertexPaint(null);
        lp_formatter_underweight.getLinePaint().setShadowLayer(0, 0, 0, 0);
 
        mySimpleXYPlot.addSeries(new SimpleXYSeries(Arrays.asList(timestamps),
        		Arrays.asList(underweight),
                "Poco Peso"), lp_formatter_underweight);
        
     // BMI SERIE:
        lp_formatter_bmi = new LineAndPointFormatter(
                Color.BLACK,
                Color.BLACK,
                Color.BLACK);
        lp_formatter_bmi.setFillPaint(null);
        
        lp_formatter_bmi.getLinePaint().setShadowLayer(0, 0, 0, 0);
 
 
        mySimpleXYPlot.addSeries(new SimpleXYSeries(Arrays.asList(timestamps),
        		Arrays.asList(bmi_array),
                "IMC"), lp_formatter_bmi);
        
        // draw a domain tick for each year:
        mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, timestamps.length);
 
        // customize our domain/range labels
        mySimpleXYPlot.setDomainLabel("Fecha");
        mySimpleXYPlot.setRangeLabel("IMC");
 
        // get rid of decimal points in our range labels:
        mySimpleXYPlot.setRangeValueFormat(new DecimalFormat("##.#"));
 
        mySimpleXYPlot.setDomainValueFormat(new MyDateFormat());
 
        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();
    }
 
    /**
     * Cleans up the plot's general layout and color scheme
     */
    private void makePlotPretty() {
    	// Creación de la leyenda
        mySimpleXYPlot.getLegendWidget().setTableModel(new DynamicTableModel(5, 1));
        
        // Añade un fondo semitransparente negro a la leyenda para facilitar
        // su visualización
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(140);
        mySimpleXYPlot.getLegendWidget().setBackgroundPaint(bgPaint);
        
        // adjust the padding of the legend widget to look a little nicer:
        mySimpleXYPlot.getLegendWidget().setPadding(10, 1, 1, 1);
 
        // adjust the legend size so there is enough room
        // to draw the new legend grid:
        mySimpleXYPlot.getLegendWidget().setSize(new SizeMetrics(20, SizeLayoutType.ABSOLUTE, 375, SizeLayoutType.ABSOLUTE));
 
        // reposition the grid so that it rests above the bottom-left
        // edge of the graph widget:
        mySimpleXYPlot.position(
        		mySimpleXYPlot.getLegendWidget(),
                20,
                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
                35,
                YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.RIGHT_TOP);
        
        // Poner fondo en blanco y lineas de grid discontinuas
        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        mySimpleXYPlot.getBorderPaint().setStrokeWidth(1);
        mySimpleXYPlot.getBorderPaint().setAntiAlias(false);
        mySimpleXYPlot.getBorderPaint().setColor(Color.WHITE);
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
