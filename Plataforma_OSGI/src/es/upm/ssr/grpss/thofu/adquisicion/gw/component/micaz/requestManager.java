package es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz;


import org.restlet.Response;
import org.restlet.representation.StringRepresentation;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

public class requestManager extends StringRepresentation implements Runnable
{
	private String sensor;
	private String operador;
	private String threshold;
	private String uri;
	
	public requestManager( String Sensor_URI, String sensor, String  operador, String threshold, Response response)
	{
		
		super("<button><br><br>"+
				"esto es una <b>Alarma</b> <br> <br>"+
				"</button>");
		
		this.sensor = sensor;
		this.operador = operador;
		this.threshold = threshold;
		uri = Sensor_URI;
				
		this.response = response;
	}

	public void run()
	{
		try {
			System.out.println( "Analizando valores Micaz \n"+
			"Ultima medida: "+Double.valueOf(Utils.getSensorReading(uri, 0))+
			"\n operador: "+ operador+
			"\n threshold: "+ threshold);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		response.setEntity( this );
		
		String result;
		boolean event = false;
		
		try
		{
			
			while(!event){
				
				// Get the most current measure of a sensor reading
				result = Utils.getSensorReading(uri, 0);
				
				if(operador.equalsIgnoreCase("equal")){
					System.out.println( "Operador "+operador);
					if( Double.valueOf(result) == Double.valueOf(threshold)){ 
						event = true;
						System.out.println( "Evento igual");
					}
									
				} else if (operador.equalsIgnoreCase("more")){
					System.out.println( "Operador "+operador);
					if( Double.valueOf(result) > Double.valueOf(threshold)){ 
						event = true;
						System.out.println( "Evento mayor");
						
					}
					
				} else if (operador.equalsIgnoreCase("less")){
					System.out.println( "Operador "+operador);
					if( Double.valueOf(result) < Double.valueOf(threshold)) 
						event = true;
					System.out.println( "Evento menor");
					
				}
				
				Thread.sleep( 2000 );
				
				System.out.println( "Analizando valores Micaz \n"+
						"Ultima medida: "+Double.valueOf(Utils.getSensorReading(uri, 0))+
						"\n operador: "+ operador+
						"\n threshold: "+ Double.valueOf(threshold));
			}
			
									
			
		}
		catch( InterruptedException x )
		{
			x.printStackTrace();
		}		
				
		response.commit();

	}

	private final Response response;
}
