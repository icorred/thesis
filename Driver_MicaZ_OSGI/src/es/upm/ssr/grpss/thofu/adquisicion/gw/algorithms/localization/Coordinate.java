package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;

public class Coordinate {
	double [][]Coord;
	int first_TOS_NODE_ID=2; //node number begins from 2 until 15

	//TODO Implementar mecanismo para actualizaci�n de coordenadas a trav�s de la Base de Datos
	public Coordinate ( int MAX_beacon_node) {
		Coord= new double[MAX_beacon_node][2];
		this.Coord[0][0] =10.50; this.Coord[0][1]= 6.60;	// 2
	    this.Coord[1][0] =8.77;  this.Coord[1][1]=  1.35;	//3
	    this.Coord[2][0] =8.77;  this.Coord[2][1]= -1.35;	//4
	    this.Coord[3][0] =9.76; this.Coord[3][1]=  -8.18;	//5
	    this.Coord[4][0] =5.15;  this.Coord[4][1]=  4.05;	//6
	    this.Coord[5][0] =1.25;  this.Coord[5][1]=  1.35;	//7
	    this.Coord[6][0] =1.25;  this.Coord[6][1]= -1.35;	//8
	    this.Coord[7][0] =8.77;  this.Coord[7][1]=  4.05;	//9
	    /*-----------------------------------------------------------*/
	    this.Coord[8][0] =8.77;  this.Coord[8][1]= -4.05;  //10
	    this.Coord[9][0] =5.15;  this.Coord[9][1]=  1.35;  //11
	    this.Coord[10][0]=5.15;  this.Coord[10][1]=-1.35;  //12
	    /*-----------------------------------------------------------*/
	    this.Coord[11][0]=5.15;   this.Coord[11][1]=-4.05;  //13
	    this.Coord[12][0]=1.25;   this.Coord[12][1]= 4.05;  //14
	    this.Coord[13][0]=1.25;   this.Coord[13][1]=-4.05;  //15
	  }
	
	/*

9)     8.4700    1.6000
10)    5.2700    4.4800
11)    8.4000    4.5550
12)    4.3500    8.4000
13)    0.4600         0
14)    2.8600    4.4900
15)         0    4.5600
16)    2.2000    8.6550
....................................
17)    0.6700   10.6950
18)    4.3800   15.4250
19)    0.1800   19.7750
....................................
20)	   4.4200		12.4200
21)	   8.1300		15.2000
*/ 
	
	
	
	
	 public double get_x(int node_id) {
		 return this.Coord[node_id-first_TOS_NODE_ID][0];
	    }
	 public double get_y(int node_id) {
		 return this.Coord[node_id-first_TOS_NODE_ID][1];
	    }
	 
}
