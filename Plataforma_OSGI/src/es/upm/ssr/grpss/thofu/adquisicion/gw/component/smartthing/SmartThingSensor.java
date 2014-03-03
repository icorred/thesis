/*
 * Copyright (c) 2012, Data Processing and Simulation Group 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the Data Processing and Simulation Group nor the names 
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

/**
 * @author Iv�n
 *
 */
public class SmartThingSensor extends ServerResource {
	
	private final static String ProducerURI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/";
	
	 @Put
	 public String putSensorReading(Representation sensorValue){
		 
		//Update mote status
			Hashtable<String, String> values = new Hashtable<String, String>();
			
			String thingId = (String) getRequestAttributes().get("thing-id");
		    String nodeId = (String) getRequestAttributes().get("node-id");
		    String sensor = (String) getRequestAttributes().get("sensor");
					 
		   
		    String deviceURI = ProducerURI+thingId+"/"+nodeId;
		    
		    //Check if the source device is in the data base
			if(Utils.isDevice(deviceURI, null) != null){
				
				String sensorURI = deviceURI+"/sensor/"+sensor;
				Device device = DeviceManager.findByName(thingId+"/"+nodeId);
				
				try {
					
					JSONObject value = new JSONObject(sensorValue.getText());									
					device.setValueCache(sensorURI, (String) value.get("value"));
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			 
				if(!updateSensorStatus(sensorURI, deviceURI, device)){
					
					return "{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+
			    			"does not have a "+(String) getRequestAttributes().get("sensor")+" sensor\"}";
				}
			}
			
		 return "{\"responseCode\": \"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+
			    			"is not registered into the Platform";
	 }
	
	 @Get(":json")
		public String getSensorReading() throws SQLException, IOException, JSONException  {
	    	
	    	System.out.println("*** GET Sensor value***");
	    	
	    	Device dev = DeviceManager.findByName((String) getRequestAttributes().get("thing-id")+"/"+(String) getRequestAttributes().get("node-id"));

	    	Form queryParams = getQuery();
			String from = queryParams.getFirstValue("from");
			
	    	String Sensor_URI=null;
	    	Constants.SensorRet req;
	    	
	    	if(dev != null){
	    		
	    		if(dev.isLocal()){
	    			Sensor_URI = dev.getUri()+"/sensor/"+(String) getRequestAttributes().get("sensor");
	    			req = Constants.SensorRet.DB;
	    			
	    		}else{
	    			//If the device is not local, reformat Id for building the original URI
	    			System.out.println("*** URI: "+Sensor_URI+"*** NO ES LOCAL");
	    			Sensor_URI = dev.getUri().replaceAll("-e", "")+"/sensor/"+(String) getRequestAttributes().get("sensor");
	    			req = Constants.SensorRet.REST; 	
	    		}
		    	
		    	System.out.println("*** URI: "+Sensor_URI+"***");
		    	
		    	if(Utils.isSensor(Sensor_URI) ){
		    		
		    		String message = null;
		    		
		    		if(from == null){
		    			message = requestManager(req, Sensor_URI, 0);
		    			
		    		}else{
		    			message = requestManager(req, Sensor_URI, Long.valueOf(from));
		    		}
		    		
		    		if(message == null){
						return "{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+
				    			" do not match with reading query for sensor "+(String) getRequestAttributes().get("sensor")+"\"}";
		    		}
		    		
		    		return message;
		    	}
		    	
		    	return "{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+
		    			"and sensor "+(String) getRequestAttributes().get("sensor")+" does not exist\"}";
	    	}
	   
	    	return "{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+" does not exist\"}";
	        
		}
	 
	 private String requestManager(Constants.SensorRet method, String Sensor_URI, long from) throws IOException, SQLException, JSONException {
						
			String message=null;
			
			switch(method){
				case DB:
					// Get the latest measure of a sensor reading
					if(!(((String) getRequestAttributes().get("sensor")).contains("status"))){
						
						message = lastReading(Sensor_URI, (String) getRequestAttributes().get("sensor"), from);
						    				
					}else{
						
						JSONObject st = new JSONObject(lastReading(Sensor_URI, (String) getRequestAttributes().get("sensor"), from));
								
						message = (String) st.get("status");
					}
					
				break;
				
				case REST:
					// Get a sensor reading from external resource
					 ClientResource restClient = new ClientResource(Sensor_URI+"?from="+from);
					 message = get(restClient);
				break;
			}
			    	
			return message;
			
		}
	 
	 private String lastReading(String Sensor_URI, String sensor, long from) throws IOException, SQLException {
				
		 Hashtable<String, String> readings = new Hashtable<String, String>();
		 String result = "{}";
		 
		//String result = null;
		//TODO Opci�n a obtener una lectura con un criterio de antig�edad configurable
		System.out.println(">>> Sensor URI: "+Sensor_URI+" From: "+from);
		// Get the latest measure of a sensor reading (60 seconds old max)
		 readings = Utils.getSensorReading2(Sensor_URI, from);
		 
		 if(readings != null){
			 
			if(readings.size() == 1){
				
				JSONObject oneReading = new JSONObject();
				Iterator<String> it = readings.keySet().iterator();
				
				String timestamp = it.next();
				
				try {
					oneReading.put(sensor, readings.get(timestamp));
					oneReading.put("timestamp", timestamp);
				} catch (JSONException e) {
					e.printStackTrace();
				}		
				
				result = oneReading.toString();
				
			}else if(readings.size() > 1){
				
				JSONArray arrayReadings = new JSONArray();
				JSONObject oneReading;
				Iterator<String> it = readings.keySet().iterator();
				
				for(int i = 0; i < readings.size(); i++){
					String timestamp = it.next();
					
					oneReading = new JSONObject();
					
					try {
						
						oneReading.put(sensor, readings.get(timestamp));
						oneReading.put("timestamp", timestamp);
					} catch (JSONException e) {
			
						e.printStackTrace();
					}
								
					arrayReadings.put(oneReading);
				}
				
				result = arrayReadings.toString();
				
			}
		 }
		
		
		System.out.println("*** Resultado "+Sensor_URI+ ": "+result);
						
		return result;
	 }
	 
	private boolean updateSensorStatus(String sensorURI, String deviceURI, Device moteDev){
			
			try {
				
				if(Utils.isSensor(sensorURI)){
				
					if(moteDev != null){
						moteDev.setLastHeardTime(System.currentTimeMillis());
					}
				
					//If Device exists in DB, then update BD with new device if necessary	
					Utils.setDeviceStatus(deviceURI, "awake");
														
					return (true);
				
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (false);		
			
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
