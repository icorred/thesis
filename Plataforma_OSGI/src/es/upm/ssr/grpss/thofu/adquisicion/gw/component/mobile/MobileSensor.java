	package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

	import java.io.IOException;
	
	import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

	import org.restlet.data.Form;


	import org.restlet.representation.Representation;
	import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
	import org.restlet.resource.Get;
	import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
	import org.restlet.resource.ServerResource;

	import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceForwarder;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;


	public class MobileSensor extends ServerResource{
		 
		static public boolean TagEvent = false;
		static public String idCode = null;
		
		static public boolean MovEvent = false;
		//static public String movCode = null;
		static public JSONObject movCode = null;
		
		static public boolean LocEvent = false;
		static public String coordinates = null;
		
		
		 @Get
			public Representation represent() throws SQLException, IOException  {
		    	
		    	System.out.println("*** GET Mobile Sensor***");
		    	
		    	String Device_URI = Constants.GW_ROOT_URI+
		    			"/"+Constants.GW_ID+"/mobile/"+(String) getRequestAttributes().get("nodeId");
		    	
		    	String Sensor_URI = Device_URI+"/sensor/"+(String) getRequestAttributes().get("Sensor");

		    	StringRepresentation rep;
		    	
		    	
		    	if(Utils.isSensor(Sensor_URI) ){
		    		
		    		if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("rfid")){
		    		    	
		    			TagEvent = false;
		    			
			    		requestTagManager req = new requestTagManager(getResponse());
			    			
			    		setAutoCommitting( false );
			    		getApplication().getTaskService().submit( req );
			    		    
			    		
		    		} else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("mov")){
		    			
		    			requestMovManager req = new requestMovManager(getResponse());
		    			
			    		setAutoCommitting( false );
			    		getApplication().getTaskService().submit( req );			    					    				    			
		    		
		    		} else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("loc")){
		    			
		    			    			
		    			// Define our Restlet client resources.  
		    		    ClientResource mobileClient = new ClientResource(  
		    		    		"http://10.1.10.57:8182/GW-Mobile/NexusS/Sensor/location");
		    		    	//	"http://192.168.1.101:8182/GW-Mobile/NexusS/Sensor/location");
		    		 //   LocClient client = new LocClient();
		    				    	    
		    		    try {
		    				get(mobileClient);
		    			} catch (ResourceException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} catch (IOException e) {
		    				// TODO Auto-generated catch block
		    				//e.printStackTrace();
		    			}
		    			rep = new StringRepresentation (coordinates);
		    					    					    			
		    			System.out.println("*** Coordinates:"+coordinates+"***");		    			  
		    	        		    	        	    			
		    			return rep;	    	
		    			
		    		}	
		    		
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
		 
		 @Put
			public  String updateActuator(Representation entity) throws SQLException, JSONException, IOException {
		    	
		    	System.out.println("*** PUT Mobile Sensor***");
		    	
		    	String Device_URI = Constants.GW_ROOT_URI+
		    			"/"+Constants.GW_ID+"/mobile/"+(String) getRequestAttributes().get("nodeId");
		    	
		    	String Sensor_URI = Device_URI+"/sensor/"+(String) getRequestAttributes().get("Sensor");
		    	
		    	
		    	if(Utils.isSensor(Sensor_URI) ){
		    		
		    		 
		    		 if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("rfid")){
		    			 		    			 
		    		/*	 Form form = new Form(entity);
			    	     idCode = form.getFirstValue("idTag");*/
			    	     
			    	     TagEvent = true;
			    	     
			    	     JSONObject nfc = new JSONObject(entity.getText());
			    	     
			    	     if(nfc.has("idTag")){
			    	    	 idCode = nfc.getString("idTag");
			    	    	 System.out.println("*** ID CODE:"+idCode+"***");
			    	     } else if(nfc.has("stopMonitor")){
			    	    	 idCode = "stop";
			    	    	 System.out.println("*** STOP MONITOR RFID ***");
			    	     }	     		    			 	    			 		    			 
		    			 
		    		 }else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("mov")){
 		    			 			 
		    			 movCode = new JSONObject(entity.getText());
		    			 
						 MovEvent = true;
																		
						 System.out.println("*** MOV CODE:"+movCode.toString()+"***");
		    			 
		    		 }else if(((String) getRequestAttributes().get("Sensor")).equalsIgnoreCase("loc")){
		    				    			    
			    			 Form form = new Form(entity);  
				    	     String status = form.getFirstValue("status"); 
				    	     ClientResource mobileClient = new ClientResource(
				    		    		"http://10.1.10.57:8182/GW-Mobile/NexusS/Sensor/location");
				    	// "http://192.168.1.101:8182/GW-Mobile/NexusS/Sensor/location");
				    		
				    	     
				    	     System.out.println("*** Status:"+status+"***");
				    	     				    	     
		    			 
				    		try {
								put(mobileClient, form.getWebRepresentation());
							} catch (ResourceException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
				    						    					    	   
		    	     
		    	     		    	     		    	     
		    		 }
		    		 
		    		 updateDevList((String) getRequestAttributes().get("nodeId")); 			      
		        
			}
		    	return null;
		 }
		 
		 		 
		 public static void get(ClientResource clientResource) throws IOException, ResourceException {  
   	    	
			   Representation r = clientResource.get();
		    	
		    	
		    
		        if (clientResource.getStatus().isSuccess()
		               && clientResource.getResponseEntity().isAvailable()) {  
		                       	
		            MobileSensor.coordinates = clientResource.getResponseEntity().getText();
		                                    
		            MobileSensor.LocEvent = true;
		           	  	            
		        }
		        
		        clientResource.release();
		        r.release();
				       
		 }
		   
		 private void put(ClientResource clientResource, Representation message) throws IOException, ResourceException {  
	   	    				    			    
			   Representation r=clientResource.put(message);
		    	
		    	clientResource.release();
		    	r.release();	    			    			       
		 }
		 
		 
		 private void updateDevList(String nodeId){
			 
			 String name = new String ("mobile/"+nodeId);
			 String tipo = "Movil Android (NexusS)";
			 String addr = "10.1.10.57";
			 //String addr = "192.168.1.101";
			 String uri = Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"+name;
			 
			 Device dev = DeviceManager.findByName(name);
			
			 if(dev != null){
	 			// Set last heard time		    					    			
	 	        dev.setLastHeardTime(System.currentTimeMillis());
			 }else {
				 
				// This device is considered as local device for the Gateway which load the driver 
				 dev = new Device(name, DeviceForwarder.Type.WIFI, tipo, true, true, addr, uri, (30*60));
		         DeviceManager.addDevice(dev);
				 
			 }
			 
		 }
		 
		   
		   
}
