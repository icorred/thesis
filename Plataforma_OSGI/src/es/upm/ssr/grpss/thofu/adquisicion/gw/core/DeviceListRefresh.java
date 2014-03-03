/*
 * Copyright (c) 2009, Sun Microsystems
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
 * Neither the name of the Sun Microsystems nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.ArrayList;
//import com.sun.spot.util.Utils;

/**
 *
 * @author Iv�n Corredor
 */
public class DeviceListRefresh extends Thread {
    boolean done = false;

    public void markDone() {
        done = true;
    }

    public void run() {
        System.out.println("Starting device list refresh thread ... ");
        
        ArrayList<Device> devices = null;
        ArrayList<String> gw = null;
        int count = 0;
        
        while (!done) {
        	        	
        	if(count==3){
	        	//Search active device into every Gw associated to the Smart Space
	        	
	        	gw = Utils.getGwList();
	        	count = 0;
	        	
	        	for(int i=0; i < gw.size(); i++){
	        		
	        		//System.out.println(gw.get(i));
	        		
	        		//For every external Gw
	        	//	if(gw.get(i).compareTo(Constants.GW_ROOT_URI+"/"+Constants.GW_ID)!=0){
	        			
	        		//	System.out.println("Entro");
	        			devices = Utils.getActiveDeviceList(gw.get(i));
	        			
	        			for(int d=0; d < devices.size(); d++){
	        				DeviceManager.addDevice(devices.get(d));
	        			}
	        	//	}
        		
	        	}
	        	
        	}
        	        	
            DeviceManager.expireDevices();
            
            count++;               
                       
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}
