/*
 * Copyright (c) 2012, Group of Data Processing and Simulation
 * Universidad Polit�cnica de Madrid 
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
 * Neither the name of the Universidad Polit�cnica de Madrid nor the names of its contributors
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

package driver_micaz_osgi;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import driverinterfaces.IMicaZDriver;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Constants;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Device;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.Utils;

import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceForwarder;
import es.upm.ssr.grpss.thofu.adquisicion.gw.core.DeviceManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.Event;

import org.json.JSONException;
import org.json.JSONObject;

import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.PrintStreamMessenger;

import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.Circular;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.Hyperbolic;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.Weighted_Circular;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.Weighted_Hyperbolic;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.DataBase;

import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IEventManager;
import es.upm.ssr.grpss.thofu.adquisicion.serviceinterfaces.IAreaDistinguisher;


public class MicazPacketHandler implements MessageListener, IMicaZDriver{
	private static String source = "serial@/dev/ttyUSB1:micaz";
	
	private static String driverId = "MICAZ";
	
	boolean done = false;
	
	private PhoenixSource phoenix;
	private static MoteIF mif;
	
	private HashMap<Integer, Integer> duplicateTable = new HashMap<Integer, Integer>();
	private static int seqNoEvn = 0;
	
	int n;
	
	//For environmental motes
	float temp = 0;
	float humid = 0;
	double light = 0;
	double battery = 0;
	double[] sensirionCalcData;
	String status;
	Hashtable<String, String> values = null;
	
	Message micazMessage;
	
	//For localization motes
	 public Map<Integer,Map<Integer, DataBase>> beaconReg = new HashMap<Integer, Map<Integer, DataBase>>();
	 public organize_packets OP=new organize_packets();
	 public int flag=0;
	 public int count=0;
	 private int previousArea=0;
	 
	 //Localization Command
	 final static int START_LOCALIZATION = 1;
	 final static int STOP_LOCALIZATION = 0;
	 final static int CHANGE_POWER = 2;
	 final static int RESET_S_NUM = 3;
	 
	 final static int MAX_LOC_THREADS = 5;
	 
	 private static int seqNoLoc = 0;
	 
	 //Hash table with open threads using localization (5 threads max)
	 private static HashMap<Integer, TimerTask> locProcesses = new HashMap<Integer, TimerTask>(MAX_LOC_THREADS);
	 private static IAreaDistinguisher ad;
	 
	 private final static int LOC_ACCURACY = 2;
	 
	 //Localization algorithms
	 Circular task1=new Circular(); 
	 Hyperbolic task2=new Hyperbolic();
	 Weighted_Circular task3=new Weighted_Circular(); 
	 Weighted_Hyperbolic task4=new Weighted_Hyperbolic();
	 
	 //For events
	 private static String ProducerURI;
	 	 

	boolean init = true;
	
	static IEventManager em;
	
	static Constants cons;
		
	//Instance of MicazPacketHandler
	
	
	public MicazPacketHandler(IEventManager emanager, IAreaDistinguisher ad,  Constants cons) {
						
		this.cons = cons;
		
		ProducerURI = cons.GW_ROOT_URI+"/"+cons.GW_ID+"/";
		
		this.em = emanager;
		this.ad = ad;
		
		/*** Test localization ***/
	/*	resetLocSystem(103);
		
		Map<Integer, DataBase> mobileDB = new HashMap<Integer, DataBase>();
    	
    	beaconReg.put(103, mobileDB);
		
		startLocalization(103, 1, 3000, 90000);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	/*	Map<Integer, DataBase> mobileDB2 = new HashMap<Integer, DataBase>();
    	
		resetLocSystem(100);
		
    	beaconReg.put(100, mobileDB2);
    	
		//resetLocSystem(103);
		startLocalization(100, 1, 3000, 100000);*/
			
	}
	
	@Override
	public void init() {
		
		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		mif = new MoteIF(phoenix);
		mif.registerListener(new Greenhouse(), this);
		mif.registerListener(new EnvEvent(), this);
		mif.registerListener(new AlivePacket(), this);
		mif.registerListener(new ControlLocalizationMsg(), this);
		mif.registerListener(new DataPacketMsg(), this);
		mif.registerListener(new MatMsg(), this);
		mif.registerListener(new PIRMsg(), this);
		mif.registerListener(new ProxMsg(), this);
	
		//AreaDistinguisher.initArea();
		
		this.setEnvThreshold((short) 0,0.0,0.0);		
		
	}

	@Override
	public void stop() {
		
		mif.deregisterListener(new Greenhouse(), this);
		mif.deregisterListener(new EnvEvent(), this);
		mif.deregisterListener(new AlivePacket(), this);
		mif.deregisterListener(new ControlLocalizationMsg(), this);
		mif.deregisterListener(new DataPacketMsg(), this);
		mif.deregisterListener(new MatMsg(), this);
		mif.deregisterListener(new PIRMsg(), this);
		mif.deregisterListener(new ProxMsg(), this);
		
		//
		phoenix.shutdown();
		
	}	
	
	@Override
	public String getDriverId(){
		
		return driverId;
	}
	
	

	
	public void markDone() {
		done = true;
	}


	private Device addDevicetoBD(String mote, String moteId, int moteType){


		String name = new String(mote+"/"+moteId);

		String addr = moteId;

		String uri = cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name;

		String type = " ";
		
		String sensorBoard;

		Device moteDevice = null;

		// Device if doesn't exist in the list

		String Dev_URI = Utils.isDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+mote, addr);

		if(Dev_URI == null){

			switch (moteType){
			
			//Micaz: Environmental+localization
			case 1:
				
				System.out.println("*** Micaz (Env/loc) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "MTS400 (temperature/humidity/localization)";
				type = "Environmental Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=Gateway in d205 office \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				Utils.setSensor(uri+"/sensor/temp", uri,  "temp", "celsius", "double", "Sensor Temperatura");
				Utils.setSensor(uri+"/sensor/hum", uri, "hum", "HR", "double", "Sensor Humedad");
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Micaz-MTS400 Agregada");
				
			break;
			
			//Micaz: Localization
			case 2:
				System.out.println("*** Micaz (Loc) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Micaz (localization)";
				type = "Localization Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=Gateway in d205 office \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
								
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Micaz-Localizacion Agregada");
				
			break;
			
			//Arduino: sense floor
			case 3:
				
				System.out.println("*** Arduino (Sense Floor) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Arduino (sense floor)";
				type = "Sense Floor Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=Gateway in d205 office \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				Utils.setSensor(uri+"/sensor/mat", uri, "presence", "binary", "str", "Presion sobre alfombrita");
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Micaz-Sense Floor Agregada");				
				
			break;
			
			//Arduino: PIR
			case 4:
				
				System.out.println("*** Arduino (PIR) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Arduino (PIR)";
				type = "PIR Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=Gateway in d205 office \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				Utils.setSensor(uri+"/sensor/pir", uri, "presence", "binary", "str", "Presencia PIR");
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Arduino-PIR Agregada");				
				
			
			break;
			
			//Micaz: Proximity
			case 5:
				
				System.out.println("*** Micaz (Proximity) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Micaz (Proximity)";
				type = "Proximity Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=Gateway in d205 office \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				Utils.setSensor(uri+"/sensor/proximity", uri, "presence", "binary", "str", "Proximidad de mota movil");
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Micaz-Proximity Agregada");				
				
			break;
			
			//Micaz: mobile
			case 6:
				
				System.out.println("*** Micaz (Mobile) to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Micaz (Mobile)";
				type = "Mobile Mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=not defined \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				//TODO: �Incluir sensores en mota m�vil?
				
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Micaz-Movil Agregada");				
				
			break;
			
			//Cricket
			case 7:
				
				System.out.println("*** Cricket to BD ADDING TO THOFU DB ***");
				
				sensorBoard = "Cricket";
				type = "Cricket mote";
				
				Utils.setContextualDevice(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+name, 
						"location=not defined \n board="+sensorBoard, "v1.0", type, 
						"IEEE802154", Utils.makeTimestamp(System.currentTimeMillis()), 5*60,  addr, true , "awake", "123");	
				
				//TODO: �Incluir sensores en mota cricket?
				
				Utils.setSensor(uri+"/sensor/status", uri, "status", "status", "str", "Estado mota");					
				
				System.out.println("Nueva Cricket Agregada");		
				
			break;
			
			default:
				System.out.println("*** Not defined Mote Type ***");
			break;
				
			}
			
			//If the Mote type is recognized by the system
			if(moteType > 0){
				// This device is considered as local device for the Gateway which load the driver 
				moteDevice = new Device(name, DeviceForwarder.Type.IEEE802154, type, true, true, addr, uri, (5*60));
				DeviceManager.addDevice(moteDevice);
			}

			//If Micaz exists in DB, then update BD with new device if necessary
		}else {

			//Extract Node Id
			String[] id = Dev_URI.split("/");

			//If Micaz exists in DB, then update BD with new device if necessary	
			Utils.setDeviceStatus(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+id[id.length-2]+"/"+id[id.length-1], "awake");
							
			switch (moteType){
			
			//Environmental Mote
			case 1:
			    type = "Environmental Mote";
			break;
			
			//Localization Mote
			case 2:
			    type = "Localization Mote";
			break;
			
			//Sense floor
			case 3:
			    type = "Sense floor Mote";
			break;
			
			//PIR
			case 4:
			    type = "PIR Mote";
			break;
			
			//Proximity
			case 5:
			    type = "Proximity Mote";
			break;
			
			//Mobile
			case 6:
			    type = "Mobile Mote";
			break;
			
			//Cricket
			case 7:
			    type = "Cricket Mote";
			break;
			
			}
			
			moteDevice= new Device(id[id.length-2]+"/"+id[id.length-1], DeviceForwarder.Type.IEEE802154, type, true, true, 
					addr, cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+id[id.length-2]+"/"+id[id.length-1], (5*60));
			
			DeviceManager.addDevice(moteDevice);
		}
				
		return moteDevice;

	}

	//Luca
	@Override
	public void messageReceived(int dst, Message msg) {
		
		micazMessage = msg;
				
		Thread micazHandler = new Thread( new Runnable() {
			
			public void run() {
			
			//If an environmental packet is received
			if (micazMessage instanceof Greenhouse) {
				Greenhouse packet = (Greenhouse)micazMessage;
					
				if ((!duplicateTable.containsKey(packet.get_id()) || duplicateTable.get(packet.get_id()) != packet.get_seqNo())
						&& (packet.get_error() == 0)) {
					
					duplicateTable.put(packet.get_id(), packet.get_seqNo());
											
					//Check if the source device is in the data base
					String nameMTS400 = "micaz/"+String.valueOf(packet.get_id()); 
					Device moteDev = checkMoteList("micaz", String.valueOf(packet.get_id()), 1);
									
					if(packet.get_Voltage_data() != 0){
						battery = 1230 * 1024 / packet.get_Voltage_data();
					}
					
					status = setStatusDescription(nameMTS400, battery); 
					
					//values = new Hashtable<String, Hastable<Lon String>();
					
					moteDev.setValueCache(ProducerURI+nameMTS400+"/sensor/status", status);
									
					//Calculate temperature and humidity
					sensirionCalcData = calculateSensirion(packet.get_Temp_data(), packet.get_Hum_data());
										
					moteDev.setValueCache(ProducerURI+nameMTS400+"/sensor/temp", String.valueOf(sensirionCalcData[0]));

					moteDev.setValueCache(ProducerURI+nameMTS400+"/sensor/hum", String.valueOf(sensirionCalcData[1]));

					System.out.println("*** Paquete de datos periodico de mota:"+nameMTS400+" temp:"+ sensirionCalcData[0]+ " hum: "+ sensirionCalcData[1]);
					
					//Add sensors reading to DB
					updateMoteStatus(moteDev);
					
				}
			}
			
					
			//If an event is received
			else if (micazMessage instanceof EnvEvent) {
				EnvEvent eventPacket = (EnvEvent)micazMessage;
				
				if (!duplicateTable.containsKey(eventPacket.get_id()) || duplicateTable.get(eventPacket.get_id()) != eventPacket.get_seqNo()) {
					duplicateTable.put(eventPacket.get_id(), eventPacket.get_seqNo());
					
					//Calculate temperature and humidity
					sensirionCalcData = calculateSensirion(eventPacket.get_temperature(), eventPacket.get_humidity());
					
					Event ev;
													
					String ProducerId;

					ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
					JSONObject pair = new JSONObject();
					
					//Check if the source device is in the data base
					Device moteDev = checkMoteList("micaz", String.valueOf(eventPacket.get_id()), 1);
					
					switch (eventPacket.get_type()){
						
					//Event triggered from temperature threshold
					case 3:
						
						try {
							pair.put("key", "temp");
							pair.put("unit", "celsius");
							pair.put("value", String.valueOf(sensirionCalcData[0]));
							
						} catch (JSONException e) {
							e.printStackTrace();
						}					
						
						payload.add(pair);
						
						ProducerId = "micaz/"+eventPacket.get_id()+"/sensor/temp";
																
						ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
						
						System.out.println("*** Evento de temperatura recibido: "+ev.toString());
						
						em.setEvent(ev);	
						
						//Update mote status
						moteDev.setValueCache(ProducerURI+ProducerId, String.valueOf(sensirionCalcData[0]));
						
						updateMoteStatus(moteDev);
						
					break;
					
					//Event triggered from humidity threshold
					case 4:
						
						try {
							pair.put("key", "hum");
							pair.put("unit", "RH");
							pair.put("value", String.valueOf(sensirionCalcData[1]));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						payload.add(pair);
						
						ProducerId = "micaz/"+eventPacket.get_id()+"/sensor/hum";
						
						ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
						
						System.out.println("*** Evento de humedad recibido: "+ev.toString());
						
						em.setEvent(ev);
						
						//Update mote status
					//	values = new Hashtable<String, String>();
						
						moteDev.setValueCache(ProducerURI+ProducerId, String.valueOf(sensirionCalcData[1]));
						
					   updateMoteStatus(moteDev);
						
					break;
					
					
					}			
					
				}
			}
			
			//If an Alive packet is received 
			else if (micazMessage instanceof AlivePacket) {
				AlivePacket alivePacket = (AlivePacket)micazMessage;
				
				if (!duplicateTable.containsKey(alivePacket.get_id()) || duplicateTable.get(alivePacket.get_id()) != alivePacket.get_seqNo()) {
					duplicateTable.put(alivePacket.get_id(), alivePacket.get_seqNo());
					
					System.out.println("*** Recibido paquete Alive desde mota : "+alivePacket.get_id());
					//Check if the source device is in the data base. If not add this mote to the data base.
					
					Device moteDev;
					String name;
					
									
					if(alivePacket.get_code() == 3 || alivePacket.get_code() == 4){//If the mote is arduino
						
						name = "arduino";
						
					}else{
						
						name = "micaz";
					}
						
					moteDev = checkMoteList(name, String.valueOf(alivePacket.get_id()), alivePacket.get_code());
					//TODO actualizar valor de estado en caso de que el "alive" incluya bateria
					
					if(alivePacket.get_battery() != 0){
						battery = 1230 * 1024 / alivePacket.get_battery();
					}
					
					name = name+"/"+alivePacket.get_id();
					status = setStatusDescription(name, battery); 
															
					moteDev.setValueCache(ProducerURI+name+"/sensor/status", status);
					
					updateMoteStatus(moteDev);				
					
				}
			}
			
			//If a PIR packet is received
			else if (micazMessage instanceof PIRMsg){
				
				PIRMsg PIRPacket = (PIRMsg) micazMessage;
				
				if (!duplicateTable.containsKey(PIRPacket.get_node_tx()) || duplicateTable.get(PIRPacket.get_node_tx()) != PIRPacket.get_seqNo()) {
					duplicateTable.put(PIRPacket.get_node_tx(), PIRPacket.get_seqNo());
					
					Event ev;
														
					String ProducerId;

					ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
					JSONObject pair = new JSONObject();
					JSONObject aux = new JSONObject();
					
					//Check if the source device is in the data base
					Device moteDev = checkMoteList("arduino", String.valueOf(PIRPacket.get_node_tx()), 4);
					
					try {
						pair.put("key", "presence");
						pair.put("unit", "binary");
						
						aux.put("id", String.valueOf(PIRPacket.get_node_tx()));
						aux.put("event", String.valueOf(PIRPacket.get_event()));
											
					    pair.accumulate("value", aux.toString());
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					payload.add(pair);
					
					ProducerId = "arduino/"+PIRPacket.get_node_tx()+"/sensor/pir";
					
					ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
					
					System.out.println("*** Evento de PIR recibido: "+ev.toString());
					
					em.setEvent(ev);
					
					//Update mote status
										
					moteDev.setValueCache(ProducerURI+ProducerId, aux.toString());
									
					if(PIRPacket.get_battery() != 0){
						battery = 1230 * 1024 / PIRPacket.get_battery();
					}
					
					status = setStatusDescription("arduino", battery); 
								
					moteDev.setValueCache(ProducerURI+"arduino/"+PIRPacket.get_node_tx()+"/sensor/status", status);
					
					updateMoteStatus(moteDev);
					
				}
				
			}
			
			//If a proximity packet is received
			else if (micazMessage instanceof ProxMsg){
				
				ProxMsg ProxPacket = (ProxMsg) micazMessage;
						
				if (!duplicateTable.containsKey(ProxPacket.get_node_tx()) || duplicateTable.get(ProxPacket.get_node_tx()) != ProxPacket.get_seqNo()) {
					duplicateTable.put(ProxPacket.get_node_tx(), ProxPacket.get_seqNo());
					
					Event ev;
									
					String ProducerId;

					ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
					JSONObject pair = new JSONObject();
					JSONObject aux = new JSONObject();
					
					//Check if the source device is in the data base
					Device moteDev = checkMoteList("micaz", String.valueOf(ProxPacket.get_node_tx()), 5);
					
					System.out.println(">>> Id Rx: "+ProxPacket.get_node_rx()+", Id Tx:"+ ProxPacket.get_node_tx());
					
					try {
						pair.put("key", "presence");
						pair.put("unit", "binary");
						
						aux.put("beacon", String.valueOf(ProxPacket.get_node_tx()));
						aux.put("mobile", String.valueOf(ProxPacket.get_node_rx()));
								
					    pair.put("value", aux.toString());
					    
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					payload.add(pair);
					
					ProducerId = "micaz/"+ProxPacket.get_node_tx()+"/sensor/proximity";
					
					ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
					
					System.out.println("*** Evento de proximidad recibido: "+ev.toString());
					
					em.setEvent(ev);
					
					//Update mote status
										
					moteDev.setValueCache(ProducerURI+ProducerId, aux.toString());
					
					if(ProxPacket.get_battery() != 0){
						battery = 1230 * 1024 / ProxPacket.get_battery();
					}
					
					status = setStatusDescription("micaz", battery); 
								
					moteDev.setValueCache(ProducerURI+"micaz/"+ProxPacket.get_node_tx()+"/sensor/status", status);
					
					updateMoteStatus(moteDev);
				}
				
			}
			
			//If a carpet packet is received
			else if (micazMessage instanceof MatMsg){
										
				MatMsg MatPacket = (MatMsg) micazMessage;
				
				if (!duplicateTable.containsKey(MatPacket.get_node_tx()) || duplicateTable.get(MatPacket.get_node_tx()) != MatPacket.get_seqNo()) {
					duplicateTable.put(MatPacket.get_node_tx(), MatPacket.get_seqNo());
					
					Event ev;
										
					String ProducerId;

					ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
					JSONObject pair = new JSONObject();
					JSONObject aux = new JSONObject();
					
					//Check if the source device is in the data base
					Device moteDev = checkMoteList("arduino", String.valueOf(MatPacket.get_node_tx()), 3);
					
					try {
						pair.put("key", "presence");
						pair.put("unit", "binary");
						
						aux.put("id", String.valueOf(MatPacket.get_node_tx()));
						aux.put("event", String.valueOf(MatPacket.get_event()));
																					
					    pair.accumulate("value", aux.toString());
					    
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					payload.add(pair);
					
					ProducerId = "arduino/"+MatPacket.get_node_tx()+"/sensor/mat";
					
					ev = new Event(ProducerURI, ProducerId, "1", System.currentTimeMillis(), payload);
					
					System.out.println("*** Evento de alfombrita recibido: "+ev.toString());
					
					em.setEvent(ev);
					
					//Update mote status
										
					moteDev.setValueCache(ProducerURI+ProducerId, aux.toString());
					
					if(MatPacket.get_battery() != 0){
						battery = 1230 * 1024 / MatPacket.get_battery();
					}
					
					status = setStatusDescription("arduino", battery); 
								
					moteDev.setValueCache(ProducerURI+"arduino/"+MatPacket.get_node_tx()+"/sensor/status", status);
									
					updateMoteStatus(moteDev);
					
					
				}
				
			}		
			
			else if (micazMessage instanceof DataPacketMsg) {
				DataPacketMsg locPacket = (DataPacketMsg)micazMessage;
				//System.out.println("Probando antes");
				if (!duplicateTable.containsKey(locPacket.get_source()) || duplicateTable.get(locPacket.get_source()) != locPacket.get_seqNumber()) {
					duplicateTable.put(locPacket.get_source(), locPacket.get_seqNumber());
					
					//System.out.println("*** Recibido paquete Localizaci�n desde mota : "+locPacket.get_source()+ " Destination: "+locPacket.get_uniqueID());
					
					 int l= locPacket.dataLength();
					 // System.out.println("\nPacket received of size " + l);
				//	  if(l==locPacket.DEFAULT_MESSAGE_SIZE)
				//	  {  
									
					 if(!beaconReg.containsKey(locPacket.get_uniqueID()))
					    {
						
					    	Map<Integer, DataBase> mobileDB = new HashMap<Integer, DataBase>();
					    	
					    	beaconReg.put(locPacket.get_uniqueID(), mobileDB);
					   }
					 //System.out.println("Probando......");
					 if(flag == locPacket.get_uniqueID()){
					//	 System.out.println("*** Registro Localization ***");
					   OP.use_message(locPacket, beaconReg);
					 }
					 
			/*		  }else{
						  System.out.println("the packet dimension is " +l);
						  System.out.println(" instead the dataPacketMsg.DEFAULT_MESSAGE_SIZE is "+locPacket.DEFAULT_MESSAGE_SIZE );
					  }*/
					
				}
			 }
		   }
		 }
			 
		);
		
		micazHandler.start();

	}
	
	private void updateMoteStatus(Device moteDev){
		
		try {
				
			moteDev.setLastHeardTime(System.currentTimeMillis());
			//If Micaz exists in DB, then update BD with new device if necessary	
			Utils.setDeviceStatus(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+moteDev.getId(), "awake");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	
	private String setStatusDescription(String name, double battery){
		
		JSONObject st = new JSONObject();

		try {
			st.put("n", ((name == null) ? "" : name));
			st.put("b", battery);
			st.put("c", "n");
			st.put("u", System.currentTimeMillis());
			st.put("e", "n");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return st.toString();
		
	}
	
	private void updateMoteLocalization(Device moteDev, String coordinates){
		
		try {
					
			moteDev.setLastHeardTime(System.currentTimeMillis());
			
			//If Micaz exists in DB, then update BD with new device if necessary	
			Utils.setDeviceStatus(cons.GW_ROOT_URI+"/"+cons.GW_ID+"/"+moteDev.getId(), "awake");
						
			//If there are sensor values to be updated
			if(coordinates != null){
				Utils.setDeviceLocalization(moteDev.getUri(), coordinates);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * 
	 * @param deviceType - Mote type (description)
	 * @param id - Id of the mote
	 * @param moteType - Mote type (code)
	 * @return Device - a new Device or already existing device
	 */
	private Device checkMoteList(String deviceType, String id, int moteType){
		
		String deviceName = new String(deviceType+"/"+id);


		Device device;
		device = DeviceManager.findByName(deviceName);
		
		if (device == null){
			device = addDevicetoBD(deviceType, id, moteType);
		}
		
		return device;
	}
	
	/**
	 * Manage the temperature threshold configuration on motes
	 * @param eventConfig - (0) Remove threshold; (1) Reset; (2) Set thresholds
	 * @param temp - Value of the temperature threshold
	 * @param hum - Value of the humidity threshold
	 */
	public void setEnvThreshold(short eventConfig, double temp, double hum){
		
		EnvEvent configPacket = new EnvEvent();
		  
		  configPacket.set_dst(MoteIF.TOS_BCAST_ADDR); //Boadcast
		  configPacket.set_id(0); //Base address
		  
		  //Set Temperature
		  switch(eventConfig){
		  
		  //Remove all threshold from motes
		  case 0:
			  configPacket.set_type((short)0);
			  configPacket.set_humidity(0);
			  configPacket.set_temperature(0);
			  configPacket.set_longTime(0);
			  configPacket.set_seqNo(seqNoEvn);
			  configPacket.set_shortTime(0);	
			  configPacket.set_single_hop_src(0);
			  
		  break;
		  
		  //Reset
		  case 1:
					
			  
			  configPacket.set_type((short)1);
			  
			  if(temp != 0){
				  
				  System.out.println(">>> Resetea Threshold temperatura "+temp+" en motas");
				  int tempRaw = (int) ( (temp+39.4)*100.0 );
				  configPacket.set_temperature(tempRaw);			  
			  }
			  			  
			  if(hum != 0){				 
				  System.out.println(">>> Resetea Threshold humedad "+hum+" en motas");
				  int humRaw = (int) ((hum-0.022968)/0.0403);				  
				  configPacket.set_humidity(humRaw);				  
			  }
			  
			  configPacket.set_longTime(0);
			  configPacket.set_seqNo(seqNoEvn);
			  configPacket.set_shortTime(0);
			  configPacket.set_single_hop_src(0);
			  
		  break;
		  
		//Set thresholds (temp/hum)
		  case 2:
			  
			  configPacket.set_temperature(0);
			  configPacket.set_humidity(0);
			  configPacket.set_type((short)2);
			  
			  if(temp != 0){
				 
				  int tempRaw = (int) ( (temp+39.4)*100.0 );
				  System.out.println(">>> Pone Threshold temperatura "+tempRaw+" en motas. Temp: "+temp);
				  configPacket.set_temperature(tempRaw);		  
			  }
			  
			  if(hum != 0){	
				  
				  int humRaw = (int) ((hum-0.022968)/0.0403);
				  
				  System.out.println(">>> Pone Threshold humedad "+hum+" en motas. Raw: "+humRaw);
				  configPacket.set_humidity(humRaw);				  
			  }
			  
			  configPacket.set_longTime(0);
			  configPacket.set_seqNo(seqNoEvn);
			  configPacket.set_shortTime(0);
			  configPacket.set_single_hop_src(0);		  
		  
		  break;		 	 
		  
		  }
		  
		  seqNoEvn++;
		  
		  sendPacket(MoteIF.TOS_BCAST_ADDR, configPacket);	
	}
	
	/**
	 * Start a localization process for a mobiles device.
	 * @param nodeId - Node Id of the mobile node. 
	 * @param algorithm - Indicates the algorithm for calculating localization.
	 * @param period - Period time during which localization is performed
	 */
	public synchronized String startLocalization(int nodeId, int algorithm, long period, long end, int average, int eventManagement) 
	 {
		JSONObject message;
		
		if(locProcesses.size() < MAX_LOC_THREADS && !locProcesses.containsKey(nodeId)){
									
			LocationProcess lp = new LocationProcess(nodeId, algorithm, end, average, eventManagement);
			
			Timer t = new Timer();
			
			//Configure timer
			t.scheduleAtFixedRate(lp, 10, average);
			
			locProcesses.put(nodeId, lp);
			
			message = new JSONObject();
			
			try {
				message.put("message", "Localization started");
				message.put("numLocThreads", locProcesses.size());
			} catch (JSONException e) {
				e.printStackTrace();
			}
				
			return message.toString();
	 	}
		
		message = new JSONObject();
		
		try {
			message.put("message", "Number of localization threads exceeded or node id already being localized");
			message.put("numLocThreads", locProcesses.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		return message.toString();
	
	}
	
	public synchronized String stopLocalization(int nodeId){
		
		JSONObject message;
		
		if(locProcesses.containsKey(nodeId)){
			
			message = new JSONObject();
			
			LocationProcess lp = (LocationProcess) locProcesses.get(nodeId);
			
			lp.CancelLocProcess();
			
			locProcesses.remove(nodeId);
			
			message = new JSONObject();
			
			try {
				message.put("message", "Localization stopped for node id "+nodeId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return message.toString();			
			
		}
		
		message = new JSONObject();
		
		try {
			message.put("message", "There is not node with id "+nodeId+" being localizated");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return message.toString();
		
	}
	
	
	/**
	 * 
	 * @param nodeId
	 */
	public synchronized String resetLocSystem(int nodeId){
			
		JSONObject message;
		
		if(locProcesses.containsKey(nodeId)){
			
			ControlLocalizationMsg loc = new ControlLocalizationMsg();
			
		 	seqNoLoc = 0;
		    loc.set_source(0); //Basestation=0 
			loc.set_destination(nodeId); //mobile device
			loc.set_seqNumber(0);
			loc.set_intermediateSource(0);
			loc.set_action(RESET_S_NUM);//reset sequence number
		    sendPacket(nodeId, loc); // Send "a" to mote via this mote-interface 
	//	    System.out.println("\n\nRESET seqNumber is done for node id "+nodeId);
		    
		    message = new JSONObject();
			
			try {
				message.put("message", "RESET sequence number is done for node id "+nodeId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		    
		    
		    return message.toString();
			
		}
		
		message = new JSONObject();
		
		try {
			message.put("message", "Node id "+nodeId+" is not being localizated");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return message.toString();
		
	}
	
	/**
	 * 
	 * @param power
	 */
	public synchronized String powerChangeLocSystem(int power){
		
	   ControlLocalizationMsg pcm = new ControlLocalizationMsg();
		
	   pcm.set_power_value(power);
	   pcm.set_source(0);
		
	   pcm.set_destination(MoteIF.TOS_BCAST_ADDR); //mobile device
	   pcm.set_action(CHANGE_POWER);//reset sequence number
	   sendPacket(MoteIF.TOS_BCAST_ADDR, pcm); // Send "a" to mote via this mote-interface 
	   System.out.println("\n\nPower Level is changed");
	   
	   JSONObject message = new JSONObject();
		
		try {
			message.put("message", "Power Level has been changed");
			message.put("powerLevel", String.valueOf(power));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	   		
	   return message.toString();			
	
	}
		
	private synchronized void sendPacket(int addr, Message msg) {
			  
		  try {
		   mif.send(addr, msg);
		  } catch (IOException e) {
			  System.err.println("Exception thrown when sending packets. Exiting.");
			  System.err.println(e);
			  System.out.println("failed initialization ");
		  }
	}
	
/*
 * Sensor de luz, de momento no lo tenemos
 */
//	private int[] calculateTaos(int VisibleLight, int InfraredLight) {
//		final int CHORD_VAL[] = {0, 16, 49, 115, 247, 511, 1039, 2095};
//		final int STEP_VAL[] = {1, 2, 4, 8, 16, 32, 64, 128};
//		int chordVal, stepVal;
//		int[] lightVal = new int[2];
//		
//		chordVal = (VisibleLight >> 4) & 7;
//		stepVal = VisibleLight & 15;
//		lightVal[0] = CHORD_VAL [chordVal] + stepVal * STEP_VAL[chordVal];
//		chordVal = (InfraredLight >> 4) & 7;
//		stepVal = VisibleLight & 15;
//		lightVal[1] = CHORD_VAL[chordVal] + stepVal * STEP_VAL[chordVal];
//		return lightVal;
//	}
	
	
	private double[] calculateSensirion(int Temperature, int Humidity) {
		double [] converted = new double[2]; 
		
		converted[0] = -39.4 + (0.01 * (double)Temperature);
		//converted[1] = (-2.0468 + 0.0367 * (double)Humidity - 0.0000015955 * Math.pow((double)Humidity, (double )2)) + (converted[0] - 25) * (0.01 + 0.00008 * (double)Humidity);
		converted[1] = ((double) Humidity*0.0403)+0.022968;	
		return converted;
	}
	
	
	class LocationProcess extends TimerTask{
		
		int id;
		int alg;
		int average;
		
		long end;
		long StartTime = 0;
		
		int eventManagement;
		
		boolean started=false;
		
		Event ev;
	
		public LocationProcess(int nodeId, int algorithm, long end, int average, int evMng){
			
			this.id = nodeId;
			this.alg = algorithm;
			this.end = end;
			this.average = average;
			
			this.eventManagement = evMng;
			
			this.StartTime = System.currentTimeMillis();
									
		}
		
		public void CancelLocProcess(){
			
			ControlLocalizationMsg loc = new ControlLocalizationMsg();
			
			  loc.set_source(0); //Basestation=0 
			  loc.set_destination(id); //mobile node
			  loc.set_intermediateSource(0);   
			  loc.set_action(STOP_LOCALIZATION);
			  loc.set_seqNumber(0); 
			  
			  sendPacket(id, loc); 		
			  
			  locProcesses.remove(id);
			
			this.cancel();	
			
		}

	
		@Override
		public void run(){
		
			System.out.println("*** Comienza localizacion nodo id: "+ id);
			
		if((StartTime + end) - System.currentTimeMillis() < 0){
				System.out.println("*** Cancela Proceso Loc");
				//If the task has to be finished, then cancel it
				this.CancelLocProcess();
			
		}else{
			
		  flag = id;
		  
		//Restart beacon register
		  Map<Integer, DataBase> mobileDB = new HashMap<Integer, DataBase>();
	      beaconReg.put(id, mobileDB);
		  
		  if(!started){
			  			  
			  resetLocSystem(id);
			  			 
			  ControlLocalizationMsg loc = new ControlLocalizationMsg();
			
		      loc.set_source(0); //Basestation=0 
			  loc.set_destination(id); //mobile node
			  loc.set_intermediateSource(0);   
			  loc.set_action(START_LOCALIZATION);
			
			//  if(!started){
				  
			  //Send three START_LOCATION packets
			   for(int i=1;i<=3;i++)
			   {	  
					seqNoLoc++;   
					loc.set_seqNumber(seqNoLoc); 
					sendPacket(id, loc);
					
					//flag = 0;
					try 
					{
					 Thread.sleep(300);	 
					}
					catch (InterruptedException exception) 
					{System.out.println("no sleeping time");}
										  
			   }
			   
			//}
			   started = true;
		  }
			   
		   Map<Integer,DataBase> databases = beaconReg.get(id);
		 
		try {
			Thread.sleep(this.average);
		} catch (InterruptedException e1) {
			
			e1.printStackTrace();
		}//LOC_ACCURACY*1000);
		 
		 String stamp="Node:\n";
		 
		 System.out.println("Nodo id: "+id);
		 System.out.println("Database_size:\n"+databases.size());
		 
		 String result = null;

		 if(databases.size() >= 3){
			
			 for(DataBase B: databases.values())
			 {
			  stamp=stamp +B.node_id+ "_";
			 }
			  System.out.println(stamp);
											 
			 switch(alg){
			 					     
			 	case 1: //Circular
			 		result = task1.runner(databases);
		         break;
		     
		    	 case 2: //Hyperbolic
		    		result = task2.runner(databases);
			     break;
			     
			     case 3: //W. Circular
			    	 result = task3.runner(databases);
			     break;
			     
		    	 case 4: //W. Hyperbolic
		    		 result = task4.runner(databases);
			     break;
			     					 
			 }
			 
			 System.out.println("Localizacion nodo: "+id+". Coordenadas: "+result);
			 
			 //Insert localization into the data base
			 updateMoteLocalization(DeviceManager.findByName("micaz/"+id), result);
											
			 try {
				 
				 JSONObject coordinates = new JSONObject(result);
					
				ArrayList<JSONObject> payload = new ArrayList<JSONObject>();
					
				JSONObject coordinate = new JSONObject();
					
				coordinate.put("key", "x");
				coordinate.put("unit", "meter");
				coordinate.put("value", String.valueOf(coordinates.get("x")));
					
				payload.add(coordinate);
					
				coordinate = new JSONObject();
					
				coordinate.put("key", "y");
				coordinate.put("unit", "meter");
				coordinate.put("value", String.valueOf(coordinates.get("y")));
					
				payload.add(coordinate);
																	
			 //Manage the localization information depending on the event management mode
			switch(this.eventManagement){
			
				//Generates Threshold event
				case Constants.THR:
					
					System.out.println("*** Comenzar Identificacion Area ***");
					areaThresholdEvent(coordinates);
								
				break;
				
				//Generates Contract event
				case Constants.CONT:
					
					System.out.println("*** Event CONTRACT Loc x="+coordinates.get("x")+" y="+coordinates.get("y"));						
					//Creates an event and store it into the Contract Event Table 
					ev = new Event(ProducerURI, "micaz/"+id+"/localization", "2", System.currentTimeMillis(), payload);
					em.setEvent(ev);
					
				break;
				
				//Generate both kinds of events
				case Constants.BTH:

					areaThresholdEvent(coordinates);
				
					System.out.println("*** Event CONTRACT Loc x="+coordinates.get("x")+" y="+coordinates.get("y"));
					//Creates an event and store it into the Contract Event Table 
					ev = new Event(ProducerURI, "micaz/"+id+"/localization", "2", System.currentTimeMillis(), payload);
					em.setEvent(ev);
					
				break;
			}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			 
			 
		 }else {
			 
			 System.out.println("Too few RSS levels to calculate localization");					 
		 }
		}
			
			
		 } //run
		
		void areaThresholdEvent(JSONObject coordinates){
			
			ArrayList<Integer> rooms = ad.pointIsInArea2D(coordinates.toString());
			Event ev;
					
			try {
				
			if(rooms.size() > 0){ //There is some area encompassing such coordinate
				
				for(int i=0; i<rooms.size(); i++){
					
					if(rooms.get(i) != previousArea){
						
							System.out.println("*** Event THRESHOLD " +
									"Loc x="+coordinates.get("x")+" y="+coordinates.get("y")+". Area: "+rooms.get(i));
						
									
						JSONObject roomEv = new JSONObject();
										
						roomEv.put("key", "area");
						
						roomEv.put("unit", "integer");
						roomEv.put("value", String.valueOf(rooms.get(i)));
						
						ArrayList<JSONObject> payloadT = new ArrayList<JSONObject>();
						
						payloadT.add(roomEv);
						
						//Creates an event and store it into the Threshold Event Table 
						ev = new Event(ProducerURI, "micaz/"+id+"/localization", "1", System.currentTimeMillis(), payloadT);
						em.setEvent(ev);
						
						previousArea = rooms.get(i);
					}
					
				}
				
			}else if(previousArea != 0){ //There is no area encompassing such coordinate
				
				JSONObject roomEv = new JSONObject();
				
				roomEv.put("key", "area");
				roomEv.put("unit", "integer");
				roomEv.put("value", "0");
				
				ArrayList<JSONObject> payloadT = new ArrayList<JSONObject>();
				
				payloadT.add(roomEv);
				
				//Creates an event and store it into the Threshold Event Table 
				ev = new Event(ProducerURI, "micaz/"+id+"/localization", "1", System.currentTimeMillis(), payloadT);
				em.setEvent(ev);
				
				previousArea = 0;
				
			}
			
		}catch(JSONException e) {
			
			e.printStackTrace();
		}
		
	}
		
	}
	
}
