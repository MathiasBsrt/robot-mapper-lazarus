package decision;

//helper class for matrices, to make maths in RoomMap compacter
//zero based
public class Matrix {
	private double field[][];
	private int numRows;
	private int numCols;
	
	public Matrix(int rows, int cols) {
		this.field = new double[rows][cols];
		this.numRows = rows;
		this.numCols = cols;
	}
	
	public Matrix(double[][] field) {
		this.field = field.clone();
		this.numRows = field.length;
		this.numCols = field[0].length;
	}
	
	public double getElement(int row, int col) {
		if(row < 0 || row > field.length || col < 0 || col > field[0].length) return 0;
		return field[row][col];
	}
	
	public void setElement(int row, int col, double val) {
		if(row < 0 || row > field.length || col < 0 || col > field[0].length) return;
		field[row][col] = val;
	}
	
	
	/**
	 * Computes m1 * m2, m1 is a m*n and m2 is a n*p Matrix. The resulting Matrix is m*p
	 * @param m1
	 * @param m2
	 * @returns product of m1 and m2
	 * */
	static public Matrix multiplyMatrix(Matrix m1, Matrix m2) {
		double m1field[][] = m1.field;
		double m2field[][] = m2.field;
		int m1ColLen = m1field[0].length;
		int m2RowLen = m2field.length;
		if(m1ColLen != m2RowLen) return null;
		int m1RowLen = m1field.length;
		int m2ColLen = m2field[0].length;
		Matrix mOut = new Matrix(m1RowLen, m2ColLen);
		double fOut[][] = mOut.field;
		
		for(int i = 0; i < m1RowLen; ++i) {
			for(int j = 0; j < m2ColLen; ++j) {
				for(int k = 0; k < m1ColLen; ++k) {
					fOut[i][j] += m1field[i][k] * m2field[k][j];
				}
			}
		}
		return mOut;
	}
	
	static public Matrix multiplyMatrix(Matrix m1, double scalar) {
		Matrix mOut = m1.clone();
		
		for(int i = 0; i < mOut.numCols; ++i) {
			for(int j = 0; j < mOut.numRows; ++j) {
				mOut.field[i][j] *= scalar;
			}
		}
		return mOut;
	}
	
	@Override
	public Matrix clone() {
		return new Matrix(this.field.clone());
	}
	
	@Override
	public String toString() {
		StringBuffer sOut = new StringBuffer();
		//sOut.append(super.toString() + ":\n");
		for(int i = 0; i < numRows; ++i) {
			sOut.append("\t[");
			for (int j = 0; j < numCols; j++) {
				sOut.append(" " + field[i][j] + ",");
			}
			sOut.deleteCharAt(sOut.length() - 1);
			sOut.append("]\n");
		}
		return sOut.toString();
	}
}
