package com.android.tfm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

public class AlarmReceiverDay extends BroadcastReceiver {

//	public String title = "FFT    MEDIA    DESV_ESTANDAR" + "\r\n";
	
	private static final int HELLO_ID = 1;
	
	private static final String REST = "reposo";
	private static final String MODERATE = "moderado";
	private static final String INTENSIVE = "intensivo";
	
	private int num_min_rest = 0;
	private int num_min_moderate = 0;
	private int num_min_intensive = 0;
	
	private int tt_moderate = 0;
	private int tt_intensive = 0;
	
	int nYear;
    int nMonth;
    int nDay;
    
    String date_day_activity;
    int mission_day_accomplished;
    int mission_week_accomplished;
    long fk_week;
	
	private static CharSequence msg1_title = "¡ENHORABUENA!";
	private static CharSequence msg2_title = "¡BUEN TRABAJO!";
	private static CharSequence msg3_title = "Esta vez no pudo ser...";
	 
	private static CharSequence msg1_txt = "¡Has superado el objetivo de esta semana!";
	private static CharSequence msg2_txt = "¡Falta menos para conseguir el reto, sigue así!";
	private static CharSequence msg3_txt = "¡No te desanimes! Invierte más tiempo esta semana y lo conseguirás ;)";
	
	/*
	 * Se fijan los siguientes valores como objetivos (En minutos).
	 * >OBESO
	 * >> Moderado: 3,5h semanales (210 min) => 30 min diarios
	 * >> Inteso: 0h semanales
	 * >PREOBESO
	 * >> Moderado: 3,5h/s (210min) => 30min/d
	 * >> Intenso: 1h 10min/s (70min) => 10min/d
	 * >NORMAL
	 * >> Moderado: 3,5h/s (210min) => 30min/d
	 * >> Intenso: 1h 10min/s (70min) => 10min/d
	 * >POCO PESO
	 * >> Moderado: (105min) => 15min/d
	 * >> Intenso: (35min) => 5min/d
	 */
	int aim_week_moderate_obesity     = 210;
	int aim_week_intensive_obesity    = 0;
	int aim_day_moderate_obesity      = 30;
	int aim_day_intensive_obesity     = 0;
	
	int aim_week_moderate_preobesity  = 210;
	int aim_week_intensive_preobesity = 70;
	int aim_day_moderate_preobesity   = 30;
	int aim_day_intensive_preobesity  = 10;
	
	int aim_week_moderate_normal      = 210;
	int aim_week_intensive_normal     = 70;
	int aim_day_moderate_normal       = 30;
	int aim_day_intensive_normal      = 10;
	
	int aim_week_moderate_prenormal   = 105;
	int aim_week_intensive_prenormal  = 35;
	int aim_day_moderate_prenormal    = 15;
	int aim_day_intensive_prenormal   = 5;

	private TfmDbAdapter mDbHelper;
	
 @Override
 public void onReceive(Context context, Intent intent) {
	 try {
		 mDbHelper = new TfmDbAdapter(context);
	     mDbHelper.open();
	        
//		 create_clasification_file();
		 ArrayList<PreprocessingObject> preprocessing_list = 
				new ArrayList<PreprocessingObject>();
		 preprocessing_list = AlarmReceiverMinute.preprocessing_list;
		 
		 calculate_num_min_daily_activity(preprocessing_list);
		 
		 insert_daily_activity(context);
		 
		 update_week_activity(context);
		 
		 manage_notifications(context);
		 
		 // Una vez guardados y gestionados los datos, se resetea el array.
		 AlarmReceiverMinute.preprocessing_list.clear();

	 	} catch (Exception e) {
	    	Toast.makeText(context, "There was an error somewhere, but we still received a daily alarm", Toast.LENGTH_SHORT).show();
	    	e.printStackTrace();
	    }
 	}
 
