package es.upm.ssr.grpss.thofu.adquisicion.gw.core;
/*
 * Copyright (c) 2011, Group of Data Processing and Simulation
 * Universidad Politecnica de Madrid 
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
 * Neither the name of the Universidad Politecnica de Madrid nor the names of its contributors
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



import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Driver;

/**
 *
 * @author Ivan Corredor
 */
public class Utils {
	
    private static String baseurl = "";
    
    Connection con = null;
	public static Connection conexion;
	private static Timer t;

    public static String InteractionToString(DeviceForwarder.Type type) {
        String result = null;
        switch (type) {
            case RADIOGRAM:
                result = "radiogram";
                break;
            case RADIOSTREAM:
                result = "radiostream";
                break;
            case TCP6:
                result = "TCP over 6loWPAN";
                break;
            case UDP6:
                result = "UDP over 6loWPAN";
                break;
            case BLUETOOTH:
            	result = "Bluetooth";
            	break;            	
            case ZIGBEE:
            	result = "Zigbee";
            	break;            	
            case IEEE802154:
            	result = "IEEE 802.15.4";
            	break;            	
            case WIFI:
            	result = "Wifi";
            	break;            	
            case REST:
            	result = "REST";
            	break;
        }

        return result;
    }
    
    public static void startRefreshProcess() { 	   
        	
        //Insert new Gw entry if necessary
  		setGateway(Constants.GW_ROOT_URI+"/"+Constants.GW_ID, "123","0");
	   
 	   //Add device from associated GW
 	   
 	   
 	   //Start device list refreshing thread
       DeviceListRefresh deviceExpirer = new DeviceListRefresh();
       deviceExpirer.setPriority(Thread.MIN_PRIORITY);
       deviceExpirer.start();
    	   
    }
    
    public static void startCacheDumping(){
    	
    	t = new Timer();
    	SensorValueDump svd = new SensorValueDump();
		//Configure timer
		t.scheduleAtFixedRate(svd, 0, Constants.DB_DUMP_PERIOD);
    }
    
    public static void stopCacheDumping(){
    	t.cancel();
    }
    
    

    public static String makeTimestamp(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT:00"));
        StringBuffer sb = sdf.format(timeInMillis, new StringBuffer(),
                new FieldPosition(0));
        return sb.toString();
    }

    static String makeDurationEstimate(long durationInMillis) {
        long tmp = durationInMillis / 1000;
        String result = null;

        if (tmp > 0x1e13380) {
            result = (tmp / 0x1e13380) + " yrs";
        } else if (tmp > 0x15180) {
            result = (tmp / 0x15180) + " days";
        } else if (tmp > 3600) {
            result = (tmp / 3600) + " hrs";
        } else if (tmp > 60) {
            result = (tmp / 60) + " mins";
        } else {
            result = tmp + " sec";
        }

        return (result);
    }
    
	
	public static String extractAddress(String uri) {
	        int idx = uri.indexOf("/", 2);

	        if (idx < 0) {
	            idx = uri.length();
	        }

	        return uri.substring(0, idx);
	 }

    public static String getBaseURL() {
        return baseurl;
    }

    public static void setBaseURL(String baseURL) {
        baseurl = baseURL;
    }
    

   
	public static void openDBConnection() throws SQLException {

		DriverManager.registerDriver(new Driver());

		System.out.println("Conectar base datos: "+Constants.BD_URL+":"+Constants.BD_PORT+"/"+Constants.BD_name);
		conexion = DriverManager.getConnection(Constants.BD_URL+":"+Constants.BD_PORT+"/"+Constants.BD_name, Constants.BD_USER, Constants.BD_PASS);

	}

	public static void closeDBConnection() throws SQLException {
		conexion.close();
	}
	
	// Look up for for a Gw
	
	public static boolean isGateway(String URI) throws SQLException{
		
		boolean isGw=false;
		Statement s = conexion.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`gateway` " +
				"where Gateway_URI='"+URI+"';");
		
		if(rs.next()) isGw=true;
		
		s.close();
		rs.close();
		
