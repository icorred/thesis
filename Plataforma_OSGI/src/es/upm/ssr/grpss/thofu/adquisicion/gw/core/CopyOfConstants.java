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

/**
 *
 * @author vgupta
 */
public class CopyOfConstants {
	//Expiration time for registered devices
    public static final int DEFAULT_REG_LIFETIME = 30;
    
    public static final int MICAZ_BATTERY_LEVEL = 3000;
    
    //Sensor types
    public enum operator { MORE, LESS, EQUAL };
    
    public enum SensorState { RIGTH, LEFT, UP, DOWN, PUSH };
    
    //Ports
    public static final int UDP6_SVCPORT = 8888;
    public static final int TCP6_SVCPORT = 8888;
    public static final int TCP_SVCPORT = 8886;
    public static final int UDP6_ADVPORT = 8889;
    public static final int XSERVE_PORT = 9002;
       
    public static final int BD_PORT = 3306;
    
    // IP Addresses
    public static final String XSERVE_IP= "localhost";
            
 // URI of the root directory.  
    public static final String GW_ID = "gw-d205b";
    
    public static final String GW_ROOT_URI ="http://10.1.10.227:6008";
    		
    public static final String ROOT_URI = "file:///C:/Users/Iván/workspace/THOFU_v2/htdocs";
    
   // public static final String ROOT_URI = "file:///./htdocs";
    
    public static final String BD_URL = "jdbc:mysql://10.1.10.233";
    
    public static final String BD_name = "thofu";

	
            
}
