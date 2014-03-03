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

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;

import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz.LocalizationManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

/**
 * @author Iv�n
 *
 */
public class SmartThingLoc extends ServerResource{
	
	
	@Get(":json|jsonp")
	public StringRepresentation getLocalization(Representation entity){
		
		Device dev = DeviceManager.findByName((String) getRequestAttributes().get("thing-id")+"/"+(String) getRequestAttributes().get("node-id"));
    		
		Request reqest = getRequest();
		
	//	System.out.println(reqest.getResourceRef().getQueryAsForm().toString());
		
		Form form = reqest.getResourceRef().getQueryAsForm();
										
    	String Loc_URI=null;
    	Constants.SensorRet req=null;
    	
    	if(dev != null){
    		
    		if(dev.isLocal()){
    			Loc_URI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+
    	        (String) getRequestAttributes().get("thing-id");
    			req = Constants.SensorRet.DB;
    			System.out.println("*** URI: "+Loc_URI+"*** ES LOCAL");
    			
    		}else{
    			//If the device is not local, reformat Id for building the original URI
    			Loc_URI = dev.getUri().replaceAll("/"+(String) getRequestAttributes().get("node-id"), "");
    			    			
    			System.out.println("*** URI: "+Loc_URI+"*** NO ES LOCAL");
    			
    			req = Constants.SensorRet.REST; 	
    		}
	      	
	    	    	
	    	String Complete_URI = Loc_URI +"/"+((String) getRequestAttributes().get("node-id")).replaceAll("-e", "");
	    	
	    	System.out.println("*** URI: "+Complete_URI+"***");
	    	
	    	if(Utils.isDevice(Loc_URI, dev.getAddress()) != null){
	    		
	    		//A JSONP is required
	    		if(form.getFirstValue("callback", true) != null){
	    				    			
	    			String par = form.getFirstValue("callback", true);
	    			 StringRepresentation sr = new StringRepresentation(par+"("+getLocationValue(req, Complete_URI)+");");
	    			 sr.setMediaType(MediaType.APPLICATION_JAVASCRIPT);
	    			 
	    			 return sr;
	    		}
	    		
	    		//A JSON is required
	    		StringRepresentation sr = new StringRepresentation(getLocationValue(req, Complete_URI));
   			 	sr.setMediaType(MediaType.APPLICATION_JSON);
	    		
					return sr;
	    	}
    	}
   
    	return new StringRepresentation("{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+" does not exist\"}");
		
	}
	
	@Put("json:json")
	public String setLocalization(Representation entity){
		
		Device dev = DeviceManager.findByName((String) getRequestAttributes().get("thing-id")+"/"+(String) getRequestAttributes().get("node-id"));
		
    	String Loc_URI = null;
    	Constants.SensorRet req = null;
    	
    	if(dev != null){
    		
    		if(dev.isLocal()){
    			Loc_URI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+
    	        (String) getRequestAttributes().get("thing-id");
    			req = Constants.SensorRet.DB;
    			System.out.println("*** URI: "+Loc_URI+"*** ES LOCAL");
    			
    		}else{
    			//If the device is not local, reformat Id for building the original URI
    			Loc_URI = dev.getUri().replaceAll("/"+(String) getRequestAttributes().get("node-id"), "");
    			    			
    			System.out.println("*** URI: "+Loc_URI+"*** NO ES LOCAL");
    			
    			req = Constants.SensorRet.REST; 	
    		}
	      	
    		
	    	    	
	    	String Complete_URI = Loc_URI +"/"+((String) getRequestAttributes().get("node-id")).replaceAll("-e", "");
	    	
	    	System.out.println("*** URI: "+Complete_URI+"***");
	    	
	    	if(Utils.isDevice(Loc_URI, dev.getAddress()) != null){
	    			    		
					try {
						
						setLocationValue(req, Complete_URI,  entity.getText());
						
						dev.setLastHeardTime(System.currentTimeMillis());
						
						return ("{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+
								" was localizated successfully \"}");
					} catch (IOException e) {
						e.printStackTrace();
					}
	    	}
       	}
    	
    	return ("{\"responseCode\":\"The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+" does not exist\"}");
	}
				
