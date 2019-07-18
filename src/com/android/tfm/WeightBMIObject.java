package com.android.tfm;

import android.content.SharedPreferences;
import android.widget.Toast;

public class WeightBMIObject{
	
	/**
	 * Variables del objeto.
	 */
	float weight;
	float bmi;
	long day_timestamp;
	
	
	public WeightBMIObject() {
		// TODO Auto-generated constructor stub
	}

	public WeightBMIObject(float weight, float tall, long day_timestamp) {
		this.weight = weight;
		// Se calcula el IMC a partir de la altura y el peso.
		this.bmi = (float) (this.weight/Math.pow(tall,2));
		this.day_timestamp = day_timestamp;
	}

	/**
	 * Método que devuelve el peso.
	 * @return
	 */
	public float get_weight(){
		return weight;
	}
	
	/**
	 * Método que devuelve el IMC.
	 * @return
	 */
	public float get_bmi(){
		return bmi;
	}
	
	/**
	 * Método que devuelve el timestamp.
	 * @return
	 */
	public long get_timestamp(){
		return day_timestamp;
	}
	
	/**
	 * Método que fija el peso.
	 * @return
	 */
	public void set_weight(float weight){
		this.weight = weight;
	}
	
	/**
	 * Método que fija el IMC.
	 * @return
	 */
	public void set_bmi(float bmi){
		this.bmi = bmi;
	}
	
	/**
	 * Método que fija el timestamp.
	 * @return
	 */
	public void set_timestamp(long day_timestamp){
		this.day_timestamp = day_timestamp;
	}
}
