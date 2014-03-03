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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Event;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.EventManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Subscription;

/**
 * @author Ivan Corredor
 *
 */
public class SmartThingEvent extends ServerResource{
	
	String ProducerURI;
	String ProducerId;
	String bodyMessage;
	
	Device dev;
	
	@Put
	public void eventReceived(Representation message){
		
		String thingId = (String) getRequestAttributes().get("thing-id");
	    String nodeId = (String) getRequestAttributes().get("node-id");
	    String sensor = (String) getRequestAttributes().get("sensor");
	 		
	    //Producer URI of the subscription: 
		ProducerURI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/";
		ProducerId = thingId+"/"+nodeId+"/sensor/"+sensor;
		
		dev = DeviceManager.findByName(thingId+"/"+nodeId);
    	 	
    	if(updateSensorStatus(ProducerURI+ProducerId,ProducerURI, dev)){
    	 	
  	    	
	   // 	System.out.println("*** URI: "+Loc_URI+"***");
    		try {
				bodyMessage = message.getText();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		Thread eventThread = new Thread( new Runnable() {
    			
    			public void run() {
    				dev.setLastHeardTime(System.currentTimeMillis());
    				setEvent(ProducerURI, ProducerId, bodyMessage);
    			}
    				
    		}, "smartthing-event");
			
    		eventThread.start();
	    }
       			    
	}
	
   public void setEvent(String ProducerURI, String ProducerId, String eventText){
	   
	   	PubSubCore psc = PubSubCore.getInstance();
		EventManager em = EventManager.getInstance();
   
	    try {
	    	
	    	JSONObject evAux = new JSONObject(eventText);
	    	
	    	//System.out.println("*** Payload evento: "+(String) evAux.get("payload"));
	    	
			JSONArray payloadAux = new JSONArray((String) evAux.get("payload"));
			Event ev;
			ConcurrentHashMap<Integer, Subscription> subsList;
									    
		    ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
		    
		    JSONObject p = new JSONObject();
		    
		    
		    for(int i=0; i < payloadAux.length(); i++){
		    	
		    	payload.add(payloadAux.getJSONObject(i));
		    	
		    	if(payloadAux.length() > 1){
		    		p.put("value"+i+1, (String) payloadAux.getJSONObject(i).get("value"));
		    		    		
		    	}else{
		    		
		    		dev.setValueCache(ProducerURI+ProducerId, 
		    				(String) payloadAux.getJSONObject(i).get("value"));		    		
		    	}
		    	
		    }
		    
		    if(p.length() > 1){
		    	
		    	dev.setValueCache(ProducerURI+ProducerId, p.toString());
		    	
		    }		    
		    
		    dev.setLastHeardTime(System.currentTimeMillis());
		    
		    subsList = psc.getTSubscriptionByProducer(ProducerURI, ProducerId);
		    
		    if(subsList.size() > 0){
		    	 ev = new Event(ProducerURI, ProducerId, Constants.THRESHOLD, System.currentTimeMillis(), payload);		    	 
		    //	 System.out.println("*** Evento Threshold preparado : "+ev.toString());		    	 
		    	 em.setEvent(ev);
		    }
		    
		    subsList = psc.getCSubscriptionByProducer(ProducerURI, ProducerId);
		    if(subsList.size() > 0){
		    	 ev = new Event(ProducerURI, ProducerId, Constants.CONTRACT, System.currentTimeMillis(), payload);
		    	 System.out.println("*** Evento Contrato preparado: "+ev.toString());	
		    	 em.setEvent(ev);
		    }
	    
	    } catch (JSONException e) {
			e.printStackTrace();
		}		
		
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
	
	

}
