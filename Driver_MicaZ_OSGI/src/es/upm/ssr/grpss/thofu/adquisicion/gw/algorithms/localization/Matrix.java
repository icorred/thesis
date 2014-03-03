package es.upm.ssr.grpss.thofu.adquisicion.gw.algorithms.localization;


public class Matrix {
   double[][] data;

   public int rows()
   {
    return this.data.length;
   }
   
   public int cols()
   {
    return this.data[1].length;
   }
   
   /*
    * public Matrix(){} 
   */
   
  // create a square matrix initialized to the identity
  public Matrix(int dimension)
  {
   this.data = new double[dimension][dimension];
   for (int i = 0; i < dimension; i++)
   {
	for (int j = 0; j < dimension; j++)	
	{
     if(i==j)
     {this.data[i][j] = 1;
     }else{this.data[i][j] = 0;}
	}
   }
  }

  // create a matrix of any dimensions initialized to all zeroes.
  public Matrix(int rows, int cols)
  {
	  if (rows<= 0 || cols <= 0)
	    {
		  
	     System.out.println("rows="+ rows+ " cols=" + cols);	
	    }
    this.data = new double [rows][cols];
    for (int i = 0; i < rows; i++)
      for (int j = 0; j < cols; j++)
        this.data[i][j] = 0;
  }

//create a matrix equal to M
  public Matrix(Matrix M) throws MatrixDimensionError
  {
	  this.data = new double[M.rows()][M.cols()];
    copy(M);
  }

  // copy each cell from M to this, throwing an error
  // if M and this don't have the same dimensions.
  public void copy(Matrix M) throws MatrixDimensionError
  {
    if (M.rows() != this.rows() || M.cols() != this.cols())
    {
     throw new MatrixDimensionError();	
    }else{
    	  for (int i = 0; i < M.rows(); i++)
    	  {
    	   for (int j = 0; j < M.cols(); j++) 
    	   {
    		this.setCell(i, j, M.cell(i, j));
    	   }  
    	  }    
    	 }	
   }

   // return the cell at the ith row and jth column
  public double cell(int i, int j)
  {
    return this.data[i][j];
  }

  // set the value of the cell at the ith row and jth column
  public void setCell(int i, int j, double value)
  {
	  this.data[i][j] = value;
  }

  /* Matrix addition */
  public Matrix add(Matrix M2) throws MatrixDimensionError
  {
	  if (this.rows() != M2.rows() || this.cols() != M2.cols())
	  {
	   throw new MatrixDimensionError();
	  }
	  Matrix result = new Matrix(this.rows(), this.cols());
	  for (int i = 0; i < this.rows(); i++)
	  {
	   for (int j = 0; j < this.cols(); j++)
	   {
	    result.setCell(i, j, this.cell(i, j) + M2.cell(i, j));
	   }
	  }
     return result; 
  }

  /* matrix subtraction */
  public Matrix subtract(Matrix M2) throws MatrixDimensionError
  {
	  if (this.rows() != M2.rows() || this.cols() != M2.cols())
	  {
	   throw new MatrixDimensionError();
	  }
	  Matrix result = new Matrix(this.rows(), this.cols());
	  for (int i = 0; i < this.rows(); i++)
	  {
	   for (int j = 0; j < this.cols(); j++)	   
	   {
	    result.setCell(i, j, this.cell(i, j) - M2.cell(i, j));
	   }
	  }
     return result;
   }


  /* scalar multiplication */
  public Matrix mult(double a) throws MatrixDimensionError
  {
    Matrix result = new Matrix(this.rows(), this.cols());
    result.copy(this);
    result.selfMult(a);
    return result;
  }

  public void selfMult(double a)
  {
    for (int i = 0; i < this.rows(); i++)
    {	
     for (int j = 0; j < this.cols(); j++)
     {
    	 this.data[i][j] = this.data[i][j]*a;
     }
    } 
  }

  /* vector multiplication SIZE: Vector(1,n) Matrix(n,c) Resulting size is (1,c)*/
  public Vector mult(Vector v2, Matrix M1) throws MatrixDimensionError
  {
    if (M1.rows() != v2.size())
    {
	 throw new MatrixDimensionError();
    }
     
    Vector result = new Vector(M1.cols());
    for (int j = 0; j < M1.cols(); j++) 
    {
     double tmp = 0;
     for (int i = 0; i < M1.rows(); i++)
     {
	  tmp = tmp + M1.cell(i, j) * v2.cell(i);
     }
    result.setCell(j, tmp);
    }
    return result;
  }

