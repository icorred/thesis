package driver_micaz_osgi;
import java.util.*;

import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.Coordinate;
import es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization.DataBase;

import net.tinyos.message.Message;

public class organize_packets 
{

	//TODO A y N variables del modelo de canal. (Podr�an variar, tenerlo en cuenta en interfaz).
 //double A=-63.22,N=2.288; 
 double A=-58,N=2.3; 
 int MAX_beacon_node=99;
 public Coordinate coord= new Coordinate( MAX_beacon_node);	
	
 public organize_packets ()
 {}
 
 public void use_message (Message message, Map<Integer, Map<Integer, DataBase>> beaconReg )
 {
  DataPacketMsg msg = (DataPacketMsg)message;
  int Tx_node=msg.get_intermediateSource();
  int S_Num=msg.get_seqNumber();
  int mobile_RSSI;
  String stamp=null;
  stamp="\nTransmitting node number " + Tx_node;
  System.out.println(stamp);
  
  stamp = null;
  if(msg.get_source()!=0) //check against fake packets
  {
   int source=msg.get_source();
   int uniqueId=msg.get_uniqueID();
  
   stamp="Source=" + msg.get_source();
   stamp=stamp + " S_Num=" + S_Num;
   stamp=stamp + " Parent=" + msg.get_nextHop(); 
   stamp=stamp + " uniqueID=" + msg.get_uniqueID();
   stamp=stamp + " hopCount=" + msg.get_hopCount();
   stamp=stamp + " RSSI mobile=" + msg.getElement_data(0);
   stamp=stamp + " mobile power level=" + msg.get_power_level();
   stamp=stamp + " path=" + msg.get_path();
   System.out.println(stamp);
  
   if(msg.get_uniqueID() > 99)
   {
    mobile_RSSI=msg.getElement_data(0);
    
       	
     Map<Integer, DataBase> databases = beaconReg.get(uniqueId);
     
     if(databases.containsKey(source)){
    	 
    	 DataBase d = databases.get(source);
         if(databases.get(source)==null){
        	 System.out.println("database d is null");
         }
    	 System.out.println("Old S_Num: " + d.sequence_number + " new S_Num: " + S_Num+ " para node id: "+uniqueId);
    	 
	     if(S_Num>d.sequence_number)
	     {
	      //System.out.println("Old S_Num:" + d.sequence_number + " new S_Num:" + S_Num);
	      System.out.println("entra update distance");
	      System.out.println("d info"+d.node_id);
	      System.out.println(d.get_dis());
	      d.updateDistance(mobile_RSSI, A, N, S_Num); 
	      System.out.println(d.get_dis());
	      System.out.println("sale update distance");
	      //System.out.println("Updated DB referred to node: " + source+ "for node mobile" +uniqueId+" RSSI "+mobile_RSSI);
	      
	      databases.put(source, d);
	      beaconReg.put(uniqueId, databases);
	      
	     }
	     
	    }else{
	    	
	    		//TODO obtener datos de localizaci�n de las balizas a trav�s de la base de datos
	   	      double Tx_X=coord.get_x(source);
		      double Tx_Y=coord.get_y(source);
		      //System.out.println("Coordinate of node: "+source+"\nare: "+ Tx_X+" "+ Tx_Y);
		      DataBase d = new DataBase(source,mobile_RSSI,Tx_X,Tx_Y,S_Num);
		      System.out.println("entra computeDistance");
		      d.computeDistance(A,N);
		      databases.put(source, d);
		      beaconReg.put(uniqueId, databases);
		      //System.out.println("New DB entry: node " + source);
		    
	    }
        
   }
  }
 }
 
}
