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
 * @author Iv�n
 *
 */
public class SmartThingLocEvent extends ServerResource{
	
	@Put
	public void eventReceived(Representation message){
		
				
		String thingId = (String) getRequestAttributes().get("thing-id");
	    String nodeId = (String) getRequestAttributes().get("node-id");
	 		
	    //Producer URI of the subscription: 
		String ProducerURI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/";
		String ProducerId = thingId+"/"+nodeId+"/localization";
		
		Device dev = DeviceManager.findByName(thingId+"/"+nodeId);
		
    	String Loc_URI = null;
    	
    	if(dev != null){
    		
    		Loc_URI = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+thingId+"/"+nodeId;
  	    	
	    	System.out.println("***Evento de localizacion para "+Loc_URI+"***");
	    		    
	    			    		
			try {
						
				String eventText = message.getText();
				setLocationValue(Loc_URI, eventText);						
						
				setEvent(ProducerURI, ProducerId, eventText);
						
				dev.setLastHeardTime(System.currentTimeMillis());
						
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
       	}
    
		    
	}
	
   public void setEvent(String ProducerURI, String ProducerId, String eventText){
	   
	   	PubSubCore psc = PubSubCore.getInstance();
		EventManager em = EventManager.getInstance();
   
	    try {
	    	
	    	JSONObject evAux = new JSONObject(eventText);
			JSONArray payloadAux = (JSONArray) evAux.get("payload");
			Event ev;
			ConcurrentHashMap<Integer, Subscription> subsList;
			    
		    ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
		    
		    for(int i=0; i < payloadAux.length(); i++){
		    	
		    	payload.add(payloadAux.getJSONObject(i));
		    }
    
		    subsList = psc.getTSubscriptionByProducer(ProducerURI, ProducerId);
		    
		    if(subsList.size() > 0){
		    	 ev = new Event(ProducerURI, ProducerId, Constants.THRESHOLD, System.currentTimeMillis(), payload);		    	 
		    	 System.out.println("*** Evento de localizacion Threshold preparado : "+ev.toString());		    	 
		    	 em.setEvent(ev);
		    }
		    
		    subsList = psc.getCSubscriptionByProducer(ProducerURI, ProducerId);
		    if(subsList.size() > 0){
		    	 ev = new Event(ProducerURI, ProducerId, Constants.CONTRACT, System.currentTimeMillis(), payload);
		    	 System.out.println("*** Evento localizacion Contrato preparado: "+ev.toString());	
		    	 em.setEvent(ev);
		    }
	    
	    } catch (JSONException e) {
			e.printStackTrace();
		}		
		
	}
   
   private void setLocationValue(String devURI, String eventLoc){
							
	   try{
		   
		   JSONObject localization = new JSONObject();
		   JSONObject locAux = new JSONObject(eventLoc);
		   JSONArray payload = (JSONArray) locAux.get("payload");
		   
		   locAux = (JSONObject) payload.get(0);
		   localization.put("x", locAux.get("value"));
		   
		   locAux = (JSONObject) payload.get(1);
		   localization.put("y", locAux.get("value"));
		   
		   locAux = (JSONObject) payload.get(2);
		   localization.put("z", locAux.get("value"));
		   		   
		   System.out.println("*** Inserta localización en Base Datos:"+localization+" ***");
		   Utils.setDeviceLocalization(devURI, localization.toString());						
   			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
  }
  
}