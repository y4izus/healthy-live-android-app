/**
 * Clase que contiene los datos de una lectura del aceler�metro
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
		
		// Se calcula el m�dulo de los tres ejes del aceler�metro.
		function_xyz = (float) Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2)+Math.pow(this.z, 2));
	}
	
	/**
	 * M�todo que devuelve el valor de x.
	 * @return
	 */
	public float get_x(){
		return x;
	}
	
	/**
	 * M�todo que devuelve el valor de y.
	 * @return
	 */
	public float get_y(){
		return y;
	}
	
	/**
	 * M�todo que devuelve el valor de z.
	 * @return
	 */
	public float get_z(){
		return z;
	}	
	
	/**
	 * M�todo que devuelve el valor de la funcion de los 3 valores.
	 * @return
	 */
	public float get_function_xyz(){
		return function_xyz;
	}
}
