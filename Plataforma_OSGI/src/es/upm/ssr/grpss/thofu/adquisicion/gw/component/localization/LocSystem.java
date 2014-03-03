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
package es.upm.ssr.grpss.thofu.adquisicion.gw.component.localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Event;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.EventManager;

/**
 * @author Iv�n
 *
 */
public class LocSystem extends ServerResource{
	

	String ProducerURI = "http://10.1.10.212:6008/gw-d205/loc/";
	String ProducerId = "ultrasound";
	
	Map<String,String> payload = new HashMap<String,String>();
	
	
	@Post
	public String localization(Representation r){
		
		JSONObject coordinates;
		
		JSONObject temp;
		
	/*	try{
			
			temp = new JSONObject(r.getText());
			
			JSONObject temperature = new JSONObject();
			ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
			
			temperature.put("key", "temp");
			temperature.put("unit", "celsius");
			temperature.put("value", (String) temp.get("temp"));
			
			payload.add(temperature);
			
			//Creates an event and store it into the Contract Event Table 
			Event ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
			EventManager em = EventManager.getInstance();			
			em.setEvent(ev);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			coordinates = new JSONObject(r.getText());
			
			ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
			
			JSONObject coordinate = new JSONObject();
			
			coordinate.put("key", "x");
			coordinate.put("unit", "");
			coordinate.put("value", String.valueOf(coordinates.get("x")));
			
			payload.add(coordinate);
			
			coordinate = new JSONObject();
			
			coordinate.put("key", "y");
			coordinate.put("unit", "");
			coordinate.put("value", String.valueOf(coordinates.get("y")));
			
			payload.add(coordinate);
			
			coordinate = new JSONObject();
			
			coordinate.put("key", "z");
			coordinate.put("unit", "");
			coordinate.put("value", String.valueOf(coordinates.get("z")));
			
			payload.add(coordinate);

			
			System.out.println("*** Evento Loc x="+coordinates.get("x")+" y="+coordinates.get("y")+" z="+coordinates.get("z"));
			
			//Creates an event and store it into the Contract Event Table 
			Event ev = new Event(ProducerURI, ProducerId, "2", System.currentTimeMillis(), payload);
			EventManager em = EventManager.getInstance();			
			em.setEvent(ev);
			
			//System.out.println("*** Evento loc: "+ev.toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Coordinates received OK!!!";
		
	}
		

}
