//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import platform.SmartThing.SmartThing;
//import platform.SmartThing.SmartThingSensor;
//import platform.rest.PlatformClient;
//
//
//public class CopyOfMain {
//
//	PlatformClient client;	
//	
//	public static void main(String[] args) {
//		
//		 // Params: IP and Gw Id of the Platform Server.
//		  PlatformClient client = new PlatformClient("138.4.40.58:6008","gw-d205");
//		  		  
//		  client.start();
//		  SmartThing sm;
//		 
//		  for(int i=Integer.valueOf(args[0]); i <= Integer.valueOf(args[1]) ; i++){
//			  
//			  sm = new SmartThing();
//			  
//			  sm.setThingId("generator"); //Name of the object (usually the technological platform)
//			  sm.setDeviceType("localization"); //Role of this object within the smart space
//			  sm.setDescription("cricket node for localization"); //Brief descriptio5n of the object
//			  sm.setPysicalAddress(String.valueOf(i)); //Address assigned to this object within its own network (it could be an IP address)
//			  sm.setStatus("awake"); //Initial state of the object: awake, off
//			  sm.setLifeTime(300000); //Time until expiring the object in local list of the 
//			  						  // Platform without receiving any data from it
//			  
//			  //Add sensors of the Smart Thing (Smartphone
//			  SmartThingSensor sensor = new SmartThingSensor();
//			
//			  
//			  sensor.setKey("virtual"); //Kind of sensor
//			  sensor.setUnit("unidad"); // Which is the concept of the measure?
//			  sensor.setDataType("string"); //Data type used
//			  sensor.setDescription("ultrasound for localization");
//			  
//			  sm.addSensor(sensor); //add sensor to the object
//			  
//			//  System.out.println(sm.getSmartThing());
//					 					
//			 client.createSmartThing(sm);
//		  }
//		  
//		  
//		/*  //Create a Smart Thing: Smartphone with several sensors: RFID, movement, etc.
//		  for(int i=Integer.valueOf(args[0]); i <=  Integer.valueOf(args[1]) ; i++){
//			  
//			  SmartThing sm = new SmartThing();
//			  
//			  sm.setThingId("cricket"); //Name of the object (usually the technological platform)
//			  sm.setDeviceType("localization"); //Role of this object within the smart space
//			  sm.setDescription("cricket node for localization"); //Brief description of the object
//			  sm.setPysicalAddress(String.valueOf(i)); //Address assigned to this object within its own network (it could be an IP address)
//			  sm.setStatus("awake"); //Initial state of the object: awake, off
//			  sm.setLifeTime(30000000); //Time until expiring the object in local list of the 
//			  						  // Platform without receiving any data from it
//			  
//			  //Add sensors of the Smart Thing (Smartphone
//			  SmartThingSensor sensor = new SmartThingSensor();
//			  
//			  sensor.setKey("ultrasound"); //Kind of sensor
//			  sensor.setUnit("coordinates"); // Which is the concept of the measure?
//			  sensor.setDataType("string"); //Data type used
//			  sensor.setDescription("ultrasound for localization");
//			  
//			  sm.addSensor(sensor); //add sensor to the object
//					 					
//			 client.createSmartThing(sm);
//		  }
//		  
//		  EventGenerator eg = new EventGenerator(ConstantsTest.PRODUCER_URI, ConstantsTest.PRODUCER_ID, ConstantsTest.ITERATIONS, ConstantsTest.DURATION);*/
//		  
//		  EventGenerator eg = new EventGenerator(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]), Long.valueOf(args[3]), client, Integer.valueOf(args[4]));
//		  
//		  Timer t = new Timer();
//			
//			//Configure timer
//			t.scheduleAtFixedRate(eg, 0, 250);				 
//			
//		//	client.stop();
//		   
//		}		
//	}
//	
//	class EventGenerator extends TimerTask{
//		
//		String ProducerURI;
//		String ProducerId;
//		
//		int initId;
//		int finalId;
//		
//		int iterations;
//		long duration;
//		long initProcess=0;
//		
//		int totalEvents=0;
//		
//		int j;
//		
//		int maxPayload;
//		
//		PlatformClient clientProcess;
//		
//		public EventGenerator(int initId, int finalId, int iterations, long duration, PlatformClient client, int maxPayload){
//			
//			this.ProducerURI = ConstantsTest.PRODUCER_URI;
//			this.ProducerId = ConstantsTest.PRODUCER_ID;
//			
//			this.iterations = iterations;
//			this.duration = duration;
//			
//			this.initId = initId;
//			this.finalId = finalId;
//			
//			this.maxPayload = maxPayload;
//						
//			clientProcess = client;			
//			
//		}
//		
//		
//		@Override
//		public void run() {
//			
//				long initIt;
//				
//			/*	long nextEvent;
//				long period;*/
//				
//												
//				if(initProcess == 0){
//					initProcess = System.currentTimeMillis();
//					
//				}
//							
//				initIt = System.currentTimeMillis();
//				
//				j = initId;
//			
//			System.out.println(">>> Inicio periodo de envio");
//			
//				for(int i=1; i <= iterations; i++){
//									
//					if((System.currentTimeMillis() - initProcess) >= duration){
//						System.out.println(">>> Eventos totales generados = "+totalEvents);
//						clientProcess.stop();
//						this.cancel();												
//						break;
//					}
//										
//					try {
//																 			
//					   //Send an Event
//						JSONObject code = new JSONObject();
//						JSONArray eventPayload = new JSONArray();
//						
//						// TODO Generar eventos de n pares clave-valor
//						
//						for(int z=1; z<=maxPayload; z++){
//							//Define a triple for RFID event
//							code.put("key", "evento"+String.valueOf(z));
//							code.put("unit", "unidad");
//							code.put("value", String.valueOf(z));
//							
//							//Add triple
//							eventPayload.put(code);
//							
//							code = new JSONObject();
//							
//						}
//						
//						
//					   
//						System.out.println(">>> Evento "+totalEvents);
//						//Send the event
//						//ThingName is the mobile id (mobile/{id}). We have to add a sensor id which generates the event
//						clientProcess.sendEvent(ProducerURI+"/"+j+"/"+ProducerId, eventPayload);
//					    j++;
//					 
//					    if(j > finalId){
//					    	j=initId;
//					    }
//					    
//					    totalEvents++;
//					    
//					/*	nextEvent = (1000/iterations)*i;
//						
//						period = System.currentTimeMillis()-initIt;
//						
//						if( period < nextEvent){
//							Thread.sleep(nextEvent - period);
//						}	*/					
//					    					    					    
//					  } catch (JSONException err){// | InterruptedException err){// | InterruptedException err) {   
//						   err.printStackTrace();
//					  }
//					
//					
//					
//				}
//			
//		}
//		
//		
//	}
//	
//
