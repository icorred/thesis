package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;


import java.util.*;

public class Weighted_Hyperbolic{
	  
      
	public Map<Integer,DataBase> d =new HashMap<Integer,DataBase>();
	double A=-58,N=2.3;
	
	public Weighted_Hyperbolic()
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
		 double A=-63.22,N=2.288; 
		 DataBase n;
		 for(int u=0;u<3;u++)
		 {
		  n = new DataBase(u, u*10 +1, C.get_x(u), C.get_y(u),u); 
		  n.computeDistance(A,N);
		  databases.put(u, n);
		  System.out.println("\n\n\nNew DB entry: node " + u);
		  System.out.println("\n\n\n values: RSSI" + n.RSSI_RAW);
		  System.out.println("  X_coord" + n.x_coord);
		  System.out.println("  y_coord" + n.y_coord);
		 }
		}*/  
	   
	   d.putAll(databases); //copy the origin Map
	   System.out.println("\n /*-----------------*/ \n" );   
   //System.out.println("\n DB passed at the task \n" );
	    
	Matrix S =new Matrix(d.size()-1);
	Matrix invS =new Matrix(d.size()-1);
    Matrix H =new Matrix(d.size()-1,2);
    Matrix HT =new Matrix(2,d.size()-1);
    Matrix HT_invS =new Matrix(2,(d.size()-1));
    Matrix HT_invS_H =new Matrix(2,2);
    Matrix inv__HT_invS_H =new Matrix(2,2);
    Matrix b =new Matrix(d.size()-1,1);
    Matrix HT_invS_b =new Matrix(2,1);
    Matrix Coord= new Matrix(2,1);
    Vector Variance_di_square = new Vector(d.size());
    
    double rx=0,ry=0,c,d1=0, x_final,y_final,sigma=1,sigma_d,var_part,di,X_new,Y_new;
	  
    // System.out.println("Running the task");
    sigma_d = sigma * Math.log(10) / (10 * N);
    var_part = Math.exp(8*Math.pow(sigma_d, 2))-Math.exp(4*Math.pow(sigma_d, 2));
    //System.out.println("var_part is " + var_part );
    //var_part = 1;
    int i=0;
	for(DataBase B: d.values())
    {
	 if(i==0)
	 {
	  rx=B.get_x(); //x_coord of the first node
	  ry=B.get_y();  //y_coord of the first node
	  d1=B.get_dis();
	  Variance_di_square.setCell(i, Math.pow(d1, 4)*var_part ); // Variance of the 1st node
	 }else
	 {
	  X_new=B.get_x()-rx; //x_coord normaliz with respect the first node
	  Y_new=B.get_y()-ry; //y_coord normaliz with respect the first node
	  H.setCell((i-1), 0, 2*X_new); // H matrix setting -> x_coord  
	  H.setCell((i-1), 1, 2*Y_new);  // H matrix setting -> y_coorde   	   
	  di=B.get_dis();
	  c = Math.pow(X_new,2) + Math.pow(Y_new,2) - Math.pow(di,2) + Math.pow(d1,2);
	  b.setCell(i-1, 0,c);	// b Matrix (vector) setting
	  double mu_d;
	  mu_d=Math.log(10)*Math.log10(di);
	  Variance_di_square.setCell(i, Math.exp(4*mu_d)*var_part); // Variance of the 2nd node on
	 }
	 i++;
    }
	
	for(i=0;i<S.rows();i++)
	{                                // Covariance Matrix creation
	 for(int j=0;j<S.cols();j++)
	 {
	  if(i==j)
	  {
	   S.setCell(i, j, Variance_di_square.cell(0)+Variance_di_square.cell(i+1));
	  }else{
		    S.setCell(i, j, Variance_di_square.cell(0));
		   }
	 }
	}
	
	invS=S.inv(); //S and invS have dimensions (d.size()-1,d.size()-1) 
	HT=H.transpose(H); //HT dimension (2,(d.size()-1))
	HT_invS=HT.mult(invS); //HTinvS dimension (2,(d.size()-1))
	HT_invS_H=HT_invS.mult(H);   //HT_invS_H dimension (2,2)
	inv__HT_invS_H=HT_invS_H.inv();  //HT_invS_H dimension (2,2)
	HT_invS_b=HT_invS.mult(b);  //HT_invS_b dimension (2,1)
	Coord=inv__HT_invS_H.mult(HT_invS_b); //Coord dimension (2,1)
	x_final = Coord.cell(0, 0) + rx;  //renormaliz of the x_coord with respect the 1st node 
    y_final = Coord.cell(1, 0) + ry; //renormaliz of the y_coord with respect the 1st node 
    System.out.println("Weighted_Hyperbolic_Algorithm:");
    System.out.println("x_calc="+ x_final+ "\ny_calc="+ y_final );
    //System.out.println(" x_error = "+ (x_final-this.x_known) + "\n y_error = "+ (y_final-this.y_known) );
    
    d.clear(); // HERE WE HAVE TO CLEAN THE DATABASE
    //System.out.println("\n Cleaned DB \n" );
    
    return "{\"x\":\""+x_final+"\", \"y\":\""+y_final+"\"}";
   
   } catch(MatrixDimensionError e)
     {
	   System.out.println("PROBLEM ABOUT MATRIXES DIMENSIONS");
     }
   
   return "Problema with weighted hyperbolic algorithm";
 }
}
