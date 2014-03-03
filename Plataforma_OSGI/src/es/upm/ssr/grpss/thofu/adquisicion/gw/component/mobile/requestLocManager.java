package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

import java.io.IOException;
import java.sql.SQLException;

import org.restlet.Response;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class requestLocManager extends StringRepresentation implements Runnable
{


	public requestLocManager(Response response)
	{
		
		super("Alarma");
	
		
		this.response = response;
	}

	public void run()
	{
		System.out.println( "Comprobando estado Localizacion \n");
		
		// Define our Restlet client resources.  
	    ClientResource mobileClient = new ClientResource(  
	    		"http://10.1.10.57:8182/GW-Mobile/NexusS/Sensor/location");
	 //   LocClient client = new LocClient();
			    	    
	    try {
			MobileSensor.get(mobileClient);
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		response.setEntity( this );
				
		try
		{
			
			while(!MobileSensor.LocEvent){
						
				Thread.sleep( 1000 );
				
				System.out.println( "Comprobando recepcion de localizacion");
			}
			
			MobileSensor.LocEvent = false;
							
		}
		catch( InterruptedException x )
		{
			x.printStackTrace();
		} 		
				
		setText(MobileSensor.coordinates);
						
		response.commit();
				
	}
			

	private final Response response;
}
