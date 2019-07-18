package com.android.tfm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple tfm database access helper class. Defines the basic CRUD operations
 * and gives the ability to list all information as well as retrieve or modify 
 * a specific information.
 */
public class TfmDbAdapter {

	// Datos de la bbdd
	private static final String DATABASE_NAME 	  		= "data2.db";
    private static final String DB_TABLE_WEIGHT   		= "weight_bmi";
    private static final String DB_TABLE_DAILY_ACTIVITY = "daily_activity";
    private static final String DB_TABLE_WEEK_ACTIVITY 	= "week_activity";
    private static final int 	DATABASE_VERSION  		= 2;
    
	// Tabla peso_imc
    public static final String KEY_WEIGHT 	   	  = "weight";
    public static final String KEY_BMI 	       	  = "bmi";
    public static final String KEY_DATE_WEIGHT 	  = "date_weight";
    
    
    // Tabla actividad diaria
    public static final String KEY_DATE_ACTIVITY  			= "date_activity";
    public static final String KEY_REST_TIME      			= "rest_time_min";
    public static final String KEY_MODERATE_TIME  			= "moderate_time_min";
    public static final String KEY_INTENSIVE_TIME			= "intensive_time_min";
    public static final String KEY_MISSION_ACCOMPLISHED_DAY = "mission_accomplished_day";
    public static final String KEY_WEEK 					= "fk_week_activity";
  
    // Tabla actividad semanal
    public static final String KEY_FIRST_DAY_WEEK  			 = "first_day_week";
    public static final String KEY_TOTAL_TIME_MODERATE		 = "total_moderate";
    public static final String KEY_TOTAL_TIME_INTENSIVE		 = "total_intensive";
    public static final String KEY_AIM_DAY_MODERATE 		= "aim_day_moderate";
    public static final String KEY_AIM_DAY_INTENSIVE 		= "aim_day_intensive";
    public static final String KEY_AIM_WEEK_MODERATE	     = "aim_week_moderate";
    public static final String KEY_AIM_WEEK_INTENSIVE	     = "aim_week_intensive";
    public static final String KEY_MISSION_ACCOMPLISHED_WEEK = "mission_accomplished_week";
 
    // Campo compartido por ambas tablas
    public static final String KEY_ROWID = "_id";
    
    private static final String TAG 	 = "TfmDbAdapter";
    private DbOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String TABLE_WEIGHT_CREATE =
        "create table "+ DB_TABLE_WEIGHT + " ("
    	+ KEY_ROWID 	    + " integer primary key autoincrement, "
        + KEY_WEIGHT 	    + " double not null, "
        + KEY_BMI 	 	    + " double not null, "
        + KEY_DATE_WEIGHT   + " datetime not null);";
    
    private static final String TABLE_WEEK_ACTIVITY_CREATE =
        "create table "+ DB_TABLE_WEEK_ACTIVITY + " ("
    	+ KEY_ROWID 	    			+ " integer primary key autoincrement, "
        + KEY_FIRST_DAY_WEEK 	    	+ " text not null, "
        + KEY_TOTAL_TIME_MODERATE 		+ " integer not null, "
        + KEY_TOTAL_TIME_INTENSIVE 		+ " integer not null, "
        + KEY_AIM_DAY_MODERATE			+ " integer not null, "
        + KEY_AIM_DAY_INTENSIVE			+ " integer not null, "
        + KEY_AIM_WEEK_MODERATE 		+ " integer not null, "
    	+ KEY_AIM_WEEK_INTENSIVE 		+ " integer not null, "
    	+ KEY_MISSION_ACCOMPLISHED_WEEK + " integer not null);";
    
    private static final String TABLE_DAILY_ACTIVITY_CREATE =
        "create table "+ DB_TABLE_DAILY_ACTIVITY + " ("
    	+ KEY_ROWID 	    			+ " integer primary key autoincrement, "
    	+ KEY_DATE_ACTIVITY 			+ " text not null,"
        + KEY_REST_TIME 	    		+ " integer not null, "
        + KEY_MODERATE_TIME 			+ " integer not null, "
        + KEY_INTENSIVE_TIME 			+ " integer not null, "
        + KEY_MISSION_ACCOMPLISHED_DAY 	+ " integer not null, " 
        + "FOREIGN KEY ("+KEY_WEEK+") REFERENCES "
        + TABLE_WEEK_ACTIVITY_CREATE+" ("+KEY_ROWID+"));";
  
