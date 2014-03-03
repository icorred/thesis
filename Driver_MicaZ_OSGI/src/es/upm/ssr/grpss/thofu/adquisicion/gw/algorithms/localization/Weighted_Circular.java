package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;

import java.util.*;


public class Weighted_Circular{
	  
	public Map<Integer,DataBase> d =new HashMap<Integer,DataBase>();
	double a = 0.8; // con a =0.01 converge prima  ma sembra + preciso con 0.8
    double N=2.288;
	public Weighted_Circular()
  	{}
	
	
   public String runner(Map<Integer,DataBase> databases) 
   {  
	d.putAll(databases); //copy the origin Map
	System.out.println("\n /*-----------------*/ \n" );
	//System.out.println("\n DB passed at the task \n" );
        
    double x_final,y_final;
    double x_pre,y_pre,x_next,y_next,x_partial,y_partial,x_variation,y_variation,weight_sum;
    
    double x_sum=0,y_sum=0;
	boolean flag=false;
	int count=0;
	        
    for(DataBase B: d.values())
    {
     x_sum = x_sum + B.get_x();
     y_sum = y_sum + B.get_y();
    } 
    //average the coordinates of the sensing beacons
    x_pre = x_sum /d.size(); 
    y_pre = y_sum / d.size();

    weight_sum=0;
    for(DataBase B: d.values())
    { /*difference with the other circular algorithm*/
     B.weight = 1 /	Math.pow(B.distance,2);
     weight_sum=weight_sum+B.weight;
    } 
    for(DataBase B: d.values())
    { /*difference with the other circular algorithm*/
     B.weight = B.weight/weight_sum;
    } 
    
       
    while(flag==false)
    { 
     count++;
     x_partial = y_partial = 0;
     for(DataBase B: d.values())
     {
      x_partial = x_partial + (-2.0 * B.get_weight()*(B.get_x() - x_pre) * (Math.sqrt(Math.pow((B.get_x() - x_pre),2.0) + Math.pow((B.get_y() - y_pre),2.0)) - B.get_dis())) / Math.sqrt(Math.pow((B.get_x() - x_pre),2.0) + Math.pow((B.get_y() - y_pre),2.0));
      y_partial = y_partial + (-2.0 * B.get_weight()*(B.get_y() - y_pre) * (Math.sqrt(Math.pow((B.get_x() - x_pre),2.0) + Math.pow((B.get_y() - y_pre),2.0)) - B.get_dis())) / Math.sqrt(Math.pow((B.get_x() - x_pre),2.0) + Math.pow((B.get_y() - y_pre),2.0));     
     } 
          
     x_next = x_pre - a * x_partial;
     y_next = y_pre - a * y_partial;
     x_variation = Math.abs(x_next - x_pre);
     y_variation = Math.abs(y_next - y_pre);
     if(x_variation < 0.001 && y_variation < 0.001)
     {
      x_final = x_next;
      y_final = y_next; 
      flag=true;   
      
      System.out.println("Weighted_Circular_Algorithm:");
      System.out.println("x_calc="+ x_final+ "\ny_calc="+ y_final );
      //System.out.println(" x_error = "+ (x_final-this.x_known) + "\n y_error = "+ (y_final-this.y_known) );
     
    //System.out.println("number of steps:" +count);
      d.clear(); // HERE WE HAVE TO CLEAN THE DATABASE
      //System.out.println("\n Cleaned DB \n" );
      
      return "{\"x\":\""+x_final+"\", \"y\":\""+y_final+"\"}";
      
      
     }else{
    	 if(count>1000000)
         {
      	  System.out.println("\n The number of iterative steps is larger than: "+(count-1) );
      	  System.out.println("\n X_coord: "+x_next+" Y_coord: "+y_next);
      	  d.clear(); // HERE WE HAVE TO CLEAN THE DATABASE
          //System.out.println("\n Cleaned DB \n" );
      	  flag=true;
         }else{
        	 //System.out.println("\n" +count);
           	   x_pre = x_next;
               y_pre = y_next;
         	  }
          } 
    }
    
    return "Problem with Wighted Circular algorithm";
    
   }
}
