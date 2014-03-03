package es.upm.ssr.grpss.thofu.adquisicion.gw.driver;

import java.util.Hashtable;

import driverinterfaces.IMicaZDriver;
import driverinterfaces.ThofuDriver;

public class DriverManager {
		
	private static Hashtable<String, IMicaZDriver> driverMicaZList;
	
	private static DriverManager dm;
	
	private DriverManager(){
		
		driverMicaZList = new Hashtable<String, IMicaZDriver>();
	}
	
	public static DriverManager getInstance(){
		
		if(dm == null){
			dm = new DriverManager();
		}
		
		return dm;
	}

	public ThofuDriver getDriver(String id){
		
		return (ThofuDriver) driverMicaZList.get(id);		
	}
	
	public void putDriver(ThofuDriver driver){
		driverMicaZList.put(driver.getDriverId(), (IMicaZDriver) driver);
	}
			
	public void removeDriver(String id){
		driverMicaZList.remove(id);		
	}
}
