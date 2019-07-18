package com.android.tfm;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiverMinute extends BroadcastReceiver {

	// Valores del PreprocessingObject
//	public float fft = 1;
	public float mean;
	public float std_dev;
	public String activity_type;
	
	public float threshold_rest_moderate = (float)0.5354;
	public float threshold_moderate_intensive = (float)1.9241;
	
	public static ArrayList<PreprocessingObject> preprocessing_list = 
			new ArrayList<PreprocessingObject>();

 @Override
 public void onReceive(Context context, Intent intent) {
	 try {
		
		 mean = calculate_mean();
     	
     	 std_dev = (float) Math.sqrt(calculate_acc_variance());
     	 
     	 activity_type = calculate_activity();
     	
     	 PreprocessingObject pObj = new PreprocessingObject(mean, std_dev, activity_type);
     	 preprocessing_list.add(pObj);
     	 
     	Toast.makeText(context, activity_type, Toast.LENGTH_SHORT).show();
     	
//     	 Calendar calendario = Calendar.getInstance();
//     	 Toast.makeText(AccelerometerService.this, calendario.get(Calendar.MINUTE)+" "+ calendario.get(Calendar.SECOND)+" "+ calendario.get(Calendar.MILLISECOND), Toast.LENGTH_SHORT).show();
     	
     	 AccelerometerService.measure_list.clear();
	
		
	 	} catch (Exception e) {
	    	Toast.makeText(context, "There was an error somewhere, but we still received an alarm per minute", Toast.LENGTH_SHORT).show();
	    	e.printStackTrace();
	    }
 	}
 
 	/**
	 * Función que calcula la media de la aceleración absoluta.
	 * @return
	 */
	public float calculate_mean(){
		float measure_mean = 0;
		
		// Se devuelven todos los valores del ArrayList
		Iterator<MeasureObject> iterador = AccelerometerService.measure_list.listIterator();
		
		//Mientras que el iterador tenga un proximo elemento
		while( iterador.hasNext() ) {
			MeasureObject mObj = (MeasureObject) iterador.next(); 
			
			measure_mean += mObj.function_xyz;		
		}
		return measure_mean/AccelerometerService.measure_list.size();
	}

	/**
	 * Función que calcula la varianza de la aceleración.
	 * @return
	 */
	public float calculate_acc_variance(){
		float measure_acc_var = 0;
		
		// Se devuelven todos los valores del ArrayList
		Iterator<MeasureObject> iterador = AccelerometerService.measure_list.listIterator();
		
		//Mientras que el iterador tenga un proximo elemento
		while( iterador.hasNext() ) {
			MeasureObject mObj = (MeasureObject) iterador.next(); 
			
			measure_acc_var += Math.pow(mObj.function_xyz-mean, 2);		
		}
		return measure_acc_var/AccelerometerService.measure_list.size();
	}
	
	/**
	 * Función que calcula la actividad realizada.
	 * @return
	 */
	public String calculate_activity(){
		if(std_dev < threshold_rest_moderate)
     		return "reposo";
     	 else
     		 if(std_dev > threshold_moderate_intensive)
     			return "intensivo";
     		 else return "moderado";
	}


}