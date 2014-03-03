package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;


import java.util.*;

public class Hyperbolic{
	        
	public Map<Integer,DataBase> d =new HashMap<Integer,DataBase>();
	
	public Hyperbolic()
  	{}
	
  public String runner(Map<Integer,DataBase> databases) 
  {  
   try{
   /*
	   if(databases.size() ==0)
		{
		 int MAX_beacon_node=99;
		 Coordinate C = new Coordinate(MAX_beacon_node);
		 System.out.println("\n\n ATTENTION PLEASE... database size is equal to 0 ");
		 double A=-59.55,N=2.326;
		 DataBase n;
		 for(int u=0;u<=3;u++)
		 {
		  n = new DataBase(u, u*10 +1, C.get_x(u), C.get_y(u),u); 
		  n.computeDistance(A,N);
		  databases.put(u, n);
		  System.out.println("\n\n\nNew DB entry: node " + u);
		  System.out.println("\n\n\n values: RSSI" + n.RSSI_RAW);
		  System.out.println("  X_coord" + n.x_coord);
		  System.out.println("  y_coord" + n.y_coord);
		 }
		}  
	 */  
	   
	    	d.putAll(databases); //copy the origin Map
	    	System.out.println("\n /*-----------------*/ \n" );	    
	   //System.out.println("\n DB passed at the task \n" );
	
    Matrix H =new Matrix(d.size()-1,2);
    Matrix HT =new Matrix(2,d.size()-1);
    Matrix HTH =new Matrix(HT.rows(),H.cols());
    Matrix invHTH =new Matrix(HTH.rows(),HTH.cols());
    Matrix invHTH_HT= new Matrix(invHTH.rows(),HT.cols());
    Matrix b =new Matrix(d.size()-1,1);
    Matrix Coord= new Matrix(invHTH_HT.rows(),b.cols());
    
    double rx=0,ry=0,c,d1=0,x_final,y_final,X_new,Y_new,di;
	  
    //System.out.println("Running the task...");
	int i=0;
	for(DataBase B: d.values())
    {
	 if(i==0)
	 {
	  rx=B.get_x(); //x_coord of the first node
	  ry=B.get_y(); //y_coord of the first node
	  d1=B.get_dis();
	 }else
	 {
	  X_new=B.get_x()-rx; //x_coord normaliz with respect the first node
	  Y_new=B.get_y()-ry; //y_coord normaliz with respect the first node
	  H.setCell((i-1), 0, 2*X_new); // H matrix setting -> x_coord  
	  H.setCell((i-1), 1, 2*Y_new);  // H matrix setting -> y_coord
	  di=B.get_dis();
	  c = Math.pow(X_new,2) + Math.pow(Y_new,2) - Math.pow(di,2) + Math.pow(d1,2);
	  b.setCell(i-1, 0,c);	// b Matrix (vector) setting
 	 }
	 i++;
    } 
	//H.print();
	HT=H.transpose(H); //HT dimension (2,(d.size()-1))
	//HT.print();
	HTH=HT.mult(H);    // HTH dimension (2,2)
	//HTH.print();
	invHTH=HTH.inv(); //invHTH dimension (2,2)
	//invHTH.print();
	invHTH_HT=invHTH.mult(HT); // invHTH_HT dimension (2,(d.size()-1))
	//invHTH_HT.print();
	//b.print();
	Coord=invHTH_HT.mult(b); // invHTH_HT_b dimension (2,1)
	//Coord.print();
	x_final = Coord.cell(0, 0) + rx;  //renormaliz of the x_coord with respect the 1st node 
    y_final = Coord.cell(1, 0) + ry; //renormaliz of the y_coord with respect the 1st node 
    System.out.println("Hyperbolic Algorithm:");
    System.out.println("x_calc="+ x_final+ "\ny_calc="+ y_final );
    //System.out.println(" x_error = "+ (x_final-this.x_known) + "\n y_error = "+ (y_final-this.y_known) );
     
    d.clear(); // HERE WE HAVE TO CLEAN THE DATABASE
    //System.out.println("\n Cleaned DB \n" );
        
    return "{\"x\":\""+x_final+"\", \"y\":\""+y_final+"\"}";
    
   } catch(MatrixDimensionError e)
     {
	   System.out.println("PROBLEM ABOUT MATRIXES DIMENSIONS");
     }
   
   return "Problema with hyperbolic algorithm";
 }
}
