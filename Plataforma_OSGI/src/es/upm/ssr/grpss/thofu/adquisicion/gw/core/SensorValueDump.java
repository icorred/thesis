package es.upm.ssr.grpss.thofu.adquisicion.gw.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class SensorValueDump extends TimerTask{
	
	@Override
	public void run() {
		
		
		ConcurrentHashMap<String, Device> devices = DeviceManager.getList();
		Iterator<String> it = devices.keySet().iterator();
		
		//Aux variables to check values in cache
		String deviceName;
		Device deviceAux;
		ConcurrentHashMap<String, Hashtable<Long, String>>  cacheValues;
		
		while(it.hasNext()){
			
			try{
				deviceName = it.next();
				
				deviceAux = (Device) devices.get(deviceName);
				
				if(deviceAux.isValueCache()){
					
					System.out.println(">>> Guardando en BD: "+deviceName);
					cacheValues = deviceAux.getValueCache();
					
					//Dump values into the DB
					Utils.setSensorsReading(cacheValues);
					
					//Remove cache
					deviceAux.cleanValueCache();
				}
				
							
			}catch(Throwable e1){
				
				FileWriter fichero = null;
		        PrintWriter pw = null;
		        
		        File file = new File ("/home/gpds/Desktop/errorSensorDumping.txt");
	        	long bytes = file.length();
		        
	        	if(bytes/1024 <= 1024){
	        		
			        try
			        {	        	
			        	fichero = new FileWriter("/home/gpds/Desktop/errorSensorDumping.txt",true);
			          	          
			            pw = new PrintWriter(fichero);
			 
			           pw.println(">>> Hora: " + System.currentTimeMillis());
			           pw.println(">>> Error: \n" + e1.toString());
			 
			        } catch (Exception e) {
			            e.printStackTrace();
			        } finally {
			           try {
			        
			           if (null != fichero)
			              fichero.close();
			           } catch (Exception e2) {
			              e2.printStackTrace();
			           }
			        }
	        	}
				
			}
			
			
		}		
		
	}	
	
}
