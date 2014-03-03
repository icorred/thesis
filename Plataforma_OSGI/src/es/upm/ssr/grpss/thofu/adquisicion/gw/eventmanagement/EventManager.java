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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IEventManager;

/**
 * @author Ivan
 *
 */
public class EventManager implements IEventManager{
		
	//Event Table; Threshold
		private static ConcurrentHashMap<String,Map<String,Event>> Tevents;
		//Event Table; contract
		private static ConcurrentHashMap<String,Map<String,Event>> Cevents;
		
		private static EventManager EM;
		private static ThresholdManager tm; 
		
		public EventManager(){
			Tevents = new ConcurrentHashMap<String, Map<String,Event>>();
			Cevents = new ConcurrentHashMap<String, Map<String,Event>>();
		}
		
		public static EventManager getInstance(){
			
			if(EM == null){
				EM = new EventManager();
				tm = ThresholdManager.getInstance();
			}
			
			return EM;
		}
		
		public synchronized boolean setEvent(Event event){
			
			if(event.getType().compareTo(Constants.THRESHOLD) == 0){
												
				if(!Tevents.containsKey(event.getProducerURI())){
								
					Tevents.put(event.getProducerURI(), new HashMap<String,Event>());					
				}
					
				Tevents.get(event.getProducerURI()).put(event.getProducerId(), event);
												
				//Call ThreshdoldManager in order to dispatch the event to the consumer
				tm.ThresholdCheckerTask(event);
				
				return true;				
				
			}else if(event.getType().compareTo(Constants.CONTRACT) == 0){
						
				if(!Cevents.containsKey(event.getProducerURI())){
					
					System.out.println("*** Se registro evento bajo CONTRATO ***");
								
					Cevents.put(event.getProducerURI(), new HashMap<String,Event>());
				}
				
				System.out.println("*** Se registro evento bajo CONTRATO ***");
				Cevents.get(event.getProducerURI()).put(event.getProducerId(), event);
				
				return true;
			}
		
			return false;			
		}
		
		public Event getEvent(String ProducerURI, String ProducerId, String type){
									
			if(type.compareTo(Constants.THRESHOLD)==0){
				
				if(Tevents.containsKey(ProducerURI)){
					return Tevents.get(ProducerURI).get(ProducerId);
				}
				
			}else if(type.compareTo(Constants.CONTRACT)==0){
				
				System.out.println("*** Obteniendo evento bajo contrato ***"); 
				System.out.println("*** ProducerURI "+ProducerURI+" Size Tabla eventos "+Cevents.size());
				
				if(Cevents.containsKey(ProducerURI)){
					System.out.println("*** Evento encontrado ***");
					return Cevents.get(ProducerURI).get(ProducerId);
				}
				
			}
			
			return null;
		}
		
	
		public boolean removeEvent(String ProducerURI, String ProducerId, String type){
			
			if(type.compareTo("threshold") == 0){
				
				if(Tevents.containsKey(ProducerURI)){
					Tevents.get(ProducerURI).remove(ProducerId);
					return true;
				}
				
			}else if(type.compareTo("contract") == 0){
				
				if(Cevents.containsKey(ProducerURI)){
					Cevents.get(ProducerURI).remove(ProducerId);
					return true;
				}
				
			}
			
			return false;			
				
		}
		
		
}
