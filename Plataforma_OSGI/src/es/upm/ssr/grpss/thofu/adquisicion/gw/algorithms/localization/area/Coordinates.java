package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area;

import java.io.Serializable;

public class Coordinates implements Serializable, Cloneable {

	private static final long serialVersionUID = -8041339284184338364L;
	private double x;	// longitude
	private double y;	// altitude
	private double z;	// latitude(creo que al contrario en el plano porque la z positiva esta hacia abajo)
	
	public Coordinates (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
    @Override
	public Object clone() {
		Object obj = null;
		
        try {
        	obj = (Coordinates)super.clone();
        } catch(CloneNotSupportedException ex) {
        	System.out.println(" no se puede duplicar");
        }
        
        ((Coordinates)obj).x = x;
        ((Coordinates)obj).y = y;
        ((Coordinates)obj).z = z;
        
        return obj;
    }
    
    @Override
	public String toString() {
		return "Coord--> x= " + Double.toString(x) + "y= " + Double.toString(y) + "z= " + Double.toString(z);
	}	
}