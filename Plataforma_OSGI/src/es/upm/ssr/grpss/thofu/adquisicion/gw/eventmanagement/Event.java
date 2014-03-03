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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Iv�n
 *
 */
public class Event {
	
	private String ProducerURI;
	private String ProducerId;
	
	private long timestamp;
	private String type;
	
	private ArrayList<JSONObject> payload;
	
	private JSONObject jsonEvent;
	
	public Event(String ProducerURI, String ProducerId, String type, long timestamp, 
				ArrayList<JSONObject> payload){
		
		this.ProducerURI = ProducerURI;
		this.ProducerId = ProducerId;
		
		this.timestamp = timestamp;
		this.type = type;
				
		ArrayList<JSONObject> clone = (ArrayList<JSONObject>) payload.clone();
		this.payload = clone;
	}
	
	public Event(JSONObject jsonEvent){
	
		this.jsonEvent = jsonEvent;
	}
	
	public String getProducerURI(){
		return ProducerURI;
	}
	
	public String getProducerId(){
		return ProducerId;
	}
	
	public long getTimeStamp(){
		return timestamp;
	}

	public String getType(){
		return type;
	}
	
	public ArrayList<JSONObject> getPayload(){
		return payload;
	}
	
	public String toString(){
		
		String ev = null;
		
		if(jsonEvent == null){
			
			//TO-DO: Construir Evento
			
			jsonEvent = new JSONObject();
			
			JSONObject jsonAux = new JSONObject();
			
			try {
				
				jsonAux.put("URI", ProducerURI);
				jsonAux.put("Id", ProducerId);
				
				jsonEvent.accumulate("producer", jsonAux);
				jsonEvent.put("type", type);
				
				jsonEvent.put("timestamp", String.valueOf(timestamp));
				
			
				for(int i = 0; i < payload.size(); i++){
					
					jsonAux = new JSONObject();
					
					jsonAux = payload.get(i);
									
					jsonEvent.accumulate("payload", jsonAux);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
		return jsonEvent.toString();
	}


}
