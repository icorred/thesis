import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.lucene.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import platform.SmartThing.SmartThing;
import platform.SmartThing.SmartThingSensor;
import platform.evmanagement.Subscription;
import platform.rest.PlatformClient;
import platform.rest.ResourceManager;



public class Main {

	
	//Instanciar servidor de escucha
	public static ResourceManager rm = new ResourceManager(6002);
	
	public static PlatformClient client = new PlatformClient("10.163.1.102:6008","gw-d205");
	
	 static int n;
	
	public static void main(String[] args) {
		
		
		 rm.addResource("/platform/agent/1", EventServerP.class);
		  
		 rm.start();
		 
		// System.out.println(" Create Thread " + Thread.currentThread().getName());
 		  
		  //client.start();
		  SmartThing sm;
		  SmartThingSensor sensor;
		 
		  //Creamos 8 generadores = BANs generando eventos
//		  for(int s=1; s<=8; s++){
//			  
//			  sm = new SmartThing();
//			  
//			  sm.setThingId("generator"); //Name of the object (usually the technological platform)
//			  sm.setDeviceType("simulated BAN"); //Role of this object within the smart space
//			  sm.setDescription("simulated shimmer for generating events"); //Brief descriptio5n of the object
//			  sm.setPysicalAddress(Integer.toString(s)); //Address assigned to this object within its own network (it could be an IP address)
//			  sm.setStatus("awake"); //Initial state of the object: awake, off
//			  sm.setLifeTime(300000); //Time until expiring the object in local list of the 
//			  						  // Platform without receiving any data from it
//			  
//			  //Add sensors of the device
//			  sensor = new SmartThingSensor();
//			
//			  
//			  sensor.setKey("virtual"); //Kind of sensor
//			  sensor.setUnit("unidad"); // Which is the concept of the measure?
//			  sensor.setDataType("string"); //Data type used
//			  sensor.setDescription("fall detector");
//			  
//			  sm.addSensor(sensor); //add sensor to the object
//			  
//			  System.out.println(sm.getSmartThing());
//					 					
//			 client.createSmartThing(sm);
//		  }
								 
			
			//Send an Event
				JSONObject startEvent;
				JSONObject code;
				JSONArray eventPayload;
				JSONObject triple = new JSONObject();
				ArrayList<JSONObject> subsPayload = new ArrayList<JSONObject>();
				ArrayList<JSONObject> subsPayload2 = new ArrayList<JSONObject>();
				 
				 int Nc; //Nc = Número subscripciones-consumidores. 64 a 224 en pasos de 16.
				 
				
				 				 
				 Subscription sub;
				 					 
					 for(Nc=208; Nc<=208; Nc=Nc+16){ //Cambiar NC
										
						 	ConstantsTest.Nc = Nc;
							 subsPayload = new ArrayList<JSONObject>();
							 
							//Definir n pares clave-valor para subscripcion	  
							  for(int z=1; z<=ConstantsTest.maxPayload; z++){
									
								  triple = new JSONObject();
																  
								 try {
									triple.put("key", "evento"+String.valueOf(z));
									triple.put("unit", "unidad");
									triple.put("value", "?"); //This subscription is for any value
									   
									triple.put("operator", "EQUAL");
								} catch (JSONException e) {
									e.printStackTrace();
								}						   
								  						   
								   subsPayload.add(triple); //add to the payload of interest								  		   
								   
							  }
							  					  					 
							//Hacemos las subscripciones fijas: Smart Things (5) y Medico (1)
							//Para todos los pacientes: 8
							 for(int i=1; i<=6; i++){ 
												
								 for(int ix=1; ix<=8; ix++){
									  sub = new Subscription(
									      ConstantsTest.CONSUMER_ROOT, //Root URI of the consumer 
									      "agent/1",       				   //Id of the consumer
									      ConstantsTest.PRODUCER_ROOT,  //Root URI of the producer
									      "generator/"+ix+"/sensor/virtual",        //Id de producer
									      subsPayload,        //Payload of the event
									      45*60*1000 //45 minutes		  
										);
									  
																	  
								  System.out.println(">>> Subscripcion num.: "+i+"..."+sub.getSubscription());
								  
								  // Perform a subscription and get the subscription code for 
								  // future unsubscriptions
								  Main.client.subscribe(sub);
								  
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
								  	  
							}
							 
							 
							 n = (Nc-48)/8;
							 
							 //Hacemos subscripciones a los generadores (familiares suscritos)
							 for(int iy=1; iy<=8; iy++){
								 
								 for(int iz=1; iz<=(Nc-48)/8; iz++){
									 									 									 
									 sub = new Subscription(
											 ConstantsTest.CONSUMER_ROOT, //Root URI of the consumer 
										      "agent/1",       				   //Id of the consumer
										      ConstantsTest.PRODUCER_ROOT,  //Root URI of the producer
										      "generator/"+iy+"/sensor/virtual",        //Id de producer
										      subsPayload,        //Payload of the event
										      45*60*1000 //45 minutes		  
											);
										  
																		  
									  System.out.println(">>> Subscripcion num.:"+iy+"-"+iz+"..."+sub.getSubscription());
									  
									  // Perform a subscription and get the subscription code for 
									  // future unsubscriptions
									  Main.client.subscribe(sub);
									  
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									}
									 
									 
								 }
							 
							 
							 
							//Send an Event
							 	startEvent = new JSONObject();
								code = new JSONObject();
																								
								eventPayload = new JSONArray();
								
								try {
									code.put("Nc", String.valueOf(Nc));
									
									code.put("file", Integer.toString(ConstantsTest.round));
									
									//Define a triple for RFID event
									startEvent.put("key", "start");
									startEvent.put("unit", "unidad");
									startEvent.put("value", code.toString()); //Id de prueba Nc+Nc
									
									eventPayload.put(startEvent);
									
								} catch (JSONException e) {
									e.printStackTrace();
								}					
							 
								//Preparamos al consumidor para esperar Ns eventos
								ConstantsTest.Nc = Nc;
															
							 //Enviar evento inicio 
							 client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);
														 
							 eventPayload = new JSONArray();
							 							 
							 
							//Generar eventos de n pares clave-valor						
							for(int z = 1; z <= ConstantsTest.maxPayload; z++){
								code = new JSONObject();
								try {
									code.put("key", "evento"+String.valueOf(z));
									code.put("unit", "unidad");
									code.put("value", String.valueOf(z));
										
									//Add triple
									eventPayload.put(code);
								} catch (JSONException e) {

									e.printStackTrace();
								}
								
							}
							
							//Estable payload en consumer
							EventServerP.eventPayload = eventPayload;
							
							//Desbloquea servidor
							ConstantsTest.newTest = false;
							
							ConstantsTest.producerId=1;
							
							//Enviar primer evento para calculo de T servicio
							client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);
							
							if(EventServerP.t == null){
								System.out.println(">>> Se instancia un timer");
								EventServerP.t = new Timer();						
								EventServerP.t.schedule(new WatchDog(), 250, 250);
							}
							
							do{
						
								if(ConstantsTest.newTest){
									System.out.println(">>> Continua con desuscripciones");
								}
								
								
							}while(!ConstantsTest.newTest);
							
														
							System.out.println(">>> Desuscripciones");
							//Borrar todas las subscripciones
							client.unsubscribeAll();
					
							System.out.println(">>> Termino Ronda, envia mensaje STOP");
							//Enviar evento stop
							startEvent = new JSONObject();
							eventPayload = new JSONArray();
							
							try {
														
								//Define a triple for RFID event
								startEvent.put("key", "stop");
								startEvent.put("unit", "unidad");
								startEvent.put("value", ""); //Id de prueba Nc+Nc
								
								eventPayload.put(startEvent);
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
							ConstantsTest.envio = System.nanoTime();
							client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);
														
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							ConstantsTest.round++;
						 
					 }
					 
