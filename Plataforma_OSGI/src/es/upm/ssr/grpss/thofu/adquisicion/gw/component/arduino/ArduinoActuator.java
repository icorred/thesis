package es.upm.ssr.grpss.thofu.adquisicion.gw.component.arduino;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceForwarder;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class ArduinoActuator extends ServerResource{
	
	static private String[] RGBleds = {"ledr", "ledy", "ledg", "ledb"};
	static private String[] binaryLeds = {"ledp"};
	static private String[] switches = {"relay"};
	private String Device_URI;
	static long recibido;
	
	
	 @Put
		public  String updateActuator(Representation entity) throws JSONException, IOException, SQLException{
			
		 String action = null;
		 JSONObject resp;
		 
		 recibido = System.currentTimeMillis();
		 
		 
		 System.out.println("*** PUT Arduino Actuator ***");
	    	
	    	Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/arduino/"+(String) getRequestAttributes().get("nodeId");
	    	
	    	String Actuator_URI = Device_URI+"/actuator/"+(String) getRequestAttributes().get("Actuator");
	    	
	    	updateDevList("/arduino/"+(String) getRequestAttributes().get("nodeId"));
	    	
	    	if(Utils.isActuator(Actuator_URI)){
	    		
	    		 ClientResource mobileClient = new ClientResource(
	    				 "http://10.1.10.205:6008/GW-Arduino/1/act");
	    			    			    		 			 
	    		if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("ledr")){
	    				    			
	    			resp = updateStatus(Actuator_URI, "ledr");
	    			
	    			System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			put(mobileClient, resp);
	    			updateActuatorState("ledr","led",resp.getJSONArray("status").getJSONObject(0).getString("colour"));
	    			return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("ledy")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "ledy");
	    			 
	    			 System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			 put(mobileClient, resp);
	    			 updateActuatorState("ledy","led",resp.getJSONArray("status").getJSONObject(0).getString("colour"));
		    		 return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("ledg")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "ledg");
	    			 
	    			 System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			 put(mobileClient, resp);
	    			 updateActuatorState("ledg","led",resp.getJSONArray("status").getJSONObject(0).getString("colour"));
		    		 return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("ledb")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "ledb");
	    			 
	    			 System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			 put(mobileClient, resp);	
	    			 updateActuatorState("ledb","led",resp.getJSONArray("status").getJSONObject(0).getString("colour"));
	    			 return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("ledp")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "ledp");
	    			 
	    			 System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			 put(mobileClient, resp);	
	    			 updateActuatorState("ledp","led",resp.getJSONArray("status").getJSONObject(0).getString("colour"));
		    		 return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("relay")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "relay");
	    			 
	    			 System.out.println(" *** Actualizado estado en : "+(System.currentTimeMillis()-recibido));
	    			 put(mobileClient, resp);	
	    			 updateActuatorState("relay","switch",resp.getString("status"));
		    			return resp.toString();
	    			 
	    		 } else if(((String) getRequestAttributes().get("Actuator")).equalsIgnoreCase("all")){
	    			 
	    			 resp = updateStatus(Actuator_URI, "all");
	    			 	    			 	    			 
		    		 put(mobileClient, resp);	
	    			 
		    		 return resp.toString();
	    			 
	    		 }
		         
	    	}
	    	
	    	return action;		 
	    
     }
	
	 public static String put(ClientResource clientResource, JSONObject message) throws IOException, ResourceException {  
   			    
		// JsonRepresentation entity = new JsonRepresentation(message);
		 Representation r;
		 
		 System.out.println(" *** Comienza envio ***");
		 
		 System.out.println(" ***Mensaje cambio estado LED enviado en "+(System.currentTimeMillis()-recibido)+" ms ***");
	   	 r = clientResource.put(message.toString());
	   	
	    if (clientResource.getStatus().isSuccess()
	               && clientResource.getResponseEntity().isAvailable()) {  
	                       	
	    	 System.out.println(" ***Respuesta ok en "+(System.currentTimeMillis()-recibido)+" ms ***");
	            return clientResource.getResponseEntity().getText();  	            
	        }
	    
	   
	    //Release allocated resources
	    clientResource.release();
	    r.release();
	    
	    
	    
		 System.out.println("*** PUT JSON Message *** "+message.toString());
		 
	    return message.toString();
		    		    			    			       
	 }
	 
	 private JSONObject updateStatus(String actuator_URI, String actuator) throws SQLException, JSONException{
		 
		//Get the most recent status of a specific actuator
		 String actuatorStatus = Utils.getActuatorStatus(actuator_URI, "0");
		 String actuator_URI_aux = Device_URI+"/actuator/";		 	
		 
		 JSONObject newStatus = new JSONObject();
		 
    	 if(actuatorStatus!=null){
    		
    		 JSONObject com = new JSONObject(actuatorStatus) ;
    		 
    		 JSONObject status_aux = new JSONObject();
    		 		
    	    		 
    		 //It is dealing with a particular actuator
    		 if(!com.getString("actuator").equalsIgnoreCase("all")){
    		    		 
		    	 if(((String)com.get("type")).equalsIgnoreCase("led")){
		    		 
		    		 System.out.println(com.toString());
		    		 
		    		 status_aux = new JSONObject(com.getJSONArray("status").get(0).toString());
		    		 
		    		 System.out.println(com.getJSONArray("status").get(0).toString());
		    		 
		    		 String act = com.getString("actuator");
		    		 
		    		//If led is off, switch on with specific colour
		    		 if(status_aux.getString("colour").compareTo("grey")==0){
		    			 status_aux = changeLedColour(act);
		    		 } //If led is on, switch off
		    		 else {
						
						status_aux.put("colour", "grey");
							    			 
					 }
		    		 	 	    			 
		    		 newStatus.append("status", status_aux);		    		 
		    		 	    		 
		    	 }else if (((String)com.get("type")).equalsIgnoreCase("switch")){
		    		 
		    		 //Switch is on, put off
		    		 if(com.getString("status").equalsIgnoreCase("on")){
		    			 
		    			 newStatus.put("status", "off");
		    			 		    		
		    		//Switch is off, put on
		    		 }else {
		    			 
		    			 newStatus.put("status", "on");
		    			 
		    		 }
		    	 }
	    		 
		    	 newStatus.put("actuator", com.getString("actuator"));
				 newStatus.put("type", com.getString("type"));
				 
				Utils.setActuatorStatus(actuator_URI, newStatus.toString());
				 				 									    		 
			 //It is dealing with all actuator
	    	 }else {
	    		 	    		 
	    		 //Switch off all actuators
	    		if(com.getString("status").equalsIgnoreCase("on")){
	    			    			
	    			
	    		}else{
	    			
	    			int i;
	    			
	    			//Switch off RRGB Leds
	    			for(i=0; i<RGBleds.length; i++){
	    				
	    				updateActuatorState(RGBleds[i],"led","grey");
	   				 
	    			}
	    			
	    			//Switch off binary leds
	    			for(i=0; i<binaryLeds.length; i++){
	    			   				 
	    				updateActuatorState(binaryLeds[i],"led","grey");   				 
	   				 
	    			}

	    			//Switch off switches
					for(i=0; i<switches.length; i++){
																 
						 updateActuatorState(switches[i], "switch", "off");
						 
					}
					
					updateActuatorState("all","any","off");
					
					newStatus.put("actuator", "all");
					newStatus.put("type", "any");
					newStatus.put("status", "off");
	    			
	    		}
	    		 
	    		 
	    	 }
    		 
    		 return newStatus;
	    	     
	    			 
	   }
    	 
       return null;
	 }
	 
     private JSONObject changeLedColour(String led) throws JSONException{
  	   
			 		    			 
			 JSONObject  status = new JSONObject();
			 
			 if(led.equalsIgnoreCase("ledr")){
				 
				status.put("colour", "red");
				 
			 }else if(led.equalsIgnoreCase("ledy")){
				 
				status.put("colour", "yellow");
				 
			 }else if(led.equalsIgnoreCase("ledg")){
				 
				status.put("colour", "green");
				 
			 }else if(led.equalsIgnoreCase("ledb")){
				 
				status.put("colour", "blue");
				 
			 } else if(led.equalsIgnoreCase("ledp")){
				 
				 System.out.println("*** Cambiar ledp ****");
				status.put("colour", "white");
			 }
			 
			 return status;
   	   
     }
     
     private void updateActuatorState(String actuator, String type, String state){
			
			JSONObject actStatus = new JSONObject();
			JSONObject actStatus_aux = new JSONObject();
			
			try {
				
				actStatus.put("type", type);
				
				actStatus.put("actuator", actuator);
				
				if(type.equalsIgnoreCase("led")){
					actStatus_aux.put("colour", state);
					actStatus.append("status", actStatus_aux);
				} else {
				
					actStatus.put("status", state);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			 Utils.setActuatorStatus( Device_URI+"/actuator/"+actuator, actStatus.toString());
			
		}
     
     private void updateDevList(String nodeId){
		 
    	String name = nodeId;
    	String tipo = "Arduino ADK";
    	String addr = "10.1.10.205";
    	String uri = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+name;
		 
		 Device dev = DeviceManager.findByName(name);
		
		 if(dev != null){
 			// Set last heard time		    					    			
 	        dev.setLastHeardTime(System.currentTimeMillis());
		 }else {
			 
			// This device is considered as local device for the Gateway which load the driver 
			 dev = new Device(name, DeviceForwarder.Type.REST, tipo, true, true, addr, uri, (30*60));
	         DeviceManager.addDevice(dev);	        
			 
		 }
		 
	 }

}
