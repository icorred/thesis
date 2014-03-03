package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IAreaDistinguisher;

public class AreaDistinguisher implements IAreaDistinguisher{
	
	static Map<Integer, ArrayList<Coordinates>> areas;
	
	static AreaDistinguisher ad;
	
	private AreaDistinguisher(){
		initArea();
	}
	
	public static AreaDistinguisher getInstance(){
		
		if(ad == null){
			
			ad = new AreaDistinguisher();
		}
		
		return ad;
	}

	/**
	 * @param args
	 */
	public void initArea()
	{
		
		areas = new HashMap<Integer, ArrayList<Coordinates>>();
		
	/*	JSONArray areasAux = Utils.getAreaList();
	
		if(areasAux != null){ //There are areas stored into the Data Base
			
			JSONObject area;
			int areaId;
			JSONArray coordinates;
					
			for(int i=0; i<areasAux.length(); i++){
				
				try {
					area = areasAux.getJSONObject(i);
					areaId = area.getInt("areaId");
					coordinates = area.getJSONArray("limits");
					
					setArea(areaId, coordinates);
										
				} catch (JSONException e) {
					e.printStackTrace();
				}			
				
			}
		}*/
		
		//TODO Area definition
		
		/*Sala 1
		(4.6,4.65)
		(9.06,4.65)
		(9.06,8.80)
		(4.6,8.80)*/
		ArrayList<Coordinates> area1 = new ArrayList<Coordinates>();
		
		Coordinates v1 = new Coordinates(-100.0, 100.0, 0.0);
		Coordinates v2 = new Coordinates(-100.0, 0.0, 0.0);
		Coordinates v3 = new Coordinates(100.0, 0.0, 0.0);
		Coordinates v4 = new Coordinates(100.0, 100.0, 0.0);
		
		/*Coordinates v1 = new Coordinates(0.0, 0.0, 0.0);
		Coordinates v2 = new Coordinates(0.0, 0.0, 20.0);
		Coordinates v3 = new Coordinates(20.0, 0.0, 20.0);
		Coordinates v4 = new Coordinates(20.0, 0.0, 0.0);*/
		
		area1.add(v1);
		area1.add(v2);
		area1.add(v3);
		area1.add(v4);
		
		areas.put(1, area1);
				
	/*	Sala 2
		(4.6, 0.0, 0.0)
		(9.06,0.0,0.0)
		(9.06,0.0,4.65)
		(4.6,0.0,4.65)*/
		ArrayList<Coordinates> area2 = new ArrayList<Coordinates>();
		
		 v1 = new Coordinates(-100.0, 0.0, 0.0);
		 v2 = new Coordinates(-100.0, -100.0, 0.0);
		 v3 = new Coordinates(100.0, -100.0, 0.0);
		 v4 = new Coordinates(100.0, 0.0, 0.0);
		
		area2.add(v1);
		area2.add(v2);
		area2.add(v3);
		area2.add(v4);
		
		areas.put(2, area2);
				
	/*	Sala 3
		(0.0,0.0,4.65)
		(4.6,0.0,4.65)
		(4.6,0.0,8.80)
		(0.0,0.0,8.80)*/
//		ArrayList<Coordinates> area3 = new ArrayList<Coordinates>();
//		
//		v1 = new Coordinates(-100.0,0.0,100.0);
//		v2 = new Coordinates(0.0,0.0,100.0);
//		v3 = new Coordinates(0.0,0.0,200.0);
//		v4 = new Coordinates(-100.0,0.0,200.0);
//		
//		area3.add(v1);
//		area3.add(v2);
//		area3.add(v3);
//		area3.add(v4);
//		
//		areas.put(3, area3);
		
	/* Sala 4
		(0.0,0.0,0.0)
		(4.6,0.0,0.0)
		(4.6,0.0,4.65)
		(0.0,0.0,4.65)*/
//		ArrayList<Coordinates> area4 = new ArrayList<Coordinates>();
//		
//		v1 = new Coordinates(0.0,0.0,100.0);
//		v2 = new Coordinates(100.0,0.0,100.0);
//		v3 = new Coordinates(100.0,0.0,200.0);
//		v4 = new Coordinates(0.0,0.0,200.0);
//		
//		area4.add(v1);
//		area4.add(v2);
//		area4.add(v3);
//		area4.add(v4);
//		
//		areas.put(4, area4);
						
	}	
	
