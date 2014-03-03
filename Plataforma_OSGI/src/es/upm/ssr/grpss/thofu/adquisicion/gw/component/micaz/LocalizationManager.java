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
package es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz;

import org.json.JSONException;
import org.json.JSONObject;

import driverinterfaces.IMicaZDriver;

import es.upm.ssr.grpss.thofu.adquisicion.gw.driver.DriverManager;

/**
 * @author Ivan
 *
 */
public class LocalizationManager {
	
	//Four possible states: "running", "starting", "reseting" and "stop"
//	private static String loc_state = "stop";

	
	public LocalizationManager(){
				
	}
	
	public synchronized static String configRSSLocalization(int nodeId, JSONObject config){
		
		JSONObject result; 
		
		try {
			
			DriverManager dm = DriverManager.getInstance();
			IMicaZDriver md = (IMicaZDriver) dm.getDriver("MICAZ");
			
			System.out.println("Configurar localizacion");
						
			if(md != null){
			
				System.out.println("Comenzar localizacion");
				//START LOCATION command
				if(((String) config.get("command")).compareTo("start") == 0){
					
					long period = Long.valueOf((String) config.get("locPeriod"));
					long end = Long.valueOf((String) config.get("end"));
					int algorithm = Integer.valueOf((String) config.get("algorithm"));
					
					int average = Integer.valueOf((String) config.get("averageTime"));
					
					int evMng = Integer.valueOf((String) config.get("eventManagement"));
							
					//Stop localization if it was started for this node id
	        		md.resetLocSystem(nodeId);
					
					//Start localization for node id
					md.startLocalization(nodeId, algorithm, period, end, average, evMng);
					
					/*if(loc_state.compareTo("running") == 0){
						
						
											
					}else if(loc_state.compareTo("starting") == 0){					
						//TODO
					}else if(loc_state.compareTo("reseting") == 0){
						//TODO
					}else if(loc_state.compareTo("stop") == 0){
						
						//Config Loc system
						
						//Start					
					}*/
					
				}
				
				//STOP command
				else if(((String) config.get("command")).compareTo("stop") == 0){
					
					System.out.println("*** STOP LOCALIZATION "+nodeId+"***");
					
					result = new JSONObject(md.stopLocalization(nodeId));
					
				}
				
				//CHANGE POWER command
				else if(((String) config.get("command")).compareTo("power_change") == 0){
					
					int power = config.getInt("powerLevel");			
					result = new JSONObject(md.powerChangeLocSystem(power));
					
				} else {
					
					result = new JSONObject();
					result.put("message", "RSS Location Command not allowed");
				}
				
				return null; //result.toString();
				
			}else {
				result = new JSONObject();
				result.put("message", "MicaZ Driver not installed");
				System.out.println(result.toString());
				return result.toString();				
			}		
								
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
		
		
		result = new JSONObject();
		try {
			result.put("message", "RSS localization system has failed");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}
	
}