					 Iterator<String> iter = EventServerP.eventResult.iterator();
					 
					 while(iter.hasNext()){
						 System.out.println(iter.next());
					 }
					 
					 
	}
					 
					 //Pruebas de 25 a 100 para payload 1
				//	 fase1();
					 
					 //Pruebas de 25 a 500 para payload 4,7,10
				//	 fase2();

					 
		
	//	 System.out.println(">>> Se termina hilo eventos");
		
	//	rm.stop();
		   
	
	
	//Pruebas de Ns=25..100 para payload 1 y Nc > 10
	public static void fase1(){
		
		//Send an Event
		JSONObject startEvent;
		JSONObject code;
		JSONArray eventPayload;
		JSONObject triple = new JSONObject();
		ArrayList<JSONObject> subsPayload = new ArrayList<JSONObject>();
		 
		 int Nc; //Nc = Número subscripciones-consumidores. 10 a 100 en pasos de 10.
		 int Ns; //Ns = Número subscripciones-sin consumidor. 25 a 500 en pasos de 25.
	
		 
		 Subscription sub;
		 
			 
			 for(Nc=30; Nc<=100;Nc=Nc+10){ //Cambiar NC
				 				 
				 for(Ns=25; Ns<=100; Ns=Ns+25){
					 
					 subsPayload = new ArrayList<JSONObject>();
					 
					//Definir n pares clave-valor para subscripcion	  
					  for(int z=1; z<=1; z++){
							
						 try {
							triple.put("key", "evento"+String.valueOf(z));
							triple.put("unit", "unidad");
							triple.put("value", "?"); //This subscription is for any value
							   
							triple.put("operator", "EQUAL");
						} catch (JSONException e) {
							e.printStackTrace();
						}						   
						  						   
						   subsPayload.add(triple); //add to the payload of interest
						   
						   triple = new JSONObject();
					  }

					 //Hacemos las subscripciones
					 for(int i=1; i<=Ns+Nc; i++){ 
				  		
						 if(i <= Nc ){
							 							 							
							  sub = new Subscription(
							      "http://138.4.40.34:6002/platform/", //Root URI of the consumer 
							      "agent/1",       				   //Id of the consumer
							      "http://138.4.40.58:6008/gw-d205/",  //Root URI of the producer
							      "generator/1/sensor/virtual",        //Id de producer
							      subsPayload,        //Payload of the event
							      45*60*1000 //45 minutes		  
								);
							  
						 }else{
							 
							 sub = new Subscription(
								      "http://138.4.40.34:6002/platform/", //Root URI of the consumer 
								      "consumer/"+i,        				   //Id of the consumer
								      "http://138.4.40.58:6008/gw-d205/",  //Root URI of the producer
								      "generator/"+i+"/sensor/virtual",    //Id de producer
								      subsPayload,        //Payload of the event
								      45*60*1000 //45 minutes		  
									);					 
							 
						 }
							  
						  System.out.println(sub.getSubscription());
						  
						  // Perform a subscription and get the subscription code for 
						  // future unsubscriptions
						  Main.client.subscribe(sub);
						  
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						  	  
						}
					 
					 
					//Send an Event
					 	startEvent = new JSONObject();
						code = new JSONObject();
						
														
						eventPayload = new JSONArray();
						
						try {
							code.put("Nc", String.valueOf(Nc));
							
							code.put("Ns", String.valueOf(Ns));
							
							code.put("file", "1");
							
							//Define a triple for RFID event
							startEvent.put("key", "start");
							startEvent.put("unit", "unidad");
							startEvent.put("value", code.toString()); //Id de prueba Nc+Nc
							
							eventPayload.put(startEvent);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}					
					 
						//Preparamos al consumidor para esperar Ns eventos
						ConstantsTest.Nc = Nc;
						ConstantsTest.Ns = Ns;
						
					 //Enviar evento inicio 
					 client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);
					 
					 eventPayload = new JSONArray();
					//Generar eventos de n pares clave-valor						
					for(int z = 1; z <= 1; z++){
						code = new JSONObject();
						try {
							code.put("key", "evento"+String.valueOf(z));
							code.put("unit", "unidad");
							code.put("value", String.valueOf(z));
								
							//Add triple
							eventPayload.put(code);
						} catch (JSONException e) {

							e.printStackTrace();
						}
						
					}
					
					//Estable payload en consumer
					EventServerP.eventPayload = eventPayload;
					
					//Desbloquea servidor
					ConstantsTest.newTest = false;
					
					//Enviar primer evento para calculo de T servicio
					client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);

					
					do{
				
						if(ConstantsTest.newTest){
							System.out.println(">>> Continua con desuscripciones");
						}
						
						
					}while(!ConstantsTest.newTest);
					
												
					System.out.println(">>> Desuscripciones");
					//Borrar todas las subscripciones
					client.unsubscribeAll();
			
					System.out.println(">>> Termino Ronda, envia mensaje STOP");
					//Enviar evento stop
					startEvent = new JSONObject();
					eventPayload = new JSONArray();
					try {
												
						//Define a triple for RFID event
						startEvent.put("key", "stop");
						startEvent.put("unit", "unidad");
						startEvent.put("value", ""); //Id de prueba Nc+Nc
						
						eventPayload.put(startEvent);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);					
					
				 }				 
				 
			 }
		
	}
	
	
	//Nc=10..100 y Ns=25..100, con payload 4,7 y 10
	public static void fase2(){
		
		//Send an Event
				JSONObject startEvent;
				JSONObject code;
				JSONArray eventPayload;
				JSONObject triple = new JSONObject();
				ArrayList<JSONObject> subsPayload = new ArrayList<JSONObject>();
				 
				 int Nc; //Nc = Número subscripciones-consumidores. 10 a 100 en pasos de 10.
				 int Ns; //Ns = Número subscripciones-sin consumidor. 25 a 500 en pasos de 25.
				 
				 int payload; //Valor de longitud inicial 4.
				 
				 Subscription sub;
				 
				
				 for(payload=4; payload<=10; payload=payload+3){
				 
					 for(Nc=10; Nc<=100;Nc=Nc+10){ //Cambiar NC
						 				 
						 for(Ns=25; Ns<=100; Ns=Ns+25){
							 
							 subsPayload = new ArrayList<JSONObject>();
							 
							//Definir n pares clave-valor para subscripcion	  
							  for(int z=1; z<=payload; z++){
									
								 try {
									triple.put("key", "evento"+String.valueOf(z));
									triple.put("unit", "unidad");
									triple.put("value", "?"); //This subscription is for any value
									   
									triple.put("operator", "EQUAL");
								} catch (JSONException e) {
									e.printStackTrace();
								}						   
								  						   
								   subsPayload.add(triple); //add to the payload of interest
								   
								   triple = new JSONObject();
							  }
				
							 //Hacemos las subscripciones
							 for(int i=1; i<=Ns+Nc; i++){ 
						  		
								 if(i <= Nc ){
									 							 							
									  sub = new Subscription(
									      "http://138.4.40.34:6002/platform/", //Root URI of the consumer 
									      "consumer/1",       				   //Id of the consumer
									      "http://138.4.40.58:6008/gw-d205/",  //Root URI of the producer
									      "generator/1/sensor/virtual",        //Id de producer
									      subsPayload,        //Payload of the event
									      45*60*1000 //45 minutes		  
										);
									  
								 }else{
									 
									 sub = new Subscription(
										      "http://138.4.40.34:6002/platform/", //Root URI of the consumer 
										      "consumer/"+i,        				   //Id of the consumer
										      "http://138.4.40.58:6008/gw-d205/",  //Root URI of the producer
										      "generator/"+i+"/sensor/virtual",    //Id de producer
										      subsPayload,        //Payload of the event
										      45*60*1000 //45 minutes		  
											);					 
									 
								 }
									  
								  System.out.println(sub.getSubscription());
								  
								  // Perform a subscription and get the subscription code for 
								  // future unsubscriptions
								  Main.client.subscribe(sub);
								  
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								  	  
								}
							 
							 
							//Send an Event
							 	startEvent = new JSONObject();
								code = new JSONObject();
								
																
								eventPayload = new JSONArray();
								
								try {
									code.put("Nc", String.valueOf(Nc));
									
									code.put("Ns", String.valueOf(Ns));
									
									code.put("file", String.valueOf(payload));
									
									//Define a triple for RFID event
									startEvent.put("key", "start");
									startEvent.put("unit", "unidad");
									startEvent.put("value", code.toString()); //Id de prueba Nc+Nc
									
									eventPayload.put(startEvent);
									
								} catch (JSONException e) {
									e.printStackTrace();
								}					
							 
								//Preparamos al consumidor para esperar Ns eventos
								ConstantsTest.Nc = Nc;
								ConstantsTest.Ns = Ns;
								
							 //Enviar evento inicio 
							 client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);
							 
							 eventPayload = new JSONArray();
							//Generar eventos de n pares clave-valor						
							for(int z = 1; z <= payload; z++){
								code = new JSONObject();
								try {
									code.put("key", "evento"+String.valueOf(z));
									code.put("unit", "unidad");
									code.put("value", String.valueOf(z));
										
									//Add triple
									eventPayload.put(code);
								} catch (JSONException e) {

									e.printStackTrace();
								}
								
							}
							
							//Estable payload en consumer
							EventServerP.eventPayload = eventPayload;
							
							//Desbloquea servidor
							ConstantsTest.newTest = false;
							
							//Enviar primer evento para calculo de T servicio
							client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);

							
							do{
						
								if(ConstantsTest.newTest){
									System.out.println(">>> Continua con desuscripciones");
								}
								
								
							}while(!ConstantsTest.newTest);
																					
							System.out.println(">>> Desuscripciones");
							//Borrar todas las subscripciones
							client.unsubscribeAll();
					
							System.out.println(">>> Termino Ronda, envia mensaje STOP");
							//Enviar evento stop
							startEvent = new JSONObject();
							eventPayload = new JSONArray();
							try {
														
								//Define a triple for RFID event
								startEvent.put("key", "stop");
								startEvent.put("unit", "unidad");
								startEvent.put("value", ""); //Id de prueba Nc+Nc
								
								eventPayload.put(startEvent);
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
							client.sendEvent(ConstantsTest.PRODUCER_URI+"/1/"+ConstantsTest.PRODUCER_ID, eventPayload);					
							
						 }				 
						 
					 }
				 }
		
	}

}


	
	
	
		
		

	

