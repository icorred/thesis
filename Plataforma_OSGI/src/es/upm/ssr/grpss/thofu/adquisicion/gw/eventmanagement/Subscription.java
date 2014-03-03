
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
package es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;

/**
 * @author Iván Corredor
 *
 */
public class Subscription {
	
	private String ConsumerURI;
	private String ConsumerId;
	private String ProducerURI;
	private String ProducerId; 
	
	private String type;
	
	//Payload of interest
	private ArrayList<JSONObject> eventPayload;
	
	/*For subscription under contract*/
	private long start;
	private long end;
	private long period;
	
	/*JSON format*/
	private JSONObject jsonSubscription;
	
	//Forwarder flag
	private String forwarderDriver;
	
	/**
	 * Empty subscription
	 */
	public Subscription(){
				
	}
	
	/**
	 * Constructor for a threshold event subscription.
	 * 
	 * @param ConsumerURI - Root URI of the event consumer.
	 * @param Consumer Id - Consumer Id belonging to the root URI.
	 * @param ProducerURI -  Root URI of the event producer.
	 * @param Payload - Information of interest characterizing a threshold event.
	 * This parameter is an ArrayList of JSON Objects  (See JSONObject).
	 * @param end - Indicates the end time for the subscription. 
	 * (reference Unix Epoch).
	 */
	public Subscription(String ConsumerURI, String ConsumerId, String ProducerURI, String ProducerId, 
			ArrayList<JSONObject> payload, long end){
		
		this.ConsumerURI = ConsumerURI;
		this.ConsumerId = ConsumerId;
		
		this.ProducerURI = ProducerURI;
		this.ProducerId = ProducerId;
		
		this.type = Constants.THRESHOLD;
		
		this.end = end;
		
		jsonSubscription = new JSONObject();
		JSONObject jsonAux = new JSONObject();
	
		
		/*** Construct a JSON document ***/
		try {
			//Consumer information
			jsonAux.put("URI", ConsumerURI);
			jsonAux.put("Id", ConsumerId);
			jsonSubscription.accumulate("consumer", jsonAux);
			
			//Producer information
			jsonAux = new JSONObject();
			jsonAux.put("URI", ProducerURI);
			jsonAux.put("Id", ProducerId);
			jsonSubscription.accumulate("producer", jsonAux);
			
			jsonSubscription.put("type", type);
			jsonSubscription.put("end", end);
			
			//Interest event
			for(int i = 0; i < payload.size(); i++){
				
				jsonAux = new JSONObject();
				jsonAux = payload.get(i);
							
				jsonAux.get("key");
				jsonAux.get("unit");
				jsonAux.get("value");
				jsonAux.get("operator");
					
				jsonSubscription.accumulate("event", jsonAux);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * Constructor for a threshold event subscription.
	 * 
	 * @param ConsumerURI - Root URI of the event consumer.
	 * @param Consumer Id - Consumer Id belonging to the root URI.
	 * @param ProducerURI -  Root URI of the event producer.
	 * @param Payload - Information of interest characterizing a threshold event.
	 * This parameter is an ArrayList of JSON Objects  (See JSONObject).
	 * @param end - Indicates the end time for the subscription. 
	 * (reference Unix Epoch).
	 * @param driver - Id of the driver to forward the subscription  
	 */
	public Subscription(String ConsumerURI, String ConsumerId, String ProducerURI, String ProducerId, 
			ArrayList<JSONObject> payload, long end, String driver){
		
		this.ConsumerURI = ConsumerURI;
		this.ConsumerId = ConsumerId;
		
		this.ProducerURI = ProducerURI;
		this.ProducerId = ProducerId;
		
		this.type = Constants.THRESHOLD;
		
		this.end = end;
		
		jsonSubscription = new JSONObject();
		JSONObject jsonAux = new JSONObject();
	
		
		/*** Construct a JSON document ***/
		try {
			//Consumer information
			jsonAux.put("URI", ConsumerURI);
			jsonAux.put("Id", ConsumerId);
			jsonSubscription.accumulate("consumer", jsonAux);
			
			//Producer information
			jsonAux = new JSONObject();
			jsonAux.put("URI", ProducerURI);
			jsonAux.put("Id", ProducerId);
			jsonSubscription.accumulate("producer", jsonAux);
			
			jsonSubscription.put("type", type);
			jsonSubscription.put("end", end);
			
			if(driver != null){
				forwarderDriver = driver;
				jsonSubscription.put("subscriptionForwarder", driver);
			}
			
			//Interest event
			for(int i = 0; i < payload.size(); i++){
				
				jsonAux = new JSONObject();
				jsonAux = payload.get(i);
							
				jsonAux.get("key");
				jsonAux.get("unit");
				jsonAux.get("value");
				jsonAux.get("operator");
					
				jsonSubscription.accumulate("event", jsonAux);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * Constructor for a contract event subscription.
	 * 
	 * @param ConsumerURI - Root URI of the event consumer.
	 * @param Consumer Id - Consumer Id belonging to the root URI.
	 * @param ProducerURI -  Root URI of the event producer.
	 * @param Payload - Information of interest characterizing the event by contract. 
	 * This parameter is an ArrayList of JSON Objects  (See JSONObject).
	 * @param start - Indicates the starting time (in milliseconds) for the subscription.
	 * (reference Unix Epoch).
	 * @param end - Indicates the end time (in milliseconds) for the subscription 
	 * (reference Unix Epoch). 
	 * @param period - Period until forwarding to the consumer any event in the buffer.
	 */
	public Subscription(String ConsumerURI, String ConsumerId, String ProducerURI, String ProducerId,
			ArrayList<JSONObject> payload, long start, long end, long period){
		
		this.ConsumerURI = ConsumerURI;
		this.ConsumerId = ConsumerId;
		
		this.ProducerURI = ProducerURI;
		this.ProducerId = ProducerId;
		
		this.type = Constants.CONTRACT;
			
		jsonSubscription = new JSONObject();
		JSONObject jsonAux = new JSONObject();
		
		/*** Construct a JSON document ***/
		try {
			//Consumer information
			jsonAux.put("URI", ConsumerURI);
			jsonAux.put("Id", ConsumerId);
			jsonSubscription.accumulate("consumer", jsonAux);
			
			//Producer information
			jsonAux = new JSONObject();
			jsonAux.put("URI", ProducerURI);
			jsonAux.put("Id", ProducerId);
			jsonSubscription.accumulate("producer", jsonAux);
			
			jsonSubscription.put("type", type);
			jsonSubscription.put("start", start);
			jsonSubscription.put("end", end);
			jsonSubscription.put("samplingPeriod", period);
							
			//Interest event
			for(int i = 0; i < payload.size(); i++){
				
				jsonAux = new JSONObject();
				jsonAux = payload.get(i);
							
				jsonAux.get("key");
				jsonAux.get("unit");
				
				jsonSubscription.accumulate("event", jsonAux);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * This constructor can be used for either contract events or
	 * threshold events.
	 *  
	 * @param jsonSubscription - A JSON Object containing a subscription as
	 * it is defined in Platform specification.
	 */
	public Subscription(JSONObject jsonSubscription){
		
		try {
			JSONObject jsonAux = new JSONObject();
			
			//Consumer information
			jsonAux = (JSONObject) jsonSubscription.get("consumer");
			this.ConsumerURI = (String) jsonAux.get("URI");
			this.ConsumerId = (String) jsonAux.get("Id");
			
			//Consumer information
			jsonAux = (JSONObject) jsonSubscription.get("producer");
			this.ProducerURI = (String) jsonAux.get("URI");
			this.ProducerId = (String) jsonAux.get("Id");
						
			JSONArray jsonArrayAux = null;
			//Event information		
			if(jsonSubscription.get("event") instanceof JSONArray){
				
				jsonArrayAux = (JSONArray) jsonSubscription.get("event");
												
			} else if(jsonSubscription.get("event") instanceof JSONObject){
				
				jsonAux = (JSONObject) jsonSubscription.get("event");
								
			}
			
			this.type = (String) jsonSubscription.get("type");
							
			if(this.type.compareTo(Constants.THRESHOLD) == 0){
				
				this.end = (Long) jsonSubscription.getLong("end");
				
				if(jsonArrayAux != null){
					
					eventPayload = new ArrayList<JSONObject>(jsonArrayAux.length());
					
					for(int i = 0; i < jsonArrayAux.length(); i++){
					
						JSONObject jsonAux2 = new JSONObject(jsonArrayAux.get(i).toString());
						
						//Access every field in order to check if it is well-formed
						jsonAux2.get("key");
						jsonAux2.get("unit");
						jsonAux2.get("value");
						jsonAux2.get("operator");
						
						eventPayload.add(jsonAux2);
						
					}
					
				}else {
					
			       eventPayload = new ArrayList<JSONObject>(1);
					//Access every field in order to check if it is well-formed
					jsonAux.get("key");
					jsonAux.get("unit");
					jsonAux.get("value");
					jsonAux.get("operator");
					
					eventPayload.add(jsonAux);
					
				}
				
			}else if(this.type.compareTo(Constants.CONTRACT) == 0){
				
				this.start = (Long) jsonSubscription.getLong("start");
				this.end = (Long) jsonSubscription.getLong("end");
				this.period = (Long) jsonSubscription.getLong("samplingPeriod");				
				
				if(jsonArrayAux != null){
										
					eventPayload = new ArrayList<JSONObject>(jsonArrayAux.length());
					
					for(int i = 0; i < jsonArrayAux.length(); i++){
						
						JSONObject jsonAux2 = new JSONObject(jsonArrayAux.get(i).toString());
						
						//Access every field in order to check if it is well-formed
						jsonAux2.get("key");
						jsonAux2.get("unit");
												
						eventPayload.add(jsonAux2);					
					}
					
				}else {
					
					eventPayload = new ArrayList<JSONObject>();
					//Access every field in order to check if it is well-formed
					jsonAux.get("key");
					jsonAux.get("unit");
											
					eventPayload.add(jsonAux);	
					
				}
				
				
			}
			
						
			if(jsonSubscription.has("subscriptionForwarder")){				
				this.forwarderDriver = (String) jsonSubscription.get("subscriptionForwarder");				
			}else {
				this.forwarderDriver = null;
			}
			
			
			this.jsonSubscription = jsonSubscription;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}				
	}
	
	/**
	 * Set the Consumer URI of the subscription
	 * @param ConsumerURI - Consumer URI 
	 */
	public void setConsumerURI(String ConsumerURI){
		this.ConsumerURI = ConsumerURI;
	}
	
	/**
	 * Set the Consumer Id of the subscription
	 * @param ConsumerId - Consumer Id 
	 */
	public void setConsumerId(String ConsumerId){
		this.ConsumerId = ConsumerId;
	}
	
	/**
	 * Set the Consumer URI of the subscription
	 * @param ProducerURI - Producer URI 
	 */
	public void setProducerURI(String ProducerURI){
		this.ProducerURI = ProducerURI;
	}
	
	/**
	 * Set the Producer Id of the subscription
	 * @param ProducerId - Producer Id 
	 */
	public void setProducerId(String ProducerId){
		this.ProducerId = ProducerId;
	}
	
	/**
	 * Set the event type of the subscription
	 * @param type - the event type for which the subscription is built.
	 */
	public void setType(String type){
		this.type = type;	
	}
	
	/**
	 * 	Set the start time of the subscription
	 * @param start - the start time of the subscription.
	 */
	public void setStart(long start){
		this.start = start;
	}
	
	/**
	 * Set the end time of the subscription
	 * @param end - the end time of the subscription.
	 */
	public void setEnd(long end){
		 this.end = end;
	}
	
	/**
	 * Set the period for contract-based subscription.
	 * @param period - the period for contract-based subscription.
	 */
	public void setPeriod(long period){
		this.period = period;
	}
	
	/**
	 * 
	 * @param eventPayload - the payload indicating interesting events
	 */
	public void setPayloadInterest(ArrayList<JSONObject> eventPayload){
		this.eventPayload = eventPayload;		
	}
	
	/**
	 * 	
	 * @param sf - the id of the driver to forward the subscription
	 */
	public void setSubscriptionForwarder(String sf){
		this.forwarderDriver = sf;		
	}
	
	/**
	 * 
	 * @return the Consumer URI of the subscription
	 */
	public String getConsumerURI(){
		return ConsumerURI;
	}
	
	/**
	 * 
	 * @return the Consumer Id of the subscription
	 */
	public String getConsumerId(){
		return ConsumerId;
	}
	
	/**
	 * 
	 * @return the Producer URI of the subscription
	 */
	public String getProducerURI(){
		return ProducerURI;
	}

	/**
	 * 
	 * @return the Producer Id of the subscription
	 */
	public String getProducerId(){
		return ProducerId;
	}
	
	/**
	 * 
	 * @return the event type for which the subscription is built.
	 */
	public String getType(){
		return type;	
	}
	
	/**
	 * 	
	 * @return the start time of the subscription.
	 */
	public long getStart(){
		return start;
	}
	
	/**
	 * 
	 * @return the end time of the subscription.
	 */
	public long getEnd(){
		return end;
	}
	
	/**
	 * 
	 * @return the period for contract-based subscription.
	 */
	public long getPeriod(){
		return period;
	}
	
	/**
	 * 
	 * @return the id of the driver to forward the subscription 
	 */
	public String getSubscriptionForwarder(){
		return forwarderDriver;
	}
	
	/**
	 * 
	 * @return the payload indicating interesting events
	 */
	public ArrayList<JSONObject> getPayloadInterest(){
		return eventPayload;		
	}
	
	/**
	 * 
	 * @return a String of the subscription (JSON format)
	 */
	public String getSubscription(){
		return jsonSubscription.toString();
	}
}
