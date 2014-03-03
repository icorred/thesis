
package driver_micaz_osgi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.EventManager;
import es.upm.ssr.grpss.thofu.adquisicion.gw.eventmanagement.ThresholdManager;


/**
 *
 * @author Ivan Corredor
 */
public class Constants {
	//Expiration time for registered devices
    public static final int DEFAULT_REG_LIFETIME = 30;
    
    public static final int MICAZ_BATTERY_LEVEL = 3000;
    
    //Operator types
    public enum operator { MORE, LESS, EQUAL };
    
    //Event types
    public static final String THRESHOLD = "1";
    public static final String CONTRACT = "2";
    public static final String BOTH = "3";
    public static final String DATA_BASE = "4";
    
    public static final int THR = 1;
    public static final int CONT = 2;
    public static final int BTH = 3;
    public static final int DB = 4;
    
    
    //Limits for Consumers and Subscriptions
    public static final int MAX_CONSUMERS = 1000; 
	public static final int MAX_SUBS_PER_CONS = 200 ;
	
	//Limits for sensor value buffer
	public static final int MAX_BUFFER_ENTRIES = 2000;
	public static final int DB_DUMP_PERIOD = 20000; //ms
	
	//Period for threshold subscription checker
	public static final int TH_CHECKER = 10000; //ms
	
	//Drivers
	 public enum Driver { MICAZ, SUNSPOT, BIOHARNESS, SUPERVISOR };
    
    //Sensor states
    public enum SensorState { RIGTH, LEFT, UP, DOWN, PUSH };
        
    //Sensorial retrieval types
    public enum SensorRet { DB, REST, CACHE, LOCAL_DRIVER };    
    
    //Ports
    public static final int UDP6_SVCPORT = 8888;
    public static final int TCP6_SVCPORT = 8888;
    public static final int TCP_SVCPORT = 8886;
    public static final int UDP6_ADVPORT = 8889;
    public static final int XSERVE_PORT = 9002;
    public static int BD_PORT = 3306;
    public static int RESTLET_PORT;// = 6008;
            
    // Data Base IP address have to be instanced 
    public static String BD_IP="localhost";
    
    //user and password for data Base connection. They have to be instanced.
    public static String BD_USER="thofu";
    public static String BD_PASS="thofu";
    
    // IP Addresses
    public static final String XSERVE_IP= "localhost";
            
 // URI of the root directory.  
    public static String GW_ID = "gw-d205";
    
    public static String GW_IP = "127.0.0.1";
    
    public static String GW_ROOT_URI ="http://:"+RESTLET_PORT;
    
 //   public static String GW_ROOT_URI; //="http://172.16.5.34:"+RESTLET_PORT;
    		
    public static final String ROOT_URI = "file:///home/thofu/workspace/Plataforma_v2/htdocs";
    
    public static String BD_URL = "jdbc:mysql://";
    
    public static final String BD_name = "thofu";
    
    private static Constants cons;
    
    public static Constants getInstance(){
    	
    	if(cons == null){
    		cons = new Constants();
			Constants.configServer();
		}
		
		return cons;
    }
    
    public static void initConstants(){
       		
    	      try {
    	    	  
    	    	  //Get Wifi interface (Is it always the same interface?)
    	         NetworkInterface ni = NetworkInterface.getByName("net3");
    	         
    	         List<InterfaceAddress> ip = ni.getInterfaceAddresses();
    	             	         
    	         GW_ROOT_URI = "http://"+ip.get(0).getAddress().getHostAddress()+":"+RESTLET_PORT;
    	         
    	         System.out.println("*** Servidor iniciado.URI raiz: "+GW_ROOT_URI);
    	         
    	       
    	 /*    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
    	     
    	         for (NetworkInterface netint : Collections.list(nets)){
    	        	 System.out.printf("Display name: %s\n", netint.getDisplayName());
    	             System.out.printf("Name: %s\n", netint.getName());
    	         		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
    	         		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
    	         			System.out.printf("InetAddress: %s\n", inetAddress);
    	         		}
    	         		System.out.printf("\n");
    	     }*/

    	    
    		}  catch (Exception e) {
    	    	  GW_ROOT_URI = "http://localhost:"+RESTLET_PORT;
    	         e.printStackTrace();
    	      }
    	   
    }
    
    public static void configServer(){
    	
    	try{
            // Abrimos el archivo
            FileInputStream fstream = new FileInputStream("/home/gpds/Desktop/ExpLab/config_plataforma");
            // Creamos el objeto de entrada
            DataInputStream entrada = new DataInputStream(fstream);
            // Creamos el Buffer de Lectura
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
            String strLinea;
            StringTokenizer st;
            
            String stringAux;
            // Leer el archivo linea por linea
            while ((strLinea = buffer.readLine()) != null)   {
                                
                if(!strLinea.startsWith("#") && (strLinea.length() > 0)){
                	
                	st = new StringTokenizer(strLinea, "=");
                	
                	stringAux = st.nextToken();                	
                	
                	if(stringAux.contains("SERVER_IP")){                		                                      
                		GW_IP = st.nextToken();            		
                	}else if(stringAux.contains("SERVER_PORT")){
                		RESTLET_PORT = Integer.valueOf(st.nextToken());
                	}else if(stringAux.contains("DB_IP")){
                		BD_IP = st.nextToken();
                		BD_URL = BD_URL+ BD_IP;
                	}else if(stringAux.contains("DB_PORT")){
                		BD_PORT = Integer.valueOf(st.nextToken());
                	}else if(stringAux.contains("DB_USER")){
                		BD_USER = st.nextToken();
                	}else if(stringAux.contains("DB_PASS")){
                		BD_PASS = st.nextToken();
                	}         	
                }
            }
                       
            // Cerramos el archivo
            entrada.close();
            
            GW_ROOT_URI = "http://"+GW_IP+":"+RESTLET_PORT;
            
            cons = new Constants();
            
        }catch (Exception e){ //Catch de excepciones
            System.err.println("Ocurrio un error: " + e.getMessage());
        }
    	
    }
    
