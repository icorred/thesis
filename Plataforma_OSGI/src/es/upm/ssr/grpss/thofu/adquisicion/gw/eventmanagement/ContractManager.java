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

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
public class ContractManager {
	
	//List of Task for getting and return events under contract (if there are)
	private Hashtable<String, Hashtable<Integer,ContractEventsChecker>> contractTask;
	
	private BlockingQueue<Client> pool = new ArrayBlockingQueue<Client>(20);
	private AtomicInteger connCount = new AtomicInteger();
	
	private static ContractManager CM;
	
	public ContractManager(){
		
		contractTask = new Hashtable<String, Hashtable<Integer, ContractEventsChecker>>(Constants.MAX_CONSUMERS);
	}
	
	public static ContractManager getInstance(){
		
		if(CM == null){
			CM = new ContractManager();
		}
		
		return CM;
	}
	
private Client getNewConnection() {
		
		Client restClient = new Client(new Context(), Protocol.HTTP);
	
		Series<Parameter> parameters = restClient.getContext().getParameters();
	
		parameters.add("persistingConnections", "false");
    	parameters.add("socketReuseAddress", "true");
    	//parameters.add("socketConnectTimeoutMs", "800");
    	
    	try {
    		System.out.println("*** NUEVA CONEXION CONTRACT ***");
			restClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
           	
    	return restClient;
	}
	
	private Client getConnection() {
		
		Client client = null;
	
		client = pool.poll();//50, TimeUnit.MILLISECONDS);
				
		if(client == null){
			
			synchronized (connCount) {
				
			   if (connCount.get() < 20) {
				   client = getNewConnection();
				   
			      pool.offer(client);
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
			      pool.remove(restClient);
			    }
		}
	}

	private void releaseConnection(Client restClient){
		
		try {
			restClient.stop();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		pool.offer(restClient);
		
	}
	
	public boolean initContractTask(Subscription subs, int subsCode){
		
		if(!contractTask.containsKey(subs.getConsumerURI())){
			
			contractTask.put(subs.getConsumerURI(), new Hashtable<Integer, ContractEventsChecker>(Constants.MAX_SUBS_PER_CONS));
		}
		
		//Set up a new task
		ContractEventsChecker cec = new ContractEventsChecker(subs, subsCode);
		
		Timer t = new Timer();
		
		
		//Configure timer
		t.scheduleAtFixedRate(cec, subs.getStart(), subs.getPeriod());
		
		
		//Add timer to the timer list
		contractTask.get(subs.getConsumerURI()).put(subs.hashCode(), cec);	
		
		System.out.println("*** Timer agregado: Periodo = "+ subs.getPeriod()+ 
		" y saliendo. Hash Code: "+subsCode);
		
		return true;
		
	}
	
	public boolean stopContract(int hashCode, String ConsumerURI){
		
		if(contractTask.containsKey(ConsumerURI)){
			
			System.out.println("*** Stop Contract");
			
			Hashtable<Integer, ContractEventsChecker> timers = contractTask.get(ConsumerURI);
			 
			 if(timers.containsKey(hashCode)){
				 
				 System.out.println("*** Timer Contrato asociado a Subscripcion con HashCode "+hashCode);
				 
				 ContractEventsChecker cec = timers.get(hashCode);
				
				 //This cancel will not have effect if it has been invoked before
				 cec.CancelContract();			
				 
				 timers.remove(hashCode);				 
				 
				 return true;
			 }
			 
			 return false;
			
		}		
		
		return false;
		
	}
	
	class ContractEventsChecker extends TimerTask{

		private String ConsumerURI;
		private String ConsumerId;
		private String ProducerURI;
		private String ProducerId;
		
		private long end;
		private long StartTime;
		
		private int subsCode;
		
		private Event ev;
		private long lastTimeStamp;
		
		private JSONObject message;
		
		private EventManager Em = EventManager.getInstance();
				
		public ContractEventsChecker(Subscription subscription, int hashCode){
			
			this.ConsumerURI = subscription.getConsumerURI();
			this.ConsumerId = subscription.getConsumerId();
			
			this.ProducerURI = subscription.getProducerURI();
			this.ProducerId = subscription.getProducerId();
			this.end = subscription.getEnd();
						
			this.StartTime = System.currentTimeMillis();
			
			this.subsCode = hashCode;
			
			this.lastTimeStamp = 0;
		}
		
		public void CancelContract(){
			
			this.cancel();
					
		}
		
		public String getConsumerURI(){
			return ConsumerURI;
		}
		
		public String getConsumerId(){
			return ConsumerId;
		}
		
		public String getProducerURI(){
			return ProducerURI;
		}

		public String getProducerId(){
			return ProducerId;
		}	
		
		public void stopTask(){
			
			//If something went wrong, it is better to cancel this task and remove associated subscription
			this.cancel();					
			PubSubCore psc = PubSubCore.getInstance();						
			psc.unSubscribe(ConsumerURI, "2", subsCode);
		}
				
		@Override
		public void run() {
			
			System.out.println("*** Ejecutando Tarea obtencion eventos ***");
			
			if((StartTime + end) - System.currentTimeMillis() < 0){
				System.out.println("*** Cancela Tarea");
				this.stopTask();
				
			}else{
			
				//Retrieve event
				ev = Em.getEvent(ProducerURI, ProducerId, Constants.CONTRACT);	
							
				if((ev != null) && (ev.getTimeStamp() > lastTimeStamp)){
					
					lastTimeStamp = ev.getTimeStamp();
					
					//If an event has been found, then it is sent to the consumer
					System.out.println("*** Se encontro evento asociado a contrato: "+ev.toString());
					
					try {
						message = new JSONObject(ev.toString());
					} catch (JSONException e) {						
						e.printStackTrace();
					}
					
				} else {
					
					System.out.println("*** No se encontro evento valido para el contrato ***");
					
					message = new  JSONObject();
					
					try {
						
						//Si evento no refrescado
						if(ev == null){
							
							message.put("messageError","1"); // No event in cache
							message.put("ProducerURI", ProducerURI);
							message.put("ProducerId", ProducerId);
							message.put("lastTimeStamp", String.valueOf(lastTimeStamp));
							message.put("subsCode",subsCode);							
							
						}else if(!(ev.getTimeStamp() > lastTimeStamp)){
							
							message.put("messageError", "2"); //Event out-of-date
							message.put("ProducerURI", ProducerURI);
							message.put("ProducerId", ProducerId);
							message.put("lastTimeStamp", String.valueOf(lastTimeStamp));
							message.put("subsCode",subsCode);
							
						}
						
					} catch (JSONException e) {						
						e.printStackTrace();
					}
				}
				
				Request request = new Request(Method.POST, ConsumerURI+ConsumerId);			
				try{
					post(request, message);
				} catch (Exception e) {
				
					e.printStackTrace();				    	
					this.stopTask();
				} 
			}	
		}
		
		 private void post(Request req, JSONObject message) throws Exception{  
			 
			 final Request req2 = req;
			 final JSONObject message2 = message;
					 
			 Thread postThread = new Thread( new Runnable(){
					 
				public void run(){
						 Client restClient = getConnection();
						 
						 if(!restClient.isStarted()){
							 try {
								restClient.start();
							} catch (Exception e) {
								
								e.printStackTrace();									  		
							  	removeConnection(restClient);
							}
						 }
							
						if(restClient != null){	
						 
							 req2.setEntity(message2.toString(), MediaType.APPLICATION_JSON);
									    
							 Response resp = restClient.handle(req2);
									
							 req2.release();
															
							 if(resp.getStatus().isError()){
							
							System.out.println(">>> ERROR POST CONTRACT");
							/*  	restClient.stop();			  		
							  	removeConnection(restClient);*/
							  	
							 releaseConnection(restClient);
							  	
							 }else{
								 
								 releaseConnection(restClient);
								 
							 }
							}
						}
				}, "postThreshold-Thread");	
			 
			 postThread.start();
		}
		
		
		
	}

}
