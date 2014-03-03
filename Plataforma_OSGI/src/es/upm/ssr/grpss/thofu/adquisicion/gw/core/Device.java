/*
 * Copyright (c) 2012, GPDS (UPM-SSR)
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
 * Neither the name of the (UPM-SSR) nor the names of its contributors
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
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Subscription;

/**
 *
 * @author Iv�n Corredor
 */
public class Device {
    private static final int DEFAULT_RETRY_WAITTIME = 10000; // in milliseconds
    private String Id = null;
    private String uri = null;
    private String type = null;
    private DeviceForwarder.Type interactionType;
    private String address = null;
    private boolean usesCompression = false;
    private boolean isLocal = false;

    private long lastHeardTime = 0L;
    private long lastCheckInTime = 0L;
    private int regLifetime = 0;
    private long awakeDuration = 0L;
    private long nextSleepDuration = 0L;
    private long inaccessibleUntil = 0L; // stores a duration (in ms since 1970/1/1
    
    //Buffer of values (for sensors and actuators)
    private  ConcurrentHashMap<String, Hashtable<Long, String>> sensorBuffer;
    
    public Device(String Id, DeviceForwarder.Type type, String sensorType,
            boolean usesCompression, boolean isLocal, String address, String uri, int lifetime) {
        this.Id = Id;
        this.type = sensorType;
        this.interactionType = type;
        this.usesCompression = usesCompression;
        this.isLocal = isLocal;
        this.address = address;
        this.uri = uri;
        this.lastHeardTime = System.currentTimeMillis();
        this.regLifetime = lifetime;
        
      //Producer URI --> (TimeStamp (long) , Value (String))
        sensorBuffer =  new ConcurrentHashMap<String, Hashtable<Long, String>>(Constants.MAX_BUFFER_ENTRIES);
    }

    /**
     * @return the Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return the interactionType
     */
    public DeviceForwarder.Type getInteractionType() {
        return interactionType;
    }

    /**
     * @param interactionType the interactionType to set
     */
    public void setInteractionType(DeviceForwarder.Type type) {
        this.interactionType = type;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }
    
