package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

import java.sql.SQLException;

import org.restlet.Response;
import org.restlet.representation.StringRepresentation;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class requestTagManager extends StringRepresentation implements Runnable
{

	static private int count;
	
	public requestTagManager(Response response)
	{
		
		super("alarma_2");
	
				
		this.response = response;
	}

	public void run()
	{
		
			
		response.setEntity( this );
		
		
		try
		{
			
			MobileSensor.TagEvent = true;
			
			//Wait a while to leave finishing a possible previous thread
			Thread.sleep( 500 );
			
			MobileSensor.TagEvent = false;
			
			
			while(!MobileSensor.TagEvent){
						
				Thread.sleep( 1000 );
				
				System.out.println( "Comprobando etiqueta RFID");
				
				
			}
			
			setText(MobileSensor.idCode);
			
			MobileSensor.TagEvent = false;
											
		}
		catch( InterruptedException x )
		{
			x.printStackTrace();
		} 		
				
		
		response.commit();		
		
	}
		
	

	private final Response response;
}
