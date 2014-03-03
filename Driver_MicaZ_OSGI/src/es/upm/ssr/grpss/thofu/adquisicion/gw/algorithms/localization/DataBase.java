package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;




public class DataBase {

	public int node_id;
	int RSSI_RAW;
	double x_coord;
	double y_coord;
	double distance;
	double weight; //used only by weighted algorithms
	public int sequence_number;
	
	public DataBase(int node_id, int RSSI_RAW, double x_coord, double y_coord,int sequence_number)
	{
	 this.node_id=node_id;
	 this.RSSI_RAW=RSSI_RAW;
	 this.x_coord=x_coord;
	 this.y_coord=y_coord;
	 this.sequence_number=sequence_number;
	}
	
	
	 public double computeDistance(double A, double N) {
		 int RSSI=0;
		 
		 System.out.println("Compute Distance");
		 if(this.RSSI_RAW< 128)
		 {
			 RSSI = this.RSSI_RAW - 45; 
		 }else{
			 RSSI = this.RSSI_RAW - 256 - 45;
		 	  }
		 this.distance=Math.pow(10,(A - RSSI) / (10 * N));//calculate the distance
		 System.out.println("nodo = " + node_id + ", RSS = "+RSSI+ " distance " +distance);
		 //System.out.flush();
		 return this.distance;
		 
	    }
	  
	 public double updateDistance(int new_RSSI_RAW,double A, double N, int seq_number) {
		 System.out.println("pass updateDistance");
		 this.RSSI_RAW=(this.RSSI_RAW + new_RSSI_RAW)/2;		 
		 this.computeDistance(A, N);
		 this.sequence_number=seq_number;
		 return this.RSSI_RAW;
	    }
	 public double updateDistance(int []new_RSSI_RAW,int index, double A, double N, int seq_number)
	 {
		 this.RSSI_RAW=(this.RSSI_RAW + new_RSSI_RAW[index])/2;		 
		 this.computeDistance(A, N);
		 this.sequence_number=seq_number;
		 return this.RSSI_RAW;
	    }
	 
	  
	 public double get_x() {
		 return this.x_coord;
	    }

	 public double get_y() {
		 return this.y_coord;
	    }
 public double get_dis() {
		 return this.distance;
	    }
 
 public double get_S_Num() {
	 return this.sequence_number;
    }
 
 public double get_weight() { //used only by weighted algorithms
	 return this.weight;
    }
 
}