	@Post("json:json")
	public String setConfigLocalization(Representation entity){
		
		Device dev = DeviceManager.findByName((String) getRequestAttributes().get("thing-id")+"/"+(String) getRequestAttributes().get("node-id"));
    				
    	String Loc_URI = null;
    	Constants.SensorRet req = null;
    	
    	if(dev != null){
    		
    		if(dev.isLocal()){
    			Loc_URI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+
    	        (String) getRequestAttributes().get("thing-id");
    			req = Constants.SensorRet.LOCAL_DRIVER;
    //			System.out.println("*** URI: "+Loc_URI+"*** ES LOCAL");
    			
    		}else{
    			//If the device is not local, reformat Id for building the original URI
    			Loc_URI = dev.getUri().replaceAll("/"+dev.getAddress(), "");
    			    			
    //			System.out.println("*** URI: "+Loc_URI+"*** NO ES LOCAL");
    			
    			req = Constants.SensorRet.REST; 	
    		}
	      	 		
	    	    	
	    	String Complete_URI = Loc_URI +"/"+((String) getRequestAttributes().get("node-id")).replaceAll("-e", "");
	    	
	    //	System.out.println("*** URI: "+Complete_URI+"***");
	    	
	    	if(Utils.isDevice(Loc_URI, ((String) getRequestAttributes().get("node-id")).replaceAll("-e", "")) != null){
	    		
					try {
						return locSystemConfig(req, 
								Integer.valueOf((String) getRequestAttributes().get("node-id")), Complete_URI,  entity.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
	    	}
       	}
    	
    	return ("The "+(String) getRequestAttributes().get("thing-id")+" with Id "+ (String) getRequestAttributes().get("node-id")+" does not exist");
	}
	
	private String getLocationValue(Constants.SensorRet method, String devURI) {
		
		String result=null;
		
		switch(method){
			case DB:
				
				// Get the latest measure of a sensor reading
								
    			result = lastLoc(devURI);
							
			break;
			
			case REST:
				// Get a sensor reading from external resource
				 ClientResource restClient = new ClientResource(devURI+"/localization");
				try {
					result = get(restClient);
				} catch (ResourceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				 
			break;
		}
		    	
		return result;
		
	}
 
	
	 
	 private String locSystemConfig(Constants.SensorRet method, int nodeId,String devURI, String configuration){
		 
		 String result=null;
			
			switch(method){
				case LOCAL_DRIVER:
					
					// Config localization system
					
					try {
						JSONObject config = new JSONObject(configuration);
					
					
					//Micaz RSS-base loc system
	    			if(((String) config.get("locSystem")).compareTo("RSS") == 0){
	    				
	    				result = LocalizationManager.configRSSLocalization(nodeId, config);
	    					    					    				
	    			//Cricket-base loc system
	    			}else if(((String) config.get("locSystem")).compareTo("cricket") == 0){
	    				result = "Cricket system not enabled";
	    			}else {
	    				result = (String) config.get("locSystem")+" is not allowed localization system";
	    			}
	    			
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
								
				break;
				
				case REST:
					// Send the configuration message to the indicated Gw
					 ClientResource restClient = new ClientResource(devURI);
					 
					try {
						result = post(restClient, configuration);
					} catch (ResourceException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					 
				break;
			}
			    	
			return result;
		 
	 }
	 
	 private void setLocationValue(Constants.SensorRet method, String devURI, String localization){
			
			switch(method){
				case DB:
					
					// Set localization
																				
					System.out.println("*** Inserta localizacion en Base Datos:"+localization+" ***");
					Utils.setDeviceLocalization(devURI, localization);				
	    										
				break;
				
				case REST:
					// Send the localization message to the indicated Gw
					 ClientResource restClient = new ClientResource(devURI);
					 
					try {
						put(restClient, localization);
					} catch (ResourceException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					 
				break;
			}
		 
	 }

	private String lastLoc(String devURI) {
		
		String result=null;
		
		// Get the latest measure of a sensor reading
		result = Utils.getDeviceLoc(devURI, 0);
		
	//	System.out.println("*** Resultado loc "+devURI+ ": "+result);
						
		return result;
	 }
	 
	 /**
	  * REST client
	  * @param clientResource
	  * @return
	  * @throws IOException
	  * @throws ResourceException
	  */
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
	 
	 /**
	  * REST client
	  * @param clientResource
	  * @return
	  * @throws IOException
	  * @throws ResourceException
	  */
	 public static String put(ClientResource clientResource, String message) throws IOException, ResourceException {  
	    	
		   Representation r = clientResource.put(message);	    	 	
	    
		   String result = null;
		   
	        if (clientResource.getStatus().isSuccess()
	               && clientResource.getResponseEntity().isAvailable()) {  
	                       	
	            result = clientResource.getResponseEntity().getText();	                                    
	     	           	  	            
	        }
	        
	        clientResource.release();
	        r.release();
	        
	        return result;
			       
	 }
	 
	 /**
	  * REST client
	  * @param clientResource
	  * @return
	  * @throws IOException
	  * @throws ResourceException
	  */
	 public static String post(ClientResource clientResource, String message) throws IOException, ResourceException {  
	    	
		   Representation r = clientResource.post(message);	    	 	
	    
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