    public static void configDBCon(){
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	    	
    	boolean answer = false;
    	    	
    	System.out.println("**** CONFIGURACION INICIAL DE LA PLATAFORMA DE ADQUISICION. GPDS 2012 ****");
    	
    	while(!answer){
    		
    		//Is the MySQL server local to gateway?
	    	System.out.print("->Inserte la direccion IP asignada al servidor de la Plataforma: ");
       		    		    	
	    	try {
	    		
	    		GW_IP = br.readLine();
				
				InetAddress.getByName(GW_IP);
		  		  	    	
				answer = true;
				
			} catch (IOException e) {
				System.out.println("Dirección IP no válida. Por favor, vuelvalo a intentar.");
				answer = false;
			}   	    	
	    	
    	}
    	
    	answer = false;
    	
    	while(!answer){
        	
    		//Get Data Base server port
    		System.out.print("->Introduzca el puerto del servidor de la Plataforma (por defecto 6008): ");
    	    		
			try {
				
		    	String port = br.readLine();
				
				if(port.equalsIgnoreCase("")){
	    			
	    			//Set default port
	    			answer = true;
	    			
	    		} else if((Integer.valueOf(port) > 0) && (Integer.valueOf(port) < 65535)){
	    			
	    			//Set new port
	    			RESTLET_PORT = Integer.valueOf(port);
	    				    				    			
	    			answer = true;
	    			
	    		} else {
	    			System.out.print("*** Valor de puerto incorrecto ***");
	    		}
				
				GW_ROOT_URI ="http://"+GW_IP+":"+RESTLET_PORT;
				BD_URL = "jdbc:mysql://"+BD_IP;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
    	}
    	
    	answer = false;
            	    	
    	while(!answer){
    		
	    	//Is the MySQL server local to gateway?
	    	System.out.print("->El servidor de base de datos es local al Gw? (S/N) ");
       	
	    	
	    	try {
	    		
				String loc = br.readLine();
				
				if(loc.equalsIgnoreCase("N")){
		    		
		    		//Get the DB IP address
		        	System.out.print("->Introduzca la dirección IP del servidor MySQL: ");
		        	
		         	BD_IP = br.readLine();
		        	
		         	BD_URL = "jdbc:mysql://"+BD_IP;
		         	
		        	answer = true;
		    		
		    	} else if(loc.equalsIgnoreCase("S")) {
		    		
		    		//It is local to the gateway
		    		BD_IP = "localhost";
		    		
		    		BD_URL = "jdbc:mysql://"+BD_IP;
		    		
		    		answer = true;
		    		
		    	} else {
		    		
		    		System.out.println("*** Respuesta incorrecta ***");	    	
		    	}
				
			} catch (IOException e) {
				e.printStackTrace();
			}	    	
    	}   	
    	
    	answer = false;
        	
    	while(!answer){
    	
    		//Get Data Base server port
    		System.out.print("->Introduzca el puerto del servidor MySQL (por defecto 3306): ");
    	    		
			try {
				
		    	String port = br.readLine();
				
				if(port.equalsIgnoreCase("")){
	    			
	    			//Set default port
	    			answer = true;
	    			
	    		} else if((Integer.valueOf(port) > 0) && (Integer.valueOf(port) < 65535)){
	    			
	    			//Set new port
	    			BD_PORT = Integer.valueOf(port);
	    			answer = true;
	    			
	    		} else {
	    			System.out.print("*** Valor de puerto incorrecto ***");
	    		}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
    	}
    	
    	answer = false;
    	
    	while(!answer){
    		
    		//User of Data Base
    		System.out.print("->Introduzca el usuario de la conexion de la Base de Datos (por defecto \"thofu\"): ");
    		
    		try {
				
		    	String user = br.readLine();
				
				if(user.equalsIgnoreCase("")){
	    			
	    			//Set default user
	    			answer = true;
	    			
	    		} else {
	    			
	    			//Set new user
	    			BD_USER = user;
	    			answer = true;
	    			
	    		} 
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
    		
    	}
    	
    	answer = false;
    	
    	while(!answer){
    		
    		//User of Data Base
    		System.out.print("->Introduzca el password de la conexion de la Base de Datos (por defecto \"thofu\"): ");
    		
    		try {
				
		    	String user = br.readLine();
				
				if(user.equalsIgnoreCase("")){
	    			
	    			//Set default pass
	    			answer = true;
	    			
	    		} else {
	    			
	    			//Set new pass
	    			BD_PASS = user;
	    			answer = true;
	    			
	    		} 
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
    		
    	}
    	
    		
    	    	
    }

	
            
}