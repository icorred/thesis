/**
 * Copyright (c) 2013, Data Processing and Simulation Group 
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import driverinterfaces.IMicaZDriver;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.driver.DriverManager;

/**
 * @author Iv�n
 *
 */
public class PubSubCore{
	
	/*** List of Tables ***/
	//Subscription Table
	//Consumer URI --> Consumer Id --> List of Subscriptions
	//Threshold subscription
	private ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Tsubscriptions;
	//Contract subscription
	private ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Csubscriptions;
	
	//Publication Table
	//Producer URI --> Producer Id --> List of Publications
	private ConcurrentHashMap<String, ConcurrentHashMap<Integer, Publication>> publications;
		
	private static PubSubCore psc;
	
	private static ThresholdManager tm;
	private static ContractManager cm;
	
	public PubSubCore(){
						
		//Init tables
		Tsubscriptions = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>>(Constants.MAX_CONSUMERS);
		Csubscriptions = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>>(Constants.MAX_CONSUMERS);
		
		publications = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Publication>>();
		
		//Instantiate ThresholdManager
		tm = ThresholdManager.getInstance();
		cm = ContractManager.getInstance();
			
	}
	
	public static PubSubCore getInstance(){
		
		if(psc == null){
			psc = new PubSubCore();	
		}		
		
		return psc;		
	}
	
	public boolean Publish(Publication pub){
		
		//TO-DO
		return true;
		
	}
	
	public synchronized int  Subscribe(Subscription sub, Constants.Driver subsForwarder){
		
		ConcurrentHashMap<Integer, Subscription> subscriptionsById;
		
		int hashCode = 0;			
				
			
			if(sub.getType().compareTo(Constants.THRESHOLD)==0){
							
				//There is not a subscription list for this Consumer
				if(!Tsubscriptions.containsKey(sub.getConsumerURI())){
					
					Tsubscriptions.put(sub.getConsumerURI(), new ConcurrentHashMap<Integer, Subscription>(Constants.MAX_SUBS_PER_CONS));
								
				}
				
				//Get the subscription list for this consumer
				subscriptionsById = Tsubscriptions.get(sub.getConsumerURI());
				hashCode = sub.hashCode();
				//Set start time for this subscription
				sub.setStart(System.currentTimeMillis());
				subscriptionsById.put(hashCode, sub);
				
				if(!tm.isStarted()){
										
					tm.initThresholdTask();
				}
				
				if(subsForwarder != null){
					try {
						
						DriverManager dm = DriverManager.getInstance();
						
						switch(subsForwarder){
							
						//Configure MicaZ Threshold
							case MICAZ:
														
								IMicaZDriver mph = (IMicaZDriver) dm.getDriver("MICAZ");
								
								ArrayList<JSONObject> poi = sub.getPayloadInterest();
								
								Hashtable<String, Double> envValues = new Hashtable<String, Double>();
															
								envValues.put("temp",0.0);
								envValues.put("hum", 0.0);
								
								for(int i=0; i < poi.size(); i++){
									
									if(((String) poi.get(i).get("key")).compareTo("temp")==0){
										envValues.put("temp", Double.valueOf((String) poi.get(i).get("value")));																			
									}else if(((String) poi.get(i).get("key")).compareTo("hum")==0){
										envValues.put("hum", Double.valueOf((String) poi.get(i).get("value")));
									}								
								}
								
								//Set new thresholds on motes
								mph.setEnvThreshold((short) 2, envValues.get("temp"), envValues.get("hum"));
								
							break;
							
						default:
							break;
						
						}
						
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}					
				}
				
			}else if( sub.getType().compareTo(Constants.CONTRACT)==0){
				
				//There is not a subscription list for this Consumer
				if(!Csubscriptions.containsKey(sub.getConsumerURI())){
					
					Csubscriptions.put(sub.getConsumerURI(), new ConcurrentHashMap<Integer, Subscription>(Constants.MAX_SUBS_PER_CONS));
								
				}
				
				//Get the subscription list for this consumer
				subscriptionsById = Csubscriptions.get(sub.getConsumerURI());
				hashCode = sub.hashCode();
				subscriptionsById.put(hashCode, sub);
				
				System.out.println("*** Se registro una subscripcion bajo contrato: "+sub.getSubscription());
				System.out.println("*** Inicio: "+sub.getStart()+" final: "+sub.getEnd()+" periodo: "+sub.getPeriod());
				
				//Init a task for getting events from contract event table
				cm = ContractManager.getInstance();
				
				cm.initContractTask(sub, hashCode);
				
			}
				
		
		return hashCode;
		
	}
	
	
	public boolean unPublish(Publication pub){
						
		return true;
		
	}
	
