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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area.AreaDistinguisher;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

/**
 * @author Iv�n
 *
 */
public class LocAreaManager extends ServerResource {
	
	@Get
	public String getAreaList(){

		JSONArray areas = Utils.getAreaList();
		
		//TODO: mejor coger de registro local.
		
		if(areas == null){
			return "Error: there is no area defined";
		}
		
		return areas.toString();
		
	}
	
	@Post
	public String setArea(Representation r){
		
		JSONObject area;
		String areaId = null;
		JSONObject response = null;
		
		try {
			area = new JSONObject(r.getText());
			response = new JSONObject();
			
			AreaDistinguisher ad = AreaDistinguisher.getInstance();
			//Define an area in AreaDistinguisher
			areaId = ad.setArea(area.hashCode(), area.getJSONArray("limits"));
			
			//Insert area information into data base
			Utils.setAreaInformation(areaId, area.getJSONArray("limits").toString(), 
					area.getJSONArray("semanticTags").toString());
			
			//Build a response
			response.put("status", "OK");
			response.put("areaId", String.valueOf(areaId));
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "Error: wrong area definition message";
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();		
	}
	
	@Put
	public String modifyArea(Representation r){
		
		JSONObject area;
		String areaId = null;
		JSONObject response = null;
		
		try {
			area = new JSONObject(r.getText());
			response = new JSONObject();			
			
			AreaDistinguisher ad = AreaDistinguisher.getInstance();
			//Define an area in AreaDistinguisher
			ad.setArea(Integer.valueOf((String) area.get("areaId")),
												area.getJSONArray("limits"));
		
			//Insert area information into data base
			Utils.setAreaInformation((String) area.get("areaId"), 
					area.getJSONArray("limits").toString(), 
					area.getJSONArray("semanticTags").toString());
			
			//Build a response
			response.put("status", "OK");			
						
		} catch (JSONException e) {
			e.printStackTrace();
			return "Error de construccion mensaje JSON de definicion de area";
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.toString();		
	}
	
	@Delete
	public String removeArea(Representation r){
		
		JSONObject area;
		String areaId = null;
		
		try {
			area = new JSONObject(r.getText());
			
			AreaDistinguisher ad = AreaDistinguisher.getInstance();
			ad.removeArea(Integer.valueOf((String) area.get("areaId")));
			
			//TODO: Delete area information into data base. If area is associated to an object...
									
		} catch (JSONException e) {
			e.printStackTrace();
			return "Error de construccion mensaje JSON de definicion de area";
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return areaId;		
	}
	
	

}
