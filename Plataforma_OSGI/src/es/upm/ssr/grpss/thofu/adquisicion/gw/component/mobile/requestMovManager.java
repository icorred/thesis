package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Response;
import org.restlet.representation.StringRepresentation;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class requestMovManager extends StringRepresentation implements Runnable
{

	static private int count;
	public requestMovManager(Response response)
	{
		
		super("movimiento Movil");
	
		count = 0;
		
		this.response = response;
	}

	public void run()
	{
				
		response.setEntity( this );
		
		
		try
		{
			
			MobileSensor.MovEvent = true;
			
			//Wait a while to leave finishing a possible previous thread
			Thread.sleep( 500 );
			
			MobileSensor.MovEvent = false;
			
			while(!MobileSensor.MovEvent){ //&& count <= 600){
						
				Thread.sleep( 500 );
				
				System.out.println( "Comprobando Sensor Movimiento");
				
				count++;
				
				
			}
			
			MobileSensor.MovEvent = false;
			
			if(count > 600){
				JSONObject resp = new JSONObject();
				try {
					resp.put("pos", "Subscripcion caducada");
					resp.put("turn", "Subscripcion caducada");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				setText("Subscripción caduco");
				response.commit();
			}
							
		}
		catch( InterruptedException x )
		{
			x.printStackTrace();
		} 		
				
		setText(MobileSensor.movCode.toString());
		response.commit();		
		
	}
		
	

	private final Response response;
}