	public String setArea(int areaId, JSONArray coordinates){
		
		
		JSONObject coordinate;
		Coordinates coor;
		ArrayList<Coordinates> area = new ArrayList<Coordinates>();
		
		for(int i = 0; i < coordinates.length();i++){
			
			try {
				coordinate = coordinates.getJSONObject(i);
				
				coor = new Coordinates(Double.valueOf((String) coordinate.get("x")), 
						   Double.valueOf((String) coordinate.get("y")), 
						   Double.valueOf((String) coordinate.get("z")));
				
				area.add(coor);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		areas.put(areaId, area);
		
		return String.valueOf(area.hashCode());
		
	}
	
	public void removeArea(int areaId){
		
		areas.remove(areaId);
	}
	
	/**
	 * Calculates whether a point is inside an area (2 dimensions)
	 * @param _point reference point
	 * @return number of area identified
	 */
	public int pointIsInArea(final String _point){
		
		int area = 0;
		int areaIndex = 0;
		
		Coordinates coor = new Coordinates(0.0,0.0,0.0);
		try {
			JSONObject point = new JSONObject(_point);
			coor = new Coordinates(Double.valueOf((String) point.get("x")), Double.valueOf((String) point.get("y")), 0.0);
			
			System.out.println(point.toString());
			
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
		ArrayList<Coordinates> polygon;
		
		while(area == 0 && areaIndex < areas.size()){
									
			polygon = areas.get(areaIndex+1);
			
			if(polygonContainsPoint(polygon, coor)){
				area = areaIndex+1;
				
			}
			
			areaIndex++;
		}
		
		return area;
	}
	
	/**
	 * Calculates whether a point is inside an area (2 dimensions)
	 * @param _point reference point
	 * @return number of area identified
	 */
	public ArrayList<Integer> pointIsInArea2D(final String _point){

		ArrayList<Integer> areaIds = new ArrayList<Integer>();
		
		Coordinates coor = new Coordinates(0.0,0.0,0.0);
		try {
			JSONObject point = new JSONObject(_point);
			coor = new Coordinates(Double.valueOf((String) point.get("x")), Double.valueOf((String) point.get("y")), 0.0);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
		System.out.println(">>> Coordenada: "+coor.toString());
		
		//ArrayList<Coordinates> polygon;
		
		for(Map.Entry<Integer, ArrayList<Coordinates>> polygon : areas.entrySet()){
									
			System.out.println(">>> Calcular area "+polygon.getKey());
			
			if(polygonContainsPoint(polygon.getValue(), coor)){
								
				areaIds.add(polygon.getKey());
			}
		}
		
		return areaIds;
	}
	
	/**
	 * Calculates whether a point is inside an area (3 dimensions)
	 * @param _point reference point
	 * @return number of area identified
	 */
	public int pointIsInArea3D(final String _point){
		
		int area = 0;
		int areaIndex = 0;
		
		Coordinates coor = new Coordinates(0.0,0.0,0.0);
		
		try {
			JSONObject point = new JSONObject(_point);
			coor = new Coordinates(Double.valueOf((String) point.get("x")), 
								   Double.valueOf((String) point.get("y")), 
								   Double.valueOf((String) point.get("z")));
			
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
		ArrayList<Coordinates> polygon;
		
		while(area == 0 && areaIndex < areas.size()){
			
			polygon = areas.get(areaIndex+1);
			
			if(polygonContainsPoint(polygon, coor)){
				area = areaIndex+1;
			}
			
			areaIndex++;
		}
		
		return area;
	}
	

	/**
	 * 
	 * @param _polygon
	 * @param _point
	 * @return
	 */
	public boolean polygonContainsPoint(final ArrayList<Coordinates> _polygon, final Coordinates _point)
	{
		
		boolean contains = false;

	    if ( _polygon.size() != 0 )
	    {
		    int windingNumber = 0;
			
		    Coordinates lastPt = _polygon.get(0);
		    Coordinates lastStart = _polygon.get(0);
		    
		    for ( Coordinates coord : _polygon )
		    {
		    	windingNumber = polygonIsectLine(lastPt, coord, _point, windingNumber);
		    	lastPt = coord;		    	
		    }
		
		    // implicitly close last subpath
		    if ( lastPt != lastStart )
		    {
		    	windingNumber = polygonIsectLine(lastPt, lastStart, _point, windingNumber);
		    }
		
		    contains = ((windingNumber % 2) != 0);
	    }
	    
	    return contains;	
	}
	
	public int polygonIsectLine(final Coordinates _p1, final Coordinates _p2, final Coordinates _pos, int _winding)
	{
		int windingOut = _winding;

		double x1 = _p1.getX();
		double z1 = _p1.getY();
		double x2 = _p2.getX();
		double z2 = _p2.getY();
		double z = _pos.getY();
		
		int dir = 1;		
		
		if ( Double.compare(z1,z2) != 0 )
		{
			if ( z2 < z1 )
			{
				double xTmp = x2; x2 = x1; x1 = xTmp;
				double yTmp = z2; z2 = z1; z1 = yTmp;
				dir = -1;
			}
			
			if ( z >= z1 && z < z2 )
			{
				double x = x1 + ((x2 - x1) / (z2 - z1)) * (z - z1);
				
				// count up the winding number if we're
				if ( x <= _pos.getX() )
				{
					windingOut = windingOut + dir;
				}
			}

		}
		else
		{
			// ignore horizontal lines according to scan conversion rule
		}
		
		return windingOut;
	}
	
}
