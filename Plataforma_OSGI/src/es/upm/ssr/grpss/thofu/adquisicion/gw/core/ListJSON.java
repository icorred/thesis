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

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;



/**
 *
 * @author vgupta
 */
public class ListJSON extends ServerResource {


    private String result;
    
   @Get(":json")
   public String processRequest() {
      //  HttpResponse res = new HttpResponse();

	   System.out.println("***** Lista de dispositivos *****");
        // Check if this is a request for the base URL ...
   /*     if (req.getPathInfo().equalsIgnoreCase("/") ||
                req.getPathInfo().equals("")) {
            res.setStatus(HttpResponse.SC_MOVED_PERMANENTLY);
            res.setHeader("Location", "/doc/index.html");

            result = new String(res.getBody());
            
            return result;
        }

        if (req.getPathInfo().equalsIgnoreCase("/spots")) {
            res.setStatus(HttpResponse.SC_OK);
            res.setHeader("Content-Type", "text/plain");
            String devList = DeviceManager.getDeviceList("text");
            res.setHeader("Content-Length", "" + devList.getBytes().length);
            res.setBody(devList.getBytes());
            
            result = new String(res.getBody());
            return result;
        }*/

        // Check if this is a request to list all available devices ...
    //    if (req.getPathInfo().equalsIgnoreCase("/devices.json")) {
      //      res.setStatus(HttpResponse.SC_OK);
      //      res.setHeader("Content-Type", "text/plain");
            String devList = DeviceManager.getDeviceList(null,"JSON");
            
            System.out.println("*****"+ devList +"*****");
      //      res.setHeader("Content-Length", "" + devList.getBytes().length);
      //      res.setBody(devList.getBytes());
            
            result = new String(devList.getBytes());            
            return result;
      //  }

        // Check if this is a request to list all available devices ...
   /*     if (req.getPathInfo().equalsIgnoreCase("/devices.html")) {
            res.setStatus(HttpResponse.SC_OK);
            res.setHeader("Content-Type", "text/html");
            String devList = DeviceManager.getDeviceList("HTML");
            res.setHeader("Content-Length", "" + devList.getBytes().length);
            res.setBody(devList.getBytes());
            
            result = new String(res.getBody());            
            return result;
        }*/

    }

}
