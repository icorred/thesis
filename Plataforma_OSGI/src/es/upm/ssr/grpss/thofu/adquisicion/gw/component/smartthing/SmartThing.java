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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceForwarder;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

/**
 * @author Iván
 *
 */
public class SmartThing  extends ServerResource{
	
	@Post("json:json")
	public String createThing(Representation entity){
		
		try {
		//	System.out.println("*** Smart things creating: "+entity.getText());
			JSONObject smartThing = new JSONObject(entity.getText());
						
			return "{\"thingId\":\""+addDevicetoBD(smartThing)+"\"}";
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "{\"thingId\":\"Error\"," +
					"\"error_message\":\"Smart Thing description not valid\"}";			
		} catch (IOException e1) {
			e1.printStackTrace();	
			return "{\"thingId\":\"Error\"," +
			"\"error_message\":\"Error reading body message\"}";	
		}
		
	}
	
	@Put("json:json")
	public String updateThing(Representation entity){
		
		try {
			//	System.out.println("*** Smart things creating: "+entity.getText());
				JSONObject smartThing = new JSONObject(entity.getText());
				String deviceURI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+(String) smartThing.get("deviceId");
			
				if(Utils.isDevice(deviceURI, null) != null){
								
					Utils.setDeviceStatus(deviceURI, (String) smartThing.get("status"));
						
					return "{\"status\":\""+(String) smartThing.get("status")+"\"}";
				}
				
				return "{\"error_message\":\"Smart Thing does not exist\"}";				
				
			} catch (JSONException e) {
				e.printStackTrace();
				return "{\"thingId\":\"Error\"," +
						"\"error_message\":\"Smart Thing description not valid\"}";			
			} catch (IOException e1) {
				e1.printStackTrace();	
				return "{\"thingId\":\"Error\"," +
				"\"error_message\":\"Error reading body message\"}";	
			}		
		
	}
	
	@Delete
	public void deleteThing(Representation entity){
		
	}
			
	private String addDevicetoBD(JSONObject st){
				
		String name = "Error";
		
		try {
			
			String rootName;
			String addr;
			
			
			rootName = (String) st.get("thingName");
						
			addr = (String) st.get("physicalAddr");
			
									
			String deviceURI = Utils.isDevice(Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+rootName, addr);
						
			if(deviceURI != null){
			
				name = deviceURI.replace(Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/", "");
			}else{
				name = new String(rootName+"/"+(Utils.getNumDevices(rootName)+1));
			}
											
			String uri = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+name;
			
			String localization = "{\"x\":\"0.0\",\"y\":\"0.0\",\"z\":\"0.0\"}"; //(String) st.get("localization");
			
			String type = (String) st.get("deviceType");
			
			int lifetime = Integer.valueOf((String)st.get("lifeTime"));
			
			String description = (String) st.get("description");
			
			String status = (String) st.get("status");
					
			Device dev = null;
			
		    System.out.println(rootName+" agregado");
		// Device if doesn't exist in the list
			        
	        //Update BD with new device
	      
	        //	String Dev_URI = Utils.isDevice(Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+rootName, addr);
	        	        	
	        //	if(Dev_URI == null){
	        		
	        System.out.println("*** "+rootName+" to BD ADDING TO THOFU DB ***");
	        		
			Utils.setContextualDevice(Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+name, 
							description, "v1.0", type, 
							"REST", Utils.makeTimestamp(System.currentTimeMillis()), lifetime,  addr, true , status, localization);	 
					
											
			/*** Add sensors and actuators ***/
			
			String key;
			String unit;
			String dataType;	
			String sensorDescription;
				
			
			if(st.has("sensors")){
				
				JSONArray sensors = st.getJSONArray("sensors");
										
				for(int i=0; i<sensors.length(); i++){
							
					key = (String) sensors.getJSONObject(i).get("key");
					unit = (String) sensors.getJSONObject(i).get("unit");
					dataType = (String) sensors.getJSONObject(i).get("dataType");
					sensorDescription = (String) sensors.getJSONObject(i).get("description");
							
					String sensorURI = uri+"/sensor/"+key;
					System.out.println("Sensor URI " + sensorURI);
					//Add sensor to list
					Utils.setSensor(sensorURI, uri, key, unit, dataType, sensorDescription);					
							
				}
			}
			
			/*** Add sensors and actuators ***/
			
						
			if(st.has("actuators")){
				
				JSONArray actuators = st.getJSONArray("actuators");
				
				for(int j=0; j<actuators.length(); j++){
							
					key = (String) actuators.getJSONObject(j).get("key");
					unit = (String) actuators.getJSONObject(j).get("unit");
					dataType = (String) actuators.getJSONObject(j).get("dataType");
							
					//Add sensor to list. TO-DO: Add description param
		//			Utils.setActuator(uri+"/actuator/"+key, uri, " ", dataType, key, unit);					
							
				}
				
			}
				
			// This device is considered as local device for the Gateway which load the driver 
			dev = new Device(name, DeviceForwarder.Type.REST, type, true, true, addr, uri, lifetime);

		      DeviceManager.addDevice(dev);
	          	            
	    } catch (JSONException e1) {
			e1.printStackTrace();
	    }	        		
	
		return name;
 }

}	
