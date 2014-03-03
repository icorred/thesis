package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;
							
public class Vector {
	private double[] data;
	   
	  // create a square matrix initialized to the identity
	  public Vector(int dimension)
	  {
	   data = new double[dimension];
	   for (int i = 0; i < dimension; i++)
	   {
		data[i] = 0;
	   }
	  }
	  public int size()
	  {
	   return this.data.length;
	  }
	  
	  public double cell(int i)
	  {
	   return this.data[i];
	  }
	  
	  public void setCell(int j,double tmp)
	  {
	   data[j]=tmp;
	  }
	  
	  public void copy(Matrix M, int k)
	  {
	   for(int i=0;i<this.size();i++)
	   {
	    this.setCell(i, M.cell(k, i));
	   }
	  }
	  
	  public void copy(Vector V)
	  {
	   for(int i=0;i<this.size();i++)
	   {
	    this.setCell(i, V.cell(i));
	   }
	  }
	  
	  public Vector subtr(Vector V1)
	  {double r;
	   for(int i=0;i<this.size();i++)
	   {
	    r= this.cell(i)-V1.cell(i);
	    this.setCell(i, r);
	   }
	   return this;
	  }
	  
	  public Vector scalar(double a)
	  {double r;
	   for(int i=0;i<this.size();i++)
	   {
	    r= this.cell(i)*a;
	    this.setCell(i, r);
	   }
	   return this;
	  }
	  
	  public void print()
	  {
	   for(int i=0;i<this.size();i++)
	   {
		System.out.println(this.cell(i)); 
	   }
	   System.out.println("\n");   
	  }
	  
	  
	  public void print(String a)
	  {
	   System.out.println("This is " + a +"\n");
	   for(int i=0;i<this.size();i++)
	   {
		System.out.println(this.cell(i)); 
	   }
	   System.out.println("\n");   
	  }
	  
}
