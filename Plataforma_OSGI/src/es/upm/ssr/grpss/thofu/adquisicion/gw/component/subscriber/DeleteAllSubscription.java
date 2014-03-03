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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Subscription;

/**
 * @author Iván
 *
 */
public class DeleteAllSubscription extends ServerResource{
	
	@Get
	public String removeSubscriptions(){
		
								
		PubSubCore psc = PubSubCore.getInstance();
		
		ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Csubs = psc.getCSubscriptionList();
		ConcurrentHashMap<String, ConcurrentHashMap<Integer, Subscription>> Tsubs = psc.getTSubscriptionList();
		
		ConcurrentHashMap<Integer, Subscription> conCsubs = new ConcurrentHashMap<Integer, Subscription>();
		ConcurrentHashMap<Integer, Subscription> conTsubs = new ConcurrentHashMap<Integer, Subscription>();
			
			
		Iterator<String> cons;
		Iterator<Integer> subs;
		
		int t = 0;
		int c = 0;
		
		Subscription subscription;
		int code;
		
		if(Csubs != null){
				
			cons = (Csubs.keySet()).iterator();
			
			while(cons.hasNext()){
				
				conCsubs = Csubs.get(cons.next());
				subs = (conCsubs.keySet()).iterator();				
				
				t = t+conCsubs.size();
				
				while(subs.hasNext()){
						
					code = subs.next();
									
					subscription = (Subscription) conCsubs.get(code);
																					
					psc.unSubscribe(subscription.getConsumerURI(), "2", code);
											
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
					
					subs = (conTsubs.keySet()).iterator();
					
					System.out.println(">>> Elimina subscripcion Threshold: "+code);
											
				}				
			}				
			
		}
		
		return "Eliminadas "+conTsubs.size()+" subscripciones Threshold"+
				" y "+conCsubs.size()+" bajo contrato";
		
	}

}