  /* matrix multiplication */
  public Matrix mult(Matrix M2) throws MatrixDimensionError
  {
    if (this.cols() != M2.rows())
    {throw new MatrixDimensionError();
    }
    
    Matrix result = new Matrix(this.rows(),M2.cols());
    for (int i = 0; i < result.rows(); i++) 
    {
     for (int j = 0; j < result.cols(); j++) 
     {
      double tmp = 0;
      for (int k = 0; k < this.cols(); k++)
      {
       tmp = tmp + this.cell(i, k) * M2.cell(k, j);  
      }   
      result.setCell(i, j, tmp);
     }
    }
    return result;
  }
  
//copy the vector instead of the k-th Matrix row
 public void copy_vector(Vector v1,int k) throws MatrixDimensionError
 {
	 if (v1.size()!= this.cols())
	    {throw new MatrixDimensionError();
	    } 
	
	for(int i=0;i<v1.size();i++)
	{
	 this.setCell(k, i, v1.cell(i));
	} 
 } 
  
//reduce the Matrix M in row echelon form
  public Matrix Row_echelon(Matrix M) throws MatrixDimensionError {
	  int i,j,k,dim;
	  boolean flag,found;
	  dim=M.rows();
	  Vector v1 =new Vector(M.cols());
	  Vector v2 =new Vector(M.cols());
	  
	  	for(i=0;i<dim;i++)
	  	{flag=false;
	  	 for(j=i;j<dim&&flag!=true;j++)
	  	 { 
	  	  if(M.cell(i, j)==0)
	  	  {found=false;
	  	   for(k=i+1;k<dim&&found!=true;k++)
	  	   {
	  	    if(M.cell(k, j)!=0)
	  	    {
	  	     v1.copy(M, k);
	  	     v2.copy(M, i);
	  	     M.copy_vector(v1, i);
	  	     M.copy_vector(v2, k);
	  	     found=true;
	  	    }
	  	   }
	  	   if(k>=dim&&found!=true)
	  	   {
	  		 throw new MatrixDimensionError();
	  	   }
	  	  }else{
	  		    //if the i-th element is not null, then from the k-th element downward
	  		    //that column must have all zeros 
	  		  	
	  		  	for(k=j+1;k<dim;k++)
	  		    {
	  		     if(M.cell(k, j)!=0)
	 	  	  	 {
	  		      v1.copy(M, i);
		  		  v1.scalar(1/M.cell(i, j));
	  		      v1.scalar(M.cell(k, j));
	  		      v2.copy(M, k);
	  		      v1=v1.subtr(v2);
	  		      M.copy_vector(v1, k);
	 	  	  	 } 
	  		    }
	  		    flag=true;
	  	  	   }	     
	  	 }
	  	}
	  	return M;
	  }

  //return the inverse matrix obtained with the Gauss-Jordan algorithm
  public Matrix inv() throws MatrixDimensionError 
  {
	  Matrix M= new Matrix(this.rows(),2*this.cols());
	  Matrix INV=new Matrix(this.rows(),this.rows());
	  
   if(this.rows()!=this.cols())
   {
	   throw new MatrixDimensionError();
   }
   //System.out.println("inverse Matrix computation beginning... ");
   M=this.attach();
   //System.out.println("Matrix attached: ");
   //M.print();
   
   M=Row_echelon(M);
   //System.out.println("Matrix reduced: ");
   //M.print();  
   
   M=Gauss_inv(M);
   //System.out.println("Row echelon Matrix is inverted: ");
   //M.print();
   
   INV=M.sub_matrix(INV,M, 0, INV.cols());
   //System.out.println("Inverse Matrix : ");
   //INV.print();
   
   //System.out.println("inverse Matrix computation ended");
   return INV;
  }
  
//return the initial matrix M enlarged of an Identity(M.rows())
  // return (M|I)
  public Matrix attach()
  {
   Matrix I= new Matrix(this.rows());
   Matrix M= new Matrix(this.rows(),2*this.rows());
   for(int i=0;i< this.rows();i++)
   {
	for(int j=0;j<M.cols();j++)
	{
     if(j<this.cols())
     {
      M.setCell(i, j, this.cell(i, j));
     }else{
    	   M.setCell(i, j, I.cell(i, j-this.cols()));   
     	  }
	}
   }
   return M;
  }
  
  //given a Matrix in the Row echelon form return its inverse
  public Matrix Gauss_inv(Matrix M)
  {
   boolean flag;
   Vector v1 =new Vector(M.cols());
   Vector v2 =new Vector(M.cols());
   
   for(int i=M.rows()-1; i>=0; i--)
   {flag=false;
	for(int j=i;j>=0&&flag!=true;j--)
	{
	 v1.copy(M, i);
	 v1=v1.scalar(1/M.cell(i, j)); //normalizzato a 1
	 try{M.copy_vector(v1, i);}
	 catch(MatrixDimensionError e){}
	 for(int k=i-1;k>=0;k--)
	 {
	  v1.copy(M, i);
	  v2.copy(M, k);
	  v1=v1.scalar(M.cell(k, j));
	  v1=v1.subtr(v2);
	  try{M.copy_vector(v1, k);}
	  catch(MatrixDimensionError e){}
	 }
	 flag=true;
	}
   }
   return M;
  }
  
  public Matrix sub_matrix(Matrix sub_M, Matrix M, int lim_i_r, int lim_i_c)
  {
   for(int i=lim_i_r; i<M.rows();i++)
   {
	for(int j=lim_i_c;j<M.cols();j++)
	{
	 sub_M.setCell(i-lim_i_r, j-lim_i_c, M.cell(i, j));
	}
   }
   return sub_M;
  }
  
  
  public Matrix transpose(Matrix M)
  {
	Matrix T =new Matrix(M.cols(),M.rows());
   for(int i =0; i<T.rows();i++)
   {
	for(int j =0; j<T.cols();j++)
	{
	 T.setCell(i, j, M.cell(j, i));
	}
   }
   return T;
  }  
  
  
  
  public void print()
{
 System.out.println("Matrix \n");
 String string;
 for(int i =0; i<this.rows();i++)
 {string="";
  for(int j =0; j<this.cols();j++)
  {
	  //System.out.println(this.cell(i, j));
	  string=string + " " + this.cell(i, j)+ " | ";
  }System.out.println(string + "\n");
 }
}

}
