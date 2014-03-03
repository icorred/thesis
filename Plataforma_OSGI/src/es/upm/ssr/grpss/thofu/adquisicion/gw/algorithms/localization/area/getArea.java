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
package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area;

import java.io.IOException;
import java.util.ArrayList;


import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;

import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

/**
 * @author Iv�n
 *
 */
public class getArea extends ServerResource{
	
	@Put	
	public String getLocbyArea(Representation r){

		JSONObject coordinates;
		ArrayList<Integer> areaIds = null;
		JSONObject response = null;
		JSONObject aux;
		
		try {
			
			//System.out.println("Coordenadas para calcular area: "+r.getText());
			
			coordinates = new JSONObject(r.getText());
			
			AreaDistinguisher ad = AreaDistinguisher.getInstance();
			
			areaIds = ad.pointIsInArea2D(coordinates.getJSONObject("coordinates").toString());
			
			response = new JSONObject();
			
			if(areaIds.size() > 0){
				
				response.put("responseCode","OK");
				int i = 0;
				for(int areaId : areaIds){
					
					aux = new JSONObject();
					aux.put("areaId"+i, areaId);
					
					response.accumulate("areaIds", aux);
					
					i++;
				}
				
			}else{
				response.put("responseCode","No matches found for coodinate "+coordinates.getJSONObject("coordinates").toString());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return response.toString();
		
	}
	

}
