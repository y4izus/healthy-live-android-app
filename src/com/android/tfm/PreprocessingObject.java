package com.android.tfm;

public class PreprocessingObject {
	
//	float fft;
	float mean;
	float std_dev;
	String activity_type;
	
	/**
	 * Constructor de la clase PreprocessingObject.
	 * @param fft
	 * @param mean
	 * @param std_dev
	 * @param activity_type
	 */
	public PreprocessingObject(float mean, float std_dev, String activity_type){
//		this.fft = fft;
		this.mean = mean;
		this.std_dev = std_dev;
		this.activity_type = activity_type;		
	}
	
//	/**
//	 * Método que devuelve la fft.
//	 * @return
//	 */
//	public float get_fft(){
//		return fft;
//	}
	
	/**
	 * Método que devuelve la media
	 * @return
	 */
	public float get_mean(){
		return mean;
	}
	
	/**
	 * Método que devuelve la desviación estándar.
	 * @return
	 */
	public float get_std_dev(){
		return std_dev;
	}
	
	/**
	 * Método que devuelve el tipo de actividad.
	 * @return
	 */
	public String get_activity_type(){
		return activity_type;
	}
}
