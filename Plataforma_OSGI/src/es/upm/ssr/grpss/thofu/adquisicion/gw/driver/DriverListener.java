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
package es.upm.ssr.grpss.thofu.adquisicion.gw.driver;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;

import driverinterfaces.IMicaZDriver;
import driverinterfaces.ThofuDriver;

//import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;

import es.upm.ssr.grpss.thofu.adquisicion.gw.driver.DriverManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;
//import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.PubSubCore;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IEventManager;

/**
 * @author Ivan
 *
 */
public class DriverListener implements BundleListener {
	
	private enum Driver { MICAZ, SUNSPOT, BIOHARNESS, SUPERVISOR };

	private static BundleContext context;
	
	private static ServiceReference ref;
	
	private static Hashtable<String, ServiceReference> driverServices;
	
	private static DriverManager dm;
		
	public DriverListener(BundleContext context){
		
		DriverListener.context = context;
		
		driverServices = new Hashtable<String, ServiceReference>();  
		
		dm = DriverManager.getInstance();
	}
	
	/*	
	 */
	@Override
	public void bundleChanged(BundleEvent event) {

		String symbolicName = event.getBundle().getSymbolicName();
		
		switch(event.getType()){
		
		case BundleEvent.STARTED:
			
		//	System.out.println("*** Comenzando bundle: "+symbolicName);
			
			if(symbolicName.compareTo("MicaZ") == 0){
								
				ServiceReference ref = context
				        .getServiceReference(IMicaZDriver.class.getName());
				
				IMicaZDriver md = (IMicaZDriver) context.getService(ref);
				
				dm.putDriver((ThofuDriver) md);
										
				driverServices.put(md.getDriverId(), ref);
							
				System.out.println("*** Registrado Driver MICAZ ***");
					
			}
			
		break;
		
		case BundleEvent.STOPPED:
					
			if(driverServices.containsKey(symbolicName)){
					
				System.out.println("*** Finalizando bundle: "+symbolicName);
				
				context.ungetService((ServiceReference) driverServices.get(symbolicName));
				
				driverServices.remove(symbolicName);
				
				dm.removeDriver(symbolicName);
								
				System.out.println("*** Desregistrado Driver MICAZ ***");
								
			}
			
		break;		
			
		}

	}
	
	public void stopDriverBundles(){
		
	}

}
