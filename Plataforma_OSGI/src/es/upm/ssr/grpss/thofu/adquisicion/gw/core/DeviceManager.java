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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author vgupta
 */
public class DeviceManager {
    private static long EXPIRATION_THRESHOLD = 30000;
    private static ConcurrentHashMap<String, Device> deviceByNameList = new ConcurrentHashMap<String, Device>();
    private static ConcurrentHashMap<String, Device> deviceByAddressList = new ConcurrentHashMap<String, Device>();

    public static Device findByName(String deviceName) {
        return ((Device) deviceByNameList.get(deviceName));
    }

    public static Device findByAddress(String deviceAddress) {
        Device result = (Device) deviceByAddressList.get(deviceAddress);

        // XXX Hack to allow radiogram for check in messages even when
        // the SPOT is using UDP
        if ((result == null) && (deviceAddress.length() == 19)) {
            String newaddr = "fe80:0:0:0:214:4f01:0:" +
                    deviceAddress.substring(15).toLowerCase();
            //System.out.println("Checking for IPv6 addr " + newaddr);
            result = (Device) deviceByAddressList.get(newaddr);
        }

        return result;
    }

    public static void addDevice(Device device) {
        if (deviceByNameList.get(device.getId()) != null) {
            System.err.println("Device with name:" + device.getId() +
                    " & address:" + device.getAddress() + " already on list");
        } else {
            String addr = device.getAddress();
            String addrWoutPort = null;
            if (addr.startsWith("[fe80:")) {
                int indexOfColon = addr.indexOf("]");
                addrWoutPort = addr.substring(1, indexOfColon);
            } else if (addr.indexOf(":")!=-1) {
                int indexOfColon = addr.indexOf(":");
                addrWoutPort = addr.substring(0, indexOfColon);
            } else {
            	addrWoutPort = addr;
            }

            deviceByAddressList.put(addrWoutPort, device);
            deviceByNameList.put(device.getId(), device);

            Date date = new Date();
            System.out.println(date + ": Device " + device.getId() +
                    " added to list");              
           
        }
    }

    public static void removeDevice(String deviceId) {
        Device device = findByName(deviceId);
        if (null == device) {
            System.err.println("Device " + deviceId + " not found.");
        } else {
                     
          //Change state of the device in DB (only if this device is locally connected)
        if(device.isLocal()){        	
            Utils.setDeviceStatus(Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"/"
            + device.getId().replace('-', '/'), "off");
        }
            
            deviceByNameList.remove(device.getId());
            deviceByAddressList.remove(device.getAddress());    
            
        }
    }
    
    public static String getDeviceList(String nodeType, String format) {
        StringBuffer sb = new StringBuffer();
        Enumeration e = deviceByNameList.elements();
        int idx = 0;
        int enc = 0;

        expireDevices();
        
        if(nodeType==null){
	        if (format.equalsIgnoreCase("html")) {
	            sb.append("Found " + deviceByNameList.size() + " devices " +
	                    "[Updated: " +
	                    Utils.makeTimestamp(System.currentTimeMillis()) + "]");
	            sb.append("<table border=\"1\">");
	            sb.append("<tr align=\"center\">" + "<th>" + "No." + "</th>" +
	                    Device.getInfoHdrs() + "</tr>");
	
	            while (e.hasMoreElements()) {
	                Device d = (Device) e.nextElement();
	                idx++;
	                sb.append("<tr align=\"center\">" +
	                        "<td>" + idx + "</td>" +
	                        d.getInfoAsHTMLRow() +
	                        "</tr>");
	            }
	            sb.append("</table>");
	        } else if (format.equalsIgnoreCase("json")) {
	            sb.append("[\n");
	            while (e.hasMoreElements()) {
	                Device d = (Device) e.nextElement();
	                idx++;
	                sb.append(d.getInfoAsJSON());
	                if (e.hasMoreElements()) {
	                    sb.append(",\n");
	                } else {
	                    sb.append("\n");
	                }
	            }
	            sb.append("]");
	        } else { // send a simple array of device Ids
	            sb.append("[\n");
	            while (e.hasMoreElements()) {
	            	Device d = (Device) e.nextElement();
	                idx++;
	                sb.append("\"" + d.getId().replace('-', '/') + "\"");
	                if (e.hasMoreElements()) {
	                    sb.append(",\n");
	                } else {
	                    sb.append("\n");
	                }
	            }
	            sb.append("]");
	        }
        
        } else {
        	
        	 sb.append("[\n");
        	 
             while (e.hasMoreElements()) {
            	 
                 Device d = (Device) e.nextElement();
                 String name=null;
                 idx++;
                 if(d.getId().contains(nodeType)){
                	
                if(d.isLocal()){
                	name = d.getId().replace('-', '/');
                }
                	 
                   name = d.getId();
                	 
                   sb.append("\"" + name + "\"");
                	 
                	if (e.hasMoreElements()) {
                       sb.append(",\n");
                    } else {
                       sb.append("\n");
                    }
                	             	                 	 
                	 System.out.println("**** Nombre sensor encontrado: "+d.getId());
                	 
                      
                 }
               
             }             
             sb.append("]");
        	
        	
        }
        
        return sb.toString();
    }
    
    public static ConcurrentHashMap<String, Device> getList(){
        
        return deviceByNameList;
       		 
       }

    public static void expireDevices() {
        Enumeration e = deviceByNameList.elements();
        long now = System.currentTimeMillis();
        Date date = new Date();
        
        while (e.hasMoreElements()) {
            Device d = (Device) e.nextElement();

            if ((d.getLastHeardTime() + (d.getRegLifetime() * 1000)) < now) {
                System.out.println(date + ": Expiring " + d.getId() + "...");
                
                // If the device is local, remove from local list
                if(d.isLocal()){
                	removeDevice(d.getId());
                // If it is a device externally controlled, ask previously about its status
                }else if((Utils.getStatus(d.getUri())).compareTo("awake") == 0){
                	d.setLastHeardTime(System.currentTimeMillis());
                }else{
                	removeDevice(d.getId());
                }
                
            }
        }
        
    }
}