		return isGw;
	
	}
	
	// Look for a contextual device
	public static String isDevice(String URI, String addr){
		
		String dev_URI = null;
		Statement s;
		try {
			s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs;
			
			if(addr != null){
				rs = s.executeQuery("select Device_URI from `"+Constants.BD_name+"`.`contextual_device` " +
						"where Device_URI LIKE '%"+URI+"/%' and address='"+addr+"';");
			}else{
				
				rs = s.executeQuery("select Device_URI from `"+Constants.BD_name+"`.`contextual_device` " +
						"where Device_URI='"+URI+"';");
			}
			
			if(rs.next()) dev_URI = rs.getString("Device_URI");
			
			s.close();
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("*** Problema SQL comprobando dispositivo presente ***");
		}
				
		return dev_URI;
	
	}
	
	// Look for a contextual sensor
	public static boolean isSensor(String URI){
		
		boolean isSensor=false;
		 
		try {
			
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`sensor` " +
					"where sensor_URI='"+URI+"';");
			
			if(rs.next()) isSensor=true;
			
			s.close();
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("*** Problema obteniendo tipo de sensor ***");
		}
		
		return isSensor;
	
	}
	
	// Look for a contextual actuator
	public static boolean isActuator(String URI) throws SQLException{
		
		boolean isDevice=false;
		Statement s = conexion.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`actuator` " +
				"where actuator_URI='"+URI+"';");
		
		if(rs.next()) isDevice=true;
		
		return isDevice;
	
	}
	
	
	// Return ArrayList with URIs of every Gateway in the DB
	public static ArrayList<String> getGwList(){
		
		Statement s;
		ArrayList<String> gateways = null;
		try {
			
			s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Gateway_URI from `"+Constants.BD_name+"`.`gateway`;");
			
			gateways = new ArrayList<String>();
			
			int i=0;
			
			if(rs.first()){
				
				while(!rs.isAfterLast()){
								
					gateways.add(i, rs.getString(1));
					i++;
					rs.next();
				}
			}
			
								
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return gateways;		
		
	}
	
	// Return ArrayList with URIs of every contextual device in the DB
	public static ArrayList<String> getDeviceList(String gw_URI){
		
		ArrayList<String> devices = null;
		
			try {
				Statement s = conexion.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				Statement s2 = conexion.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`gateway_has_contextual_device` " +
						"where Gateway_Gateway_URI='"+gw_URI+"';");
				
				ResultSet rs2;
				
				devices = new ArrayList<String>();
				int i=0;
				
				if(rs.first()){
					
					while(!rs.isAfterLast()){
						
						rs2 = s2.executeQuery("select * from `"+Constants.BD_name+"`.`contextual_device` " + 
						"where Device_URI='"+rs.getString(3)+"';");
						
						if(rs2.first()){
							devices.add(i, rs2.getString(1));
							i++;
						}	
						
						rs.next();
						
						rs2.close();
					}
					
				}
				
				rs.close();
				s.close();
				s2.close();
				
			} catch (SQLException e) {
				System.out.println("*** Problema obteniendo lista de dispositivos ***");
			}
			
			
		
			return devices;		
			
	}
	
	// Return ArrayList with URIs of every contextual active device in the DB (only from external GWs)
	public static ArrayList<Device> getActiveDeviceList(String gw_URI){
					
			ArrayList<Device> devices = null;
			Device device = null;
			
			try {
				Statement s = conexion.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
				ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`contextual_device` " + 
						"where status='awake' and Device_URI LIKE '%"+gw_URI+"/%';");			
							
				devices = new ArrayList<Device>();
				int i=0;
				
				if(rs.first()){
					String subfix;
					boolean local;
					if(gw_URI.compareTo(Constants.GW_ROOT_URI+"/"+Constants.GW_ID) == 0){
						subfix = ""; //It is a local device
						local = true;
					}else{
						subfix = "-e";
						local = false;
					}
					
					while(!rs.isAfterLast()){
												
							
							
							device = new Device(
									rs.getString("Device_URI").substring(gw_URI.length()+1)+subfix,
									DeviceForwarder.Type.valueOf(rs.getString("communication_iface")),
									rs.getString("sensor_type"),
									Boolean.valueOf(rs.getString("compression")),
									local,
									rs.getString("address"),
									rs.getString("Device_URI"),
									Integer.valueOf(rs.getString("life_time")));
									
							devices.add(i, device);
							i++;
												
						rs.next();			
					}
					
				}
				
				rs.close();
				s.close();
				
				
			} catch (SQLException e) {
				System.out.println("*** Problema obteniendo lista de dispositivos activos ***");
			}
									
			return devices;					
			
	}
	
	//Return number of Device matching a name
	public static int getNumDevices(String device_name){
				
		try {
			
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`contextual_device` " + 
					"where Device_URI LIKE '%"+Constants.GW_ID+"/"+device_name+"/%';");
			
			s.close();
			
			if(rs.next()){
				//Go to last row
				rs.last();	
				rs.close();
				return rs.getRow();
			}else{
				return 0;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;		
	}
	
	// Return status of a device registered into the DB
	public static String getStatus(String device_URI){
				
		String result = null;
		try {
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = s.executeQuery("select status from `"+Constants.BD_name+"`.`contextual_device` " + 
					"where Device_URI='"+device_URI+"';");
			
			if(rs.next()){
				result = rs.getString("status");				
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			System.out.println("*** Problema obteniendo status de dispositivo ***");
		}
			
		return result;	
	}
	
	// Return ArrayList with descriptions of every sensors in the DB
	public static ArrayList<String> getSensorList(String device_URI){
			 
		ArrayList<String> sensors = null;
		try {
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`sensor` " +
					"where Contextual_Device_Device_URI='"+device_URI+"';");
			
			sensors = new ArrayList<String>();
			
			int i=0;
			while(rs.next()){
				
				sensors.add(i, rs.getString(1));
				i++;
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return sensors;		
		
	}
	
	
	
	// Return ArrayList with descriptions of every sensors in the DB
	public static ArrayList<String> getActuatorList(String device_URI){
			
		ArrayList<String> actuator = null;
		
		try {
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`actuator` " +
					"where Contextual_Device_Device_URI='"+device_URI+"';");
			
			actuator = new ArrayList<String>();
			
			int i=0;
			while(rs.next()){
				
				actuator.add(i, rs.getString(1));
				i++;
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return actuator;	
		
	}
	
	// Return Gateway Description
	public static String getDeviceDescription(String device_URI){
				
		try {
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Description from `"+Constants.BD_name+"`.`contextual_device` " +
					"where Device_URI='"+device_URI+"';");
					
					if(rs.first()){
						return rs.getString(1);
					}
					
			rs.close();
			s.close();
					
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return null;	
		
	}
	
	// Return Sensor Description
	public static String getSensorDescription(String sensor_URI){
				
		try {
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Description from `"+Constants.BD_name+"`.`sensor` " +
					"where sensor_URI='"+sensor_URI+"';");
					
			if(rs.first()){
				return rs.getString(1);
			}
			
			rs.close();
			s.close();
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return null;
		
	}
	
	// Return Actuator Description
	public static String getActuatorDescription(String actuator_URI){
				 
		try {
			
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Description from `"+Constants.BD_name+"`.`actuator` " +
					"where actuator_URI='"+actuator_URI+"';");
					
			if(rs.first()){
				return rs.getString(1);
			}
			
			rs.close();
			s.close();
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	// Insert a new Gateway
	public static void setGateway(String Gw_URI, String coordinates, String description){
		
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Gateway_URI from `"+Constants.BD_name+"`.`gateway` " +
					" where Gateway_URI = '"+Gw_URI+"';");
			
			if(!rs.next()){
				
				s.execute("SET FOREIGN_KEY_CHECKS=0");
				//Insert a new Gateway
				s.execute("insert into `"+Constants.BD_name+"`.`gateway`" +
						" (Gateway_URI, coordinates_coordinates_id, Description) " +
						"values ('"+Gw_URI+"','"+coordinates+"','"+description+"');");
				
			} else if (rs.getString(1).equalsIgnoreCase(Gw_URI)) {
				
				s.execute("SET FOREIGN_KEY_CHECKS=0");
				//Update entry
				s.execute("update `"+Constants.BD_name+"`.`gateway` set "+
				"coordinates_coordinates_id='"+coordinates+"', " +
				"Description='"+description+"' "+		
			    " where Gateway_URI='"+Gw_URI+"';");

			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			System.out.println("*** Problema insertando informacion de Gateway ***");
		}	
		
	}
	
	
	// Insert a new contextual device into the BD
	
	public static void setContextualDevice(String URI, String des, String fw, String sensor_type, String comm_iface,
			String last_heard_time, int life_time, String address, boolean comp, String status, String coordinates){
		
	//	boolean isnew=true;
	
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			Statement s2 = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select Device_URI from `"+Constants.BD_name+"`.`contextual_device` " +
					" where Device_URI = '"+URI+"';");
			
			if(!rs.next()){
				
				System.out.println("*** Set new contextual device ***");
					
				//Insert a new contextual device
				s.execute("insert into `"+Constants.BD_name+"`.`contextual_device`" +
						" (Device_URI, Description, firmware_version, sensor_type, communication_iface, last_heard_time, " +
						"life_time,  address, compression, status, coordinates) " +
						"values ('"+URI+"','"+des+"','"+fw+"','"+sensor_type+"','"+comm_iface+"','"+last_heard_time+"','"+life_time+
						"','"+address+"',"+comp+",'"+status+"', '"+coordinates+"');");
				
				//Creates link between Gw and contextual device
				s2.execute("insert into `"+Constants.BD_name+"`.`gateway_has_contextual_device` " +
				" (Gateway_Gateway_URI, Contextual_Device_device_URI) "+
				"values ('"+Constants.GW_ROOT_URI+"/"+Constants.GW_ID+"' , '"+URI+"');");	
									
			} else if(rs.getString(1).equalsIgnoreCase(URI)) {
				
				System.out.println("*** Updating contextual device URI:"+URI+" ***");
				//Update entry
				s.execute("update `"+Constants.BD_name+"`.`contextual_device` set "+
				"Description='"+des+"', firmware_version='"+fw+"', " +"sensor_type='"+sensor_type+"', " +
						"communication_iface='"+comm_iface+"', last_heard_time='"+last_heard_time+"', life_time='"+life_time+
						"', address='"+address+"', compression="+comp+", status='"+status+"' where Device_URI='"+URI+"';");
				
			//	isnew=false;
				
			}
			
			//Only for Sun SPOT boards: Add sensors and actuator
		/*	if((des.indexOf("board=SunSpot")!=-1) && isnew){
				
				//Add sensor list
				setSensor(URI+"/sensor/temp", "sensor=temp",URI);
				setSensor(URI+"/sensor/light", "sensor=light",URI);
				setSensor(URI+"/sensor/accl", "sensor=accl",URI);
				
				//To get status
				setSensor(URI+"/sensor/status", "sensor=status",URI);
							
				//Add actuator list
				setActuator(URI+"/actuator/leds","actuator=leds",URI); 
							
			} else if((des.indexOf("board=Micaz-MDA300")!=-1) && isnew){
				
				//Add sensor list
				setSensor(URI+"/sensor/temp", "sensor=temp",URI);
				setSensor(URI+"/sensor/hum", "sensor=hum",URI);
				
				//Add control sensor 
				setSensor(URI+"/sensor/status", "sensor=status",URI);
						
				//Add actuator list
				setActuator(URI+"/actuator/leds","actuator=leds",URI); 
				
			} else if((des.indexOf("board=Micaz-MDA100")!=-1) && isnew){
				
				//Add sensor list
				setSensor(URI+"/sensor/temp", "sensor=temp",URI);
				setSensor(URI+"/sensor/light", "sensor=light",URI);
				
				//Add control sensor 
				setSensor(URI+"/sensor/status", "sensor=status",URI);
						
				//Add actuator list
				setActuator(URI+"/actuator/leds","actuator=leds",URI);
				
			}else if((des.indexOf("board=Zephyr")!=-1) && isnew){
				
				//Add biometric sensors
				setSensor(URI+"/sensor/posture","sensor=posture",URI);
				setSensor(URI+"/sensor/heart","sensor=heart rate",URI);
				setSensor(URI+"/sensor/bodytemp","sensor=body temperature",URI);
				
				//Add control sensors
				setSensor(URI+"/sensor/bat", "sensor=battery level",URI);
				
			}*/
			
			rs.close();
			s.close();
			s2.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("*** Problema insertando Contextual Device  ***");
		}
	
	}
	
	//Update status of a devices associated to the GW
	public static void setDeviceStatus(String deviceURI, String status){
				
		try{
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);			
						
			ResultSet rs = s.executeQuery("select Device_URI from `"+Constants.BD_name+"`.`contextual_device` " +
					" where Device_URI = '"+deviceURI+"';");
			
			if (rs.next() && rs.getString("Device_URI").compareTo(deviceURI)==0) {
				
				//Update entry
				s.execute("update `"+Constants.BD_name+"`.`contextual_device` set "+
				"status='"+status+"' where Device_URI='"+deviceURI+"';");

			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//Update status of a devices associated to the GW
	public static void setDeviceLocalization(String deviceURI, String localization){
					
			try{
				Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);			
							
				ResultSet rs = s.executeQuery("select Device_URI from `"+Constants.BD_name+"`.`contextual_device` " +
						" where Device_URI = '"+deviceURI+"';");
				
				if (rs.next() && rs.getString("Device_URI").compareTo(deviceURI)==0) {
					
					//Update entry
					s.execute("update `"+Constants.BD_name+"`.`contextual_device` set "+
					"coordinates='"+localization+"' where Device_URI='"+deviceURI+"';");

				}
				
				rs.close();
				s.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
	}
	
	// Insert a sensor associated to a contextual device
	public static void setSensor(String sensorURI,  String deviceURI, String key, String unit, String dataType, String des){
		
		try {
	    Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		
		Statement s2 = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		ResultSet rs = s.executeQuery("select sensor_URI from `"+Constants.BD_name+"`.`sensor` " +
				" where sensor_URI = '"+sensorURI+"';");
		
		if(!rs.first()){
			
			//Insert a new contextual device
			s2.execute("insert into `"+Constants.BD_name+"`.`sensor`" +
					" (sensor_URI, Contextual_Device_Device_URI, Description, DataType, Concept, Unit)"+
					" values('"+sensorURI+"','"+deviceURI+"','"+des+"','"+dataType+"','"+key+"','"+unit+"');");
			
		} else if(rs.getString(1).equalsIgnoreCase(sensorURI)) {
			
			//Update entry
			s2.execute("update `"+Constants.BD_name+"`.`sensor` set description='"+des+"', " 
			+"DataType='"+dataType+"', Concept='"+key+"', Unit='"+unit+"'"+
					"where sensor_URI='"+sensorURI+"';");
		
					
		}	
		
		rs.close();
		s.close();
		s2.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
	
	// Insert a actuator associated to a contextual device
	public static void setActuator(String actuatorURI, String des, String deviceURI){
		
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select actuator_URI from `"+Constants.BD_name+"`.`actuator` " +
					" where actuator_URI = '"+actuatorURI+"';");
			
			if(!rs.first()){
				
				//Insert a new contextual device
				//Insert temperature sensor
				s.execute("insert into `"+Constants.BD_name+"`.`actuator`" +
						" (actuator_URI, description, Contextual_Device_Device_URI)"+
						" values('"+actuatorURI+"','"+des+"','"+deviceURI+"');");
				
			} else if(rs.getString(1).equalsIgnoreCase(actuatorURI)) {
				
				//Update entry
				s.execute("update `"+Constants.BD_name+"`.`actuator` set description='"+des+"' " +
						"where actuator_URI='"+actuatorURI+"';");
				
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
			
	}
	
	// Insert a sensor value for a specific sensor of a contextual device
	
	public static void setSensorReading(String sensorURI, String value, String timestamp){
	
		try {
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			s.execute("insert into `"+Constants.BD_name+"`.`sensor_reading`" +
					" (Sensor_sensor_URI, reading, timestamp) values('"+sensorURI+"', '"+value+"', '"+timestamp+"');");						
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	// Insert a set of sensor values related to unspecific kinds of sensors
	
	public static void setSensorsReading(ConcurrentHashMap<String, Hashtable<Long, String>> values){
		
		
		if(values.size() > 0){
			try {
				Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				//Iterator for sensor devices list
				Enumeration<String> keysDevice = values.keys();
				
				//Iterator for values
				Enumeration<Long> keysValues;
				Hashtable<Long, String> cacheValues;
				
				String SensorURI;
				Long timeStamp;
							
				for(int i=0; i < values.size(); i++){
					
					SensorURI = keysDevice.nextElement();
					
					cacheValues = (Hashtable<Long, String>) values.get(SensorURI);
					
					keysValues = cacheValues.keys();
								
					while(keysValues.hasMoreElements()){
						
						timeStamp = keysValues.nextElement();  
															
						s.execute("insert into `"+Constants.BD_name+"`.`sensor_reading`" +
								" (Sensor_sensor_URI, reading, timestamp, milliseconds) values('"+SensorURI+
								"', '"+((String) cacheValues.get(timeStamp))+
								"', '"+Utils.makeTimestamp(timeStamp)+"', '"+timeStamp+"');");
																
					}						
				}
				
				s.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}					
		
			
			
		}
		
	}
	
	// Insert a actuator status for a specific actuator of a contextual device
	
	public static void setActuatorStatus(String actuatorURI, String value){
				 
		try {
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			s.execute("insert into `"+Constants.BD_name+"`.`action` (Actuator_actuator_URI, actuator_status, timestamp, milliseconds) " +
					"values('"+actuatorURI+"', '"+value+"','"+Utils.makeTimestamp(System.currentTimeMillis())
					+"', '"+System.currentTimeMillis()+"');");
			
			s.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}		
			
	}
	
	
	public static void setAreaInformation(String idZone, String limits, String tags){
		
				
		try {
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet rs = s.executeQuery("select idarea from `"+Constants.BD_name+"`.`area` " +
					" where idarea = '"+idZone+"';");
			
			if(!rs.next()){
			
				s.execute("insert into `"+Constants.BD_name+"`.`area` (idarea, area_limit, tags) " +
						"values('"+idZone+"', '"+limits+"','"+tags+"');");
			}else{
				
				//Update entry
				s.execute("update `"+Constants.BD_name+"`.`area` set "+
				"idarea='"+idZone+"', " +
				"area_limit='"+limits+"', "+
				"tags='"+tags+"' "+
			    " where idarea='"+idZone+"';");				
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
/*
 * Getters
 */
	
	
	// Get a sensor value for a specific sensor of a contextual device
	
	public static String getSensorReading(String sensorURI, long refreshTime ){
		
		 
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			
			
			ResultSet rs = null;
			
			
			if(refreshTime > 0){
		
					rs = s.executeQuery("select reading from `"+Constants.BD_name+"`.`sensor_reading`"+
					" where Sensor_sensor_URI='"+sensorURI+"' and TIMESTAMPDIFF(second, timestamp, CURRENT_TIMESTAMP) <= "+refreshTime+";");

			}else{
				
				rs = s.executeQuery("select reading from `"+Constants.BD_name+"`.`sensor_reading`"+
						" where Sensor_sensor_URI='"+sensorURI+"' ORDER BY timestamp DESC LIMIT 1;");						
			}
				
			
			if(rs!=null){
				
				//ArrayList reading = new ArrayList<String>();
				
				if (rs.first()){
				//	reading.add(rs.getString(1));
				//	reading.add(rs.getString(3));
					return(rs.getString(1));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	
	}
	
	public static Hashtable<String, String> getSensorReading2(String sensorURI, long refreshTime ){
		
		 
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			
			
			ResultSet rs = null;
			
			
			if(refreshTime > 0){
		
					rs = s.executeQuery("select reading,milliseconds from `"+Constants.BD_name+"`.`sensor_reading`"+
					" where Sensor_sensor_URI='"+sensorURI+"' and TIMESTAMPDIFF(second, timestamp, CURRENT_TIMESTAMP) <= "+refreshTime+";");

			}else{
				
				rs = s.executeQuery("select reading,milliseconds from `"+Constants.BD_name+"`.`sensor_reading`"+
						" where Sensor_sensor_URI='"+sensorURI+"' ORDER BY timestamp DESC LIMIT 1;");						
			}
				
			
			if(rs!=null){
				
				Hashtable<String, String> reading = new Hashtable<String, String>();
				
				if (rs.first()){
					
					do{
					
						reading.put(rs.getString(2), rs.getString(1));
											
					}while(rs.next());
					
				}
				
				return(reading);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	
	}
	

	
	// Get a sensor value for a specific sensor of a contextual device
	
		public static String getDeviceLoc(String devURI, long refreshTime ) {
					
			try {
				
				Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
				ResultSet rs = s.executeQuery("select coordinates from `"+Constants.BD_name+"`.`contextual_device`"+
						" where Device_URI='"+devURI+"';");
				
				//TODO Acceso a historial de localizaciones
				/*if(refreshTime > 0){
					
					rs = s.executeQuery("select reading from `"+Constants.BD_name+"`.`contextual_device`"+
					" where Device_URI='"+devURI+"' and TIMESTAMPDIFF(second, timestamp, CURRENT_TIMESTAMP) <= "+refreshTime+";");

				}else{
					
					rs = s.executeQuery("select reading from `"+Constants.BD_name+"`.`contextual_device`"+
							" where Device_URI='"+devURI+"' ORDER BY timestamp DESC LIMIT 1;");						
				}*/
					
				
				if(rs!=null){
					
					if (rs.first()){
						return(rs.getString(1));
					}
				}
				
				rs.close();
				s.close();
			
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			
			return null;
		
		}
	
	
	
	// Get a actuator status for a specific actuator of a contextual device
	
	public static String getActuatorStatus(String actuatorURI, String timestamp){
		
		 
		try {
			
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = null;
			
			if(!timestamp.equalsIgnoreCase("0")){
				
				rs = s.executeQuery("select actuator_status from `"+Constants.BD_name+"`.`action`"+
				" where Actuator_actuator_URI='"+actuatorURI+"' and timestamp >=CURRENT_TIMESTAMP;");
				
			}else{
				
				rs = s.executeQuery("select actuator_status from `"+Constants.BD_name+"`.`action`"+
						" where Actuator_actuator_URI='"+actuatorURI+"' ORDER BY timestamp DESC LIMIT 1;");						
			}
					
			
					
			if(rs!=null){
										
				if (rs.next()){
					return(rs.getString(1));
				}
			}
			
			rs.close();
			s.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		return null;
	
	}
	
	public static JSONArray getAreaList(){
		
		JSONArray areas = null;
		
		JSONObject aux = null;
		JSONArray aux2 = null;
		
		try {
			Statement s = conexion.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				
			ResultSet rs = s.executeQuery("select * from `"+Constants.BD_name+"`.`area`;");
			
			areas = new JSONArray();
			
			int i=0;
			
			if(rs.first()){
				
				while(!rs.isAfterLast()){
					
					aux = new JSONObject();
					
									
					try {
						
						aux2 = new JSONArray(rs.getString("area_limit"));
						aux.put("areaId", rs.getString("idarea"));
						aux.put("limits", aux2);
						aux2 = new JSONArray(rs.getString("tags"));
						aux.put("semanticTags", aux2);
						
						areas.put(i, aux);
						
												
					} catch (JSONException e) {
						e.printStackTrace();
					}
												
					i++;
										
					rs.next();					
					
				}
				
			}
			
			rs.close();
			s.close();
									
		} catch (SQLException e) {
			System.out.println("*** Problema obteniendo lista de areas ***");
		}
		
		return areas;
	}
	
/*
 * Deleters
 */
	
	public static void removeArea(String areaId){
		
		try {
			Statement s = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			s.execute("delete from `"+Constants.BD_name+"`.`area` where idzone='"+areaId+"');");
			
			s.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}   
    
}
