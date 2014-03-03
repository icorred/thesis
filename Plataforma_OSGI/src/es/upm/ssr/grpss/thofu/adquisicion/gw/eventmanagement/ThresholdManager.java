/*
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


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;


/**
 * @author Iv�n
 *
 */
public class ThresholdManager {
		
private static ThresholdManager TM;

private ArrayList<JSONObject> payload;

private static boolean started=false;

private PubSubCore psc;
private Event event;

private BlockingQueue<Client> poolConnection;
private ExecutorService poolManager;

private AtomicInteger connCount = new AtomicInteger();
	
	public ThresholdManager(){
			
		System.out.println("*** Configura Timer Threshold checker");	
		poolConnection = new ArrayBlockingQueue<Client>(Constants.MAX_THREAD_POOL);
		poolManager = Executors.newFixedThreadPool(Constants.MAX_THREAD_POOL);		
		
	}
	
	private Client getNewConnection() {
		
		Client restClient = new Client(new Context(), Protocol.HTTP);
	
		Series<Parameter> parameters = restClient.getContext().getParameters();
	
		//parameters.add("persistingConnections", "true");
    	parameters.add("socketReuseAddress", "true");
    	parameters.add("socketConnectTimeoutMs", "1000");
    	
    	try {
			restClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
           	
    	return restClient;
	}
	
	private Client getConnection() {
		
		Client client = null;
	
		client = poolConnection.peek();
					
		if(client == null){
			
			synchronized (connCount) {
				
			   if (connCount.get() < Constants.MAX_THREAD_POOL) {
				   client = getNewConnection();
			      poolConnection.offer(client);
			      connCount.incrementAndGet();
			 
			   }
			}			
					
		}else {
			try {
				
				if(!client.isStarted()){
					client.start();
				}
			} catch (Exception e) {		
				e.printStackTrace();
			}
		}
		
		return client;
	}
	
	private void removeConnection(Client restClient){
		
		synchronized (connCount) {
			
			   if (connCount.get() > 0) {
				  
			      connCount.decrementAndGet();
			      
			      try {
						restClient.stop();
					} catch (Exception e) {
												
						e.printStackTrace();
					}
			      
			      poolConnection.remove(restClient);
			    }
		}
	}

	private void releaseConnection(Client restClient){

		
		poolConnection.offer(restClient);
		
		
	}

	public static ThresholdManager getInstance(){
		
		if(TM == null){
			TM = new ThresholdManager();
		}
		
		return TM;
	}
	
	public void initThresholdTask(){
		
		if(!started){
			
			started = true;
				
			Timer t = new Timer();
			
			//Set up a new task
			ThresholdSubscriptionChecker tsc = new ThresholdSubscriptionChecker();
			 
			//Configure timer
			t.scheduleAtFixedRate(tsc, 0, Constants.TH_CHECKER);				
		}		
	}
	
	public void stopThresholTask(){
		
		if(started){
			started = false;
		}
	}
	
	public boolean isStarted(){
		
		return started;
	}
	
	public boolean ThresholdCheckerTask(Event ev){
									
		payload = ev.getPayload();
		
		event = ev;
		
		poolManager.execute( new Runnable() {
			
			public void run() {
							
				psc = PubSubCore.getInstance();
				
				ArrayList<JSONObject> payloadAux = event.getPayload();
				ConcurrentHashMap<Integer, Subscription> subsByProdAux = psc.getTSubscriptionByProducer(event.getProducerURI(), event.getProducerId());
				Iterator<Integer> codeListAux = subsByProdAux.keySet().iterator();
				
				Event evAux = event;
								
				while(codeListAux.hasNext()){
					
					int code = codeListAux.next();
					
					Subscription subAux = (Subscription) subsByProdAux.get(code);
					
					//Check every key-value pair of interest
					ArrayList<JSONObject> poi = subAux.getPayloadInterest();
					
				//	System.out.println("*** Comenzar a comprobar subscripcion con codigo: "+code);
					
				//	System.out.println("*** Tamanno subs "+poi.size()+" Tammano ev "+payloadAux.size()+". Event payload: "+payloadAux.toString());
					
					if(poi.size() == payloadAux.size()){
						
						boolean Match = true;
						int noMatches = 0;
						int i = 0;
						int j = 0;
						
					//	System.out.println("*** Se procede al Matcheo entre subscripcion y evento");
						
						while( i < poi.size() && Match){
												
							j = 0;					
							
					//		System.out.println("*** Comprobar cada uno de los pares clave valor");
							while(j < payloadAux.size() && Match){
								
								String key;
								String unit;
								String value;
								
								try {
								
									// Get triple payload from event
									key = (String) payloadAux.get(j).get("key");
									unit = (String) payloadAux.get(j).get("unit");
																				
									value = (String) payloadAux.get(j).get("value");
																							
									//Do Key and Unit match with event? 
									if( ((String) poi.get(i).get("key")).compareTo(key) == 0
											&& ((String) poi.get(i).get("unit")).compareTo(unit) == 0){
										
									//	System.out.println("*** Clave y unidad coincidieron key: "+key+" unidad:"+unit);
										Constants.operator operator = Constants.operator.valueOf((String) poi.get(i).get("operator"));
										
								//		System.out.println("*** Comparar operator: "+(String) poi.get(i).get("operator")+" value ev: "+value+" sub: "+
								//				(String) poi.get(i).get("value"));
										
										//Does the Threshold match with the event information?
										switch(operator){
										
										case MORE:									
																			
											if(Double.valueOf(value) > Double.valueOf((String)poi.get(i).get("value"))){
												System.out.println("*** Match "+key+" MORE");
												noMatches++;
											}else{
												Match = false;
											}
																				
										break;
										
										case LESS:
											
											if(Double.valueOf(value) < Double.valueOf((String)poi.get(i).get("value"))){
												System.out.println("*** Match "+key+" LESS");
												noMatches++;
											}else{
												Match = false;
											}
											
										break;
										
										case EQUAL:
																	
											if(((String) value).compareTo((String)poi.get(i).get("value")) == 0){
												
												System.out.println("*** Match "+key+" EQUAL");
												noMatches++;
												
											}else if (((String)poi.get(i).get("value")).compareTo("?") == 0) {
													
												System.out.println("*** Match "+key+" EQUAL");
												noMatches++;	
													
											}else {
												
												Match = false;	
											}		
											
											
											break;
										
									}
								  }
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								j++;							
							}
							
							//Had there been a Match?
							if(Match && noMatches == payloadAux.size()){
																	
								//Send the event to the Consumer
								System.out.println("*** Enviando evento THRESHOLD a "+
								subAux.getConsumerURI()+subAux.getConsumerId());
										
								Request request = new Request(Method.POST, subAux.getConsumerURI()+subAux.getConsumerId());
															
								try {
																				
									postThread pt = new postThread(request, new JSONObject(evAux.toString()));
									
									Thread evThread = new Thread(pt);
									
									evThread.start();
											
								} catch (Exception e) {
									//Selfunsubcription because of consumer disappearance
									System.out.println(">>> Desuscripcion por error. Codigo: "+code);
									psc.unSubscribe(subAux.getConsumerURI(), "1", code);
								}																	
							}
							
							i++;
						}						
					}				
				}				
			  } 
			});
		

						
		return true;
	}
	
	class ThresholdSubscriptionChecker extends TimerTask{

		private ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Tsubscriptions;
		
		@Override
		public void run() {
			
			System.out.println(">>> Comprobar subscripciones caducadas"); 
			
			psc = PubSubCore.getInstance();
					
			Tsubscriptions = psc.getTSubscriptionList();
				
			if(!Tsubscriptions.isEmpty()){
				
				System.out.println("*** Lista de subscripciones llena: "+Tsubscriptions.size());
				
				Hashtable<String, ArrayList<Integer>> unSubsList = new Hashtable<String, ArrayList<Integer>>();
				
				Iterator<String> cons = (Tsubscriptions.keySet()).iterator();
				
				ConcurrentHashMap<Integer, Subscription> subsAux;
				
				Iterator<Integer> subs;
				
				ArrayList<Integer> listSubsCodes = new ArrayList<Integer>();
							
				String ConsumerURI;
			
				while(cons.hasNext()){
															
					ConsumerURI = cons.next();
					
					subsAux = (ConcurrentHashMap<Integer, Subscription>) Tsubscriptions.get(ConsumerURI);
								
					subs = (subsAux.keySet()).iterator();
					
					while(subs.hasNext()){
						
						int subsCode = subs.next();
																		
						Subscription subAux = (Subscription) subsAux.get(subsCode);
						
						if((System.currentTimeMillis()-subAux.getStart()) >= subAux.getEnd()){
							
							//If threshold subscription period has been exceeded, save subs code
							//to unsubscribe then
							listSubsCodes.add(subsCode);						
							
							System.out.println("*** Desubscripcion automatica para: "+ConsumerURI);
							System.out.println("*** Duracion subscripcion: "+
							(System.currentTimeMillis()-subAux.getStart()));							
							
						}												
						
					}
					
					if(!listSubsCodes.isEmpty()){
						
						unSubsList.put(ConsumerURI, listSubsCodes);
						
						listSubsCodes = new ArrayList<Integer>();
					}
					
				}
				
				if(!unSubsList.isEmpty()){
					
					Iterator<String> consURI = (unSubsList.keySet()).iterator();
					String consURIaux;
					
					while(consURI.hasNext()){
						
						consURIaux = consURI.next();
						
						listSubsCodes = (ArrayList<Integer>) unSubsList.get(consURIaux);
						
						for(int i=0; i<listSubsCodes.size(); i++){
							psc.unSubscribe(consURIaux, "1", listSubsCodes.get(i));
						}
					}				
				}
				
			}else {
				System.out.println("*** Lista de subscripciones vacia");
				started = false;
				this.cancel();				
			}
		}		
	}
	
	 public class postThread implements Runnable{

		 Request req;
		 JSONObject message;
		
		 public postThread(Request req, JSONObject message){
			 
			 this.req = req;
			 this.message = message;
			 
		 }
		 
		@Override
		public void run() {
			 Client restClient = getConnection();
										
			if(restClient != null){	
			 
				 req.setEntity(message.toString(), MediaType.APPLICATION_JSON);
										 
				 Response resp = restClient.handle(req);
						
				 req.release();
												
				 if(resp.getStatus().isError()){
								 
					 System.out.println(">>> ERROR POST THRESHOLD");
				//  	restClient.stop();			  		
				  	//removeConnection(restClient);
										  	
				 }
				 
				 releaseConnection(restClient);
				 
				}
			}
			
		}
	 
}
