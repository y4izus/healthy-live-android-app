package com.android.tfm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class AccelerometerService extends Service {

	private float [] gravity = new float [3];
	private float [] linear_acceleration = new float [3];
	
	public static ArrayList<MeasureObject> measure_list = new ArrayList<MeasureObject>();
	
	private int ALARM_REQUEST_CODE_DAY = 75;
	private int ALARM_REQUEST_CODE_MINUTE = 76;
	
	@Override
	public void onCreate(){
		// TODO: Actions to perform when service is created.
		
		// Inicializar Sensor
		initSensor();
		
		// Inicializar las alarmas 
		startAlarm();
	}
	
	/**
	 * Método que inicializa y registra el sensor.
	 */
	public void initSensor(){
		// Obtener la referencia al Sensor
		SensorManager s_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// Se va a usar el acelerómetro
		Sensor accelerometer_sensor = s_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		// Registrar sensor
		s_manager.registerListener(mySensorEventListener, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * Método que inicializa la alarma para que todos los días a la misma hora 
	 * se ejecute la misma operación.
	 */
	public void startAlarm(){
		// Se quiere que se ejecute cada día a las 23:55
		// 23:59 is the last minute of the day and 00:00 is the first minute of the next day
		Calendar cal = Calendar.getInstance(); 
	    cal.set(Calendar.HOUR_OF_DAY, 18); 
	    cal.set(Calendar.MINUTE, 43); 
	    cal.set(Calendar.SECOND, 0); 
 
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    
	    Intent intent_day    = new Intent(this, AlarmReceiverDay.class);
	    Intent intent_minute = new Intent(this, AlarmReceiverMinute.class);
	    
	    PendingIntent pending_intent_day    = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE_DAY, intent_day, 1);
	    PendingIntent pending_intent_minute = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE_MINUTE, intent_minute, 1);

	    // Alarma cada 60000 segundos => 1 minuto
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),60000,pending_intent_minute);
	    
	    // Alarma cada 86400000 segundos => 24h
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),120000,pending_intent_day);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Launch a background thread to do processing.
		if ((flags & START_FLAG_RETRY) == 0) {
			// TODO If it’s a restart, do something.
		}
		else {
			// TODO Alternative background process.
		}
		// Se quiere que el Servicio continue corriendo hasta que se pare
		// explicitamente, por eso se devuelve STICKY
		return Service.START_STICKY;
	}


	final SensorEventListener mySensorEventListener = new SensorEventListener(){
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			
			// Eliminar la influencia de la gravedad en la medicion de la 
			// aceleracion.
			// alpha is calculated as t / (t + dT) with t, the low-pass filter's 
			// time-constant and dT, the event delivery rate

	        final float alpha = (float) 0.8;

	        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
	        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
	        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

	        linear_acceleration[0] = event.values[0] - gravity[0];
	        linear_acceleration[1] = event.values[1] - gravity[1];
	        linear_acceleration[2] = event.values[2] - gravity[2];
	   
	        // Se crea el objeto con los datos y se guarda en el ArrayList
	        // correspondiente
	        MeasureObject mObj = new MeasureObject(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
	        measure_list.add(mObj);
		}
	};
}
