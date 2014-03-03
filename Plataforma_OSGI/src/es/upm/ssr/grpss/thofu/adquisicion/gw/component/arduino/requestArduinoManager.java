package es.upm.ssr.grpss.thofu.adquisicion.gw.component.arduino;

import java.sql.SQLException;

import org.restlet.Response;
import org.restlet.representation.StringRepresentation;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class requestArduinoManager extends StringRepresentation implements Runnable {
	
	private String sensor;
	private String actuator_URI;
	
	public requestArduinoManager( Response response)
	{
		
		super("sensor arduino");
				
		this.response = response;
	}

	public void run()
	{
				
		response.setEntity( this );
				
		ArduinoSensor.sensorEvent = true;
		
		//Wait a while to leave finishing a possible previous thread
		try {
			Thread.sleep( 1500 );
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArduinoSensor.sensorEvent = false;
				
		while(!ArduinoSensor.sensorEvent){
				
				try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println( "Chequeando estado sensores interactivos ");
								
				if(!ArduinoSensor.joyReq){
								
					ArduinoSensor.sensorEvent = true;
		
					System.out.println( ArduinoSensor.actCode.toString());
					setText(ArduinoSensor.actCode.toString());
									
				} else if(!ArduinoSensor.touchReq){
					
					ArduinoSensor.sensorEvent = true;
					
					setText(ArduinoSensor.actCode.toString());
									
				} else if(!ArduinoSensor.buttonReq){
					
					ArduinoSensor.sensorEvent = true;
					
					setText(ArduinoSensor.actCode.toString());
									
				}
				
			}
			
			
		response.commit();

	}

	private final Response response;

}