 	/**
 	 * Método que según los objetivos cumplidos envía unas notificaciones u
 	 * otras.
 	 * @param context
 	 */
 	private void manage_notifications(Context context) {
 	// La semana en la que estamos es la última de la bbdd.
		Cursor c = mDbHelper.fetchAllWeekActivities();
		int num_rows = c.getCount();
		c.moveToPosition(num_rows-1);
		
		int week_aim_moderate  = c.getInt(6);
		int week_aim_intensive = c.getInt(7);
		
		c.close();
		
		//Se coge la fecha
 		Calendar cal = Calendar.getInstance();
        
 		// Si no es domingo se mira si se ha superado el 50% del reto. Si es
 		// así se envía una notificación de ánimo. Sino nada.
 		// Si es domingo, se mira si se ha cumplido el obj semanal, si se ha 
 		// cumplido se envía un mensaje de enhorabuena y sino uno de ánimo
 		// de cara a la siguiente semana. Además de esto se calcula el nuevo
 		// IMC y se fijan los obj para la nueva semana.
 		if(!(cal.DAY_OF_WEEK == cal.MONDAY)){
 			if(tt_moderate  >= week_aim_moderate *0.5 ||
 			   tt_intensive >= week_aim_intensive*0.5)
 				instantiate_notification(msg2_title, msg2_txt, context);	   
 		} else{
 			if(tt_moderate  >= week_aim_moderate ||
 			   tt_intensive >= week_aim_intensive)
 				instantiate_notification(msg1_title, msg1_txt, context);
 			else instantiate_notification(msg3_title, msg3_txt, context);
 			
 			float new_bmi = calculate_new_bmi();
 			create_new_week_activity(new_bmi);	
 		}
 	}

 	/**
 	 * Método que inicializa los objetivos de la nueva semana en función
 	 * del último IMC que se tenga.
 	 * @param new_bmi
 	 */
 	private void create_new_week_activity(float new_bmi) {
 		// Según el IMC se fijan unos objetivos
 		// 18.5=< IMC < 25 => NORMAL
 		// 25  =< IMC < 30 => PREOBESO
 		// 30  =< IMC      => OBESO
 		String next_day = (nDay+1) + "/" + (nMonth +1) + "/" + nYear;
 		if(new_bmi < 18.5)
 			mDbHelper.insertWeekActivity(0, 0,
 										 aim_day_moderate_prenormal,
 										 aim_day_intensive_prenormal,
 										 aim_week_moderate_prenormal,
 										 aim_week_intensive_prenormal,
 										 0, next_day);
 		if(new_bmi >= 18.5 && new_bmi < 25)
 			mDbHelper.insertWeekActivity(0, 0,
 										 aim_day_moderate_normal,
 										 aim_day_intensive_normal,
 										 aim_week_moderate_normal,
 										 aim_week_intensive_normal,
 										 0, next_day);
 		if(new_bmi >= 25 && new_bmi < 30)
 			mDbHelper.insertWeekActivity(0, 0,
 										 aim_day_moderate_preobesity,
 										 aim_day_intensive_preobesity,
 										 aim_week_moderate_preobesity,
 										 aim_week_intensive_preobesity,
 										 0, next_day);
 		if(new_bmi >= 30)
 			mDbHelper.insertWeekActivity(0, 0,
 										 aim_day_moderate_obesity,
 										 aim_day_intensive_obesity,
 										 aim_week_moderate_obesity,
 										 aim_week_intensive_obesity,
 										 0, next_day);	
	}

	/**
 	 * Recoge el último valor del IMC que es el que determina los objetivos
 	 * semanales.
 	 * 
 	 * @return el último bmi de la tabla.
 	 */
 	private float calculate_new_bmi() {
 		// Nos interesa el ultimo peso de la bbdd
		Cursor c = mDbHelper.fetchAllWeightBMI();
		int num_rows = c.getCount();
		c.moveToPosition(num_rows-1);
		
		float new_bmi = c.getFloat(2);
		
		c.close();
		
		return new_bmi;
	}

	
 	/**
 	 * Método que actualiza diariamente el progreso de actividad de la semana.
 	 */
	private void update_week_activity(Context context) {
 		// La semana en la que estamos es la última de la bbdd.
		Cursor c = mDbHelper.fetchAllWeekActivities();
		int num_rows = c.getCount();
		c.moveToPosition(num_rows-1);
		
		tt_moderate  =  num_min_moderate  + c.getInt(2);
		tt_intensive =  num_min_intensive + c.getInt(3);
 		
		mission_week_accomplished = is_mission_week_accomplished();
		
	    Toast.makeText(context, tt_moderate+"", Toast.LENGTH_SHORT).show();
	    Toast.makeText(context, tt_intensive+"", Toast.LENGTH_SHORT).show();

		
 		mDbHelper.updateWeekActivity(num_rows, 
				   					 tt_moderate, 
				   					 tt_intensive,
				   					 c.getInt(4) ,
				   					 c.getInt(5) ,
				   					 c.getInt(6) ,
				   					 c.getInt(7) ,
				   					 mission_week_accomplished,
				   					 c.getString(1));
 		c.close();
	
 	}
	
