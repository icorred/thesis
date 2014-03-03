package es.upm.ssr.grpss.thofu.adquisicion.gw.component.arduino;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceForwarder;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class ArduinoSensor extends ServerResource{
		
	static public boolean joyReq = false;
	static public boolean touchReq = false;
	static public boolean buttonReq = false;
	
	static public Hashtable interactiveSensor = new Hashtable();
	
	static public JSONObject actCode = null;
		
	static public boolean sensorEvent = false;
	
	private String Device_URI;
	
	private long recibido;
	
	 @Get
		public Representation represent() throws SQLException, IOException  {
	    	
	    	System.out.println("*** GET Mobile Sensor***");
	    	
	    	Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/arduino/"+(String) getRequestAttributes().get("nodeId");
	    		    	
	    //	if(Utils.isDevice(Device_URI)){ 
	    		    		    		    		
	    		joyReq = true;
	    		touchReq = true;
	    		buttonReq = true;
	    			
	    		requestArduinoManager req = new requestArduinoManager(getResponse());
		    	setAutoCommitting( false );
		    	getApplication().getTaskService().submit( req );
											
			//}
	    		
	    		try
				{
						Thread.sleep( 1000 );
				}
				catch( InterruptedException x )
				{
						x.printStackTrace();
				}
	    			    			    		
	    		return null;
	    	}
	

	 
	 @Put
		public  String updateActuator(Representation entity) throws SQLException, JSONException, IOException {
	    	
	    	System.out.println("*** PUT Arduino Sensor***");
	    	
	    	recibido = System.currentTimeMillis();
	    		    		    	
	    	Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/arduino/"+(String) getRequestAttributes().get("nodeId");
	    	
	    	String Sensor_URI = Device_URI+"/sensor/"+(String) getRequestAttributes().get("Sensor");
	    	
	    	JSONObject actuatorStatus;	    	
	    	
	    	if(Utils.isSensor(Sensor_URI) ){
	    	
	    		
	    		 updateDevList("/arduino/"+(String) getRequestAttributes().get("nodeId"));
	    			    			    		
	    		 if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("joystick")){
	    			 
	    			JSONObject action = new JSONObject(entity.getText());	    			 
	    			 
					interactiveSensor.put("joystick", action.get("value"));
		    	      		    		
		    	      System.out.println("*** Joystick Arduino: "+action.get("value")+"***");
		    	      		    	      
		    	      
		    	     // Right
		    	     if(((String)interactiveSensor.get(new String("joystick"))).equalsIgnoreCase("{1,0}")){
		    	    	 
		    	    	 actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/ledb", "0"));
		    	    	 
		    	    	 if(actuatorStatus!=null){
		    	    				    	    	 
			    	    	 if(actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("blue")){
			    	    		 
			    	    		 actCode = updateActuatorState("ledb", "led", "grey");
			    	    		 
			    	    		 joyReq = false;
			    	    				    	    		 
			    	    		 return actCode.toString();
			    	    		 
			    	    	 } else if (actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("grey") ){
			    	    		 
			    	    		 actCode = updateActuatorState("ledb", "led", "blue");
			    	    		 
			    	    		 joyReq = false;
			      	    		 return actCode.toString();
			    	    	 }
		    	    		 
		    	    	 } else {
		    	    		 		    	    		 
		    	    		 actCode = updateActuatorState("ledb", "led", "grey");
		    	    		 
		    	    		 joyReq = false;
		    	    		 
		    	    		 return actCode.toString();		    	    		 
			    	    	 
		    	    	 }    	    	     
		    	   
		    	     // Left
		    	     }else if (((String)interactiveSensor.get(new String("joystick"))).equalsIgnoreCase("{-1,0}")){
		    	    	 
		    	    	 actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/ledr", "0"));
		    	    	 
		    	    	 if(actuatorStatus!=null){
		    	    				    	    	 
			    	    	 if(actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("red")){
			    	    		 
			    	    		 actCode = updateActuatorState("ledr", "led", "grey");
			    	    		 
			    	    		 joyReq = false;
			    	    			    	    		 
			    	    		 return actCode.toString();
			    	    		 
			    	    	 } else if (actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("grey") ){
			    	    		 
			    	    		 actCode = updateActuatorState("ledr", "led", "red");
			    	    		 
			    	    		 joyReq = false;
					    	    		 
			    	    		 return actCode.toString();
			    	    	 }
		    	    		 
		    	    	 } else {
		    	    		 		    	    		 
		    	    		 actCode = updateActuatorState("ledr", "led", "grey");
		    	    		 
		    	    		 joyReq = false;
		    	    		 
		    	    		 return actCode.toString();		    	    		 
			    	    	 
		    	    	 }  
		    	    	 
		    	     // Up
		    	     }else if (((String)interactiveSensor.get(new String("joystick"))).equalsIgnoreCase("{0,1}")){
		    	    	 
		    	    	 actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/ledy", "0"));
		    	    	 
		    	    	 if(actuatorStatus!=null){
		    	    				    	    	 
			    	    	 if(actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("yellow")){
			    	    		 
			    	    		 actCode = updateActuatorState("ledy", "led", "grey");
			    	    		 
			    	    		 joyReq = false;
			    	    					    	    		 
			    	    		 return actCode.toString();
			    	    		 
			    	    	 } else if (actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("grey") ){
			    	    		 
			    	    		 actCode = updateActuatorState("ledy", "led", "yellow");
			    	    		 
			    	    		 joyReq = false;
			    	
			    	    		 return actCode.toString();
			    	    	 }
		    	    		 
		    	    	 } else {
		    	    		 		    	    		 
		    	    		 actCode = updateActuatorState("ledy", "led", "grey");
		    	    		 
		    	    		 joyReq = false;
		    	    		 
		    	    		 return actCode.toString();		    	    		 
			    	    	 
		    	    	 }
		    	    	 
		    	     // Down
		    	     }else if (((String)interactiveSensor.get(new String("joystick"))).equalsIgnoreCase("{0,-1}")){
		    	    	 
		    	    	 actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/ledg", "0"));
		    	    	 
		    	    	 if(actuatorStatus!=null){
		    	    				    	    	 
			    	    	 if(actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("green")){
			    	    		 
			    	    		 actCode = updateActuatorState("ledg", "led", "grey");
			    	    		 
			    	    		 joyReq = false;
			    				    	    		 
			    	    		 return actCode.toString();
			    	    		 
			    	    	 } else if (actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("grey") ){
			    	    		 
			    	    		 actCode = updateActuatorState("ledg", "led", "green");
			    	    		 
			    	    		 joyReq = false;
			    	    				    	    		 
			    	    		 return actCode.toString();
			    	    	 }
		    	    		 
		    	    	 } else {
		    	    		 		    	    		 
		    	    		 actCode = updateActuatorState("ledg", "led", "grey");
		    	    		 
		    	    		 joyReq = false;
		    			    	    		 
		    	    		 return actCode.toString();		    	    		 
			    	    	 
		    	    	 }
		    	    	 
		    	     }
		    		    	     
		    	     return "*** Action not allowed ***";   			 	    			 		    			 
	    			 
	    		 }else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("touch")){
	    			 
	    			 System.out.println("*** Touch ***");
	    			 	    			     			 
	    			 actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/ledp", "0"));
	    	    	 
	    	    	 if(actuatorStatus!=null){
	    	    				    	    	 
		    	    	 if(actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("white")){
		    	    		 
		    	    		 actCode = updateActuatorState("ledp", "led", "grey");
		    	    		 
		    	    		 joyReq = false;
		    	    		 
		    	    		 return actCode.toString();
		    	    		 
		    	    	 } else if (actuatorStatus.getJSONArray("status").getJSONObject(0).getString("colour").equalsIgnoreCase("grey") ){
		    	    		 
		    	    		 actCode = updateActuatorState("ledp", "led", "white");
		    	    		 
		    	    		 joyReq = false;
		    	    		 return actCode.toString();
		    	    	 }
	    	    		 
	    	    	 } else {
	    	    		 		    	    		 
	    	    		 actCode = updateActuatorState("ledp", "led", "grey");
	    	    		 
	    	    		 joyReq = false;
	    	    		 
	    	    		 return actCode.toString();		    	    		 
		    	    	 
	    	    	 }
		    	  

	    		 }else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("button")){
	    			    			
	    			 System.out.println("*** Button ***");
	    			 
		    	    		    	     		    	     
		    	     actuatorStatus = new JSONObject(Utils.getActuatorStatus(Device_URI+"/actuator/relay", "0"));
		    	     
		    	     if(actuatorStatus!=null){
		    	    	 
		    	    	 interactiveSensor.put("button", actuatorStatus);
		    	    	 
			    	     if(actuatorStatus.getString("status").equalsIgnoreCase("on")  ){
			    				    			    	    		 
		    	    		 actCode = updateActuatorState("relay", "switch", "off");
		    	    		 
		    	    		 buttonReq = false;
			    	    	 return actCode.toString();
			    	    		 
			    	     } else if (actuatorStatus.getString("status").equalsIgnoreCase("off") ){
			    	    				    	    				    	    	 
			    	    	 actCode = updateActuatorState("relay", "switch", "on");
		    	    		 
		    	    		 buttonReq = false;
		    	    		 
			    	    	 return actCode.toString();
			    	     }
		    	    	 
		    	     } else {
		    	    	 
		    	    	 actCode = updateActuatorState("relay", "switch", "on");
	    	    		 
	    	    		 buttonReq = false;
	    	    		 
		    	    	 return actCode.toString();
		    	     }		    	     
		    	    	     
	    		 }
	    	   		 
	    	}
	    	
	    	return "Sensor not allowed for "+Device_URI;
	 }
	 
	 private JSONObject updateActuatorState(String actuator, String type, String state) throws JSONException, SQLException{
			
			JSONObject actStatus = new JSONObject();
			JSONObject actStatus_aux = new JSONObject();
			
			actStatus.put("type", type);
			actStatus.put("actuator", actuator);
			
			if(type.equalsIgnoreCase("led")){
				actStatus_aux.put("colour", state);
				//Intensity
				actStatus.append("status", actStatus_aux);
			} else {
			
				actStatus.put("status", state);
			}
			
			long inicio = System.currentTimeMillis();
			
			 Utils.setActuatorStatus(Device_URI+"/actuator/"+actuator, actStatus.toString());
			 
			 System.out.println(" *** Tiempo en actualizar estado en BD : "+(System.currentTimeMillis()-inicio));
			 
			 System.out.println(" *** Actualizado estado "+actuator+" en : "+(System.currentTimeMillis()-recibido));
			 
			 return actStatus;
			
		}
	 
     private void updateDevList(String nodeId){
		 
    	String name = nodeId;
    	String tipo = "Arduino ADK";
    	
    	//TO-DO: hacer dinamico la asignación de la dirección
    	String addr = "10.1.10.208";
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
