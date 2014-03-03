package driver_micaz_osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import driverinterfaces.IMicaZDriver;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IEventManager;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IAreaDistinguisher;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	 private IEventManager em;
	 
	 private IAreaDistinguisher ad;
	 
	 private Constants cn;
	 
	 private MicazPacketHandler mph;
	 
	 private ServiceReference reference1;
	 
	 private ServiceReference reference2;
	 
	 private ServiceReference reference3;

	static BundleContext getContext() {
		return context;
	}

	/*
	 */
	public void start(BundleContext bundleContext) throws Exception {
		
		Activator.context = bundleContext;
	
		serviceRegister(bundleContext);
		
		serviceDiscovery(bundleContext);
		
	}

	/*	 
	 */
	public void stop(BundleContext bundleContext) throws Exception {		
		
		context.ungetService(reference1);
		context.ungetService(reference2);
		
		mph.stop();
		
		Activator.context = null;
	}
	
	private void serviceRegister(BundleContext context){
		
		//Obtener servicios de gestion de eventos y parametros de la plataforma
		reference1 = context
			        .getServiceReference(IEventManager.class.getName());
				
		em = (IEventManager) context.getService(reference1);
				
		reference2 = context
				        .getServiceReference(Constants.class.getName());
				
		cn = (Constants) context.getService(reference2);
		
		reference3 = context
		        .getServiceReference(IAreaDistinguisher.class.getName());
		
		ad = (IAreaDistinguisher) context.getService(reference3);		
		
		mph = new MicazPacketHandler(em, ad, cn);
		mph.init();	
		
	}
	
	private void serviceDiscovery(BundleContext context){
		
		context.registerService(IMicaZDriver.class.getName(), 
								mph, null);			
		
		
	}

}