	/**
	 * Comprueba si se ha cumplido el objetivo semanal.
	 * @return 0 sino se ha cumplido, 1 si se ha cumplido.
	 */

	private int is_mission_week_accomplished() {
		// Para saber si el objetivo semanal se ha cumplido, mirar obj 
		// fijado en la tabla semanal y compararlo con el obtenido
		// en el total semanal.
		
		// La semana en la que estamos es la última de la bbdd.
		Cursor c = mDbHelper.fetchAllWeekActivities();
		int num_rows = c.getCount();
		c.moveToPosition(num_rows-1);
		int week_aim_moderate  = (int) c.getLong(6);
		int week_aim_intensive = (int) c.getLong(7);
		c.close();
		
		if (tt_moderate  >= week_aim_moderate || 
			tt_intensive >= week_aim_intensive)
			return 1;
		else return 0;
	}

	
	/**
 	 * Método que inserta el resumen de la actividad diaria en la bbdd.
 	 */
 	private void insert_daily_activity(Context context) {
 		//Se coge la fecha
 		Calendar cal = Calendar.getInstance();
        nYear  = cal.get(Calendar.YEAR);
        nMonth = cal.get(Calendar.MONTH);
        nDay   = cal.get(Calendar.DAY_OF_MONTH);
        
        // Los meses empiezan a contar en 0, por eso se suma 1
        date_day_activity = nDay + "/" + (nMonth +1) + "/" + nYear;
        
        Toast.makeText(context, "antes de mision cumplida", Toast.LENGTH_SHORT).show();
        
        // Ver si el objetivo se ha cumplido
//        mission_day_accomplished = is_day_mission_accomplished(context);
        
        mission_day_accomplished = 0;
        // Coger el id de la ultima fila de la tabla week_activity
        Cursor c = mDbHelper.fetchAllWeekActivities();
        fk_week = c.getCount();
        
//        c.close();
        
        // Se inserta en la bbdd
        mDbHelper.insertDailyActivity(num_min_rest, 
        							  num_min_moderate,
        							  num_min_intensive,
        							  mission_day_accomplished,
        							  fk_week,
        							  date_day_activity);
 	}
 	
 	/**
 	 * Método que comprueba si el objetivo se ha cumplido o no.
 	 * 
 	 * @return 0 si no se ha cumplido
 	 * 		   1 si se ha cumplido
 	 */

	private int is_day_mission_accomplished(Context context) {
		// Para saber si el objetivo diario se ha cumplido, mirar obj 
		// diario fijado en la tabla semanal y compararlo con el obtenido
		// en el dia.
		
		// La semana en la que estamos es la última de la bbdd.
		Toast.makeText(context, "antes de coger valores", Toast.LENGTH_SHORT).show();
		Cursor c = mDbHelper.fetchAllWeekActivities();
		Toast.makeText(context, "antes de ir a ultimo", Toast.LENGTH_SHORT).show();
		
		c.moveToLast();
		
		int daily_aim_moderate  = (int) c.getLong(4);
		int daily_aim_intensive = (int) c.getLong(5);
		c.close();
		
		if (num_min_moderate  >= daily_aim_moderate || 
			num_min_intensive >= daily_aim_intensive)
			return 1;
		else return 0;
	}

	/**
 	 * Método que calcula los minutos de cada actividad realizados en un día
 	 * 
 	 * @param preprocessing_list lista con la actividad de cada minuto
 	 */
 	public void calculate_num_min_daily_activity(ArrayList<PreprocessingObject> preprocessing_list){
 		for (int i=0; i<preprocessing_list.size(); i++){
			 if (preprocessing_list.get(i).activity_type == REST)
				 num_min_rest ++;
			 if (preprocessing_list.get(i).activity_type == MODERATE)
				 num_min_moderate ++;
			 if (preprocessing_list.get(i).activity_type == INTENSIVE)
				 num_min_intensive ++;	 
		 }
 	}
 
 	public void instantiate_notification(CharSequence title, CharSequence text, Context context){
 		// Instantiate the Notification:
 		int icon = R.drawable.icon;
		CharSequence tickerText = "Notificación";
		long when = System.currentTimeMillis();
		 
		// Get a reference to the NotificationManager:
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
	
		Notification notification = new Notification(icon, tickerText, when);
		  
		notification.defaults |= Notification.DEFAULT_SOUND;
		   
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		 
		// Define the Notification's expanded message and Intent:
		CharSequence contentTitle = title;
		CharSequence contentText = text;
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		   
		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(HELLO_ID, notification); 
 	}
}