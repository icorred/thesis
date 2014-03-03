package es.upm.ssr.grpss.thofu.adquisicion.gw.component.mobile;

import java.io.IOException;  

import org.restlet.data.Form;  
import org.restlet.representation.Representation;  
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException; 

public class LocClient{


	/** 
     * Prints the resource's representation. 
     *  
     * @param clientResource 
     *            The Restlet client resource. 
     * @throws IOException 
     * @throws ResourceException 
     */  
    public void get(ClientResource clientResource) throws IOException, ResourceException {  
    	    	
    	clientResource.get();
    
        if (clientResource.getStatus().isSuccess()
               && clientResource.getResponseEntity().isAvailable()) {  
                       	
            MobileSensor.coordinates = clientResource.getResponseEntity().getText();
                                    
            MobileSensor.LocEvent = true;
                    
            
        }  
       
    }
	
	

}
