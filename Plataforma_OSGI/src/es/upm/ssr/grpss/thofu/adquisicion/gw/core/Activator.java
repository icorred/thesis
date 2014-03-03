/*
 * Copyright (c) 2013, Data Processing and Simulation Group 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the Data Processing and Simulation Group nor the names 
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package es.upm.ssr.grpss.thofu.adquisicion.gw.core;

import java.sql.SQLException;

import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area.AreaDistinguisher;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.area.getArea;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.arduino.ArduinoActuator;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.arduino.ArduinoSensor;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.localization.LocAreaManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.localization.LocSystem;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.micaz.MicazActuator;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing.SmartThing;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing.SmartThingEvent;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing.SmartThingLoc;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing.SmartThingLocEvent;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.smartthing.SmartThingSensor;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.subscriber.DeleteAllSubscription;
import es.upm.ssr.grpss.thofu.adquisicion.gw.component.subscriber.SubscriptionManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.driver.DriverListener;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.EventManager;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IAreaDistinguisher;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IEventManager;

/*import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.restlet.resource.Directory;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;


public class Activator implements BundleActivator {
	
	// Create a new Component.
	static Component component = new Component();
	
	static DriverListener dl;
	
	@Override	
	public void start(BundleContext context) throws Exception {
        			
		//Create a list of drivers
	//	final String[] driverList = {" "}; //{"arduino","micaz"};
			
		//Configure Data Base server
	//	System.out.println(" *** CONFIGURACION INICIAL DEL GATEWAY DE LA PLATAFORMA DE ADQUISICION *** \n\n");
		Constants.configServer();
	//	Constants.initConstants();
		
		// Add a new HTTP server listening on port 6008.
				
		component.getServers().add(Protocol.HTTP, Constants.RESTLET_PORT);
		component.getContext().getParameters().add("maxThreads", "512");
	/*	component.getContext().getParameters().add("minThreads", "100");
		component.getContext().getParameters().add("lowThreads", "145");
		component.getContext().getParameters().add("maxQueued", "100");
		component.getContext().getParameters().add("maxTotalConnections", "100");
		component.getContext().getParameters().add("maxIoIdleTimeMs", "100");*/
		
		
	    component.getClients().add(Protocol.FILE);
		component.getClients().add(Protocol.HTTP);
		
		
			
		// **** Create an application a Web Service ****
		Application application = new Application(component.getContext().createChildContext()){
			
			@Override
			public Restlet createInboundRoot(){

			//create router
			Router router=new Router(getContext());
			
			//attach static web files to "www" folder
			Directory dir=new Directory(getContext(), Constants.ROOT_URI);
			dir.setListingAllowed(true);
			dir.setDeeplyAccessible(true);
						
			router.attach("/sensorweb",dir);
			router.attach("/listJSON",ListJSON.class);
			router.attach("/listtext",ListSensorText.class);
			router.attach("/listmicaztext",ListMicazText.class);
			router.attach("/listspottext",ListSpotText.class);
			router.attach("/listbiotext",ListBioText.class);
						
			//	router.attach("/{gw-id}/mobile/{nodeId}/sensor/{Sensor}", MobileSensor.class);
				
			router.attach("/{gw-id}/{thing-id}/{node-id}/sensor/{sensor}", SmartThingSensor.class);
				
			//Subscriber
			router.attach("/{gw-id}/eventmanager/{Component}", SubscriptionManager.class);	
			router.attach("/{gw-id}/eventmanager/remove/all", DeleteAllSubscription.class);
			
			//Localization Cricket
			router.attach("/{gw-id}/loc/{LocSystem}", LocSystem.class);
			
			//Smart Thing Management
			router.attach("/{gw-id}/smartthing", SmartThing.class);
			router.attach("/{gw-id}/{thing-id}/{node-id}/sensor/{sensor}", SmartThingSensor.class);
			router.attach("/{gw-id}/{thing-id}/{node-id}/sensor/{sensor}/event", SmartThingEvent.class);
			
			//Localization Service 
			router.attach("/{gw-id}/{thing-id}/{node-id}/localization", SmartThingLoc.class);
			router.attach("/{gw-id}/{thing-id}/{node-id}/localization/event", SmartThingLocEvent.class);
			router.attach("/{gw-id}/areamanager", LocAreaManager.class);
			router.attach("/{gw-id}/areaidentifier", getArea.class);
			
			//For all actuators controlled by Arduino
			router.attach("/{gw-id}/arduino/{nodeId}/actuator/{Actuator}", ArduinoActuator.class);
			router.attach("/{gw-id}/arduino/{nodeId}/sensor/{Sensor}", ArduinoSensor.class);
	
				
				return router;
			}
		};
						
		
		try {
			Utils.openDBConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Init drivers
		Utils.startRefreshProcess();
		
		//Register platform services
		serviceRegister(context);
		
		//Init Dumping process
		Utils.startCacheDumping();
		
		dl = new DriverListener(context);
		
		context.addBundleListener(dl);
					
		// Attach the application to the component and start it  
		component.getDefaultHost().attach(application);		
	 
		// Start the component.
		try {
			component.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
	//	context.addBundleListener(dl);
		
		component.stop();		
	}
	
	private void serviceRegister(BundleContext context){
		
		context.registerService(IEventManager.class.getName(), 
								EventManager.getInstance(), null);	
		
		context.registerService(Constants.class.getName(), 
								Constants.getInstance(), null);	
		
		context.registerService(IAreaDistinguisher.class.getName(), 
				AreaDistinguisher.getInstance(), null);		
		
		
	}

}
