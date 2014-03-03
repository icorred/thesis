
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.representation.Representation;

import platform.rest.PlatformClient;
import platform.rest.RestResource;

/**
 * 
 * This class implements business logic to handle events to which the client
 * has been subscribed. Each class method manage a HTTP method. Events
 * are transmitted to the platform client using POST method.
 * 
 * @author Iv�n
 *
 */
public class EventServerP extends RestResource{
	
	public static AtomicInteger totalEvents = new AtomicInteger(1);
	public static AtomicInteger roundEvents = new AtomicInteger(0);
	public static AtomicInteger testRound = new AtomicInteger(10);
	public static AtomicInteger eventSent = new AtomicInteger(0);
	
	

	public static ArrayList<String> eventResult = new ArrayList<String>();
	
	public static JSONArray eventPayload;
		
	public static ArrayList<Long> retardos = new ArrayList<Long>();
	
	static boolean transition;
	
	static Timer t=null;

	
	@Override
	public String createState(Representation arg0) {
		
			synchronized(totalEvents){
				
				if(!ConstantsTest.newTest){ //Si no se esta iniciando una nueva tanda de prueba
								
				
					 roundEvents.incrementAndGet();
				 
					 totalEvents.incrementAndGet();
				 				 
				// System.out.println(">>> Eventos totales ronda: "+roundEvents.get());
				 
//					}else if(totalEvents.get() > 6000){
//								
//								ConstantsTest.newTest = true;
//								
//								System.out.println(">>> Se termino la ronda. Nc: "+ConstantsTest.Nc+
//										" Ns: "+ConstantsTest.Ns);
//																									
//								totalEvents.set(1);
//								eventRound.set(1);
//											
//					}
			}
						
				
		}
			
		arg0.release();
			
		return (null);
	}

	@Override
	public String getStatus(Representation arg0) {
	
		// Business logic for GET method
		
		return ("respuesta GET");
	}

	@Override
	public String updateResource(Representation arg0) {
				
		// Business logic for PUT method
		
		return ("respuesta PUT");
	}	
	
	static public void sendEvent(){
				
		ConstantsTest.producerId++;
		
		if(ConstantsTest.producerId > 8){
			ConstantsTest.producerId = 1;
		}
		
		//Enviar evento
		System.out.println(">>> Envio nuevo evento: "+ConstantsTest.producerId );
		
		
		Main.client.sendEvent(ConstantsTest.PRODUCER_URI+"/"+ConstantsTest.producerId+"/"+ConstantsTest.PRODUCER_ID, eventPayload);
		
	}
	
}


class WatchDog extends TimerTask{
	
	@Override
	public void run() {
		
		//Reinicia generación de eventos
	//	EventServerP.roundEvents.set(0);
		
		EventServerP.eventSent.incrementAndGet();
		
		System.out.println(">>> Ronda: "+ EventServerP.eventSent.get()+". Eventos: "+ EventServerP.roundEvents.get());
		
		//Si pasaron 3 iteraciones completas (24 eventos enviados)
		 if(EventServerP.roundEvents.get() > 6000){
						 
			 EventServerP.t.cancel();
			 EventServerP.t.purge();
			 EventServerP.t = null;
			 					 
			 System.out.println(">>> Se termino la ronda. Eventos recibidos: "+EventServerP.roundEvents.get());
			 
			 //Eventos recibidos / eventos teoricamente enviados
			 
			 float lostEvents = (float) EventServerP.roundEvents.get() / (((EventServerP.testRound.get()*2)+6)*EventServerP.eventSent.get());
			 
			 System.out.println(">>> Tasa de eventos recibidos: "+lostEvents);
			 
			 EventServerP.eventResult.add(">>> Ronda "+EventServerP.testRound.get()+". Tasa de eventos recibidos: "+lostEvents);
			 
			 EventServerP.testRound.incrementAndGet();
			 
			 ConstantsTest.newTest = true;
			 EventServerP.roundEvents.set(0);
			 EventServerP.eventSent.set(0);
			 
		 }else{
			EventServerP.sendEvent();
		 }
				
	}
		
}