    private static final String DATABASE_CREATE =
    	TABLE_WEIGHT_CREATE + TABLE_WEEK_ACTIVITY_CREATE + TABLE_DAILY_ACTIVITY_CREATE;

    private final Context mCtx;

    private static class DbOpenHelper extends SQLiteOpenHelper {

    	DbOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_WEIGHT+";");
            db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_DAILY_ACTIVITY+";");
            db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_WEEK_ACTIVITY+";");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TfmDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new instance 
     * of the database. If it cannot be created, throw an exception to signal 
     * the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TfmDbAdapter open() throws SQLException {
        mDbHelper = new DbOpenHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new weight_bmi using the weight, bmi and date provided. If the 
     * weight_bmi is successfully created return the new rowId for that note, 
     * otherwise return a -1 to indicate failure.
     *  
     * @param weight
     * @param bmi
     * @param date_weight
     * @return rowId or -1 if failed
     */
    public long insertWeightBMI(WeightBMIObject w_bmi_obj) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WEIGHT	 , w_bmi_obj.get_weight());
        initialValues.put(KEY_BMI		 , w_bmi_obj.get_bmi());
        initialValues.put(KEY_DATE_WEIGHT, w_bmi_obj.get_timestamp());
       
        return mDb.insert(DB_TABLE_WEIGHT, null, initialValues);
    }
    
   /**
    * Create a new daily activity. If the weight_bmi is successfully created 
    * return the new rowId for that note, otherwise return a -1 to indicate 
    * failure.
    * 
    * @param rowId
    * @param rest_time
    * @param moderate_time
    * @param intensive_time
    * @param aim_moderate
    * @param aim_intensive
    * @param mission_accomplished
    * @param fk_week
    * @param date_activity
    * @return
    */
    public long insertDailyActivity(int rest_time, 
			   						int moderate_time,
			   						int intensive_time,
			   						int mission_accomplished,
			   						long fk_week,
			   						String date_activity) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_REST_TIME	    		   , rest_time);
        initialValues.put(KEY_MODERATE_TIME  		   , moderate_time);
        initialValues.put(KEY_INTENSIVE_TIME		   , intensive_time);
        initialValues.put(KEY_DATE_ACTIVITY  		   , date_activity);
        initialValues.put(KEY_MISSION_ACCOMPLISHED_DAY , mission_accomplished);
        initialValues.put(KEY_WEEK                     , fk_week);
       
        return mDb.insert(DB_TABLE_DAILY_ACTIVITY, null, initialValues);
    }

    /**
     * Create a new week activity.
     * 
     * @param rowId
     * @param tt_moderate
     * @param tt_intensive
     * @param aim_moderate
     * @param aim_intensive
     * @param mission_accomplished
     * @param fk_week
     * @param first_week_day
     * @return
     */
    public long insertWeekActivity( int tt_moderate, 
				   					int tt_intensive,
				   					int aim_moderate_day,
			   						int aim_intensive_day,
				   					int aim_moderate_week,
				   					int aim_intensive_week,
				   					int mission_accomplished,
				   					String first_week_day) {
         ContentValues initialValues = new ContentValues();
         
         initialValues.put(KEY_FIRST_DAY_WEEK  		       , first_week_day);
         initialValues.put(KEY_TOTAL_TIME_MODERATE	   	   , tt_moderate);
         initialValues.put(KEY_TOTAL_TIME_INTENSIVE  	   , tt_intensive);
         initialValues.put(KEY_AIM_DAY_MODERATE		       , aim_moderate_day);
         initialValues.put(KEY_AIM_DAY_INTENSIVE		   , aim_intensive_day);
         initialValues.put(KEY_AIM_WEEK_MODERATE		   , aim_moderate_week);
         initialValues.put(KEY_AIM_WEEK_INTENSIVE		   , aim_intensive_week);
         initialValues.put(KEY_MISSION_ACCOMPLISHED_WEEK   , mission_accomplished);
        
         return mDb.insert(DB_TABLE_WEEK_ACTIVITY, null, initialValues);
     }
     
    /**
     * Delete the weight_bmi with the given rowId
     * 
     * @param rowId id of weight_bmi to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteWeightBMI(long rowId) {

        return mDb.delete(DB_TABLE_WEIGHT, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the daily activity with the given rowId
     * 
     * @param rowId id of daily activity to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteDailyActivity(long rowId) {

        return mDb.delete(DB_TABLE_DAILY_ACTIVITY, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Delete the week activity with the given rowId
     * 
     * @param rowId id of daily activity to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteWeekActivity(long rowId) {

        return mDb.delete(DB_TABLE_WEEK_ACTIVITY, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all weight_bmi in the database
     * 
     * @return Cursor over all weight_bmi
    */ 
    public Cursor fetchAllWeightBMI() {
       
        return mDb.query(DB_TABLE_WEIGHT, new String[] {KEY_ROWID, KEY_WEIGHT,
                KEY_BMI, KEY_DATE_WEIGHT}, null, null, null, null, null); 
    }
    
    /**
     * Return a Cursor over the list of all daily activities in the database
     * 
     * @return Cursor over all daily activities
    */ 
    public Cursor fetchAllDailyActivities() {

        return mDb.query(DB_TABLE_DAILY_ACTIVITY, 
        				 new String[] {KEY_ROWID, 
        							   KEY_DATE_ACTIVITY,
        							   KEY_REST_TIME, 
        							   KEY_MODERATE_TIME,
        							   KEY_INTENSIVE_TIME,
        							   KEY_MISSION_ACCOMPLISHED_DAY,
        							   KEY_WEEK},
        				 null, null, null, null, null);  
    }
    
    /**
     * Return a Cursor over the list of all week activities in the database
     * 
     * @return Cursor over all daily activities
    */ 
    public Cursor fetchAllWeekActivities() {

        return mDb.query(DB_TABLE_WEEK_ACTIVITY, 
        				 new String[] {KEY_ROWID, 
        							   KEY_FIRST_DAY_WEEK,
        							   KEY_TOTAL_TIME_MODERATE, 
        							   KEY_TOTAL_TIME_INTENSIVE,
        							   KEY_AIM_DAY_MODERATE,
        							   KEY_AIM_DAY_INTENSIVE,
        							   KEY_AIM_WEEK_MODERATE,
        							   KEY_AIM_WEEK_INTENSIVE,
        							   KEY_MISSION_ACCOMPLISHED_WEEK}, null, null, null, null, null);    
    }

    /**
     * Return a Cursor positioned at the weight_bmi that matches the given rowId
     * 
     * @param rowId id of weight_bmi to retrieve
     * @return Cursor positioned to matching weight_bmi, if found
     * @throws SQLException if weight_bmi could not be found/retrieved
    */ 
    public Cursor fetchWeightBMI(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TABLE_WEIGHT, new String[] {KEY_ROWID,
                    KEY_WEIGHT, KEY_BMI, KEY_DATE_WEIGHT}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the daily activity that matches the given 
     * rowId
     * 
     * @param rowId id of daily activity to retrieve
     * @return Cursor positioned to matching daily activity, if found
     * @throws SQLException if daily activity could not be found/retrieved
    */ 
    public Cursor fetchDailyActivity(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TABLE_DAILY_ACTIVITY, 
            		new String[] {KEY_ROWID, 
					   			  KEY_DATE_ACTIVITY,
					   			  KEY_REST_TIME, 
					   			  KEY_MODERATE_TIME,
					   			  KEY_INTENSIVE_TIME,
					   			  KEY_MISSION_ACCOMPLISHED_DAY,
					   			  KEY_WEEK}, 
					KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor positioned at the week activity that matches the given 
     * rowId
     * 
     * @param rowId id of daily activity to retrieve
     * @return Cursor positioned to matching daily activity, if found
     * @throws SQLException if daily activity could not be found/retrieved
    */ 
    public Cursor fetchWeekActivity(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TABLE_WEEK_ACTIVITY, 
            		new String[] {KEY_ROWID, 
					   			  KEY_FIRST_DAY_WEEK,
					   			  KEY_TOTAL_TIME_MODERATE, 
					   			  KEY_TOTAL_TIME_INTENSIVE,
					   			  KEY_AIM_DAY_MODERATE,
					   			  KEY_AIM_DAY_INTENSIVE,
					   			  KEY_AIM_WEEK_MODERATE,
					   			  KEY_AIM_WEEK_INTENSIVE,
					   			  KEY_MISSION_ACCOMPLISHED_WEEK}, 
					   			  KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the weight_bmi using the details provided. The weight_bmi to be 
     * updated is specified using the rowId, and it is altered to use the
     * weight, bmi and date_weight values passed in
     * 
     * @param rowId id of weight_bmi to update
     * @param weight value to set weight_bmi weight to
     * @param bmi value to set weight_bmi bmi to
     * @param date_weight value to set weight_bmi date_weight to
     * @return true if the weight_bmi was successfully updated, false otherwise
     */
    public boolean updateWeightBMI(long rowId, float weight, float bmi, String date_weight) {
        ContentValues args = new ContentValues();
        args.put(KEY_WEIGHT, weight);
        args.put(KEY_BMI, bmi);
        args.put(KEY_DATE_WEIGHT, date_weight);
        
        return mDb.update(DB_TABLE_WEIGHT, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Update the daily activity using the details provided. The daily activity
     * to be updated is specified using the rowId, and it is altered to use the
     * rest_time, moderate_time, intensive_time and date_activity values passed
     * in
     * 
     * @param rowId id of daily activity to update
     * @param rest_time
     * @param moderate_time
     * @param intensive_time
     * @param date_activity
     * @return true if the daily activity was successfully updated, false 
     * otherwise
     */
    public boolean updateDailyActivity(long rowId, 
    								   int rest_time, 
    								   int moderate_time,
    								   int intensive_time,
    								   int mission_accomplished,
    								   long fk_week,
    								   String date_activity) {
        ContentValues args = new ContentValues();
        
        args.put(KEY_REST_TIME	    		  , rest_time);
        args.put(KEY_MODERATE_TIME  		  , moderate_time);
        args.put(KEY_INTENSIVE_TIME			  , intensive_time);
        args.put(KEY_DATE_ACTIVITY  		  , date_activity);
        args.put(KEY_MISSION_ACCOMPLISHED_DAY , mission_accomplished);
        args.put(KEY_WEEK, fk_week);
        
        return mDb.update(DB_TABLE_DAILY_ACTIVITY, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Update the week activity using the details provided.
     * 
     * @param rowId
     * @param tt_moderate
     * @param tt_intensive
     * @param aim_moderate
     * @param aim_intensive
     * @param mission_accomplished
     * @param fk_week
     * @param first_week_day
     * @return
     */
    public boolean updateWeekActivity(long rowId, 
			   						   int tt_moderate, 
			   						   int tt_intensive,
			   						   int aim_moderate_day,
			   						   int aim_intensive_day,
			   						   int aim_moderate_week,
			   						   int aim_intensive_week,
			   						   int mission_accomplished,
			   						   String first_week_day) {
    	ContentValues args = new ContentValues();

    	args.put(KEY_FIRST_DAY_WEEK  		   , first_week_day);
    	args.put(KEY_TOTAL_TIME_MODERATE	   , tt_moderate);
    	args.put(KEY_TOTAL_TIME_INTENSIVE  	   , tt_intensive);
    	args.put(KEY_AIM_DAY_MODERATE		   , aim_moderate_day);
        args.put(KEY_AIM_DAY_INTENSIVE		   , aim_intensive_day);
    	args.put(KEY_AIM_WEEK_MODERATE		   , aim_moderate_week);
    	args.put(KEY_AIM_WEEK_INTENSIVE		   , aim_intensive_week);
    	args.put(KEY_MISSION_ACCOMPLISHED_WEEK , mission_accomplished);
    	
    	return mDb.update(DB_TABLE_WEEK_ACTIVITY, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public void rehacerBBDD() {
        
    	mDb.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_WEIGHT);
    	mDb.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_DAILY_ACTIVITY);
    	mDb.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE_WEEK_ACTIVITY);
    	mDb.execSQL(DATABASE_CREATE);
    	WeightBMIObject w1 = new WeightBMIObject(88, (float) 1.70, 1309737600);
		WeightBMIObject w2 = new WeightBMIObject((float) 87.5, (float) 1.70, 1310342400);
		WeightBMIObject w3 = new WeightBMIObject((float) 86.9, (float) 1.70, 1310947200);
		insertWeightBMI(w1);
		insertWeightBMI(w2);
		insertWeightBMI(w3);
		
		insertWeekActivity(208, 7, 30, 0, 210, 0, 0, "19/07/2011");
		
		insertDailyActivity(300, 10, 5, 0, 1, "19/07/2011");
		insertDailyActivity(300, 40, 0, 1, 1, "20/07/2011");
		insertDailyActivity(300, 40, 2, 1, 1, "21/07/2011");
		insertDailyActivity(300, 20, 0, 0, 1, "22/07/2011");
		insertDailyActivity(300, 40, 0, 1, 1, "23/07/2011");
		insertDailyActivity(300, 58, 0, 1, 1, "24/07/2011");
		mDb.close();    
    }  
}