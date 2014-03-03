package es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;


public class MicazSensor extends ServerResource{
	
	 @Get
		public String represent() throws SQLException, IOException, JSONException  {
	    	
	    	System.out.println("*** GET MicaZ Sensor***");
	    	
	    	Device dev = DeviceManager.findByName("micaz/"+(String) getRequestAttributes().get("nodeId"));
	    	
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
	   
	    	return ("Does not exist the Micaz-Sensor with Id "+ (String) getRequestAttributes().get("nodeId"));
	        
		}

		private String requestManager(Constants.SensorRet method, String Sensor_URI) throws IOException, SQLException, JSONException {
			
			String result=null;
			
			switch(method){
				case DB:
					// Get the latest measure of a sensor reading
					JSONObject resp = new JSONObject();
					if(!(((String) getRequestAttributes().get("Sensor")).contains("status"))){
	    				resp.put((String) getRequestAttributes().get("Sensor"), lastReading(Sensor_URI));
	    				result = resp.toString();
					}else{
						result = lastReading(Sensor_URI);
					}
					
				break;
				
				case REST:
					// Get a sensor reading from external resource
					 ClientResource restClient = new ClientResource(Sensor_URI);
					 result = get(restClient);
				break;
			}
			    	
			return result;
			
		}
	 
	 private String lastReading(String Sensor_URI) throws IOException, SQLException {
				
		String result=null;
		
		// Get the latest measure of a sensor reading
		result = Utils.getSensorReading(Sensor_URI, 60);
		
		System.out.println("*** Resultado "+Sensor_URI+ ": "+result);
				
		
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