    /**
     * @return the sensor type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param locally connected
     */
    public  boolean isLocal() {
        return isLocal;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
            
    /**
     * @param URI the URI to set
     */
    public void setUri(String URI) {
        this.uri = URI;
    }
    
    /**
     * @param Type the sensor type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return whether the device is sleeping or not
     */
    public boolean isSleeping() {
        // Handle the special case for new devices that haven't
        // sent a check in message yet
        if (lastCheckInTime == 0L) return false;
        long now = System.currentTimeMillis();
        if ((now < (lastCheckInTime + awakeDuration)) ||
                (now > (lastCheckInTime + awakeDuration + nextSleepDuration))) {
            return false;
        }

        return true;
    }

    public long getRemainingSleepDuration() {
        return (lastCheckInTime + awakeDuration +
                nextSleepDuration - System.currentTimeMillis());
    }

    public long getRemainingAwakeDuration() {
        return (lastCheckInTime + awakeDuration - System.currentTimeMillis());
    }

    public boolean isInaccessible() {
        if (System.currentTimeMillis() > inaccessibleUntil) {
            inaccessibleUntil = 0L;
            return false;
        }
        
        return true;
    }
    
    public long getRemainingInaccessibleDuration() {
        return (inaccessibleUntil - System.currentTimeMillis());
    }

    /**
     * @return the usesCompression
     */
    public boolean usesCompression() {
        return usesCompression;
    }

    /**
     * @param usesCompression the usesCompression to set
     */
    public void setUsesCompression(boolean usesCompression) {
        this.usesCompression = usesCompression;
    }
    
    /**
     * @param isLocal the isLocal to set
     */
    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    /**
     * @return the lastHeardTime
     */
    public long getLastHeardTime() {
        return lastHeardTime;
    }

    /**
     * @param lastHeardTime the lastHeardTime to set
     */
    public void setLastHeardTime(long lastHeardTime) {
        this.lastHeardTime = lastHeardTime;
    }
    
    /**
     * 
     * @param URI URI of the device to be set
     * @param value value produced by the device
     */
    public void setValueCache(String URI, String value){
       	
       	    		
		if(!sensorBuffer.containsKey(URI)){
				
			sensorBuffer.put(URI, new Hashtable<Long, String>(Constants.MAX_BUFFER_ENTRIES));
							
		}
						
		Hashtable<Long, String> values = sensorBuffer.get(URI);
			
		values.put(System.currentTimeMillis(), value);
			
		sensorBuffer.put(URI, values);			
			
		System.out.println(">>> Guardando en cache de device: "+URI);
	    	
    }
    
    public boolean isValueCache(){
    	
    	    	
    	if(sensorBuffer.isEmpty()){
 
    		return false;
    	}
     	
    	return true;
    	
    }
    
    public ConcurrentHashMap<String, Hashtable<Long, String>>  getValueCache(){
  		
   		return sensorBuffer;
     }
    
    public void cleanValueCache(){
    	    	
    	sensorBuffer.clear();
    }
      
    public int getRegLifetime() {
        return regLifetime;
    }
    
    public static String getInfoHdrs() {
        String result =
                "<th>" + "Id" + "</th>" +
                //"<th>" + "Connection" + "</th>" +
                "<th>" + "Service addr" + "</th>" +
                "<th>" + "Compression" + "</th>" +
                "<th>" + "Heard" + "</th>" +
                "<th>" + "Expiration" + "</th>" +
                "<th>" + "CheckIn" + "</th>" +
                "<th>" + "Status" + "</th>" +
                "<th>" + "Resource <br/> cache" + "</th>" +
                "<th>" + "Request <br/> queue" + "</th>";

        return result;
    }

    private String getStatus() {
        String result = "";
            if (isInaccessible()) {
                result += "Inaccessible <br/>for " +
                Utils.makeDurationEstimate(getRemainingInaccessibleDuration());
            } else if (isSleeping()) {
                result += "Sleeping for another " +
                        Utils.makeDurationEstimate(getRemainingSleepDuration());
            } else {
                result += "Awake";

                if (getRemainingAwakeDuration() > 0) {
                    result += "<br/>for " +
                            Utils.makeDurationEstimate(getRemainingAwakeDuration()) +
                            ", then asleep for " +
                            Utils.makeDurationEstimate(nextSleepDuration);
                }
            }
        return result;
    }
    
    public String getInfoAsHTMLRow() {
        String summary = "";
        String result =
                    "<td>" + "<a href=\"" + getId() + "/.well-known/r\">" + getId() +
                        "</a>" + "</td>" +
                    "<td>" + getAddress() + "<br/>" +
                    "(" + Utils.InteractionToString(getInteractionType()) +
                    ")" + "</td>" +
                    "<td>" + (usesCompression() + "").toUpperCase() + "</td>" +
                    "<td>" + 
                        Utils.makeDurationEstimate(System.currentTimeMillis() -
                            getLastHeardTime()) + " ago" + "</td>";
        result += "<td> In " + Utils.makeDurationEstimate((getLastHeardTime() +
                1000*getRegLifetime()) - System.currentTimeMillis()) + "</td>";
        
        if (lastCheckInTime == 0) {
            result += "<td>" + "" + "</td>" +
                    "<td>" + "Awake" + "</td>";
        } else {
            result += "<td>" +
                    Utils.makeDurationEstimate(System.currentTimeMillis() -
                        lastCheckInTime) + " ago" + "</td>";
            result += "<td>" + getStatus() + "</td>";
        }

        return result;
    }

    public String getInfoAsJSON() {
       
        String result =
                    "\t{\n" +
                    "\t\t\"id\": \"" + getId() + "\",\n" +
                    "\t\t\"address\": \"" + getAddress() + "\",\n" +
                    "\t\t\"uri\": \"" + getUri() + "\",\n" +
                    "\t\t\"type\": \"" + getType() + "\",\n" +
                    "\t\t\"protocol\": \"" +
                    Utils.InteractionToString(getInteractionType()) + "\",\n" +
                    "\t\t\"compression\": " +
                    (usesCompression() + "").toLowerCase() + ",\n" +
                    "\t\t\"lastHeard\": " + getLastHeardTime() + ",\n" +
//                    "\t\tawakeTime: " + awakeDuration + ",\n" +
//                    "\t\tsleepTime: " + nextSleepDuration + ",\n" +
                    "\t\t\"expiry\": " + ((getLastHeardTime() +
                    1000*getRegLifetime()) - System.currentTimeMillis()) + ",\n" +
                    "\t\t\"lastCheckIn\": " + lastCheckInTime + ",\n" +
                    "\t\t\"status\": \"" + getStatus() + "\",\n" +
                    "\t\t\"resCache\": " + "\"/rc/" + getId() + "/\",\n" +
                    "\t}";

        return result;
    }
    
    public void handleCheckIn(int awakeDuration, int sleepDuration) {
        long now = System.currentTimeMillis();
        setLastHeardTime(now);
        lastCheckInTime = now;
        this.awakeDuration = awakeDuration * 1000;
        this.nextSleepDuration = sleepDuration * 1000;
        //notifyAll(); // activate any waiting threads ...
    }

}
