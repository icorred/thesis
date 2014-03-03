package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

import java.io.IOException;
import java.sql.SQLException;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;


public class LocSensor extends ServerResource{
	
		
	 @Get
		public Representation represent() throws SQLException, IOException  {
	    	
	    	System.out.println("*** GET Loc Sensor***");
	    	
	    	String Device_URI = Constants.GW_ROOT_URI+
	    			"/"+Constants.GW_ID+"/mobile/"+(String) getRequestAttributes().get("nodeId");
	    	
	    	String Sensor_URI = Device_URI+"/sensor/"+(String) getRequestAttributes().get("Sensor");

	    	StringRepresentation rep;
	    	
	    	
	    	if(Utils.isSensor(Sensor_URI) ){
	    		
	    		    		
	    		requestLocManager req = new requestLocManager(getResponse());
	    			
	    		setAutoCommitting( false );
	    		getApplication().getTaskService().submit( req );
	    			
	    		try
				{
						Thread.sleep( 1000 );
				}
				catch( InterruptedException x )
				{
						x.printStackTrace();
				}
	    			
	    		return null;
	    			
					
	    	}
	    	
	    	
	    	rep = new StringRepresentation (" Mobile Sensor does not exist with URI "+ Sensor_URI);
			return (rep);
	        
		}
}
