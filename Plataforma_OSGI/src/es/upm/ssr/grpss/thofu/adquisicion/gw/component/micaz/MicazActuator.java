package es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz;

import java.io.IOException;
import java.sql.SQLException;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;


public class MicazActuator extends ServerResource{
	
	 @Get
		public String represent() throws SQLException, IOException  {
	    	
	    	System.out.println("*** PUT MicaZ Actuator***");
	    	
	    	String Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/"+(String) getRequestAttributes().get("nodeId");
	    	
	    	String Sensor_URI = Device_URI+"/actuator/"+(String) getRequestAttributes().get("Actuator");
	    	
	    	
	    	if(Utils.isSensor(Sensor_URI) ){
	    		
					return requestManager("PUT");
	    	}
	    	
			return ("Does not exist the Sensor with URI "+ Sensor_URI);
	        
		}
	 
	 private String requestManager(String method) throws IOException, SQLException {
		 
		String Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/"+(String) getRequestAttributes().get("nodeId");
	    	
	    String Sensor_URI = Device_URI+"/sensor/"+(String) getRequestAttributes().get("Actuator");
		
		String result=null;
		
		// Get the most current measure of a sensor reading
		result = Utils.getSensorReading(Sensor_URI, 30);
				
		
		return result;
	 }

}
