/**
 * Clase que contiene los datos de una lectura del acelerómetro
 */


package com.android.tfm;

public class MeasureObject {
	
	float x;
	float y;
	float z;
	float function_xyz;
	
	/**
	 * Constructor.
	 * @param x
	 * @param y
	 * @param z
	 */
	public MeasureObject (float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		
		// Se calcula el módulo de los tres ejes del acelerómetro.
		function_xyz = (float) Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2)+Math.pow(this.z, 2));
	}
	
	/**
	 * Método que devuelve el valor de x.
	 * @return
	 */
	public float get_x(){
		return x;
	}
	
	/**
	 * Método que devuelve el valor de y.
	 * @return
	 */
	public float get_y(){
		return y;
	}
	
	/**
	 * Método que devuelve el valor de z.
	 * @return
	 */
	public float get_z(){
		return z;
	}	
	
	/**
	 * Método que devuelve el valor de la funcion de los 3 valores.
	 * @return
	 */
	public float get_function_xyz(){
		return function_xyz;
	}
}
