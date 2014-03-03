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
package es.upm.ssr.grpss.thofu.adquisicion.gw.component.subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Subscription;

/**
 * @author Iv�n
 *
 */
public class SubscriptionManager extends ServerResource{
	
	@Get(":json")
	public String getSubscriptionStatus(){
		
		Form queryParams = getQuery();

		String consumer = queryParams.getFirstValue("consumer");
						
		PubSubCore psc = PubSubCore.getInstance();
		
		ConcurrentHashMap<Integer, Subscription> Csubs = psc.getCSubscriptionbyConsumer(consumer);
		ConcurrentHashMap<Integer, Subscription> Tsubs = psc.getTSubscriptionbyConsumer(consumer);
		
		JSONArray subscriptions = new JSONArray();
		
		Iterator<Integer> subs;
		JSONObject subsAux;
		Subscription subscription;
		int code;
		
		if(Csubs != null){
									
			subs = (Csubs.keySet()).iterator();
			
			while(subs.hasNext()){
					
					code = subs.next();
					
					subsAux = new JSONObject();
					
					subscription = (Subscription) Csubs.get(code);
																				
					try {
						
						subsAux.put("subscriptionCode", String.valueOf(code));
						subsAux.put("subscription", new JSONObject(subscription.getSubscription()));
						
						subscriptions.put(subsAux);
												
					} catch (JSONException e) {
						e.printStackTrace();
					}
										
			}
			
		}
		
		if(Tsubs != null){
			
			subs = (Tsubs.keySet()).iterator();
			
			while(subs.hasNext()){
					
					code = subs.next();
					
					subsAux = new JSONObject();
					
					subscription = (Subscription) Tsubs.get(code);
																				
					try {
						
						subsAux.put("subscriptionCode", String.valueOf(code));
						subsAux.put("subscription", new JSONObject(subscription.getSubscription()));
						
						subscriptions.put(subsAux);
												
					} catch (JSONException e) {
						e.printStackTrace();
					}
										
			}
			
			
		}
		
		return subscriptions.toString();
		
	}
	
	@Post("json:json")	
	public String Subscriber(Representation entity){
		
		System.out.println("*** POST Subscriber ***");
		
		String component = (String) getRequestAttributes().get("Component");
		
		int subCode = 0;
		JSONObject response = new JSONObject();
		
		try {
				
			
			JSONObject message = new JSONObject(entity.getText());
			
			System.out.println("JSON subscription: "+message.toString());
			
			PubSubCore psc = PubSubCore.getInstance();
						
			if(component.compareTo("subscriber") == 0){
				
				String forwarderDriver = null;
							
				Subscription sub = new Subscription(message);
												
				forwarderDriver = sub.getSubscriptionForwarder();
				
				
				if(forwarderDriver != null){
					subCode = psc.Subscribe(sub, Constants.Driver.valueOf(forwarderDriver));
				}else {
					subCode = psc.Subscribe(sub, null);
				}
				
				response.put("status", "OK");
				response.put("subscriptionCode", String.valueOf(subCode));
				
			}else if(component.compareTo("unsubscriber") == 0){
				
				String consumerURI = (String) ((JSONObject)(message.get("consumer"))).get("URI");
				String consumerId = (String) ((JSONObject)(message.get("consumer"))).get("Id");
				String eventType = (String) message.get("eventType");
				int subsCode = Integer.valueOf((String) message.get("subscriptionCode"));
																								
				psc.unSubscribe(consumerURI, eventType, subsCode);
				
				response.put("unSubscriptionStatus", "OK");		
				
			} else {
				response.put("unSubscriptionStatus", "Error");
			}
			
		} catch (JSONException e) {			
			e.printStackTrace();			
			return ("Error in Subscription");
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		return (response.toString());
	}	
	
	@Delete
	public String removeSubscriptions(){
		
		System.out.println("*** DELETE Subscriptions ***");
		
		String component = (String) getRequestAttributes().get("Component");
		
		if(component.equalsIgnoreCase("removesubscriptions")){
		
			PubSubCore psc = PubSubCore.getInstance();
			
			ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Csubs = psc.getCSubscriptionList();
			ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Tsubs = psc.getTSubscriptionList();
			
			ConcurrentHashMap<Integer, Subscription> conCsubs = new ConcurrentHashMap<Integer, Subscription>();
			ConcurrentHashMap<Integer, Subscription> conTsubs = new ConcurrentHashMap<Integer, Subscription>();
		
			Iterator<String> cons;
			Iterator<Integer> subs;
			
			ArrayList<Integer> codes = new ArrayList();
		
			Subscription subscription;
			int code;
			
			if(Csubs != null){
					
				cons = (Csubs.keySet()).iterator();
				
				while(cons.hasNext()){
					
					conCsubs = Csubs.get(cons.next());
					subs = (conCsubs.keySet()).iterator();				
					
					while(subs.hasNext()){
							
						codes.add(subs.next());					
												
					}	
					
					for(int i=0; i <= codes.size(); i++){
						subscription = (Subscription) conCsubs.get(codes.get(i));						
						psc.unSubscribe(subscription.getConsumerURI(), "2", codes.get(i));
					}
				}		
			}
			
			if(Tsubs != null){
				
				cons = (Tsubs.keySet()).iterator();
				
				while(cons.hasNext()){
					
					conTsubs = Tsubs.get(cons.next());
					subs = (conTsubs.keySet()).iterator();				
					
					while(subs.hasNext()){
							
						code = subs.next();
										
						subscription = (Subscription) conTsubs.get(code);
																						
						psc.unSubscribe(subscription.getConsumerURI(), "1", code);
												
					}				
				}				
				
			}
			
			return "Eliminadas "+conTsubs.size()+" subscripciones Threshold"+
					" y "+conCsubs.size()+" bajo contrato";
			
		}
		
		return null;
	}	

}