	public boolean unSubscribe(String ConsumerURI, String eventType, int subsCode){
		
		ConcurrentHashMap<Integer, Subscription> subscriptionsById;
						
		
		if(eventType.compareTo(Constants.THRESHOLD)==0){
				
				if(Tsubscriptions.containsKey(ConsumerURI)){
					//Get the subscription list for this consumer
					subscriptionsById = Tsubscriptions.get(ConsumerURI);
					
					//If there is a subscription with that code
					if(subscriptionsById.containsKey(subsCode)){
					
						//Remove subscription from smart objects if there are some
						Subscription sub = (Subscription) subscriptionsById.get(subsCode);
						
						if(sub.getSubscriptionForwarder() != null){
							
							try{
								
							switch(Constants.Driver.valueOf(sub.getSubscriptionForwarder())){
							
							case MICAZ:
								
								DriverManager dm = DriverManager.getInstance();
								
								IMicaZDriver mph = (IMicaZDriver) dm.getDriver("MICAZ");
								
								
								ArrayList<JSONObject> poi = sub.getPayloadInterest();
								
								Hashtable<String, Double> envValues = new Hashtable<String, Double>();
								
								envValues.put("temp",0.0);
								envValues.put("hum", 0.0);
								
								for(int i=0; i < poi.size(); i++){
									
									if(((String) poi.get(i).get("key")).compareTo("temp")==0){
										envValues.put("temp", Double.valueOf((String) poi.get(i).get("value")));																			
									}else if(((String) poi.get(i).get("key")).compareTo("hum")==0){
										envValues.put("hum", Double.valueOf((String) poi.get(i).get("value")));
									}							
								}
								
								//Remove thresholds on motes								
								mph.setEnvThreshold((short) 1, envValues.get("temp"), envValues.get("hum"));
								
								break;
								
							default:
								break;
							}
							
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
																	
						subscriptionsById.remove(subsCode);
						
						if(subscriptionsById.isEmpty()){
							Tsubscriptions.remove(ConsumerURI);
						}
						
						System.out.println("*** Tamanno lista Subscripciones THRESHOLD: "+subscriptionsById.size()+
								", para Consumer: "+ConsumerURI);						
						
					}
															
					if(Tsubscriptions.isEmpty() && tm.isStarted()){
						tm.stopThresholTask();
					}
		    	}
				
			
		}else if(eventType.compareTo(Constants.CONTRACT)==0){
		    	
		    	if(Csubscriptions.containsKey(ConsumerURI)){
					//Get the subscription list for this consumer
					subscriptionsById = Csubscriptions.get(ConsumerURI);
					
					//If there is a subscription with that code
					if(subscriptionsById.containsKey(subsCode)){
																	
						subscriptionsById.remove(subsCode);
						
						if(subscriptionsById.isEmpty()){
							Csubscriptions.remove(ConsumerURI);
						}
						
						System.out.println("*** Tamanno lista Subscripciones CONTRACT: "+subscriptionsById.size());
												
					}
		    	}
					
			    ContractManager cm = ContractManager.getInstance();					
				cm.stopContract(subsCode, ConsumerURI);
						
			    	  			
		}
			   
		   return true;
		
	}	
	
	public ConcurrentHashMap<Integer, Subscription> getTSubscriptionByProducer(String ProducerURI, String ProducerId){
		
		ConcurrentHashMap<Integer, Subscription> subsList = new ConcurrentHashMap<Integer, Subscription>();
		
		Iterator<String> cons = (Tsubscriptions.keySet()).iterator();
		
		while(cons.hasNext()){
			
			String str = cons.next();
			
			ConcurrentHashMap<Integer, Subscription> subsAux = (ConcurrentHashMap<Integer, Subscription>) Tsubscriptions.get(str);
						
			Iterator<Integer> subs = (subsAux.keySet()).iterator();
			
			while(subs.hasNext()){
				
				int code = subs.next();
				
				Subscription subAux = (Subscription) subsAux.get(code);
				
				if(subAux.getProducerURI().compareTo(ProducerURI)==0 &&
						subAux.getProducerId().compareTo(ProducerId)==0){
					
					subsList.put(code, subAux);
				}
			}
		}
				
		return subsList;
		
	}
	
	public ConcurrentHashMap<Integer, Subscription> getCSubscriptionByProducer(String ProducerURI, String ProducerId){
		
		ConcurrentHashMap<Integer, Subscription> subsList = new ConcurrentHashMap<Integer, Subscription>();
		
		Iterator<String> cons = (Csubscriptions.keySet()).iterator();
		
		while(cons.hasNext()){
			
			String str = cons.next();
			
			ConcurrentHashMap<Integer, Subscription> subsAux = (ConcurrentHashMap<Integer, Subscription>) Csubscriptions.get(str);
						
			Iterator<Integer> subs = (subsAux.keySet()).iterator();
			
			while(subs.hasNext()){
				
				int code = subs.next();
				
				Subscription subAux = (Subscription) subsAux.get(code);
				
				if(subAux.getProducerURI().compareTo(ProducerURI)==0 &&
						subAux.getProducerId().compareTo(ProducerId)==0){
					
					subsList.put(code, subAux);
				}
			}
		}
				
		return subsList;
		
	}
	
	public ConcurrentHashMap<Integer, Subscription> getTSubscriptionbyConsumer(String ConsumerURI){
												
		return Tsubscriptions.get(ConsumerURI);
		
	}
	
	public ConcurrentHashMap<Integer, Subscription> getCSubscriptionbyConsumer(String ConsumerURI){
		
		return Csubscriptions.get(ConsumerURI);
		
	}
	
	public ConcurrentHashMap<String , ConcurrentHashMap<Integer, Subscription>> getTSubscriptionList(){
		 return Tsubscriptions;
	}
	
	public ConcurrentHashMap<String , ConcurrentHashMap<Integer, Subscription>> getCSubscriptionList(){
		 return Csubscriptions;
	}
	
	

}
