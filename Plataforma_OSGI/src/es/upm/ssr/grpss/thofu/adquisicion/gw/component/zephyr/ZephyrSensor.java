package es.upm.ssr.grpss.thofu.adquisicion.gw.component.zephyr;

import java.io.IOException;
import java.sql.SQLException;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;



import es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile.MobileSensor;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;


public class ZephyrSensor extends ServerResource{
	
    @Get
	public String represent() throws SQLException, IOException  {
    	
    	System.out.println("*** GET Sensor ***");
    	
    	Device dev = DeviceManager.findByName("zephyr/"+(String) getRequestAttributes().get("nodeId"));
    	
    	String Sensor_URI=null;
    	Constants.SensorRet req;
    	    	    	
    	if(dev != null){
    		
    		if(dev.isLocal()){
    			Sensor_URI = dev.getUri()+"/sensor/"+(String) getRequestAttributes().get("Sensor");
    			req = Constants.SensorRet.DB;
    			
    		}else{
    			//If the device is not local, reformat Id for building the original URI
    			System.out.println("*** URI: "+Sensor_URI+"*** NO ES LOCAL");
    			Sensor_URI = dev.getUri().replaceAll("-e", "")+"/sensor/"+(String) getRequestAttributes().get("Sensor");
    			req = Constants.SensorRet.REST; 	
    		}
	    	
	    	System.out.println("*** URI: "+Sensor_URI+"***");
	    	
	    	if(Utils.isSensor(Sensor_URI) ){
	    		
					return requestManager(req, Sensor_URI);
	    	}
    	}
    	
		return ("Does not exist the Zephyr-Sensor with Id "+ (String) getRequestAttributes().get("nodeId"));
        
	}
    
    private String requestManager(Constants.SensorRet method, String Sensor_URI) throws IOException, SQLException {
    	
		String result=null;
    	
		switch(method){
			case DB:
				// Get the latest measure of a sensor reading
				result = Utils.getSensorReading(Sensor_URI, 5);
			break;
			
			case REST:
				// Get a sensor reading from external resource
				 ClientResource restClient = new ClientResource(Sensor_URI);
				 result = get(restClient);
			break;
		}
		    	
		return result;
    	
    }
    
    public static String get(ClientResource clientResource) throws IOException, ResourceException {  
	    	
		   Representation r = clientResource.get();	    	 	
	    
		   String result = null;
		   
	        if (clientResource.getStatus().isSuccess()
	               && clientResource.getResponseEntity().isAvailable()) {  
	                       	
	            result = clientResource.getResponseEntity().getText();	                                    
	     	           	  	            
	        }
	        
	        clientResource.release();
	        r.release();
	        
	        return result;
			       
	 }	

}
